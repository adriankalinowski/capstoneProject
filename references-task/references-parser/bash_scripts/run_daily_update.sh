#!/bin/bash
set -xeuo pipefail

#include config file for default variables
. /opt/scripts/reference.config

#define a function for handling update failures
update_failed () {
    psql -c "UPDATE reference.update_database SET status='ERROR', error_message='$1' WHERE update_id='${update_uuid}'::uuid"
    exit 0
}

#retrieve arguments from command line
while getopts ":d:" opt; do
	case ${opt} in
	d) download_cache=${OPTARG};;
	h) 
		echo "Usage: ./run_daily_update.sh -d [download_cache]"
		exit 0;;
	esac
done

#which returns in string format as "UUID,update_num"
#once we retrieve this string we split on the comma and place the elements into an array
IFS=',' read -a uuid_update_num_arr <<< $(psql -v ON_ERROR_STOP=1 -tq <<-EOL
     WITH u as (SELECT COALESCE(MAX(update_num)+1, '${first_update_file_num}'::numeric ) as update_num_max FROM update_database WHERE status='COMPLETE')
    INSERT INTO update_database(pubmed_update_id, update_num, status) 
        SELECT '${pubmed_file_name}'||LPAD(u.update_num_max::text, 4, '0'), u.update_num_max , 'PENDING' FROM u
    RETURNING CAST(update_id AS TEXT)||','||(SELECT LPAD(update_num::text, 4, '0'))
EOL
) || { echo 'could not retrieve last update ID'; exit 1; }

update_uuid=${uuid_update_num_arr[0]}
update_file_num=${uuid_update_num_arr[1]}

shopt -q -s extglob
update_uuid="${update_uuid##+([[:space:]])}"
shopt -q -u extglob

#check for corresponding XML file in downloaded_data volume
input_file=/usr/src/app/downloaded_data/${pubmed_file_name}${update_file_num}.xml.gz
input_file_stats=/usr/src/app/downloaded_data_stats/${pubmed_file_name}${update_file_num}_stats.html
json_stats='[]'
if [ ! -e $input_file ]; then { update_failed "Could not find update file"; } fi
if [ -e $input_file_stats ]; then 
    python3 stats_script.py -i ${input_file_stats} -o /data_stats/stats.txt || { echo "WARNING: stats unavailable"; } 
    json_stats=`cat /data_stats/stats.txt`
fi

journal_file=/usr/src/app/downloaded_data/${journal_cache_file_name}
if [ ${download_cache} == "true" ];
then { 
	if [ -e ${journal_file} ]; then { rm ${journal_file}; } fi
	bash /opt/scripts/download_annual_baseline.sh -u ${journal_cache_url} -f /usr/src/app/downloaded_data/${journal_cache_file_name};
} fi
python3 journal_cache_script.py -j /data/journal_cache.csv

dtd_file=/usr/src/app/downloaded_data/${dtd_file_name}
if [ ! -e ${dtd_file} ]; then {
	bash /opt/scripts/download_annual_baseline.sh -u https://dtd.nlm.nih.gov/ncbi/pubmed/out/${dtd_file_name} -f ${dtd_file_name}; } fi

#parse references
python3 script.py -a /data/article.csv.gz -c /data/chemical.csv.gz -m /data/mesh.csv.gz -ab /data/abstract.csv.gz -d /data/deleted.csv.gz -i ${input_file} -av /data/versions_article.csv.gz -cv /data/versions_chemical.csv.gz -mv /data/versions_mesh.csv.gz -abv /data/versions_abstract.csv.gz -dv /data/versions_deleted.csv.gz -cm 'gzip' || { update_failed "Update failed due to an issue with the parser"; } 

zcat /data/article.csv.gz | pgloader updates_article.load || { update_failed "Update failed while loading the articles in updates_article.load";}
zcat /data/mesh.csv.gz | pgloader updates_mesh.load || { update_failed "Update failed while loading the mesh terms in updates_mesh.load";}
zcat /data/chemical.csv.gz | pgloader updates_chemical.load || { update_failed "Update failed while loading the chemicals in updates_chemical.load";}
zcat /data/abstract.csv.gz | pgloader updates_abstract.load || { update_failed "Update failed while loading the abstracts in updates_abstract.load";}
zcat /data/deleted.csv.gz | pgloader updates_deleted.load || { update_failed "Update failed while loading the deleted articles in updates_deleted.load";}
pgloader updates_journal.load || { update_failed "Update failed while loading the journals in updates_journal.load"; }

zcat /data/versions_article.csv.gz | pgloader updates_version_article.load || { update_failed "Update failed while loading the article versions in updates_version_article.load";}
zcat /data/versions_mesh.csv.gz | pgloader updates_version_mesh.load || { update_failed "Update failed while loading the mesh versions in updates_version_mesh.load";}
zcat /data/versions_chemical.csv.gz | pgloader updates_version_chemical.load || { update_failed "Update failed while loading the chemical versions in updates_version_chemical.load";}
zcat /data/versions_abstract.csv.gz | pgloader updates_version_abstract.load || { update_failed "Update failed while loading the abstract versions in updates_version_abstract.load";}
zcat /data/versions_deleted.csv.gz | pgloader updates_version_deleted.load || { update_failed "Update failed while loading the deleted versions in updates_version_deleted.load";}

#begin data import
(
psql -v ON_ERROR_STOP=1 <<-EOL
    BEGIN TRANSACTION;

    INSERT INTO statistics VALUES((SELECT COUNT(*) from temp.article_versions), 0, 0);

    --inserts all articles with Version 1 and uses update for articles that are already existing 
   	INSERT INTO reference.article 
   	SELECT * FROM temp.article ON CONFLICT (pubmed_id) DO UPDATE set version=excluded.version, title=excluded.title, is_translated=excluded.is_translated, publication_date=excluded.publication_date, medline_date=excluded.medline_date, author_list=excluded.author_list, nlm_unique_id=excluded.nlm_unique_id, journal=excluded.journal, keywords=excluded.keywords, article_ids=excluded.article_ids;

   	--inserts version 1 articles from article table and inserts them into article_versions table
   	INSERT INTO reference.article_versions 
   	SELECT * FROM reference.article WHERE pubmed_id IN (SELECT DISTINCT pubmed_id FROM temp.article_versions) ON CONFLICT (pubmed_id, version) DO UPDATE set title=excluded.title, is_translated=excluded.is_translated, publication_date=excluded.publication_date, medline_date=excluded.medline_date, author_list=excluded.author_list, nlm_unique_id=excluded.nlm_unique_id, journal=excluded.journal, keywords=excluded.keywords, article_ids=excluded.article_ids;

	--deleted all articles that have a versions higher than 1
	DELETE FROM reference.article WHERE pubmed_id IN (SELECT DISTINCT pubmed_id FROM temp.article_versions);

	--inserts higher version article references into main article table
	INSERT INTO reference.article 
	SELECT * FROM temp.article_versions v WHERE (v.pubmed_id, v.version) IN (SELECT  pubmed_id, max(version) as version FROM temp.article_versions GROUP BY pubmed_id);

	--move all other versions, which are not the newest table, from the working table to article_versions
	INSERT INTO reference.article_versions
	SELECT * FROM temp.article_versions v WHERE version <> (
	SELECT version FROM reference.article a WHERE v.pubmed_id = a.pubmed_id) ON CONFLICT (pubmed_id, version) DO UPDATE set title=excluded.title, is_translated=excluded.is_translated, publication_date=excluded.publication_date, medline_date=excluded.medline_date, author_list=excluded.author_list, nlm_unique_id=excluded.nlm_unique_id, journal=excluded.journal, keywords=excluded.keywords, article_ids=excluded.article_ids;

   	--insert any new mesh terms into the mesh table
	INSERT INTO reference.mesh 
	SELECT term_id, term FROM temp.mesh ON CONFLICT (term_id) DO NOTHING;

	--insert any new mesh terms into the mesh table from max versions
	INSERT INTO reference.mesh 
	SELECT DISTINCT (term_id, term)
	FROM temp.mesh_versions
	ON CONFLICT DO NOTHING;


	INSERT INTO reference.mesh_article
	SELECT pubmed_id, term_id FROM temp.mesh ON CONFLICT (term_id, pubmed_id) DO NOTHING;

	INSERT INTO reference.mesh_article --insert any new mesh to article relationships into mesh_article
	SELECT pubmed_id, term_id
	FROM temp.mesh_versions v
	WHERE (pubmed_id, version) IN (
	SELECT  pubmed_id, max(version) as version
	FROM temp.mesh_versions
	GROUP BY pubmed_id)
	ON CONFLICT DO NOTHING;

	--inserts chemical compounds from working table
	INSERT INTO reference.chemical_compound
	SELECT chemical_compound_id, chemical_compound FROM temp.chemical_compound
	ON CONFLICT (chemical_compound_id) DO NOTHING;

	INSERT INTO reference.chemical_compound --insert any new chemical compounds into chemical_compound table
	SELECT DISTINCT (chemical_compound_id, chemical_compound)
	FROM temp.chemical_versions
	ON CONFLICT DO NOTHING;

	--insert any new relationships into article_chemical_compound table from references (version 1)
	INSERT INTO reference.article_chemical_compound
	SELECT pubmed_id, chemical_compound_id
	FROM temp.chemical_compound
	ON CONFLICT (chemical_compound_id, pubmed_id) DO NOTHING;

	--insert any new relationships into article_chemical_compound table from max versions
	INSERT INTO reference.article_chemical_compound 
	SELECT pubmed_id, chemical_compound_id
	FROM temp.chemical_versions v1
	WHERE (pubmed_id, version) IN (
	SELECT  pubmed_id, max(version) as version
	FROM temp.chemical_versions
	GROUP BY pubmed_id)
	ON CONFLICT DO NOTHING;

	INSERT INTO reference.abstract_article
	SELECT pubmed_id, associated_abstract
	FROM temp.abstract
	ON CONFLICT (pubmed_id) DO NOTHING;

	--update the abstract for the latest version of the reference
	UPDATE reference.abstract_article a SET version=v.version, associated_abstract=v.associated_abstract FROM temp.article_abstract_versions v WHERE a.pubmed_id = v.pubmed_id AND (v.pubmed_id, v.version) IN (
	SELECT  pubmed_id, max(version) as version
	FROM temp.article_abstract_versions
	GROUP BY pubmed_id);

	--move old version of abstract to article_abstract_versions for each pubmed_id which has a new version in the working table
	INSERT INTO reference.article_abstract_versions(pubmed_id, version, associated_abstract) 
	SELECT a.pubmed_id, a.version, a.associated_abstract
	FROM reference.abstract_article a
	WHERE pubmed_id IN (
	SELECT DISTINCT pubmed_id
	FROM temp.article_abstract_versions) ON CONFLICT (pubmed_id, version) DO UPDATE set associated_abstract=excluded.associated_abstract;

	--put any other new versions (which are not the newest) into the article_abstract_versions table
	INSERT INTO reference.article_abstract_versions
	SELECT * FROM temp.article_abstract_versions v
	WHERE version <> (
	SELECT version
	FROM reference.article a
	WHERE v.pubmed_id = a.pubmed_id) ON CONFLICT (pubmed_id, version) DO UPDATE set associated_abstract=excluded.associated_abstract;
	
	--update global_search table
	
	--handle deletions
	DELETE FROM reference.article a WHERE a.pubmed_id IN (SELECT DISTINCT pubmed_id FROM temp.deleted);
	DELETE FROM reference.article a WHERE a.pubmed_id IN (SELECT DISTINCT pubmed_id FROM temp.deleted_versions);

--update global_search table
	INSERT INTO reference.global_search (pubmed_id, concatenated_columns_tsv) --insert tsvector columns based on rows in the article table
    SELECT pubmed_id, setweight(TO_TSVECTOR(COALESCE(A.title, '')), 'A') ||  setweight(TO_TSVECTOR('english', COALESCE((SELECT string_agg(b->>'Keyword',',') FROM jsonb_array_elements(A.keywords->'Keywords') b), '')), 'B') || setweight(TO_TSVECTOR(COALESCE((SELECT string_agg(a->>'LastName', ',') FROM jsonb_array_elements(A.author_list) a), '')), 'C') || setweight(TO_TSVECTOR(COALESCE(J.journal_title, '')), 'D') 
    FROM reference.article A, reference.journal J 
    WHERE A.nlm_unique_id=J.nlm_unique_id AND pubmed_id IN (SELECT pubmed_id FROM temp.article UNION SELECT pubmed_id FROM temp.article_versions)
	ON CONFLICT (pubmed_id) DO UPDATE SET concatenated_columns_tsv=excluded.concatenated_columns_tsv;


	INSERT INTO reference.update_article(pubmed_id, update_id) SELECT pubmed_id, '${update_uuid}'::uuid FROM (SELECT pubmed_id FROM temp.article UNION SELECT pubmed_id FROM temp.article_versions) as updated_articles;

    UPDATE update_database
    SET statistics=JSON_BUILD_OBJECT('updated', (SELECT records_updated FROM reference.statistics), 'deleted', (SELECT records_deleted FROM statistics), 'inserted', (SELECT records_inserted FROM statistics)), status='COMPLETE', pubmed_statistics='${json_stats}'
    WHERE update_id='${update_uuid}'::uuid;

    --changed to DELETE from TRUNCATE since deletes need to be granted to regular user. One less thing to grant to regular user
    DELETE FROM statistics;

    COMMIT;
EOL
) || { update_failed "Update failed on import into the database"; }

#Update the update_time. Trigger initially sets update_time to the time the transaction above began
psql -c "UPDATE reference.update_database SET update_time = now() WHERE update_id = '${update_uuid}'::uuid;"
DROP SCHEMA IF EXISTS reference CASCADE;
CREATE SCHEMA reference AUTHORIZATION cloud;
CREATE EXTENSION IF NOT EXISTS "pgcrypto";

CREATE TABLE IF NOT EXISTS  reference.article(
    pubmed_id INTEGER UNIQUE NOT NULL,
    version INTEGER,
    title VARCHAR,
    publication_date DATE,
    medline_date VARCHAR,
    author_list JSONB,
    nlm_unique_id VARCHAR,
    journal JSON,
    keywords JSONB,
	article_ids JSONB,
    is_translated BOOLEAN DEFAULT FALSE,
    creation_time timestamp with time zone DEFAULT NOW(),
    update_time timestamp with time zone DEFAULT NOW(),
    PRIMARY KEY(pubmed_id)
);

CREATE TABLE IF NOT EXISTS reference.mesh(
    term_id VARCHAR UNIQUE NOT NULL,
    term VARCHAR,
    creation_time timestamp with time zone DEFAULT NOW(),
    update_time timestamp with time zone DEFAULT NOW(),
    PRIMARY KEY(term_id)
);

CREATE TABLE IF NOT EXISTS reference.mesh_article(
    pubmed_id INTEGER NOT NULL,
    term_id VARCHAR NOT NULL,
    FOREIGN KEY(pubmed_id) REFERENCES reference.article(pubmed_id),
    FOREIGN KEY(term_id) REFERENCES reference.mesh(term_id),
    UNIQUE (pubmed_id, term_id)
);

CREATE TABLE IF NOT EXISTS reference.chemical_compound(
    chemical_compound_id VARCHAR UNIQUE NOT NULL,
    chemical_compound VARCHAR,
    creation_time timestamp with time zone DEFAULT NOW(),
    update_time timestamp with time zone DEFAULT NOW(),
    PRIMARY KEY(chemical_compound_id)
);

CREATE TABLE IF NOT EXISTS reference.abstract_article(
    pubmed_id INTEGER NOT NULL,
    version INTEGER,
	associated_abstract JSONB,
    creation_time timestamp with time zone DEFAULT NOW(),
    update_time timestamp with time zone DEFAULT NOW(),
    PRIMARY KEY(pubmed_id)
);

CREATE TABLE IF NOT EXISTS reference.article_chemical_compound(
    pubmed_id INTEGER NOT NULL,
    chemical_compound_id VARCHAR NOT NULL,
    FOREIGN KEY(pubmed_id) REFERENCES reference.article(pubmed_id),
    FOREIGN KEY(chemical_compound_id) REFERENCES reference.chemical_compound(chemical_compound_id),
    UNIQUE (pubmed_id, chemical_compound_id)
);

CREATE TABLE IF NOT EXISTS reference.update_database(
    update_id UUID DEFAULT gen_random_uuid(),
    pubmed_update_id VARCHAR(13) NOT NULL,
    update_num INTEGER,
    status VARCHAR(9),
    error_message VARCHAR DEFAULT NULL,
    creation_time timestamp with time zone DEFAULT NOW(),
    update_time timestamp with time zone DEFAULT NOW(),
    statistics JSONB,
    pubmed_statistics JSONB,
    PRIMARY KEY(update_id)
);

CREATE TABLE IF NOT EXISTS reference.update_article(
    update_id UUID NOT NULL,
    pubmed_id INTEGER UNIQUE NOT NULL,
    FOREIGN KEY(pubmed_id) REFERENCES reference.article(pubmed_id),
    FOREIGN KEY(update_id) REFERENCES reference.update_database(update_id)
);

CREATE TABLE IF NOT EXISTS reference.journal(
	nlm_unique_id VARCHAR UNIQUE NOT NULL,
	journal_title VARCHAR NOT NULL,
	issn_print VARCHAR,
	issn_online VARCHAR,
	iso_abbr VARCHAR,
	med_abbr VARCHAR,
	journal_id VARCHAR,
	start_year VARCHAR,
	end_year VARCHAR,
	activity_flag VARCHAR,
	alias JSONB,
	source VARCHAR DEFAULT 'JCache',
    creation_time timestamp with time zone DEFAULT NOW(),
    update_time timestamp with time zone DEFAULT NOW(),
    PRIMARY KEY(nlm_unique_id)
);

CREATE TABLE reference.global_search(
  pubmed_id integer NOT NULL,
  concatenated_columns_tsv tsvector NOT NULL,
  PRIMARY KEY (pubmed_id)
);

CREATE TABLE IF NOT EXISTS  reference.article_versions(
    pubmed_id INTEGER,
    version INTEGER,
    title VARCHAR,
    publication_date DATE,
    medline_date VARCHAR,
    author_list JSONB,
    nlm_unique_id VARCHAR,
    journal JSON,
    keywords JSONB,
	article_ids JSONB,
    is_translated BOOLEAN DEFAULT FALSE,
    creation_time timestamp with time zone DEFAULT NOW(),
    update_time timestamp with time zone DEFAULT NOW(),
    UNIQUE(pubmed_id, version)
);

CREATE TABLE IF NOT EXISTS reference.article_abstract_versions(
    pubmed_id INTEGER,
    associated_abstract JSONB,
    version INTEGER,
    creation_time timestamp with time zone DEFAULT NOW(),
    update_time timestamp with time zone DEFAULT NOW(),
    UNIQUE(pubmed_id, version)
);

--declare function to be used for text search queries
CREATE FUNCTION reference.gen_ts_query(ts_vector_column tsvector, ts_query_string TEXT, ts_phrase_query_string TEXT, weight CHAR) RETURNS BOOLEAN AS '
DECLARE
    to_tsquery_count INTEGER;
    phraseto_tsquery_count INTEGER;
BEGIN    
    SELECT INTO to_tsquery_count length(to_tsvector(ts_query_string));
    SELECT INTO phraseto_tsquery_count length(to_tsvector(ts_phrase_query_string));

    --check combinations
    RETURN (SELECT CASE
                WHEN to_tsquery_count+phraseto_tsquery_count = 0 THEN TRUE
                WHEN phraseto_tsquery_count=0 THEN (ts_vector_column @@ to_tsquery(ts_query_string))
                WHEN to_tsquery_count=0 THEN (ts_vector_column @@ to_tsquery(phraseto_tsquery(ts_phrase_query_string)::text || $$:*$$ || weight))
                ELSE (ts_vector_column @@ (to_tsquery(ts_query_string) || to_tsquery(phraseto_tsquery(ts_phrase_query_string)::text || $$:*$$ || weight))) END);
END;'LANGUAGE plpgsql;
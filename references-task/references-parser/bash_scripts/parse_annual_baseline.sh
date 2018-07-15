#!/bin/bash
set -e 
set -u
set -o pipefail

filepattern='pubmed18n*'
n_jobs=3

#retrieve arguments from command line
while getopts "p:n:h" opt; do
	case ${opt} in
	p) 
		filepattern=${OPTARG}
		;;
	n) 
		n_jobs=${OPTARG}
		;;
	h) 
		echo "Usage: ./parse_annual_baseline.sh"
		exit 0;;
	esac
done

temp_archive=$(mktemp -d /tmp/references_archive.XXXX)

ls -1 downloaded_data/${filepattern}.xml.gz | parallel --progress -j ${n_jobs} python3 script.py -a ${temp_archive}/article{#}.csv.gz -c ${temp_archive}/chemical{#}.csv.gz -m ${temp_archive}/mesh{#}.csv.gz -ab ${temp_archive}/abstract{#}.csv.gz -i {} -av ${temp_archive}/versions_article{#}.csv.gz -cv ${temp_archive}/versions_chemical{#}.csv.gz -mv ${temp_archive}/versions_mesh{#}.csv.gz -abv ${temp_archive}/versions_abstract{#}.csv.gz -cm 'gzip'

> /usr/src/app/data/article.csv.gz
> /usr/src/app/data/mesh.csv.gz
> /usr/src/app/data/chemical.csv.gz
> /usr/src/app/data/abstract.csv.gz
> /usr/src/app/data/deleted.csv.gz

#versions
> /usr/src/app/data/versions_article.csv.gz
> /usr/src/app/data/versions_mesh.csv.gz
> /usr/src/app/data/versions_chemical.csv.gz
> /usr/src/app/data/versions_abstract.csv.gz
> /usr/src/app/data/versions_deleted.csv.gz

for f in $(dir ${temp_archive}/article*.csv.gz); do
	cat $f >> /usr/src/app/data/article.csv.gz
done

for f in $(dir ${temp_archive}/mesh*.csv.gz); do
	cat $f >> /usr/src/app/data/mesh.csv.gz
done

for f in $(dir ${temp_archive}/chemical*.csv.gz); do
	cat $f >> /usr/src/app/data/chemical.csv.gz
done

for f in $(dir ${temp_archive}/abstract*.csv.gz); do
    cat $f >> /usr/src/app/data/abstract.csv.gz
done

#versions
for f in $(ls ${temp_archive}/versions_article*.csv.gz); do
	cat $f >> /usr/src/app/data/versions_article.csv.gz
done

for f in $(ls ${temp_archive}/versions_mesh*.csv.gz); do
	cat $f >> /usr/src/app/data/versions_mesh.csv.gz
done

for f in $(ls ${temp_archive}/versions_chemical*.csv.gz); do
	cat $f >> /usr/src/app/data/versions_chemical.csv.gz
done

for f in $(ls ${temp_archive}/versions_abstract*.csv.gz); do
    cat $f >> /usr/src/app/data/versions_abstract.csv.gz
done
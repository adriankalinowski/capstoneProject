#!/bin/bash

fileName=testReport
#defaults
PGHOST=192.168.99.100
PGUSER=cloud
PGDATABASE=capstone
PGPASSWORD=cloud

#retrieve arguments from command line
while getopts "i:u:d:p:h" opt; do
	case ${opt} in
	i) 
		PGHOST=${OPTARG}
		;;
	u) 
		PGUSER=${OPTARG}
		;;
	d)
		PGDATABASE=${OPTARG}
		;;
	p)
		PGPASSWORD=${OPTARG}
		;;
	h) 
		echo "Usage: ./parse_annual_baseline.sh"
		exit 0;;
	esac
done

export PGHOST
export PGUSER
export PGDATABASE
export PGPASSWORD

if [[ -e $fileName.txt ]] ; 
	then
    	cnt=1
    while [[ -e $fileName-$cnt.txt ]] ; 
    do
        let cnt++
    done
    	fileName=$fileName-$cnt
fi
touch "$fileName".txt

psql -c "SELECT nspname AS schemaname,relname,reltuples FROM pg_class C LEFT JOIN pg_namespace N ON (N.oid = C.relnamespace) WHERE nspname NOT IN ('pg_catalog', 'information_schema') AND relkind='r' ORDER BY reltuples DESC" >> "$fileName".txt

echo '\n' >> "$testReport".txt

pgbench -r -s 1000 -f testArticlePerformance >> "$fileName".txt

echo '\n' >> "$testReport".txt

psql -c "SELECT * FROM pg_indexes WHERE schemaname = 'reference';" >> "$fileName".txt

echo '\n' >> "$testReport".txt

psql -c "SELECT * FROM pg_stat_all_indexes where schemaname = 'reference';" >> "$fileName".txt

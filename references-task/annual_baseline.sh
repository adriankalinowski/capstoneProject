#!/bin/bash
set -euo pipefail
set -x

#rebuild docker image
docker-compose build

#assumes postgres server is already live

#variable for the pubmed file name
pubmed_file_name=${1:-pubmed18n}

#download DTD
docker run --rm -v $(pwd)/${pubmed_file_name}/downloaded/baseline:/usr/src/app/downloaded_data/ advaitacapstone/references-task:latest bash /opt/scripts/download_annual_baseline.sh -u https://dtd.nlm.nih.gov/ncbi/pubmed/out/pubmed_180101.dtd -f pubmed_180101.dtd

#download annual baseline 2018
#docker run --rm -v $(pwd)/${pubmed_file_name}/downloaded/baseline:/usr/src/app/downloaded_data/ advaitacapstone/references-task:latest bash /opt/scripts/download_annual_baseline.sh

#parse annual baseline 2018
docker run --rm -v $(pwd)/${pubmed_file_name}/parsed/baseline:/usr/src/app/data/ -v $(pwd)/${pubmed_file_name}/downloaded/baseline:/usr/src/app/downloaded_data/ advaitacapstone/references-task:latest bash /opt/scripts/parse_annual_baseline.sh

#download journal script
docker run --rm -v $(pwd)/${pubmed_file_name}/downloaded/baseline:/usr/src/app/downloaded_data/ --name reference-journal-download advaitacapstone/references-task:latest bash /opt/scripts/download_annual_baseline.sh -u ftp://ftp.ncbi.nih.gov/pubmed/J_Entrez.txt -f J_Entrez.txt

#parse journal script
docker run --rm -v $(pwd)/${pubmed_file_name}/parsed/baseline:/usr/src/app/data/ -v $(pwd)/${pubmed_file_name}/downloaded/baseline:/usr/src/app/downloaded_data/ --name reference-journal-parse advaitacapstone/references-task:latest python3 journal_script.py

#download journal cache file
docker run --rm -v $(pwd)/${pubmed_file_name}/downloaded/baseline:/usr/src/app/downloaded_data/ advaitacapstone/references-task:latest bash /opt/scripts/download_annual_baseline.sh -u ftp://ftp.ncbi.nih.gov/pubmed/jourcache.xml -f jourcache.xml

#parse journal cache script
docker run --rm -v $(pwd)/${pubmed_file_name}/parsed/baseline:/usr/src/app/data/ -v $(pwd)/${pubmed_file_name}/downloaded/baseline:/usr/src/app/downloaded_data/ --name reference-asdf-parse advaitacapstone/references-task:latest python3 journal_cache_script.py

#import into Postgres
docker run --rm \
-v $(pwd)/${pubmed_file_name}/parsed/baseline:/usr/src/app/data/ \
--security-opt seccomp=unconfined \
-e PGOPTIONS="--search_path=reference,public" \
-e PGHOST=192.168.99.100 \
-e PGDATABASE=capstone \
-e PGUSER=cloud \
-e PGPASSWORD=cloud \
--name references-daily-update -it advaitacapstone/references-task \
bash /opt/scripts/import_csv.sh \

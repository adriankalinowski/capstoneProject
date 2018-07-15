#!/bin/bash
set -euo pipefail
set -x

#include config file for default variables
. $(pwd)/references-parser/bash_scripts/reference.config

#run daily update task in docker container
docker run --rm \
-v $(pwd)/${pubmed_file_name}/parsed/updates:/data/ \
-v $(pwd)/${pubmed_file_name}/downloaded/updates:/usr/src/app/downloaded_data/ \
-v $(pwd)/${pubmed_file_name}/parsed/stats:/data_stats/ \
-v $(pwd)/${pubmed_file_name}/downloaded/stats:/usr/src/app/downloaded_data_stats/ \
-e PGOPTIONS="--search_path=reference,public" \
-e PGHOST=192.168.99.100 \
-e PGDATABASE=capstone \
-e PGUSER=cloud \
-e PGPASSWORD=cloud \
-e pubmed_file_name=pubmed18n \
-e pubmed_file_end='.xml.gz' \
-e update_url_name='https://ftp.ncbi.nlm.nih.gov/pubmed/updatefiles/' \
-e first_update_file_num=929 \
--name references-daily-update -it advaitacapstone/references-task \
bash /opt/scripts/run_daily_update.sh \
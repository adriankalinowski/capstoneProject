#!/bin/bash
set -euo pipefail
set -x

#include config file for default variables
. $(pwd)/references-parser/bash_scripts/reference.config

#download daily update files
docker run --rm -v $(pwd)/${pubmed_file_name}/downloaded/updates:/usr/src/app/downloaded_data/ advaitacapstone/references-task:latest bash /opt/scripts/download_annual_baseline.sh -u ftp://ftp.ncbi.nlm.nih.gov/pubmed/updatefiles/ -f pubmed18n*.xml.gz

#download daily update stat files 
docker run --rm -v $(pwd)/${pubmed_file_name}/downloaded/stats:/usr/src/app/downloaded_data/ advaitacapstone/references-task:latest bash /opt/scripts/download_annual_baseline.sh -u ftp://ftp.ncbi.nlm.nih.gov/pubmed/updatefiles/ -f pubmed18n*_stats.html
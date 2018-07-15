#!/bin/bash
#to do: run parsing docker containers in parrellel
#to do: enhance script to accomodate for a year other than 2018
set -e 
set -u
set -o pipefail

#include config file for default variables
. /opt/scripts/reference.config

#retrieve arguments from command line
while getopts ":t:r:w:u:d:f:" opt; do
	case ${opt} in
	t) timeout=${OPTARG};;
    r) tries=${OPTARG};;
    w) wait=${OPTARG};;
	u) baseline_url=${OPTARG};;
    d) down_path=${OPTARG};;
    f) file_name_wildcard=${OPTARG};;
	h) 
		echo "Usage: ./download_annual_baseline.sh -t [timeout] -r [retries] -w [wait] -u [URL] -d [down path] -f [file name]"
		exit 0;;
	esac
done

#download files from NLM
# -r = download recursively
# -t = number of tries it will perform if there is ever a timeout
# -P = prefix of the output directory
# -A = downloads all files in the directory located in the url with those parameters
wget ${baseline_url} -r --no-clobber --no-directories -A ${file_name_wildcard} --timeout=${timeout} --wait=${wait} -t ${tries} --retry-connrefused -P ${down_path}

#!/bin/bash
set -euo pipefail
set -x

counter=929
until [[ $counter -gt 1084 ]]; do
	time sh daily_update.sh >> daily_update_logs.txt
done
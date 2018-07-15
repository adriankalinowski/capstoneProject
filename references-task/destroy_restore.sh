#!/bin/bash

docker stop pg
docker rm -v pg
docker-compose build

sh start_postgres.sh
sh annual_baseline.sh
sh daily_update.sh
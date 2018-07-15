#!/bin/bash
set -euo pipefail
set -x

docker-compose build

#stop pg, if pg is not running return true to keep script from exiting
#remove pg, if pg does not exist return true to keep script from exiting
docker stop pg || true && docker rm -v pg || true

docker run --name pg --publish "5432:5432" -e POSTGRES_USER=cloud -e POSTGRES_PASSWORD=cloud -e POSTGRES_DB=capstone -d advaitacapstone/postgres-references:9.6.6 -c "work_mem=4000kB" -c "shared_buffers=2GB" -c "maintenance_work_mem=256MB" -c "effective_cache_size=6GB"

docker logs -f pg

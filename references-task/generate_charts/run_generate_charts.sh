#!/bin/bash

docker build -t generate_charts:latest ./

docker stop stats_notebook | docker rm stats_notebook || true

docker run --name stats_notebook \
--publish "8890:8890" \
-v $(pwd)/graphs:/usr/src/app/graphs/ \
-e PGHOST=192.168.99.100 \
-e PGDATABASE=capstone \
-e PGUSER=cloud \
-e PGPASSWORD=cloud \
-e GRAPHSPATH=/usr/src/app/graphs/ \
-e NOTEBOOK_PORT=8890 \
generate_charts:latest bash ./start_notebook.sh

docker exec stats_notebook bash -c "python3 generate_charts.py"
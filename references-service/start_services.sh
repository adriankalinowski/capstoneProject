#!/bin/bash

cd registry
mvn clean package docker:build
docker run -p 8761:8761 --name registry -d advaitacapstone/registry:0.0.1-SNAPSHOT

cd ../references
mvn clean package docker:build
docker run -p 8080:8080 --name references -d advaitacapstone/references:0.0.1-SNAPSHOT

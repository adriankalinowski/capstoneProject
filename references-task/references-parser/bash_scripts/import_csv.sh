#!/bin/bash
set -euo pipefail

#load baseline consisting of first version references
zcat /usr/src/app/data/article.csv.gz | pgloader article.load
zcat /usr/src/app/data/mesh.csv.gz | pgloader mesh.load
zcat /usr/src/app/data/chemical.csv.gz | pgloader chemical.load
zcat /usr/src/app/data/abstract.csv.gz | pgloader abstract.load
pgloader journal.load

#load versions
zcat /usr/src/app/data/versions_article.csv.gz | pgloader version_article.load
zcat /usr/src/app/data/versions_mesh.csv.gz | pgloader version_mesh.load
zcat /usr/src/app/data/versions_chemical.csv.gz | pgloader version_chemical.load
zcat /usr/src/app/data/versions_abstract.csv.gz | pgloader version_abstract.load

#vacuum analyze tables
bash /opt/scripts/vacuum_analyze.sh
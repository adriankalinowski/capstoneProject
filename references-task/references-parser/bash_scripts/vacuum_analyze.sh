#!/bin/bash
set -xeuo pipefail

psql -c "VACUUM ANALYZE VERBOSE reference.article;"
psql -c "VACUUM ANALYZE VERBOSE reference.mesh_article;"
psql -c "VACUUM ANALYZE VERBOSE reference.mesh;"
psql -c "VACUUM ANALYZE VERBOSE reference.article_chemical_compound;"
psql -c "VACUUM ANALYZE VERBOSE reference.abstract_article;"
psql -c "VACUUM ANALYZE VERBOSE reference.journal;"
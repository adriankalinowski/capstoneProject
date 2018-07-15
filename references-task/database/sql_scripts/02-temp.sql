DROP SCHEMA IF EXISTS temp CASCADE;
CREATE SCHEMA temp;

CREATE TABLE IF NOT EXISTS  temp.article
(
    pubmed_id INTEGER UNIQUE NOT NULL,
    version INTEGER,
    title VARCHAR,
    publication_date DATE,
    medline_date VARCHAR,
    author_list JSONB,
    nlm_unique_id VARCHAR,
    journal JSONB,
    keywords JSONB, 
    article_ids JSONB,
    is_translated BOOLEAN DEFAULT FALSE
); 

CREATE TABLE IF NOT EXISTS temp.abstract
(
    pubmed_id INTEGER,
    associated_abstract JSONB,
    version INTEGER
);

CREATE TABLE IF NOT EXISTS temp.chemical_compound
(
    chemical_compound_id VARCHAR,
    chemical_compound VARCHAR,
    pubmed_id INTEGER,
    version INTEGER
);

CREATE TABLE IF NOT EXISTS temp.mesh
(
    term_id VARCHAR,
    term VARCHAR,
    pubmed_id INTEGER,
    version INTEGER
);

CREATE TABLE IF NOT EXISTS temp.deleted
(
    pubmed_id INTEGER UNIQUE NOT NULL,
    version INTEGER NOT NULL
);

CREATE TABLE IF NOT EXISTS temp.article_versions (LIKE temp.article);

CREATE TABLE IF NOT EXISTS temp.article_abstract_versions (LIKE temp.abstract);

CREATE TABLE IF NOT EXISTS temp.chemical_versions (LIKE temp.chemical_compound);

CREATE TABLE IF NOT EXISTS temp.mesh_versions (LIKE temp.mesh);

CREATE TABLE IF NOT EXISTS temp.deleted_versions (LIKE temp.deleted);

CREATE TABLE IF NOT EXISTS temp.journal (LIKE reference.journal);
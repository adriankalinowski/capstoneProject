--SQL script to run import on daily update data and store data integrity statistics
--requires a parameter "update_uuid" to be passed which represents the corresponding entry
--in the update_database table
BEGIN TRANSACTION;

INSERT INTO reference.statistics VALUES(0, 0, 0);

SELECT * FROM import_csv(:update_uuid::uuid);

UPDATE reference.update_database
SET statistics=JSON_BUILD_OBJECT('updated', (SELECT records_updated FROM reference.statistics), 'deleted', (SELECT records_deleted FROM reference.statistics), 'inserted', (SELECT records_inserted FROM reference.statistics))
WHERE update_id=:update_uuid;

TRUNCATE TABLE reference.statistics;

UPDATE reference.update_database 
SET status='COMPLETE' 
WHERE update_id=:update_uuid::uuid;

COMMIT;
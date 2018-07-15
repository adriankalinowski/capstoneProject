--declare function to be used for text search queries
CREATE FUNCTION reference.gen_ts_query(ts_vector_column tsvector, ts_query_string TEXT, ts_phrase_query_string TEXT) RETURNS BOOLEAN AS '
DECLARE
	to_tsquery_count INTEGER;
	phraseto_tsquery_count INTEGER;
BEGIN    
    SELECT INTO to_tsquery_count length(to_tsvector(ts_query_string));
	SELECT INTO phraseto_tsquery_count length(to_tsvector(ts_phrase_query_string));
    
    --check combinations
    RETURN (SELECT CASE
           		WHEN to_tsquery_count+phraseto_tsquery_count = 0 THEN TRUE
           		WHEN phraseto_tsquery_count=0 THEN (ts_vector_column @@ to_tsquery(ts_query_string))
           		WHEN to_tsquery_count=0 THEN (ts_vector_column @@ phraseto_tsquery(ts_phrase_query_string))
           		ELSE (ts_vector_column @@ (to_tsquery(ts_query_string) || phraseto_tsquery(ts_phrase_query_string))) END);
END;'LANGUAGE plpgsql;


--declare functions to increment counts
CREATE FUNCTION reference.records_updated_function() RETURNS TRIGGER AS
$BODY$BEGIN    
	if new.version = 1 then
		UPDATE reference.statistics SET records_updated = records_updated +1;
	end if;
    RETURN NULL;
END;$BODY$LANGUAGE plpgsql;
   
CREATE FUNCTION reference.records_deleted_function() RETURNS TRIGGER AS
$BODY$BEGIN
    UPDATE reference.statistics SET records_deleted = records_deleted +1;
    RETURN NULL;
END;$BODY$LANGUAGE plpgsql;

CREATE FUNCTION reference.records_inserted_function() RETURNS TRIGGER AS
$BODY$BEGIN
	if new.version > 1 then
		UPDATE reference.statistics SET records_deleted = records_deleted -1;
	else
		UPDATE reference.statistics SET records_inserted = records_inserted +1;
	end if;

	RETURN NULL;
END;$BODY$LANGUAGE plpgsql;

CREATE FUNCTION reference.daily_update_status_function() RETURNS TRIGGER AS
$BODY$BEGIN
    NEW.update_time = current_timestamp;
    RETURN NEW;
END;$BODY$LANGUAGE plpgsql;

 --create triggers
CREATE TRIGGER records_inserted_trigger AFTER INSERT ON reference.article FOR EACH ROW
	EXECUTE PROCEDURE reference.records_inserted_function();

CREATE TRIGGER records_deleted_trigger AFTER DELETE on reference.article FOR EACH ROW
	EXECUTE PROCEDURE reference.records_deleted_function();
		
CREATE TRIGGER records_updated_trigger AFTER UPDATE on reference.article FOR EACH ROW
	EXECUTE PROCEDURE reference.records_updated_function();

CREATE TRIGGER daily_update_status BEFORE UPDATE ON reference.update_database FOR EACH ROW
	EXECUTE PROCEDURE reference.daily_update_status_function();
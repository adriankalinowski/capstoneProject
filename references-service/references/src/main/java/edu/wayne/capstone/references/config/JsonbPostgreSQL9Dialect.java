package edu.wayne.capstone.references.config;

import java.sql.Types;

import org.hibernate.dialect.PostgreSQL9Dialect;

public class JsonbPostgreSQL9Dialect extends PostgreSQL9Dialect {

	public static final String ARTICLE_TEXT_SEARCH_OP = "article_text_search_operator";
	public static final String GLOBAL_TEXT_SEARCH_OP = "global_text_search_operator";
	
	public JsonbPostgreSQL9Dialect() {
		super();
		this.registerColumnType(Types.JAVA_OBJECT, "jsonb");
		this.registerFunction(ARTICLE_TEXT_SEARCH_OP, new ArticleTextSearchOperator());
		this.registerFunction(GLOBAL_TEXT_SEARCH_OP, new GlobalTextSearchOperator());
	}
}

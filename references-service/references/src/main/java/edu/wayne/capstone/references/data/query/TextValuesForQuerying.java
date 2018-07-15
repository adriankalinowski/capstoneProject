package edu.wayne.capstone.references.data.query;

import javax.persistence.Embeddable;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;

@Data
@Embeddable
public class TextValuesForQuerying {
	
	public static final String CONTAINS = "contains";
	
	public static final String STARTSWITH = "startsWith"; 
	
	public static final String ENDSWITH = "endsWith"; 
	
	public static final String VALUE = "value";
	
	@JsonProperty(CONTAINS)
	String contains;
	
	@JsonProperty(STARTSWITH)
	String startsWith;
	
	@JsonProperty(ENDSWITH)
	String endsWith;
	
	@JsonProperty(VALUE)
	String value;
}

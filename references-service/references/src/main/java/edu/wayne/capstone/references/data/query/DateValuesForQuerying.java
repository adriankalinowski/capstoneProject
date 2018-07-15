package edu.wayne.capstone.references.data.query;

import java.util.Date;
import javax.persistence.Embeddable;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import org.springframework.format.annotation.DateTimeFormat;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Embeddable
public class DateValuesForQuerying {
	
	public static final String TO = "to";
	
	public static final String FROM = "from";
	
	public static final String VALUE = "value";
	
	public static final String TOYEAR = "toYear";
	
	public static final String FROMYEAR = "fromYear";
	
	public static final String YEAR = "year";
	
	//Stores the most recent date parameter in query
	@JsonProperty(TO)
	@Temporal(TemporalType.DATE)
	@DateTimeFormat(pattern = "yyyy-MM-dd")	
	Date to;
	
	//Stores the earliest date parameter in query
	@JsonProperty(FROM)
	@Temporal(TemporalType.DATE)
	@DateTimeFormat(pattern = "yyyy-MM-dd")
	Date from;
	
	@JsonProperty(VALUE)
	@Temporal(TemporalType.DATE)
	@DateTimeFormat(pattern = "yyyy-MM-dd")
	Date value;
	
	//Stores the most recent date (year) parameter in query
	@JsonProperty(TOYEAR)
	@Temporal(TemporalType.DATE)
	@DateTimeFormat(pattern = "yyyy")	
	Date toYear;
	
	//Stores the earliest date (year) parameter in query
	@JsonProperty(FROMYEAR)
	@Temporal(TemporalType.DATE)
	@DateTimeFormat(pattern = "yyyy")	
	Date fromYear;
	
	@JsonProperty(YEAR)
	@Temporal(TemporalType.DATE)
	@DateTimeFormat(pattern = "yyyy")
	Date year;
}

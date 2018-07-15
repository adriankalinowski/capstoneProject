package edu.wayne.capstone.references.data.query;

import javax.persistence.Embeddable;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
@Embeddable
public class NumericValuesForQuerying {
	
	 public static final String MIN = "min";
	
	 public static final String MAX = "max";
	
	 public static final String VALUE = "value";
	 
	 @JsonProperty(MIN)
	 Integer min;
	 
	 @JsonProperty(MAX)
	 Integer max;
	 
	 @JsonProperty(VALUE)
	 Integer value;
	
	 
}

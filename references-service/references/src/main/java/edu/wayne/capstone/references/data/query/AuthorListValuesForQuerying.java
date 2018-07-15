package edu.wayne.capstone.references.data.query;

import javax.persistence.Embeddable;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
@Embeddable
public class AuthorListValuesForQuerying {
	public static final String LASTNAME = "lastName";
	
	@JsonProperty(LASTNAME)
	String lastName;
	
}

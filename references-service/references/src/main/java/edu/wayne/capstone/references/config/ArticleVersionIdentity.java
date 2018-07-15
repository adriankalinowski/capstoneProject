package edu.wayne.capstone.references.config;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Embeddable;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Embeddable
@Data
@AllArgsConstructor
@NoArgsConstructor
public class ArticleVersionIdentity implements Serializable{
	
	private static final long serialVersionUID = 1L;

	@Column(name="pubmed_id", insertable = false, updatable = false)
	private Integer pubmedId;
	
	@Column(name="version", insertable = false, updatable = false)
	private Integer version;
	
	@Override
	public String toString() {
		return String.valueOf(pubmedId) + "." + String.valueOf(version);
	}
}

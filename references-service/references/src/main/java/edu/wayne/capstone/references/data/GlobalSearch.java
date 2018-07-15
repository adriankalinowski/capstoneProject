package edu.wayne.capstone.references.data;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import lombok.Data;

@Entity
@Table(name="global_search")
@Data
public class GlobalSearch {
	@Id
	@Column(name="pubmed_id")
	private Integer id;
	
	@Column(name = "pubmed_id", insertable = false, updatable = false)
	private String authors;
	
	@Column(name = "pubmed_id", insertable = false, updatable = false)
	private String keywords;
	
	@Column(name = "pubmed_id", insertable = false, updatable = false)
	private String title;
	
	@Column(name = "pubmed_id", insertable = false, updatable = false)
	private String journalTitle;
	
	@Column(name = "pubmed_id", insertable = false, updatable = false)
	private String all;
	
	@OneToOne(mappedBy="globalSearch")
    private Article article;
}

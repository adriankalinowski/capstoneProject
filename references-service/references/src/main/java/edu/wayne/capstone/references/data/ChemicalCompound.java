package edu.wayne.capstone.references.data;

import java.util.Date;
import java.util.List;

import javax.persistence.AttributeOverride;
import javax.persistence.AttributeOverrides;
import javax.persistence.Column;
import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.ManyToMany;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

import edu.wayne.capstone.references.data.query.TextValuesForQuerying;
import lombok.Data;

@Entity
@Table(name="chemical_compound")
@Data
public class ChemicalCompound {
	
	@Id
	private String chemicalCompoundId;
	
	@JsonProperty("name")
	@Column(name = "chemical_compound")
	private String name;
	
	//This variable is used for customizing filtering by chemical compound 
	@JsonIgnore
	@Embedded
	@AttributeOverrides({
		@AttributeOverride(name = "value", column = @Column(name = "chemical_compound", insertable = false, updatable = false)),
		@AttributeOverride(name = "contains", column = @Column(name = "chemical_compound", insertable = false, updatable = false)),
		@AttributeOverride(name = "startsWith", column = @Column(name = "chemical_compound", insertable = false, updatable = false)),
		@AttributeOverride(name = "endsWith", column = @Column(name = "chemical_compound", insertable = false, updatable = false)) })
	private TextValuesForQuerying nameFilter;
	
    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "creation_time", nullable=false, insertable = false, updatable = false)
	private Date creationTime;
	
    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "update_time", nullable=false, insertable = false, updatable = true)
	private Date updateTime;
    
    @ManyToMany(mappedBy="chemicals")
    private List<Article> articles;
}

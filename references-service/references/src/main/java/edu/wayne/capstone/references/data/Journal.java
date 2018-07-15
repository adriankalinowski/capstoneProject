package edu.wayne.capstone.references.data;

import java.util.Date;
import java.util.List;

import javax.persistence.AttributeOverride;
import javax.persistence.AttributeOverrides;
import javax.persistence.Column;
import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import org.hibernate.annotations.Type;
import org.hibernate.annotations.TypeDef;
import org.hibernate.annotations.TypeDefs;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.node.ObjectNode;

import edu.wayne.capstone.references.config.JsonbUserType;
import edu.wayne.capstone.references.data.query.TextValuesForQuerying;
import lombok.Data;

@TypeDefs({
	@TypeDef(name=JsonbUserType.OBJECT_NODE, typeClass=JsonbUserType.class),
})
@Entity
@Table(name="journal")
@Data
public class Journal {
	
	@Id
	@Column(name="nlm_unique_id")
	private String nlmUniqueId;
	
	@JsonProperty("journalTitle")
	@Column(name="journal_title")
	private String journalTitle;
	
	@JsonIgnore
	@Embedded
	@AttributeOverrides({
		@AttributeOverride(name = "value", column = @Column(name = "journal_title", insertable = false, updatable = false)),
		@AttributeOverride(name = "contains", column = @Column(name = "journal_title", insertable = false, updatable = false)),
		@AttributeOverride(name = "startsWith", column = @Column(name = "journal_title", insertable = false, updatable = false)),
		@AttributeOverride(name = "endsWith", column = @Column(name = "journal_title", insertable = false, updatable = false)) })
	private TextValuesForQuerying journalTitleFilter;
	
	@JsonProperty("issnPrint")
	private String issnPrint;
	
	@JsonProperty("issnOnline")
	private String issnOnline;
	
	@JsonProperty("isoAbbr")
	private String isoAbbr;
	
	@JsonProperty("medAbbr")
	private String medAbbr;
	
	@JsonProperty("journalId")
	private String journalId;
	
	@JsonProperty("startYear")
	private String startYear;
	
	@JsonProperty("endYear")
	private String endYear;
	
	@JsonProperty("activityFlag")
	private String activityFlag;
	
	@Type(type=JsonbUserType.OBJECT_NODE)
	@JsonProperty("alias")
	@Column(name="alias")
	private ObjectNode alias;
	
	@JsonProperty("source")
	private String source;
	
    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "creation_time", nullable=false, insertable = false, updatable = false)
	private Date creationTime;
	
    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "update_time", nullable=false, insertable = false, updatable = true)
	private Date updateTime;
	
	@OneToMany(mappedBy = "journal")
    public List<Article> articles;
	
}
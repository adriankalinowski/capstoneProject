package edu.wayne.capstone.references.data;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import org.hibernate.annotations.Type;
import org.hibernate.annotations.TypeDef;
import org.hibernate.annotations.TypeDefs;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;

import edu.wayne.capstone.references.config.ArticleVersionIdentity;
import edu.wayne.capstone.references.config.JsonbArrayUserType;
import edu.wayne.capstone.references.config.JsonbUserType;
import lombok.Data;

@TypeDefs({
	@TypeDef(name=JsonbUserType.OBJECT_NODE, typeClass=JsonbUserType.class),
	@TypeDef(name=JsonbArrayUserType.ARRAY_NODE, typeClass=JsonbArrayUserType.class),
})
@Entity
@Table(name="article_versions")
@Data
public class ArticleVersion {

	@EmbeddedId
	private ArticleVersionIdentity id;
	
	@Column(name="version", insertable = false, updatable = false)
	private Integer version;

	@JsonProperty("title")
	@Column(name = "title")
	private String title;
	
	@Temporal(TemporalType.DATE)
	@JsonProperty("publicationDate")
	@Column(name = "publication_date")
	private Date publicationDate;
	
	private String medlineDate;
	
	@Type(type=JsonbArrayUserType.ARRAY_NODE)
	@JsonProperty("authorList")
	@Column(name="author_list")
    private ArrayNode authorList;

	@Type(type=JsonbUserType.OBJECT_NODE)
	@JsonProperty("journalDetails")
	@Column(name="journal")
    private ObjectNode journalDetails;
	
	@JsonProperty("translated")
	@Column(name="is_translated")
	private boolean translated;
	
    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "creation_time", nullable=false, insertable = false, updatable = false)
	private Date creationTime;
	
    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "update_time", nullable=false, insertable = false, updatable = true)
	private Date updateTime;
    
    @ManyToOne()
    @JoinColumn(name = "nlm_unique_id")
    private Journal journal;
	
	@ManyToOne
	@JoinColumn(name="pubmed_id", insertable = false, updatable = false)
	private Article article;
}

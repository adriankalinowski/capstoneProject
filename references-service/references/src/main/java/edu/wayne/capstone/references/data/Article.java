package edu.wayne.capstone.references.data;

import java.util.Date;
import java.util.List;
import javax.persistence.AttributeOverride;
import javax.persistence.AttributeOverrides;
import javax.persistence.Column;
import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import org.hibernate.annotations.Type;
import org.hibernate.annotations.TypeDef;
import org.hibernate.annotations.TypeDefs;
import org.springframework.data.rest.core.annotation.RestResource;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import edu.wayne.capstone.references.config.JsonbArrayUserType;
import edu.wayne.capstone.references.config.JsonbUserType;
import edu.wayne.capstone.references.data.query.DateValuesForQuerying;
import edu.wayne.capstone.references.data.query.TextValuesForQuerying;
import lombok.Data;

@TypeDefs({
	@TypeDef(name=JsonbUserType.OBJECT_NODE, typeClass=JsonbUserType.class),
	@TypeDef(name=JsonbArrayUserType.ARRAY_NODE, typeClass=JsonbArrayUserType.class),
})
@Entity
@Table(name="article")
@Data
public class Article {
	
	@Id
	@Column(name="pubmed_id")
	private Integer id;
	
	private Integer version;
	
	//This variable is used for customizing filtering by pubmed id
    @JsonIgnore
    @Column(name="pubmed_id", insertable = false, updatable = false)
    private String pubmedId;
	
	@JsonProperty("title")
	@Column(name = "title")
	private String title;
	
	//This variable is used for customizing filtering by title 
	@JsonIgnore
	@Embedded
	@AttributeOverrides({
		@AttributeOverride(name = "value", column = @Column(name = "title", insertable = false, updatable = false)),
		@AttributeOverride(name = "contains", column = @Column(name = "title", insertable = false, updatable = false)),
		@AttributeOverride(name = "startsWith", column = @Column(name = "title", insertable = false, updatable = false)),
		@AttributeOverride(name = "endsWith", column = @Column(name = "title", insertable = false, updatable = false)) })
	private TextValuesForQuerying titleFilter;
	
	@Temporal(TemporalType.DATE)
	@JsonProperty("publicationDate")
	@Column(name = "publication_date")
	private Date publicationDate;
	
	//This variable is used for customizing filtering by publication date 
	@JsonIgnore
	@Embedded
	@AttributeOverrides({
		@AttributeOverride(name = "value", column = @Column(name = "publication_date", insertable = false, updatable = false)),
		@AttributeOverride(name = "year", column = @Column(name = "publication_date", insertable = false, updatable = false)),
		@AttributeOverride(name = "toYear", column = @Column(name = "publication_date", insertable = false, updatable = false)),
		@AttributeOverride(name = "fromYear", column = @Column(name = "publication_date", insertable = false, updatable = false)),
		@AttributeOverride(name = "to", column = @Column(name = "publication_date", insertable = false, updatable = false)),
		@AttributeOverride(name = "from", column = @Column(name = "publication_date", insertable = false, updatable = false)) })
	private DateValuesForQuerying publicationDateFilter;
	
	private String medlineDate;
	
	@Type(type=JsonbArrayUserType.ARRAY_NODE)
	@JsonProperty("authorList")
	@Column(name="author_list")
    private ArrayNode authorList;

	@Type(type=JsonbUserType.OBJECT_NODE)
	@JsonProperty("journalDetails")
	@Column(name="journal")
    private ObjectNode journalDetails;
	
	@Type(type=JsonbUserType.OBJECT_NODE)
	@JsonProperty("keywords")
	@Column(name="keywords")
	private ObjectNode keywords;

	@Type(type=JsonbArrayUserType.ARRAY_NODE)
	@JsonProperty("articleIds")
	@Column(name="article_ids")
	private ArrayNode articleIds;
	
	@JsonProperty("translated")
	@Column(name="is_translated")
	private boolean translated;
	
    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "creation_time", nullable=false, insertable = false, updatable = false)
	private Date creationTime;
	
    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "update_time", nullable=false, insertable = false, updatable = true)
	private Date updateTime;
    
    @ManyToMany()
    @JoinTable(
    		name = "mesh_article", 
    		joinColumns = { @JoinColumn(name = "pubmed_id") }, 
    		inverseJoinColumns = { @JoinColumn(name = "term_id") }
    )
    private List<MeshTerm> mesh;
   
    @ManyToMany()
    @JoinTable(
    		name = "article_chemical_compound", 
    		joinColumns = { @JoinColumn(name = "pubmed_id") }, 
    		inverseJoinColumns = { @JoinColumn(name = "chemical_compound_id") }
    )
    private List<ChemicalCompound> chemicals;
    
    @ManyToMany()
    @JoinTable(
    		name = "update_article",
    		joinColumns = { @JoinColumn(name = "pubmed_id")},
    		inverseJoinColumns = { @JoinColumn(name = "update_id")}
    )
    private List<UpdateDatabase> updates;
    
    @OneToOne()
    @JoinTable(
    		name = "abstract_article",
    		joinColumns = { @JoinColumn(name = "pubmed_id")},
    		inverseJoinColumns = { @JoinColumn(name = "pubmed_id")}
    )
    @RestResource(path="abstract", rel="abstract")
    private AbstractArticle associatedAbstract;
    
    @ManyToOne()
    @JoinColumn(name = "nlm_unique_id")
    private Journal journal;
    
    @JsonIgnore
    @OneToOne()
    @JoinTable(
    		name = "global_search",
    		joinColumns = { @JoinColumn(name = "pubmed_id")},
    		inverseJoinColumns = { @JoinColumn(name = "pubmed_id")}
    )
    private GlobalSearch globalSearch;
    
    @OneToMany(mappedBy="article")
    private List<ArticleVersion> articleVersions;
}
package edu.wayne.capstone.references.data;

import java.util.Date;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import org.hibernate.annotations.Type;
import org.hibernate.annotations.TypeDef;
import org.hibernate.annotations.TypeDefs;
import com.fasterxml.jackson.databind.node.ArrayNode;
import edu.wayne.capstone.references.config.JsonbArrayUserType;
import lombok.Data;

@TypeDefs({
	@TypeDef(name=JsonbArrayUserType.ARRAY_NODE, typeClass=JsonbArrayUserType.class),
})
@Entity
@Table(name="abstract_article")
@Data
public class AbstractArticle {
	@Id
	@Column(name="pubmed_id")
	private Integer id;
	
	@Type(type=JsonbArrayUserType.ARRAY_NODE)
	@Column(name="associated_abstract", nullable=false, insertable = false, updatable = false)
    private ArrayNode sections;
	
	@Temporal(TemporalType.TIMESTAMP)
    @Column(name = "creation_time", nullable=false, insertable = false, updatable = false)
	private Date creationTime;
	
    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "update_time", nullable=false, insertable = false, updatable = true)
	private Date updateTime;
    
    @OneToOne(mappedBy="associatedAbstract")
    private Article article;
}

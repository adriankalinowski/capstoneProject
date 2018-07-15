package edu.wayne.capstone.references.data;

import java.util.Date;
import java.util.List;
import java.util.UUID;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.Id;
import javax.persistence.ManyToMany;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import org.hibernate.annotations.Type;
import org.hibernate.annotations.TypeDef;
import org.hibernate.annotations.TypeDefs;

import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;

import edu.wayne.capstone.references.config.JsonbArrayUserType;
import edu.wayne.capstone.references.config.JsonbUserType;
import lombok.Data;

@TypeDefs({
	@TypeDef(name=JsonbUserType.OBJECT_NODE, typeClass=JsonbUserType.class),
	@TypeDef(name=JsonbArrayUserType.ARRAY_NODE, typeClass=JsonbArrayUserType.class),
})
@Entity
@Table(name="update_database")
@Data
public class UpdateDatabase {

	@Id
	private UUID updateId;
	
	private String pubmedUpdateId;
	
	private Integer updateNum;
	
	@Enumerated(EnumType.STRING)
	private Status status;
	
	private String errorMessage;
	
    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "creation_time", nullable=false, insertable = false, updatable = false)
	private Date creationTime;
	
    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "update_time", nullable=false, insertable = false, updatable = true)
	private Date updateTime;
    
    @Type(type=JsonbUserType.OBJECT_NODE)
    private ObjectNode statistics;

    @Type(type=JsonbArrayUserType.ARRAY_NODE)
    private ArrayNode pubmedStatistics;
    
    @ManyToMany(mappedBy = "updates")
    private List<Article> articles;
}

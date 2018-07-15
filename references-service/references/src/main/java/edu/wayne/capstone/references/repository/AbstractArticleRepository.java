package edu.wayne.capstone.references.repository;

import org.springframework.data.rest.core.annotation.RepositoryRestResource;
import edu.wayne.capstone.references.data.AbstractArticle;

@RepositoryRestResource(path="abstracts", collectionResourceRel="abstracts", itemResourceRel="abstract")
public interface AbstractArticleRepository extends ReadOnlyJpaRepository<AbstractArticle, Integer> {

}

package edu.wayne.capstone.references.repository;

import edu.wayne.capstone.references.config.ArticleVersionIdentity;
import edu.wayne.capstone.references.data.ArticleVersion;

public interface ArticleVersionRepository extends ReadOnlyJpaRepository<ArticleVersion, ArticleVersionIdentity>{

}

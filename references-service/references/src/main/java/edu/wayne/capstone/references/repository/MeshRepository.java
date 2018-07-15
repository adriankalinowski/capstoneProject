package edu.wayne.capstone.references.repository;

import org.springframework.data.querydsl.binding.QuerydslBindings;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;
import edu.wayne.capstone.references.data.MeshTerm;
import edu.wayne.capstone.references.data.QMeshTerm;


@RepositoryRestResource
public interface MeshRepository extends ReadOnlyJpaRepository<MeshTerm, String>, ReadOnlyQueryDslExecuter<MeshTerm, String, QMeshTerm>  {
	
	@Override
	default void repositorySpecificCustomize(QuerydslBindings bindings, QMeshTerm root) {
		bindings.bind(root.termId).all((path, values) -> path.in(sanitizeStringCollection(values)));
	}
}

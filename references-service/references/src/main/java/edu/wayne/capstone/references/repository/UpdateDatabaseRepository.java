package edu.wayne.capstone.references.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.querydsl.binding.QuerydslBindings;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;
import java.util.UUID;

import edu.wayne.capstone.references.data.QUpdateDatabase;
import edu.wayne.capstone.references.data.UpdateDatabase;

@RepositoryRestResource
public interface UpdateDatabaseRepository extends JpaRepository<UpdateDatabase, UUID>, ReadOnlyQueryDslExecuter<UpdateDatabase, UUID, QUpdateDatabase>{
	
	@Override
	default void repositorySpecificCustomize(QuerydslBindings bindings, QUpdateDatabase root) {
	
	}
}

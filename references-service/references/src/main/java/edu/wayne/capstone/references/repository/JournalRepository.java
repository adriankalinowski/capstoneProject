package edu.wayne.capstone.references.repository;

import org.springframework.data.querydsl.binding.QuerydslBindings;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;
import edu.wayne.capstone.references.data.Journal;
import edu.wayne.capstone.references.data.QJournal;

@RepositoryRestResource
public interface JournalRepository extends ReadOnlyJpaRepository<Journal, String>, ReadOnlyQueryDslExecuter<Journal, String, QJournal> {
	
	
	@Override
	default void repositorySpecificCustomize(QuerydslBindings bindings, QJournal root) {
		bindings.bind(root.nlmUniqueId, root.issnPrint, root.issnOnline)
		.all((path, values) -> path.in(sanitizeStringCollection(values)));
		
		bindings.excluding(root.isoAbbr);
	}
}

package edu.wayne.capstone.references.repository;

import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import org.springframework.data.querydsl.binding.QuerydslBindings;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;
import com.querydsl.core.types.dsl.BooleanTemplate;
import com.querydsl.core.types.dsl.Expressions;

import edu.wayne.capstone.references.config.BadPubmedIdException;
import edu.wayne.capstone.references.data.Article;
import edu.wayne.capstone.references.data.QArticle;

@RepositoryRestResource
public interface ArticleRepository extends ReadOnlyJpaRepository<Article, Integer>, ReadOnlyQueryDslExecuter<Article, Integer, QArticle> {

	@Override
	default void repositorySpecificCustomize(QuerydslBindings bindings, QArticle root) {
		bindings.bind(root.pubmedId).all((path, values) -> {
			try {
				return root.id.in(Stream.of(values.stream().collect(Collectors.joining(","))
				 .split(",")).filter(Objects::nonNull).filter(s -> !s.isEmpty()).map(Integer::valueOf).collect(Collectors.toList()));
			}
			catch (NumberFormatException e) {
				throw new BadPubmedIdException();
			}
		});
		
		//Filter by title in global search column (using weight A)
		bindings.bind(root.globalSearch.title).all((path, values) -> {
			BooleanTemplate t = createBooleanTemplateForGlobalSearch(values, "A");
			return t.isTrue();
		});
		
		//Filter by keyword in global search column (using weight B)
		bindings.bind(root.globalSearch.keywords).all((path, values) -> {
			BooleanTemplate t = createBooleanTemplateForGlobalSearch(values, "B");
			return t.isTrue();
		});
								
		//Filter by authors in global search column (using weight C)
		bindings.bind(root.globalSearch.authors).all((path, values) -> {
			BooleanTemplate t = createBooleanTemplateForGlobalSearch(values, "C");
			return t.isTrue();
		});
		
		//Filter by journal title (using weight D)
		bindings.bind(root.globalSearch.journalTitle).all((path, values) -> {
			BooleanTemplate t = createBooleanTemplateForGlobalSearch(values, "D");
			return t.isTrue();
		});
		
		//Global search 
		bindings.bind(root.globalSearch.all).all((path, values) -> {
			BooleanTemplate t = createBooleanTemplateForGlobalSearch(values, "");
			return t.isTrue();
		});
		
		bindings.bind(root.chemicals.any().chemicalCompoundId).all((path, values) -> root.chemicals.any().chemicalCompoundId
				.in(sanitizeStringCollection(values)));
						
		bindings.bind(root.mesh.any().termId).all((path, values) -> root.mesh.any().termId
				.in(sanitizeStringCollection(values)));
		
		bindings.bind(root.journal.nlmUniqueId).all((path, values) -> root.journal.nlmUniqueId
				.in(sanitizeStringCollection(values)));
	}
}	
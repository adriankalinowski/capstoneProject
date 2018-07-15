package edu.wayne.capstone.references.repository;

import java.io.Serializable;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.springframework.data.querydsl.QueryDslPredicateExecutor;
import org.springframework.data.querydsl.binding.QuerydslBinderCustomizer;
import org.springframework.data.querydsl.binding.QuerydslBindings;
import org.springframework.data.repository.NoRepositoryBean;

import com.querydsl.core.types.EntityPath;
import com.querydsl.core.types.dsl.BooleanTemplate;
import com.querydsl.core.types.dsl.DatePath;
import com.querydsl.core.types.dsl.Expressions;
import com.querydsl.core.types.dsl.NumberPath;
import com.querydsl.core.types.dsl.StringPath;

import edu.wayne.capstone.references.data.query.DateValuesForQuerying;
import edu.wayne.capstone.references.data.query.NumericValuesForQuerying;
import edu.wayne.capstone.references.data.query.TextValuesForQuerying;

@NoRepositoryBean
public interface ReadOnlyQueryDslExecuter<T, ID extends Serializable, R extends EntityPath<?>> extends QueryDslPredicateExecutor<T>, 
		QuerydslBinderCustomizer<R>{

	public static final String OR_OP = " | ";
	public static final String PARTIAL_SEARCH_OP = ":*";

	@Override
	default void customize(QuerydslBindings bindings, R root) {
		defaultCustomize(bindings, root);
		repositorySpecificCustomize(bindings, root);
	}
	
	default void defaultCustomize(QuerydslBindings bindings, R root) { 
		bindings.bind(Integer.class).first((NumberPath<Integer> path, Integer value) -> {
			switch (path.getMetadata().getName()) {
			case NumericValuesForQuerying.MIN:
				return path.goe(value);
			case NumericValuesForQuerying.MAX:
				return path.loe(value);
			default:
				return path.eq(value);
			}
		});		
		bindings.bind(String.class).first((StringPath path, String value)-> {
			switch(path.getMetadata().getName()) {
			case TextValuesForQuerying.CONTAINS:
				return path.containsIgnoreCase(value);
			case TextValuesForQuerying.STARTSWITH:
				return path.startsWithIgnoreCase(value);
			case TextValuesForQuerying.ENDSWITH:
				return path.endsWithIgnoreCase(value);
			default:
				return path.equalsIgnoreCase(value);
			}
		});
		bindings.bind(Date.class).first((DatePath<Date> path, Date value)-> {
			switch(path.getMetadata().getName()) {
			case DateValuesForQuerying.FROM:
				return path.goe(value);
			case DateValuesForQuerying.TO:
				return path.loe(value);
			case DateValuesForQuerying.FROMYEAR:
				return path.goe(value);
			case DateValuesForQuerying.TOYEAR:
				return path.loe(value);
			default:
				return path.eq(value);
			}
		});	
	}
	
	default List<String> sanitizeStringCollection(Collection<? extends String> values) {
		return Stream.of(values.stream().collect(Collectors.joining(",")).split(","))
				.filter(Objects::nonNull).filter(s -> !s.isEmpty()).collect(Collectors.toList());
	}
	
	default BooleanTemplate createBooleanTemplateForGlobalSearch(Collection<? extends String> values, String weight) {
		BooleanTemplate t = Expressions.booleanTemplate("global_text_search_operator({0},{1}, {2})",
				Expressions.asString(Stream.of(values.stream().collect(Collectors.joining(",")).split(","))
						.filter(Objects::nonNull).filter(s-> !s.isEmpty()).filter(s-> !s.contains(" ")).map(s -> (s.concat(PARTIAL_SEARCH_OP + weight)))
						.collect(Collectors.joining(OR_OP))),
				Expressions.asString(Stream.of(values.stream().collect(Collectors.joining(",")).split(","))
						.filter(Objects::nonNull).filter(s-> !s.isEmpty()).filter(s-> s.contains(" ")).map(s -> (s.concat(PARTIAL_SEARCH_OP)))
						.collect(Collectors.joining(OR_OP))),
				weight);
		return t;
	}
	
	void repositorySpecificCustomize(QuerydslBindings bindings, R root);
}

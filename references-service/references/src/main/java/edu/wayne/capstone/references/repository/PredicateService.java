package edu.wayne.capstone.references.repository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.querydsl.binding.QuerydslBindingsFactory;
import org.springframework.data.querydsl.binding.QuerydslPredicateBuilder;
import org.springframework.data.util.ClassTypeInformation;
import org.springframework.data.util.TypeInformation;
import org.springframework.stereotype.Service;
import org.springframework.util.MultiValueMap;

import com.querydsl.core.types.Predicate;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class PredicateService {
	
    private final QuerydslPredicateBuilder querydslPredicateBuilder;
    private final QuerydslBindingsFactory querydslBindingsFactory;
 
    public <T> Predicate getPredicateFromParameters(final MultiValueMap<String, String> parameters, Class<T> tClass) {
        TypeInformation<T> typeInformation = ClassTypeInformation.from(tClass);
        return querydslPredicateBuilder.getPredicate(typeInformation, parameters, querydslBindingsFactory.createBindingsFor(null, typeInformation));
    }
}

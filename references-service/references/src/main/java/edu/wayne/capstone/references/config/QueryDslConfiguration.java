package edu.wayne.capstone.references.config;

import org.springframework.beans.factory.ObjectFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.convert.ConversionService;
import org.springframework.data.querydsl.SimpleEntityPathResolver;
import org.springframework.data.querydsl.binding.QuerydslBindingsFactory;
import org.springframework.data.querydsl.binding.QuerydslPredicateBuilder;

@Configuration
public class QueryDslConfiguration{
	
	@Autowired
    @Qualifier("mvcConversionService")
    private ObjectFactory<ConversionService> conversionService;
	
    @Bean
    public QuerydslBindingsFactory querydslBindingsFactory() {
        return new QuerydslBindingsFactory(SimpleEntityPathResolver.INSTANCE);
    }
 
    @Bean
    public QuerydslPredicateBuilder querydslPredicateBuilder() {
        return new QuerydslPredicateBuilder(conversionService.getObject(), querydslBindingsFactory().getEntityPathResolver());
    }
    
}

package edu.wayne.capstone.references.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.data.rest.webmvc.config.RepositoryRestMvcConfiguration;
import org.springframework.format.support.DefaultFormattingConversionService;

@Configuration
public class CustomRepositoryRestMvcConfiguration extends RepositoryRestMvcConfiguration{

	@Override
	public DefaultFormattingConversionService defaultConversionService() {
		DefaultFormattingConversionService defaultConversionService = super.defaultConversionService();
		defaultConversionService.addConverter(new ArticleVersionConverter());
		return defaultConversionService;
	}

}

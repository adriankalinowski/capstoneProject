package edu.wayne.capstone.references.config;

import java.util.Iterator;

import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

import com.google.common.base.Splitter;

@Component
public final class ArticleVersionConverter implements Converter<String, ArticleVersionIdentity> {
	
	@Override
	public ArticleVersionIdentity convert(String source) {
		Iterable<String> split = Splitter.on('.').split(source);
		
		try {
			Iterator<String> itr = split.iterator();
			Integer pubmedId = itr.hasNext() ? Integer.valueOf(itr.next()) : null;
			Integer version = itr.hasNext() ? Integer.valueOf(itr.next()) : null;
			
			if(pubmedId != null && version != null) {
				return new ArticleVersionIdentity(pubmedId, version);
			} else {
				throw new BadArticleVersionException();
			}
		} catch(NumberFormatException ex) {
			throw new BadArticleVersionException();
		}
	}
}
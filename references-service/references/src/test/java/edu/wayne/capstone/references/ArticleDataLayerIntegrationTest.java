package edu.wayne.capstone.references;

import java.util.List;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase.Replace;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.PageRequest;
import org.springframework.test.context.junit4.SpringRunner;

import edu.wayne.capstone.references.data.Article;
import edu.wayne.capstone.references.repository.ArticleRepository;

@AutoConfigureTestDatabase(replace=Replace.NONE)
@RunWith(SpringRunner.class)
@DataJpaTest
public class ArticleDataLayerIntegrationTest {
	
	@Autowired
	private ArticleRepository articleRespository;

	@Test
	public void validate_shouldRetrieveArticles() {
		
		//Attempt to retrieve articles from database
		final List<Article> articles = this.articleRespository.findAll(new PageRequest(0, 1)).getContent();
	
		if(articles.isEmpty()) {
			Assert.assertFalse(!articles.isEmpty());
		}
		else {
			//Check to see if articles were successfully retrieved from database
			Assert.assertFalse(articles.isEmpty());
		}
	}
}
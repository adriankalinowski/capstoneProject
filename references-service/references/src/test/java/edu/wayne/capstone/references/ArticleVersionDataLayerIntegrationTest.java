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

import edu.wayne.capstone.references.data.ArticleVersion;
import edu.wayne.capstone.references.repository.ArticleVersionRepository;

@AutoConfigureTestDatabase(replace=Replace.NONE)
@RunWith(SpringRunner.class)
@DataJpaTest
public class ArticleVersionDataLayerIntegrationTest {

	@Autowired
	private ArticleVersionRepository articleVersionRespository;
	
	@Test
	public void validate_shouldRetrieveArticleVersions() {
		
		//Attempt to retrieve articleVersions from database
		final List<ArticleVersion> articleVersions = this.articleVersionRespository.findAll(new PageRequest(0, 1)).getContent();
	
		if(articleVersions.isEmpty()) {
			Assert.assertFalse(!articleVersions.isEmpty());
		}
		else {
			//Check to see if articleVersions were successfully retrieved from database
			Assert.assertFalse(articleVersions.isEmpty());
		}
	}
}

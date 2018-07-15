package edu.wayne.capstone.references;

import java.util.List;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.Assert;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase.Replace;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.PageRequest;
import org.springframework.test.context.junit4.SpringRunner;

import edu.wayne.capstone.references.data.MeshTerm;
import edu.wayne.capstone.references.repository.MeshRepository;

@AutoConfigureTestDatabase(replace=Replace.NONE)
@RunWith(SpringRunner.class)
@DataJpaTest
public class MeshDataLayerIntegrationTest {

	@Autowired
	private MeshRepository meshRepository;
	
	@Test
	public void validate_shouldRetrieveMeshTerms() {
		// Attempts to retrieve all meshTerms from database 
		final List<MeshTerm> meshTerms = this.meshRepository.findAll(new PageRequest(0, 1)).getContent();
		
		// checks to see if retrieval was successful
		Assert.assertFalse(meshTerms.isEmpty());
	}
}

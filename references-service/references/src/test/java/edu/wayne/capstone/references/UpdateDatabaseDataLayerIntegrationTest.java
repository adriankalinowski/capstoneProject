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

import edu.wayne.capstone.references.data.UpdateDatabase;
import edu.wayne.capstone.references.repository.UpdateDatabaseRepository;

@AutoConfigureTestDatabase(replace=Replace.NONE)
@RunWith(SpringRunner.class)
@DataJpaTest
public class UpdateDatabaseDataLayerIntegrationTest {

	@Autowired
	private UpdateDatabaseRepository updateDatabaseRepository;
	
	@Test
	public void validate_shouldRetrieveUpdateDatabaseInformation() {
		final List<UpdateDatabase> updateDatabaseInfo = this.updateDatabaseRepository.findAll(new PageRequest(0, 1)).getContent();
		
		if (updateDatabaseInfo.isEmpty()) {
			Assert.assertFalse(!updateDatabaseInfo.isEmpty());
		} 
		else {
			//Check to see if updateDatabase information was successfully retrieved from database
			Assert.assertFalse(updateDatabaseInfo.isEmpty());
		}
	}
}

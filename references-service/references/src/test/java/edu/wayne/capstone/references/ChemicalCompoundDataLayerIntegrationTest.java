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

import edu.wayne.capstone.references.data.ChemicalCompound;
import edu.wayne.capstone.references.repository.ChemicalCompoundRepository;

@AutoConfigureTestDatabase(replace=Replace.NONE)
@RunWith(SpringRunner.class)
@DataJpaTest
public class ChemicalCompoundDataLayerIntegrationTest {
	
	@Autowired
	private ChemicalCompoundRepository chemicalCompoundRespository;
	
	@Test
	public void validate_shouldRetrieveChemicalCompounds() {
		
		//Attempt to retrieve chemical compounds from database
		final List<ChemicalCompound> chemicalCompounds = this.chemicalCompoundRespository.findAll(new PageRequest(0, 1)).getContent();
	
		//Check to see if chemical compounds were successfully retrieved from database
		Assert.assertFalse(chemicalCompounds.isEmpty());
	}
}

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

import edu.wayne.capstone.references.data.Journal;
import edu.wayne.capstone.references.repository.JournalRepository;

@AutoConfigureTestDatabase(replace=Replace.NONE)
@RunWith(SpringRunner.class)
@DataJpaTest
public class JournalDataLayerIntegrationTest {

	@Autowired
	private JournalRepository journalRepository;
	
	@Test
	public void validate_shouldRetrieveJournals() {
		// Attempts to retrieve all journals from database 
		final List<Journal> journals = this.journalRepository.findAll(new PageRequest(0, 1)).getContent();
		
		// checks to see if retrieval was successful
		Assert.assertFalse(journals.isEmpty());
	}
}

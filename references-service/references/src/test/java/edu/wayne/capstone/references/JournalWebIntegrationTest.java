package edu.wayne.capstone.references;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.List;

import org.hamcrest.Matchers;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.PageRequest;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;

import edu.wayne.capstone.references.data.Journal;
import edu.wayne.capstone.references.repository.JournalRepository;

@RunWith(SpringRunner.class)
@SpringBootTest
@AutoConfigureMockMvc
public class JournalWebIntegrationTest {

	@Autowired
	private MockMvc mockMvc;
	
	@Autowired
	private JournalRepository journalRepository;
	
    @Test
    public void validate_shouldReturnOKForGETAllJournals() throws Exception {
    	String path ="/journals";
        this.mockMvc.perform(get(path)).andDo(print()).andExpect(status().isOk());
    }
    
    @Test
    public void validate_shouldReturnOKForFindByJournalTitle() throws Exception {
    		List<Journal> journals = this.journalRepository.findAll(new PageRequest(0,5)).getContent();
    		String path = "/journals?journalTitleFilter.value=";
    		
    		if (journals.isEmpty()) {
    			this.mockMvc.perform(get(path)).andDo(print()).andExpect(status().isOk()).andExpect(jsonPath("$.page.totalElements", Matchers.is(0)));
    		}
    		else {
    			this.mockMvc.perform(get(path + journals.get(0).getJournalTitle())).andDo(print()).andExpect(status().isOk()).andExpect(jsonPath("$.page.totalElements", Matchers.greaterThanOrEqualTo(1)));
    		}
    		
    }
    
    @Test
    public void validate_shouldReturnOKForListOfNlmIds() throws Exception {
    		List<Journal> journals = this.journalRepository.findAll(new PageRequest(0,5)).getContent();
    		String path = "/journals?NlmUniqueId=";
    		
    		if (journals.isEmpty()) {
    			this.mockMvc.perform(get(path)).andDo(print()).andExpect(status().isOk()).andExpect(jsonPath("$.page.totalElements", Matchers.is(0)));
    		}
    		else {
    			path = path + journals.get(0).getNlmUniqueId() + "," + journals.get(1).getNlmUniqueId() + "," + journals.get(2).getNlmUniqueId();
    			this.mockMvc.perform(get(path)).andDo(print()).andExpect(status().isOk()).andExpect(jsonPath("$.page.totalElements", Matchers.is(3)));
    		}
    }
    
    @Test
    public void validate_shouldReturnOKForListOfIssnPrints() throws Exception {
    		List<Journal> journals = this.journalRepository.findAll();
    		String path = "/journals?issnPrint=0022-2623,0377-8231,0021-8820,1427-440X";
    		
    		if (journals.isEmpty()) {
    			this.mockMvc.perform(get(path)).andDo(print()).andExpect(status().isOk()).andExpect(jsonPath("$.page.totalElements", Matchers.is(0)));
    		}
    		else {
    			this.mockMvc.perform(get(path)).andDo(print()).andExpect(status().isOk()).andExpect(jsonPath("$.page.totalElements", Matchers.is(4)));
    		}
    }
    
    @Test
    public void validate_shouldReturnMethodNotAllowedForDeleteArticle() throws Exception {
    		List<Journal> journals = this.journalRepository.findAll(new PageRequest(0,5)).getContent();
    		String path = "/journals/" + journals.get(0).getNlmUniqueId();

    		this.mockMvc.perform(delete(path)).andDo(print()).andExpect(status().isMethodNotAllowed());
    }
}

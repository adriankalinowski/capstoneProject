package edu.wayne.capstone.references;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
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
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;

import edu.wayne.capstone.references.data.ChemicalCompound;
import edu.wayne.capstone.references.repository.ChemicalCompoundRepository;


@RunWith(SpringRunner.class)
@SpringBootTest
@AutoConfigureMockMvc
public class ChemicalCompoundWebIntegrationTest {
	
	@Autowired
	private MockMvc mockMvc;
	
	@Autowired
	private ChemicalCompoundRepository chemicalCompoundRepository;
	
    @Test
    public void validate_shouldReturnOKForGETAllChemicalCompounds() throws Exception {
    	String path = "/chemicalCompounds";
        this.mockMvc.perform(get(path)).andDo(print()).andExpect(status().isOk());
    }
    
    @Test
    public void validate_shouldReturnOKForFindByChemicalCompoundTerm() throws Exception {
    	//Retrieve first page of chemicalCompounds (up to 20 per page)
    	List<ChemicalCompound> chemicalCompounds = this.chemicalCompoundRepository.findAll(new PageRequest(0, 20)).getContent();
    	
    	String path = "/chemicalCompounds?nameFilter.value="; 
    	
    	if(chemicalCompounds.isEmpty()) {
    		this.mockMvc.perform(get(path)).andDo(print()).andExpect(status().isOk()).andExpect(jsonPath("$.page.totalElements", Matchers.is(0)));
    	} 
    	else {
    		String chemicalCompound = chemicalCompounds.get(0).getName();
    		path += chemicalCompound;
    		this.mockMvc.perform(get(path)).andDo(print()).andExpect(status().isOk()).andExpect(jsonPath("$.page.totalElements", Matchers.is(1)));
    	}
    }
    
    @Test
    public void validate_shouldReturnOKForFindChemicalCompoundByContains() throws Exception {
    	//Retrieve first page of chemicalCompounds (up to 20 per page)
    	List<ChemicalCompound> chemicalCompounds = this.chemicalCompoundRepository.findAll(new PageRequest(0, 20)).getContent();
    	String path = "/chemicalCompounds?nameFilter.contains=ace"; 
    	
   		if (chemicalCompounds.isEmpty()) {
   			this.mockMvc.perform(get(path)).andDo(print()).andExpect(status().isOk()).andExpect(jsonPath("$.page.totalElements", Matchers.is(0)));
   		}
   		else {
    		this.mockMvc.perform(get(path)).andDo(print()).andExpect(status().isOk()).andExpect(jsonPath("$.page.totalElements", Matchers.greaterThanOrEqualTo(1)));
    	}
    }
    
    @Test
    public void validate_shouldReturnNotFoundError() throws Exception {
    		String path = "/chemsCompounds?nameFilter.contains=testtest"; 
    		this.mockMvc.perform(get(path)).andDo(print()).andExpect(status().isNotFound());
    }
    
    @Test
    public void validate_shouldReturnOKForFindByListOfIdentifiers() throws Exception {
    	//Retrieve first page of chemicalCompounds (up to 20 per page)
    	List<ChemicalCompound> chemicalCompounds = this.chemicalCompoundRepository.findAll(new PageRequest(0, 20)).getContent();
    	
    	String path = "/chemicalCompounds?chemicalCompoundId=";
    	
   		if (chemicalCompounds.isEmpty()) {
   			this.mockMvc.perform(get(path)).andDo(print()).andExpect(status().isOk()).andExpect(jsonPath("$.page.totalElements", Matchers.is(0)));
   		}
   		else {
    		//Get the chemicalCompoundId of three chemical compounds and appends them to the path for testing
    		path += chemicalCompounds.get(0).getChemicalCompoundId() + "," + chemicalCompounds.get(1).getChemicalCompoundId() + ","
    		+ chemicalCompounds.get(2).getChemicalCompoundId();
    		
    		this.mockMvc.perform(get(path)).andDo(print()).andExpect(status().isOk()).andExpect(jsonPath("$.page.totalElements", Matchers.is(3)));
    	}
    }
    
    @Test
    public void validate_shouldReturnMethodNotAllowedForDeleteArticle() throws Exception {
    	//Retrieve first page of chemicalCompounds (up to 20 per page)
    	List<ChemicalCompound> chemicalCompounds = this.chemicalCompoundRepository.findAll(new PageRequest(0, 20)).getContent();
    	
    	String path = "/chemicalCompounds/" + chemicalCompounds.get(0).getChemicalCompoundId();

   		this.mockMvc.perform(delete(path)).andDo(print()).andExpect(status().isMethodNotAllowed());
    }
    
    @Test
    public void validate_shouldReturnMethodNotAllowedForPostChemicalCompounds() throws Exception {
    	this.mockMvc.perform(post("/chemicalCompounds").accept(MediaType.APPLICATION_JSON).contentType(MediaType.APPLICATION_JSON).content("{}")).andDo(print()).andExpect(status().isMethodNotAllowed());
    }
}

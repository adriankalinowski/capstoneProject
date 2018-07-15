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

import edu.wayne.capstone.references.data.MeshTerm;
import edu.wayne.capstone.references.repository.MeshRepository;

@RunWith(SpringRunner.class)
@SpringBootTest
@AutoConfigureMockMvc
public class MeshWebIntegrationTest {

	@Autowired
	private MockMvc mockMvc;
	
	@Autowired
	private MeshRepository meshRepository;
	
    @Test
    public void validate_shouldReturnOKForGETAllMeshterms() throws Exception {
    	String path ="/meshTerms";
        this.mockMvc.perform(get(path)).andDo(print()).andExpect(status().isOk());
    }
    
    @Test
    public void validate_shouldReturnOKForFindByMeshTerm() throws Exception {
    	//Retrieve first page of meshTerms (up to 20 per page)
    	List<MeshTerm> meshTerms = this.meshRepository.findAll(new PageRequest(0, 20)).getContent();
    	String path = "/meshTerms?nameFilter.value="; 
    	
    	if (meshTerms.isEmpty()) {
			this.mockMvc.perform(get(path)).andDo(print()).andExpect(status().isOk()).andExpect(jsonPath("$.page.totalElements", Matchers.is(0)));
		}
		else {
			String meshTerm = meshTerms.get(0).getName();
			path += meshTerm;
			this.mockMvc.perform(get(path)).andDo(print()).andExpect(status().isOk()).andExpect(jsonPath("$.page.totalElements", Matchers.greaterThanOrEqualTo(1)));
		}
    }
    
    @Test
    public void validate_shouldReturnOKForFindMeshTermByContains() throws Exception {
    	//Retrieve first page of meshTerms (up to 20 per page)
		List<MeshTerm> meshTerms = this.meshRepository.findAll(new PageRequest(0, 20)).getContent();
		String path = "/meshTerms?nameFilter.contains=an"; 
		
		if (meshTerms.isEmpty()) {
			this.mockMvc.perform(get(path)).andDo(print()).andExpect(status().isOk()).andExpect(jsonPath("$.page.totalElements", Matchers.is(0)));
		}
		else {
			this.mockMvc.perform(get(path)).andDo(print()).andExpect(status().isOk()).andExpect(jsonPath("$.page.totalElements", Matchers.greaterThanOrEqualTo(1)));
		}
}
    
    @Test
    public void validate_shouldReturnNotFoundError() throws Exception {
    	String path = "/meshTermss?nameFilter.contains=test"; 
    	this.mockMvc.perform(get(path)).andDo(print()).andExpect(status().isNotFound());
    }
    
    @Test
    public void validate_shouldReturnOKForFindByListOfIdentifiers() throws Exception {
    	//Retrieve first page of meshTerms (up to 20 per page)
    	List<MeshTerm> meshTerms = this.meshRepository.findAll(new PageRequest(0, 20)).getContent();
    	String path = "/meshTerms?termId=";
    	
   		if (meshTerms.isEmpty()) {
   			this.mockMvc.perform(get(path)).andDo(print()).andExpect(status().isOk()).andExpect(jsonPath("$.page.totalElements", Matchers.is(0)));
   		}
   		else {  
   			//Get the meshTermId of three mesh terms and appends them to the path for testing
    		path += meshTerms.get(0).getTermId() + "," + meshTerms.get(1).getTermId() + "," + meshTerms.get(2).getTermId();    	
    		this.mockMvc.perform(get(path)).andDo(print()).andExpect(status().isOk()).andExpect(jsonPath("$.page.totalElements", Matchers.is(3)));
   		}
    }
    
    @Test
    public void validate_shouldReturnMethodNotAllowedForDeleteArticle() throws Exception {
    	//Retrieve first page of meshTerms (up to 20 per page)
    	List<MeshTerm> meshTerms = this.meshRepository.findAll(new PageRequest(0, 20)).getContent();
    	
    	String path = "/meshTerms/";
    	if(meshTerms.isEmpty()) {
    		path += "test";
	    	this.mockMvc.perform(delete(path)).andDo(print()).andExpect(status().isMethodNotAllowed());
    	}
    	else {
    		//Get the meshTermId of first meshTerm in list, append it to path
    		path += meshTerms.get(0).getTermId();
    		this.mockMvc.perform(delete(path)).andDo(print()).andExpect(status().isMethodNotAllowed());
    	}

   		this.mockMvc.perform(delete(path)).andDo(print()).andExpect(status().isMethodNotAllowed());
    }
    
    @Test
    public void validate_shouldReturnMethodNotAllowedForPostMeshTerm() throws Exception {
   		this.mockMvc.perform(post("/meshTerms").accept(MediaType.APPLICATION_JSON).contentType(MediaType.APPLICATION_JSON).content("{}")).andDo(print()).andExpect(status().isMethodNotAllowed());
    }
}

package edu.wayne.capstone.references;

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

import edu.wayne.capstone.references.data.UpdateDatabase;
import edu.wayne.capstone.references.repository.UpdateDatabaseRepository;

@RunWith(SpringRunner.class)
@SpringBootTest
@AutoConfigureMockMvc
public class UpdateDatabaseWebIntegrationTest {

	@Autowired
    private MockMvc mockMvc;

    @Autowired
    private UpdateDatabaseRepository updateDatabaseRepository;
    
    @Test
    public void validate_shouldReturnOKForFindByStatus() throws Exception {
    	List<UpdateDatabase> updateDatabaseInfo = this.updateDatabaseRepository.findAll(new PageRequest(0,20)).getContent();
    	
    	String path = "/updateDatabases?status=ERROR";
    	
    	if (updateDatabaseInfo.isEmpty()) {
    		this.mockMvc.perform(get(path)).andDo(print()).andExpect(status().isOk()).andExpect(jsonPath("$.page.totalElements", Matchers.is(0)));
    	} 
    	else {
    		this.mockMvc.perform(get(path)).andDo(print()).andExpect(status().isOk()).andExpect(jsonPath("$.page.totalElements", Matchers.greaterThanOrEqualTo(1)));
    	}
    }
    
    @Test
    public void validate_shouldReturnBadRequestForFindByStatus() throws Exception {    	
    	
    	String path = "/updateDatabases?status=TEST";
    	this.mockMvc.perform(get(path)).andDo(print()).andExpect(status().isBadRequest());
    }
}

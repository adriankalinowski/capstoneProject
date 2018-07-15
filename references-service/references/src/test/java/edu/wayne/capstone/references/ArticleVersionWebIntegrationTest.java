package edu.wayne.capstone.references;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.List;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;

import edu.wayne.capstone.references.data.ArticleVersion;
import edu.wayne.capstone.references.repository.ArticleVersionRepository;

@RunWith(SpringRunner.class)
@SpringBootTest
@AutoConfigureMockMvc
public class ArticleVersionWebIntegrationTest {

	@Autowired
    private MockMvc mockMvc;

    @Autowired
    private ArticleVersionRepository articleVersionRepository;
	
    @Test
    public void validate_shouldReturnOKForGETAllArticles() throws Exception {
        this.mockMvc.perform(get("/articleVersions")).andDo(print()).andExpect(status().isOk());
    }
    
    @Test
    public void validate_shouldReturnMethodNotAllowedForPostArticle() throws Exception {
    		this.mockMvc.perform(post("/articleVersions").accept(MediaType.APPLICATION_JSON).contentType(MediaType.APPLICATION_JSON).content("{}")).andDo(print()).andExpect(status().isMethodNotAllowed());
    }
    
    @Test
    public void validate_shouldReturnOKForLinkArticleVersionAssociation() throws Exception {
	 
	    List<ArticleVersion> articleVersions = this.articleVersionRepository.findAll(new PageRequest(0, 20)).getContent();
	    String path = "/articleVersions/" + articleVersions.get(0).getId().toString() + "/article";
	    
	    if(articleVersions.isEmpty()) {
	    		this.mockMvc.perform(get(path)).andDo(print()).andExpect(status().isOk());
	    }
	   	else {
	   		this.mockMvc.perform(get(path)).andDo(print()).andExpect(status().isOk());
	    }
    }
}

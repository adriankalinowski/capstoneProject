package edu.wayne.capstone.references;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status; 
import java.util.Date;
import java.util.List;
import org.hamcrest.Matchers;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Slice;
import org.springframework.data.domain.Sort;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import edu.wayne.capstone.references.data.Article;
import edu.wayne.capstone.references.data.ChemicalCompound;
import edu.wayne.capstone.references.data.MeshTerm;
import edu.wayne.capstone.references.repository.ArticleRepository;
import edu.wayne.capstone.references.repository.ChemicalCompoundRepository;
import edu.wayne.capstone.references.repository.MeshRepository;

//DO NOT ADD BACK: use command line argument if needed: mvn clean package -Dspring.profiles.active=it
//@ActiveProfiles("it")
@RunWith(SpringRunner.class)
@SpringBootTest
@AutoConfigureMockMvc
public class ArticleWebIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ArticleRepository articleRepository;
    
    @Autowired
    private ChemicalCompoundRepository chemicalRepository;

    @Autowired
	private MeshRepository meshRepository;
    
    @Test
    public void validate_shouldReturnOKForGETAllArticles() throws Exception {
        this.mockMvc.perform(get("/articles")).andDo(print()).andExpect(status().isOk());
    }
    
    @Test
    public void validate_shouldReturnOKForFindByNonExistingTitle() throws Exception {
    	
    	String path = "/articles?titleFilter.value=asdfghjklljgh"; 
    	this.mockMvc.perform(get(path)).andDo(print()).andExpect(status().isOk()).andExpect(jsonPath("$._embedded").exists());
    }
    
    @Test
    public void validate_shouldReturnOKForFindByPartialTitle() throws Exception {
    	//Retrieve first page of articles (5 articles per page)
	    List<Article> articles = this.articleRepository.findAll(new PageRequest(0, 5)).getContent();
	    String path = "/articles?globalSearch.searchParameters.articleTitle=Hospital"; 
	    
	    if(articles.isEmpty()) {
	    	this.mockMvc.perform(get(path)).andDo(print()).andExpect(status().isOk()).andExpect(jsonPath("$.page.totalElements", Matchers.is(0)));
	    } 
	    else {
	    	this.mockMvc.perform(get(path)).andDo(print()).andExpect(status().isOk()).andExpect(jsonPath("$.page.totalElements", Matchers.greaterThanOrEqualTo(1)));
	    }
    }
    
    @Test
    public void validate_shouldReturnOKForFindByListOfIdentifiers() throws Exception {
	    //Retrieve first page of articles (up to 20 total articles)
	    List<Article> articles = this.articleRepository.findAll(new PageRequest(0, 20)).getContent();
	    
	    if(articles.isEmpty()) {
	    	this.mockMvc.perform(get("/articles?pubmedId=")).andDo(print()).andExpect(status().isOk()).andExpect(jsonPath("$.page.totalElements", Matchers.is(0)));
	    }
	   	else {
	   		if (articles.size() > 2) {
	   			//Get the pubmedId of three articles, append them to /articles to create URI path
	   			String path = "/articles?pubmedId=" + articles.get(0).getPubmedId() + "," + articles.get(1).getPubmedId() + ","
    						+ articles.get(2).getPubmedId();
	    			
	   			this.mockMvc.perform(get(path)).andDo(print()).andExpect(status().isOk()).andExpect(jsonPath("$.page.totalElements", Matchers.is(3)));
	    	}
	    }
    }
    
    @Test
    public void validate_shouldReturnOKForFindByRangeOfDates() throws Exception {
    	//Retrieve first page of articles (10 per page) and sort them by publicationDate in ascending order
    	List<Article> articles = this.articleRepository.findAll(new PageRequest(0, 10, new Sort(Sort.Direction.ASC, "publicationDate"))).getContent();
    	
    	if(articles.isEmpty()) {
    		this.mockMvc.perform(get("/articles?publicationDateFilter.from=&publicationDateFilter.to=")).andDo(print())
    		.andExpect(status().isOk()).andExpect(jsonPath("$.page.totalElements", Matchers.is(0)));
    	} 
    	else {
    		//Get earliest date of publication
        	Date dateFrom = articles.get(0).getPublicationDate();
        	
        	//Get most recent date of publication; could be null
        	Date dateTo = articles.get(articles.size() - 1).getPublicationDate();
        	
        	if (dateTo == null) {
        		//Find first instance of article where date is not null
        		for(int i = articles.size() - 1; i > 0; i--) {
            		if(articles.get(i).getPublicationDate() != null) {
            			dateTo = articles.get(i).getPublicationDate();
            			break;
            		}
            	}
        	} 
        	
        	String path = "/articles?publicationDateFilter.from=" + dateFrom + "&publicationDateFilter.to=" + dateTo;
        	
        	this.mockMvc.perform(get(path)).andDo(print()).andExpect(status().isOk()).andExpect(jsonPath("$.page.totalElements", Matchers.greaterThanOrEqualTo(1)));
    	}
    }
   
    @Test
    public void validate_shouldReturnMethodNotAllowedForDeleteArticle() throws Exception {
    	//Retrieve an article to delete
	    List<Article> articles = this.articleRepository.findAll(new PageRequest(0, 1)).getContent();
	    
	    String path = "/articles/";
	    if(articles.isEmpty()) {
	    	path += "test";
	    	this.mockMvc.perform(delete(path)).andDo(print()).andExpect(status().isMethodNotAllowed());
	    }
	    else {
		    //Get the pubmedId of first article in list, append it to /articles to create URI path
		    path += articles.get(0).getPubmedId();
		    this.mockMvc.perform(delete(path)).andDo(print()).andExpect(status().isMethodNotAllowed());
	    }
    }
    
    @Test
    public void validate_shouldReturnMethodNotAllowedForPostArticle() throws Exception {
    	this.mockMvc.perform(post("/articles").accept(MediaType.APPLICATION_JSON).contentType(MediaType.APPLICATION_JSON).content("{}")).andDo(print()).andExpect(status().isMethodNotAllowed());
    }
    
    @Test
    public void validate_shouldReturnOKForFindByAuthorLastName() throws Exception {
    	List<Article> articles = this.articleRepository.findAll(new PageRequest(0, 1)).getContent();
		String path = "/articles?globalSearch.authors=Hunt";
		
    	if(articles.isEmpty()) {
    		this.mockMvc.perform(get(path)).andDo(print()).andExpect(status().isOk()).andExpect(jsonPath("$.page.totalElements", Matchers.is(0)));
    	}
    	else {
    		this.mockMvc.perform(get(path)).andDo(print()).andExpect(status().isOk()).andExpect(jsonPath("$.page.totalElements", Matchers.greaterThanOrEqualTo(1)));
    	}
    }
    
    @Test
    public void validate_shouldReturnOkForFindByEmptyAuthorLastName() throws Exception {
    	List<Article> articles = this.articleRepository.findAll(new PageRequest(0, 1)).getContent();
	    String path = "/articles?globalSearch.authors=";
	    
 	    if(articles.isEmpty()) {
 	    	this.mockMvc.perform(get(path)).andDo(print()).andExpect(status().isOk()).andExpect(jsonPath("$.page.totalElements", Matchers.is(0)));
 	    } 
 	    else {
 	    	this.mockMvc.perform(get(path)).andDo(print()).andExpect(status().isOk()).andExpect(jsonPath("$.page.totalElements", Matchers.greaterThanOrEqualTo(1)));
 	    }
    }
    
    @Test
    public void validate_shouldReturnOKForFindByListOfPartialAuthorLastNames() throws Exception {
    	List<Article> articles = this.articleRepository.findAll(new PageRequest(0, 1)).getContent();
    	
    	String path = "/articles?globalSearch.authors=Hu,Dugg&globalSearch.authors=Smi";
    	
    	if(articles.isEmpty()) {
    		this.mockMvc.perform(get(path)).andDo(print()).andExpect(status().isOk()).andExpect(jsonPath("$.page.totalElements", Matchers.is(0)));
    	}
    	else {
    		this.mockMvc.perform(get(path)).andDo(print()).andExpect(status().isOk()).andExpect(jsonPath("$.page.totalElements", Matchers.greaterThanOrEqualTo(1)));
    	}
    }
    
    @Test
    public void validate_shouldReturnOKForFindByBadFormatAuthorLastName() throws Exception {
    	List<Article> articles = this.articleRepository.findAll(new PageRequest(0, 1)).getContent(); 
    	String path = "/articles?globalSearch.authors=,Hunt";
    	
    	if(articles.isEmpty()) {
    		this.mockMvc.perform(get(path)).andDo(print()).andExpect(status().isOk()).andExpect(jsonPath("$.page.totalElements", Matchers.is(0)));
    	}
    	else {
    		this.mockMvc.perform(get(path)).andDo(print()).andExpect(status().isOk()).andExpect(jsonPath("$.page.totalElements", Matchers.greaterThanOrEqualTo(1)));
    	}
    }
    
    @Test
    public void validate_shouldReturnOKForFindByBadFormatListOfAuthorLastNames() throws Exception {
    	List<Article> articles = this.articleRepository.findAll(new PageRequest(0, 1)).getContent();  
    	
    	String path = "/articles?globalSearch.authors=Hunt&globalSearch.authors=,";
    	
    	if(articles.isEmpty()) {
    		this.mockMvc.perform(get(path)).andDo(print()).andExpect(status().isOk()).andExpect(jsonPath("$.page.totalElements", Matchers.is(0)));
    	}
    	else {
    		this.mockMvc.perform(get(path)).andDo(print()).andExpect(status().isOk()).andExpect(jsonPath("$.page.totalElements", Matchers.greaterThanOrEqualTo(1)));
    	}
    }
    
    @Test
    public void validate_shouldReturnOKForFindByPartialJournalTitle() throws Exception {
    	List<Article> articles = this.articleRepository.findAll(new PageRequest(0, 1)).getContent(); 
    	
    	String path = "/articles?globalSearch.journalTitle=journal,chemistry";
    	
    	if(articles.isEmpty()) {
    		this.mockMvc.perform(get(path)).andDo(print()).andExpect(status().isOk()).andExpect(jsonPath("$.page.totalElements", Matchers.is(0)));
    	}
    	else {
    		this.mockMvc.perform(get(path)).andDo(print()).andExpect(status().isOk()).andExpect(jsonPath("$.page.totalElements", Matchers.greaterThanOrEqualTo(1)));
    	}
    }
    
    @Test
    public void validate_shouldReturnOKForFindByGlobalSearch() throws Exception {
    	List<Article> articles = this.articleRepository.findAll(new PageRequest(0, 1)).getContent(); 
    	
    	String path = "/articles?globalSearch.all=journal,of,medicinal";
    	
    	if(articles.isEmpty()) {
    		this.mockMvc.perform(get(path)).andDo(print()).andExpect(status().isOk()).andExpect(jsonPath("$.page.totalElements", Matchers.is(0)));
    	}
    	else {
    		this.mockMvc.perform(get(path)).andDo(print()).andExpect(status().isOk()).andExpect(jsonPath("$.page.totalElements", Matchers.greaterThanOrEqualTo(1)));
    	}
    }
    
    @Test 
    public void validate_shouldReturnOKForFindByBadFormatGlobalSearch() throws Exception {
    	List<Article> articles = this.articleRepository.findAll(new PageRequest(0, 1)).getContent();  
    	
    	String path = "/articles?globalSearch.all=journal&globalSearch.all=,";
    	
    	if(articles.isEmpty()) {
    		this.mockMvc.perform(get(path)).andDo(print()).andExpect(status().isOk()).andExpect(jsonPath("$.page.totalElements", Matchers.is(0)));
    	}
    	else {
    		this.mockMvc.perform(get(path)).andDo(print()).andExpect(status().isOk()).andExpect(jsonPath("$.page.totalElements", Matchers.greaterThanOrEqualTo(1)));
    	}
    }
    
    @Test 
    public void validate_shouldReturnOKForFindArticleByChemicalCompoundId() throws Exception { 
    	List<ChemicalCompound> chemicals = this.chemicalRepository.findAll(new PageRequest(0, 10)).getContent();
        String path = "/articles?chemicals.chemicalCompoundId=D000924";
        
        if(chemicals.isEmpty()) {
            this.mockMvc.perform(get(path)).andDo(print()).andExpect(status().isOk()).andExpect(jsonPath("$.page.totalElements", Matchers.is(0)));
        }
        else {
        		this.mockMvc.perform(get(path)).andDo(print()).andExpect(status().isOk()).andExpect(jsonPath("$.page.totalElements", Matchers.greaterThanOrEqualTo(1)));
        }
    }
    
    
    @Test 
    public void validate_shouldReturnOKForFindArticleByListOfChemicalCompoundIds() throws Exception {
		List<ChemicalCompound> chemicals = this.chemicalRepository.findAll(new PageRequest(0, 5)).getContent();
        String path = "/articles?chemicals.chemicalCompoundId=D003997,D011162,D000924,D011433,D010634";
        
        if(chemicals.isEmpty()) {
            this.mockMvc.perform(get(path)).andDo(print()).andExpect(status().isOk()).andExpect(jsonPath("$.page.totalElements", Matchers.is(0)));
        }
        else {
        		this.mockMvc.perform(get(path)).andDo(print()).andExpect(status().isOk()).andExpect(jsonPath("$.page.totalElements", Matchers.greaterThanOrEqualTo(1)));
        }
    }
    
    @Test 
    public void validate_shouldReturnOKForFindArticleByMeshTermId() throws Exception {
		List<MeshTerm> mesh = this.meshRepository.findAll(new PageRequest(0, 10)).getContent();
        String path = "/articles?mesh.termId=D001530";
        
        if(mesh.isEmpty()) {
            this.mockMvc.perform(get(path)).andDo(print()).andExpect(status().isOk()).andExpect(jsonPath("$.page.totalElements", Matchers.is(0)));
        }
        else {
        		this.mockMvc.perform(get(path)).andDo(print()).andExpect(status().isOk()).andExpect(jsonPath("$.page.totalElements", Matchers.greaterThanOrEqualTo(1)));
        }
    }
    
    @Test 
    public void validate_shouldReturnOKForFindArticleByListOfMeshTermIds() throws Exception {
		List<MeshTerm> mesh = this.meshRepository.findAll(new PageRequest(0, 5)).getContent();
        String path = "/articles?mesh.termId=D008239,D051379,D001530,D000587,D014407";
        
        if(mesh.isEmpty()) {
            this.mockMvc.perform(get(path)).andDo(print()).andExpect(status().isOk()).andExpect(jsonPath("$.page.totalElements", Matchers.is(0)));
        }
        else {
        	this.mockMvc.perform(get(path)).andDo(print()).andExpect(status().isOk()).andExpect(jsonPath("$.page.totalElements", Matchers.greaterThanOrEqualTo(1)));
        }
    }
    
    @Test 
    public void validate_shouldReturnOKForFindArticleByNlmUniqueId() throws Exception {
    	List<Article> articles = this.articleRepository.findAll(new PageRequest(0, 5)).getContent();
        String path = "/articles?journal.nlmUniqueId=7506854";
        
        if(articles.isEmpty()) {
            this.mockMvc.perform(get(path)).andDo(print()).andExpect(status().isOk()).andExpect(jsonPath("$.page.totalElements", Matchers.is(0)));
        }
        else {
        	this.mockMvc.perform(get(path)).andDo(print()).andExpect(status().isOk()).andExpect(jsonPath("$.page.totalElements", Matchers.greaterThanOrEqualTo(1)));
        }
    }
    
    @Test 
    public void validate_shouldReturnOKForFindArticleByListOfNlmUniqueIds() throws Exception {
    	List<Article> articles = this.articleRepository.findAll(new PageRequest(0, 5)).getContent();
        String path = "/articles?journal.nlmUniqueId=7506854,0413775,0330420,2985121R,8303205";
        
        if(articles.isEmpty()) {
            this.mockMvc.perform(get(path)).andDo(print()).andExpect(status().isOk()).andExpect(jsonPath("$.page.totalElements", Matchers.is(0)));
        }
        else {
        		this.mockMvc.perform(get(path)).andDo(print()).andExpect(status().isOk()).andExpect(jsonPath("$.page.totalElements", Matchers.greaterThanOrEqualTo(1)));
        }
    }
	
	@Test 
    public void validate_shouldReturnOKForFindArticlePhraseSearch() throws Exception {
    	List<Article> articles = this.articleRepository.findAll(new PageRequest(0, 5)).getContent();
        String path = "/articles?globalSearch.journalTitle=journal of medicine&size=1";
        
        if(articles.isEmpty()) {
            this.mockMvc.perform(get(path)).andDo(print()).andExpect(status().isOk()).andExpect(jsonPath("$.page.totalElements", Matchers.is(0)));
        }
        else {
        	this.mockMvc.perform(get(path)).andDo(print()).andExpect(status().isOk()).andExpect(jsonPath("$.page.totalElements", Matchers.greaterThanOrEqualTo(1)));
        }
    }
    
    @Test 
    public void validate_shouldReturnOKForFindArticleCombinationPhraseSearch() throws Exception {
    	List<Article> articles = this.articleRepository.findAll(new PageRequest(0, 5)).getContent();
        String path = "/articles?globalSearch.all=kidney,journal of medicine&size=1";
        
        if(articles.isEmpty()) {
            this.mockMvc.perform(get(path)).andDo(print()).andExpect(status().isOk()).andExpect(jsonPath("$.page.totalElements", Matchers.is(0)));
        }
        else {
        	this.mockMvc.perform(get(path)).andDo(print()).andExpect(status().isOk()).andExpect(jsonPath("$.page.totalElements", Matchers.greaterThanOrEqualTo(1)));
        }
    }
    
    @Test 
    public void validate_shouldReturnOKForFindArticleEmptyPhraseSearch() throws Exception {
    	List<Article> articles = this.articleRepository.findAll(new PageRequest(0, 5)).getContent();
        String path = "/articles?globalSearch.all=the,of and&size=1";
        
        if(articles.isEmpty()) {
            this.mockMvc.perform(get(path)).andDo(print()).andExpect(status().isOk()).andExpect(jsonPath("$.page.totalElements", Matchers.is(0)));
        }
        else {
        	this.mockMvc.perform(get(path)).andDo(print()).andExpect(status().isOk()).andExpect(jsonPath("$.page.totalElements", Matchers.greaterThanOrEqualTo(1)));
		}
	}
    
    @Test
    public void validate_shouldReturnNumberFormatExceptionInPubmedIdList() throws Exception {
    		this.mockMvc.perform(get("/articles?pubmedId=123,53214,52*321")).andDo(print()).andExpect(status().is4xxClientError());
    }
}
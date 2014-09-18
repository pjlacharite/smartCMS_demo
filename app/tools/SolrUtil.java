package tools;

import java.io.IOException;
import java.util.List;

import models.Category;

import org.apache.solr.client.solrj.SolrQuery;
import org.apache.solr.client.solrj.SolrQuery.ORDER;
import org.apache.solr.client.solrj.SolrServer;
import org.apache.solr.client.solrj.SolrServerException;
import org.apache.solr.client.solrj.impl.HttpSolrServer;
import org.apache.solr.client.solrj.response.FacetField;
import org.apache.solr.client.solrj.response.FacetField.Count;
import org.apache.solr.client.solrj.response.QueryResponse;
import org.apache.solr.common.SolrInputDocument;

public class SolrUtil {
    private static final String SOLR_URL = "http://localhost:8085/solr-dev";

    private static SolrServer solr = new HttpSolrServer(SOLR_URL);

    public static SolrInputDocument createDocument(String contentId, List<Category> categories, String username){
		SolrInputDocument inputDocument;
		inputDocument = new SolrInputDocument();
		inputDocument.addField("id", contentId);		
		for (Category category: categories){
			inputDocument.addField("cat_ss", category.name);
		}
		inputDocument.addField("username_ss", username);
		inputDocument.addField("userCount_i", 1);
		return inputDocument;
    }
    
    /**
     * Returns the 3 top facets categories of a user
     * @param username
     * @return
     */
    public static QueryResponse getCategoriesQuery(String username){
    	System.out.println("Querying username: " + username);
    	SolrQuery query = new SolrQuery();
    	query.setQuery("*:*");
    	query.setFields("id", "cat_ss");
    	query.addFilterQuery("username_ss:"+username);
    	query.addFacetField("cat_ss");
    	query.setFacetLimit(3);
    	query.set("rows", 1000);
    	QueryResponse response;
		try {
			response = solr.query(query);
			return response;
		} catch (SolrServerException e) {
			e.printStackTrace();
		}
		return null; 
    }

    /**
     * Returns the 10 articles that match the user's top categories and were viewed by other users.
     * @param username
     * @param categories
     * @return
     */
    public static QueryResponse getRecommendedArticleByCategories(String username, List<FacetField> categories){
    	SolrQuery query = new SolrQuery();
    	query.setQuery("*:* NOT username_ss:" + username);
    	query.setFields("id");
    	query.addFilterQuery("-username_ss:"+ username);
    	List<Count> counts = categories.get(0).getValues();
    	String categoriesFilter = "(" + counts.get(0).getName() + " OR " + counts.get(1).getName() + " OR " + counts.get(2).getName() + ")";
    	query.addFilterQuery("cat_ss:" + categoriesFilter);
    	query.addFacetField("id");
    	query.setFacetLimit(10);
    	query.set("rows", 10);
    	QueryResponse response;
		try {
			response = solr.query(query);
			return response;
		} catch (SolrServerException e) {
			e.printStackTrace();
		}
		return null;
    }
    
    /**
     * Returns the 3 articles that were viewed by the most users INCLUDING those that are the closest match to the initial user.
     * @param username
     * @param users
     * @return
     */
    public static QueryResponse getRecommendedArticleByUsers(String username, List<FacetField> users){
    	SolrQuery query = new SolrQuery();
    	query.setQuery("*:* NOT username_ss:" + username);
    	query.setFields("id, username_ss, cat_ss, userCount_i");
    	query.addFilterQuery("-username_ss:"+ username);
    	List<Count> counts = users.get(0).getValues();
    	String categoriesFilter = "(" + counts.get(0).getName() + " OR " + counts.get(1).getName() + " OR " + counts.get(2).getName() + ")";
    	query.addFilterQuery("username_ss:" + categoriesFilter);
    	query.addSort("userCount_i", ORDER.desc);
    	query.set("rows", 3);
    	QueryResponse response;
		try {
			response = solr.query(query);
			return response;
		} catch (SolrServerException e) {
			e.printStackTrace();
		}
		return null;
    }
    
    /**
     * Returns the 3 facets users that have the closest match to the initial user.
     * @param username
     * @param categories
     * @return
     */
    public static QueryResponse getMatchingUsers(String username, List<FacetField> categories){
    	SolrQuery query = new SolrQuery();
    	query.setQuery("*:* NOT username_ss:" + username);
    	query.setFields("username_ss");
    	query.addFilterQuery("-username_ss:"+ username);
    	List<Count> counts = categories.get(0).getValues();
    	String categoriesFilter = "(" + counts.get(0).getName() + " OR " + counts.get(1).getName() + " OR " + counts.get(2).getName() + ")";
    	query.addFilterQuery("cat_ss:" + categoriesFilter);
    	query.addFacetField("username_ss");
    	query.setFacetLimit(3);
    	QueryResponse response;
		try {
			response = solr.query(query);
			return response;
		} catch (SolrServerException e) {
			e.printStackTrace();
		}
		return null;
    }
    
    public static void clearIndex(){
    	try {
			solr.deleteByQuery("*:*");
		} catch (SolrServerException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
    }
    
    public static void index(List<SolrInputDocument> documents){
    	for (SolrInputDocument document: documents){
    		try {
				solr.add(document);
				solr.commit();
			} catch (SolrServerException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
    	}
    }
}
package tools;

import java.io.IOException;
import java.util.List;

import models.Category;

import org.apache.solr.client.solrj.SolrQuery;
import org.apache.solr.client.solrj.SolrServer;
import org.apache.solr.client.solrj.SolrServerException;
import org.apache.solr.client.solrj.impl.HttpSolrServer;
import org.apache.solr.client.solrj.response.FacetField;
import org.apache.solr.client.solrj.response.FacetField.Count;
import org.apache.solr.client.solrj.response.QueryResponse;
import org.apache.solr.client.solrj.response.UpdateResponse;
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
		return inputDocument;
    }
    
    public static QueryResponse getCategoriesQuery(String username){
    	System.out.println("Querying username: " + username);
    	SolrQuery query = new SolrQuery();
    	query.setQuery("*:*");
    	query.setFields("id", "cat_ss", "username_ss");
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

    
    public static QueryResponse getRecommendedArticle(String username, List<FacetField> categories){
    	SolrQuery query = new SolrQuery();
    	query.setQuery("*:* NOT username_ss:" + username);
    	query.setFields("id");
    	query.addFilterQuery("-username_ss:"+ username);
    	List<Count> counts = categories.get(0).getValues();
    	String categoriesFilter = "(" + counts.get(0).getName() + " OR " + counts.get(1).getName() + " OR " + counts.get(2).getName() + ")";
    	query.addFilterQuery("cat_ss:" + categoriesFilter);
    	query.addFacetField("id");
    	query.setFacetLimit(10);
    	QueryResponse response;
		try {
			response = solr.query(query);
			return response;
		} catch (SolrServerException e) {
			e.printStackTrace();
		}
		return null;
    }
    
    public static QueryResponse getMatchingUsers(String username, List<FacetField> categories){
    	SolrQuery query = new SolrQuery();
    	query.setQuery("*:* NOT username_ss:" + username);
    	query.setFields("username_ss");
    	query.addFilterQuery("-username_ss:"+ username);
    	List<Count> counts = categories.get(0).getValues();
    	String categoriesFilter = "(" + counts.get(0).getName() + " OR " + counts.get(1).getName() + " OR " + counts.get(2).getName() + ")";
    	query.addFilterQuery("cat_ss:" + categoriesFilter);
    	query.addFacetField("username_ss");
    	query.setFacetLimit(10);
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
				UpdateResponse response = solr.add(document);
				System.out.println(response);
				solr.commit();
			} catch (SolrServerException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
    	}
    }
}
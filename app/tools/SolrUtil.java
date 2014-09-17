package tools;

import java.io.IOException;
import java.util.UUID;

import org.apache.solr.client.solrj.SolrQuery;
import org.apache.solr.client.solrj.SolrServer;
import org.apache.solr.client.solrj.SolrServerException;
import org.apache.solr.client.solrj.impl.HttpSolrServer;
import org.apache.solr.client.solrj.response.QueryResponse;
import org.apache.solr.client.solrj.response.UpdateResponse;
import org.apache.solr.common.SolrInputDocument;

public class SolrUtil {
    private static final String SOLR_URL = "http://localhost:8085/solr-dev";

    private static SolrServer solr = new HttpSolrServer(SOLR_URL);

    public static void indexTracking(String contentId, String categoryName, String username){
    	try {
            SolrInputDocument document = new SolrInputDocument();
            String uuid = UUID.randomUUID().toString();
            document.addField("id", uuid);
            document.addField("cid_ss", contentId);
            document.addField("cat_ss", categoryName);
            document.addField("username_ss", username);
            UpdateResponse response = solr.add(document);
            response.getStatus();
            solr.commit();
        } catch (Exception e) {
            System.out.println("index error : " + e.getMessage());
        }
    }
    
    public static QueryResponse getCategoriesQuery(String username){
    	SolrQuery query = new SolrQuery();
    	query.setQuery("*:*");
    	query.setFields("cat_ss");
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
    
    public static QueryResponse getRecommendedArticle(String otherUsername, String cat1, String cat2, String cat3){
    	SolrQuery query = new SolrQuery();
    	query.setQuery("*:*");
    	query.setFields("cid_ss");
    	query.addFilterQuery("username_ss:"+ otherUsername);
    	query.addFacetField("cat_ss");
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

    public static void clearIndex(){
    	try {
			solr.deleteByQuery("*:*");
		} catch (SolrServerException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
    }
    
}

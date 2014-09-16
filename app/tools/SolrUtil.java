package tools;

import java.util.UUID;

import org.apache.solr.client.solrj.SolrQuery;
import org.apache.solr.client.solrj.SolrServer;
import org.apache.solr.client.solrj.SolrServerException;
import org.apache.solr.client.solrj.impl.HttpSolrServer;
import org.apache.solr.client.solrj.response.QueryResponse;
import org.apache.solr.client.solrj.response.UpdateResponse;
import org.apache.solr.common.SolrDocumentList;
import org.apache.solr.common.SolrInputDocument;

public class SolrUtil {
    private static final String SOLR_URL = "http://localhost:8085/solr-dev";

    private static SolrServer solr = new HttpSolrServer(SOLR_URL);

    public static void index(String key, String value) {

        try {
            SolrInputDocument document = new SolrInputDocument();
            String uuid = UUID.randomUUID().toString();
            document.addField("id", uuid);
            document.addField(key + "_ss", value);
            UpdateResponse response = solr.add(document);
            solr.commit();

        } catch (Exception e) {
            System.out.println("index error : " + e.getMessage());
        }
    }

    public static int getHits(String key, String value) {
        SolrQuery parameters = new SolrQuery();
        parameters.set("q", "+" + key + "_ss:" + value);

        parameters.set("rows", 10);
        parameters.set("start", 0);

        int ret = 0;
        try {
            int pageHits = 1;
            int nbPage = 0;
            while (pageHits > 0) {
                QueryResponse response = solr.query(parameters);
                SolrDocumentList list = response.getResults();
                pageHits = list.size();

                ret += pageHits;

                nbPage += 1;
                parameters.set("start", nbPage * 10);
            }
        } catch (SolrServerException e) {
            System.out.println("search error : " + e.getMessage());
        }

        return ret;

    }
}

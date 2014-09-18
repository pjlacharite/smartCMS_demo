package controllers;

import java.util.ArrayList;
import java.util.List;

import models.PolopolyUser;
import models.StandardArticle;

import org.apache.solr.client.solrj.response.FacetField;
import org.apache.solr.client.solrj.response.FacetField.Count;
import org.apache.solr.client.solrj.response.QueryResponse;
import org.apache.solr.common.SolrDocument;

import play.data.Form;
import play.mvc.Controller;
import play.mvc.Result;
import play.mvc.Security;
import tools.DemoData;
import tools.SolrUtil;
import views.html.login;

public class Application extends Controller {

	@Security.Authenticated(Secured.class)
    public static Result index() {
        PolopolyUser user = PolopolyUser.find.where().eq("email", (request().username())).findUnique();
        return ok(views.html.index.render(user));
    }
	
	@Security.Authenticated(Secured.class)
    public static Result algo1() {
        PolopolyUser user = PolopolyUser.find.where().eq("email", (request().username())).findUnique();
        QueryResponse response = SolrUtil.getCategoriesQuery(user.email);
        System.out.println("Number of document returned by query: " + response.getResults().size());
        //Top Facet Categories of this user
        List<StandardArticle> articlesViewed = new ArrayList<StandardArticle>();
        for (SolrDocument doc : response.getResults()){
        	System.out.println(doc);
    		StandardArticle article = DemoData.getInstance().articleList.get(Integer.parseInt(doc.getFieldValue("id").toString().replace("[","").replace("]", "")));
    		articlesViewed.add(article);
    	}
        List<FacetField> facetFieldList = response.getFacetFields();
    	for (FacetField ff : facetFieldList){
    		System.out.println(ff.getName()+":");
    		List<Count> vals = ff.getValues();
    		if (vals!=null){
    		  for (Count val : vals){
    			System.out.println(val.getName()+"("+val.getCount()+")");    			
    		  }
    		}
    	}

    	//Top articles viewed by other users with categories matching the top facets
    	response = SolrUtil.getRecommendedArticle(user.email ,facetFieldList);
    	facetFieldList = response.getFacetFields();
    	List<StandardArticle> articlesRecommended = new ArrayList<StandardArticle>();
    	for (FacetField ff : facetFieldList){
    		System.out.println("Facet: " + ff.getName()+":");
    		List<Count> vals = ff.getValues();
    		if (vals!=null){
    		  for (Count val : vals){
    	    		StandardArticle article = DemoData.getInstance().articleList.get(Integer.parseInt(val.getName().replace("[","").replace("]", "")));
    	    		//Check if this user has already seen the article recommended.
    	    		if (!articlesViewed.contains(article)){
    	    			articlesRecommended.add(article);
    	    		}else{
    	    			System.out.println("alreadyViewed");
    	    		}
    	    		System.out.println("CID: " + article.contentId);
    	    		System.out.println("CAT1: " + article.categories.get(0).name);
    	    		System.out.println("CAT2: " + article.categories.get(1).name + "\n");
    		  }
    		}
    	}
    	System.out.println(articlesRecommended.size());
    	return ok(views.html.algo1.render(user));
	}
    
	@Security.Authenticated(Secured.class)
    public static Result algo2() {
		PolopolyUser user = PolopolyUser.find.where().eq("email", (request().username())).findUnique();
        //Top Facet Categories of this user
		QueryResponse response = SolrUtil.getCategoriesQuery(user.email);
		System.out.println("Number of document returned by query: " + response.getResults().size());
		//Top Facet Categories of this user
		List<StandardArticle> articlesViewed = new ArrayList<StandardArticle>();
		for (SolrDocument doc : response.getResults()){
			System.out.println(doc);
			StandardArticle article = DemoData.getInstance().articleList.get(Integer.parseInt(doc.getFieldValue("id").toString().replace("[","").replace("]", "")));
			articlesViewed.add(article);
		}
		List<FacetField> facetFieldList = response.getFacetFields();
		for (FacetField ff : facetFieldList){
			System.out.println(ff.getName()+":");
			List<Count> vals = ff.getValues();
			if (vals!=null){
			  for (Count val : vals){
				System.out.println(val.getName()+"("+val.getCount()+")");    			
			  }
			}
		}
		//Top Facet Users with the matching top Facet categories
		response = SolrUtil.getMatchingUsers(user.email, facetFieldList);
		
		
		//Top Articles viewed by these top facet users
        return ok(views.html.algo2.render(user));
	}
	
    public static Result login() {
        return ok(
            login.render(Form.form(Login.class))
        );
    }
    
    public static Result logout() {
        session().clear();
        flash("success", "You've been logged out");
        return redirect(
            routes.Application.login()
        );
    }
    
    public static Result setupDemo(){
    	DemoData.getInstance().initializeData();
    	return redirect(routes.Application.login());
    }

    public static Result authenticate() {
        Form<Login> loginForm = Form.form(Login.class).bindFromRequest();
        if (loginForm.hasErrors()) {
            return badRequest(login.render(loginForm));
        } else {
            session().clear();
            session("email", loginForm.get().email);
            return redirect(
                routes.Application.index()
            );
        }
    }

    public static class Login {

        public String email;
        public String password;

        public String validate() {
            if (PolopolyUser.authenticate(email, password) == null) {
                return "Invalid user or password";
            }
            return null;
        }
    }
}

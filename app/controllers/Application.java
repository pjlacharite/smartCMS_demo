package controllers;

import java.util.List;

import models.PolopolyUser;

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
        QueryResponse response = SolrUtil.getCategoriesQuery(user.email);
        
        for (SolrDocument doc : response.getResults()){
    		System.out.println(doc);
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
    	
        //Retrieve Each content category count consumed by this user for display purpose
        //Retrieve a static number of content consumed by another user that were not consumed by this one and that represent his consumption habits (based on the category with the most hits).
        
        return ok(views.html.index.render(String.valueOf(response.getResults().size()), user));
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
    	DemoData.getInstance();
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

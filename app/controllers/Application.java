package controllers;

import models.PolopolyUser;
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
		SolrUtil.index("test", "val2");
        int nbHits = SolrUtil.getHits("test", "val2");
        
        PolopolyUser user = PolopolyUser.find.where().eq("email", (request().username())).findUnique();
        //Retrieve Each content category count consumed by this user for display purpose
        //Retrieve a static number of content consumed by another user that were not consumed by this one and that represent his consumption habits (based on the category with the most hits).
        
        return ok(views.html.index.render(String.valueOf(nbHits), user));
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

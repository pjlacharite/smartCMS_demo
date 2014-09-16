package controllers;

import models.PolopolyUser;
import play.data.Form;
import play.mvc.Controller;
import play.mvc.Result;
import tools.SolrUtil;
import views.html.login;

public class Application extends Controller {

    public static Result index() {
        SolrUtil.index("test", "val2");

        int nbHits = SolrUtil.getHits("test", "val2");
        return ok(views.html.index.render("cette page a été vue : " + nbHits));
    }
    
    public static Result login() {
        return ok(
            login.render(Form.form(Login.class))
        );
    }

    public static Result setupDemo(){
    	if (PolopolyUser.find.where().eq("email", "bob@gmail.com").findList().size() == 0){
    		new PolopolyUser("bob@gmail.com", "Bob", "secret").save();
    	}
    	if (PolopolyUser.find.where().eq("email", "alice@gmail.com").findList().size() == 0){
    		new PolopolyUser("alice@gmail.com", "Alice", "secret").save();
    	}
    	
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

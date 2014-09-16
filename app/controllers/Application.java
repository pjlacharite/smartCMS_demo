package controllers;

import play.mvc.Controller;
import play.mvc.Result;
import tools.SolrUtil;
import views.html.index;

public class Application extends Controller {

    public static Result index() {
        SolrUtil.index("test", "val2");

        int nbHits = SolrUtil.getHits("test", "val2");
        return ok(index.render("cette page a été vue : " + nbHits));
    }

}

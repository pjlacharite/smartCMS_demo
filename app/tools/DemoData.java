package tools;

import java.util.ArrayList;
import java.util.List;

import models.Category;
import models.PolopolyUser;
import models.StandardArticle;

public class DemoData {
	private static final String[] articleNames = new String[]{"sausage", "blubber", "pencil", "butt", "moon", "water", "computer", "school", "network", "hammer", "walking",
	                                 "violently", "mediocre", "literature", "chair", "two", "window", "cords", "musical", "zebra", "xylophone", "penguin", "home", "dog",
	                                 "final", "ink", "teacher", "fun", "website", "banana", "uncle", "softly", "mega", "ten", "awesome", "attatch", "blue", "internet",
	                                 "bottle", "tight", "zone", "tomato", "prison", "hydro", "cleaning", "telivision", "send", "frog", "cup", "book", "zooming", "falling",
	                                 "evily", "gamer", "lid", "juice", "moniter", "captain", "bonding", "loudly", "thudding", "guitar",	"shaving", "hair", "soccer", "water",
	                                 "racket", "table", "late", "media", "desktop", "flipper", "club", "flying", "smooth", "monster", "purple", "guardian", "bold", "hyperlink",
	                                 "presentation", "world", "national", "comment", "element", "magic", "lion", "sand", "crust", "toast", "jam", "hunter", "forest", "foraging",
	                                 "silently", "tawesomated", "joshing", "pong"};
	
	private static final String[] categoryNames = new String[]{"action", "horror", "comedy", "drama", "fiction", "foreign", "bollywood", "children", "film noir", "thriller"};
	private static DemoData instance;
	private static List<StandardArticle> articleList;
	private static List<Category>  categoryList;
	private static List<PolopolyUser> polopolyUsers;
	
	public static DemoData getInstance(){
		if (instance == null){
			instance = new DemoData();
			SolrUtil.clearIndex();
			instance.initializeUsers();
			instance.initializeCategories();
			instance.initializeArticles(categoryList);
			instance.buildIndex(articleList);
		}
		return instance;
	}
	
	private void initializeUsers(){
		polopolyUsers = new ArrayList<PolopolyUser>();
		PolopolyUser user1 = new PolopolyUser("bob@gmail.com", "Bob", "secret");
		PolopolyUser user2 = new PolopolyUser("alice@gmail.com", "Alice", "secret");
		polopolyUsers.add(user1);
		polopolyUsers.add(user2);
		if (PolopolyUser.find.where().eq("email", "bob@gmail.com").findList().size() == 0){
    		user1.save();
    	}
    	if (PolopolyUser.find.where().eq("email", "alice@gmail.com").findList().size() == 0){
    		user2.save();
    	}
	}
	
	private void initializeCategories(){
		categoryList = new ArrayList<Category>();
		for (int i = 0; i < categoryNames.length; i++){
			Category category = new Category(categoryNames[i]);
			categoryList.add(category);
		}
	}
	
	private void initializeArticles(List<Category> categories){
		articleList = new ArrayList<StandardArticle>();
		for (int i = 0; i < articleNames.length; i++){
			int random1 = (int)Math.floor(Math.random()*categoryNames.length);
			int random2 = (int)Math.floor(Math.random()*categoryNames.length);
			StandardArticle article = new StandardArticle(i, articleNames[i], categories.get(random1), categories.get(random2));
			articleList.add(article);
		}
	}
	
	private void buildIndex(List<StandardArticle> articles){
		//Call SolrUtil.index() with Article/Category associated with a User.
	}
}
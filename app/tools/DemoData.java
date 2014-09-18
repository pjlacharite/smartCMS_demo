package tools;

import java.util.ArrayList;
import java.util.List;

import org.apache.solr.common.SolrInputDocument;

import models.Category;
import models.PolopolyUser;
import models.StandardArticle;

public class DemoData {
	private static final int contentViewed = 60;
	private static final String[] articleNames = new String[]{"sausage", "blubber", "pencil", "butt", "moon", "water", "computer", "school", "network", "hammer", "walking",
	                                 "violently", "mediocre", "literature", "chair", "two", "window", "cords", "musical", "zebra", "xylophone", "penguin", "home", "dog",
	                                 "final", "ink", "teacher", "fun", "website", "banana", "uncle", "softly", "mega", "ten", "awesome", "attatch", "blue", "internet",
	                                 "bottle", "tight", "zone", "tomato", "prison", "hydro", "cleaning", "telivision", "send", "frog", "cup", "book", "zooming", "falling",
	                                 "evily", "gamer", "lid", "juice", "moniter", "captain", "bonding", "loudly", "thudding", "guitar",	"shaving", "hair", "soccer", "water",
	                                 "racket", "table", "late", "media", "desktop", "flipper", "club", "flying", "smooth", "monster", "purple", "guardian", "bold", "hyperlink",
	                                 "presentation", "world", "national", "comment", "element", "magic", "lion", "sand", "crust", "toast", "jam", "hunter", "forest", "foraging",
	                                 "silently", "tawesomated", "joshing", "pong"};
	
	private static final String[] categoryNames = new String[]{"action", "horror", "comedy", "drama", "fiction", "foreign", "bollywood", "children", "film noir", "thriller"};
	
	private static final String[] userNames = new String[]{"Alice","Bob","Charlie","Dave", "Erin", "Francis", "George", "Harry", "Ingrid", "John"};
	
	private static DemoData instance;
	public List<StandardArticle> articleList;
	public List<Category>  categoryList;
	public List<PolopolyUser> polopolyUsers;
	
	public static DemoData getInstance(){
		if (instance == null){
			instance = new DemoData();
			SolrUtil.clearIndex();
		}
		return instance;
	}
	
	public void initializeData(){
		System.out.println("Initializing...");
		System.out.println("Initializing...Users");
		instance.initializeUsers();
		System.out.println("Initializing...Categories");
		instance.initializeCategories();
		System.out.println("Initializing...Articles");
		instance.initializeArticles(categoryList);
		System.out.println("Initializing...Index");
		instance.buildIndex(articleList);
	}
	
	private void initializeUsers(){
		polopolyUsers = new ArrayList<PolopolyUser>();
		for (int i = 0; i < userNames.length; i++){
			String name = userNames[i];
			PolopolyUser user = new PolopolyUser(name.toLowerCase() + "@gmail.com", name, "test");
			polopolyUsers.add(user);
			if (PolopolyUser.find.where().eq("email", name.toLowerCase() + "@gmail.com").findList().size() == 0){
				user.save();
	    	}
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
			StandardArticle article = new StandardArticle(String.valueOf(i), articleNames[i], categories.get(random1), categories.get(random2));
			articleList.add(article);
		}
	}
	
	private void buildIndex(List<StandardArticle> articles){
		//SolrJ seems to lack document update features introduced in Solr4, we have to build all document before indexing them.
		List<SolrInputDocument> documents = new ArrayList<SolrInputDocument>();
		for (PolopolyUser user : polopolyUsers){
			for (int i = 0; i < contentViewed; i++){
				int random = (int)Math.floor(Math.random()*articleList.size());
				StandardArticle article = articleList.get(random);
				SolrInputDocument document = null;
				for (SolrInputDocument existingDocument: documents){
					if (existingDocument.getFieldValue("id").equals(article.contentId)){
						document = existingDocument;
						document.addField("username_ss", user.email);
					}
				}
				if (document == null){
					document = SolrUtil.createDocument(article.contentId, article.categories, user.email);
					documents.add(document);
				}
			}
		}
		SolrUtil.index(documents);
	}
}
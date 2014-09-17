package models;

import java.util.ArrayList;
import java.util.List;

import play.db.ebean.Model.Finder;

public class StandardArticle {
	public int contentId;
	public String name;
	public List<Category> categories;
	
	public StandardArticle(int contentId, String name, Category category1, Category category2){
		this.categories = new ArrayList<Category>();
		this.categories.add(category1);
		this.categories.add(category2);
		this.contentId = contentId;
		this.name = name;
	}
	
	public static Finder<String,StandardArticle> find = new Finder<String,StandardArticle>(
	        String.class, StandardArticle.class
    );
}

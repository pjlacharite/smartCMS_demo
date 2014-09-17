package models;

import play.db.ebean.Model.Finder;

public class Category {
	public String name;
	
	public Category(String name){
		this.name = name;
	}
	
	public static Finder<String,Category> find = new Finder<String,Category>(
	        String.class, Category.class
    );
}

package models;

import javax.persistence.Entity;
import javax.persistence.Id;

import play.db.ebean.Model;

@Entity
public class PolopolyUser extends Model {

    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	@Id
    public String email;
    public String name;
    public String password;
    
    public PolopolyUser(String email, String name, String password) {
      this.email = email;
      this.name = name;
      this.password = password;
    }

    public static Finder<String,PolopolyUser> find = new Finder<String,PolopolyUser>(
        String.class, PolopolyUser.class
    );
    
    public static PolopolyUser authenticate(String email, String password) {
        return find.where().eq("email", email)
            .eq("password", password).findUnique();
    }
}
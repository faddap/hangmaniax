package hm_model;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

import javax.jdo.annotations.Extension;
import javax.jdo.annotations.IdGeneratorStrategy;
import javax.jdo.annotations.PersistenceCapable;
import javax.jdo.annotations.Persistent;
import javax.jdo.annotations.PrimaryKey;

@PersistenceCapable
public class User implements Serializable {
	/**
	 * @author vatov
	 */
	/**
	 * User roles:
	 * PLAYER 	can only play and propose word/description
	 * TRUSTED 	has PLAYER's rights but adds a word/description without first proposing it
	 * ADMIN 	has TRUSTED's rights and can additionally grant roles to other registered users
	 */
	public enum Role { PLAYER, APPROVER, ADMIN }
	
	/**
	 * Primary key
	 */
    @PrimaryKey
    @Persistent(valueStrategy = IdGeneratorStrategy.IDENTITY)
    @Extension(vendorName="datanucleus", key="gae.encoded-pk", value="true")
    private String encodedKey = null;
    
	/**
	 * User's username
	 */
	@Persistent
	private String name = null;
	
	/**
	 * User's email
	 */
	@Persistent
	@Extension(vendorName="datanucleus", key="gae.pk-name", value="true")
	private String email = null;
	
	/**
	 * User's password
	 */
	@Persistent
	private String pass = null;
	
	/**
	 * Date of last login
	 */
	@Persistent
	private Date lastLogin = null;
	
	/**
	 * User's game count
	 */
	@Persistent
	private int gamesPlayed = 0;
	
	/**
	 * User's role
	 */
	@Persistent
	private Role role = Role.PLAYER;
	
	/**
	 * User's score
	 */
	@Persistent
	private int score = 0;
	/**
	 * All the words, entered by the user
	 */
	@Persistent(mappedBy = "user")
	private List<Word> words = null;
	
	public User(String name, String email, String pass) {
		this.name = name;
		this.email = email;
		this.pass = pass;
	}
	
	/**
	 * Sets User's key.
	 * @param key
	 */
	public void setKey(String key) {
		this.encodedKey = key;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getEmail() {
		return email;
	}
	
	public void setEmail(String email) {
		this.email = email;
	}

	public String getPass() {
		return pass;
	}

	public void setPass(String pass) {
		this.pass = pass;
	}

	public Date getLastLogin() {
		return lastLogin;
	}

	public void setLastLogin(Date lastLogin) {
		this.lastLogin = lastLogin;
	}

	public int getGamesPlayed() {
		return gamesPlayed;
	}

	public void setGamesPlayed(int gamesPlayed) {
		this.gamesPlayed = gamesPlayed;
	}

	public Role getRole() {
		return role;
	}
	
	public void setRole(Role r) {
		this.role = r;
	}

	public List<Word> getWords() {
		return words;
	}
	
	public void addWord(Word w) {
		this.words.add(w);
	}
	
	public void addPoints(int p) {
		this.score += p;
	}
	
	public int getScore() {
		return this.score;
	}
}

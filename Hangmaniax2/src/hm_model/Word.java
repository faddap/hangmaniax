package hm_model;

import javax.jdo.annotations.Extension;
import javax.jdo.annotations.IdGeneratorStrategy;
import javax.jdo.annotations.PersistenceCapable;
import javax.jdo.annotations.Persistent;
import javax.jdo.annotations.PrimaryKey;

@PersistenceCapable
public class Word {
	/**
	 * @author vatov
	 */
	/**
	 * The currently used part of speech.
	 */
	public enum PartOfSpeech { NOUN, VERB, ADJECTIVE}
	
	/**
	 * Primary key
	 */
	@PrimaryKey
    @Persistent(valueStrategy = IdGeneratorStrategy.IDENTITY)
	@Extension(vendorName="datanucleus", key="gae.encoded-pk", value="true")
    private String encodedKey = null;
	
	/**
	 * The actual word
	 */
	@Persistent
	@Extension(vendorName="datanucleus", key="gae.pk-name", value="true")
	private String body = null;
	
	/**
	 * Word's description
	 */
	@Persistent
	private String descr = null;
	
	/**
	 * Word's state - indicates whether it can be 'drawn' for a game
	 */
	@Persistent
	private boolean approved = false;
	
	/**
	 * Which part of speech the word belongs to
	 */
	@Persistent
	private PartOfSpeech pos = PartOfSpeech.NOUN;
	
	/**
	 * How many time the word has been 'drawn' for a game
	 */
	@Persistent
	private int played = 0;
	
	/**
	 * How many times the word has been guessed
	 */
	@Persistent
	private int guessed = 0;
	
	/**
	 * The user, owning the word
	 */
	@Persistent
	private User user = null;
	
	public void setKey(String encodedKey) {
		this.encodedKey = encodedKey;
	}
}

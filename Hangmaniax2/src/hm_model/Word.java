package hm_model;

import java.io.Serializable;

import javax.jdo.annotations.Extension;
import javax.jdo.annotations.IdGeneratorStrategy;
import javax.jdo.annotations.PersistenceCapable;
import javax.jdo.annotations.Persistent;
import javax.jdo.annotations.PrimaryKey;

@PersistenceCapable
public class Word implements Serializable {
	/**
	 * @author vatov
	 */
	/**
	 * The currently used part of speech.
	 */
	public enum PartOfSpeech { NOUN, VERB, ADJECTIVE}
	
	/**
	 * ID
	 */
	@Persistent(valueStrategy = IdGeneratorStrategy.SEQUENCE)
	private long id;
	
	/**
	 * The actual word
	 */
	@PrimaryKey
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
	
	public Word(String body) {
		this.body = body.toLowerCase();
	}
	
	public Word(String body, String descr) {
		this.body = body.toLowerCase();
		this.descr = descr;
	}
}

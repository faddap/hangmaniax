package hm_model;

import java.io.Serializable;
import java.util.List;

import javax.jdo.JDOException;
import javax.jdo.PersistenceManager;
import javax.jdo.Query;
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
	
	public long getId() {
		return this.id;
	}
	
	@Override
	public String toString() {
		return this.body;
	}
	
	public static Word getRandom(PersistenceManager pm) throws JDOException {
		Word random = null;
		Query q = pm.newQuery(Word.class);
		q.setOrdering("id desc");
		q.setRange(0,1);
		try {
			Word lastWord = ((List<Word>) q.execute()).get(0);
			long lastIndex = lastWord.getId();
			long randomIndex = Utils.getRandomInRange(0, lastIndex);
			q = null;
			
			q = pm.newQuery(Word.class);
			q.setFilter("id <= idParam");
			q.declareParameters("long idParam");
			q.setOrdering("id desc");
			q.setRange(0, 10);
			random = ((List<Word>) q.execute(randomIndex)).get(0);
		} finally {
			q.closeAll();
			pm.close();
		}
		return random;
	}
}

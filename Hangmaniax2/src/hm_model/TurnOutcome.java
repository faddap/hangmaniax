package hm_model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import com.google.appengine.labs.repackaged.org.json.JSONException;
import com.google.appengine.labs.repackaged.org.json.JSONObject;

/**
 * The class has the aim to preserve the state of the last game turn. The Game class knows about the state,
 * therefore both classes are tightly coupled with each other with this one being 'part of' the former.
 * The state could be:
 * 1. 	a hit - a letter is hit and its positions should be therefore preserved
 * 2.1 	a miss - no letter is hit
 * 2.2 	duplicate submit - letter already submitted is being submitted again
 * @author vatov
 *
 */
public class TurnOutcome implements Serializable {
	private char letter = ' ';
	private List<Integer> letterPosition = null;
	private boolean hit = false;
	private boolean error = false;
	
	public TurnOutcome(char letter, boolean successful) {
		this.letter = letter;
		this.hit = successful;
	}
	
	public void setOccurences(ArrayList<Integer> occurrences) {
		this.letterPosition = occurrences;
		this.hit = true;
		this.error = !this.hit;
	}
	
	public void setErroneous() {
		this.error = true;
		this.hit = !this.error;
	}
	
	public boolean isSuccessful() {
		return this.hit;
	}
	
	public JSONObject toJson() throws JSONException {
		JSONObject json = new JSONObject();
		
		json.put("hit", this.isSuccessful());
		json.put("letter", Character.toString(this.letter));
		json.put("error", this.error);
		
		if (this.isSuccessful()) {
			json.put("occurrences", this.letterPosition.toArray());
		}
		return json;
	}
}

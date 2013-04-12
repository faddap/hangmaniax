package hm_model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.Iterator;

public class Game implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1305780889427399438L;
	private final static int MAX_TURNS = 7;
	private final static int DEFAULT_POINTS = 30;
	private final static int POINTS_WITH_DESCRIPTION = 10;
	
	private String email = "";
	private String word = "";
	private String askerEmail = "";
	
	private TurnOutcome lastOutcome = null;
	private int pointsForTheGame = DEFAULT_POINTS;
	private int turnsLeft = MAX_TURNS;
	private char[] lettersLeft = null;
	private LinkedHashSet<Character> lettersSubmitted = null;
	
	public Game(String email, Word w) {
		this.word = w.toString();
		this.email = email;
		this.lettersLeft = w.toString().toCharArray();
		this.lettersSubmitted = new LinkedHashSet<Character>();
	}
	
	public Game(String email, Word w, String askedByEmail) {
		this.word = w.toString();
		this.email = email;
		this.askerEmail = askedByEmail;
		this.lettersLeft = w.toString().toCharArray();
		this.lettersSubmitted = new LinkedHashSet<Character>();
	}
	
	public TurnOutcome turn(char letterSubmitted) {
		this.lastOutcome = null;
		Character ch = new Character(letterSubmitted);
		
		//try to add letter to the set
		if(this.lettersSubmitted.add(ch)) {
			//submitted letter is not present, so search for it in the list of letters left
			ArrayList<Integer> occ = this.getOccurrences(ch.charValue());
			if (!occ.isEmpty()) {
				//return position(s) of occurrences/matches
				this.relaxLetters(occ);
				this.lastOutcome = new TurnOutcome(ch.charValue(), true);
				this.lastOutcome.setOccurences(occ);
			} else {
				--this.turnsLeft;
				this.lastOutcome = new TurnOutcome(ch.charValue(), false);
			}
		} else {
			//submitted letter is present, so return an erroneous response
			this.lastOutcome = new TurnOutcome(ch.charValue(), false);
			this.lastOutcome.setErroneous();
		}
		
		return this.lastOutcome;
	}
	
	public TurnOutcome getLastTurnOutcome() {
		return this.lastOutcome;
	}
	
	public boolean isWin() {
		for (int i=0; i<this.lettersLeft.length; i++) {
			if (this.lettersLeft[i] != ' ') {
				return false;
			}
		}
		return true;
	}
	
	public boolean isLoss() {
		return this.turnsLeft == 0;
	}
	
	private ArrayList<Integer> getOccurrences(char letter) {
		ArrayList<Integer> occ = new ArrayList<Integer>();

		for (int i=0; i<this.lettersLeft.length; i++) {
			if (this.lettersLeft[i] == letter) {
				occ.add(i);
			}
		}
		
		return occ;
	}
	
	private void relaxLetters(ArrayList<Integer> pos) {
		Iterator<Integer> it = pos.iterator();
		while(it.hasNext()) {
			this.lettersLeft[it.next()] = ' ';
		}
	}

	public static int getMaxTurns() {
		return MAX_TURNS;
	}

	public static int getDefaultPoints() {
		return DEFAULT_POINTS;
	}

	public static int getPointsWithDescription() {
		return POINTS_WITH_DESCRIPTION;
	}

	public int getPointsForTheGame() {
		return pointsForTheGame;
	}

	public void setPointsForTheGame(int pointsForTheGame) {
		this.pointsForTheGame = pointsForTheGame;
	}
	
	public String getDescription() {
		String descr = "";
		
		this.pointsForTheGame = POINTS_WITH_DESCRIPTION;
		//TODO: get the description from the word
		
		return descr;
	}
	
	public String getEmail() {
		return this.email;
	}
	
	public String getWord() {
		return this.word;
	}
}

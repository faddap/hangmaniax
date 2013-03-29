package hm_model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.Iterator;

public class Game implements Serializable {
	private final static int MAX_TURNS = 7;
	private final static int DEFAULT_POINTS = 30;
	private final static int POINTS_WITH_DESCRIPTION = 10;
	
	private User player = null;
	private Word word = null;
	private User askedBy = null;
	
	private TurnOutcome lastOutcome = null;
	private int pointsForTheGame = DEFAULT_POINTS;
	private int turnsLeft = MAX_TURNS;
	private char[] lettersLeft = null;
	private LinkedHashSet<Character> lettersSubmitted = null;
	
	public Game(User pl, Word w) {
		this.player = pl;
		this.word = w;
		
		//TODO: init all needed variables
	}
	
	public Game(User pl, Word w, User askedBy) {
		this.player = pl;
		this.word = w;
		this.askedBy = askedBy;
		
		//TODO: init all needed variables
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
			//submitted letter has been already present, so return an erroneous response
			this.lastOutcome = new TurnOutcome(ch.charValue(), false);
			this.lastOutcome.setErroneous();
		}
		
		if (this.isWin() || this.isLoss()) {
			//TODO: increment word's <code>played</code> field
			if (this.isWin()) {
				//TODO: increment word's <code>guessed</code> field
				this.player.addPoints(this.pointsForTheGame);
			} else if (User.class.isInstance(this.askedBy)) {
				this.askedBy.addPoints(this.pointsForTheGame);
			}
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
}

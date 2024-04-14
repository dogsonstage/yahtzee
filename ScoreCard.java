package main;

import java.util.Collection;
import java.util.HashMap;

/**
 * Keeps track of all score card functions
 */
class ScoreCard {
	
	//Holds score card info in order of <name, score>
	private HashMap<String, Integer>scoreCard = new HashMap<String, Integer>();
	//Hold unchanging specific score values
	private final HashMap<String, Integer>scoreValues = new HashMap<String, Integer>();
	//
	
	protected ScoreCard() {
		//Upper score
		scoreCard.put("ONES", null);
		scoreCard.put("TWOS", null);
		scoreCard.put("THREES", null);
		scoreCard.put("FOURS", null);
		scoreCard.put("FIVES", null);
		scoreCard.put("SIXES", null);
		scoreCard.put("TOTAL SCORE", null);
		scoreCard.put("BONUS", null);
		//FOR DEBUG PURPOSES ONLY, DO NOT INCLUDE WITH PLAYER DISPLAY
		scoreCard.put("UPPER SUBTOTAL", null);
		
		//Lower Score
		scoreCard.put("3 OF A KIND", null);
		scoreCard.put("4 OF A KIND", null);
		scoreCard.put("FULL HOUSE", null);
		scoreCard.put("SMALL STRAIGHT", null);
		scoreCard.put("LARGE STRAIGHT", null);
		scoreCard.put("YAHTZEE", null);
		scoreCard.put("CHANCE", null);
		scoreCard.put("YAHTZEE BONUS", null);
		//FOR DEBUG PURPOSES ONLY, DO NOT INCLUDE WITH PLAYER DISPLAY
		scoreCard.put("LOWER SUBTOTAL", null);
		scoreCard.put("GRAND TOTAL", null);
		
		//Score reference
		scoreValues.put("BONUS", 35);
		scoreValues.put("FULL HOUSE", 25);
		scoreValues.put("SMALL STRAIGHT", 30);
		scoreValues.put("LARGE STRAIGHT", 40);
		scoreValues.put("YAHTZEE", 50);
		scoreValues.put("YAHTZEE BONUS", 100);
	}
	
	/***
	 * Updates the ScoreCard to reflect users desired valid score.
	 * 
	 * @param name - Score to be updated
	 * @param hand - dice in player's hand
	 * @param table - dice on the table
	 * @param zero - True if player willingly scores a zero
	 */
	protected void score(String name, HashMap<String, Integer> hand, HashMap<String, Integer> table, boolean zero) {
		if (zero) {
			scoreCard.put(name, 0);
			return;
		}
		if (name.equals("YAHTZEE BONUS") && scoreCard.get("YAHTZEE BONUS") != null) {
			scoreCard.put(name, scoreCard.get(name) + scoreValues.get(name));
		}
		scoreCard.put(name, findSingleScore(name, hand, table));
		
		//Calculate upper subtotal and bonus
		if (scoreCard.get("ONES") != null && scoreCard.get("TWOS") != null && scoreCard.get("THREES") != null &&
			scoreCard.get("FOURS") != null && scoreCard.get("FIVES") != null && scoreCard.get("SIXES") != null) {
			
			int sum = scoreCard.get("ONES") + scoreCard.get("TWOS") + scoreCard.get("THREES") + scoreCard.get("FOURS") 
						+ scoreCard.get("FIVES") + scoreCard.get("SIXES");
			
			scoreCard.put("TOTAL SCORE", sum);
			
			if (sum >= 63) { 
				scoreCard.put("BONUS", scoreValues.get("BONUS"));
			}
			else {
				scoreCard.put("BONUS", 0);
			}
			
			if (scoreCard.get("BONUS") != null) {
				scoreCard.put("UPPER SUBTOTAL", scoreValues.get("BONUS") + sum);
			}
			scoreCard.put("UPPER SUBTOTAL", sum);
		}
		//Calculate lower subtotal
		if (scoreCard.get("3 OF A KIND") != null && scoreCard.get("4 OF A KIND") != null && scoreCard.get("FULL HOUSE") != null &&
				scoreCard.get("SMALL STRAIGHT") != null && scoreCard.get("LARGE STRAIGHT") != null && scoreCard.get("YAHTZEE") != null &&
				scoreCard.get("CHANCE") != null && scoreCard.get("YAHTZEE BONUS") != null) {
				
				int sum = scoreCard.get("3 OF A KIND") + scoreCard.get("4 OF A KIND") + scoreCard.get("FULL HOUSE") 
						+ scoreCard.get("SMALL STRAIGHT") + scoreCard.get("LARGE STRAIGHT") + scoreCard.get("YAHTZEE")
						+ scoreCard.get("CHANCE");
				
				scoreCard.put("LOWER SUBTOTAL", sum);
		}
		
		//Calculate grand total
		if (scoreCard.get("UPPER SUBTOTAL") != null && scoreCard.get("LOWER SUBTOTAL") != null) {
			scoreCard.put("GRAND TOTAL", scoreCard.get("UPPER SUBTOTAL") + scoreCard.get("LOWER SUBTOTAL"));
		}
		
			
	}

	/***
	 * Finds all possible scores greater than zero of all dice in the game.
	 * Used by possibleScoresToString.
	 * 
	 * @param hand - dice set in the hand
	 * @param table - dice set on the table
	 * @return a String array of the names of all possible scores
	 */
	protected String[] findPossibleScores(HashMap<String, Integer> hand, HashMap<String, Integer> table) {
		String possScrStr = "";
		HashMap<String, Integer> allDice = new HashMap<String, Integer>();
		if (!hand.isEmpty()) { allDice.putAll(hand); }
		if (!table.isEmpty()) { allDice.putAll(table); }
		Collection<Integer>diceVals = allDice.values();
		
		/*
		 * ORDER OF SCORES:
		 * 
		 * -Yahtzee
		 * -LARGE STRAIGHT
		 * -SMALL STRAIGHT
		 * -Full house
		 * -4 kind
		 * -3 Kind
		 * -CHANCE
		 * -SIXES
		 * -FIVES
		 * -FOURS
		 * -THREES
		 * -TWOS
		 * -ONES
		 * 
		 * Generally In order from hardest to get to easiest/least to greatest
		 */
		
		//Find Yahtzee
			if (findMultiKind(diceVals).equals("YAHTZEE")) {
				//Check for Yahtzee bonus
				if (scoreCard.get("YAHTZEE") == 50) {
					possScrStr = possScrStr + "YAHTZEE BONUS,";
				}
				//if Yahtzee then 4 and 3 kind is possible by default, so check for that too
				if (scoreCard.get("4 OF A KIND") == null) {
					possScrStr = possScrStr + "4 OF A KIND,";
				}
				if (scoreCard.get("3 OF A KIND") == null) {
					possScrStr = possScrStr + "3 OF A KIND,";
				}
				if (scoreCard.get("YAHTZEE") == null) {
					possScrStr = possScrStr + "YAHTZEE,";
				}
			}
			
		//Find LARGE STRAIGHT
			
		//Find SMALL STRAIGHT
			
		//Find FULL HOUSE
		if (findMultiKind(diceVals).equals("FULL HOUSE") && scoreCard.get("FULL HOUSE") == null) {
			possScrStr = possScrStr + "FULL HOUSE,";
		}
		
		//Find 4 of a kind
		if (findMultiKind(diceVals).equals("4 OF A KIND")) {
			//if four kind then 3 kind is possible by default, so check for that too
			if (scoreCard.get("4 OF A KIND") == null) {
				possScrStr = possScrStr + "4 OF A KIND,";
			}
			if (scoreCard.get("3 OF A KIND") == null) {
				possScrStr = possScrStr + "3 OF A KIND,";
			}
			
		}
		//FInd 3 of a kind
		if (findMultiKind(diceVals).equals("3 OF A KIND") && scoreCard.get("3 OF A KIND") == null) {
				possScrStr = possScrStr + "3 OF A KIND,";
		}
		
		//Find CHANCE
		if (scoreCard.get("CHANCE") == null) {
			possScrStr = possScrStr + "CHANCE,";
		}
		
		//Find any upper number scores
		if (findNumOfMultis(6, diceVals) != 0 && scoreCard.get("SIXES") == null) { possScrStr = possScrStr + "SIXES,"; }
		if (findNumOfMultis(5, diceVals) != 0 && scoreCard.get("FIVES") == null) { possScrStr = possScrStr + "FIVES,"; }
		if (findNumOfMultis(4, diceVals) != 0 && scoreCard.get("FOURS") == null) { possScrStr = possScrStr + "FOURS,"; }
		if (findNumOfMultis(3, diceVals) != 0 && scoreCard.get("THREES") == null) { possScrStr = possScrStr + "THREES,"; }
		if (findNumOfMultis(2, diceVals) != 0 && scoreCard.get("TWOS") == null) { possScrStr = possScrStr + "TWOS,"; }
		if (findNumOfMultis(1, diceVals) != 0 && scoreCard.get("ONES") == null) { possScrStr = possScrStr + "ONES,"; }
		
		String[] possScr = possScrStr.split(",");
		return possScr;

	}
	
	/**
	 * Finds all scores available to mark zero.
	 * Used by possibleZerosToString().
	 * 
	 * @return a String array of the names of all scores that can be marked zero
	 */
	protected String[] findPossibleZeros() {
		String possZeros = "";
		Object[] keys = scoreCard.keySet().toArray();
		
		for (Object key : keys) {
			if (scoreCard.get(key) == null) {
				possZeros = possZeros + key + ",";
			}
		}
		
		return possZeros.split(",");

	}
	
	/***
	 * Finds the total number of the specified integer in a set of dice.
	 * Used by findPossibleScores() and findSingleScore.
	 * 
	 * @param key - number to be counting for
	 * @param dice - set of dice to be checked
	 * @return the total int of key number
	 */
	private int findNumOfMultis (Integer key, Collection<Integer> dice) {
		int totalKey = 0;
		for (Integer die : dice) {
			if (die == key) {
				totalKey++;
			}
		}
		return totalKey;
	}
	
	/***
	 * Turns all possible scores into a readable String for display purposes.
	 * In the format of:
	 * 
	 * ScoreName1(ScoreVal1) ScoreName2(ScoreVal2) ... ScoreNameN(ScoreValN)
	 * 
	 * @param scores - array of scores to become a String
	 * @param numOfMultis - total number of 
	 * @return a String representation of all possible scores
	 */
	protected String possibleScoresToString(HashMap<String, Integer> hand, HashMap<String, Integer> table) {
		String [] scores = findPossibleScores(hand, table);
		String scoresString = "";
		if (!scores[0].equals("")) {
			for (String score : scores) {
				scoresString = scoresString + score + "(" + findSingleScore(score, hand, table).toString() + ") ";
			}
		}
		return scoresString;
	}
	
	/**
	 * Turns all possible zeros into a readable String for display purposes.
	 * In the format of:
	 * 
	 * ScoreName1(0) ScoreName2(0) ... ScoreNameN(0)
	 * 
	 * @return a String representation of all possible zeros
	 */
	protected String possibleZerosToString() {
		String[] zeros = findPossibleZeros();
		String zerosString = "";
		for (String zero : zeros) {
			zerosString = zerosString + zero + "(0) ";
		}
		return zerosString;
	}

	/***
	 * Find and returns the correct score of the indicated name.
	 * Used by for possibleScoresToString() and score(). 
	 * 
	 * @param name - name of the score to be calculated
	 * @param hand - dice in player's hand
	 * @param table - dice on the table
	 * @return an integer value of the calculated score
	 */
	private Integer findSingleScore(String name, HashMap<String, Integer> hand, HashMap<String, Integer> table) {
		HashMap<String, Integer> allDice = new HashMap<String, Integer>();
		if (!hand.isEmpty()) { allDice.putAll(hand); }
		if (!table.isEmpty()) { allDice.putAll(table); }
		Collection<Integer>diceVals = allDice.values();
		
		switch(name) {
		case "ONES":
			return findNumOfMultis(1, diceVals);
			
		case "TWOS":
			return findNumOfMultis(2, diceVals) * 2;
			
		case "THREES":
			return findNumOfMultis(3, diceVals) * 3;
			
		case "FOURS":
			return findNumOfMultis(4, diceVals) * 4;
			
		case "FIVES":
			return findNumOfMultis(5, diceVals) * 5;
			
		case "SIXES":
			return findNumOfMultis(6, diceVals) * 6;
			
		case "3 OF A KIND":
			int sumA = 0;
			for (Integer die : diceVals) {
				sumA += die;
			}
			return sumA;
			
		case "4 OF A KIND":
			int sumB = 0;
			for (Integer die : diceVals) {
				sumB += die;
			}
			return sumB;
			
		case "FULL HOUSE":
			return scoreValues.get("FULL HOUSE");
			
		case "SMALL STRAIGHT":
			return scoreValues.get("SMALL STRAIGHT");
			
		case "LARGE STRAIGHT":
			return scoreValues.get("LARGE STRAIGHT");
			
		case "YAHTZEE":
			return scoreValues.get("YAHTZEE");
			
		case "YAHTZEE BONUS":
			return scoreValues.get("YAHTZEE BONUS");
		case "CHANCE":
			int sumC = 0;
			for (Integer die : diceVals) {
				sumC += die;
			}
			return sumC;
	}
		return null;
	}
	
	/***
	 * Finds the type of score if present out of all possible 
	 * scores involving multiple duplicates of dice 
	 * (3,4,5 of a Kind and FULL HOUSE) and returns null otherwise.
	 * Used by findPossibleScores.
	 * 
	 * @param dice - set of dice to be checked
	 * @return a String of the name of the type of valid score
	 */
	private String findMultiKind (Collection<Integer> dice) {
		//Store all occurrences of numbers
		Integer[] nums = {0,0,0,0,0,0};
		for (Integer die : dice) {
			nums[die-1]++;
		}
		//Find max number occurences + possible full house
		int maxNumIndex = 0;
		for (int i = 0; i < nums.length; i++) {
			if(nums[i] > nums[maxNumIndex]) {
				maxNumIndex = i;
			}
		}
		
		//Return appropriate kind
		if (nums[maxNumIndex] == 3) {
			for (int num : nums) {
				if (num == 2) {
					return "FULL HOUSE";
				}
			}
			return "3 OF A KIND";
		}
		else if (nums[maxNumIndex] == 4) {
			return "4 OF A KIND";
		}
		else if (nums[maxNumIndex] == 5) {
			return "YAHTZEE";
		}
		else {
			return "";
		}
	}
	
	/***
	 * Retrieves recorded score
	 * @param name - score name to retrieve
	 * @return score
	 */
	protected Integer getScore(String name) {
		return scoreCard.get(name);
	}
	
	
	/***
	 * Returns a string representation of the current scorecard.
	 * Only used for debug_log. 
	 * STILL IN PROGRESS, TODO
	 */
	public String toString(String playerName) {
		
		String s = "-------------------------------------------------------------------\n";
		s = s + playerName + "'s SCORECARD\n\n";
		
		String[] scores = {"ONES", "TWOS", "THREES", "FOURS", "FIVES", "SIXES", "TOTAL SCORE", "BONUS", "UPPER SUBTOTAL", 
							"3 OF A KIND", "4 OF A KIND", "FULL HOUSE", "SMALL STRAIGHT", "LARGE STRAIGHT", "CHANCE", "YAHTZEE", 
							 "YAHTZEE BONUS", "LOWER SUBTOTAL", "GRAND TOTAL"};
		
		for (String score : scores) {
			s = s + score + ": " + scoreCard.get(score) + "\n";
		}
		
		
		
		return s;
	}
	
	

}

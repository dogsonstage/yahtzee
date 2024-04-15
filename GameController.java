/***
 * Created on 3/12/24 by @author Madison S.
 */
package main;

import java.util.HashMap;
import java.util.Map.Entry;
import java.util.Random;
import java.util.concurrent.TimeUnit;

/**
 * Controls the gameplay loop, calls actions required for the game
 */
class GameController {
	
	private String name;
	private int finalScore;
	private ScoreCard scoreCard;
	private int rollsLeft;
	private boolean turnScored;
	private String tempInput;
	
	private HashMap<String, Integer> diceOnTable;
	private HashMap<String, Integer> diceInHand;
	private Random rand;
	
	/**
	 * STEPS:
	 * 
	 * - Roll Dice
	 * - Find possible moves
	 * - Player Hold/Release dice
	 * - Player selects score
	 * - display any prompts
	 * - repeat until end of game or quit
	 */

	protected GameController(String name) {
		finalScore = 0;
		scoreCard = new ScoreCard();
		rollsLeft = 3;
		this.name = name;
		turnScored = false;
		diceOnTable = new HashMap<String, Integer>();
		diceOnTable.put("a", 0);
		diceOnTable.put("b", 0);
		diceOnTable.put("c", 0);
		diceOnTable.put("d", 0);
		diceOnTable.put("e", 0);
		diceInHand = new HashMap<String, Integer>();
		rand = new Random();
	}
	
	/***
	 * Uses player input to decide which move to execute
	 * 
	 * @param input - move to be executed
	 * @throws InterruptedException
	 */
	protected void nextMove(String input) throws InterruptedException {
		switch (input){
			case "roll":
				roll();
				break;
			case "hold":
				hold();
				break;
			case "drop":
				drop();
				break;
			case "score":
				score();
				break;
			case "commands":
				commands();
				break;
			case "rules":
			case "h":
			case "help":
				help();
				break;
			case "score card":
				scoreCard();
				break;
			case "q":
			case "quit":
				quit();
				break;
		}
	}

	/***
	 * Rolls all dice available on the table if there are rolls left and 
	 * the table is not empty.
	 */
	private void roll() {
		if (rollsLeft == 0) {
			Yahtzee.print("You do not have any rolls left. Enter 'score' to choose what points to add to your scorecard.");
			return;
		}
		if (diceOnTable.isEmpty()) {
			Yahtzee.print("There are no dice on the table. Drop dice from your hand or choose a score for your score card.");
			return;
		}
		rollsLeft--;
		for (Entry<String, Integer> d : diceOnTable.entrySet()) {
			diceOnTable.put(d.getKey(), rand.nextInt(1, 7));
		}
		
		if (scoreCard.possibleScoresToString(diceInHand, diceOnTable).length() != 0) {
			Yahtzee.print("You rolled: " + diceToString(diceOnTable) + "\n"
					+ "You can mark a score for: " + scoreCard.possibleScoresToString(diceInHand, diceOnTable) + "\n"
					+ "You have " + rollsLeft + " rolls left.");
		}
		else {
			Yahtzee.print("You rolled: " + diceToString(diceOnTable) + "\n"
					+ "You do not have any possible scores." + "\n"
					+ "You can mark zeros for: " + scoreCard.possibleZerosToString() + "\n"
					+ "You have " + rollsLeft + " rolls left.");
		}
		
	}

	/***
	 * Picks up specified dice from user input after a prompt to be picked up 
	 * and removed from table and put into the hand.
	 */
	private void hold() {
		//Cancel if havent rolled yet
		if (rollsLeft == 3) {
			Yahtzee.print("You do not have any dice to hold. Please roll first.");
			return;
		}
		
		//Handle no dice on table
		if (rollsLeft > 2) {
			Yahtzee.print("You currently do not have any dice to hold. Please roll first.");
			return;
		}
		//Handle if hand is full
		if (diceOnTable.isEmpty()) {
			Yahtzee.print("Your hand is already full. Drop dice from your hand or choose a score for your score card.");
			return;
		}
		
		//Handle picking up dice from table
		if (diceInHand.isEmpty()) {
			Yahtzee.print("You are not currently holding any dice.\n"
						+ "Which dice would you like to hold?");
		}
		else {
			Yahtzee.print("You are currently holding these dice: " + diceToString(diceInHand) + "\n"
						+ "Which dice would you like to hold?");
		}
		//Normal hold function
		tempInput = checkHoldInputValidity(Yahtzee.scnr.nextLine(), "hold");
		if (tempInput.equals("back")) {
			Yahtzee.print("You don't pick up any dice.");
		}
		else {
			String held = pickUpDie(tempInput);
			if (held.length() == 0) {
				Yahtzee.print("You don't pick up any new dice.");
				
			}
		}
		Yahtzee.print(tableStatus());
	}
	
	/***
	 * Picks up selected dice from the table and places it in hand, removing it form the table.
	 * Helper method for hold().
	 * 
	 * @param dice - dice to be picked from table and put into hand
	 * @return a String of dice picked up in format 'A B C ...'
	 */
	private String pickUpDie(String dice) {
		String diceL = dice.toLowerCase();
		String held = "";
		String[] dieSplit = diceL.split("\\s+");
		for (String d : dieSplit) {
			if (diceOnTable.containsKey(d)) {
				held = held + " " + d.toUpperCase();
				diceInHand.put(d,diceOnTable.get(d));
				diceOnTable.remove(d);
			}
		}
		return held;
	}

	/***
	 * Prompts player to select dice to drop from hand. 
	 * Selected dice are then removed from hand and placed on table.
	 */
	private void drop() {
		//Cancel if havent rolled yet
		if (rollsLeft == 3) {
			Yahtzee.print("You do not have any dice to drop. Please roll first.");
			return;
		}
		if (diceInHand.isEmpty()) {
			Yahtzee.print("You do not have any dice in your hand to drop.");
			return;
		}
		Yahtzee.print("You currently have these dice in your hand: " + diceToString(diceInHand) + "\n"
		+ "Which dice would you like to drop?");
		tempInput = checkHoldInputValidity(Yahtzee.scnr.nextLine(), "drop");
		if (tempInput.equals("back")) {
			Yahtzee.print("You don't drop any dice.");
		}
		else {
			String dropped = dropDieFromHand(tempInput);
			if (dropped.length() == 0) {
				Yahtzee.print("You don't drop any dice.");
			}
			else {
				Yahtzee.print("You dropped: " + dropped);
			}
			Yahtzee.print(tableStatus());
		}
	}

	/***
	 * Removes selected valid dice from player hand and puts them on the table. 
	 * Ignores duplicates and dice not in hand.
	 * Helper method for drop().
	 * 
	 * @param die - the dice to be dropped from player's hand
	 * @return a String of all dice dropped
	 */
	private String dropDieFromHand(String dice) {
		String diceL = dice.toLowerCase();
		String dropped = "";
		String[] dieSplit = diceL.split("\\s+");
		for (String d : dieSplit) {
			if (diceInHand.containsKey(d)) {
				dropped = dropped + " " + d.toUpperCase();
				diceOnTable.put(d,diceInHand.get(d));
				diceInHand.remove(d);
			}
		}
		return dropped;
	}

	private void score() {
		
		//Cancel if haven't rolled yet
		if (rollsLeft == 3) {
			Yahtzee.print("You do not have any dice to score. Please roll first.");
			return;
		}
 
		Yahtzee.print("Which score would like to mark for?");
		String[] exp = {"","ones", "twos", "threes", "fours", "fives", "sixes", "three of a kind", "3 of a kind", 
				"4 of a kind", "four of a kind", "full house", "small straight", "smll strght", "large straight",
				"lrg strght", "yahtzee", "chance", "back"};
		if (scoreCard.getScore("YAHTZEE") != null && scoreCard.getScore("YAHTZEE") != 0) {
			exp[0] = "yahtzee bonus";
		}
		tempInput = Yahtzee.checkInput(Yahtzee.scnr.nextLine(), exp, "the name of the score you wish to mark\n"
				+ "To mark a zero for score not listed above, enter the name of the score.\n"
				+ "If you've already scored a Yahtzee and would like to score another, enter 'Yahtzee Bonus'.\n"
				+ "To cancel scoring, enter 'back'.");
		
		//User-cancel score
		if (tempInput.equals("back")) {
			return;
		}
		
		//Verify score is in possible scores
		String inputScore = tempInput.toUpperCase();
		String[] possScores = scoreCard.findPossibleScores(diceInHand, diceOnTable);
		boolean inPossScores = false;
		for (String score : possScores) {
			if (inputScore.equals(score.toUpperCase())) {
				inPossScores = true;
			}
		}
		
		//Score is already marked
		if (scoreCard.getScore(inputScore) != null) {
			Yahtzee.print("You have already marked that score.\n"
					+ "You don't mark any new scores.");
			return;
		}
		
		
		//Mark zero for a score
		boolean scoredZero = false;
		if (!inPossScores && scoreCard.getScore(inputScore) == null) {
			Yahtzee.print("WARNING: You are about to mark a zero for this score.\n"
					+ "Would you like to proceed?");
			String[] exp2 = {"yes", "no"};
			tempInput = Yahtzee.checkInput(Yahtzee.scnr.nextLine(), exp2, "'yes' or 'no'. Do you want to mark a zero?");
			if (tempInput.equals("yes")) {
				scoreCard.score(inputScore, diceInHand, diceOnTable, true);
				scoredZero = true;
			}
			else {
				Yahtzee.print("You don't mark any new scores.");
				return;
			}
		}
		
		//Update score and reset players table
		if (!scoredZero) {
			scoreCard.score(inputScore.toUpperCase(), diceInHand, diceOnTable, false);
		}
		turnScored = true;
		rollsLeft = 3;
		diceOnTable.put("a", 0);
		diceOnTable.put("b", 0);
		diceOnTable.put("c", 0);
		diceOnTable.put("d", 0);
		diceOnTable.put("e", 0);
		diceInHand.clear();
		Yahtzee.print("You marked a score for: " + inputScore + "(" + scoreCard.getScore(inputScore) + ")");
		
		
		//Update score card window
		updateScoreWindow(inputScore.toUpperCase(), scoreCard.getScore(inputScore), name.toUpperCase());
		if (scoreCard.getScore("BONUS") != null) {
			updateScoreWindow("TOTAL SCORE", scoreCard.getScore("TOTAL SCORE"), name.toUpperCase());
			updateScoreWindow("BONUS", scoreCard.getScore("BONUS"), name.toUpperCase());
		}
		if (scoreCard.getScore("YAHTZEE BONUS") != null && scoreCard.getScore("YAHTZEE BONUS") == 0) {
			updateScoreWindow("YAHTZEE BONUS", scoreCard.getScore("YAHTZEE BONUS"), name.toUpperCase());
		}
	}

	/***
	 * After a single confirmation prompt, quits the game and exits the application.
	 * @throws InterruptedException 
	 */
	private void quit() throws InterruptedException {
		Yahtzee.print("Are you sure you want to quit? Your game will not be saved. Yes or no?");
		String[] exp = {"yes", "no"};
		tempInput = Yahtzee.checkInput(Yahtzee.scnr.nextLine(), exp, "'yes' or 'no'. Do you want to quit?");
		if (tempInput.equals("yes")) {
//			Yahtzee.print("Thank you for playing!");
//			Yahtzee.print("The game will close in 3 seconds.");
//			System.setOut(Yahtzee.debugLog);
//			System.out.println(scoreCard.toString());
//			System.setOut(Yahtzee.console);
//			TimeUnit.SECONDS.sleep(3);
//			System.exit(0);
			
			Yahtzee.quit();
		}
	}

	/***
	 * Checks an input for the hold and drop methods to be valid. If it is not considered valid, then it
	 * will loop continuously until a valid input is seen.
	 * Valid inputs: back, a, b, c, d, e
	 * 
	 * @param input - the String being checked for validity
	 * @param action - what the player is doing (either 'hold' or 'drop')
	 * @return A String considered a valid input
	 */
	private String checkHoldInputValidity(String input, String action) {
		boolean matches = false;
		String[] expected = {"back", "a", "b", "c", "d", "e"};
		System.setOut(Yahtzee.debugLog);
		System.out.println(input);
		System.setOut(Yahtzee.console);
		while (matches == false) {
			String inputL = input.toLowerCase();
			String[] inputSplit = inputL.split("\\s+");
			//matches = true;
			for (String i : inputSplit) {
				for (String e : expected) {
					if (e.equals(i)) {
						matches = true;
						break;
					}
					else {
						matches = false;
					}
				}
				if (!matches) {
					break;
				}
			}
			if (matches == false) {
				Yahtzee.print("Invalid input: please enter only the letter(s) of the dice you wish to " + action + " followed by a space (i.e. 'A C D').\n"
							+ "If you don't want to " + action + " any dice, please enter 'back'.\n"
							+ "Which dice would you like to " + action  + "?");
				input = Yahtzee.scnr.nextLine();
				System.setOut(Yahtzee.debugLog);
				System.out.println(input);
				System.setOut(Yahtzee.console);
			}
		}
		return input;
	}
	
	/**
	 * Gets turnScored.
	 * 
	 * @return turnScored value
	 */
	protected boolean getTurnScored() {
		return turnScored;
	}
	
	/**
	 * Switches the current value of turnScored.
	 */
	protected void updateTurnScored() {
		if (turnScored) {
			turnScored = false;
		}
		else {
			turnScored = true;
		}
	}

	/***
	 * Returns a String detailing information of the dice contained within a HashMap in the format:
	 * 		Dice [key] = [value] Dice [key] = [value] Dice [key] = [value]...
	 * 
	 * @param dice - HashMap of dice to create String from
	 * @return A String of all dice within the Hashmap
	 */
	private String diceToString(HashMap<String, Integer> dice) {
		String diceFormat = "";
		for (Entry<String, Integer> d : dice.entrySet()) {
			diceFormat = diceFormat + "Dice " + d.getKey().toUpperCase() + " = " + d.getValue() + " ";
		}
		return diceFormat;
	}

	/***
	 * Displays game score card window.
	 */
	private void scoreCard() {
		Yahtzee.scoreCard();
		
		
	}
	
	/***
	 * Prints rules and information about the game.
	 */
	private void help() {
		Yahtzee.print("Visit https://www.hasbro.com/common/instruct/yahtzee.pdf\n"
				+ "for a complete explanation of how to play Yahtzee.\n"
				+ "NOTE: This version of Yahtzee does not support Joker rules."
				+ "      In the event of a Yahtzee Bonus, only a flat 100 points for each bonus is awarded."
				+ "Enter 'commands' to see a complete list of actions available."
				+ "");
		
	}

	/***
	 * Returns a String of information regarding the current player's turn including:
	 * whose turn it is, the dice on the table, the dice being held, rolls left, and
	 * what scores are available.
	 * 
	 * @return a String of the current player's game status
	 */
	private String tableStatus() {
		String s = "TABLE STATUS\n"
				+ "--------------------\n"
				+ name + "'s turn\n"
				+ "Table: " + diceToString(diceOnTable) + "\n"
				+ "Holding: " + diceToString(diceInHand) + "\n"
				+ "Rolls left: " + rollsLeft + "\n"
				+ "You can mark a score for: " + scoreCard.possibleScoresToString(diceInHand, diceOnTable);
		return s;
	}

	/***
	 * Prints all valid commands and what they do.
	 */
	private void commands() {
		Yahtzee.print("COMMANDS\n"
					+ "--------------------\n"
					+ "commands - displays a list of all possible commands.\n"
					+ "help, h, rules - displays the rules and scoring system of the game.\n"
					+ "roll - Roll all dice not currently in your hand.\n"
					+ "hold - After each roll, you can select which dice you want to hold from the table.\n"
					+ "       Multiple dice can be chosen at a single time (i.e. 'A C D').\n"
					+ "drop - After each roll, you can select which dice you want to drop to the table.\n"
					+ "       Multiple dice can be chosen at a single time (i.e. 'A C D').\n"
					+ "score - After each roll, you can choose to mark a score on the scorecard.\n"
					+ "score card - Displays the current game scorecard.\n"
					+ "quit, q - Quits the game.\n"
					+ "\n"
					+ "Please enter a valid command.");
		
	}
	
	/***
	 * Returns the current scorecard.
	 * @return scorecard
	 */
	protected ScoreCard getScoreCard() {
		return scoreCard;
	}
	
	/***
	 * Updates the score card window to reflect any new values after a player
	 * scores.
	 * 
	 * @param scoreName - String name of score to be updated
	 * @param scoreVal - int value of the score to be marked
	 * @param name - name of the player whose score is to be updated
	 */
	protected void updateScoreWindow(String scoreName, int scoreVal, String name) {
		for (int i = 0; i < Yahtzee.scoreWindow.data.length; i++) {
			if (Yahtzee.scoreWindow.data[i][0].equals(scoreName)) {
				if (name.equals("PLAYER 1")) {
					Yahtzee.scoreWindow.table.setValueAt(Integer.toString(scoreVal), i, 1);
				}
				else {
					Yahtzee.scoreWindow.table.setValueAt(Integer.toString(scoreVal), i, 2);
				}
			}
		}
	}
}

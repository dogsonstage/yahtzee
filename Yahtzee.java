package main;

import java.io.FileNotFoundException;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Scanner;
import java.util.concurrent.TimeUnit;

/**
 * The main controller of the game, what the player "executes" to play
 */
public class Yahtzee {
	private static int numPlayers = 0;
	private static int numOfPlayerTurn = 1;
	protected static Scanner scnr = new Scanner(System.in);
	protected static PrintStream debugLog;
	protected static PrintStream console;
	private static GameController player1 = new GameController("Player 1");
	private static GameController player2 = new GameController("Player 2");
	private static String input;
	private static boolean player1FirstTurn = true;
	private static boolean player2FirstTurn = true;
	protected static ScoreCardWindow scoreWindow;

	public static void main(String[] args) throws FileNotFoundException, InterruptedException {
		/*
		 * STEPS TO PLAY GAME:
		 * 
		 * -Opening text and prompts
		 * -Loop prompt until quit
		 * -POSSIBLE: Consider two player mode (a new GameController for each)
		 */
		
		//Set up debug log
		debugLog = new PrintStream("debug_log.txt");
		console = System.out;
		
		
		
		
		//TODO: Make proper intro
		//Intro and player count
		scoreWindow = new ScoreCardWindow();
		print("Welcome to Yahtzee!\n"
				+ "If you are unfamiliar with the game, type 'help' to see how to play.\n"
				+ "To see a list of commands, type 'commands'.\n"
				+ "A separate window displays the score card for the current game,\n"
				+ "it's helpful to keep it off to the side to keep track of scores as you play.");
		print("How many players are there, 1 or 2?");
		String[] expNumPlayers = {"1", "2", "one", "two"};
		input = checkInput(scnr.nextLine(), expNumPlayers, "1 or 2. How many players?");
		if (input.equals("2") || input.equals("two")) {
			numPlayers = 2;
		}
		else {
			numPlayers = 1;
		}
		
		//Begin play
		while (true) {
			
			String[] expCommands = {"roll", "hold", "drop", "score", "help", "h", "rules", "quit", "q", "commands", "score card", "q", "quit"};
			if (player1FirstTurn && numPlayers == 2){
				print("Player 1 it's your turn!\nType 'roll' to begin.");
			}
			else {
				print("Type 'roll' to begin.");
			}
			while (!player1.getTurnScored()) {
				player1FirstTurn = false;
				print("What would you like to do?");
				input = checkInput(scnr.nextLine().toLowerCase(),expCommands, "a valid command. Enter 'commands' for a list of commands or 'help' for the rules of the game.");
				player1.nextMove(input);
			}
			
			//Switch to player 2 if applicable
			if (numPlayers == 2) {
				numOfPlayerTurn = 2;
				player2FirstTurn = true;
			}
			player1FirstTurn = true;
			player1.updateTurnScored();
			
			//Player 2's turn
			if (numPlayers == 2 && numOfPlayerTurn == 2) {
				if (player2FirstTurn) {
					print("Player 2 it's your turn!\nType 'roll' to begin.");
				}
				while (!player2.getTurnScored()) {
					player2FirstTurn = false;
					print("What would you like to do?");
					input = checkInput(scnr.nextLine().toLowerCase(),expCommands, "a valid command. Enter 'commands' for a list of commands or 'help' for the rules of the game.");
					player2.nextMove(input);
				}
				
				numOfPlayerTurn = 1;
				player2FirstTurn = true;
				player2.updateTurnScored();
				
				//TODO: Handle endgame (all scores entered)
			}
		}
	}
	
	/**
	 * Analyzes a user's input against an array of expected, valid Strings. If input is not valid,
	 * the console prints the specified message to prompt the user to input a valid command. Otherwise,
	 * returns the user's valid input in lowercase form.
	 * 
	 * @param input - player input to be checked
	 * @param expected - a String array of all valid inputs
	 * @param message - String to be printed describing what is considered valid input
	 * @return a String of valid input by the plower in lowercase
	 */
	protected static String checkInput(String input, String[] expected, String message) {
		boolean matches = false;
		System.setOut(debugLog);
		System.out.println(input);
		System.setOut(console);
		while (matches == false) {
			String inputL = input.toLowerCase();
			for (String e : expected) {
				if (e.equals(inputL)) {
					matches = true;
					break;
				}
			}
			//Invalid input
			if (matches == false) {
				print("Invalid input: please enter only " + message);
				input = scnr.nextLine();
				System.setOut(debugLog);
				System.out.println(input);
				System.setOut(console);
			}
		}
		
		return input.toLowerCase();
	}
	
	/***
	 * Prints a String in the desired format to both the console and debug_log.
	 * 
	 * @param message - String to be printed
	 */
	protected static void print(String message) {
		System.setOut(debugLog);
		System.out.println("\n" + message);
		System.setOut(console);
		System.out.println("\n" + message);
	}
	
	/***
	 * Prints an ending message, prints all player scorecards and score window to debug_log,
	 * then quits the game and exits the application.
	 * @throws InterruptedException
	 */
	protected static void quit() throws InterruptedException {
		print("Thank you for playing!");
		print("The game will close in 3 seconds.");
		
		//Save both scorecards to debug log
		System.setOut(Yahtzee.debugLog);
		System.out.println(player1.getScoreCard().toString("Player 1"));
		if (numPlayers == 2) {
			System.out.println(player2.getScoreCard().toString("Player 2"));
		}
		
		//Get all data from score window
		ArrayList<Object[]> data = new ArrayList<Object[]>();
		for (int i = 0; i < Yahtzee.scoreWindow.data.length; i++) {
			Object[] rowData = new String[3];
			for (int j = 0; j < Yahtzee.scoreWindow.data[0].length; j++) {
				rowData[j] = Yahtzee.scoreWindow.table.getValueAt(i, j);
			}
			data.add(rowData);
		}
		
		//Make printable format
		String s = "";
		for (Object[] o : data) {
			s = s + Arrays.toString(o) + "\n";
		}
		
		//Save scorecard window to debug log
		System.out.println(s);
		System.setOut(Yahtzee.console);
		
		//Quit app
		TimeUnit.SECONDS.sleep(3);
		System.exit(0);
	}
	
	/***
	 * Sets score card windows as visible in event of closure.
	 */
	protected static void scoreCard() {
		scoreWindow.frame.setVisible(true);
	}
}

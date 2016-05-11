package main;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Scanner;

public class Main {

	/**
	 * The List of known input options. The key is the mode such as "tian" or
	 * "default"
	 * 
	 * TODO I might need to sort these even further by both the mode and the
	 * type of input such as question
	 */
	private static HashMap<String, ArrayList<String>> input = new HashMap<String, ArrayList<String>>();

	/**
	 * The HashMap of all the possible outputs that can be chosen for each
	 * input. The key is the mode such as "tian" or "default"
	 */
	private static HashMap<String, ArrayList<String[]>> output = new HashMap<String, ArrayList<String[]>>();

	/**
	 * The Password used for moderator commands such as Learn
	 */
	private static String Password;

	/**
	 * The mode the system is in. options include "learn", "default", "tian",
	 * "math", "done"
	 * 
	 */
	private static String mode;

	/**
	 * 
	 */
	private static ArrayList<String> modes = new ArrayList<String>();

	/**
	 * Either "random" if a random response will be selected or "all" if all
	 * responses will be printed
	 */
	private static String outputMode = "random";

	/**
	 * The Scanner for input
	 */
	private static Scanner scn = new Scanner(System.in);

	/**
	 * 
	 */
	// private static Scanner know = new Scanner(new File("Knowledge.txt"));

	/**
	 * The main method that runs at start up
	 * 
	 * @param args
	 */
	public static void main(String args[]) throws IOException {

		// Load on startup
		Load();

		String[] replies;

		System.out.println("Hello");

		// Until the close command is received just keep taking input and
		// replying
		while (!mode.equals("done")) {

			// Take input
			String in = scn.nextLine();

			// Get the proper response
			replies = findReply(in);

			// Print the reply either randomly or simply all of them depending
			// on the mode
			if (replies == null) {

			} else if (outputMode.equals("random")) {// print random
				int rnd = (int) (replies.length * Math.random());
				System.out.println(replies[rnd]);
			} else { // print all
				for (String i : replies) {
					System.out.print(i + " ");
				}
				System.out.println();
			}
		}

		// Before shutting down save the information to the Knowledge file
		Save();
	}

	/**
	 * Loads the contents of the Knowledge text file into ArrayLists on start up
	 * 
	 * @throws FileNotFoundException
	 */
	private static void Load() throws FileNotFoundException {

		// Get list of modes
		String modesar[] = Find("Modes:","Knowledge.txt").split(" ");
		for (int i = 0; i < modesar.length; i++) {
			modes.add(modesar[i]);
		}

		// Get Password
		Password = Find("Password:","Knowledge.txt");

		// Get Mode
		mode = Find("Mode:","Knowledge.txt");

		// Get OutputMode
		outputMode = Find("OutputMode:","Knowledge.txt");

		// Download input and output
		for (String i : modes) {

			ArrayList<String> inputs = new ArrayList<String>();
			ArrayList<String[]> outputs = new ArrayList<String[]>();

			ArrayList<String> raw = FindAL(i,"Knowledge.txt");

			// Make sure there is data
			if (raw != null) {
				// Convert the raw data into two ArrayLists for inputs and
				// outputs
				for (int e = 0; e < raw.size(); e += 2) {
					inputs.add(raw.get(e));
					outputs.add(raw.get(e + 1).split("  "));
				}

				// Place the ArrayLists into their HashMaps
				input.put(i, inputs);
				output.put(i, outputs);
			}
		}
	}

	/**
	 * Searches through the Knowledge file for an arraylist based on the given
	 * key
	 * 
	 * @param key
	 *            The mode or string we are looking for the ArrayList under
	 * @param in 
	 * @return an ArrayList of everything underneath the key until it reaches
	 *         another mode
	 * @throws FileNotFoundException
	 */
	private static ArrayList<String> FindAL(String key, String in)
			throws FileNotFoundException {
		Scanner know = new Scanner(new File(in));

		// Becomes true when the key is found
		boolean found = false;

		// The output
		ArrayList<String> args = new ArrayList<String>();

		// Get to the starting point
		while (know.hasNext() && found == false) {
			if (know.nextLine().equals(key)) {
				found = true;
			}
		}

		// Get everything under the starting point until the next mode name
		// comes up
		while (know.hasNext()) {
			String line = know.nextLine();
			for (String i : modes) {
				if (line.equals(i)) {
					know.close();
					return args;
				}
			}
			args.add(line);
		}
		know.close();
		return args;
	}

	/**
	 * Finds the String next to the key
	 * 
	 * @param key
	 *            The Deliminator used to find what we want in the file
	 * @return the String after the key
	 * @throws FileNotFoundException
	 */
	private static String Find(String key, String file) throws FileNotFoundException {
		Scanner know = new Scanner(new File(file));

		String[] args;

		// search the entire Knowledge.txt until the first part is equal to the
		// key return the second part
		while (know.hasNext()) {
			args = know.nextLine().split(" ");
			if (args[0].equals(key)) {
				know.close();
				String out = "";
				for (int i = 1; i < args.length; i++) {
					out += args[i] + " ";
				}
				return out.substring(0, out.length() - 1);
			}
		}
		know.close();
		return null;
	}

	/**
	 * Saves the contents of the ArrayLists to the Knowledge text file
	 * 
	 * @throws FileNotFoundException
	 */
	private static void Save() throws FileNotFoundException {
		PrintWriter know = new PrintWriter(new File("Knowledge.txt"));
		know.print("Modes: ");
		for (String i : modes) {
			know.print(i + " ");
		}
		know.println();
		know.println("Password: " + Password);
		know.println("OutputMode: " + outputMode);
		if (!mode.equals("done")) {
			know.println("Mode: " + mode);
		} else {
			know.println("Mode: Default");
		}
		for (String i : modes) {
			know.println(i);
			try {
				ArrayList<String> ins = input.get(i);
				ArrayList<String[]> outs = output.get(i);
				for (int e = 0; e < ins.size(); e++) {
					know.println(ins.get(e));
					for (String j : outs.get(e)) {
						know.print(j + "  ");
					}
					know.println();
				}
			} catch (Exception ex) {

			}
		}
		know.close();
	}

	/**
	 * Searches through the input ArrayList to find the most similar question
	 * and return the output corresponding to it
	 * 
	 * @param in
	 *            The user inputed text
	 * @return A list of possible responses to the question
	 * @throws FileNotFoundExceptioná1
	 */
	private static String[] findReply(String in) throws FileNotFoundException {
		// Commands
		String out[];
		out = Commands(in);
		if (out != null) {
			return out;
		}

		// Learn mode
		out = new String[1];
		if (mode.equalsIgnoreCase("Learn")) {
			out[0] = Learn(in);
			return out;
		}

		// Math mode
		if (mode.equalsIgnoreCase("Math")) {
			out[0] = Math(in);
			return out;
		}

		// If William is in a talking mode go here
		int mostSimilar = getSimilar(mode, in);
		if (mostSimilar == -1) {
			out[0] = "My Data Processors Appear to be Malfunctioning";// TODO
																		// make
																		// sure
																		// this
																		// is
																		// the
																		// output
																		// i
																		// want
			return out;
		}
		out = output.get(mode).get(mostSimilar);
		return out;
	}

	/**
	 * Math Mode. Takes the input and calculates the answer if it can. It doesnt
	 * know parenthesis or order of operations
	 * 
	 * @param in
	 *            The input
	 * @return The answer
	 */
	private static String Math(String in) {
		// TODO this doesnt do pemdas! fix!
		int out = 0;
		try {
			ArrayList<String> chars = new ArrayList<String>();
			String[] raw = in.split("");

			// Create ArrayList of each character
			for (String i : raw) {
				chars.add(i);
			}

			// Find each number and operation
			ArrayList<String> nums = new ArrayList<String>();
			ArrayList<String> ops = new ArrayList<String>();
			for (int i = 0; i < chars.size(); i++) {
				if (chars.get(i).equals(" ")) {
					chars.remove(i);
					i--;
				} else if (isOp(chars.get(i))) {
					ops.add(chars.get(i));
					chars.remove(i);
					i--;
					String num = "";
					for (int e = 0; e <= i; e++) {
						num += chars.get(e);
						chars.remove(e);
						i--;
						e--;
					}
					nums.add(num);
				}
			}

			// Get the last number
			String num = "";
			for (String i : chars) {
				num += i;
			}
			nums.add(num);

			// Calculate
			out = Integer.parseInt(nums.get(0));
			for (int i = 1; i < nums.size(); i++) {
				int num1 = Integer.parseInt(nums.get(i));
				if (ops.get(i - 1).equals("+")) {
					out += num1;
				} else if (ops.get(i - 1).equals("-")) {
					out -= num1;
				} else if (ops.get(i - 1).equals("*")) {
					out *= num1;
				} else if (ops.get(i - 1).equals("/")) {
					out /= num1;
				}
			}

			return Integer.toString(out);
		} catch (Exception ex) {
			return "Operations accepted include (+,-,*,/)\nOrder of Operations Pack not Included";
		}
	}

	/**
	 * returns true if the String is a mathematical operation
	 * 
	 * @param string
	 * @return
	 */
	private static boolean isOp(String cha) {
		String[] ops = { "+", "-", "/", "*" };
		for (String i : ops) {
			if (cha.equals(i)) {
				return true;
			}
		}
		return false;
	}

	/**
	 * Finds the most similar input to the actual input
	 * 
	 * @param mode
	 *            The mode we are in
	 * @param in
	 *            The actual input
	 * @return The location of the most similar input in the ArrayList
	 */
	private static int getSimilar(String mode, String in) {
		int score = 0;
		int highestscore = 0;
		int highestloc = -1;
		ArrayList<String> data = input.get(mode);
		for (int i = 0; i < data.size(); i++) {
			if (in.equals(data.get(i))) {
				return i;
			} else {
				score = getScore(data.get(i), in);
				if (score > highestscore) {
					highestscore = score;
					highestloc = i;
				}
			}
		}
		return highestloc;
	}

	/**
	 * Calculates the "score" of the word compared to the input Subtract 1 for
	 * every 3 long difference in length
	 * 
	 * 
	 * @param string
	 *            what we are comparing the input to
	 * @param in
	 *            The input
	 * @return its score
	 */
	private static int getScore(String string, String in) {
		int score = 0;
		score -= Math.floor(Math.abs(in.length() - string.length()) / 3);
		int length = 0;
		if (string.length() < in.length()) {
			length = string.length();
		} else {
			length = in.length();
		}
		for (int i = 0; i < length - 1; i++) {
			if (in.substring(i, i + 1).equals(string.substring(i, i + 1))) {
				score++;
			}
		}
		return score;
	}

	/**
	 * Checks if the input is a command
	 * 
	 * @return If the input is a command it returns that commands response if it
	 *         isn't it returns null
	 */
	private static String[] Commands(String in) {
		String out[] = new String[1];
		// Close the program
		if (in.equalsIgnoreCase("Goodbye: " + Password)
				|| in.equalsIgnoreCase("Close: " + Password)) {
			mode = "done";
			out[0] = "Goodbye!";
			return out;
		}

		// Output a list of commands
		else if (in.equalsIgnoreCase("Command Help")) {
			System.out.println("*****************************************");
			System.out.println("Goodbye: Password or Close: Password");
			System.out.println("     Ends the program");
			for (String i : modes) {
				System.out.println(i + ": Password");
				System.out.println("     Puts William into " + i + " mode");
			}
			System.out.println("ChangePassword: Password");
			System.out.println("     Changes the password");
			System.out.println("Random: Password");
			System.out.println("     Changes output mode to random");
			System.out.println("All: Password");
			System.out.println("     Changes output mode to print all");
			out[0] = "*****************************************";
			return out;
		}
		// Change the Password
		else if (in.equalsIgnoreCase("ChangePassword: " + Password)) {
			// input password
			System.out.println("Please enter new password");
			String ps1 = scn.nextLine();
			// confirm it
			System.out.println("Confirm");
			String ps2 = scn.nextLine();
			// if they are the same then change password
			if (ps1.equals(ps2)) {
				Password = ps1;
				out[0] = "Password changed";
				return out;
			} else {
				out[0] = "Passwords do not match";
				return out;
			}

		}

		// Change to Random Output
		else if (in.equalsIgnoreCase("Random: " + Password)
				|| in.equalsIgnoreCase("PrintRandom: " + Password)) {
			outputMode = "random";
			out[0] = "OutputMode: Random";
			return out;
		}

		// Change to All Mode
		else if (in.equalsIgnoreCase("All: " + Password)
				|| in.equalsIgnoreCase("PrintAll: " + Password)) {
			outputMode = "all";
			out[0] = "OutputMode: All";
			return out;
		} else {
			// Command to change to all available modes
			for (String i : modes) {
				if (in.equalsIgnoreCase(i + ": " + Password)) {
					mode = i;
					out[0] = "Mode: " + i;
					return out;
				}
			}
		}

		return null;
	}

	/**
	 * Take input and download it to the Knowledge text file
	 * 
	 * @throws FileNotFoundException
	 */
	private static String Learn(String in) throws FileNotFoundException {
		// Find the Text file to be learned from
		String out = "";
		if (!istxt(in)) {
			out = "I'm sorry, the file " + in
					+ " has an invalid file extension.";
		} else {
			try {
				// Create the file to check if it can be found
				Scanner file = new Scanner(new File(in));

				// Get list of modes
				String modesar[] = Find("Modes:", in).split(" ");
				for (String i: modesar) {
					if (!modes.contains(i)) {
						modes.add(i);
					}
				}

				// Download input and output
				for (String i : modes) {

					ArrayList<String> inputs = new ArrayList<String>();
					ArrayList<String[]> outputs = new ArrayList<String[]>();

					ArrayList<String> raw = FindAL(i,in);

					// TODO this is all wrong! THIS IS what i have to fix
					// Make sure there is data
					if (raw != null) {
						// Convert the raw data into two ArrayLists for inputs
						// and
						// outputs
						for (int e = 0; e < raw.size(); e += 2) {
							inputs.add(raw.get(e));
							outputs.add(raw.get(e + 1).split("  "));
						}
						for(String j: inputs){
							System.out.println(j);
						}
						// Place the ArrayLists into their HashMaps
						ArrayList<String> tmpin = input.put(i, inputs);
						ArrayList<String[]> tmpout = output.put(i, outputs);
						boolean found = false;
						// Be sure we didn't just remove data
						if (tmpin != null) {
							// Merge the ArrayLists
							for (int j = 0; j < input.get(i).size(); j++) {
								found = false;
								for (int e = 0; e < tmpin.size(); e++) {
									if (tmpin.get(e)
											.equals(input.get(i).get(j))) {
										found = true;
										
										// Merge them
										String[] newout = new String[tmpout
												.get(e).length
												+ output.get(i).get(j).length];
										for (int k = 0; k < tmpout.get(e).length; k++) {
											newout[k] = tmpout.get(e)[k];
										}
										for (int k = tmpout.get(e).length; k < tmpout
												.get(e).length
												+ output.get(i).get(j).length; k++) {
											newout[k] = output.get(i).get(j)[k
													- (tmpout.get(e).length)];
											System.out.println(output.get(i).get(j)[k
													- (tmpout.get(e).length)] + "\n");
										}
										tmpout.remove(j);
										tmpout.add(j, newout);
									}
								}
								if (found == false) {
									tmpin.add(input.get(i).get(j));
									tmpout.add(output.get(i).get(j));
								}
							}
							output.put(i, tmpout);
							input.put(i, tmpin);
						}
					}
				}

				out = "Input incorporated into knowledge database";
			} catch (FileNotFoundException ex) {
				out = "File Not Found";
			}
		}
		Save();
		return out;
	}

	/**
	 * Determines if the last 4 characters in a String are ".txt"
	 * 
	 * @param input
	 *            The String to be checked
	 * @return a boolean of whether or not input ends in ".txt"
	 */
	private static boolean istxt(String input) {
		String ending = "";
		// Get the last 4 characters
		if (input.length() >= 4) {
			ending = input.substring(input.length() - 4);
		}
		// Check if they are .txt
		if (ending.equals(".txt")) {
			return true;
		}
		// if not return false
		return false;
	}
}

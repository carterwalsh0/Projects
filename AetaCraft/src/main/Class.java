package main;

import java.util.ArrayList;

public class Class {
	/**
	 * The name of the class
	 */
	private String name;
	/**
	 * List of the mcmmo skills that can be used
	 */
	private ArrayList<String> mcmmo;
	/**
	 * List of item IDs of items that can be crafted by this class
	 */
	private ArrayList<Integer> canCraft;
	/**
	 * List of item IDs of items that can be smelted by this class
	 */
	private ArrayList<Integer> canSmelt;
	/**
	 * The type of class. Each player can have one of each type of class making
	 * it possible to have as many classes as you want
	 */
	private ArrayList<String> type;
	/**
	 * Whether or not this class can enchant
	 */
	private boolean canEnchant;
	/**
	 * Whether or not this class can use an anvil
	 */
	private boolean canAnvil;
	/**
	 * Whether or not this class can brew potions
	 */
	private boolean canBrew;
	/**
	 * Whther or not this class can breed animals
	 */
	private boolean canBreed;
	/**
	 * Whether or not this class can plant crops
	 */
	private boolean canFarm;
	/**
	 * Whether or not this class get logs from trees
	 */
	private boolean canLogs;
	/**
	 * A list of the tools that this class can use
	 */
	private ArrayList<Integer> tools;
	/**
	 * A list of the weapons that this class can use
	 */
	private ArrayList<Integer> weapons;
	/**
	 * The list of the pieces of armor allowed
	 */
	private ArrayList<Integer> armor;
	/**
	 * The message outputed on command /class <classname>
	 */
	private String description;

	/**
	 * 
	 * @param name
	 *            The name of the class
	 * @param mcmmo
	 *            The list of mcmmo skills that the class can use
	 * @param canCraft
	 *            A list of item IDs of the items the class can craft
	 * @param canSmelt
	 *            A list of item IDs of the items the class can smelt
	 * @param canBreak
	 *            A list of item IDs of the items the class can break for drops
	 * @param type
	 *            A list determining the type of the class
	 * @param canEnchant
	 *            A boolean whether or not this class can enchant
	 * @param canAnvil
	 *            A boolean whether or not this class can use an anvil
	 * @param canBrew
	 *            A boolean whether or not this class can brew potion
	 * @param canBreed
	 *            A boolean whether or not this class can breed animals and tame
	 *            creatures
	 * @param tools
	 *            A list of item IDs of the items the class can use as tools
	 *            (none tools dont matter)
	 * @param weapons
	 *            A list of item IDs of the items the class can use as weapons
	 *            (none weapons dont matter)
	 * @param armor
	 *            A list of item IDs of the items the class can use for armor
	 *            (pumpkins assumed to be always allowed)
	 * @param description
	 *            The message to be outputed on command /class <classname>
	 */
	public Class(String name, ArrayList<String> mcmmo,
			ArrayList<Integer> canCraft, ArrayList<Integer> canSmelt,
			ArrayList<String> type, boolean canEnchant, boolean canAnvil,
			boolean canBrew, boolean canFarm, boolean canBreed,
			boolean canLogs, ArrayList<Integer> tools,
			ArrayList<Integer> weapons, ArrayList<Integer> armor,
			String description) {
		this.name = name;
		this.mcmmo = mcmmo;
		this.canCraft = canCraft;
		this.canSmelt = canSmelt;
		this.type = type;
		this.canEnchant = canEnchant;
		this.canAnvil = canAnvil;
		this.canBrew = canBrew;
		this.canBreed = canBreed;
		this.canFarm = canFarm;
		this.canLogs = canLogs;
		this.tools = tools;
		this.weapons = weapons;
		this.armor = armor;
		this.description = description;
	}

	/**
	 * Sends a string of the name and type of the class
	 */
	public String toString() {
		String out = "\nThe " + name + ": \n";
		out += "Type: " + ALtoStr(type);
		return out;
	}

	/**
	 * Returns a String containing the lists of Craftables, Smeltables,
	 * Breakables,Tools,Weapons, and Armor of the class as well as whether it
	 * can enchant, use anvils, brew potions, or breed animals
	 */
	public String toStringFull() {
		String out = "\n";
		out += "The " + name + "\n";
		out += "Description: " + description + "\n";
		out += "Type: " + ALtoStr(type) + "\n";
		out += "Craftables: ";
		for (int i : canCraft) {
			out += i + " ";
		}
		out += "\n";
		out += "Smeltabless: ";
		for (int i : canSmelt) {
			out += i + " ";
		}
		out += "\n";
		out += "Tools: ";
		for (int i : tools) {
			out += i + " ";
		}
		out += "\n";
		out += "Weapons: ";
		for (int i : weapons) {
			out += i + " ";
		}
		out += "\n";
		out += "Armor: ";
		for (int i : armor) {
			out += i + " ";
		}
		out += "\n";
		out += "canEnchant: " + canEnchant + "\n";
		out += "canAnvil: " + canAnvil + "\n";
		out += "canBrew: " + canBrew + "\n";
		out += "canBreed: " + canBreed + "\n";
		out += "canFarm: " + canFarm + "\n";
		out += "canLogs: " + canLogs + "\n";
		return out;
	}

	private String ALtoStr(ArrayList<String> type2) {
		String out = "";
		for (String i : type2) {
			out += i + " ";
		}
		return out;
	}

	public String getName() {
		return name;
	}

	public String getDescription() {
		return description;
	}

	public ArrayList<Integer> getCraftables() {
		return canCraft;
	}

	public ArrayList<Integer> getSmeltables() {
		return canSmelt;
	}

	public ArrayList<Integer> getTools() {
		return tools;
	}

	public ArrayList<Integer> getWeapons() {
		return weapons;
	}

	public ArrayList<Integer> getArmor() {
		return armor;
	}

	public boolean canEnchant() {
		return canEnchant;
	}

	public boolean canAnvil() {
		return canAnvil;
	}

	public boolean canBrew() {
		return canBrew;
	}

	public boolean canBreed() {
		return canBreed;
	}

	public boolean canFarm() {
		return canFarm;
	}

	public boolean canLogs() {
		return canLogs;
	}

	public ArrayList<String> getPerms() {
		return mcmmo;
	}

	public ArrayList<String> getType() {
		return type;
	}

}

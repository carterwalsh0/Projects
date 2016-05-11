package main;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.permissions.PermissionAttachment;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;

/**
 * 
 * @author Car4p17
 * @version 1.00
 */
public final class Main extends JavaPlugin {
	/**
	 * An arrayList of every players name that is currently untaming
	 */
	public static ArrayList<String> untame = new ArrayList<String>();
	/**
	 * An arrayList of every players name that is currently praying
	 */
	public static ArrayList<String> praying = new ArrayList<String>();
	/**
	 * A map of all the players time until next class change
	 */
	public static HashMap<String, Integer> ctime = new HashMap<String, Integer>();
	/**
	 * A map of all the players time until next prayer use
	 */
	public static HashMap<String, Integer> ptime = new HashMap<String, Integer>();
	/**
	 * A Map of all of the players permissions
	 */
	public static HashMap<UUID, PermissionAttachment> perms = new HashMap<UUID, PermissionAttachment>();
	/**
	 * An arrayList of the classes of every player online
	 */
	public static HashMap<String, ArrayList<Class>> players = new HashMap<String, ArrayList<Class>>();
	/**
	 * A map of the classes
	 */
	public static HashMap<String, Class> classes = new HashMap<String, Class>();
	/**
	 * An arraylist of the classes
	 */
	public static ArrayList<Class> classesAL = new ArrayList<Class>();
	/**
	 * An arrayList of the Smeltables that can be added to a class with the word
	 * general
	 */
	private static ArrayList<Integer> generalSmeltables = new ArrayList<Integer>();
	/**
	 * An arrayList of the Craftables that can be added to a class with the word
	 * general
	 */
	private static ArrayList<Integer> generalCraftables = new ArrayList<Integer>();
	/**
	 * An arrayList of the MCMMO skills that can be added to a class with the
	 * word general
	 */
	private static ArrayList<String> generalPerms = new ArrayList<String>();

	/**
	 * Called when plugin starts creates the listeners,fills all current players
	 * into the permissions hashmap, as well as create the classes, put the
	 * online players in the right one, and create the general lists
	 */
	public void onEnable() {
		reloadConfig();
		// Listeners
		getServer().getPluginManager().registerEvents(
				new CraftingListener(this), this);
		getServer().getPluginManager().registerEvents(
				new PlayerJoinListener(this), this);
		getServer().getPluginManager().registerEvents(
				new PlayerLeaveListener(this), this);
		getServer().getPluginManager().registerEvents(new ToolListener(this),
				this);
		getServer().getPluginManager().registerEvents(new ArmorListener(this),
				this);
		getServer().getPluginManager().registerEvents(new AttackListener(this),
				this);
		getServer().getPluginManager().registerEvents(new AnimalListener(this),
				this);
		getServer().getPluginManager().registerEvents(new PistonListener(this),
				this);
		// getServer().getPluginManager().registerEvents(
		// new NameTagListener(this), this);
		// Add all online players to map
		for (Player e : getServer().getOnlinePlayers()) {
			fillPerms(e);
		}
		// fill the general lists
		fillGSmeltables();
		fillGCraftables();
		fillGPerms();
		// getLogger().info("***Generals Filled***");
		// Create the Classes
		createClasses();
		// getLogger().info("***Classes Created***");
		// Read players from config
		fillPlayers();
		// set perms
		for (Player e : getServer().getOnlinePlayers())
			setPerms(e);
		// Runs every second
		new BukkitRunnable() {
			@Override
			public void run() {
				if (getConfig().getString("PlayerNames") != null) {
					// loops through all of the players
					for (String i : getConfig().getString("PlayerNames").split(
							" ")) {
						// makes sure that the player has a time in the config
						// and that it is in ctime
						if (ctime.get(i) == null) {
							if (getConfig().getString("Players." + i + ".Time") == null)
								getConfig().set("Players." + i + ".Time", 0);
							ctime.put(
									i,
									Integer.parseInt(getConfig().getString(
											"Players." + i + ".Time")));
						}
						// turns everyones time between class changes(ctime)
						// down by one unless it is 0
						int tmp = ctime.get(i);
						if (tmp != 0) {
							ctime.remove(i);
							ctime.put(i, tmp - 1);
						}
						// if the player isnt in the pray timer(ptime) then add
						// them with a start of 0
						if (ptime.get(i) == null) {
							ptime.put(i, 0);
						}
						tmp = ptime.get(i);
						// if they prayed 20 secs ago then remove them from the
						// praying arraylist
						if (tmp == 100) {
							if (praying.contains(i)) {
								praying.remove(i);
							}
						}
						// ticks the players ptime down 1
						if (tmp != 0) {
							ptime.remove(i);
							ptime.put(i, tmp - 1);
						}
					}
				}

			}
		}.runTaskTimer(this, 20, 20);
		// Runs every tic
		new BukkitRunnable() {
			@SuppressWarnings("deprecation")
			@Override
			public void run() {
				for (String i : getConfig().getString("PlayerNames").split(" ")) {
					Player player = getServer().getPlayer(i);
					// if the player prayed less than 20 seconds ago
					if (praying.contains(i)) {
						// valdor removes the hunger debuff
						if (perms.get(player.getUniqueId()).getPermissible()
								.hasPermission("aetacraft.valdor")) {
							try {
								player.removePotionEffect(PotionEffectType.HUNGER);
							} catch (Exception ex) {

							}
							// armvard gives water breathing
						} else if (perms.get(player.getUniqueId())
								.getPermissible()
								.hasPermission("aetacraft.armvard")) {
							player.addPotionEffect(new PotionEffect(
									PotionEffectType.WATER_BREATHING, 2, 1));
							// erthis gives night vision
						} else if (perms.get(player.getUniqueId())
								.getPermissible()
								.hasPermission("aetacraft.erthis")) {
							player.addPotionEffect(new PotionEffect(
									PotionEffectType.NIGHT_VISION, 2, 1));
						}
					}
				}
			}
		}.runTaskTimer(this, 1, 1);
	}

	/**
	 * sets the players perms
	 */
	public static void setPerms(Player player) {
		// reset to default perms
		clearPerms(player);
		// get the list of perms to set
		ArrayList<String> tmpPerms = new ArrayList<String>();
		for (Class e : players.get(player.getName())) {
			for (String j : e.getPerms()) {
				if (j.equalsIgnoreCase("general")) {
					for (String i : generalPerms) {
						if (!tmpPerms.contains(i)) {
							if (!(i.startsWith("-") && tmpPerms.contains(i
									.substring(1)))) {
								tmpPerms.add(i);
							}

						}
					}
				} else if (!tmpPerms.contains(j)) {
					if (!(j.startsWith("-") && tmpPerms
							.contains(j.substring(1)))) {
						tmpPerms.add(j);
					}

				}
			}
		}
		// set the perms
		for (String j : tmpPerms) {
			if (j.startsWith("-")) {
				perms.get(player.getUniqueId()).setPermission(j, false);
			} else {
				perms.get(player.getUniqueId()).setPermission(j, true);
			}
		}
	}

	/**
	 * sets a whole bunch of perms false
	 */
	public static void clearPerms(Player player) {
		perms.get(player.getUniqueId()).setPermission("mcmmo.*", true);
		perms.get(player.getUniqueId()).setPermission("mcmmo.*", false);
		perms.get(player.getUniqueId())
				.setPermission("aetacraft.erthis", false);
		perms.get(player.getUniqueId())
				.setPermission("aetacraft.valdor", false);
		perms.get(player.getUniqueId()).setPermission("aetacraft.armvard",
				false);
		perms.get(player.getUniqueId()).setPermission("aetacraft.faear", false);
		perms.get(player.getUniqueId()).setPermission("aetacraft.lupavio",
				false);
		perms.get(player.getUniqueId())
				.setPermission("aetacraft.vulcan", false);
	}

	/**
	 * gets and sets the classes and time between class changes of each player
	 * from the config.yml
	 */
	private void fillPlayers() {
		for (Player e : getServer().getOnlinePlayers()) {
			if (getConfig().getString("Players." + e.getName()) == null) {
				getConfig().set("Players." + e.getName() + ".Classes",
						"Peasant");
				getConfig().set("Players." + e.getName() + ".Time", "0");
				saveConfig();
			}
			players.put(e.getName(), getClasses(e));
			ctime.put(
					e.getName(),
					Integer.parseInt(getConfig().getString(
							"Players." + e.getName() + ".Time")));
		}
	}

	/**
	 * returns an arraylist of the classes a player has from the config.yml for
	 * startup
	 */
	private ArrayList<Class> getClasses(Player player) {
		ArrayList<Class> tmp = new ArrayList<Class>();
		String lst = getConfig().getString(
				"Players." + player.getName() + ".Classes");
		for (String i : lst.split(" ")) {
			tmp.add(classes.get(i));
		}
		return tmp;
	}

	/**
	 * Creates the classes from the config.yml
	 */
	private void createClasses() {
		for (int i = 1; i <= getConfig().getInt("NumberofClasses"); i++) {
			List<String> classData = getConfig().getStringList("Class." + i);
			String name = classData.get(0);
			ArrayList<String> mcmmo = toALStr(classData.get(1));
			ArrayList<Integer> canCraft = toALInt(classData.get(2),
					"craftables");
			ArrayList<Integer> canSmelt = toALInt(classData.get(3),
					"smeltables");
			ArrayList<String> type = toALStr(classData.get(4));
			boolean canEnchant = Boolean.parseBoolean(classData.get(5));
			boolean canAnvil = Boolean.parseBoolean(classData.get(6));
			boolean canBrew = Boolean.parseBoolean(classData.get(7));
			boolean canBreed = Boolean.parseBoolean(classData.get(8));
			boolean canFarm = Boolean.parseBoolean(classData.get(9));
			boolean canLogs = Boolean.parseBoolean(classData.get(10));
			ArrayList<Integer> tools = toALInt(classData.get(11), "tools");
			ArrayList<Integer> weapons = toALInt(classData.get(12), "weapons");
			ArrayList<Integer> armor = toALArmor(classData.get(13));
			String description = classData.get(14);
			classesAL.add(new Class(name, mcmmo, canCraft, canSmelt, type,
					canEnchant, canAnvil, canBrew, canBreed, canFarm, canLogs,
					tools, weapons, armor, description));
			// create the class in the classes hashmap
			classes.put(name, classesAL.get(classesAL.size() - 1));

			// getLogger().info(classes.get(name).toStringFull());
		}

	}

	/**
	 * converts the string of the highest tier armor allowed into a arrayList of
	 * all of the IDs of the items allowed
	 */
	private ArrayList<Integer> toALArmor(String string) {
		boolean found = false;
		ArrayList<Integer> tmp = new ArrayList<Integer>();
		tmp.add(86); // pumpkin
		if (string.equalsIgnoreCase("diamond")) {
			found = true;
			tmp.add(310);
			tmp.add(311);
			tmp.add(312);
			tmp.add(313);
		}
		if (string.equalsIgnoreCase("Iron") || found) {
			found = true;
			tmp.add(306);
			tmp.add(307);
			tmp.add(308);
			tmp.add(309);
		}
		if (string.equalsIgnoreCase("gold") || found) {
			found = true;
			tmp.add(314);
			tmp.add(315);
			tmp.add(316);
			tmp.add(317);
		}
		if (string.equalsIgnoreCase("chain") || found) {
			found = true;
			tmp.add(302);
			tmp.add(303);
			tmp.add(304);
			tmp.add(305);
		}
		// leather
		tmp.add(298);
		tmp.add(299);
		tmp.add(300);
		tmp.add(301);
		return tmp;
	}

	/**
	 * Converts a string into a ArrayList<String> spliting around " "
	 */
	private ArrayList<String> toALStr(String string) {
		ArrayList<String> tmp = new ArrayList<String>();
		for (String e : string.split(" ")) {
			tmp.add(e);
		}
		return tmp;
	}

	/**
	 * Converts a string of int's into a ArrayList<Integer> has many different
	 * keywords for ease of input
	 */
	private ArrayList<Integer> toALInt(String string, String key) {
		ArrayList<Integer> tmp = new ArrayList<Integer>();
		for (String e : string.split(" ")) {
			if (isInt(e)) { // if its not a keyword
				tmp.add(Integer.parseInt(e));
			} else if (e.equalsIgnoreCase("bsword")) {
				tmp.add(268);
				tmp.add(272);
			} else if (e.equalsIgnoreCase("bpic")) {
				tmp.add(270);
				tmp.add(274);
			} else if (e.equalsIgnoreCase("baxe")) {
				tmp.add(271);
				tmp.add(275);
			} else if (e.equalsIgnoreCase("bhoe")) {
				tmp.add(290);
				tmp.add(291);
			} else if (e.equalsIgnoreCase("bshovel")) {
				tmp.add(269);
				tmp.add(273);
			} else if (e.equalsIgnoreCase("basic")) {
				tmp.add(268);
				tmp.add(272);
				tmp.add(270);
				tmp.add(274);
				tmp.add(271);
				tmp.add(275);
				tmp.add(269);
				tmp.add(273);
			} else if (e.equalsIgnoreCase("all")) {
				for (int i = 0; i < 500; i++) {
					tmp.add(i);
				}
			} else if (e.equalsIgnoreCase("sword")) {
				tmp.add(268);
				tmp.add(272);
				tmp.add(267);
				tmp.add(283);
				tmp.add(276);
			} else if (e.equalsIgnoreCase("bow")) {
				tmp.add(261);
			} else if (e.equalsIgnoreCase("pic")) {
				tmp.add(257);
				tmp.add(270);
				tmp.add(274);
				tmp.add(278);
				tmp.add(285);
			} else if (e.equalsIgnoreCase("axe")) {
				tmp.add(258);
				tmp.add(271);
				tmp.add(275);
				tmp.add(279);
				tmp.add(286);
			} else if (e.equalsIgnoreCase("hoe")) {
				tmp.add(290);
				tmp.add(291);
				tmp.add(292);
				tmp.add(293);
				tmp.add(294);
			} else if (e.equalsIgnoreCase("shovel")) {
				tmp.add(256);
				tmp.add(269);
				tmp.add(273);
				tmp.add(277);
				tmp.add(284);
			} else if (e.equalsIgnoreCase("shears")) {
				tmp.add(359);
			} else if (e.equalsIgnoreCase("fishingrod")) {
				tmp.add(346);
			}
			if (e.equalsIgnoreCase("general")) {
				if (key.equalsIgnoreCase("craftables")) {
					for (int i : generalCraftables) {
						tmp.add(i);
					}
				} else if (key.equalsIgnoreCase("smeltables")) {
					for (int i : generalSmeltables) {
						tmp.add(i);
					}
				} else {
					tmp.add(268);
					tmp.add(272);
					tmp.add(270);
					tmp.add(274);
					tmp.add(271);
					tmp.add(275);
					tmp.add(269);
					tmp.add(273);
				}
			}
		}
		return tmp;
	}

	/**
	 * fills the ArrayList generalMCMMO from config.yml
	 */
	private void fillGPerms() {
		for (String e : getConfig().getString("GeneralMCMMO").split(" ")) {
			generalPerms.add(e);
		}
	}

	/**
	 * fills the ArrayList generalCraftables from config.yml
	 */
	private void fillGCraftables() {
		generalCraftables = toALInt(getConfig().getString("GeneralCraftables"),
				"general");
	}

	/**
	 * fills the ArrayList generalSmeltables from config.yml
	 */
	private void fillGSmeltables() {
		generalSmeltables = toALInt(getConfig().getString("GeneralSmeltables"),
				"general");
	}

	/**
	 * Ensures that the inputed string can be converted to an integer
	 * 
	 * @param str
	 *            the string to be tested
	 * @return if it is an integer or not
	 */
	private boolean isInt(String str) {
		for (int i = 0; i < 10; i++) {
			if (str.startsWith("" + i)) {
				return true;
			}
		}
		return false;
	}

	/**
	 * Called when plugin is disabled or server stops Removes everyone from the
	 * HashMap
	 */
	public void onDisable() {
		perms.clear();
		saveplayers();
		players.clear();
	}

	/**
	 * saves the players to the config on disable
	 */
	private void saveplayers() {
		for (Player i : getServer().getOnlinePlayers()) {
			getConfig().set("Players." + i.getName() + ".Classes",
					ALtoStr(players.get(i.getName())));
			getConfig().set("Players." + i.getName() + ".Time",
					ctime.get(i.getName()));
		}
		saveConfig();
	}

	/**
	 * converts the class arraylist into a string seperated by " "'s
	 */
	static String ALtoStr(ArrayList<Class> arrayList) {
		if (arrayList != null) {
			String tmp = "";
			for (Class i : arrayList) {
				tmp += i.getName() + " ";
			}
			return tmp;
		}
		return "";
	}

	/**
	 * Puts the player into the hashmap
	 * 
	 * @param player
	 *            the player being added
	 */
	public void fillPerms(Player player) {
		perms.put(player.getUniqueId(), player.addAttachment(this));
	}

	@SuppressWarnings("deprecation")
	public boolean onCommand(CommandSender sender, Command cmd, String label,
			String[] args) {
		if (label.equalsIgnoreCase("class")) {
			if (args.length >= 3) {
				if (args[0].equalsIgnoreCase("set")
						&& (sender.getName().equalsIgnoreCase(args[1]) || isMod((Player) sender))) {
					if (ctime.get(((Player) sender).getName()) == 0
							|| isMod((Player) sender)) {
						ArrayList<Class> classeses = new ArrayList<Class>();
						for (int i = 2; i < args.length; i++) {
							Class tmp = classes.get(toCase(args[i]));
							if (tmp != null) {
								if ((tmp.equals(classes.get("Mod")) || tmp
										.equals(classes.get("Peasant")))
										&& !isMod((Player) sender)) {
									sender.sendMessage("What do you think you are doing?");
									return true;
								}
								classeses.add(tmp);

							} else {
								sender.sendMessage("The inputed classes were not recognized");
								return true;
							}
						}
						if (areSame(classeses)) {
							sender.sendMessage("You can not change your class for another "
									+ toTime(ctime.get(((Player) sender)
											.getName())));
							return true;
						}
						if (contains(getConfig().getString("PlayerNames")
								.split(" "), sender.getName())) {
							ArrayList<String> tmp = new ArrayList<String>();
							for (int i = 2; i < args.length; i++) {
								for (String e : classes.get(toCase(args[i]))
										.getType()) {
									for (String j : tmp) {
										if (j.equalsIgnoreCase(e)) {
											sender.sendMessage("The inputed classes are the same type");
											return true;
										}
									}
									tmp.add(e);
								}
							}
							if (classes.get(getConfig()
									.getString("PlayerNames").split(" ")[0]) != classes
									.get("Peasant")
									|| isMod((Player) sender)) {
								changeClass(args[1], classeses);
								ctime.remove(args[1]);
								ctime.put(args[1], 604800);// This
															// is
															// where
															// time
															// in
															// seconds
															// go
															// 1
															// week
															// =
															// 604800
								setPerms((Player) sender);
								if (sender.getName().equals(args[1])) {
									for (int i = 2; i < args.length; i++) {
										if (classes.get(toCase(args[i]))
												.getType().get(0)
												.equalsIgnoreCase("Religion")) {
											sender.sendMessage("You have changed your religion to "
													+ args[i]);
										} else {
											sender.sendMessage("You have changed your class to "
													+ args[i]);
										}
									}
								} else {
									for (int i = 2; i < args.length; i++) {
										sender.sendMessage("You have changed "
												+ args[1] + "'s class to "
												+ args[i]);
										try {
											getServer()
													.getPlayer(args[1])
													.sendMessage(
															"Your class has been changed to "
																	+ args[i]
																	+ " by "
																	+ sender.getName());
										} catch (Exception ex) {

										}
									}
								}
								getServer()
										.getPlayer(args[1])
										.kickPlayer(
												"Please relog for your class to be updated");
								return true;

							} else {
								sender.sendMessage("Your request for a class change has been sent to the server admins");
								String out = args[1]
										+ " has requested to be a ";
								for (int e = 2; e < args.length; e++) {
									out += args[e] + " ";
								}
								for (Player i : getServer().getOnlinePlayers()) {
									if (isMod(i)) {
										i.sendMessage(out);
									}
								}
								return true;
							}

						} else {
							sender.sendMessage("The inputed player is not online");
							return true;
						}
					} else {
						sender.sendMessage("You can not change your class for another "
								+ toTime(ctime.get(((Player) sender).getName())));
						return true;
					}
				} else if (args[0].equalsIgnoreCase("set")
						&& !sender.getName().equalsIgnoreCase(args[1])
						&& (!sender.isOp() || !isMod((Player) sender))) {
					sender.sendMessage("You can not change another players class");
					return true;
				} else if (args[0].equalsIgnoreCase("Sort")) {
					if ((!args[1].equalsIgnoreCase(sender.getName()) || (sender
							.isOp() || isMod((Player) sender)))) {
						ArrayList<String> tmp = new ArrayList<String>();
						String className;
						for (int i = 2; i < args.length; i++) {
							className = getClassName(args[i], args[1]);
							if (!className.equalsIgnoreCase("INVALID")) {
								tmp.add(className);
							} else {
								sender.sendMessage("The inputed class or type was not recognized");
								return true;
							}
						}
						sortClasses(args[1], tmp);
						if (!args[1].equals(sender.getName())) {
							sender.sendMessage("Sort Complete");
							Player player = getServer().getPlayer(args[1]);
							if (player != null) {
								player.sendMessage("Your classes have been reorganized by "
										+ sender.getName());
							}
							return true;
						} else {
							sender.sendMessage("Sort Complete");
							return true;
						}
					} else {
						sender.sendMessage("You can not change another players classes");
						return true;
					}
				}
			} else if (args.length == 3
					&& (sender.isOp() || isMod((Player) sender))) {
				if (args[0].equalsIgnoreCase("remove")
						|| args[0].equalsIgnoreCase("delete")) {
					if (getServer().getPlayer(args[1]) != null) {
						String className = getClassName(args[2], args[1]);
						if (!className.equalsIgnoreCase("INVALID")) {
							removeClass(getServer().getPlayer(args[1]),
									className);
							sender.sendMessage("You have removed " + className
									+ " from " + args[1]);
							getServer().getPlayer(args[1]).sendMessage(
									className + " has been removed by "
											+ sender.getName());
							return true;
						} else {
							sender.sendMessage("The inputed class or type was not recognized");
							return true;
						}
					} else {
						sender.sendMessage("The inputed player is not online");
						return true;
					}
				}
			} else if (args.length == 2) {
				if (args[0].equalsIgnoreCase("list")) {
					String out = "";
					out += args[1] + ": ";
					out += getConfig().getString(
							"Players." + args[1] + ".Classes");
					if (out.equals(args[1] + ": null")) {
						sender.sendMessage("The inputed player doesn't exist.");
					} else
						sender.sendMessage(out);
					return true;
				}
			} else if (args.length == 1) {
				if (args[0].equalsIgnoreCase("list")) {
					sender.sendMessage(toStrClasses());
					return true;
				} else {
					if (classes.get(args[0]) != null) {
						sender.sendMessage(toCase(args[0]) + ": "
								+ classes.get(toCase(args[0])).getDescription());
						return true;
					} else {
						sender.sendMessage("The inputed class was not recognized");
						return true;
					}
				}
			}
			sender.sendMessage("Available Commands: \n  /class set <name> <class> ...\n  /class list <name>\n  /class list\n  /class <class>\n  /class remove <name> <type>\n  /class remove <name> <class>\n  /class sort <name> <class> ...\n  /class sort <name> <type> ...");
			return true;
		} else if (label.equalsIgnoreCase("untame")) {
			if (args.length == 0) {
				if (untame.contains(sender.getName())) {
					untame.remove(sender.getName());
					sender.sendMessage("Untame Mode: Off");
				} else {
					untame.add(sender.getName());
					sender.sendMessage("Untame Mode: On");
				}
				return true;
			} else {
				sender.sendMessage("The command is /untame");
				return true;
			}
		} else if (label.equalsIgnoreCase("pray")) {
			if (ptime.get(sender.getName()) == 0) {
				if (perms.get(((Player) sender).getUniqueId()).getPermissible()
						.hasPermission("aetacraft.faear")) {
					((Player) sender).getWorld().spawnEntity(
							((Player) sender).getLocation(), EntityType.WOLF);
				} else if (perms.get(((Player) sender).getUniqueId())
						.getPermissible().hasPermission("aetacraft.lupavio")) {
					((Player) sender).addPotionEffect(new PotionEffect(
							PotionEffectType.ABSORPTION, 40, 2));
				} else {
					praying.add(sender.getName());
				}
				sender.sendMessage("Your prayer has been heard");
				ptime.remove(sender.getName());
				ptime.put(sender.getName(), 120);
				return true;
			} else {
				sender.sendMessage("You should wait atleast "
						+ toTime(ptime.get(sender.getName()))
						+ " before calling for aid again");
				return true;
			}
		} else if (label.equalsIgnoreCase("trade")) {
			if (args.length == 3) {
				if (args[0].equalsIgnoreCase("xp")) {
					if (getServer().getPlayer(args[1]) != null) {
						if (((Player) sender).getTotalExperience()
								- Integer.parseInt(args[2]) > 0) {
							if (Integer.parseInt(args[2]) > 0) {
								getServer()
										.getPlayer(sender.getName())
										.giveExp(-1 * Integer.parseInt(args[2]));

								getServer().getPlayer(args[1]).giveExp(
										Integer.parseInt(args[2]));
								sender.sendMessage("You have sent " + args[1]
										+ " " + args[2] + " experience");
								getServer().getPlayer(args[1]).sendMessage(
										"You have been sent " + args[2]
												+ " experience by "
												+ sender.getName());
								setLevel(sender);
								return true;
							} else {
								sender.sendMessage("Really. really? did you think that would work?");
								return true;
							}
						} else {
							sender.sendMessage("You do not have that much experience");
							return true;
						}
					} else {
						sender.sendMessage("The inputed player is not online");
						return true;
					}
				}
			}
		}
		return false;
	}

	private void setLevel(CommandSender sender1) {
		getLogger().info("RARG!!!");
		Player sender = (Player) sender1;
		if (sender.getTotalExperience() < 17) {
			sender.setLevel(0);
		} else if (sender.getTotalExperience() < 34) {
			sender.setLevel(1);
		} else if (sender.getTotalExperience() < 51) {
			sender.setLevel(2);
		} else if (sender.getTotalExperience() < 68) {
			sender.setLevel(3);
		} else if (sender.getTotalExperience() < 85) {
			sender.setLevel(4);
		} else if (sender.getTotalExperience() < 102) {
			sender.setLevel(5);
		} else if (sender.getTotalExperience() < 119) {
			sender.setLevel(6);
		} else if (sender.getTotalExperience() < 136) {
			sender.setLevel(7);
		} else if (sender.getTotalExperience() < 153) {
			sender.setLevel(8);
		} else if (sender.getTotalExperience() < 170) {
			sender.setLevel(9);
		} else if (sender.getTotalExperience() < 187) {
			sender.setLevel(10);
		} else if (sender.getTotalExperience() < 204) {
			sender.setLevel(11);
		} else if (sender.getTotalExperience() < 221) {
			sender.setLevel(12);
		} else if (sender.getTotalExperience() < 238) {
			sender.setLevel(13);
		} else if (sender.getTotalExperience() < 255) {
			sender.setLevel(14);
		} else if (sender.getTotalExperience() < 272) {
			sender.setLevel(15);
		} else if (sender.getTotalExperience() < 292) {
			sender.setLevel(16);
		} else if (sender.getTotalExperience() < 315) {
			sender.setLevel(17);
		} else if (sender.getTotalExperience() < 341) {
			sender.setLevel(18);
		} else if (sender.getTotalExperience() < 370) {
			sender.setLevel(19);
		} else if (sender.getTotalExperience() < 402) {
			sender.setLevel(20);
		} else if (sender.getTotalExperience() < 437) {
			sender.setLevel(21);
		} else if (sender.getTotalExperience() < 475) {
			sender.setLevel(22);
		} else if (sender.getTotalExperience() < 516) {
			sender.setLevel(23);
		} else if (sender.getTotalExperience() < 560) {
			sender.setLevel(24);
		} else if (sender.getTotalExperience() < 607) {
			sender.setLevel(25);
		} else if (sender.getTotalExperience() < 657) {
			sender.setLevel(26);
		} else if (sender.getTotalExperience() < 710) {
			sender.setLevel(27);
		} else if (sender.getTotalExperience() < 766) {
			sender.setLevel(28);
		} else if (sender.getTotalExperience() < 825) {
			sender.setLevel(29);
		} else if (sender.getTotalExperience() < 887) {
			sender.setLevel(30);
		} else if (sender.getTotalExperience() < 956) {
			sender.setLevel(31);
		} else if (sender.getTotalExperience() < 1032) {
			sender.setLevel(32);
		} else if (sender.getTotalExperience() < 1115) {
			sender.setLevel(33);
		} else if (sender.getTotalExperience() < 1205) {
			sender.setLevel(34);
		} else if (sender.getTotalExperience() < 1302) {
			sender.setLevel(35);
		} else if (sender.getTotalExperience() < 1406) {
			sender.setLevel(36);
		} else if (sender.getTotalExperience() < 1517) {
			sender.setLevel(37);
		} else if (sender.getTotalExperience() < 1635) {
			sender.setLevel(38);
		} else if (sender.getTotalExperience() < 1760) {
			sender.setLevel(39);
		}

	}

	private boolean contains(String[] split, String name) {
		for (String i : split) {
			if (i.equalsIgnoreCase(name)) {
				return true;
			}
		}
		return false;
	}

	/**
	 * takes a string and puts it in Aaaa form
	 */
	private String toCase(String string) {
		if (string != null) {
			String out = string.substring(0, 1).toUpperCase();
			out += string.substring(1).toLowerCase();
			getLogger().info(out);
			return out;
		}
		return null;
	}

	private boolean isMod(Player sender) {
		for (Class i : players.get(sender.getName())) {
			if (i.equals(classes.get("Mod"))) {
				return true;
			}
		}
		return false;
	}

	private boolean areSame(ArrayList<Class> classeses) {
		ArrayList<String> tmp = new ArrayList<String>();
		for (Class i : classeses) {
			for (String e : i.getType()) {
				if (tmp.contains(e)) {
					return true;
				} else
					tmp.add(e);
			}
		}
		return false;
	}

	/**
	 * changes the players class to the list of classes
	 */
	private void changeClass(String player, ArrayList<Class> classeses) {
		ArrayList<Class> tmp = new ArrayList<Class>();
		for (String i : getConfig().getString("Players." + player + ".Classes")
				.split(" ")) {
			tmp.add(classes.get(i));
		}
		for (Class t : classeses) {
			boolean done = false;
			for (int i = 0; i < tmp.size(); i++) {
				for (String j : tmp.get(i).getType()) {
					for (String e : t.getType()) {
						if (j.equals(e)) {
							if (tmp.contains(t)) {
								tmp.remove(i);
							} else {
								tmp.set(i, t);
								done = true;
							}
						}
					}
				}
			}
			if (!done) {
				tmp.add(t);
			}
		}
		String fin = "";
		for (Class i : tmp) {
			fin += i.getName() + " ";
			getConfig().set("Players." + player + ".Classes", fin);
		}
		try {
			players.remove(player);
		} catch (Exception ex) {

		}
		players.put(player, tmp);

	}

	/**
	 * converts all classes to a string in format type: class class \n type:
	 * class class
	 */
	private String toStrClasses() {
		String out = "Classes \n";
		ArrayList<String> types = new ArrayList<String>();
		for (Class e : classesAL) {
			for (String j : e.getType()) {
				if (!types.contains(j) && !j.equals("Position")) {
					types.add(j);
				}
			}
		}
		for (String i : types) {
			out += ChatColor.BLUE + "  " + i + ": \n   " + ChatColor.WHITE;
			for (Class e : classesAL) {
				for (String j : e.getType()) {
					if (i.equalsIgnoreCase(j)
							&& !e.getName().equalsIgnoreCase("Peasant")) {
						out += e.getName() + " ";
					}
				}
			}
			out += "\n";
		}
		return out;
	}

	private String toTime(int seconds) {
		int day = (int) TimeUnit.SECONDS.toDays(seconds);
		int hours = (int) (TimeUnit.SECONDS.toHours(seconds) - (day * 24));
		int minute = (int) (TimeUnit.SECONDS.toMinutes(seconds) - (TimeUnit.SECONDS
				.toHours(seconds) * 60));
		int second = (int) (TimeUnit.SECONDS.toSeconds(seconds) - (TimeUnit.SECONDS
				.toMinutes(seconds) * 60));
		String out = "";
		if (day > 0) {
			out += day + " days ";
		}
		if (day > 0 || hours > 0) {
			out += hours + " hours ";
		}
		if (day > 0 || hours > 0 || minute > 0) {
			out += minute + " minutes ";
		}
		if (day > 0 || hours > 0 || minute > 0 || seconds > 0) {
			out += second + " seconds";
		}
		return out;
	}

	private void removeClass(Player player, String className) {
		for (int i = 0; i < players.get(player.getName()).size(); i++) {
			if (players.get(player.getName()).get(i).getName()
					.equalsIgnoreCase(className)) {
				players.get(player.getName()).remove(i);
				setPerms(player);
				return;
			}
		}
	}

	private String getClassName(String arg, String player) {
		boolean hasClass = false;
		for (String i : getConfig().getString("Players." + player + ".Classes")
				.split(" ")) {
			if (i.equalsIgnoreCase(arg)) {
				hasClass = true;
			}
		}
		if (classes.get(toCase(arg)) != null && hasClass == true) {
			return toCase(arg);
		}
		ArrayList<Class> tmp = new ArrayList<Class>();
		for (String i : getConfig().getString("Players." + player + ".Classes")
				.split(" ")) {
			tmp.add(classes.get(i));
		}
		for (Class i : tmp) {
			for (String e : i.getType()) {
				if (e.equalsIgnoreCase(toCase(arg))) {
					return i.getName();
				}
			}
		}
		return "INVALID";
	}

	private void sortClasses(String player, ArrayList<String> order) {
		ArrayList<Class> tmp = new ArrayList<Class>();
		for (String i : getConfig().getString("Players." + player + ".Classes")
				.split(" ")) {
			tmp.add(classes.get(i));
		}

		ArrayList<Class> ordered = new ArrayList<Class>();
		for (String i : order) {
			for (Class e : tmp) {
				if (i.equals(e.getName())) {
					ordered.add(e);
				}
			}
		}
		if (ordered.size() != tmp.size()) {
			return;
		}
		getConfig().set("Players." + player + ".Classes", ALtoStr(ordered));
	}
}

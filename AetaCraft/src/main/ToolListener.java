package main;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerPickupItemEvent;
import org.bukkit.plugin.Plugin;

public class ToolListener implements Listener {
	/**
	 * The plugin
	 */
	private Plugin plugin;
	/**
	 * An array of all the items deemed to be tools
	 */
	private final int[] tools = { 261, 257, 270, 274, 278, 285, 258, 271, 275,
			279, 286, 290, 291, 292, 293, 294, 256, 269, 273, 277, 284, 359,
			346 }; // input tool IDs here
	private final int[] seeds = { 81, 83, 295, 338, 361, 362, 372, 391, 392 };
	private final int[] crops = { 59, 81, 83, 86, 99, 100, 103, 104, 105, 115,
			127, 141, 142, };

	public ToolListener(Plugin plugin) {
		this.plugin = plugin;
	}

	/**
	 * Called when the player rights or left clicks
	 */
	@SuppressWarnings("deprecation")
	@EventHandler
	public void genEvent(PlayerInteractEvent event) {
		for (Class i : Main.players.get(event.getPlayer().getName())) {
			if (i.equals(Main.classes.get("Peasant"))) {
				event.setCancelled(true);
			}
		}
		/*
		 * if (event.getAction() == Action.LEFT_CLICK_BLOCK) { if
		 * (isTool(event.getPlayer().getItemInHand().getType()) &&
		 * !hasPerms(event.getPlayer(), event.getPlayer()
		 * .getItemInHand().getTypeId())) { event.setCancelled(true);
		 * event.getPlayer().sendMessage( "You are not trained with this tool");
		 * } }
		 */
		if (event.getItem() != null) {
			// Hoe
			boolean canHappen = true;
			if (event.getAction() == Action.RIGHT_CLICK_BLOCK) {
				for (int i = 290; i <= 294; i++) {
					if ((event.getClickedBlock().getType()
							.equals(Material.DIRT) || event.getClickedBlock()
							.getType().equals(Material.GRASS))
							&& event.getItem().getTypeId() == i) {
						canHappen = false;
						if (hasPerms(event.getPlayer(), i)) {
							canHappen = true;
							break;
						}
					}
				}
			}
			if (!canHappen) {
				event.setCancelled(true);
				return;
			}
			// Fishing rod
			if (event.getItem().getType().equals(Material.FISHING_ROD)) {
				if (event.getAction() == Action.RIGHT_CLICK_AIR
						|| event.getAction() == Action.RIGHT_CLICK_BLOCK) {
					if (!hasPerms(event.getPlayer(),
							Material.FISHING_ROD.getId())) {
						event.setCancelled(true);
						return;
					}
				}
			}
			// Shear sheep
			if (event.getItem().getType().equals(Material.SHEARS)) {
				if (event.getAction() == Action.RIGHT_CLICK_AIR
						|| event.getAction() == Action.RIGHT_CLICK_BLOCK) {
					if (!hasPerms(event.getPlayer(), Material.SHEARS.getId())) {
						event.setCancelled(true);
						return;
					}
				}
			}
			// Plant seeds
			if (isSeed(event.getItem().getTypeId())
					&& event.getAction() == Action.RIGHT_CLICK_BLOCK
					&& (event.getClickedBlock().getTypeId() == 60
							|| event.getClickedBlock().getTypeId() == 12
							|| event.getClickedBlock().getTypeId() == 3 || event
							.getClickedBlock().getTypeId() == 2)
					&& !canFarm(event.getPlayer())) {
				event.getPlayer().sendMessage(
						"You do not have the skills to plant crops");
				event.setCancelled(true);
			}
		}
	}

	private boolean canFarm(Player player) {
		for (Class e : Main.players.get(player.getName())) {
			if (e.canFarm()) {
				return true;
			}
		}
		return false;
	}

	private boolean isSeed(int typeId) {
		for (int i : seeds) {
			if (typeId == i) {
				return true;
			}
		}
		return false;
	}

	@SuppressWarnings("deprecation")
	@EventHandler
	public void onBreak(BlockBreakEvent event) {
		for (Class i : Main.players.get(event.getPlayer().getName())) {
			if (i.equals(Main.classes.get("Peasant"))) {
				event.setCancelled(true);
			}
		}
		if (isTool(event.getPlayer().getItemInHand().getType())) {
			if (!hasPerms(event.getPlayer(), event.getPlayer().getItemInHand()
					.getType().getId())) {
				event.getPlayer().sendMessage(
						"You are not trained with this tool");
				event.setCancelled(true);
				return;
			}
		}
		// determines block drop
		if ((event.getBlock().getType().equals(Material.LOG) || event
				.getBlock().getType().equals(Material.LOG_2))
				&& !canLogs(event.getPlayer())) {
			event.getBlock().setType(Material.WOOD);
			event.getBlock().setData((byte) 0);
		}
		if (isCrop(event.getBlock().getTypeId()) && !canFarm(event.getPlayer())) {
			event.getPlayer().sendMessage(
					"You do not have the knowledge to harvest this crop");

			boolean checkNext = true;
			Location loc = event.getBlock().getLocation();
			Location sloc = loc;
			while (checkNext) {
				loc = new Location(plugin.getServer().getWorld("official"),
						loc.getBlockX(), loc.getBlockY() + 1, loc.getBlockZ());
				if (loc.getBlock().getType().equals(Material.SUGAR_CANE_BLOCK)
						|| loc.getBlock().getType().equals(Material.CACTUS)) {
				} else {
					checkNext = false;
				}
			}
			checkNext = true;
			while (checkNext) {
				loc = new Location(plugin.getServer().getWorld("official"),
						loc.getBlockX(), loc.getBlockY() - 1, loc.getBlockZ());
				if (loc.getBlock().getType().equals(Material.SUGAR_CANE_BLOCK)
						|| loc.getBlock().getType().equals(Material.CACTUS)
						|| loc.equals(sloc)) {
					loc.getBlock().setType(Material.AIR);
				} else {
					checkNext = false;
				}
			}
			sloc.getBlock().setType(Material.AIR);
		}
	}

	private boolean isCrop(int typeId) {
		for (int i : crops) {
			if (i == typeId) {
				return true;
			}
		}
		return false;
	}

	private boolean canLogs(Player player) {
		for (Class e : Main.players.get(player.getName())) {
			if (e.canLogs()) {
				return true;
			}
		}
		return false;
	}

	@EventHandler
	public void onPlace(BlockPlaceEvent event) {
		for (Class i : Main.players.get(event.getPlayer().getName())) {
			if (i.equals(Main.classes.get("Peasant"))) {
				event.setCancelled(true);
			}
		}
		Material mat = event.getBlock().getType();
		if (mat.equals(Material.HOPPER) || mat.equals(Material.HOPPER_MINECART)) {
			event.getPlayer()
					.sendMessage(
							"Hoppers break the class system currently. I am working on it. Any ideas just tell me - Car4p17(Zane)");
			event.setCancelled(true);
		}
	}

	private boolean hasPerms(Player player, int id) {
		for (Class e : Main.players.get(player.getName())) {
			for (int i : e.getTools()) {
				if (id == i) {
					return true;
				}
			}
		}
		return false;
	}

	@SuppressWarnings("deprecation")
	private boolean isTool(Material type) {
		for (int i : tools) {
			if (i == type.getId()) {
				return true;
			}
		}
		return false;
	}

	@EventHandler
	public void onPickUp(PlayerPickupItemEvent event) {
		for (Class i : Main.players.get(event.getPlayer().getName())) {
			if (i.equals(Main.classes.get("Peasant"))) {
				event.setCancelled(true);
			}
		}
	}
}

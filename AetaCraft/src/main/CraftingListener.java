package main;

//import org.bukkit.Material;
import java.util.ArrayList;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
//import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryOpenEvent;
import org.bukkit.plugin.Plugin;

public class CraftingListener implements Listener {

	@SuppressWarnings("unused")
	private Plugin plugin;

	public CraftingListener(Plugin plugin) {
		this.plugin = plugin;
	}

	@EventHandler
	public void onUse(InventoryOpenEvent event) {
		// the type of crafting interface ex. anvil
		String inventoryName = event.getView().getType().toString();
		Player player = (Player) event.getPlayer();
		// Anvil,Brewing, and Enchanting are simply can or can't use
		if (inventoryName.equalsIgnoreCase("ANVIL")
				&& canAnvil(player) == false) {
			event.setCancelled(true);

		} else if (inventoryName.equalsIgnoreCase("ENCHANTING")
				&& canEnchant(player) == false) {
			event.setCancelled(true);
		} else if (inventoryName.equalsIgnoreCase("Brewing")
				&& canBrew(player) == false) {
			event.setCancelled(true);
		}

	}

	/**
	 * Whether the player can Brew potions or not checking all of their classes
	 */
	private boolean canBrew(Player player) {
		ArrayList<Class> tmp = Main.players.get(player.getName());
		for (Class i : tmp) {
			if (i.canBrew() == true) {
				return true;
			}
		}
		return false;
	}

	/**
	 * Whether the player can Enchant or not checking all of their classes
	 */
	private boolean canEnchant(Player player) {
		ArrayList<Class> tmp = Main.players.get(player.getName());
		for (Class i : tmp) {
			if (i.canEnchant() == true) {
				return true;
			}
		}
		return false;
	}

	/**
	 * Whether the player can use anvils or not checking all of their classes
	 */
	private boolean canAnvil(Player player) {
		ArrayList<Class> tmp = Main.players.get(player.getName());
		for (Class i : tmp) {
			if (i.canAnvil() == true) {
				return true;
			}
		}
		return false;
	}

	@SuppressWarnings("deprecation")
	@EventHandler
	public void onCraft(InventoryClickEvent event) {
		String inventoryName = event.getView().getType().toString();
		Player player = (Player) event.getWhoClicked();
		if (inventoryName.equalsIgnoreCase("furnace")) {
			if (event.getSlot() == 0) { // check slot number
				if (hasFPerms(player, event.getCursor().getTypeId()) == false) {
					event.setCancelled(true);
				}
			}
		} else if (inventoryName.equalsIgnoreCase("workbench")) {
			if (event.getSlot() == 0) { // check slot number
				if (hasCPerms(player, event.getCurrentItem().getTypeId()) == false) {
					event.setCancelled(true);
				}
			}
		}
		if (inventoryName.equalsIgnoreCase("furnace")) {
			if (event.getCurrentItem() != null) {
				if (event.isShiftClick()
						&& !hasFPerms(player, event.getCurrentItem()
								.getTypeId())) {
					event.setCancelled(true);
				}
			}
		}
	}

	private boolean hasCPerms(Player player, int id) {
		for (Class i : Main.players.get(player.getName())) {
			for (int e : i.getCraftables()) {
				if (id == e) {
					return true;
				}
			}
		}
		return false;
	}

	/*
	 * @SuppressWarnings("deprecation")
	 * 
	 * @EventHandler public void onClick(InventoryClickEvent event) { String
	 * inventoryName = event.getView().getType().toString(); Player player =
	 * (Player) event.getWhoClicked(); Material mat =
	 * event.getCursor().getType(); if (inventoryName.equals("FURNACE")) {
	 * plugin.getLogger().info("" + event.getCursor().getType().getId()); if
	 * (event.getSlot() == 0 && (canSmelt(player,mat) == false ||
	 * canCook(player,mat) == false)) { event.setCancelled(true); } } }
	 * 
	 * @SuppressWarnings("deprecation") private boolean canSmelt(Player player,
	 * Material mat) { for (Integer e : Main.smeltables) {
	 * plugin.getLogger().info("" + e); if (mat.getId() == (int) e &&
	 * Main.map.get(player.getUniqueId()).getPermissible()
	 * .hasPermission("blacksmith") == false) { return false; } } return true; }
	 * 
	 * @SuppressWarnings("deprecation") private boolean canCook(Player player,
	 * Material mat) { for (Integer e : Main.rawFoods) { if (mat.getId() ==
	 * (int) e && Main.map.get(player.getUniqueId()).getPermissible()
	 * .hasPermission("blacksmith") == false) { return false; } else return
	 * true; } return true; }
	 */

	private boolean hasFPerms(Player player, int id) {
		for (Class i : Main.players.get(player.getName())) {
			for (int e : i.getSmeltables()) {
				if (id == e) {
					return true;
				}
			}
		}
		return false;
	}
}

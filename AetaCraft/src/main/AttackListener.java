package main;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityShootBowEvent;
import org.bukkit.plugin.Plugin;

public class AttackListener implements Listener {
	@SuppressWarnings("unused")
	private Plugin plugin;
	private final int[] weapons = { 261, 268, 272, 267, 283, 276, 257, 270,
			274, 278, 285, 258, 271, 275, 279, 286, 290, 291, 292, 293, 294,
			256, 269, 273, 277, 284 };

	public AttackListener(Plugin plugin) {
		this.plugin = plugin;
	}

	@SuppressWarnings("deprecation")
	@EventHandler
	public void onAttack(EntityDamageByEntityEvent event) {
		Player player = null;
		try {
			player = (Player) event.getDamager();
		} catch (Exception ex) {

		}
		if (player != null) {
			if (player.getType().toString().equalsIgnoreCase("Player")) {
				if (!hasPerms(player, player.getItemInHand().getTypeId())
						&& isWeapon(player.getItemInHand().getTypeId())) {
					player.sendMessage("You are not trained with this weapon");
					event.setCancelled(true);
				}
			}
		}
	}

	@SuppressWarnings("deprecation")
	@EventHandler
	public void onShoot(EntityShootBowEvent event) {
		Player player = null;
		try {
			player = (Player) event.getEntity();
		} catch (Exception ex) {

		}
		if (player != null) {
			if (!hasPerms(player, Material.BOW.getId())) {
				player.sendMessage("You are not trained with this weapon");
				event.setCancelled(true);
			}
		}
	}

	private boolean isWeapon(int id) {
		for (int i : weapons) {
			if (id == i) {
				return true;
			}
		}
		return false;
	}

	private boolean hasPerms(Player player, int id) {
		for (Class e : Main.players.get(player.getName())) {
			for (int i : e.getWeapons()) {
				if (id == i) {
					return true;
				}
			}
		}
		return false;
	}
}

package main;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.plugin.Plugin;

/**
 * A listener for when armor is put on to check if the player that is putting
 * said armor on has the permissions to do so
 * 
 * @author Carter
 *
 */
public class ArmorListener implements Listener {
	/**
	 * The plugin
	 */
	@SuppressWarnings("unused")
	private Plugin plugin;

	private final int[] armor = { 86, 310, 311, 312, 313, 306, 307, 308, 309,
			314, 315, 316, 317, 302, 303, 304, 305, 298, 299, 300, 301 };

	public ArmorListener(Plugin plugin) {
		this.plugin = plugin;
	}

	@SuppressWarnings("deprecation")
	@EventHandler
	public void useArmor(InventoryClickEvent event) {
		String inventoryName = event.getView().getType().toString();
		Player player = (Player) event.getWhoClicked();
		if (inventoryName.equalsIgnoreCase("Crafting")
				|| inventoryName.equalsIgnoreCase("Player")) {
			for (int i = 36; i <= 39; i++) {
				if (event.getSlot() == i) {
					if (isArmor(event.getCursor().getTypeId())
							&& !hasPerms(player, event.getCursor().getTypeId())) {
						event.setCancelled(true);
						player.sendMessage("You are not trained in this class of armor");
					}
				}

			}

			if (event.getCurrentItem() != null) {
				if (isArmor(event.getCurrentItem().getTypeId())
						&& event.isShiftClick()
						&& !hasPerms(player, event.getCurrentItem().getTypeId())) {
					event.setCancelled(true);
					player.updateInventory();
					player.sendMessage("You are not trained in this class of armor");
				}
			}
		}
	}

	@SuppressWarnings("deprecation")
	@EventHandler
	public void rightArmor(PlayerInteractEvent event) {
		if (event.getPlayer().getItemInHand() != null) {
			if (isArmor(event.getPlayer().getItemInHand().getTypeId())) {
				if (event.getAction().equals(Action.RIGHT_CLICK_AIR)
						|| event.getAction().equals(Action.RIGHT_CLICK_BLOCK)) {
					if (!hasPerms(event.getPlayer(), event.getPlayer()
							.getItemInHand().getTypeId())) {
						event.setCancelled(true);
						event.getPlayer().updateInventory();
						event.getPlayer().sendMessage(
								"You are not trained in this class of armor");
					}
				}
			}
		}
	}

	private boolean isArmor(int id) {
		if (id == 0) {
			return false;
		}
		for (int i : armor) {
			if (id == i) {
				return true;
			}
		}
		return false;
	}

	private boolean hasPerms(Player player, int id) {
		if (id == 0) {
			return true;
		}
		for (Class e : Main.players.get(player.getName())) {
			for (int i : e.getArmor()) {
				if (id == i) {
					return true;
				}
			}
		}
		return false;
	}

	@EventHandler
	public void onDamage(EntityDamageEvent event) {
		Player player;
		try {
			player = (Player) event.getEntity();
		} catch (Exception ex) {
			return;
		}
		if ((event.getCause().equals(DamageCause.FIRE) || event.getCause()
				.equals(DamageCause.FIRE_TICK)|| event.getCause()
				.equals(DamageCause.LAVA))
				&& player.hasPermission("aetacraft.vulcan")) {
			event.setCancelled(true);
		}
	}
}

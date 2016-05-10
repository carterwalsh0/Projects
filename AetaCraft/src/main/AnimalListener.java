package main;

import org.bukkit.entity.Animals;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Ocelot;
import org.bukkit.entity.Player;
import org.bukkit.entity.Wolf;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.plugin.Plugin;

public class AnimalListener implements Listener {
	private final String[] animals = { "PIG", "SHEEP", "COW", "MOOSHROOM",
			"CHICKEN", "WOLF", "OCELOT", "Horse" };
	private final int[] breedables = { 295, 296, 322, 349, 319, 320, 350, 363,
			364, 365, 366, 367, 391, 396, 411, 412, 413, 414, 423, 424 };
	@SuppressWarnings("unused")
	private Plugin plugin;

	public AnimalListener(Plugin plugin) {
		this.plugin = plugin;
	}

	@SuppressWarnings("deprecation")
	@EventHandler
	public void onInteract(PlayerInteractEntityEvent event) {
		if (event.getRightClicked().getType().toString()
				.equalsIgnoreCase("Wolf")) {
			if (Main.untame.contains(event.getPlayer().getName())) {
				Wolf wolf = ((Wolf) event.getRightClicked());
				if (wolf.getOwner() != null
						&& wolf.getOwner().getName()
								.equals(event.getPlayer().getName())) {
					Main.untame.remove(event.getPlayer().getName());
					event.getPlayer().sendMessage("Untame Mode: off");
					wolf.setTamed(false);
					return;
				} else {
					event.getPlayer().sendMessage("This is not your pet");
					return;
				}
			}
		}

		if (event.getRightClicked().getType().toString()
				.equalsIgnoreCase("Ocelot")) {
			if (Main.untame.contains(event.getPlayer().getName())) {
				Ocelot ocelot = ((Ocelot) event.getRightClicked());
				if (ocelot.getOwner() != null
						&& ocelot.getOwner().getName()
								.equals(event.getPlayer().getName())) {
					Main.untame.remove(event.getPlayer().getName());
					event.getPlayer().sendMessage("Untame Mode: off");
					ocelot.setTamed(false);
					return;
				} else {
					event.getPlayer().sendMessage("This is not your pet");
					return;
				}
			}
		}
		if (event.getPlayer().getItemInHand() != null) {
			if (isAnimal(event.getRightClicked())
					&& !canBreed(event.getPlayer())
					&& isBreedable(event.getPlayer().getItemInHand()
							.getTypeId())) {
				Animals animal = (Animals) event.getRightClicked();
				animal.setBreed(false);
				event.setCancelled(true);
				animal.setBreed(true);
				event.getPlayer().sendMessage(
						"You do not have the knowledge to breed animals");
			}
		}
	}

	private boolean isBreedable(int id) {
		for (int i : breedables) {
			if (id == i) {
				return true;
			}
		}
		return false;
	}

	private boolean canBreed(Player player) {
		for (Class i : Main.players.get(player.getName())) {
			if (i.canBreed()) {
				return true;
			}
		}
		return false;
	}

	private boolean isAnimal(Entity en) {
		for (String i : animals) {
			if (en.getType().toString().equalsIgnoreCase(i)) {
				return true;
			}
		}
		return false;
	}
}
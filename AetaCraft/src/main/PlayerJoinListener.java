package main;

import java.util.ArrayList;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.plugin.Plugin;

public class PlayerJoinListener implements Listener {
	private Plugin plugin;

	public PlayerJoinListener(Plugin plugin) {
		this.plugin = plugin;
	}

	@EventHandler
	public void onLogin(PlayerJoinEvent event) {
		Player player = event.getPlayer();
		// code altered from code by Ultimate_N00b
		Main.perms.put(player.getUniqueId(), player.addAttachment(plugin));
		if (plugin.getConfig().getString("Players." + player.getName()) == null) {
			plugin.getConfig().set("Players." + player.getName() + ".Classes",
					"Peasant");
			plugin.getConfig()
					.set("Players." + player.getName() + ".Time", "0");
			plugin.getConfig().set(
					"PlayerNames",
					plugin.getConfig().getString("PlayerNames") + " "
							+ player.getName());
			plugin.saveConfig();
		}
		Main.players.put(player.getName(), getClasses(player));
		Main.setPerms(player);
		if(plugin.getConfig().getString("DisplayNames." + player.getDisplayName()+".Name") == null){
			plugin.getConfig().set("DisplayNames." + player.getDisplayName()+".Name", player.getName());
			plugin.saveConfig();
		}
	}

	/**
	 * Gets the classes of the player based on the names of them which are
	 * located in the config.yml
	 */
	private ArrayList<Class> getClasses(Player player) {
		ArrayList<Class> tmp = new ArrayList<Class>();
		String lst = plugin.getConfig().getString(
				"Players." + player.getName() + ".Classes");
		for (String i : lst.split(" ")) {
			tmp.add(Main.classes.get(i));
		}
		return tmp;
	}
	
}

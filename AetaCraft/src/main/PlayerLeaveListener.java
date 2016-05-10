package main;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerKickEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.plugin.Plugin;

public class PlayerLeaveListener implements Listener {

	private Plugin plugin;

	public PlayerLeaveListener(Plugin plugin) {
		this.plugin = plugin;
	}

	@EventHandler
	public void onPlayerLeave(PlayerQuitEvent event) {
		Player player = event.getPlayer();
		player.removeAttachment(Main.perms.get(player.getUniqueId()));
		Main.perms.remove(player.getUniqueId());
		plugin.getConfig().set("Players." + player.getName() + ".Classes",
				Main.ALtoStr(Main.players.get(player.getName())));
		plugin.getConfig().set("Players." + player.getName() + ".Time",
				Main.ctime.get(player.getUniqueId()));
		plugin.saveConfig();
		Main.players.remove(player.getName());
	}

	@EventHandler
	public void onPlayerKick(PlayerKickEvent event) {
		Player player = event.getPlayer();
		player.removeAttachment(Main.perms.get(player.getUniqueId()));
		Main.perms.remove(player.getUniqueId());
		plugin.getConfig().set("Players." + player.getName() + ".Classes",
				Main.ALtoStr(Main.players.get(player.getName())));
		plugin.getConfig().set("Players." + player.getName() + ".Time",
				Main.ctime.get(player.getUniqueId()));
		plugin.saveConfig();
		Main.players.remove(player.getName());
	}
}

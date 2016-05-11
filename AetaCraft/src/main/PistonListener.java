package main;

import java.util.ArrayList;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockPistonExtendEvent;
import org.bukkit.plugin.Plugin;

public class PistonListener implements Listener {
	private Plugin plugin;
	private final int[] crops = { 59, 81, 83, 86, 99, 100, 103, 104, 105, 115,
			127, 141, 142, };

	public PistonListener(Plugin plugin) {
		this.plugin = plugin;
	}

	@SuppressWarnings("deprecation")
	@EventHandler
	public void onExtend(BlockPistonExtendEvent event) {
		for (Block i : getPushed(event.getBlock().getLocation(),
				event.getDirection())) {
			if (isCrop(i.getTypeId())) {
				event.setCancelled(true);
			}
		}
	}

	public ArrayList<Block> getPushed(Location loc, BlockFace dir) {
		ArrayList<Block> tmp = new ArrayList<Block>();
		boolean checkNext = true;
		int cntr = 0;
		if (dir.equals(BlockFace.UP)) {
			while (checkNext) {
				cntr++;
				loc = new Location(plugin.getServer().getWorld("official"),
						loc.getBlockX(), loc.getBlockY() + 1, loc.getBlockZ());
				if (loc.getBlock().getType().equals(Material.OBSIDIAN)
						|| loc.getBlock().getType().equals(Material.BEDROCK)
						|| loc.getBlock().getType().equals(Material.AIR)
						|| cntr == 12) {
					checkNext = false;
				} else
					tmp.add(loc.getBlock());
			}
		} else if (dir.equals(BlockFace.DOWN)) {
			while (checkNext) {
				cntr++;
				loc = new Location(plugin.getServer().getWorld("official"),
						loc.getBlockX(), loc.getBlockY() - 1, loc.getBlockZ());
				if (loc.getBlock().getType().equals(Material.OBSIDIAN)
						|| loc.getBlock().getType().equals(Material.BEDROCK)
						|| loc.getBlock().getType().equals(Material.AIR)
						|| cntr == 12) {
					checkNext = false;
				} else
					tmp.add(loc.getBlock());
			}
		} else if (dir.equals(BlockFace.NORTH)) {
			while (checkNext) {
				cntr++;
				loc = new Location(plugin.getServer().getWorld("official"),
						loc.getBlockX(), loc.getBlockY(), loc.getBlockZ() - 1);
				if (loc.getBlock().getType().equals(Material.OBSIDIAN)
						|| loc.getBlock().getType().equals(Material.BEDROCK)
						|| loc.getBlock().getType().equals(Material.AIR)
						|| cntr == 12) {
					checkNext = false;
				} else
					tmp.add(loc.getBlock());
			}
		} else if (dir.equals(BlockFace.EAST)) {
			while (checkNext) {
				cntr++;
				loc = new Location(plugin.getServer().getWorld("official"),
						loc.getBlockX() + 1, loc.getBlockY(), loc.getBlockZ());
				if (loc.getBlock().getType().equals(Material.OBSIDIAN)
						|| loc.getBlock().getType().equals(Material.BEDROCK)
						|| loc.getBlock().getType().equals(Material.AIR)
						|| cntr == 12) {
					checkNext = false;
				} else
					tmp.add(loc.getBlock());
			}
		} else if (dir.equals(BlockFace.SOUTH)) {
			while (checkNext) {
				cntr++;
				loc = new Location(plugin.getServer().getWorld("official"),
						loc.getBlockX() - 1, loc.getBlockY(),
						loc.getBlockZ() + 1);
				if (loc.getBlock().getType().equals(Material.OBSIDIAN)
						|| loc.getBlock().getType().equals(Material.BEDROCK)
						|| loc.getBlock().getType().equals(Material.AIR)
						|| cntr == 12) {
					checkNext = false;
				} else
					tmp.add(loc.getBlock());
			}
		} else if (dir.equals(BlockFace.WEST)) {
			while (checkNext) {
				cntr++;
				loc = new Location(plugin.getServer().getWorld("official"),
						loc.getBlockX(), loc.getBlockY(), loc.getBlockZ());
				if (loc.getBlock().getType().equals(Material.OBSIDIAN)
						|| loc.getBlock().getType().equals(Material.BEDROCK)
						|| loc.getBlock().getType().equals(Material.AIR)
						|| cntr == 12) {
					checkNext = false;
				} else
					tmp.add(loc.getBlock());
			}
		}
		return tmp;
	}

	private boolean isCrop(int typeId) {
		for (int i : crops) {
			if (i == typeId) {
				return true;
			}
		}
		return false;
	}
}
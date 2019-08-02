package net.gylliegyllie.stellarsmp.threads;

import net.gylliegyllie.stellarsmp.Main;
import net.gylliegyllie.stellarsmp.utils.ThreadUtil;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Chunk;
import org.bukkit.World;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Item;

public class LagThread {

	private final Main plugin;

	private int seconds = 30 * 60;

	public LagThread(Main plugin) {
		this.plugin = plugin;

		ThreadUtil.runTaskTimer(() -> {

			this.seconds--;

			if (this.seconds == 0) {

				int count = 0;

				for (World world : this.plugin.getServer().getWorlds()) {
					for (Chunk chunk : world.getLoadedChunks()) {
						for (Entity entity : chunk.getEntities()) {
							if (entity instanceof Item) {
								entity.remove();
								count++;
							}
						}
					}
				}

				Bukkit.broadcastMessage(ChatColor.RED + "Cleared dropped items. " + count + " entities removed!");
				this.seconds = 30 * 60;

			} else {
				if (this.seconds % 300 == 0 && this.seconds / 300 == 1) {
					Bukkit.broadcastMessage(ChatColor.RED + "Dropped items will be removed in 5 minutes!");
				} else if (this.seconds % 120 == 0 && this.seconds / 120 == 1) {
					Bukkit.broadcastMessage(ChatColor.RED + "Dropped items will be removed in 2 minutes!");
				} else if (this.seconds % 60 == 0 && this.seconds / 60 == 1) {
					Bukkit.broadcastMessage(ChatColor.RED + "Dropped items will be removed in 1 minute!");
				} else if ((this.seconds < 60 && this.seconds % 15 == 0) || this.seconds <= 10) {
					Bukkit.broadcastMessage(ChatColor.RED + "Dropped items will be removed in " + this.seconds + " seconds!");
				}
			}
		}, 20L);
	}
}

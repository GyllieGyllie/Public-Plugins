package com.astronnetwork.hiddencommands.listeners;

import com.astronnetwork.hiddencommands.Plugin;
import org.bukkit.ChatColor;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;

import java.util.List;

public class CommandListener implements Listener {

	private final Plugin plugin;
	private final String message;

	public CommandListener(Plugin plugin) {
		this.plugin = plugin;

		this.message = this.plugin.getConfiguration().getString("global_message");
	}

	@EventHandler (priority = EventPriority.LOW)
	public void onCommand(PlayerCommandPreprocessEvent event) {
		String[] args = event.getMessage().split(" ");
		String command = args[0].replace("/", "");

		ConfigurationSection section = this.plugin.getConfiguration().getConfigurationSection("commands." + command);

		if (section != null) {

			boolean blocked = false;

			if (section.contains("worlds")) {
				List<String> worlds = section.getStringList("worlds");

				if (worlds.size() > 0) {
					if (worlds.contains(event.getPlayer().getWorld().getName().toLowerCase())) {
						// player is in blocked world
						blocked = true;
					}
				} else {
					// No worlds so blocked in all
					blocked = true;
				}
			} else {
				// No worlds so blocked in all
				blocked = true;
			}

			if (blocked) {
				event.setCancelled(true);

				String message = this.message;

				if (section.contains("message")) {
					message = section.getString("message");
				}

				event.getPlayer().sendMessage(ChatColor.translateAlternateColorCodes('&', message));
			}
		}
	}
}

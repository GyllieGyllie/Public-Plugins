package net.gylliegyllie.rentingcraft.files;

import net.gylliegyllie.gylliecore.files.YamlFile;
import net.gylliegyllie.rentingcraft.Plugin;
import org.bukkit.ChatColor;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;

import java.text.MessageFormat;

public class Messages {

	private final Plugin plugin;
	private final YamlConfiguration messages;

	public Messages(Plugin plugin) {
		this.plugin = plugin;

		String language = this.plugin.getConfiguration().getString("language", "EN").toUpperCase();

		YamlConfiguration messages = YamlFile.getConfiguration(this.plugin, "messages_" + language + ".yml");

		if (messages == null) {
			this.plugin.getLogger().severe("Failed to find Messages file messages_" + language + ".yml. Falling back to English file!");
			messages = YamlFile.getConfiguration(this.plugin, "messages_EN.yml");
		}

		this.messages = messages;
	}

	public String getMessage(String key) {
		if (this.messages.contains(key)) {
			return ChatColor.translateAlternateColorCodes('&', this.messages.getString(key,""));
		} else {
			return "";
		}
	}

	public String getMessage(String key, String... params) {
		if (this.messages.contains(key)) {
			return ChatColor.translateAlternateColorCodes('&', MessageFormat.format(this.messages.getString(key, ""), params));
		} else {
			return "";
		}
	}

	public void sendMessage(Player player, String key) {
		if (this.messages.contains(key)) {
			String message = ChatColor.translateAlternateColorCodes('&', this.messages.getString(key,""));
			player.sendMessage(Plugin.prefix + message);
		}
	}
}

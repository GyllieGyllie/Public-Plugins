package com.astronnetwork.hiddencommands;

import com.astronnetwork.hiddencommands.listeners.CommandListener;
import net.gylliegyllie.gylliecore.files.YamlFile;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

public class Plugin extends JavaPlugin {

	private YamlConfiguration configuration;

	@Override
	public void onEnable() {

		this.configuration = YamlFile.getConfiguration(this, "config.yml");

		this.getServer().getPluginManager().registerEvents(new CommandListener(this), this);

		this.getLogger().info("HiddenCommands enabled!");
	}

	@Override
	public void onDisable() {
		this.getLogger().info("HiddenCommands disabled!");
	}

	public YamlConfiguration getConfiguration() {
		return this.configuration;
	}
}

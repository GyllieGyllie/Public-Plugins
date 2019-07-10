package com.astronnetwork.traveler;

import com.astronnetwork.traveler.commands.TravelerCommand;
import com.astronnetwork.traveler.economy.EconomyManager;
import com.astronnetwork.traveler.manager.ItemManager;
import net.gylliegyllie.gylliecore.files.YamlFile;
import net.gylliegyllie.gylliecore.gui.GuiManager;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;

public class Plugin extends JavaPlugin {

	private YamlConfiguration configuration;

	private EconomyManager economyManager;
	private ItemManager itemManager;

	@Override
	public void onEnable() {

		this.configuration = YamlFile.getConfiguration(this, "config.yml");

		try {
			this.economyManager = new EconomyManager(this);
		} catch (Exception e) {
			this.getLogger().severe("Failed to hook into Vault: " + e.getMessage());
			this.getServer().getPluginManager().disablePlugin(this);
			return;
		}

		new GuiManager(this);
		this.itemManager = new ItemManager(this);

		this.getCommand("traveler").setExecutor(new TravelerCommand(this));

		this.getLogger().info("Traveler enabled!");
	}

	@Override
	public void onDisable() {
		this.getLogger().info("Traveler disabled!");
	}

	public YamlConfiguration getConfiguration() {
		return this.configuration;
	}

	public void saveConfig() {
		try {
			this.getConfiguration().save(new File(this.getDataFolder(), "config.yml"));
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public EconomyManager getEconomyManager() {
		return this.economyManager;
	}

	public ItemManager getItemManager() {
		return this.itemManager;
	}
}

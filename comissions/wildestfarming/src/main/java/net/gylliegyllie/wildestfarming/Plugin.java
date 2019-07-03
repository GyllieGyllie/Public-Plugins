package net.gylliegyllie.wildestfarming;

import net.gylliegyllie.wildestfarming.configuration.Configuration;
import net.gylliegyllie.wildestfarming.listeners.PlantGrowListener;
import net.gylliegyllie.wildestfarming.listeners.PlantingListener;
import net.gylliegyllie.wildestfarming.listeners.SeedDropListener;
import net.gylliegyllie.wildestfarming.listeners.VeinMineListener;
import org.bukkit.plugin.java.JavaPlugin;

public class Plugin extends JavaPlugin {

	private Configuration configuration;

	@Override
	public void onEnable() {

		try {
			this.configuration = new Configuration(this);
		} catch (Exception e) {
			e.printStackTrace();
			this.getServer().getLogger().severe("Failed to load configuration!");
			this.getServer().getPluginManager().disablePlugin(this);
			return;
		}

		this.getServer().getPluginManager().registerEvents(new PlantGrowListener(this), this);
		this.getServer().getPluginManager().registerEvents(new PlantingListener(this), this);
		this.getServer().getPluginManager().registerEvents(new SeedDropListener(this), this);
		this.getServer().getPluginManager().registerEvents(new VeinMineListener(), this);

		this.getLogger().info("Wildest Farming enabled!");
	}

	@Override
	public void onDisable() {

		this.getLogger().info("Wildest Farming disabled!");
	}

	public Configuration getConfiguration() {
		return this.configuration;
	}
}

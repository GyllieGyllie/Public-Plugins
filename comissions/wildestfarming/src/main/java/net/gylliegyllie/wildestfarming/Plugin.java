package net.gylliegyllie.wildestfarming;

import net.gylliegyllie.wildestfarming.commands.SeedPacketCommand;
import net.gylliegyllie.wildestfarming.configuration.Configuration;
import net.gylliegyllie.wildestfarming.listeners.PlantGrowListener;
import net.gylliegyllie.wildestfarming.listeners.PlantingListener;
import net.gylliegyllie.wildestfarming.listeners.SeedDropListener;
import net.gylliegyllie.wildestfarming.listeners.VeinMineListener;
import org.bukkit.NamespacedKey;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.tags.CustomItemTagContainer;
import org.bukkit.inventory.meta.tags.ItemTagType;
import org.bukkit.plugin.java.JavaPlugin;

public class Plugin extends JavaPlugin {

	private Configuration configuration;

	private NamespacedKey key = new NamespacedKey(this, "seed-type");

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

		this.getCommand("seedpacket").setExecutor(new SeedPacketCommand(this));

		this.getLogger().info("Wildest Farming enabled!");
	}

	@Override
	public void onDisable() {

		this.getLogger().info("Wildest Farming disabled!");
	}

	public Configuration getConfiguration() {
		return this.configuration;
	}

	public void addCustomFlag(ItemStack item, String value) {
		ItemMeta itemMeta = item.getItemMeta();

		if (itemMeta != null) {
			itemMeta.getCustomTagContainer().setCustomTag(this.key, ItemTagType.STRING, value);
			item.setItemMeta(itemMeta);
		}
	}

	public String getCustomFlag(ItemStack item) {
		ItemMeta itemMeta = item.getItemMeta();

		if (itemMeta != null) {
			CustomItemTagContainer container = itemMeta.getCustomTagContainer();

			if (container.hasCustomTag(this.key, ItemTagType.STRING)) {
				return container.getCustomTag(this.key, ItemTagType.STRING);
			}
		}

		return null;
	}
}

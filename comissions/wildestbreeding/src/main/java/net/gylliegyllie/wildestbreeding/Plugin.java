package net.gylliegyllie.wildestbreeding;

import net.gylliegyllie.wildestbreeding.commands.AnimalFeedPacket;
import net.gylliegyllie.wildestbreeding.configuration.Configuration;
import net.gylliegyllie.wildestbreeding.listeners.BreedListener;
import org.bukkit.NamespacedKey;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.tags.CustomItemTagContainer;
import org.bukkit.inventory.meta.tags.ItemTagType;
import org.bukkit.plugin.java.JavaPlugin;

public class Plugin extends JavaPlugin {

	private Configuration configuration;

	private NamespacedKey key = new NamespacedKey(this, "feed-type");

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

		this.getServer().getPluginManager().registerEvents(new BreedListener(this), this);

		this.getCommand("animalfeed").setExecutor(new AnimalFeedPacket(this));

		this.getLogger().info("Wildest Breeding enabled!");
	}

	@Override
	public void onDisable() {

		this.getLogger().info("Wildest Breeding disabled!");
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

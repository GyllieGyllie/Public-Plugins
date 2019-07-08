package com.astronnetwork.traveler.manager;

import com.astronnetwork.traveler.Plugin;
import com.astronnetwork.traveler.gui.TravelerGUI;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

public class ItemManager {

	private final Plugin plugin;
	private List<Item> items = new ArrayList<>();

	private List<TravelerGUI> guis = new ArrayList<>();

	public ItemManager(Plugin plugin) {
		this.plugin = plugin;

		ConfigurationSection section = this.plugin.getConfiguration().getConfigurationSection("items");

		for (String key : section.getKeys(false)) {
			ConfigurationSection itemSection = this.plugin.getConfiguration().getConfigurationSection("items." + key);

			if (itemSection != null) {
				try {
					this.items.add(new Item(
							this.plugin,
							"items." + key,
							Material.valueOf(itemSection.getString("type")),
							itemSection.getInt("price"),
							itemSection.getInt("amount")
					));
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}
	}

	public List<Item> getItems() {
		return new ArrayList<>(this.items);
	}

	void removeItem(Item item) {
		this.items.remove(item);
	}

	public void showShop(Player player) {
		TravelerGUI gui = new TravelerGUI(this.plugin, player);
		this.guis.add(gui);
	}

	public void refreshGui() {
		this.guis.forEach(TravelerGUI::update);
	}
}

package com.astronnetwork.traveler.gui;

import com.astronnetwork.traveler.Plugin;
import com.astronnetwork.traveler.manager.Item;
import net.gylliegyllie.gylliecore.gui.GuiScreen;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class TravelerGUI extends GuiScreen {

	private final Plugin plugin;

	public TravelerGUI(Plugin plugin, Player player) {
		super(player, ChatColor.AQUA + "Traveler", 54);

		this.plugin = plugin;

		this.setItems();

		this.show();
	}

	public void update() {
		this.setItems();
	}

	private void setItems() {
		int index = 0;

		for (Item item : this.plugin.getItemManager().getItems()) {
			if (item.getAmount() <= 0) continue;

			this.setItem(index++, this.createItem(item.createShopItem()))
					.onClick(e ->
						this.plugin.getServer().getScheduler().runTaskAsynchronously(this.plugin,
								() -> item.attemptPurchase(this.getPlayer()))
					);

			if (index == 54) {
				break;
			}
		}

		while (index < 54) {
			this.setItem(index++, this.createItem(new ItemStack(Material.AIR)));
		}
	}
}

package com.astronnetwork.traveler.manager;

import com.astronnetwork.traveler.Plugin;
import net.gylliegyllie.gylliecore.gui.ItemBuilder;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class Item {

	private final Plugin plugin;

	private final String key;
	private final Material type;
	private final int price;

	private int amount;

	Item(Plugin plugin, String key, Material type, int price, int amount) {
		this.plugin = plugin;

		this.key = key;
		this.type = type;
		this.price = price;

		this.amount = amount;
	}

	public void attemptPurchase(Player player) {

		synchronized (this) {
			if (this.amount <= 0) {
				player.sendMessage(ChatColor.RED + "This item is not longer in stock!");
				return;
			}

			if (!this.plugin.getEconomyManager().canAfford(player, this.price)) {
				player.sendMessage(ChatColor.RED + "You cannot afford this item!");
				return;
			}

			if (this.plugin.getEconomyManager().pay(player, this.amount)) {
				player.getInventory().addItem(new ItemStack(this.type));
				this.amount--;

				player.sendMessage(ChatColor.GREEN + "Item purchased!");

				if (this.amount > 0) {
					this.plugin.getConfiguration().set(this.key + ".amount", this.amount);
				} else {
					this.plugin.getConfiguration().set(this.key, null);
					this.plugin.getItemManager().removeItem(this);
				}

				this.plugin.saveConfig();
				this.plugin.getItemManager().refreshGui();

			} else {
				player.sendMessage(ChatColor.RED + "Something went wrong while executing the purchase!");
			}
		}
	}

	public ItemStack createShopItem() {
		return new ItemBuilder()
				.type(this.type)
				.lore(" ", ChatColor.GREEN + "Price: " + this.price, ChatColor.GREEN + "In stock: " + this.amount)
				.build();
	}
}

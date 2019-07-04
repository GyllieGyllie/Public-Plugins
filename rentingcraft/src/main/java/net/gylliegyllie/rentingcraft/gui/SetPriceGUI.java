package net.gylliegyllie.rentingcraft.gui;

import net.gylliegyllie.gylliecore.gui.GuiScreen;
import net.gylliegyllie.gylliecore.gui.ItemBuilder;
import net.gylliegyllie.rentingcraft.Plugin;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class SetPriceGUI extends GuiScreen {

	private final Plugin plugin;

	private int price = 1;

	public SetPriceGUI(Plugin plugin, Player player, ItemStack itemStack) {
		super(player, plugin.getMessages().getMessage("inventory.set-price.title"), 9);

		this.plugin = plugin;

		this.setItem(0, this.createItem(new ItemBuilder()
				.type(Material.ENDER_EYE)
				.name(this.plugin.getMessages().getMessage("inventory.confirm"))
				.lore(this.plugin.getMessages().getMessage("inventory.set-price.confirm"))
				.build()))
				.onClick(event ->
						this.plugin.getRentingManager().createNewOffer(this.getPlayer(), itemStack, this.price)
				);

		this.setItem(2, this.createItem(new ItemBuilder()
				.type(Material.EMERALD)
				.name(this.plugin.getMessages().getMessage("inventory.set-price.add1"))
				.lore(this.plugin.getMessages().getMessage("inventory.set-price.add1click"))
				.build()))
				.onClick(event -> {
					this.price++;
					this.updateGold();
				});

		this.setItem(2, this.createItem(new ItemBuilder()
				.type(Material.EMERALD_BLOCK)
				.name(this.plugin.getMessages().getMessage("inventory.set-price.add5"))
				.lore(this.plugin.getMessages().getMessage("inventory.set-price.add5click"))
				.build()))
				.onClick(event -> {
					this.price += 5;
					this.updateGold();
				});

		this.setItem(4, this.createItem(new ItemBuilder()
				.type(Material.GOLD_INGOT)
				.name(this.plugin.getMessages().getMessage("inventory.set-price.current", String.valueOf(this.price)))
				.build()));

		this.setItem(5, this.createItem(new ItemBuilder()
				.type(Material.REDSTONE_BLOCK)
				.name(this.plugin.getMessages().getMessage("inventory.set-price.remove5"))
				.lore(this.plugin.getMessages().getMessage("inventory.set-price.remove5click"))
				.build()))
				.onClick(event -> {
					this.price -= 5;
					this.updateGold();
				});

		this.setItem(6, this.createItem(new ItemBuilder()
				.type(Material.REDSTONE)
				.name(this.plugin.getMessages().getMessage("inventory.set-price.remove1"))
				.lore(this.plugin.getMessages().getMessage("inventory.set-price.remove1click"))
				.build()))
				.onClick(event -> {
					this.price--;
					this.updateGold();
				});
		
		this.setItem(8, this.createItem(new ItemBuilder()
				.type(Material.COMPASS)
				.name(this.plugin.getMessages().getMessage("inventory.cancel"))
				.lore(this.plugin.getMessages().getMessage("inventory.set-price.cancel"))
				.build()))
				.onClick(event ->
						this.getPlayer().closeInventory()
				);
	}

	private void updateGold() {
		this.getItem(4).setItemStack(new ItemBuilder()
				.type(Material.GOLD_INGOT)
				.name(this.plugin.getMessages().getMessage("inventory.set-price.current", String.valueOf(this.price)))
				.build());
	}
}

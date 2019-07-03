package net.gylliegyllie.rentingcraft.gui;

import net.gylliegyllie.gylliecore.gui.GuiItem;
import net.gylliegyllie.gylliecore.gui.GuiScreen;
import net.gylliegyllie.gylliecore.gui.ItemBuilder;
import net.gylliegyllie.rentingcraft.Plugin;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class AddOfferGUI extends GuiScreen {

	private final Plugin plugin;

	private boolean nextScreen = false;

	public AddOfferGUI(Plugin plugin, Player player, ItemStack itemStack) {
		super(player, plugin.getMessages().getMessage("inventory.add-offer.title"), 9);

		this.plugin = plugin;

		this.setItem(0, this.createItem(new ItemBuilder()
				.type(Material.ENDER_EYE)
				.name(this.plugin.getMessages().getMessage("inventory.confirm"))
				.lore(this.plugin.getMessages().getMessage("inventory.add-offer.confirm"))
				.build()))
				.onClick(event ->
					new SetPriceGUI(this.plugin, player, itemStack)
				);

		this.setItem(4, this.createItem(itemStack));

		this.setItem(8, this.createItem(new ItemBuilder()
				.type(Material.COMPASS)
				.name(this.plugin.getMessages().getMessage("inventory.cancel"))
				.lore(this.plugin.getMessages().getMessage("inventory.add-offer.cancel"))
				.build()))
				.onClick(event ->
						this.getPlayer().closeInventory()
				);

		GuiItem pane = this.createItem(new ItemBuilder().type(Material.YELLOW_STAINED_GLASS_PANE).build());
		this.setItem(1, pane);
		this.setItem(2, pane);
		this.setItem(3, pane);
		this.setItem(5, pane);
		this.setItem(6, pane);
		this.setItem(7, pane);

		this.onClose(event -> {
			if (!this.nextScreen) {
				this.plugin.getMessages().sendMessage(this.getPlayer(), "inventory.add-offer.close-unfinished");
			}
		});

		this.show();
	}
}

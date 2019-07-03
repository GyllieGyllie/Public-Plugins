package net.gylliegyllie.gylliecore.gui;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.function.Consumer;

public class GuiScreen {

	private final Inventory inventory;
	private final Player player;

	private final Map<Integer, GuiItem> items = new LinkedHashMap<>();

	private Consumer<InventoryCloseEvent> closeHandler;

	private boolean needUpdate = false;

	public GuiScreen(Player player, String name, int size) {
		this.inventory = Bukkit.createInventory(player, size, name);

		this.player = player;

		GuiManager.addScreen(player.getUniqueId(), this);
	}

	private GuiScreen(Player player, Inventory inventory) {
		this.player = player;
		this.inventory = inventory;

		GuiManager.addPlayer(player.getUniqueId(), this);
	}

	public void show() {
		this.player.openInventory(this.inventory);
	}

	public Player getPlayer() {
		return this.player;
	}

	public Inventory getInventory() {
		return this.inventory;
	}

	public GuiItem createItem(ItemStack itemStack) {
		return new GuiItem(this, itemStack);
	}

	public GuiItem setItem(int slot, GuiItem item) {
		if (slot >= this.inventory.getSize()) {
			return item;
		}

		this.items.put(slot, item);
		this.inventory.setItem(slot, item.getItemStack());

		return item;
	}

	public GuiItem getItem(int slot) {
		return this.items.get(slot);
	}

	public void onClose(Consumer<InventoryCloseEvent> closeHandler) {
		this.closeHandler = closeHandler;
	}

	void forceUpdate() {
		this.needUpdate = true;
	}

	void update() {
		if (!this.needUpdate) {
			return;
		}

		synchronized (this.items) {
			for (Map.Entry<Integer, GuiItem> entry : this.items.entrySet()) {
				this.inventory.setItem(entry.getKey(), entry.getValue().getItemStack());
			}
		}

		this.player.updateInventory();
		this.needUpdate = false;
	}

	void handleClick(InventoryClickEvent event) {
		GuiItem item = this.items.get(event.getRawSlot());

		if (item != null) {
			item.handleClick(event);
		}
	}

	void handleInteract(PlayerInteractEvent event) {
		GuiItem item = this.items.get(event.getPlayer().getInventory().getHeldItemSlot());

		if (item != null) {
			event.setCancelled(true);
			item.handleInteract(event);
		}
	}

	public void handleClose(InventoryCloseEvent event) {
		if (this.closeHandler != null) {
			this.closeHandler.accept(event);
		}
	}

	public static GuiScreen wrap(Player player) {
		return GuiManager.getPlayer(player.getUniqueId(), () -> new GuiScreen(player, player.getOpenInventory().getBottomInventory()));
	}

	protected GuiItem createBackItem() {
		return this.createItem(new ItemBuilder().type(Material.ARROW).amount(1).name(ChatColor.AQUA + "Go Back!").build());
	}
}

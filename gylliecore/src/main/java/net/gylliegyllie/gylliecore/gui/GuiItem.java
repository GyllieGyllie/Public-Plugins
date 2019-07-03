package net.gylliegyllie.gylliecore.gui;

import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;

import java.util.function.BiConsumer;
import java.util.function.Consumer;

public class GuiItem {

	private ItemStack itemStack;
	private GuiScreen screen;

	private Consumer<InventoryClickEvent> clickHandler;
	private Consumer<PlayerInteractEvent> interactHandler;

	public GuiItem(GuiScreen screen, ItemStack itemStack) {
		this.itemStack = itemStack;
		this.screen = screen;
	}

	/**
	 * @return Get the GuiScreen for this item
	 */
	public GuiScreen getScreen() {
		return this.screen;
	}

	public ItemStack getItemStack() {
		return this.itemStack;
	}

	public void setItemStack(ItemStack itemStack) {
		this.itemStack = itemStack;
		this.screen.forceUpdate();
	}

	/**
	 * Set the logic that needs to be ran when the item is clicked
	 *
	 * @param clickHandler Consumer to handle the click
	 * @return This instance for chaining.
	 */
	public GuiItem onClick(Consumer<InventoryClickEvent> clickHandler) {
		this.clickHandler = clickHandler;
		return this;
	}

	/**
	 * @param consumer A Consumer that will receive the InventoryClickEvent and
	 * this GuiItem instance for convenience when this icon is clicked.
	 * @return This instance for chaining.
	 */
	public GuiItem onClick(BiConsumer<InventoryClickEvent, GuiItem> consumer) {
		this.clickHandler = ev -> consumer.accept(ev, this);
		return this;
	}

	void handleClick(InventoryClickEvent event) {
		if (this.clickHandler != null)
			this.clickHandler.accept(event);
	}

	/**
	 * Set the logic that needs to be ran when the item is interacted with
	 *
	 * @param interactHandler Consumer to handle the interact
	 */
	public void onInteract(Consumer<PlayerInteractEvent> interactHandler) {
		this.interactHandler = interactHandler;
	}

	void handleInteract(PlayerInteractEvent event) {
		if (this.interactHandler != null)
			this.interactHandler.accept(event);
	}

}

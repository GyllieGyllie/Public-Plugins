package net.gylliegyllie.gylliecore.gui;

import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.function.Supplier;

public class GuiManager implements Listener {

	private static Map<UUID, GuiScreen> screens = new HashMap<>();
	private static Map<UUID, GuiScreen> player_screens = new HashMap<>();

	public GuiManager(JavaPlugin plugin) {
		plugin.getServer().getScheduler().runTaskTimer(plugin, () -> {
			GuiManager.screens.values().forEach(GuiScreen::update);
			GuiManager.player_screens.values().forEach(GuiScreen::update);
		}, 1L, 1L);
	}

	public static void addScreen(UUID uuid, GuiScreen screen) {
		GuiManager.screens.put(uuid, screen);
	}

	public static void addPlayer(UUID uuid, GuiScreen screen) {
		GuiManager.player_screens.put(uuid, screen);
	}

	/**
	 * @param uuid     The UUID of the player to get.
	 * @param supplier The supplier to use if an instance doesn't exist.
	 * @return The GuiScreen that represents the player's inventory.
	 */
	static GuiScreen getPlayer(UUID uuid, Supplier<GuiScreen> supplier) {
		return GuiManager.player_screens.computeIfAbsent(uuid, u -> supplier.get());
	}

	public static GuiScreen getScreen(UUID uuid) {
		GuiScreen screen = GuiManager.screens.get(uuid);

		if (screen == null) {
			screen = GuiManager.player_screens.get(uuid);
		}

		return screen;
	}

	@EventHandler
	public void onClick(InventoryClickEvent event) {
		GuiScreen screen = GuiManager.getScreen(event.getWhoClicked().getUniqueId());

		if (screen != null) {
			event.setCancelled(true);
			screen.handleClick(event);
		}
	}

	@EventHandler
	public void onInteract(PlayerInteractEvent event) {
		GuiScreen screen = GuiManager.getScreen(event.getPlayer().getUniqueId());

		if (screen != null) {
			screen.handleInteract(event);
		}
	}

	@EventHandler
	public void onClose(InventoryCloseEvent event) {
		GuiScreen screen = null;

		for (GuiScreen guiScreen : GuiManager.screens.values()) {
			if (guiScreen.getInventory().equals(event.getInventory())) {
				screen = guiScreen;
			}
		}

		if (screen == null) {
			return;
		}

		screen.handleClose(event);
		GuiManager.screens.remove(screen.getPlayer().getUniqueId());
	}

	@EventHandler
	public void onLeave(PlayerQuitEvent event) {
		GuiManager.screens.remove(event.getPlayer().getUniqueId());
		GuiManager.player_screens.remove(event.getPlayer().getUniqueId());
	}

	@EventHandler(priority = EventPriority.MONITOR)
	public void onQuit(PlayerQuitEvent event) {
		GuiManager.screens.remove(event.getPlayer().getUniqueId());
		GuiManager.player_screens.remove(event.getPlayer().getUniqueId());
	}

}

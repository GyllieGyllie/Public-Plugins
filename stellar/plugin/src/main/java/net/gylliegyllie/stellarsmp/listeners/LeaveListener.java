package net.gylliegyllie.stellarsmp.listeners;

import net.gylliegyllie.stellarsmp.Main;
import net.gylliegyllie.stellarsmp.utils.ThreadUtil;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;

public class LeaveListener implements Listener {

	private final Main plugin;

	public LeaveListener(Main plugin) {
		this.plugin = plugin;
	}

	@EventHandler
	public void onLeave(PlayerQuitEvent event) {

		ThreadUtil.runTaskAsync(() -> { if (this.plugin.getDiscordManager() != null) this.plugin.getDiscordManager().sendLeave(event.getPlayer().getName());});

	}
}

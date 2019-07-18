package net.gylliegyllie.stellarsmp.listeners;

import net.gylliegyllie.stellarsmp.Main;
import net.gylliegyllie.stellarsmp.utils.ThreadUtil;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;

public class ChatListener implements Listener {

	private final Main plugin;

	public ChatListener(Main plugin) {
		this.plugin = plugin;
	}

	@EventHandler
	public void onPlayerChat(AsyncPlayerChatEvent event) {
		ThreadUtil.runTaskAsync(() -> { if (this.plugin.getDiscordManager() != null) this.plugin.getDiscordManager().sendMessage(event.getPlayer().getDisplayName(), event.getMessage());});
	}
}

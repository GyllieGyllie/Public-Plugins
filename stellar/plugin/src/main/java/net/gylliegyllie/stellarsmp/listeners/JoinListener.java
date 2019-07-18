package net.gylliegyllie.stellarsmp.listeners;

import net.gylliegyllie.stellarsmp.Main;
import net.gylliegyllie.stellarsmp.utils.TablistUtil;
import net.gylliegyllie.stellarsmp.utils.ThreadUtil;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

public class JoinListener implements Listener {

	private final Main plugin;

	public JoinListener(Main plugin) {
		this.plugin = plugin;
	}

	@EventHandler
	public void onJoin(PlayerJoinEvent event) {

		Player player = event.getPlayer();

		TablistUtil.sendHeader(event.getPlayer(), "§6Welcome to §4§lStellar SMP", "");

		ThreadUtil.runTaskAsync(() -> { if (this.plugin.getDiscordManager() != null) this.plugin.getDiscordManager().sendJoin(player.getName());});

	}
}

package net.gylliegyllie.stellarsmp.listeners;

import net.gylliegyllie.stellarsmp.Main;
import net.gylliegyllie.stellarsmp.utils.TablistUtil;
import org.bukkit.ChatColor;
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

		TablistUtil.sendHeader(player, "§6Welcome to §4§lStellar SMP", "");

		if (this.plugin.getLiveThread().isLive(player.getUniqueId())) {
			player.setDisplayName(ChatColor.DARK_PURPLE + player.getName());
		} else {
			player.setDisplayName(ChatColor.RED + player.getName());
		}
	}
}

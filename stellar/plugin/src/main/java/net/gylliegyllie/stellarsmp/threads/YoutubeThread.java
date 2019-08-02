package net.gylliegyllie.stellarsmp.threads;

import com.mashape.unirest.http.HttpResponse;
import com.mashape.unirest.http.JsonNode;
import com.mashape.unirest.http.Unirest;
import net.gylliegyllie.stellarsmp.Main;
import net.gylliegyllie.stellarsmp.utils.ThreadUtil;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class YoutubeThread {

	private final Main plugin;

	private List<UUID> recording = new ArrayList<>();

	public YoutubeThread(Main plugin) {
		this.plugin = plugin;

		ThreadUtil.runTaskTimerAsync(() -> {

			for (Player player : this.plugin.getServer().getOnlinePlayers()) {
				if (this.recording.contains(player.getUniqueId())) {
					player.setDisplayName(ChatColor.DARK_RED + player.getName());
				} else {
					if (player.getDisplayName().startsWith(ChatColor.DARK_RED.toString())) {
						player.setDisplayName(ChatColor.RED + player.getName());
					}
				}
			}

		}, 60 * 20L, 60 * 20L);
	}

	public void togglePlayer(Player player) {
		if (this.recording.contains(player.getUniqueId())) {
			player.sendMessage(ChatColor.GREEN + "You are no longer marked as recording!");
			this.recording.remove(player.getUniqueId());
		} else {
			player.sendMessage(ChatColor.GREEN + "You are marked as recording!");
			this.recording.add(player.getUniqueId());
			player.setDisplayName(ChatColor.DARK_RED + player.getName());
		}
	}

	public void removePlayer(Player player) {
		this.recording.remove(player.getUniqueId());
	}
}

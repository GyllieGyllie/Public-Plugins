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

public class LiveThread {

	private final Main plugin;

	private List<UUID> live;

	public LiveThread(Main plugin) {
		this.plugin = plugin;

		ThreadUtil.runTaskTimerAsync(() -> {

			try {

				List<UUID> live = new ArrayList<>();

				HttpResponse<JsonNode> response = Unirest.get("https://stellarsmp.com/api/live").asJson();
				JSONObject jsonObject = response.getBody().getObject();

				if (jsonObject.has("live")) {
					JSONArray array = jsonObject.getJSONArray("live");

					for (int i = 0; i < array.length(); i++) {
						JSONObject streamer = array.getJSONObject(i);

						live.add(UUID.fromString(streamer.getString("uuid")));
					}
				}

				this.live = live;

				for (Player player : this.plugin.getServer().getOnlinePlayers()) {
					if (this.live.contains(player.getUniqueId())) {
						player.setDisplayName(ChatColor.DARK_PURPLE + player.getName());
					} else {
						if (player.getDisplayName().startsWith(ChatColor.DARK_PURPLE.toString())) {
							player.setDisplayName(ChatColor.RED + player.getName());
						}
					}
				}

			} catch (Exception e) {
				e.printStackTrace();
			}

		}, 60 * 20L, 60 * 20L);
	}

	public boolean isLive(UUID uuid) {
		return this.live.contains(uuid);
	}
}

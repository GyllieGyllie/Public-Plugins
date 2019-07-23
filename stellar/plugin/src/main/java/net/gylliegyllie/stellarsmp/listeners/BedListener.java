package net.gylliegyllie.stellarsmp.listeners;

import net.gylliegyllie.stellarsmp.utils.ThreadUtil;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerBedEnterEvent;
import org.bukkit.event.player.PlayerBedLeaveEvent;

public class BedListener implements Listener {

	private long lastNightSkip = -1L;

	@EventHandler(priority =  EventPriority.HIGH)
	public void onBedEnter(PlayerBedEnterEvent event) {
		if (event.getBedEnterResult() == PlayerBedEnterEvent.BedEnterResult.OK) {
			ThreadUtil.runTaskLater(() -> this.check(
					ChatColor.YELLOW + event.getPlayer().getDisplayName() + ChatColor.YELLOW + " is now sleeping, skipping the night...",
					ChatColor.YELLOW + event.getPlayer().getDisplayName() + ChatColor.YELLOW + " is now sleeping (%sleeping%/%online%, %needed% needed to skip the night)"),
					5L);
		}
	}

	@EventHandler
	public void onBedLeave(PlayerBedLeaveEvent event) {
		ThreadUtil.runTaskLater(() -> this.check(
				"",
				ChatColor.YELLOW + event.getPlayer().getDisplayName() + ChatColor.YELLOW + " left his bed (%sleeping%/%online%, %needed% needed to skip the night)"),
				5L);
	}

	private void check(String skipMessage, String waitMessage) {

		int sleeping = 0;
		int total = 0;

		for (Player player : Bukkit.getOnlinePlayers()) {
			total++;

			if (player.isSleeping()) {
				sleeping++;
			}
		}

		int needed = Math.max(total / 2, 1);

		// Enough sleepers
		if (needed <= sleeping) {

			this.broadcast(this.replace(skipMessage, needed, sleeping, total));

			World world = Bukkit.getWorld("world");

			if (world != null) {
				world.setTime(1000);
				world.setStorm(false);
				world.setThundering(false);
				this.lastNightSkip = System.currentTimeMillis();
			}

		} else {

			this.broadcast(this.replace(waitMessage, needed, sleeping, total));

		}

	}

	private String replace(String message, int needed, int sleeping, int total) {
		return message.replaceAll("%needed%", String.valueOf(needed))
				.replaceAll("%sleeping%", String.valueOf(sleeping))
				.replaceAll("%online%", String.valueOf(total));
	}

	private void broadcast(String message) {
		if (this.lastNightSkip + 1000 < System.currentTimeMillis() && !message.isEmpty()) {
			Bukkit.broadcastMessage(message);
		}
	}
}
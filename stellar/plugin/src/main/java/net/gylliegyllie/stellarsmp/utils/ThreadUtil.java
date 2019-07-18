package net.gylliegyllie.stellarsmp.utils;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitTask;

import java.util.concurrent.Callable;

public class ThreadUtil {

	public static JavaPlugin plugin;

	public static void initialize(JavaPlugin plugin) {
		ThreadUtil.plugin = plugin;
	}

	public static BukkitTask runTaskLater(Runnable run, long delay) {
		return Bukkit.getServer().getScheduler().runTaskLater(ThreadUtil.plugin, run, delay);
	}

	public static BukkitTask runTaskTimer(Runnable run, long start, long repeat) {
		return Bukkit.getServer().getScheduler().runTaskTimer(ThreadUtil.plugin, run, start, repeat);
	}

	public static BukkitTask runTaskTimer(Runnable run, long repeat) {
		return Bukkit.getServer().getScheduler().runTaskTimer(ThreadUtil.plugin, run, 0, repeat);
	}

	public static BukkitTask runTaskTimerAsync(Runnable run, long start, long repeat) {
		return Bukkit.getServer().getScheduler().runTaskTimerAsynchronously(ThreadUtil.plugin, run, start, repeat);
	}

	public static BukkitTask runTaskTimerAsync(Runnable run, long repeat) {
		return Bukkit.getServer().getScheduler().runTaskTimerAsynchronously(ThreadUtil.plugin, run, 0, repeat);
	}


	public static int scheduleTask(Runnable run, long delay) {
		return Bukkit.getServer().getScheduler().scheduleSyncDelayedTask(ThreadUtil.plugin, run, delay);
	}

	public static int scheduleAsyncTask(Runnable run, long delay) {
		return Bukkit.getServer().getScheduler().scheduleAsyncDelayedTask(ThreadUtil.plugin, run, delay);
	}

	public static BukkitTask runTask(Runnable run) {
		if (!ThreadUtil.plugin.isEnabled()) {
			return null;
		}
		return Bukkit.getServer().getScheduler().runTask(ThreadUtil.plugin, run);
	}

	public static <T> T runTaskSync(Callable<T> run) throws Exception {
		return Bukkit.getScheduler().callSyncMethod(ThreadUtil.plugin, run).get();
	}

	public static int runTaskNextTick(Runnable run) {
		if (!ThreadUtil.plugin.isEnabled()) {
			run.run();
			return 0;
		}
		return scheduleTask(run, 1);
	}

	public static void runTaskAsync(Runnable run) {
		if (!ThreadUtil.plugin.isEnabled()) {
			run.run();
			return;
		}
		Bukkit.getScheduler().runTaskAsynchronously(ThreadUtil.plugin, run);
	}

	public static void runTaskLaterAsync(Runnable run, long delay) {
		if (!ThreadUtil.plugin.isEnabled()) {
			run.run();
			return;
		}
		Bukkit.getScheduler().runTaskLaterAsynchronously(ThreadUtil.plugin, run, delay);
	}

	public static void catchNonAsyncThread() {
		if (Bukkit.getServer().isPrimaryThread()) {
			Bukkit.broadcastMessage(ChatColor.DARK_RED + "A plugin is doing something wrong. Please report to the developers!");
			throw new IllegalStateException("Illegal call on main thread");
		}
	}

}

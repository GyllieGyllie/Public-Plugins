package net.gylliegyllie.stellarsmp;

import net.gylliegyllie.stellarsmp.discord.DiscordManager;
import net.gylliegyllie.stellarsmp.listeners.ChatListener;
import net.gylliegyllie.stellarsmp.listeners.JoinListener;
import net.gylliegyllie.stellarsmp.listeners.LeaveListener;
import net.gylliegyllie.stellarsmp.threads.LiveThread;
import net.gylliegyllie.stellarsmp.utils.ThreadUtil;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

public class Main extends JavaPlugin {

	private DiscordManager discordManager;

	public Main() {
		//this.discordManager = new DiscordManager(this);
	}

	@Override
	public void onEnable() {

		ThreadUtil.initialize(this);

		// Register listeners
		this.getServer().getPluginManager().registerEvents(new JoinListener(this), this);
		this.getServer().getPluginManager().registerEvents(new LeaveListener(this), this);
		this.getServer().getPluginManager().registerEvents(new ChatListener(this), this);

		// Register commands

		if (this.discordManager != null) this.getDiscordManager().sendOnline();

		new LiveThread(this);

		this.getLogger().info("SMP Plugin booted :D");
	}

	@Override
	public void onDisable() {
		this.discordManager.shutdown();

		for (Player player : Bukkit.getOnlinePlayers()) {
			player.kickPlayer("Server is rebooting!");
		}

		this.getLogger().info("SMP Plugin disabled :(");
	}

	public DiscordManager getDiscordManager() {
		return this.discordManager;
	}
}

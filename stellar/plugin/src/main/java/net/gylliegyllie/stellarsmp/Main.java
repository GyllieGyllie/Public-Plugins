package net.gylliegyllie.stellarsmp;

import net.gylliegyllie.stellarsmp.listeners.JoinListener;
import net.gylliegyllie.stellarsmp.threads.LiveThread;
import net.gylliegyllie.stellarsmp.utils.ThreadUtil;
import org.bukkit.plugin.java.JavaPlugin;

public class Main extends JavaPlugin {

	@Override
	public void onEnable() {

		ThreadUtil.initialize(this);

		// Register listeners
		this.getServer().getPluginManager().registerEvents(new JoinListener(this), this);

		new LiveThread(this);

		this.getLogger().info("SMP Plugin booted :D");
	}

	@Override
	public void onDisable() {
		this.getLogger().info("SMP Plugin disabled :(");
	}
}
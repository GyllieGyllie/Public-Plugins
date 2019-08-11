package net.gylliegyllie.stellarsmp;

import net.gylliegyllie.stellarsmp.commands.Record;
import net.gylliegyllie.stellarsmp.listeners.BedListener;
import net.gylliegyllie.stellarsmp.listeners.JoinListener;
import net.gylliegyllie.stellarsmp.listeners.LeaveListener;
import net.gylliegyllie.stellarsmp.threads.LagThread;
import net.gylliegyllie.stellarsmp.threads.LiveThread;
import net.gylliegyllie.stellarsmp.threads.YoutubeThread;
import net.gylliegyllie.stellarsmp.utils.ThreadUtil;
import org.bukkit.plugin.java.JavaPlugin;

public class Main extends JavaPlugin {

	private LiveThread liveThread;
	private YoutubeThread youtubeThread;

	@Override
	public void onEnable() {

		ThreadUtil.initialize(this);

		// Register listeners
		this.getServer().getPluginManager().registerEvents(new JoinListener(this), this);
		this.getServer().getPluginManager().registerEvents(new BedListener(), this);
		this.getServer().getPluginManager().registerEvents(new LeaveListener(this), this);

		this.liveThread = new LiveThread(this);
		this.youtubeThread = new YoutubeThread(this);
		//new LagThread(this);

		this.getCommand("record").setExecutor(new Record(this));

		this.getLogger().info("SMP Plugin booted :D");
	}

	@Override
	public void onDisable() {
		this.getLogger().info("SMP Plugin disabled :(");
	}

	public LiveThread getLiveThread() {
		return this.liveThread;
	}

	public YoutubeThread getYoutubeThread() {
		return this.youtubeThread;
	}
}

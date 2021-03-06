package net.gylliegyllie.rentingcraft;

import net.gylliegyllie.gylliecore.files.YamlFile;
import net.gylliegyllie.gylliecore.gui.GuiManager;
import net.gylliegyllie.rentingcraft.commands.OfferCommand;
import net.gylliegyllie.rentingcraft.files.Messages;
import net.gylliegyllie.rentingcraft.managers.EconomyManager;
import net.gylliegyllie.rentingcraft.managers.ToolManager;
import net.gylliegyllie.rentingcraft.renting.RentingManager;
import net.gylliegyllie.rentingcraft.storage.LiteStorage;
import net.gylliegyllie.rentingcraft.storage.StorageManager;
import org.bukkit.ChatColor;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

public class Plugin extends JavaPlugin {

	public static String prefix = ChatColor.translateAlternateColorCodes('&', "&3[&4Renting&2Craft&3] ");

	private YamlConfiguration configuration;
	private Messages messages;

	private StorageManager storage;

	private EconomyManager economyManager;
	private ToolManager toolManager;
	private RentingManager rentingManager;

	@Override
	public void onEnable() {

		this.configuration = YamlFile.getConfiguration(this, "config.yml");
		this.messages = new Messages(this);

		new GuiManager(this);

		String prefix = this.messages.getMessage("prefix");

		if (prefix != null) {
			Plugin.prefix = prefix;
		}

		try {
			if (this.configuration.getBoolean("use-remote-db")) {
				// TODO SQL
			} else {
				this.storage = new LiteStorage(this);
			}
		} catch (Exception e) {
			e.printStackTrace();
			this.getLogger().severe("Failed to initialize storage mechanism!");
			this.getServer().getPluginManager().disablePlugin(this);
			return;
		}

		// Initialize economy manager
		try {
			this.economyManager = new EconomyManager(this);
		} catch (Exception e) {
			this.getLogger().severe("Failed to enable RentingCraft. Reason: " + e.getMessage());
			this.getServer().getPluginManager().disablePlugin(this);
			return;
		}

		this.toolManager = new ToolManager(this);
		this.rentingManager = new RentingManager(this);

		this.getCommand("offer").setExecutor(new OfferCommand(this));

		this.getLogger().info("RentingCraft has successfully been enabled");
	}

	@Override
	public void onDisable() {

		this.getLogger().info("RentingCraft has successfully been disabled");
	}

	public YamlConfiguration getConfiguration() {
		return this.configuration;
	}

	public Messages getMessages() {
		return this.messages;
	}

	public StorageManager getStorage() {
		return this.storage;
	}

	public ToolManager getToolManager() {
		return this.toolManager;
	}

	public RentingManager getRentingManager() {
		return this.rentingManager;
	}
}

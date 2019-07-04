package net.gylliegyllie.rentingcraft.storage;

import net.gylliegyllie.rentingcraft.Plugin;

import java.io.File;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class LiteStorage extends SQLStorage {

	private final Plugin plugin;
	private final String url;

	public LiteStorage(Plugin plugin) throws Exception {
		super();

		this.plugin = plugin;

		this.url = "jdbc:sqlite:" + new File(this.plugin.getDataFolder(), "rentingcraft.db").getAbsolutePath();
	}

	@Override
	public Connection getConnection() throws SQLException {
		return DriverManager.getConnection(this.url);
	}
}

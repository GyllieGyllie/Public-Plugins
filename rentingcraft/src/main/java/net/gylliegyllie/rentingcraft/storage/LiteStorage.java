package net.gylliegyllie.rentingcraft.storage;

import net.gylliegyllie.rentingcraft.Plugin;

import java.io.File;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

public class LiteStorage extends SQLStorage {

	private final Plugin plugin;
	private final String url;

	public LiteStorage(Plugin plugin) throws Exception {
		this.plugin = plugin;

		this.url = "jdbc:sqlite:" + new File(this.plugin.getDataFolder(), "rentingcraft.db").getAbsolutePath();

		this.init();
	}

	public void init() throws Exception {

		try (Connection connection = this.getConnection();
		     Statement statement = connection.createStatement()) {

			statement.execute("CREATE TABLE IF NOT EXISTS rents (" +
					"id char(36) primary key," +
					"owner char(36) not null," +
					"price integer not null," +
					"rented_by char(36) not null default ''," +
					"rented_for integer not null default -1," +
					"rented_left integer not null default -1," +
					"item text not null)");

		}

	}

	@Override
	public Connection getConnection() throws SQLException {
		return DriverManager.getConnection(this.url);
	}
}

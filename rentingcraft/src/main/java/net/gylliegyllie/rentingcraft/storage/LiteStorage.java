package net.gylliegyllie.rentingcraft.storage;

import net.gylliegyllie.rentingcraft.Plugin;
import net.gylliegyllie.rentingcraft.renting.Rent;

import java.io.File;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.Statement;

public class LiteStorage extends StorageManager {

	private final Plugin plugin;
	private final String url;

	public LiteStorage(Plugin plugin) {
		this.plugin = plugin;

		this.url = "jdbc:sqlite:" + new File(this.plugin.getDataFolder(), "rentingcraft.db").getAbsolutePath();

		try (Connection connection = DriverManager.getConnection(this.url);
		     Statement statement = connection.createStatement()) {

			statement.execute("CREATE TABLE IF NOT EXISTS rents (" +
					"id integer primary key auto_increment," +
					"owner char(36) not null," +
					"price integer not null," +
					"rented_by char(36) not null default ''," +
					"rented_for integer not null default -1," +
					"rented_left integer not null default -1," +
					"item text not null)");

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Override
	public void storeNewRent(Rent rent) {

	}
}

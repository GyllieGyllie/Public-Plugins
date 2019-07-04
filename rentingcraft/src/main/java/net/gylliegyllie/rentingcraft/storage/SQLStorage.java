package net.gylliegyllie.rentingcraft.storage;

import net.gylliegyllie.rentingcraft.renting.Rent;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;

public abstract class SQLStorage extends StorageManager {

	public SQLStorage() throws Exception {

		try (Connection connection = this.getConnection();
		     Statement statement = connection.createStatement()) {

			statement.execute("CREATE TABLE IF NOT EXISTS rents (" +
					"id char(36) primary key auto_increment," +
					"owner char(36) not null," +
					"price integer not null," +
					"rented_by char(36) not null default ''," +
					"rented_for integer not null default -1," +
					"rented_left integer not null default -1," +
					"item text not null)");

		}

	}

	public abstract Connection getConnection() throws SQLException;

	@Override
	public boolean storeNewRent(Rent rent) {

		String query = "INSERT INTO rents (id, owner, price, item) VALUES (?, ?, ?, ?);";

		try (Connection connection = this.getConnection();
		     PreparedStatement statement = connection.prepareStatement(query)) {

			statement.setString(1, rent.getID().toString());
			statement.setString(2, rent.getOwner().toString());
			statement.setInt(3, rent.getPrice());
			statement.setString(4, rent.getJsonItem());

			statement.execute();

			return true;
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}

	}

}

package net.gylliegyllie.rentingcraft.storage;

import net.gylliegyllie.rentingcraft.renting.Rent;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;

public abstract class SQLStorage extends StorageManager {

	abstract void init() throws Exception;

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

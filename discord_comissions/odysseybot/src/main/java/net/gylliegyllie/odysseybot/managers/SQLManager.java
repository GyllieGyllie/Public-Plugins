package net.gylliegyllie.odysseybot.managers;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class SQLManager {

	private final static Logger logger = LoggerFactory.getLogger(SQLManager.class);

	private final String url;

	public SQLManager() throws Exception {

		this.url = "jdbc:sqlite:" + new File("odysseybot.db").getAbsolutePath();

		if (!this.init()) {
			throw new Exception("Failed to init database!");
		}
	}

	public Connection getConnection() throws SQLException {
		return DriverManager.getConnection(this.url);
	}

	private boolean init() {

		Connection connection = null;
		PreparedStatement statement = null;

		try {

			connection = this.getConnection();

			statement = connection.prepareStatement("create table if not exists members (" +
					"id bigint not null primary key," +
					"portfolio text not null default ''," +
					"paypal text not null default '');");
			statement.execute();

			return true;
		} catch (Exception e) {
			logger.error("Failed to create database tables!", e);
			return false;
		} finally {
			this.close(connection, statement, null);
		}
 	}

	public void close(Connection connection, PreparedStatement statement, ResultSet resultSet) {

		if (resultSet != null) {
			try {
				resultSet.close();
			} catch (Exception e) {
				logger.error("", e);
			}
		}

		if (statement != null) {
			try {
				statement.close();
			} catch (Exception e) {
				logger.error("", e);
			}
		}

		if (connection != null) {
			try {
				connection.close();
			} catch (Exception e) {
				logger.error("", e);
			}
		}
	}
}

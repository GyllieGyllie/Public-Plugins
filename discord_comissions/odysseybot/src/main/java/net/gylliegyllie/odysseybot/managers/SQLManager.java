package net.gylliegyllie.odysseybot.managers;

import net.gylliegyllie.odysseybot.tickets.entities.Ticket;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;

public class SQLManager {

	private final static Logger logger = LoggerFactory.getLogger(SQLManager.class);

	private final String url;

	private long nextID;

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
		ResultSet resultSet = null;

		try {

			connection = this.getConnection();

			statement = connection.prepareStatement("create table if not exists members (" +
					"id bigint not null primary key," +
					"portfolio text not null default '???'," +
					"paypal text not null default '???');");
			statement.execute();;

			statement = connection.prepareStatement("create table if not exists tickets (" +
					"id bigint not null primary key," +
					"creation_time timestamp not null," +
					"owner bigint not null," +
					"channel_id bigint not null default -1," +
					"state text not null default 'CREATED'," +
					"type text not null default 'UNKNOWN'," +
					"category text not null default ''," +
					"description text not null default ''," +
					"deadline text not null default ''," +
					"extra text not null default '', " +
					"claimer bigint not null default -1);");
			statement.execute();

			statement = connection.prepareStatement("select id from tickets order by id desc limit 1;");
			resultSet = statement.executeQuery();

			if (resultSet.next()) {
				this.nextID = resultSet.getLong("id") + 1;
			} else {
				this.nextID = 1L;
			}

			return true;
		} catch (Exception e) {
			logger.error("Failed to create database tables!", e);
			return false;
		} finally {
			this.close(connection, statement, resultSet);
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

	public long insertNewTicket(Long time, Long owner) throws SQLException {

		Connection connection = null;
		PreparedStatement statement = null;

		try {

			connection = this.getConnection();

			statement = connection.prepareStatement("INSERT INTO tickets (id, creation_time, owner) VALUES (?, ?, ?);");
			statement.setLong(1, this.nextID);
			statement.setTimestamp(2, new Timestamp(time));
			statement.setLong(3, owner);

			statement.execute();

		} finally {
			this.close(connection, statement, null);
		}

		return this.nextID++;
	}

	public void setChannel(Ticket ticket) {

		Connection connection = null;
		PreparedStatement statement = null;

		try {

			connection = this.getConnection();

			statement = connection.prepareStatement("UPDATE tickets SET channel_id = ? WHERE id = ?;");
			statement.setLong(1, ticket.getChannel().getIdLong());
			statement.setLong(2, ticket.getId());

			statement.execute();

		} catch (Exception e) {
			logger.error("Failed to update ticket channel in db!", e);
		} finally {
			this.close(connection, statement, null);
		}

	}

	public void updateTicketState(Ticket ticket) {

		Connection connection = null;
		PreparedStatement statement = null;

		try {

			connection = this.getConnection();

			statement = connection.prepareStatement("UPDATE tickets SET state = ? WHERE id = ?;");
			statement.setString(1, ticket.getState().name());
			statement.setLong(2, ticket.getId());

			statement.execute();

		} catch (Exception e) {
			logger.error("Failed to update ticket status in db!", e);
		} finally {
			this.close(connection, statement, null);
		}

	}

	public void updateTicketType(Ticket ticket) {

		Connection connection = null;
		PreparedStatement statement = null;

		try {

			connection = this.getConnection();

			statement = connection.prepareStatement("UPDATE tickets SET type = ? WHERE id = ?;");
			statement.setString(1, ticket.getType().name());
			statement.setLong(2, ticket.getId());

			statement.execute();

		} catch (Exception e) {
			logger.error("Failed to update ticket type in db!", e);
		} finally {
			this.close(connection, statement, null);
		}

	}

	public void updateTicketCategory(Ticket ticket) {

		Connection connection = null;
		PreparedStatement statement = null;

		try {

			connection = this.getConnection();

			statement = connection.prepareStatement("UPDATE tickets SET category = ? WHERE id = ?;");
			statement.setString(1, ticket.getCategory());
			statement.setLong(2, ticket.getId());

			statement.execute();

		} catch (Exception e) {
			logger.error("Failed to update ticket category in db!", e);
		} finally {
			this.close(connection, statement, null);
		}

	}

	public void updateTicketDescription(Ticket ticket) {

		Connection connection = null;
		PreparedStatement statement = null;

		try {

			connection = this.getConnection();

			statement = connection.prepareStatement("UPDATE tickets SET description = ? WHERE id = ?;");
			statement.setString(1, ticket.getDescription());
			statement.setLong(2, ticket.getId());

			statement.execute();

		} catch (Exception e) {
			logger.error("Failed to update ticket description in db!", e);
		} finally {
			this.close(connection, statement, null);
		}

	}
}

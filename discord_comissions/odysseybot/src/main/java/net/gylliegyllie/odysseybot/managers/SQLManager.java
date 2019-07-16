package net.gylliegyllie.odysseybot.managers;

import net.gylliegyllie.odysseybot.tickets.entities.Ticket;
import net.gylliegyllie.odysseybot.tickets.entities.TicketState;
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
			statement.execute();

			statement = connection.prepareStatement("create table if not exists tickets (" +
					"id bigint not null primary key," +
					"creation_time timestamp not null," +
					"owner bigint not null," +
					"channel_id bigint not null default -1," +
					"commission_channel_id bigint not null default -1," +
					"state text not null default 'CREATED'," +
					"type text not null default 'UNKNOWN'," +
					"category text not null default ''," +
					"description text not null default ''," +
					"deadline text not null default ''," +
					"extra text not null default '', " +
					"claimer bigint not null default -1," +
					"price int not null default -1," +
					"final_description text not null default ''," +
					"final_deadline text not null default ''," +
					"commission_message bigint not null default -1," +
					"started boolean not null default false," +
					"completed boolean not null default false," +
					"image text not null default ''," +
					"review_manager int not null default -1," +
					"review_builder int not null default -1," +
					"review_show_image boolean not null default false);");
			statement.execute();

			statement = connection.prepareStatement("create table if not exists quotes (" +
					"ticket_id bigint not null," +
					"staff_id bigint not null," +
					"price int not null," +
					"primary key(ticket_id, staff_id));");
			statement.execute();

			statement = connection.prepareStatement("create table if not exists builders (" +
					"staff_id bigint not null," +
					"ticket_id bigint not null," +
					"disallowed boolean not null default false," +
					"unfinished boolean not null default false," +
					"primary key (staff_id, ticket_id));");
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

	public String getPortfolio(Long id) {

		Connection connection = null;
		PreparedStatement statement = null;
		ResultSet resultSet = null;

		try {

			connection = this.getConnection();

			statement = connection.prepareStatement("SELECT portfolio FROM members WHERE id = ?");
			statement.setLong(1, id);

			resultSet = statement.executeQuery();

			if (resultSet.next()) {
				return resultSet.getString("portfolio");
			}

		} catch (Exception e) {
			logger.error("Failed to fetch portfolio from db!", e);
		} finally {
			this.close(connection, statement, resultSet);
		}

		return "";
	}

	public String getPaypal(Long id) {

		Connection connection = null;
		PreparedStatement statement = null;
		ResultSet resultSet = null;

		try {

			connection = this.getConnection();

			statement = connection.prepareStatement("SELECT paypal FROM members WHERE id = ?");
			statement.setLong(1, id);

			resultSet = statement.executeQuery();

			if (resultSet.next()) {
				return resultSet.getString("paypal");
			}

		} catch (Exception e) {
			logger.error("Failed to fetch paypal from db!", e);
		} finally {
			this.close(connection, statement, resultSet);
		}

		return "";
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

	public void setCommissionChannel(Ticket ticket) {

		Connection connection = null;
		PreparedStatement statement = null;

		try {

			connection = this.getConnection();

			statement = connection.prepareStatement("UPDATE tickets SET commission_channel_id = ? WHERE id = ?;");
			statement.setLong(1, ticket.getCommissionChannel().getIdLong());
			statement.setLong(2, ticket.getId());

			statement.execute();

		} catch (Exception e) {
			logger.error("Failed to update ticket commission channel in db!", e);
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

	public void updateTicketDeadline(Ticket ticket) {

		Connection connection = null;
		PreparedStatement statement = null;

		try {

			connection = this.getConnection();

			statement = connection.prepareStatement("UPDATE tickets SET deadline = ? WHERE id = ?;");
			statement.setString(1, ticket.getDeadline());
			statement.setLong(2, ticket.getId());

			statement.execute();

		} catch (Exception e) {
			logger.error("Failed to update ticket deadline in db!", e);
		} finally {
			this.close(connection, statement, null);
		}

	}

	public void updateTicketExtra(Ticket ticket) {

		Connection connection = null;
		PreparedStatement statement = null;

		try {

			connection = this.getConnection();

			statement = connection.prepareStatement("UPDATE tickets SET extra = ? WHERE id = ?;");
			statement.setString(1, ticket.getExtra());
			statement.setLong(2, ticket.getId());

			statement.execute();

		} catch (Exception e) {
			logger.error("Failed to update ticket extra in db!", e);
		} finally {
			this.close(connection, statement, null);
		}

	}

	public void updateTicketClaimer(Ticket ticket) {

		Connection connection = null;
		PreparedStatement statement = null;

		try {

			connection = this.getConnection();

			statement = connection.prepareStatement("UPDATE tickets SET claimer = ? WHERE id = ?;");
			statement.setLong(1, ticket.getClaimer());
			statement.setLong(2, ticket.getId());

			statement.execute();

		} catch (Exception e) {
			logger.error("Failed to update ticket claimer in db!", e);
		} finally {
			this.close(connection, statement, null);
		}

	}

	public void updateTicketPrice(Ticket ticket) {

		Connection connection = null;
		PreparedStatement statement = null;

		try {

			connection = this.getConnection();

			statement = connection.prepareStatement("UPDATE tickets SET price = ? WHERE id = ?;");
			statement.setInt(1, ticket.getPrice());
			statement.setLong(2, ticket.getId());

			statement.execute();

		} catch (Exception e) {
			logger.error("Failed to update ticket price in db!", e);
		} finally {
			this.close(connection, statement, null);
		}

	}

	public void updateTicketFinalInfo(Ticket ticket) {

		Connection connection = null;
		PreparedStatement statement = null;

		try {

			connection = this.getConnection();

			statement = connection.prepareStatement("UPDATE tickets SET final_description = ?, final_deadline = ? WHERE id = ?;");
			statement.setString(1, ticket.getFinalDescription());
			statement.setString(2, ticket.getFinalDeadline());
			statement.setLong(3, ticket.getId());

			statement.execute();

		} catch (Exception e) {
			logger.error("Failed to update ticket final info in db!", e);
		} finally {
			this.close(connection, statement, null);
		}

	}

	public void updateTicketCommissionMessage(Ticket ticket) {

		Connection connection = null;
		PreparedStatement statement = null;

		try {

			connection = this.getConnection();

			statement = connection.prepareStatement("UPDATE tickets SET commission_message = ? WHERE id = ?;");
			statement.setLong(1, ticket.getCommissionMessage());
			statement.setLong(2, ticket.getId());

			statement.execute();

		} catch (Exception e) {
			logger.error("Failed to update ticket commission message in db!", e);
		} finally {
			this.close(connection, statement, null);
		}

	}

	public void updateTicketStarted(Ticket ticket) {

		Connection connection = null;
		PreparedStatement statement = null;

		try {

			connection = this.getConnection();

			statement = connection.prepareStatement("UPDATE tickets SET started = ? WHERE id = ?;");
			statement.setBoolean(1, ticket.isStarted());
			statement.setLong(2, ticket.getId());

			statement.execute();

		} catch (Exception e) {
			logger.error("Failed to update ticket started in db!", e);
		} finally {
			this.close(connection, statement, null);
		}

	}

	public void updateTicketCompleted(Ticket ticket) {

		Connection connection = null;
		PreparedStatement statement = null;

		try {

			connection = this.getConnection();

			statement = connection.prepareStatement("UPDATE tickets SET completed = ? WHERE id = ?;");
			statement.setBoolean(1, ticket.isCompleted());
			statement.setLong(2, ticket.getId());

			statement.execute();

		} catch (Exception e) {
			logger.error("Failed to update ticket completed in db!", e);
		} finally {
			this.close(connection, statement, null);
		}

	}

	public void addBuilder(Ticket ticket, Long builder) {

		Connection connection = null;
		PreparedStatement statement = null;

		try {

			connection = this.getConnection();

			statement = connection.prepareStatement("INSERT INTO builders (staff_id, ticket_id) VALUES (?, ?);");
			statement.setLong(1, builder);
			statement.setLong(2, ticket.getId());

			statement.execute();

		} catch (Exception e) {
			logger.error("Failed to insert builder in db!", e);
		} finally {
			this.close(connection, statement, null);
		}

	}

	public void deleteBuilder(Ticket ticket, Long builder) {

		Connection connection = null;
		PreparedStatement statement = null;

		try {

			connection = this.getConnection();

			if (ticket.getState() == TicketState.AWAITING_BUILDER_CONFIRM) {
				statement = connection.prepareStatement("UPDATE builders SET disallowed = true WHERE staff_id = ? AND ticket_id = ?;");
			} else {
				statement = connection.prepareStatement("UPDATE builders SET unfinished = true WHERE staff_id = ? AND ticket_id = ?;");
			}

			statement.setLong(1, builder);
			statement.setLong(2, ticket.getId());

			statement.execute();

		} catch (Exception e) {
			logger.error("Failed to remove builder in db!", e);
		} finally {
			this.close(connection, statement, null);
		}

	}

	public boolean isAllowedBuilder(Ticket ticket, Long builder) {

		Connection connection = null;
		PreparedStatement statement = null;
		ResultSet resultSet = null;

		try {

			connection = this.getConnection();

			statement = connection.prepareStatement("SELECT disallowed FROM builders WHERE staff_id = ? AND ticket_id = ?");
			statement.setLong(1, builder);
			statement.setLong(2, ticket.getId());

			resultSet = statement.executeQuery();

			if (resultSet.next()) {
				return !resultSet.getBoolean("disallowed");
			}

			return true;

		} catch (Exception e) {
			logger.error("Failed to fetch allowance builder from db!", e);
		} finally {
			this.close(connection, statement, resultSet);
		}

		return false;

	}

	public void updateTicketImage(Ticket ticket) {

		Connection connection = null;
		PreparedStatement statement = null;

		try {

			connection = this.getConnection();

			statement = connection.prepareStatement("UPDATE tickets SET image = ? WHERE id = ?;");
			statement.setString(1, ticket.getImage());
			statement.setLong(2, ticket.getId());

			statement.execute();

		} catch (Exception e) {
			logger.error("Failed to update ticket image in db!", e);
		} finally {
			this.close(connection, statement, null);
		}

	}

	public void updateManagerRating(Ticket ticket) {

		Connection connection = null;
		PreparedStatement statement = null;

		try {

			connection = this.getConnection();

			statement = connection.prepareStatement("UPDATE tickets SET review_manager = ? WHERE id = ?;");
			statement.setInt(1, ticket.getManagerRating());
			statement.setLong(2, ticket.getId());

			statement.execute();

		} catch (Exception e) {
			logger.error("Failed to update ticket manager rating in db!", e);
		} finally {
			this.close(connection, statement, null);
		}

	}

	public void updateBuilderRating(Ticket ticket) {

		Connection connection = null;
		PreparedStatement statement = null;

		try {

			connection = this.getConnection();

			statement = connection.prepareStatement("UPDATE tickets SET review_builder = ? WHERE id = ?;");
			statement.setInt(1, ticket.getBuilderRating());
			statement.setLong(2, ticket.getId());

			statement.execute();

		} catch (Exception e) {
			logger.error("Failed to update ticket builder rating in db!", e);
		} finally {
			this.close(connection, statement, null);
		}

	}

	public void updateShowPics(Ticket ticket) {

		Connection connection = null;
		PreparedStatement statement = null;

		try {

			connection = this.getConnection();

			statement = connection.prepareStatement("UPDATE tickets SET review_show_image = ? WHERE id = ?;");
			statement.setBoolean(1, ticket.isShowPics());
			statement.setLong(2, ticket.getId());

			statement.execute();

		} catch (Exception e) {
			logger.error("Failed to update ticket show pics in db!", e);
		} finally {
			this.close(connection, statement, null);
		}

	}
}

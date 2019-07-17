package net.gylliegyllie.odysseybot.discord.commands;

import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.entities.Member;
import net.dv8tion.jda.core.entities.Role;
import net.dv8tion.jda.core.events.message.MessageReceivedEvent;
import net.gylliegyllie.odysseybot.Bot;
import net.gylliegyllie.odysseybot.util.MessageUtil;
import org.joda.time.DateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.awt.Color;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.List;

public class RankCommand extends DiscordCommand {

	private final static Logger logger = LoggerFactory.getLogger(RankCommand.class);

	private final Bot bot;

	public RankCommand(Bot bot) {
		super(false, bot.getBot().ownerRole, bot.getBot().generalManagerRole);
		this.bot = bot;
	}

	@Override
	public void runCommand(MessageReceivedEvent event, String command, String[] args) {

		Connection connection = null;
		PreparedStatement statement = null;
		ResultSet resultSet = null;

		try {

			connection = this.bot.getSqlManager().getConnection();

			statement = connection.prepareStatement("SELECT builders.staff_id, COUNT(*) as count FROM builders INNER JOIN tickets ON tickets.id = builders.ticket_id WHERE tickets.completed = true ORDER BY count;");

			resultSet = statement.executeQuery();

			StringBuilder builder = new StringBuilder("__** Rankings **__");

			while (resultSet.next()) {
				String line = "**" + this.bot.getBot().getJda().getUserById(resultSet.getLong("staff_id")).getAsMention() + "**: "
						+ resultSet.getLong("count") + " commissions";

				if (builder.length() + line.length() >= 2000) {
					MessageUtil.sendMessage(event, builder.toString(), false);
					builder = new StringBuilder();
				}

				builder.append("\n").append(line);
			}

			MessageUtil.sendMessage(event, builder.toString(), false);

		} catch (Exception e) {
			logger.error("error in rank command", e);
			MessageUtil.sendMessage(event, "Something went wrong loading the info!", true);
		} finally {
			this.bot.getSqlManager().close(connection, statement, resultSet);
		}
	}

}

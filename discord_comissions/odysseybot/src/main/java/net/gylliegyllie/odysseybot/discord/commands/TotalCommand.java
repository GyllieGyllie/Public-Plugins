package net.gylliegyllie.odysseybot.discord.commands;

import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.entities.Member;
import net.dv8tion.jda.core.entities.Role;
import net.dv8tion.jda.core.events.message.MessageReceivedEvent;
import net.gylliegyllie.odysseybot.Bot;
import net.gylliegyllie.odysseybot.util.MessageUtil;
import org.joda.time.DateTime;
import org.joda.time.DurationFieldType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.awt.Color;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.Calendar;
import java.util.List;
import java.util.TimeZone;

public class TotalCommand extends DiscordCommand {

	private final static Logger logger = LoggerFactory.getLogger(TotalCommand.class);

	private final Bot bot;

	public TotalCommand(Bot bot) {
		super(false);
		this.bot = bot;
	}

	@Override
	public void runCommand(MessageReceivedEvent event, String command, String[] args) {

		Connection connection = null;
		PreparedStatement statement = null;
		ResultSet resultSet = null;

		try {

			connection = this.bot.getSqlManager().getConnection();

			statement = connection.prepareStatement("SELECT price, creation_time FROM tickets WHERE completed = true;");

			resultSet = statement.executeQuery();

			int total = 0, totalIncome = 0, month = 0, monthIncome = 0;
			double totalFee = 0, monthFee = 0;
			String prefix = "Month";

			DateTime now = DateTime.now().withDayOfMonth(1).withHourOfDay(0).withMinuteOfHour(0).withSecondOfMinute(0);
			long lower, upper;

			if (event.getMessage().getContentRaw().contains("-l")) {
				lower = now.minusMonths(1).getMillis();
				upper = now.minusDays(1).withHourOfDay(23).withMinuteOfHour(59).withSecondOfMinute(59).getMillis();
				prefix = "Last Month";
			} else {
				lower = now.getMillis();
				upper = now.plusMonths(1).getMillis();
			}

			while (resultSet.next()) {
				total++;
				totalIncome += resultSet.getInt("price");
				totalFee += ((double) resultSet.getInt("price")) * 0.075;

				if (resultSet.getLong("creation_time") >= lower && resultSet.getLong("creation_time") <= upper) {
					month++;
					monthIncome += resultSet.getInt("price");
					monthFee += ((double) resultSet.getInt("price")) * 0.075;
				}
			}

			EmbedBuilder builder = new EmbedBuilder()
					.setColor(new Color(33,119,254))
					.setTitle("Odyssey Ticket System")
					.addField("Total Commissions", String.valueOf(total), true)
					.addField("Total Income", String.valueOf(totalIncome), true)
					.addField("Total Fee", String.valueOf(totalFee), true)
					.addField(prefix + " Commissions", String.valueOf(month), true)
					.addField(prefix + " Income", String.valueOf(monthIncome), true)
					.addField(prefix + " Fee", String.valueOf(monthFee), true);

			MessageUtil.sendMessage(event, builder.build());

		} catch (Exception e) {
			logger.error("error in total command", e);
			MessageUtil.sendMessage(event, "Something went wrong loading the info!", true);
		} finally {
			this.bot.getSqlManager().close(connection, statement, resultSet);
		}
	}

}

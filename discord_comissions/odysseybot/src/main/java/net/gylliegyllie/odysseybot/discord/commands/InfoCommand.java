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

public class InfoCommand extends DiscordCommand {

	private final static Logger logger = LoggerFactory.getLogger(InfoCommand.class);

	private final Bot bot;

	public InfoCommand(Bot bot) {
		super(false);
		this.bot = bot;
	}

	@Override
	public void runCommand(MessageReceivedEvent event, String command, String[] args) {

		List<Member> mentions = event.getMessage().getMentionedMembers();

		if (mentions.size() != 1) {
			MessageUtil.sendMessage(event, String.format("Please use `%sinfo @user` to get the users info!", Bot.PREFIX), true);
			return;
		}

		Member member = mentions.get(0);
		List<Role> roles = member.getRoles();

		if (!roles.contains(this.bot.getBot().managerRole) && !roles.contains(this.bot.getBot().builderRole)
			&& !roles.contains(this.bot.getBot().terraformerRole)) {
			MessageUtil.sendMessage(event, "Can't use that command for this user.", true);
			return;
		}

		boolean admin = event.getMember().getRoles().contains(this.bot.getBot().ownerRole) || event.getMember().getRoles().contains(this.bot.getBot().generalManagerRole);

		Connection connection = null;
		PreparedStatement statement = null;
		ResultSet resultSet = null;

		PreparedStatement statement1 = null;
		ResultSet resultSet1 = null;

		try {

			connection = this.bot.getSqlManager().getConnection();

			statement = connection.prepareStatement("SELECT tickets.id, tickets.state, tickets.price, tickets.started, tickets.completed, tickets.claimer, tickets.claim_time AS manager_claim_time, tickets.review_manager, tickets.review_builder, builders.staff_id, builders.claim_time FROM tickets INNER JOIN builders ON (builders.ticket_id = tickets.id) WHERE (builders.staff_id = ? OR tickets.claimer = ?) AND tickets.type = 'COMMISSION';");
			statement.setLong(1, member.getUser().getIdLong());
			statement.setLong(2, member.getUser().getIdLong());

			resultSet = statement.executeQuery();

			int commissions = 0, finished = 0, left = 0, month = 0, rated = 0;
			double rating = 0, totalIncome = 0, monthlyIncome = 0, owedFee = 0;
			long lastClaim = 0;

			DateTime dmonth = DateTime.now().withDayOfMonth(1).withHourOfDay(0).withMinuteOfHour(0).withSecondOfMinute(0);
			long timestamp = dmonth.getMillis();

			while (resultSet.next()) {

				if (resultSet.getBoolean("started")) {
					commissions++;

					if (resultSet.getBoolean("completed")) {
						finished++;
					} else {
						left++;
					}

					long claim = -1L;
					double income = 0;
					double price = ((double) resultSet.getInt("price"));

					if (resultSet.getLong("claimer") == member.getUser().getIdLong()) {
						claim = resultSet.getLong("manager_claim_time");

						if (resultSet.getInt("review_manager") > 0) {
							rating += resultSet.getInt("review_manager");
							rated++;
						}

						if (resultSet.getBoolean("completed")) {
							double fee = price * 0.075;
							income += fee;
							owedFee += fee;
						}
					}

					if (resultSet.getLong("staff_id") == member.getUser().getIdLong()){
						claim = resultSet.getLong("claim_time");

						if (resultSet.getInt("review_builder") > 0) {
							rating += resultSet.getInt("review_builder");
							rated++;
						}

						if (resultSet.getBoolean("completed")) {
							statement1 = connection.prepareStatement("SELECT COUNT(*) AS count FROM builders WHERE ticket_id = ?;");
							statement1.setLong(1, resultSet.getLong("id"));
							resultSet1 = statement1.executeQuery();

							if (resultSet1.next()) {
								income += (price * 0.85) / resultSet1.getInt("count");
							} else {
								income += price * 0.85;
							}
						}

					}

					totalIncome += income;

					if (claim > timestamp) {
						month++;
						monthlyIncome += income;
					}

					if (claim > lastClaim) {
						lastClaim = claim;
					}
				}
			}

			EmbedBuilder builder = new EmbedBuilder()
					.setColor(new Color(33,119,254))
					.setTitle("Odyssey Ticket System")
					.setDescription(member.getAsMention() + "'s stats")
					.addField("Total Claimed", String.valueOf(commissions), true)
					.addField("Total Completed", String.valueOf(finished), true)
					.addField("Total Left", String.valueOf(left), true)
					.addField("Claimed this month", String.valueOf(month), true)
					.addField("Average Rating", String.valueOf(rating / rated), true);

			if (admin) {
				builder.addField("Total Earned", String.valueOf(totalIncome), true)
						.addField("Month Earned", String.valueOf(monthlyIncome), true);

				if (roles.contains(this.bot.getBot().managerRole)) {
					builder.addField("Total Owed", String.valueOf(owedFee), true);
				}

				builder.addField("Last Claim", new DateTime(lastClaim).toString("yyyy/MM/dd HH:mm:ss"), false);
			}

			if (roles.contains(this.bot.getBot().builderRole) || roles.contains(this.bot.getBot().terraformerRole)) {
				String portfolio = this.bot.getSqlManager().getPortfolio(member.getUser().getIdLong());
				builder.addField("Portfolio", portfolio, false);
			}

			if (admin) {
				String paypal = this.bot.getSqlManager().getPaypal(member.getUser().getIdLong());
				builder.addField("PayPal", paypal, false);
			}

			MessageUtil.sendMessage(event, builder.build());

		} catch (Exception e) {
			logger.error("error in info command", e);
			MessageUtil.sendMessage(event, "Something went wrong loading the info!", true);
		} finally {
			this.bot.getSqlManager().close(null, statement1, resultSet1);
			this.bot.getSqlManager().close(connection, statement, resultSet);
		}
	}

}

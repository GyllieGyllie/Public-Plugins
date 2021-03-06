package net.gylliegyllie.odysseybot.discord.commands;

import net.dv8tion.jda.core.events.message.MessageReceivedEvent;
import net.gylliegyllie.odysseybot.Bot;
import net.gylliegyllie.odysseybot.util.MessageUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.util.regex.Pattern;

public class UpdateInfoCommand extends DiscordCommand {

	private final Logger logger = LoggerFactory.getLogger(UpdateInfoCommand.class);

	public UpdateInfoCommand(Bot bot) {
		super(false, bot.getBot().builderRole, bot.getBot().terraformerRole);
	}

	@Override
	public void runCommand(MessageReceivedEvent event, String command, String[] args) {

		if (args.length != 2) {
			MessageUtil.sendMessage(event, String.format("Use `%supdateinfo -p <link>` to update your portfolio or `%supdateinfo -pp <email>` to update your PayPal", Bot.PREFIX, Bot.PREFIX), true);
			return;
		}

		String sub = args[0];

		switch (sub) {
			case "-p":
				String portfolio = args[1];

				if (!this.IMGUR.matcher(portfolio).matches()) {
					MessageUtil.sendMessage(event, "Invalid link provided! Please provide an Imgur gallery link!", true);
					return;
				}

				Connection connection = null;
				PreparedStatement statement = null;

				try {

					connection = Bot.getInstance().getSqlManager().getConnection();

					statement = connection.prepareStatement("INSERT INTO members (id, portfolio) VALUES (?, ?) ON CONFLICT(id) DO UPDATE SET portfolio = ?;");
					statement.setLong(1, event.getAuthor().getIdLong());
					statement.setString(2, portfolio);
					statement.setString(3, portfolio);

					statement.execute();

					MessageUtil.sendMessage(event, "Successfully updated your portfolio!", true);

				} catch (Exception e) {
					logger.error("", e);
					MessageUtil.sendMessage(event, "Something went wrong updating your portfolio!", true);
				} finally {
					Bot.getInstance().getSqlManager().close(connection, statement, null);
				}

				break;

			case "-pp":
				String email = args[1];

				if (!this.MAIL.matcher(email).matches()) {
					MessageUtil.sendMessage(event, "Invalid email provided!", true);
					return;
				}

				connection = null;
				statement = null;

				try {

					connection = Bot.getInstance().getSqlManager().getConnection();

					statement = connection.prepareStatement("INSERT INTO members (id, paypal) VALUES (?, ?) ON CONFLICT(id) DO UPDATE SET paypal = ?;");
					statement.setLong(1, event.getAuthor().getIdLong());
					statement.setString(2, email);
					statement.setString(3, email);

					statement.execute();

					MessageUtil.sendMessage(event, "Successfully updated your PayPal mail!", true);

				} catch (Exception e) {
					logger.error("", e);
					MessageUtil.sendMessage(event, "Something went wrong updating your PayPal!", true);
				} finally {
					Bot.getInstance().getSqlManager().close(connection, statement, null);
				}

				break;
			default:
				MessageUtil.sendMessage(event, "Invalid usage!", true);
		}
	}
}

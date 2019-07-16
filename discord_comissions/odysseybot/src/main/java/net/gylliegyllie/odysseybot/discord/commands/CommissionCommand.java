package net.gylliegyllie.odysseybot.discord.commands;

import net.dv8tion.jda.core.entities.ChannelType;
import net.dv8tion.jda.core.entities.TextChannel;
import net.dv8tion.jda.core.events.message.MessageReceivedEvent;
import net.gylliegyllie.odysseybot.Bot;
import net.gylliegyllie.odysseybot.util.MessageUtil;

public class CommissionCommand extends DiscordCommand {

	private final Bot bot;

	public CommissionCommand(Bot bot) {
		super(false, bot.getBot().ownerRole, bot.getBot().generalManagerRole, bot.getBot().managerRole);

		this.bot = bot;
	}

	@Override
	public void runCommand(MessageReceivedEvent event, String command, String[] args) {

		if (event.getChannel().getType() != ChannelType.TEXT) {
			return;
		}

		TextChannel channel = (TextChannel) event.getChannel();

		if (!channel.getName().startsWith("ticket_")) {
			return;
		}

		if (args.length == 0) {
			MessageUtil.sendMessage(event, String.format("Use `%scomm <price|quote> -d <details> -dl <deadline>` to set a price or start the quoting process!", Bot.PREFIX), true);
			return;
		}

		Long ticketID = Long.valueOf(channel.getName().split("_")[1]);

		String current = "";
		StringBuilder builder = new StringBuilder();

		String details = "";
		String deadline = "";
		String type = "";

		for (int t = 1; t < args.length; t++) {
			String arg = args[t];

			if (arg.toLowerCase().startsWith("-dl")) {

				if (current.equalsIgnoreCase("-d")) {
					details = builder.toString();
					builder = new StringBuilder();
				}

				current = "-dl";

			} else if (arg.toLowerCase().startsWith("-d")) {

				if (current.equalsIgnoreCase("-dl")) {
					deadline = builder.toString();
					builder = new StringBuilder();
				}

				current = "-d";

			} else if (arg.toLowerCase().equalsIgnoreCase("-b")
				|| arg.toLowerCase().equalsIgnoreCase("-t")) {

				if (current.equalsIgnoreCase("-d")) {
					details = builder.toString();
					builder = new StringBuilder();
				} else if (current.equalsIgnoreCase("-dl")) {
					deadline = builder.toString();
					builder = new StringBuilder();
				}

				if (arg.toLowerCase().equalsIgnoreCase("-b")) {
					type = "Builder";
				} else if (arg.toLowerCase().equalsIgnoreCase("-t")) {
					type = "Terraformer";
				}

				current = arg;

			} else {
				builder.append(arg).append(" ");
			}
		}

		if (current.equalsIgnoreCase("-dl")) {
			deadline = builder.toString();
		} else if (current.equalsIgnoreCase("-d")) {
			details = builder.toString();
		}

		if (details.isEmpty()) {
			MessageUtil.sendMessage(event, "Please add the project details using -d <details>", true);
			return;
		} else if (deadline.isEmpty()) {
			MessageUtil.sendMessage(event, "Please add the project deadline using -dl <deadline>", true);
			return;
		}

		if (args[0].equalsIgnoreCase("quote")) {

		} else {

			String sValue = args[0];

			if (sValue.startsWith("$")) {
				sValue = sValue.substring(1);
			}

			Integer price;

			try {
				price = Integer.valueOf(sValue);
			} catch (NumberFormatException e) {
				MessageUtil.sendMessage(event, "Invalid price amount entered!", true);
				return;
			}

			this.bot.getTicketManager().setPrice(ticketID, price, details, deadline, type);
		}

		event.getMessage().delete().queue();
	}
}

package net.gylliegyllie.odysseybot.discord.commands;

import net.dv8tion.jda.core.entities.ChannelType;
import net.dv8tion.jda.core.entities.TextChannel;
import net.dv8tion.jda.core.events.message.MessageReceivedEvent;
import net.gylliegyllie.odysseybot.Bot;
import net.gylliegyllie.odysseybot.tickets.entities.Ticket;
import net.gylliegyllie.odysseybot.util.MessageUtil;

public class QuoteCommand extends DiscordCommand {

	private final Bot bot;

	public QuoteCommand(Bot bot) {
		super(false, bot.getBot().ownerRole, bot.getBot().generalManagerRole, bot.getBot().builderRole,
				bot.getBot().terraformerRole);

		this.bot = bot;
	}

	@Override
	public void runCommand(MessageReceivedEvent event, String command, String[] args) {

		if (event.getChannel().getType() != ChannelType.TEXT) {
			return;
		}

		TextChannel channel = (TextChannel) event.getChannel();

		if (!channel.getName().startsWith("commission_")) {
			return;
		}

		if (args.length == 0) {
			MessageUtil.sendMessage(event, String.format("Please use `%squote <price>` to add a quote.", Bot.PREFIX), true);
			return;
		}

		Integer price = this.getPrice(event, args[0]);

		if (price <= 0) {
			return;
		}

		Ticket ticket = this.bot.getTicketManager().getTicket(Long.valueOf(channel.getName().split("_")[1]));

		if (ticket == null) {
			MessageUtil.sendMessage(event, "Failed to find ticket!", true);
			return;
		}

		ticket.addQuote(event.getMember().getUser().getIdLong(), price);
		this.bot.getSqlManager().addQuote(ticket, event.getMember().getUser().getIdLong(), price);

		MessageUtil.sendMessage(event, "Quote registered!", true);
	}
}

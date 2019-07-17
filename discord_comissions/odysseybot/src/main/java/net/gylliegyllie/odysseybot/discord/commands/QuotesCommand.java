package net.gylliegyllie.odysseybot.discord.commands;

import net.dv8tion.jda.core.entities.ChannelType;
import net.dv8tion.jda.core.entities.TextChannel;
import net.dv8tion.jda.core.events.message.MessageReceivedEvent;
import net.gylliegyllie.odysseybot.Bot;
import net.gylliegyllie.odysseybot.tickets.entities.Ticket;
import net.gylliegyllie.odysseybot.util.MessageUtil;

public class QuotesCommand extends DiscordCommand {

	private final Bot bot;

	public QuotesCommand(Bot bot) {
		super(false, bot.getBot().ownerRole, bot.getBot().generalManagerRole, bot.getBot().managerRole);

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

		Long ticketID = Long.valueOf(channel.getName().split("_")[1]);

		Ticket ticket = this.bot.getTicketManager().getTicket(ticketID);

		if (ticket.getQuotes().size() == 0) {
			MessageUtil.sendMessage(event, "No quotes available!", true);
			return;
		}

		this.bot.getTicketManager().finishQuoting(ticketID);
	}
}

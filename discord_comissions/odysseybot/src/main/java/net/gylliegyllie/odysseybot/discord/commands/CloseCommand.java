package net.gylliegyllie.odysseybot.discord.commands;

import net.dv8tion.jda.core.entities.ChannelType;
import net.dv8tion.jda.core.entities.TextChannel;
import net.dv8tion.jda.core.events.message.MessageReceivedEvent;
import net.gylliegyllie.odysseybot.Bot;

public class CloseCommand extends DiscordCommand {

	private final Bot bot;

	public CloseCommand(Bot bot) {
		super(false);

		this.bot = bot;
	}

	@Override
	public void runCommand(MessageReceivedEvent event, String command, String[] args) {
		if (!this.verifyInTicket(event)) return;

		this.bot.getTicketManager().closeTicket(Long.valueOf(event.getChannel().getName().split("_")[1]), event.getAuthor());
	}
}

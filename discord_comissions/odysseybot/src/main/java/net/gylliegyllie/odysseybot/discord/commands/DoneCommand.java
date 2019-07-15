package net.gylliegyllie.odysseybot.discord.commands;

import net.dv8tion.jda.core.entities.ChannelType;
import net.dv8tion.jda.core.entities.TextChannel;
import net.dv8tion.jda.core.events.message.MessageReceivedEvent;
import net.gylliegyllie.odysseybot.Bot;

public class DoneCommand extends DiscordCommand {

	private final Bot bot;

	public DoneCommand(Bot bot) {
		super(false, bot.getBot().supportRole, bot.getBot().generalManagerRole, bot.getBot().ownerRole);

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

		this.bot.getTicketManager().finish(Long.valueOf(event.getChannel().getName().split("_")[1]));
	}
}

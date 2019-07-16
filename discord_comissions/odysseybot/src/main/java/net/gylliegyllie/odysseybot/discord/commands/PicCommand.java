package net.gylliegyllie.odysseybot.discord.commands;

import net.dv8tion.jda.core.entities.ChannelType;
import net.dv8tion.jda.core.entities.TextChannel;
import net.dv8tion.jda.core.events.message.MessageReceivedEvent;
import net.gylliegyllie.odysseybot.Bot;
import net.gylliegyllie.odysseybot.util.MessageUtil;

public class PicCommand extends DiscordCommand {

	private final Bot bot;

	public PicCommand(Bot bot) {
		super(false, bot.getBot().ownerRole, bot.getBot().generalManagerRole, bot.getBot().managerRole,
				bot.getBot().terraformerRole, bot.getBot().builderRole);

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
			MessageUtil.sendMessage(event, String.format("Please use `%spic <imgurlink>` to set the images.", Bot.PREFIX), true);
			return;
		}

		String link = args[0];

		if (!this.IMGUR.matcher(link).matches()) {
			MessageUtil.sendMessage(event, "Invalid link provide!", true);
			return;
		}

		this.bot.getTicketManager().setPicture(Long.valueOf(event.getChannel().getName().split("_")[1]), link);
		MessageUtil.sendMessage(event, "Images linked!", true);
	}
}

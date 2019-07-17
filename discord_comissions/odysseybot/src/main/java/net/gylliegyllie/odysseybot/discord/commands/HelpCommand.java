package net.gylliegyllie.odysseybot.discord.commands;

import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.entities.ChannelType;
import net.dv8tion.jda.core.events.message.MessageReceivedEvent;
import net.gylliegyllie.odysseybot.Bot;
import net.gylliegyllie.odysseybot.util.MessageUtil;

import java.awt.Color;

public class HelpCommand extends DiscordCommand {

	private final Bot bot;

	public HelpCommand(Bot bot) {
		super(true);
		this.bot = bot;
	}

	@Override
	public void runCommand(MessageReceivedEvent event, String command, String[] args) {
		MessageUtil.sendDM(event.getAuthor(), new EmbedBuilder()
				.setColor(new Color(33,119,254))
				.setTitle("Odyssey Ticket System")
				.setDescription("**General Commands**\n" +
						"\n" +
						"`-info @user` Shows information about the specified Builder / Terraformer\n" +
						"\n" +
						"`-apply` Shows the application link\n" +
						"\n" +
						"`-help` Shows help message in direct messages\n" +
						"\n" +
						"`-close` Closes your ticket\n" +
						"\n" +
						"You can make a commission or support ticket by reaction to the message in " + this.bot.getBot().ticketRequestChannel.getAsMention())
		.build());

		if (event.getChannelType() == ChannelType.TEXT) {
			event.getMessage().addReaction("\uD83D\uDCE9").queue();
		}
	}

}

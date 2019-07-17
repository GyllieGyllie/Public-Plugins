package net.gylliegyllie.odysseybot.discord.commands;

import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.entities.TextChannel;
import net.dv8tion.jda.core.events.message.MessageReceivedEvent;
import net.gylliegyllie.odysseybot.Bot;
import net.gylliegyllie.odysseybot.util.MessageUtil;

import java.awt.Color;

public class EmbedCommand extends DiscordCommand {

	private final Bot bot;

	public EmbedCommand(Bot bot) {
		super(false, bot.getBot().ownerRole, bot.getBot().generalManagerRole);

		this.bot = bot;
	}

	@Override
	public void runCommand(MessageReceivedEvent event, String command, String[] args) {
		if (args.length < 2) {
			MessageUtil.sendMessage(event, String.format("Use `%sembed <channel> <message>` to create an embed!", Bot.PREFIX), true);
			return;
		}

		TextChannel channel = null;

		for (TextChannel channel1 : event.getMessage().getMentionedChannels()) {
			if (args[0].equals(channel1.getAsMention())) {
				channel = channel1;
			}
		}

		if (channel == null) {
			MessageUtil.sendMessage(event, "Please make sure to mention the channel before typing the message!", true);
			return;
		}

		String message = event.getMessage().getContentRaw().substring(event.getMessage().getContentRaw().indexOf(" ", 10));

		channel.sendMessage(new EmbedBuilder()
				.setColor(new Color(33,119,254))
				.setTitle("Odyssey Ticket System")
				.setDescription(message)
				.build())
				.queue();
	}

}

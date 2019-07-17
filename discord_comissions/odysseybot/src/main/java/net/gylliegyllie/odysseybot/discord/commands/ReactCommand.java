package net.gylliegyllie.odysseybot.discord.commands;

import net.dv8tion.jda.core.entities.Emote;
import net.dv8tion.jda.core.entities.Message;
import net.dv8tion.jda.core.entities.TextChannel;
import net.dv8tion.jda.core.events.message.MessageReceivedEvent;
import net.gylliegyllie.odysseybot.Bot;
import net.gylliegyllie.odysseybot.util.MessageUtil;

public class ReactCommand extends DiscordCommand {

	private final Bot bot;

	public ReactCommand(Bot bot) {
		super(false, bot.getBot().ownerRole, bot.getBot().generalManagerRole);

		this.bot = bot;
	}

	@Override
	public void runCommand(MessageReceivedEvent event, String command, String[] args) {
		if (args.length != 3) {
			MessageUtil.sendMessage(event, String.format("Use `%sreact <channel> <message_id> <reaction>` to add a reaction", Bot.PREFIX), true);
			return;
		}

		TextChannel channel = null;

		for (TextChannel channel1 : event.getMessage().getMentionedChannels()) {
			if (args[0].equals(channel1.getAsMention())) {
				channel = channel1;
			}
		}

		if (channel == null) {
			MessageUtil.sendMessage(event, "Please make sure to first argument mentions the channel!", true);
			return;
		}

		long messageID;

		try {
			messageID = Long.valueOf(args[1]);
		} catch (NumberFormatException e) {
			MessageUtil.sendMessage(event, "Invalid message id provided!", true);
			return;
		}

		Message message = channel.getMessageById(messageID).complete();

		if (message == null) {
			MessageUtil.sendMessage(event, "Couldn't find that message in said channel!", true);
			return;
		}

		if (event.getMessage().getEmotes().size() > 0) {
			message.addReaction(event.getMessage().getEmotes().get(0)).queue();
		} else {
			message.addReaction(args[2]).queue();
		}
	}

}

package net.gylliegyllie.odysseybot.util;

import net.dv8tion.jda.core.entities.Message;
import net.dv8tion.jda.core.entities.MessageEmbed;
import net.dv8tion.jda.core.entities.User;
import net.dv8tion.jda.core.events.message.MessageReceivedEvent;

public class MessageUtil {

	public static Message sendMessage(MessageReceivedEvent event, String message, boolean reply) {
		if (reply) {
			message = event.getAuthor().getAsMention() + ", " + message;
		}

		return event.getChannel().sendMessage(message).complete();
	}

	public static Message sendMessage(MessageReceivedEvent event, MessageEmbed embed) {
		return event.getChannel().sendMessage(embed).complete();
	}

	public static void sendDM(User user, String message) {
		user.openPrivateChannel().queue(privateChannel -> privateChannel.sendMessage(message).queue());
	}

	public static void sendDM(User user, MessageEmbed embed) {
		user.openPrivateChannel().queue(privateChannel -> privateChannel.sendMessage(embed).queue());
	}
}

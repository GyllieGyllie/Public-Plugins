package net.gylliegyllie.odysseybot.util;

import net.dv8tion.jda.core.entities.Message;
import net.dv8tion.jda.core.events.message.MessageReceivedEvent;

public class MessageUtil {

	public static Message sendMessage(MessageReceivedEvent event, String message, boolean reply) {
		if (reply) {
			message = event.getAuthor().getAsMention() + ", " + message;
		}

		return event.getChannel().sendMessage(message).complete();
	}
}

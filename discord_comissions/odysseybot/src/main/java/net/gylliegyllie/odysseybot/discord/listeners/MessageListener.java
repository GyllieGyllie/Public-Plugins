package net.gylliegyllie.odysseybot.discord.listeners;

import net.dv8tion.jda.core.entities.ChannelType;
import net.dv8tion.jda.core.events.message.MessageReceivedEvent;
import net.dv8tion.jda.core.hooks.ListenerAdapter;
import net.gylliegyllie.odysseybot.Bot;
import net.gylliegyllie.odysseybot.discord.commands.ApplyCommand;
import net.gylliegyllie.odysseybot.discord.commands.DiscordCommand;
import net.gylliegyllie.odysseybot.discord.commands.UpdateInfoCommand;
import net.gylliegyllie.odysseybot.util.MessageUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

public class MessageListener extends ListenerAdapter {

	private final Logger logger = LoggerFactory.getLogger(MessageListener.class);

	private final Map<String, DiscordCommand> commands = new HashMap<>();

	public MessageListener() {
		this.commands.put("apply", new ApplyCommand());
		this.commands.put("updateinfo", new UpdateInfoCommand());
	}

	@Override
	public void onMessageReceived(MessageReceivedEvent event) {
		if (event.getAuthor().isBot()) {
			return;
		}

		String message = event.getMessage().getContentRaw();

		if (message.startsWith(Bot.PREFIX)) {
			String[] args = message.split(" ");
			String syntax = args[0].replace(Bot.PREFIX, "");

			DiscordCommand command = this.commands.get(syntax);

			if (command != null) {

				if (event.getChannelType() == ChannelType.PRIVATE) {

					// Can't be done in private channels
					if (!command.isPrivateCompatible()) {
						MessageUtil.sendMessage(event, "Command cannot be used in private channels!", false);
						return;
					}

				} else {

					// No access
					if (!command.hasAccess(event.getMember())) {
						return;
					}

				}

				command.runCommand(event, syntax, Arrays.copyOfRange(args, 1, args.length));
				logger.info(event.getAuthor().getName() + " executed command: " + message.substring(1));

			}
		}
	}
}

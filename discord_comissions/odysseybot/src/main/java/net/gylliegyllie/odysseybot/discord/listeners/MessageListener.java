package net.gylliegyllie.odysseybot.discord.listeners;

import net.dv8tion.jda.core.entities.ChannelType;
import net.dv8tion.jda.core.events.message.MessageReceivedEvent;
import net.dv8tion.jda.core.hooks.ListenerAdapter;
import net.gylliegyllie.odysseybot.Bot;
import net.gylliegyllie.odysseybot.discord.commands.AddBuilderCommand;
import net.gylliegyllie.odysseybot.discord.commands.AddManagerCommand;
import net.gylliegyllie.odysseybot.discord.commands.AddUserCommand;
import net.gylliegyllie.odysseybot.discord.commands.ApplyCommand;
import net.gylliegyllie.odysseybot.discord.commands.CloseCommand;
import net.gylliegyllie.odysseybot.discord.commands.CloseCommissionCommand;
import net.gylliegyllie.odysseybot.discord.commands.CommissionCommand;
import net.gylliegyllie.odysseybot.discord.commands.DiscordCommand;
import net.gylliegyllie.odysseybot.discord.commands.DoneCommand;
import net.gylliegyllie.odysseybot.discord.commands.EmbedCommand;
import net.gylliegyllie.odysseybot.discord.commands.FinalCommand;
import net.gylliegyllie.odysseybot.discord.commands.FinishedCommand;
import net.gylliegyllie.odysseybot.discord.commands.HelpCommand;
import net.gylliegyllie.odysseybot.discord.commands.InfoCommand;
import net.gylliegyllie.odysseybot.discord.commands.PaidCommand;
import net.gylliegyllie.odysseybot.discord.commands.PicCommand;
import net.gylliegyllie.odysseybot.discord.commands.QuickDoneCommand;
import net.gylliegyllie.odysseybot.discord.commands.QuoteCommand;
import net.gylliegyllie.odysseybot.discord.commands.QuotesCommand;
import net.gylliegyllie.odysseybot.discord.commands.RankCommand;
import net.gylliegyllie.odysseybot.discord.commands.ReactCommand;
import net.gylliegyllie.odysseybot.discord.commands.RemoveBuilderCommand;
import net.gylliegyllie.odysseybot.discord.commands.RemoveManagerCommand;
import net.gylliegyllie.odysseybot.discord.commands.TotalCommand;
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

	private final Bot bot;

	public MessageListener(Bot bot) {
		this.bot = bot;
	}

	public void init() {
		this.commands.put("apply", new ApplyCommand());
		this.commands.put("help", new HelpCommand(this.bot));
		this.commands.put("info", new InfoCommand(this.bot));
		this.commands.put("updateinfo", new UpdateInfoCommand(this.bot));
		this.commands.put("rank", new RankCommand(this.bot));
		this.commands.put("total", new TotalCommand(this.bot));
		this.commands.put("close", new CloseCommand(this.bot));
		this.commands.put("done", new DoneCommand(this.bot));
		this.commands.put("quickdone", new QuickDoneCommand(this.bot));
		this.commands.put("comm", new CommissionCommand(this.bot));
		this.commands.put("paid", new PaidCommand(this.bot));
		this.commands.put("finished", new FinishedCommand(this.bot));
		this.commands.put("pic", new PicCommand(this.bot));
		this.commands.put("final", new FinalCommand(this.bot));
		this.commands.put("closecom", new CloseCommissionCommand(this.bot));
		this.commands.put("quote", new QuoteCommand(this.bot));
		this.commands.put("quotes", new QuotesCommand(this.bot));
		this.commands.put("adduser", new AddUserCommand(this.bot));
		this.commands.put("addbuilder", new AddBuilderCommand(this.bot));
		this.commands.put("removebuilder", new RemoveBuilderCommand(this.bot));
		this.commands.put("addmanager", new AddManagerCommand(this.bot));
		this.commands.put("removemanager", new RemoveManagerCommand(this.bot));
		this.commands.put("embed", new EmbedCommand(this.bot));
		this.commands.put("react", new ReactCommand(this.bot));
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
		} else if (event.getChannel().getName().startsWith("ticket_")) {

			this.bot.getTicketManager().handleMessage(event, Long.valueOf(event.getChannel().getName().split("_")[1]));

		}
	}
}

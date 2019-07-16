package net.gylliegyllie.odysseybot.discord.listeners;

import net.dv8tion.jda.core.entities.User;
import net.dv8tion.jda.core.events.message.guild.react.GuildMessageReactionAddEvent;
import net.dv8tion.jda.core.hooks.ListenerAdapter;
import net.gylliegyllie.odysseybot.Bot;
import net.gylliegyllie.odysseybot.tickets.TicketManager;
import net.gylliegyllie.odysseybot.util.MessageUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.TimeUnit;

public class ReactionListener extends ListenerAdapter {

	private final static Logger logger = LoggerFactory.getLogger(ReactionListener.class);

	private final Bot bot;

	public ReactionListener(Bot bot) {
		this.bot = bot;
	}

	@Override
	public void onGuildMessageReactionAdd(GuildMessageReactionAddEvent event) {
		if (event.getUser().isBot()) {
			return;
		}

		Long channelID = event.getChannel().getIdLong();
		TicketManager ticketManager = this.bot.getTicketManager();

		// Are we in the channel for requesting a ticket?
		if (this.bot.getConfiguration().getTicketRequestChannel().equals(channelID)) {

			// Is it the commission reaction?
			if (event.getReaction().getReactionEmote().getName().equals("commissions")) {

				event.getReaction().removeReaction(event.getUser()).queueAfter(500, TimeUnit.MILLISECONDS);

				if (ticketManager.hasRecentTickets(event.getUser())) {
					MessageUtil.sendDM(event.getUser(), ":x: You can only create a ticket every 10 minutes!");
					logger.info(String.format("User %s tried to create a ticket, but recently opened one!", event.getUser().getName()));
					return;
				}

				if (ticketManager.hasMaxOpen(event.getUser())) {
					MessageUtil.sendDM(event.getUser(), ":x: You cannot have more than 3 open tickets!");
					logger.info(String.format("User %s tried to create a ticket, but reached max open tickets!", event.getUser().getName()));
					return;
				}

				ticketManager.createTicket(event.getMember());
			}
		} else if (event.getChannel().getName().startsWith("ticket_")) {

			boolean foundOur = false;

			for (User user : event.getReaction().getUsers()) {
				if (user.isBot()) {
					if (this.bot.getBot().selfMember.getUser().getIdLong() == user.getIdLong()) {
						foundOur = true;
					}
				}
			}

			if (foundOur) {
				ticketManager.handleReaction(event, Long.valueOf(event.getChannel().getName().split("_")[1]));
			} else {
				event.getReaction().removeReaction(event.getUser()).queue();
			}

		} else if (event.getChannel().equals(this.bot.getBot().commissionChannel)) {

			boolean foundOur = false;

			for (User user : event.getReaction().getUsers()) {
				if (user.isBot()) {
					if (this.bot.getBot().selfMember.getUser().getIdLong() == user.getIdLong()) {
						foundOur = true;
					}
				}
			}

			if (foundOur) {
				ticketManager.handleReaction(event, event.getMessageIdLong());
			} else {
				event.getReaction().removeReaction(event.getUser()).queue();
			}

		}
	}
}

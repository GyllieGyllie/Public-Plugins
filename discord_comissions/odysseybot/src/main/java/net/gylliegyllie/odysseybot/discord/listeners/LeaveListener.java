package net.gylliegyllie.odysseybot.discord.listeners;

import net.dv8tion.jda.core.entities.Member;
import net.dv8tion.jda.core.entities.Role;
import net.dv8tion.jda.core.events.guild.member.GuildMemberLeaveEvent;
import net.dv8tion.jda.core.hooks.ListenerAdapter;
import net.gylliegyllie.odysseybot.Bot;
import net.gylliegyllie.odysseybot.tickets.entities.Ticket;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

public class LeaveListener extends ListenerAdapter {

	private final static Logger logger = LoggerFactory.getLogger(LeaveListener.class);

	private final Bot bot;

	public LeaveListener(Bot bot) {
		this.bot = bot;
	}

	@Override
	public void onGuildMemberLeave(GuildMemberLeaveEvent event) {

		Member member = event.getMember();
		List<Role> roles = member.getRoles();

		if (roles.contains(this.bot.getBot().managerRole) || roles.contains(this.bot.getBot().builderRole)
			|| roles.contains(this.bot.getBot().terraformerRole)) {

			List<Ticket> tickets = this.bot.getTicketManager().getUnfinishedTicketForStaff(member.getUser().getIdLong());
			Long staffID = event.getUser().getIdLong();

			for (Ticket ticket : tickets) {

				// Was Manager
				if (ticket.getClaimer().equals(staffID)) {
					ticket.setClaimer(-1L);
					this.bot.getSqlManager().updateTicketClaimer(ticket);
					this.bot.getSqlManager().insertUnfinishedManager(ticket, staffID);
				}

				// Was Builder
				if (ticket.getBuilders().contains(staffID)) {
					ticket.removeBuilder(staffID);
					this.bot.getSqlManager().deleteBuilder(ticket, staffID);
				}

				ticket.getChannel().sendMessage(":warning: Your Manager or Builder has left the commission. Please contact Charlie#5442 to get the issue resolved").queue();

				this.bot.getBot().importantChannel.sendMessage(this.bot.getBot().ownerRole.getAsMention() + " " + this.bot.getBot().generalManagerRole.getAsMention() + ", a manager or builder left in " + ticket.getChannel().getAsMention()).queue();
			}
		}

	}
}

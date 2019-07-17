package net.gylliegyllie.odysseybot.discord.commands;

import net.dv8tion.jda.core.Permission;
import net.dv8tion.jda.core.entities.Member;
import net.dv8tion.jda.core.events.message.MessageReceivedEvent;
import net.gylliegyllie.odysseybot.Bot;
import net.gylliegyllie.odysseybot.tickets.entities.Ticket;
import net.gylliegyllie.odysseybot.util.MessageUtil;

import java.util.List;

public class RemoveManagerCommand extends DiscordCommand {

	private final Bot bot;

	public RemoveManagerCommand(Bot bot) {
		super(false, bot.getBot().ownerRole, bot.getBot().generalManagerRole);

		this.bot = bot;
	}

	@Override
	public void runCommand(MessageReceivedEvent event, String command, String[] args) {
		if (!this.verifyInTicket(event)) return;

		List<Member> mentions = event.getMessage().getMentionedMembers();

		if (mentions.size() == 0) {
			MessageUtil.sendMessage(event, "Please mention the manager you want to add!", true);
			return;
		}

		if (mentions.size() > 1) {
			MessageUtil.sendMessage(event, "You can only add 1 manager!", true);
			return;
		}

		Long ticketID = Long.valueOf(event.getChannel().getName().split("_")[1]);
		Ticket ticket = this.bot.getTicketManager().getTicket(ticketID);

		Member member = mentions.get(0);
		Long id = member.getUser().getIdLong();

		if (!ticket.getClaimer().equals(id)) {
			MessageUtil.sendMessage(event, "This member isn't the manager!", true);
			return;
		}

		ticket.setClaimer(-1L);
		this.bot.getSqlManager().updateTicketClaimer(ticket);
		this.bot.getSqlManager().insertUnfinishedManager(ticket, id);

		ticket.getChannel().putPermissionOverride(member)
				.setAllow(Permission.EMPTY_PERMISSIONS)
				.setDeny(Permission.MESSAGE_READ)
				.queue();

		MessageUtil.sendMessage(event, "Manager removed!", true);

	}
}

package net.gylliegyllie.odysseybot.discord.commands;

import net.dv8tion.jda.core.Permission;
import net.dv8tion.jda.core.entities.Member;
import net.dv8tion.jda.core.events.message.MessageReceivedEvent;
import net.gylliegyllie.odysseybot.Bot;
import net.gylliegyllie.odysseybot.tickets.entities.Ticket;
import net.gylliegyllie.odysseybot.util.MessageUtil;

import java.util.List;

public class AddBuilderCommand extends DiscordCommand {

	private final Bot bot;

	public AddBuilderCommand(Bot bot) {
		super(false, bot.getBot().ownerRole, bot.getBot().generalManagerRole, bot.getBot().managerRole);

		this.bot = bot;
	}

	@Override
	public void runCommand(MessageReceivedEvent event, String command, String[] args) {
		if (!this.verifyInTicket(event)) return;

		List<Member> mentions = event.getMessage().getMentionedMembers();

		if (mentions.size() == 0) {
			MessageUtil.sendMessage(event, "Please mention the builder you want to add!", true);
			return;
		}

		Long ticketID = Long.valueOf(event.getChannel().getName().split("_")[1]);
		Ticket ticket = this.bot.getTicketManager().getTicket(ticketID);

		for (Member member : mentions) {
			Long id = member.getUser().getIdLong();

			if (!ticket.getBuilders().contains(id)) {

				ticket.addBuilder(id);
				this.bot.getSqlManager().addBuilder(ticket, id);

				ticket.getChannel().putPermissionOverride(member)
						.setAllow(Permission.MESSAGE_READ)
						.setDeny(Permission.EMPTY_PERMISSIONS)
						.queue();
			}
		}

		MessageUtil.sendMessage(event, "Builder(s) added!", true);

	}
}

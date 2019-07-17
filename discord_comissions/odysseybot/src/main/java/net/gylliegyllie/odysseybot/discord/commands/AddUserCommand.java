package net.gylliegyllie.odysseybot.discord.commands;

import net.dv8tion.jda.core.Permission;
import net.dv8tion.jda.core.entities.Member;
import net.dv8tion.jda.core.events.message.MessageReceivedEvent;
import net.gylliegyllie.odysseybot.Bot;
import net.gylliegyllie.odysseybot.tickets.entities.Ticket;
import net.gylliegyllie.odysseybot.util.MessageUtil;

import java.util.List;

public class AddUserCommand extends DiscordCommand {

	private final Bot bot;

	public AddUserCommand(Bot bot) {
		super(false, bot.getBot().ownerRole, bot.getBot().generalManagerRole, bot.getBot().managerRole);

		this.bot = bot;
	}

	@Override
	public void runCommand(MessageReceivedEvent event, String command, String[] args) {
		if (!this.verifyInTicket(event)) return;

		List<Member> mentions = event.getMessage().getMentionedMembers();

		if (mentions.size() == 0) {
			MessageUtil.sendMessage(event, "Please mention the user you want to add!", true);
			return;
		}

		Long ticketID = Long.valueOf(event.getChannel().getName().split("_")[1]);
		Ticket ticket = this.bot.getTicketManager().getTicket(ticketID);

		for (Member member : mentions) {
			ticket.getChannel().putPermissionOverride(member)
					.setAllow(Permission.MESSAGE_READ)
					.setDeny(Permission.EMPTY_PERMISSIONS)
					.queue();
		}

		MessageUtil.sendMessage(event, "User(s) added!", true);

	}
}

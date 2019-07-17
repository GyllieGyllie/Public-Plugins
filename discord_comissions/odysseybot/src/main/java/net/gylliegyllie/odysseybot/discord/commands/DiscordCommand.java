package net.gylliegyllie.odysseybot.discord.commands;

import net.dv8tion.jda.core.entities.ChannelType;
import net.dv8tion.jda.core.entities.Member;
import net.dv8tion.jda.core.entities.Role;
import net.dv8tion.jda.core.entities.TextChannel;
import net.dv8tion.jda.core.events.message.MessageReceivedEvent;
import net.gylliegyllie.odysseybot.util.MessageUtil;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Pattern;

public abstract class DiscordCommand {

	protected final Pattern IMGUR = Pattern.compile("[http://|https://]*imgur.com/[gallery|a]+/[a-zA-Z0-9]+");
	protected final Pattern MAIL = Pattern.compile("^[\\w-_\\.+]*[\\w-_\\.]\\@([\\w]+\\.)+[\\w]+[\\w]$");

	private final boolean privateCompatible;
	private final List<Role> roles = new ArrayList<>();

	public DiscordCommand(boolean privateCompatible, Role... roles) {
		this.privateCompatible = privateCompatible;
		this.roles.addAll(Arrays.asList(roles));
	}

	public abstract void runCommand(MessageReceivedEvent event, String command, String[] args);

	public boolean isPrivateCompatible() {
		return this.privateCompatible;
	}

	public boolean hasAccess(Member member) {

		if (this.roles.size() == 0) return true;

		List<Role> memberRoles = member.getRoles();

		for (Role role : this.roles) {
			if (memberRoles.contains(role)) {
				return true;
			}
		}

		return false;
	}

	boolean verifyInTicket(MessageReceivedEvent event) {
		if (event.getChannel().getType() != ChannelType.TEXT) {
			return false;
		}

		TextChannel channel = (TextChannel) event.getChannel();

		if (!channel.getName().startsWith("ticket_")) {
			return false;
		}

		return true;
	}

	Integer getPrice(MessageReceivedEvent event, String sValue) {

		if (sValue.startsWith("$")) {
			sValue = sValue.substring(1);
		}

		Integer price;

		try {
			price = Integer.valueOf(sValue);
		} catch (NumberFormatException e) {
			MessageUtil.sendMessage(event, "Invalid price amount entered!", true);
			return -1;
		}

		return price;
	}
}

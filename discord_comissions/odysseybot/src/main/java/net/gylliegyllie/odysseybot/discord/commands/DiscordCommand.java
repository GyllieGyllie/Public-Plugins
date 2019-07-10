package net.gylliegyllie.odysseybot.discord.commands;

import net.dv8tion.jda.core.entities.Member;
import net.dv8tion.jda.core.entities.Role;
import net.dv8tion.jda.core.events.message.MessageReceivedEvent;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public abstract class DiscordCommand {

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
}

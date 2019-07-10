package net.gylliegyllie.odysseybot.discord.commands;

import net.dv8tion.jda.core.events.message.MessageReceivedEvent;
import net.gylliegyllie.odysseybot.util.MessageUtil;

public class ApplyCommand extends DiscordCommand {

	public ApplyCommand() {
		super(true);
	}

	@Override
	public void runCommand(MessageReceivedEvent event, String command, String[] args) {
		MessageUtil.sendMessage(event, "You can apply to become a Builder here: https://bit.ly/OdysseyRecruitment", false);
	}

}

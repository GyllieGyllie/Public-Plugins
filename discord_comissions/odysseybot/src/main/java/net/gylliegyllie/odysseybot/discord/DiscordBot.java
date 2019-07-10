package net.gylliegyllie.odysseybot.discord;

import net.dv8tion.jda.core.JDA;
import net.dv8tion.jda.core.JDABuilder;
import net.gylliegyllie.odysseybot.configuration.Configuration;
import net.gylliegyllie.odysseybot.discord.listeners.MessageListener;

import javax.security.auth.login.LoginException;

public class DiscordBot {

	private final JDA jda;

	public DiscordBot(Configuration configuration) throws LoginException {
		this.jda = new JDABuilder(configuration.getBotKey())
				.addEventListener(new MessageListener())
				.build();
	}

	public void shutdown() {
		this.jda.shutdown();
	}
}

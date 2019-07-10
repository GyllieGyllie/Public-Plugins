package net.gylliegyllie.odysseybot;

import net.gylliegyllie.odysseybot.configuration.Configuration;
import net.gylliegyllie.odysseybot.discord.DiscordBot;
import net.gylliegyllie.odysseybot.managers.SQLManager;
import net.gylliegyllie.servicecore.Core;
import net.gylliegyllie.servicecore.commands.CommandManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.security.auth.login.LoginException;

public class Bot {

	private final static Logger logger = LoggerFactory.getLogger(Bot.class);

	public final static String PREFIX = "-";

	private static Bot instance;

	public static void main(String[] args) {
		 Bot.instance = new Bot();

		Core.sleepForever();
	}

	public static Bot getInstance() {
		return Bot.instance;
	}

	private final Configuration configuration;

	private SQLManager sqlManager;
	private DiscordBot bot;

	public Bot() {
		this.configuration = (Configuration) Core.loadConfiguration(new Configuration());

		if (this.configuration == null) {
			logger.error("Failed to load configuration file!");
			System.exit(-1);
		}

		if (this.configuration.getBotKey().isEmpty()) {
			logger.error("Please fill in the BotKey!");
			System.exit(-1);
		}

		try {
			this.sqlManager = new SQLManager();
		} catch (Exception e) {
			logger.error("", e);
			System.exit(-1);
		}

		try {
			this.bot = new DiscordBot(this.configuration);
		} catch (LoginException e) {
			logger.error("Failed to login", e);
			System.exit(-1);
		}

		Runtime.getRuntime().addShutdownHook(new Thread(() -> {

			logger.info("Starting shutdown!");

			this.bot.shutdown();

		}));

		CommandManager.listenToCommands();
	}

	public SQLManager getSqlManager() {
		return this.sqlManager;
	}
}

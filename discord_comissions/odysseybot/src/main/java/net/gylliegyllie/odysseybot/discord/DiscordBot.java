package net.gylliegyllie.odysseybot.discord;

import net.dv8tion.jda.core.JDA;
import net.dv8tion.jda.core.JDABuilder;
import net.dv8tion.jda.core.entities.Category;
import net.dv8tion.jda.core.entities.Emote;
import net.dv8tion.jda.core.entities.Game;
import net.dv8tion.jda.core.entities.Guild;
import net.dv8tion.jda.core.entities.Member;
import net.dv8tion.jda.core.entities.Role;
import net.dv8tion.jda.core.entities.TextChannel;
import net.gylliegyllie.odysseybot.Bot;
import net.gylliegyllie.odysseybot.configuration.Configuration;
import net.gylliegyllie.odysseybot.discord.commands.ApplyCommand;
import net.gylliegyllie.odysseybot.discord.commands.CloseCommand;
import net.gylliegyllie.odysseybot.discord.commands.DoneCommand;
import net.gylliegyllie.odysseybot.discord.commands.QuickDoneCommand;
import net.gylliegyllie.odysseybot.discord.commands.UpdateInfoCommand;
import net.gylliegyllie.odysseybot.discord.listeners.MessageListener;
import net.gylliegyllie.odysseybot.discord.listeners.ReactionListener;

import javax.security.auth.login.LoginException;

public class DiscordBot {

	private final JDA jda;

	public final Member selfMember;

	public final Role everyoneRole;
	public final Role ownerRole;
	public final Role generalManagerRole;
	public final Role managerRole;
	public final Role terraformerRole;
	public final Role builderRole;
	public final Role supportRole;

	public final TextChannel ticketRequestChannel;
	public final TextChannel commissionChannel;
	public final TextChannel reviewChannel;

	public final Category ticketsCategory;
	public final Category supportCategory;
	public final Category pricingCategory;

	public final Emote odysseyEmote;
	public final Emote exclusiveEmote;
	public final String houseEmote = "\uD83C\uDFE0";
	public final String toolsEmote = "\uD83D\uDEE0";
	public final String homesEmote = "\uD83C\uDFD8";
	public final String hammerEmote = "\uD83D\uDD28";
	public final String workerEmote = "\uD83D\uDC77\uD83C\uDFFB";
	public final String questionEmote = "❓";
	public final String terraformerEmote = "\uD83C\uDFD4";
	public final String acceptEmote = "✅";
	public final String denyEmote = "❌";

	private MessageListener messageListener;

	public DiscordBot(Bot bot, Configuration configuration) throws LoginException, InterruptedException {
		this.messageListener = new MessageListener(bot);

		this.jda = new JDABuilder(configuration.getBotKey())
				.addEventListener(this.messageListener)
				.addEventListener(new ReactionListener(bot))
				.build();

		this.jda.awaitReady();

		this.jda.getPresence().setGame(Game.listening("-help for more info"));

		Guild guild = this.jda.getGuildById(configuration.getGuildId());

		this.selfMember = guild.getSelfMember();

		this.everyoneRole = guild.getRoles().stream()
				.filter(Role::isPublicRole).findFirst().orElse(null);

		this.ownerRole = this.jda.getRoleById(configuration.getOwnerRole());
		this.generalManagerRole = this.jda.getRoleById(configuration.getGeneralManagerRole());
		this.managerRole = this.jda.getRoleById(configuration.getManagerRole());
		this.terraformerRole = this.jda.getRoleById(configuration.getTerraformerRole());
		this.builderRole = this.jda.getRoleById(configuration.getBuilderRole());
		this.supportRole = this.jda.getRoleById(configuration.getSupportRole());

		this.ticketRequestChannel = this.jda.getTextChannelById(configuration.getTicketRequestChannel());
		this.commissionChannel = this.jda.getTextChannelById(configuration.getCommissionChannel());
		this.reviewChannel = this.jda.getTextChannelById(configuration.getReviewsChannel());

		this.ticketsCategory = this.jda.getCategoryById(configuration.getTicketsCategory());
		this.supportCategory = this.jda.getCategoryById(configuration.getSupportCategory());
		this.pricingCategory = this.jda.getCategoryById(configuration.getPricingCategory());

		this.odysseyEmote = guild.getEmotes().stream().filter(e -> e.getName().equals("odyssey")).findFirst().orElse(null);
		this.exclusiveEmote = guild.getEmotes().stream().filter(e -> e.getName().equals("exclusive")).findFirst().orElse(null);
	}

	public void init() {
		this.messageListener.init();
	}

	public void shutdown() {
		this.jda.shutdown();
	}

	public JDA getJda() {
		return this.jda;
	}
}

package net.gylliegyllie.stellarsmp.discord;

import discord4j.core.DiscordClient;
import discord4j.core.DiscordClientBuilder;
import discord4j.core.event.domain.guild.GuildCreateEvent;
import discord4j.core.event.domain.lifecycle.ReadyEvent;
import discord4j.core.event.domain.message.MessageCreateEvent;
import discord4j.core.object.data.stored.ActivityBean;
import discord4j.core.object.data.stored.PresenceBean;
import discord4j.core.object.entity.Member;
import discord4j.core.object.entity.TextChannel;
import discord4j.core.object.presence.Activity;
import discord4j.core.object.presence.Presence;
import discord4j.core.object.util.Snowflake;
import net.gylliegyllie.stellarsmp.Main;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;

import java.util.Optional;

public class DiscordManager {

	private final Snowflake CHANNEL = Snowflake.of(590893528524259329L);

	private final Main plugin;

	private final DiscordClient discordClient;

	private boolean ready = false;

	public DiscordManager(Main plugin) {
		this.plugin = plugin;

		this.discordClient = new DiscordClientBuilder("NTkwODkwNjk2NzY1MDc5NTgx.XQoz7w.lgyAm6LM_SXOejdrcnMV5DsJk5A").build();

		this.discordClient.getEventDispatcher().on(ReadyEvent.class)
				.subscribe(e -> this.plugin.getLogger().info("Discord logged in as " + e.getSelf().getUsername()));

		this.discordClient.getEventDispatcher().on(ReadyEvent.class)
				.map(e -> e.getGuilds().size())
				.flatMap(amount -> this.discordClient.getEventDispatcher()
						.on(GuildCreateEvent.class)
						.take(amount)
						.collectList())
				.subscribe(e -> {
					this.plugin.getLogger().info("All discord guilds connected");

					this.ready = true;

					ActivityBean activityBean = new ActivityBean();
					activityBean.setType(Activity.Type.PLAYING.getValue());
					activityBean.setName("stellarsmp.com");

					PresenceBean presenceBean = new PresenceBean();
					presenceBean.setStatus("ONLINE");
					presenceBean.setActivity(activityBean);

					this.discordClient.updatePresence(new Presence(presenceBean)).block();
				});

		this.discordClient.getEventDispatcher().on(MessageCreateEvent.class).subscribe(e -> {

			Optional<String> content = e.getMessage().getContent();

			if (!content.isPresent()) {
				return;
			}

			if (e.getMessage().getChannelId().equals(this.CHANNEL)) {
				Optional<Member> author = e.getMember();

				if (author.isPresent() && !author.get().getId().equals(Snowflake.of(590890696765079581L))) {
					Bukkit.broadcastMessage(ChatColor.DARK_PURPLE + "[Discord] " + ChatColor.WHITE + author.get().getDisplayName() + ": " + content.get());
				}

			}
		});

		new Thread(this.discordClient.login()::block).start();
	}

	public void shutdown() {
		if (!this.ready) return;

		this.discordClient.getChannelById(this.CHANNEL).ofType(TextChannel.class).subscribe(channel ->
				channel.createMessage(":x: Server is shutting down")
						.subscribe((msg) -> this.discordClient.logout().block())
		);
	}

	public void sendOnline() {
		if (!this.ready) return;

		this.discordClient.getChannelById(this.CHANNEL).ofType(TextChannel.class).subscribe(channel ->
				channel.createMessage(":white_check_mark: Server is back online").block()
		);
	}

	public void sendJoin(String name) {
		if (!this.ready) return;

		this.discordClient.getChannelById(this.CHANNEL).ofType(TextChannel.class).subscribe(channel ->
			channel.createMessage(":bust_in_silhouette: **" + name + "** has joined the server. " + Bukkit.getOnlinePlayers().size() + " player(s) online.").block()
		);
	}

	public void sendLeave(String name) {
		if (!this.ready) return;

		this.discordClient.getChannelById(this.CHANNEL).ofType(TextChannel.class).subscribe(channel ->
				channel.createMessage(":bust_in_silhouette: **" + name + "** has left the server. " + Bukkit.getOnlinePlayers().size() + " player(s) online.").block()
		);
	}

	public void sendMessage(String name, String message) {
		if (!this.ready) return;

		this.discordClient.getChannelById(this.CHANNEL).ofType(TextChannel.class).subscribe(channel -> {
			String m = message.replace("@here", "`@here`").replace("@everyone", "`@everyone`");
			channel.createMessage(":speech_balloon: **" + name + "**: " + m).block();
		});
	}
}

package net.gylliegyllie.odysseybot.tickets;

import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.Permission;
import net.dv8tion.jda.core.entities.Guild;
import net.dv8tion.jda.core.entities.Member;
import net.dv8tion.jda.core.entities.Message;
import net.dv8tion.jda.core.entities.Role;
import net.dv8tion.jda.core.entities.TextChannel;
import net.dv8tion.jda.core.entities.User;
import net.dv8tion.jda.core.events.message.MessageReceivedEvent;
import net.dv8tion.jda.core.events.message.guild.react.GuildMessageReactionAddEvent;
import net.gylliegyllie.odysseybot.Bot;
import net.gylliegyllie.odysseybot.discord.DiscordBot;
import net.gylliegyllie.odysseybot.managers.SQLManager;
import net.gylliegyllie.odysseybot.tickets.entities.Ticket;
import net.gylliegyllie.odysseybot.tickets.entities.TicketState;
import net.gylliegyllie.odysseybot.tickets.entities.TicketType;
import net.gylliegyllie.odysseybot.util.MessageUtil;
import net.gylliegyllie.servicecore.Core;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.awt.Color;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.StringJoiner;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import static net.gylliegyllie.odysseybot.tickets.entities.TicketType.COMMISSION;
import static net.gylliegyllie.odysseybot.tickets.entities.TicketType.SUPPORT;

public class TicketManager {

	private final static Logger logger = LoggerFactory.getLogger(TicketManager.class);

	private final SQLManager sqlManager;
	private final DiscordBot bot;

	private List<Ticket> tickets = new ArrayList<>();

	private Thread idleThread;
	private boolean ending = false;

	public TicketManager(Bot bot) throws Exception {
		this.sqlManager = bot.getSqlManager();
		this.bot = bot.getBot();

		Connection connection = null;
		PreparedStatement statement = null;
		ResultSet resultSet = null;

		try {

			connection = this.sqlManager.getConnection();

			statement = connection.prepareStatement("SELECT * FROM tickets;");
			resultSet = statement.executeQuery();

			while (resultSet.next()) {
				Ticket ticket = new Ticket(resultSet);

				ticket.setChannel(this.bot.getJda().getTextChannelById(resultSet.getLong("channel_id")));
				ticket.setCommissionChannel(this.bot.getJda().getTextChannelById(resultSet.getLong("commission_channel_id")));

				this.tickets.add(ticket);
			}

			this.sqlManager.close(null, statement, resultSet);

			statement = connection.prepareStatement("SELECT * FROM builders WHERE unfinished = false;");

			resultSet = statement.executeQuery();

			while (resultSet.next()) {
				Long ticketID = resultSet.getLong("ticket_id");
				Ticket ticket = this.tickets.stream().filter(t -> t.getId().equals(ticketID)).findFirst().orElse(null);

				if (ticket != null) {
					ticket.addBuilder(resultSet.getLong("staff_id"));
				}
			}

			this.sqlManager.close(null, statement, resultSet);

			statement = connection.prepareStatement("SELECT * FROM quotes;");

			resultSet = statement.executeQuery();

			while (resultSet.next()) {
				Long ticketID = resultSet.getLong("ticket_id");
				Ticket ticket = this.tickets.stream().filter(t -> t.getId().equals(ticketID)).findFirst().orElse(null);

				if (ticket != null) {
					ticket.addQuote(resultSet.getLong("staff_id"), resultSet.getInt("price"));
				}
			}

			logger.info(String.format("Loaded %s tickets from the database!", this.tickets.size()));

		} finally {
			this.sqlManager.close(connection, statement, resultSet);
		}

		this.startIdleThread();
	}

	public void shutdown() {
		this.ending = true;

		if (this.idleThread != null) {
			this.idleThread.interrupt();
		}
	}

	public Ticket getTicket(Long id) {
		return this.tickets.stream().filter(t -> t.getId().equals(id)).findFirst().orElse(null);
	}

	public List<Ticket> getUnfinishedTicketForStaff(Long staffID) {
		return this.tickets.stream()
				// First get all tickets where they were first payed
				.filter(t -> t.getState().ordinal() > TicketState.AWAITING_FIRST_PAYMENT.ordinal())
				// Get all tickets not closed yet
				.filter(t -> t.getState().ordinal() < TicketState.RATE_MANAGER.ordinal())
				// Get tickets related to this staff
				.filter(t -> t.getClaimer().equals(staffID) || t.getBuilders().contains(staffID))
				.collect(Collectors.toList());
	}

	public void createTicket(Member member) {

		Long id;

		try {
			id = this.sqlManager.insertNewTicket(member.getUser().getIdLong());
		} catch (Exception e) {
			logger.error("", e);
			MessageUtil.sendDM(member.getUser(), ":x: Something went wrong creating a new ticket, try again later.");
			return;
		}

		Ticket ticket = new Ticket(id, System.currentTimeMillis(), member.getUser().getIdLong());
		this.tickets.add(ticket);

		this.bot.ticketsCategory.createTextChannel("ticket_" + id)
				.addPermissionOverride(this.bot.everyoneRole, Collections.emptyList(), Collections.singletonList(Permission.MESSAGE_READ))
				.addPermissionOverride(this.bot.ownerRole, Collections.singletonList(Permission.MESSAGE_READ), Collections.emptyList())
				.addPermissionOverride(this.bot.generalManagerRole, Collections.singletonList(Permission.MESSAGE_READ), Collections.emptyList())
				.addPermissionOverride(this.bot.selfMember, Collections.singletonList(Permission.MESSAGE_READ), Collections.emptyList())
				.addPermissionOverride(member, Collections.singletonList(Permission.MESSAGE_READ), Collections.emptyList())
				.queue(channel -> {
					TextChannel textChannel = (TextChannel) channel;

					ticket.setChannel(textChannel);
					this.sqlManager.setChannel(ticket);

					textChannel.sendMessage(new EmbedBuilder()
							.setColor(new Color(33,119,254))
							.setTitle("Odyssey Ticket System")
							.setDescription("Thank you for showing interest in purchasing a Commission from Odyssey Builds, or for helping us help you with our Online Support. You are now one step closer to having your vision created by our expert team of Builders.\n" +
									"\n" +
									"If you created a ticket by mistake, you can type `-close` in the ticket at any time to cancel the ticket.\n" +
									"\n" +
									"Click " + this.bot.odysseyEmote.getAsMention() + " to order a Commission.\n" +
									"Click " + this.bot.exclusiveEmote.getAsMention() + " for online Support.")
							.build())
							.queue(message -> {
								message.addReaction(this.bot.odysseyEmote).queue();
								message.addReaction(this.bot.exclusiveEmote).queue();
							});

					textChannel.sendMessage(member.getUser().getAsMention()).queue(msg -> msg.delete().queueAfter(1, TimeUnit.SECONDS));

					logger.info(String.format("User %s created a new ticket %s!", member.getUser().getName(), "ticket_" + ticket.getId()));
				});

	}

	public void closeTicket(Long id, User user) {
		Ticket ticket = this.tickets.stream().filter(t -> t.getId().equals(id)).findFirst().orElse(null);

		if (ticket != null && ticket.getOwner() == user.getIdLong()) {

			if (ticket.getType() == TicketType.COMMISSION && ticket.getState().ordinal() >= TicketState.IN_BUILD.ordinal()) {
				return;
			}

			TextChannel channel = ticket.getChannel();

			if (channel != null) {
				channel.sendMessage(
						String.format("Ticket closed! Channel will be removed in 10 minutes or react with %s to close now.", this.bot.odysseyEmote)
				).queue(message -> message.addReaction(this.bot.odysseyEmote).queue());

				ticket.delete();
			}

			ticket.setState(TicketState.CLOSED);

			this.sqlManager.updateTicketState(ticket);
		}
	}

	public void quickFinish(Long id) {
		Ticket ticket = this.tickets.stream().filter(t -> t.getId().equals(id)).findFirst().orElse(null);

		if (ticket != null && ticket.getType() == SUPPORT) {
			TextChannel channel = ticket.getChannel();

			if (channel != null) {
				ticket.forceDelete();
			}

			ticket.setState(TicketState.CLOSED);

			this.sqlManager.updateTicketState(ticket);
		}
	}

	public void finish(Long id) {
		Ticket ticket = this.tickets.stream().filter(t -> t.getId().equals(id)).findFirst().orElse(null);

		if (ticket != null && ticket.getType() == SUPPORT) {
			TextChannel channel = ticket.getChannel();

			if (channel != null) {

				channel.sendMessage(new EmbedBuilder()
						.setColor(new Color(33,119,254))
						.setTitle("Odyssey Ticket System")
						.setDescription("If you have any other issues do not hesitate to open another ticket. Thank you for choosing Odyssey.\n" +
								"\n" +
								"If you would like future updates of Odyssey, you can follow us on Twitter [here](https://twitter.com/odysseybuilds)!\n" +
								"\n" +
								"Ticket will be automatically closed in 10 minutes, or react to this message with " + this.bot.odysseyEmote.getAsMention() + " to automatically close it.")
						.build())
						.queue(message -> {
							message.addReaction(this.bot.odysseyEmote).queue();
						});

				ticket.delete();
			}

			ticket.setState(TicketState.CLOSED);

			this.sqlManager.updateTicketState(ticket);
		}
	}

	public void closeManager(Long id) {

		Ticket ticket = this.tickets.stream().filter(t -> t.getId().equals(id)).findFirst().orElse(null);

		if (ticket != null && ticket.getType() == COMMISSION && ticket.getState().ordinal() >= TicketState.OPEN.ordinal()) {
			TextChannel channel = ticket.getChannel();

			if (channel != null) {

				this.removeStaff(ticket);

				channel.sendMessage(new EmbedBuilder()
						.setColor(new Color(33,119,254))
						.setTitle("Odyssey Ticket System")
						.setDescription("Your Commission Manager has decided to close your Commission. If you believe this is an error, please open a support ticket in the <#567789671430488064> channel.\n" +
								"\n" +
								"If you would like future updates of Odyssey, you can follow us on Twitter [here](https://twitter.com/odysseybuilds)\n" +
								"\n" +
								"This ticket will be automatically closed in 10 minutes, or react to this message to automatically close it.\n")
						.build())
						.queue(message -> {
							message.addReaction(this.bot.odysseyEmote).queue();
						});

				ticket.delete();
			}

			if (ticket.getCommissionMessage() != null) {
				this.bot.commissionChannel.getMessageById(ticket.getCommissionMessage()).queue(msg -> {
					msg.delete().queue();
				});
			}

			if (ticket.getCommissionChannel() != null) {
				ticket.getCommissionChannel().delete().queue();
			}

			ticket.setState(TicketState.CLOSED);

			this.sqlManager.updateTicketState(ticket);
		}

	}

	public void startQuoting(Long id, String description, String deadline, String type) {
		Ticket ticket = this.tickets.stream().filter(t -> t.getId().equals(id)).findFirst().orElse(null);

		if (ticket != null && ticket.getState() == TicketState.OPEN) {

			ticket.setFinalDescription(description);
			ticket.setFinalDeadline(deadline);
			ticket.setState(TicketState.CONFIRMING);

			this.sqlManager.updateTicketFinalInfo(ticket);
			this.sqlManager.updateTicketState(ticket);

			if (!type.isEmpty()) {
				ticket.setCategory(type);
				this.sqlManager.updateTicketCategory(ticket);
			}

			User user = this.bot.getJda().getUserById(ticket.getOwner());

			ticket.getChannel().sendMessage(new EmbedBuilder()
					.setColor(new Color(33,119,254))
					.setTitle("Odyssey Ticket System")
					.setDescription("**" + user.getName() + "** please confirm you want to order your commission:")
					.addField("**Details**", ticket.getFinalDescription(), false)
					.addField("**Deadline**", ticket.getFinalDeadline(), false)
					.addField("**Confirm**", "React with " + this.bot.acceptEmote + " to confirm\n" +
							"React with " + this.bot.denyEmote + " to deny", false)
					.build())
					.queue(m -> {
						m.addReaction(this.bot.acceptEmote).queue();
						m.addReaction(this.bot.denyEmote).queue();
					});

			ticket.getChannel().sendMessage(user.getAsMention()).queue(m -> m.delete().queueAfter(1, TimeUnit.SECONDS));

		}
	}

	public void finishQuoting(Long id) {
		Ticket ticket = this.tickets.stream().filter(t -> t.getId().equals(id)).findFirst().orElse(null);

		if (ticket != null && ticket.getState() == TicketState.QUOTING) {

			ticket.setState(TicketState.QUOTING_SELECT);
			this.sqlManager.updateTicketState(ticket);

			Role role = ticket.getCategory().equalsIgnoreCase("builder") ? this.bot.builderRole : this.bot.terraformerRole;
			ticket.getCommissionChannel().putPermissionOverride(role)
					.setAllow(Permission.EMPTY_PERMISSIONS)
					.setDeny(Permission.MESSAGE_READ)
					.queue();

			TextChannel channel = ticket.getChannel();

			channel.sendMessage("**" + this.bot.getJda().getUserById(ticket.getOwner()).getAsMention() + ", we are happy to announce that your commission is ready to continue!**\n" +
					"We have found multiple quotes from our Builders for you to pick from.\n" +
					"Please click the " + this.bot.acceptEmote + " next to the builder you'd like")
					.queue(msg -> {

						ticket.setQuoteMessage(msg.getIdLong());
						this.sqlManager.updateTicketQuoteMessage(ticket);

						for (Map.Entry<Long, Integer> quote : ticket.getQuotes().entrySet()) {

							User user = this.bot.getJda().getUserById(quote.getKey());
							String portfolio = this.sqlManager.getPortfolio(quote.getKey());

							channel.sendMessage(user.getAsMention() + ": " + portfolio + "\n" +
									"Price: $" + quote.getValue()).queue(m -> m.addReaction(this.bot.acceptEmote).queue());

						}

						channel.sendMessage("If you would like to **keep waiting for a new builder**,\n" +
								"Please click the cross below.").queue(m -> m.addReaction(this.bot.denyEmote).queue());
					});
		}
	}

	public void setPrice(Long id, Integer price, String description, String deadline, String type) {
		Ticket ticket = this.tickets.stream().filter(t -> t.getId().equals(id)).findFirst().orElse(null);

		if (ticket != null && ticket.getState() == TicketState.OPEN) {
			ticket.setPrice(price);
			ticket.setFinalDescription(description);
			ticket.setFinalDeadline(deadline);
			ticket.setState(TicketState.CONFIRMING);

			this.sqlManager.updateTicketPrice(ticket);
			this.sqlManager.updateTicketFinalInfo(ticket);
			this.sqlManager.updateTicketState(ticket);

			if (!type.isEmpty()) {
				ticket.setCategory(type);
				this.sqlManager.updateTicketCategory(ticket);
			}

			User user = this.bot.getJda().getUserById(ticket.getOwner());

			ticket.getChannel().sendMessage(new EmbedBuilder()
					.setColor(new Color(33,119,254))
					.setTitle("Odyssey Ticket System")
					.setDescription("**" + user.getName() + "** please confirm you want to order your commission:")
					.addField("**Details**", description, false)
					.addField("**Deadline**", deadline, false)
					.addField("**Price**", "$" + price, false)
					.addField("**Confirm**", "React with " + this.bot.acceptEmote + " to confirm\n" +
							"React with " + this.bot.denyEmote + " to deny", false)
					.build())
					.queue(msg -> {
						msg.addReaction(this.bot.acceptEmote).queue();
						msg.addReaction(this.bot.denyEmote).queue();
					});

			ticket.getChannel().sendMessage(user.getAsMention()).queue(m -> m.delete().queueAfter(1, TimeUnit.SECONDS));
		}
	}

	public void setFirstHalfPaid(Long id) {
		Ticket ticket = this.tickets.stream().filter(t -> t.getId().equals(id)).findFirst().orElse(null);

		if (ticket != null && ticket.getState() == TicketState.AWAITING_FIRST_PAYMENT) {

			ticket.setState(TicketState.IN_BUILD);
			ticket.setStarted(true);

			this.sqlManager.updateTicketState(ticket);
			this.sqlManager.updateTicketStarted(ticket);

			User builder = this.bot.getJda().getUserById(new ArrayList<>(ticket.getBuilders()).get(0));

			ticket.getChannel().sendMessage(new EmbedBuilder()
					.setColor(new Color(33,119,254))
					.setTitle("Odyssey Ticket System")
					.setDescription("50% of the money has been paid. " + builder.getAsMention() + " You may now begin!")
					.build())
					.queue();

			ticket.getChannel().sendMessage(builder.getAsMention()).queue(msg -> msg.delete().queueAfter(1, TimeUnit.SECONDS));
		}
	}

	public void setFinished(Long id) {
		Ticket ticket = this.tickets.stream().filter(t -> t.getId().equals(id)).findFirst().orElse(null);

		if (ticket != null && ticket.getState() == TicketState.IN_BUILD) {

			ticket.setState(TicketState.FINISHED);
			this.sqlManager.updateTicketState(ticket);

			User owner = this.bot.getJda().getUserById(ticket.getOwner());

			ticket.getChannel().sendMessage(new EmbedBuilder()
					.setColor(new Color(33,119,254))
					.setTitle("Odyssey Ticket System")
					.setDescription("**" + owner.getAsMention() + ", your Commission is finished!**\n" +
							"\n" +
							"Click " + this.bot.acceptEmote + " to confirm finished\n" +
							"Click " + this.bot.denyEmote + " to mark as not finished")
					.build())
					.queue(msg -> {
						msg.addReaction(this.bot.acceptEmote).queue();
						msg.addReaction(this.bot.denyEmote).queue();
					});

			ticket.getChannel().sendMessage(owner.getAsMention()).queue(msg -> msg.delete().queueAfter(1, TimeUnit.SECONDS));
		}
	}

	public void setCompleted(Long id) {
		Ticket ticket = this.tickets.stream().filter(t -> t.getId().equals(id)).findFirst().orElse(null);

		if (ticket != null && ticket.getState() == TicketState.FINISHED_PAYMENT) {

			ticket.setState(TicketState.COMPLETED);
			ticket.setCompleted(true);

			this.sqlManager.updateTicketState(ticket);
			this.sqlManager.updateTicketCompleted(ticket);

			this.removeStaff(ticket);

			User owner = this.bot.getJda().getUserById(ticket.getOwner());

			ticket.getChannel().sendMessage(new EmbedBuilder()
					.setColor(new Color(33,119,254))
					.setTitle("Odyssey Ticket System")
					.setDescription("**Thank you again for choosing Odyssey Builds!**\n" +
							"\n" +
							"Leaving reviews helps us work on improving, and allows us to congratulate our staff on doing a good job if they have done so.\n" +
							"\n" +
							"Click " + this.bot.odysseyEmote.getAsMention() + " if you would willing to leave a review.\n" +
							"Click " + this.bot.exclusiveEmote.getAsMention() + " to close this ticket.")
					.build())
					.queue(msg -> {
						msg.addReaction(this.bot.odysseyEmote).queue();
						msg.addReaction(this.bot.exclusiveEmote).queue();
					});

			ticket.getChannel().sendMessage(owner.getAsMention()).queue(msg -> msg.delete().queueAfter(1, TimeUnit.SECONDS));
		}
	}

	public void setPicture(Long id, String link) {
		Ticket ticket = this.tickets.stream().filter(t -> t.getId().equals(id)).findFirst().orElse(null);

		if (ticket != null && ticket.getState().ordinal() >= TicketState.FINISHED.ordinal()) {

			ticket.setImage(link);
			this.sqlManager.updateTicketImage(ticket);

		}
	}

	public void handleReaction(GuildMessageReactionAddEvent event, Long id) {
		Ticket ticket = this.tickets.stream()
				.filter(t -> t.getId().equals(id) || (t.getState() == TicketState.AWAITING_BUILDER && t.getCommissionMessage().equals(id)))
				.findFirst().orElse(null);

		// No ticket found
		if (ticket == null) {
			event.getReaction().removeReaction(event.getUser()).queue();
			event.getChannel().sendMessage("Failed to handle reaction, please try again!").queue();
			return;
		}

		// Closed and force closing
		if (ticket.getState() == TicketState.CLOSED && event.getReactionEmote().getEmote().equals(this.bot.odysseyEmote)) {
			if (event.getUser().getIdLong() == ticket.getOwner()) {
				ticket.forceDelete();
			}
			return;
		}

		if (ticket.getState() == TicketState.AWAITING_FINAL_PAYMENT) {
			if (event.getUser().getIdLong() != ticket.getClaimer()) {
				event.getReaction().removeReaction(event.getUser()).queue();
				return;
			}
		} else if (event.getUser().getIdLong() != ticket.getOwner() && !ticket.getCommissionMessage().equals(id)) {
			event.getReaction().removeReaction(event.getUser()).queue();
			return;
		}

		switch (ticket.getState()) {

			case CREATED:

				if (event.getReactionEmote().getEmote().equals(this.bot.exclusiveEmote)) {

					this.clearMessages(ticket);
					ticket.getChannel().getManager().setParent(this.bot.supportCategory).queue();

					ticket.getChannel().sendMessage(new EmbedBuilder()
							.setColor(new Color(33,119,254))
							.setTitle("Odyssey Ticket System")
							.setDescription("Hello!\n" +
									"We’re online 24/7 to help out with support. Please rate the message below with the area of Odyssey you need support within.\n" +
									"\n" +
									this.bot.houseEmote + " An issue with a Pre-Made Build\n" +
									this.bot.toolsEmote + " An issue with an ongoing or past Commission\n" +
									this.bot.homesEmote + " A question regarding our Pre-Made Build Shop\n" +
									this.bot.hammerEmote + " A question regarding Commissions\n" +
									this.bot.workerEmote + " A question regarding Recruitment\n" +
									this.bot.questionEmote + " Other")
							.build())
							.queue(message -> {
								message.addReaction(this.bot.houseEmote).queue();
								message.addReaction(this.bot.toolsEmote).queue();
								message.addReaction(this.bot.homesEmote).queue();
								message.addReaction(this.bot.hammerEmote).queue();
								message.addReaction(this.bot.workerEmote).queue();
								message.addReaction(this.bot.questionEmote).queue();
							});

					ticket.setType(SUPPORT);
					ticket.setState(TicketState.SETUP);

					this.sqlManager.updateTicketState(ticket);
					this.sqlManager.updateTicketType(ticket);

				} else if (event.getReactionEmote().getEmote().equals(this.bot.odysseyEmote)) {

					this.clearMessages(ticket);

					ticket.getChannel().sendMessage(new EmbedBuilder()
							.setColor(new Color(33,119,254))
							.setTitle("Odyssey Ticket System")
							.setDescription("**Hello!**\n" +
									"We're excited to work with you on making your idea a reality. We have a few questions before the commission can begin to ensure we find the perfect builder for you. These questions should take no longer than 5 minutes to fill out.\n" +
									"\n" +
									"**To begin with, are you looking for a Terraformer or Builder?**\n" +
									"\n" +
									"Click " + this.bot.terraformerEmote + " for Terraformer\n" +
									"Click " + this.bot.workerEmote + " for Builder")
							.build())
							.queue(message -> {
								message.addReaction(this.bot.terraformerEmote).queue();
								message.addReaction(this.bot.workerEmote).queue();
							});

					ticket.setType(TicketType.COMMISSION);
					ticket.setState(TicketState.SETUP);

					this.sqlManager.updateTicketState(ticket);
					this.sqlManager.updateTicketType(ticket);

				} else {
					event.getChannel().sendMessage(":x: Please use the correct reactions!").queue();
					event.getReaction().removeReaction(event.getUser()).queue();
				}

				break;

			case SETUP:

				if (ticket.getType() == SUPPORT) {

					if (event.getReactionEmote().getName().equals(this.bot.houseEmote)) {
						ticket.setCategory("An issue with a Pre-Made Build");
					} else if (event.getReactionEmote().getName().equals(this.bot.toolsEmote)) {
						ticket.setCategory("An issue with an ongoing or past Commission");
					} else if (event.getReactionEmote().getName().equals(this.bot.homesEmote)) {
						ticket.setCategory("A question regarding our Pre-Made Build Shop");
					} else if (event.getReactionEmote().getName().equals(this.bot.hammerEmote)) {
						ticket.setCategory("A question regarding Commissions");
					} else if (event.getReactionEmote().getName().equals(this.bot.workerEmote)) {
						ticket.setCategory("A question regarding Recruitment");
					} else if (event.getReactionEmote().getName().equals(this.bot.questionEmote)) {
						ticket.setCategory("Other");
					} else {
						event.getChannel().sendMessage(":x: Please use the correct reactions!").queue();
						event.getReaction().removeReaction(event.getUser()).queue();
						return;
					}

					this.clearMessages(ticket);

					ticket.getChannel().sendMessage(new EmbedBuilder()
							.setColor(new Color(33,119,254))
							.setTitle("Odyssey Ticket System")
							.setDescription("Please leave a short description of what you need help with below, and a Manager will be with you shortly.")
							.build())
							.queue();

					this.sqlManager.updateTicketCategory(ticket);

				} else {

					if (ticket.getCategory().isEmpty()) {
						if (event.getReactionEmote().getName().equals(this.bot.terraformerEmote)) {
							ticket.setCategory("Terraformer");
						} else if (event.getReactionEmote().getName().equals(this.bot.workerEmote)) {
							ticket.setCategory("Builder");
						} else {
							event.getChannel().sendMessage(":x: Please use the correct reactions!").queue();
							event.getReaction().removeReaction(event.getUser()).queue();
							return;
						}

						this.clearMessages(ticket);

						ticket.getChannel().sendMessage(new EmbedBuilder()
								.setColor(new Color(33, 119, 254))
								.setTitle("Odyssey Ticket System")
								.setDescription("**Please give us a description of what type of build you are looking for.** (i.e., *a Candy themed Skyblock Spawn, floating on an island*)")
								.build())
								.queue();

						this.sqlManager.updateTicketCategory(ticket);

					} else if (!ticket.getExtra().isEmpty()) {

						if (event.getReactionEmote().getName().equals(this.bot.acceptEmote)) {

							event.getChannel().getMessageById(event.getMessageIdLong()).queue(msg -> msg.clearReactions().queue());

							ticket.getChannel().sendMessage(this.bot.acceptEmote + " User has agreed to the Terms of Services!").queue();

							ticket.getChannel().sendMessage(new EmbedBuilder()
									.setColor(new Color(33, 119, 254))
									.setTitle("Odyssey Ticket System")
									.setDescription("**Thank you for helping us get an idea of what you're looking for!**\n" +
											"All of this information is being sent off to our Managers, so we can find the right manager for the job. Once a Manager has been found we can get you a quote for the build, and potentially continue from there. If you are not happy with the quote, you may back out of the commission.\n")
									.addField("Type", ticket.getCategory(), false)
									.addField("Description", ticket.getDescription(), false)
									.addField("Deadline", ticket.getDeadline(), false)
									.addField("Extra's", ticket.getExtra(), false)
									.build())
									.queue();

							ticket.getChannel().putPermissionOverride(this.bot.managerRole).setAllow(Permission.MESSAGE_READ).queue();
							ticket.getChannel().sendMessage(this.bot.managerRole.getAsMention()).queue(m -> m.delete().queueAfter(1, TimeUnit.SECONDS));

							ticket.setState(TicketState.OPEN);

							this.sqlManager.updateTicketState(ticket);

						} else if (event.getReactionEmote().getName().equals(this.bot.denyEmote)) {

							this.clearMessages(ticket);

							ticket.getChannel().sendMessage(new EmbedBuilder()
									.setColor(new Color(33, 119, 254))
									.setTitle("Odyssey Ticket System")
									.setDescription("**We're sorry, **\n" +
											"However we could not create your ticket for you as you did not agree to our Terms of Service. All clients must agree to our Terms of Service to ensure that we, and them, are protected under the transaction. Feel free to make another ticket after 10 minutes of your first one.\n" +
											"\n" +
											"Ticket will be automatically closed in 10 minutes, or react to this message with " + this.bot.odysseyEmote.getAsMention() + " to automatically close it.")
									.build())
									.queue(message -> {
										message.addReaction(this.bot.odysseyEmote).queue();
									});

							ticket.setState(TicketState.CLOSED);
							ticket.delete();

							this.sqlManager.updateTicketState(ticket);

						} else {
							event.getChannel().sendMessage(":x: Please use the correct reactions!").queue();
							event.getReaction().removeReaction(event.getUser()).queue();
							return;
						}
					}

				}

				break;

			case QUOTING_SELECT:

				if (event.getReactionEmote().getName().equals(this.bot.acceptEmote)) {

					ticket.getChannel().getMessageById(event.getMessageIdLong()).queue(msg -> {

						String arg = msg.getContentRaw().split(" ")[0];
						Long builder = Long.valueOf(arg.substring(2, arg.length() - 2));

						Integer price = ticket.getQuotes().get(builder);

						ticket.setPrice(price);
						ticket.addBuilder(builder);
						ticket.setState(TicketState.AWAITING_FIRST_PAYMENT);

						this.sqlManager.updateTicketPrice(ticket);
						this.sqlManager.addBuilder(ticket, builder);
						this.sqlManager.updateTicketState(ticket);

						if (ticket.getCommissionChannel() != null) {
							ticket.getCommissionChannel().delete().queue();
							ticket.setCommissionChannel(null);
						}

						String paypal = this.sqlManager.getPaypal(ticket.getClaimer());

						ticket.getChannel().sendMessage(new EmbedBuilder()
								.setColor(new Color(33,119,254))
								.setTitle("Odyssey Ticket System")
								.setDescription("Before the Commission can start, we will need to take 50% of the payment ($" + (((double) ticket.getPrice()) / 2) + ")." +
										" Please send the money to " + paypal + " via the Friends & Family option.\n" +
										"\n" +
										"Once this has been done, the Manager can confirm so and the commission can begin.\n")
								.build())
								.queue();

						Guild guild = ticket.getChannel().getGuild();

						ticket.getChannel().putPermissionOverride(guild.getMemberById(ticket.getOwner()))
								.setAllow(Permission.MESSAGE_READ, Permission.MESSAGE_WRITE)
								.setDeny(Permission.EMPTY_PERMISSIONS)
								.queue();
						ticket.getChannel().putPermissionOverride(guild.getMemberById(ticket.getClaimer()))
								.setAllow(Permission.MESSAGE_READ, Permission.MESSAGE_WRITE)
								.setDeny(Permission.EMPTY_PERMISSIONS)
								.queue();

						for (Long b : ticket.getBuilders()) {
							ticket.getChannel().putPermissionOverride(guild.getMemberById(b))
									.setAllow(Permission.MESSAGE_READ, Permission.MESSAGE_WRITE)
									.setDeny(Permission.EMPTY_PERMISSIONS)
									.queue();
						}

					});

					ticket.getChannel().getMessageById(ticket.getQuoteMessage()).queue(m -> {
						ticket.getChannel().getHistoryAfter(m, ticket.getQuotes().size() + 1).queue(messageHistory -> {
							ticket.getChannel().deleteMessages(messageHistory.getRetrievedHistory()).queue();
							m.delete().queue();
						});
					});

				} else if (event.getReactionEmote().getName().equals(this.bot.denyEmote)) {

					ticket.getChannel().getMessageById(ticket.getQuoteMessage()).queue(msg -> {
						ticket.getChannel().getHistoryAfter(msg, 100).queue(messageHistory -> {
							ticket.getChannel().deleteMessages(messageHistory.getRetrievedHistory()).queue();
							msg.delete().queue();
						});
					});

					ticket.setState(TicketState.QUOTING);
					this.sqlManager.updateTicketState(ticket);

					Role role = ticket.getCategory().equalsIgnoreCase("builder") ? this.bot.builderRole : this.bot.terraformerRole;
					ticket.getCommissionChannel().putPermissionOverride(role)
							.setAllow(Permission.MESSAGE_READ, Permission.MESSAGE_WRITE)
							.setDeny(Permission.EMPTY_PERMISSIONS)
							.queue();

					ticket.getCommissionChannel().sendMessage(role.getAsMention() + ", the quotes for this project have reopened!").queue();

					ticket.getChannel().sendMessage(new EmbedBuilder()
							.setColor(new Color(33,119,254))
							.setTitle("Odyssey Ticket System")
							.setDescription("Your project has been send back to builders and we'll come back to you as soon as we have new quotes!")
							.build())
							.queue();
				} else {
					event.getChannel().sendMessage(":x: Please use the correct reactions!").queue();
					event.getReaction().removeReaction(event.getUser()).queue();
					return;
				}

				break;

			case CONFIRMING:

				if (event.getReactionEmote().getName().equals(this.bot.acceptEmote)) {

					event.getChannel().getMessageById(event.getMessageIdLong()).queue(msg -> msg.clearReactions().queue());

					if (ticket.getPrice() > 0) {
						ticket.setState(TicketState.AWAITING_BUILDER);
						this.sqlManager.updateTicketState(ticket);

						event.getChannel().sendMessage(new EmbedBuilder()
								.setColor(new Color(33, 119, 254))
								.setTitle("Odyssey Ticket System")
								.setDescription("Your final details have being sent off to our Builders. Once a Builder has confirmed that they can take the commission, you will be alerted.")
								.build())
								.queue();

						this.sendCommissionMessage(ticket);

					} else {

						ticket.setState(TicketState.QUOTING);
						this.sqlManager.updateTicketState(ticket);

						ticket.getChannel().sendMessage(new EmbedBuilder()
								.setColor(new Color(33,119,254))
								.setTitle("Odyssey Ticket System")
								.setDescription("Your project is now being quoted by our builders and we'll come back to you as soon as we have enough quotes!")
								.build())
								.queue();

						ticket.getChannel().putPermissionOverride(ticket.getChannel().getGuild().getMemberById(ticket.getOwner()))
								.setAllow(Permission.MESSAGE_READ)
								.setDeny(Permission.MESSAGE_WRITE)
								.queue();

						if (ticket.getCommissionChannel() == null) {
							this.createCommissionChannel(ticket.getChannel().getGuild(), ticket);
						}

						while (ticket.getCommissionChannel() == null) {
							Core.sleep(100);
						}

						ticket.getCommissionChannel().sendMessage(new EmbedBuilder()
								.setColor(new Color(33,119,254))
								.setTitle("Odyssey Ticket System")
								.setDescription("**New project available for quoting**")
								.addField("**Details**", ticket.getFinalDeadline(), false)
								.addField("**Deadline**", ticket.getFinalDeadline(), false)
								.addField("**Quote**", "Use " + Bot.PREFIX + "quote <price> to add your quote!", false)
								.build())
								.queue();

						Role role = ticket.getCategory().equalsIgnoreCase("builder") ? this.bot.builderRole : this.bot.terraformerRole;

						ticket.getCommissionChannel().sendMessage(role.getAsMention()).queue(msg -> msg.delete().queueAfter(1, TimeUnit.SECONDS));

					}

				} else if (event.getReactionEmote().getName().equals(this.bot.denyEmote)) {

					ticket.setState(TicketState.OPEN);
					this.sqlManager.updateTicketState(ticket);

					event.getChannel().sendMessage(":x: " + event.getUser().getName() + " has denied the offer!").queue();
					event.getChannel().getMessageById(event.getMessageIdLong()).queue(msg -> msg.clearReactions().queue());

					if (ticket.getPrice() == 0) {
						for (Long builder : ticket.getBuilders()) {
							ticket.removeBuilder(builder);
							this.sqlManager.deleteBuilder(ticket, builder);
						}

						ticket.getQuotes().clear();
						this.sqlManager.clearQuotes(ticket);

						if (ticket.getCommissionChannel() != null) {
							ticket.getCommissionChannel().delete().queue();
							ticket.setCommissionChannel(null);
						}
					}

				} else {
					event.getChannel().sendMessage(":x: Please use the correct reactions!").queue();
					event.getReaction().removeReaction(event.getUser()).queue();
				}

				break;

			case AWAITING_BUILDER:

				if (ticket.getCommissionMessage().equals(id) && event.getReactionEmote().getName().equals(this.bot.acceptEmote)) {
					Role role = ticket.getCategory().equalsIgnoreCase("builder") ? this.bot.builderRole : this.bot.terraformerRole;

					if (event.getMember().getRoles().contains(role)) {

						if (!this.sqlManager.isAllowedBuilder(ticket, event.getUser().getIdLong())) {
							event.getReaction().removeReaction(event.getUser()).queue();
							MessageUtil.sendDM(event.getUser(), ":x: You cannot claim this commission anymore!");
							return;
						}

						ticket.setState(TicketState.AWAITING_BUILDER_CONFIRM);
						ticket.addBuilder(event.getUser().getIdLong());

						this.sqlManager.updateTicketState(ticket);
						this.sqlManager.addBuilder(ticket, event.getUser().getIdLong());

						Guild guild = event.getGuild();
						Member owner = guild.getMemberById(ticket.getOwner());

						this.bot.commissionChannel.getMessageById(id).queue(msg -> msg.delete().queue());

						ticket.getChannel().putPermissionOverride(event.getMember())
								.setAllow(Permission.MESSAGE_READ)
								.setDeny(Permission.MESSAGE_WRITE)
								.queue();
						ticket.getChannel().putPermissionOverride(owner)
								.setAllow(Permission.MESSAGE_READ)
								.setDeny(Permission.MESSAGE_WRITE)
								.queue();
						ticket.getChannel().putPermissionOverride(guild.getMemberById(ticket.getClaimer()))
								.setAllow(Permission.MESSAGE_READ)
								.setDeny(Permission.MESSAGE_WRITE)
								.queue();

						String portfolio = this.sqlManager.getPortfolio(event.getUser().getIdLong());

						ticket.getChannel().sendMessage(new EmbedBuilder()
								.setColor(new Color(33,119,254))
								.setTitle("Odyssey Ticket System")
								.setDescription("**" + owner.getAsMention() + ", we're happy to announce that your commission is ready to continue!**\n" +
										event.getMember().getAsMention() + " has claimed your commission, and would love to work with you.\n" +
										"\n" +
										"Portfolio: [Here](" + portfolio + ")\n" +
										"\n" +
										"Click " + this.bot.acceptEmote + " to continue with this Builder\n" +
										"Click " + this.bot.denyEmote + " to find a new Builder\n" +
										"\n" +
										"*Please note, you may have to wait a while for a new builder*")
								.build())
								.queue(msg -> {
									msg.addReaction(this.bot.acceptEmote).queue();
									msg.addReaction(this.bot.denyEmote).queue();
								});

					} else {
						MessageUtil.sendDM(event.getUser(), ":x: This commission is only for " + ticket.getCategory() + "s!");
						event.getReaction().removeReaction(event.getUser()).queue();
					}
				}

				break;

			case AWAITING_BUILDER_CONFIRM:

				Guild guild = event.getGuild();

				if (event.getReactionEmote().getName().equals(this.bot.acceptEmote)) {

					ticket.getBuilders().forEach(builder -> {
						ticket.getChannel().putPermissionOverride(guild.getMemberById(builder))
								.setAllow(Permission.MESSAGE_READ, Permission.MESSAGE_WRITE)
								.setDeny(Permission.EMPTY_PERMISSIONS)
								.queue();
					});

					ticket.setState(TicketState.AWAITING_FIRST_PAYMENT);
					this.sqlManager.updateTicketState(ticket);

					event.getChannel().getMessageById(event.getMessageIdLong()).queue(msg -> msg.clearReactions().queue());

					String paypal = this.sqlManager.getPaypal(ticket.getClaimer());

					ticket.getChannel().sendMessage(new EmbedBuilder()
							.setColor(new Color(33,119,254))
							.setTitle("Odyssey Ticket System")
							.setDescription("Before the Commission can start, we will need to take 50% of the payment ($" + (((double) ticket.getPrice()) / 2) + ")." +
									" Please send the money to " + paypal + " via the Friends & Family option.\n" +
									"\n" +
									"Once this has been done, the Manager can confirm so and the commission can begin.\n")
							.build())
							.queue();

				} else if (event.getReactionEmote().getName().equals(this.bot.denyEmote)) {

					ticket.getBuilders().forEach(builder -> {
						ticket.removeBuilder(builder);
						this.sqlManager.deleteBuilder(ticket, builder);

						ticket.getChannel().putPermissionOverride(guild.getMemberById(builder))
								.setAllow(Permission.EMPTY_PERMISSIONS)
								.setDeny(Permission.MESSAGE_READ)
								.queue();
					});

					ticket.setState(TicketState.AWAITING_BUILDER);
					this.sqlManager.updateTicketState(ticket);

					event.getChannel().sendMessage(":x: " + event.getUser().getName() + " has denied the builder!").queue();
					event.getChannel().getMessageById(event.getMessageIdLong()).queue(msg -> msg.clearReactions().queue());

					this.sendCommissionMessage(ticket);

				} else {
					event.getChannel().sendMessage(":x: Please use the correct reactions!").queue();
					event.getReaction().removeReaction(event.getUser()).queue();
					return;
				}

				Member owner = guild.getMemberById(ticket.getOwner());

				ticket.getChannel().putPermissionOverride(owner)
						.setAllow(Permission.MESSAGE_READ, Permission.MESSAGE_WRITE)
						.setDeny(Permission.EMPTY_PERMISSIONS)
						.queue();
				ticket.getChannel().putPermissionOverride(guild.getMemberById(ticket.getClaimer()))
						.setAllow(Permission.MESSAGE_READ, Permission.MESSAGE_WRITE)
						.setDeny(Permission.EMPTY_PERMISSIONS)
						.queue();

				break;

			case FINISHED:

				if (event.getReactionEmote().getName().equals(this.bot.acceptEmote)) {

					ticket.setState(TicketState.AWAITING_FINAL_PAYMENT);
					this.sqlManager.updateTicketState(ticket);

					String paypal = this.sqlManager.getPaypal(ticket.getClaimer());
					User manager = this.bot.getJda().getUserById(ticket.getClaimer());

					ticket.getChannel().sendMessage(new EmbedBuilder()
							.setColor(new Color(33,119,254))
							.setTitle("Odyssey Ticket System")
							.setDescription("Please send the other 50% of the money ($" + (((double) ticket.getPrice()) / 2) + ") to " + paypal + " via the Friends & Family option.\n" +
									"\n" +
									manager.getAsMention() + " Please react to this message once the money has been sent.")
							.build())
							.queue(msg -> {
								msg.addReaction(this.bot.acceptEmote).queue();
								msg.addReaction(this.bot.denyEmote).queue();
							});

					ticket.getChannel().sendMessage(manager.getAsMention()).queue(msg -> msg.delete().queueAfter(1, TimeUnit.SECONDS));
					event.getChannel().getMessageById(event.getMessageIdLong()).queue(msg -> msg.clearReactions().queue());

				} else if (event.getReactionEmote().getName().equals(this.bot.denyEmote)) {

					ticket.setState(TicketState.IN_BUILD);
					this.sqlManager.updateTicketState(ticket);

					event.getChannel().sendMessage(":x: " + event.getUser().getName() + " has marked the commission as unfinished!").queue();
					event.getChannel().getMessageById(event.getMessageIdLong()).queue(msg -> msg.clearReactions().queue());

				} else {
					event.getChannel().sendMessage(":x: Please use the correct reactions!").queue();
					event.getReaction().removeReaction(event.getUser()).queue();
					return;
				}

				break;

			case AWAITING_FINAL_PAYMENT:

				if (event.getReactionEmote().getName().equals(this.bot.acceptEmote)) {

					ticket.setState(TicketState.FINISHED_PAYMENT);
					this.sqlManager.updateTicketState(ticket);

					User builder = this.bot.getJda().getUserById(new ArrayList<>(ticket.getBuilders()).get(0));

					ticket.getChannel().sendMessage(new EmbedBuilder()
							.setColor(new Color(33,119,254))
							.setTitle("Odyssey Ticket System")
							.setDescription("Thank you for paying the full amount of money. " + builder.getAsMention() + " please send a .schematic of the build (or world file).\n" +
									"\n" +
									"Please do not leave the commission ticket, or cancel it, until the ticket closes itself.\n")
							.build())
							.queue();

					ticket.getChannel().sendMessage(builder.getAsMention()).queue(msg -> msg.delete().queueAfter(1, TimeUnit.SECONDS));

					event.getChannel().getMessageById(event.getMessageIdLong()).queue(msg -> msg.clearReactions().queue());

				} else if (event.getReactionEmote().getName().equals(this.bot.denyEmote)) {

					ticket.setState(TicketState.IN_BUILD);
					this.sqlManager.updateTicketState(ticket);

					event.getChannel().sendMessage(":x: " + event.getUser().getName() + " has marked the commission as unpaid!").queue();
					event.getChannel().getMessageById(event.getMessageIdLong()).queue(msg -> msg.clearReactions().queue());

				} else {
					event.getChannel().sendMessage(":x: Please use the correct reactions!").queue();
					event.getReaction().removeReaction(event.getUser()).queue();
					return;
				}

				break;

			case COMPLETED:

				if (event.getReactionEmote().getEmote().equals(this.bot.exclusiveEmote)) {

					ticket.setState(TicketState.CLOSED);
					this.sqlManager.updateTicketState(ticket);

					ticket.getChannel().sendMessage(new EmbedBuilder()
							.setColor(new Color(33,119,254))
							.setTitle("Odyssey Ticket System")
							.setDescription("If you would like future updates of Odyssey, you can follow us on Twitter [here](https://twitter.com/odysseybuilds)!\n" +
									"\n" +
									"This ticket will be automatically closed in 10 minutes, or react to this message to automatically close it.\n")
							.build())
							.queue(msg -> {
								msg.addReaction(this.bot.odysseyEmote).queue();
							});

					ticket.delete();

				} else if (event.getReactionEmote().getEmote().equals(this.bot.odysseyEmote)) {

					ticket.setState(TicketState.RATE_MANAGER);
					this.sqlManager.updateTicketState(ticket);

					ticket.getChannel().sendMessage(new EmbedBuilder()
							.setColor(new Color(33,119,254))
							.setTitle("Odyssey Ticket System")
							.setDescription("How would you rate your manager’s service on a scale of 1-5?\n" +
									"\n" +
									"*Please react with your answer.*")
							.build())
							.queue(this::addNumberEmotes);

				}

				break;

			case RATE_MANAGER:

				int rating = this.getRating(event);

				if (rating == -1) {
					event.getChannel().sendMessage(":x: Please use the correct reactions!").queue();
					event.getReaction().removeReaction(event.getUser()).queue();
					return;
				}

				ticket.setState(TicketState.RATE_BUILD);
				ticket.setManagerRating(rating);

				this.sqlManager.updateTicketState(ticket);
				this.sqlManager.updateManagerRating(ticket);

				event.getChannel().getMessageById(event.getMessageIdLong()).queue(msg -> msg.delete().queue());
				ticket.getChannel().sendMessage(new EmbedBuilder()
						.setColor(new Color(33,119,254))
						.setTitle("Odyssey Ticket System")
						.setDescription("How would you rate your builder’s service on a scale of 1-5?\n" +
								"\n" +
								"*Please react with your answer.*")
						.build())
						.queue(this::addNumberEmotes);

				break;

			case RATE_BUILD:

				rating = this.getRating(event);

				if (rating == -1) {
					event.getChannel().sendMessage(":x: Please use the correct reactions!").queue();
					event.getReaction().removeReaction(event.getUser()).queue();
					return;
				}

				ticket.setState(TicketState.RATE_IMAGE);
				ticket.setBuilderRating(rating);

				this.sqlManager.updateTicketState(ticket);
				this.sqlManager.updateBuilderRating(ticket);

				event.getChannel().getMessageById(event.getMessageIdLong()).queue(msg -> msg.delete().queue());
				ticket.getChannel().sendMessage(new EmbedBuilder()
						.setColor(new Color(33,119,254))
						.setTitle("Odyssey Ticket System")
						.setDescription("Would you like your build to be kept private, or may we use images of it in your review?\n" +
								"\n" +
								"Click " + this.bot.acceptEmote + " if we can use images of it.\n" +
								"Click " + this.bot.denyEmote + " if we can not use images of it.")
						.build())
						.queue(msg -> {
							msg.addReaction(this.bot.acceptEmote).queue();
							msg.addReaction(this.bot.denyEmote).queue();
						});

				break;

			case RATE_IMAGE:

				if (event.getReactionEmote().getName().equals(this.bot.acceptEmote)) {

					ticket.setShowPics(true);

				} else if (event.getReactionEmote().getName().equals(this.bot.denyEmote)) {

					ticket.setShowPics(false);

				} else {
					event.getChannel().sendMessage(":x: Please use the correct reactions!").queue();
					event.getReaction().removeReaction(event.getUser()).queue();
					return;
				}

				ticket.setState(TicketState.CLOSED);
				this.sqlManager.updateTicketState(ticket);
				this.sqlManager.updateShowPics(ticket);

				event.getChannel().getMessageById(event.getMessageIdLong()).queue(msg -> msg.delete().queue());
				ticket.getChannel().sendMessage(new EmbedBuilder()
						.setColor(new Color(33,119,254))
						.setTitle("Odyssey Ticket System")
						.setDescription("Thank you very much for filling in our questionnaire!\n" +
								"\n" +
								"If you would like future updates of Odyssey, you can follow us on Twitter [here](https://twitter.com/odysseybuilds)!\n" +
								"\n" +
								"This ticket will be automatically closed in 10 minutes, or react to this message to automatically close it.\n")
						.build())
						.queue(msg -> {
							msg.addReaction(this.bot.odysseyEmote).queue();
						});

				ticket.delete();

				StringBuilder builder = new StringBuilder("**Review by ").append(this.bot.getJda().getUserById(ticket.getOwner()).getAsMention()).append("**")
						.append("\n\n")
						.append("**Manager Rating (").append(this.bot.getJda().getUserById(ticket.getClaimer()).getAsMention()).append("): **");

				for (int t = 1; t <= ticket.getManagerRating(); t++) {
					builder.append(":star:");
				}

				builder.append("\n\n")
					.append("**Builders Rating:** ");

				for (int t = 1; t <= ticket.getBuilderRating(); t++) {
					builder.append(":star:");
				}

				builder.append("\n");

				StringJoiner joiner = new StringJoiner(", ");

				for (Long b : ticket.getBuilders()) {
					User user = this.bot.getJda().getUserById(b);

					if (user != null) {
						joiner.add(user.getAsMention());
					}
				}

				builder.append(joiner.toString());

				if (ticket.isShowPics()) {
					builder.append("\n\n").append("**Images: **").append(ticket.getImage());
				}

				this.bot.reviewChannel.sendMessage(new EmbedBuilder()
						.setColor(new Color(33,119,254))
						.setTitle("Odyssey Ticket System")
						.setDescription(builder.toString())
						.build())
						.queue();

				break;

		}
	}

	public void handleMessage(MessageReceivedEvent event, Long id) {

		Ticket ticket = this.tickets.stream().filter(t -> t.getId().equals(id)).findFirst().orElse(null);

		// No ticket found
		if (ticket == null) {
			event.getChannel().sendMessage("Failed to handle message, please send it again!").queue();
			return;
		}

		// Ticket creator responding
		if (event.getAuthor().getIdLong() == ticket.getOwner()) {

			// Ticket setup was finished so we just talking
			if (ticket.getState() == TicketState.OPEN) {
				return;
			}

			if (ticket.getState() == TicketState.CREATED) {
				event.getMessage().delete().queue();
				event.getChannel().sendMessage(":x: Please react to the above message first!").queue();
			}

			// We still setting up
			if (ticket.getState() == TicketState.SETUP) {

				if (ticket.getType() == SUPPORT) {

					if (!ticket.getCategory().isEmpty()) {

						ticket.setDescription(event.getMessage().getContentRaw());
						this.clearMessages(ticket);

						ticket.getChannel().sendMessage(new EmbedBuilder()
								.setColor(new Color(33, 119, 254))
								.setTitle("Odyssey Ticket System")
								.setDescription("A member of our Support team will be with you shortly! Thank you for being patient.")
								.addField("Category", ticket.getCategory(), false)
								.addField("Description", ticket.getDescription(), false)
								.build())
								.queue();

						ticket.getChannel().putPermissionOverride(this.bot.supportRole).setAllow(Permission.MESSAGE_READ).queue();
						ticket.getChannel().sendMessage(this.bot.supportRole.getAsMention()).queue(m -> m.delete().queueAfter(1, TimeUnit.SECONDS));

						ticket.setState(TicketState.OPEN);

						this.sqlManager.updateTicketDescription(ticket);
						this.sqlManager.updateTicketState(ticket);

					} else {

						event.getMessage().delete().queue();
						event.getChannel().sendMessage(":x: Please react to the above message first!").queue();

					}

				} else {

					if (ticket.getDescription().isEmpty()) {

						ticket.setDescription(event.getMessage().getContentRaw());
						this.clearMessages(ticket);

						ticket.getChannel().sendMessage(new EmbedBuilder()
								.setColor(new Color(33, 119, 254))
								.setTitle("Odyssey Ticket System")
								.setDescription("**Do you have a deadline for this project?** Please include a specific date you need this done by, or if you do not have a deadline just put *None*\n" +
										"\n" +
										"If you do not have a deadline, we will just assume you need the build done as soon as possible!")
								.build())
								.queue();

						this.sqlManager.updateTicketDescription(ticket);

					} else if (ticket.getDeadline().isEmpty()) {

						ticket.setDeadline(event.getMessage().getContentRaw());
						this.clearMessages(ticket);

						ticket.getChannel().sendMessage(new EmbedBuilder()
								.setColor(new Color(33, 119, 254))
								.setTitle("Odyssey Ticket System")
								.setDescription("**Do you have any extra details?** We are asking this just to make sure your commission is perfect.\n" +
										"\n" +
										"*You may specify more details with the builder at a later date if needed*.")
								.build())
								.queue();

						this.sqlManager.updateTicketDeadline(ticket);

					} else if (ticket.getExtra().isEmpty()) {

						ticket.setExtra(event.getMessage().getContentRaw());
						this.clearMessages(ticket);

						ticket.getChannel().sendMessage(new EmbedBuilder()
								.setColor(new Color(33, 119, 254))
								.setTitle("Odyssey Ticket System")
								.setDescription("**Do you agree to our <#567791398888144906>?**\n" +
										"\n" +
										this.bot.acceptEmote + " Yes\n" +
										this.bot.denyEmote + " No")
								.build())
								.queue(message -> {
									message.addReaction(this.bot.acceptEmote).queue();
									message.addReaction(this.bot.denyEmote).queue();
								});

						this.sqlManager.updateTicketExtra(ticket);
					}
				}
			}

		} else if (ticket.getType() == TicketType.SUPPORT && ticket.getState() == TicketState.OPEN
				&& event.getMember().getRoles().contains(this.bot.supportRole)) {

			if (ticket.getClaimer() == -1L) {

				ticket.setClaimer(event.getAuthor().getIdLong());

				ticket.getChannel().sendMessage(new EmbedBuilder()
						.setColor(new Color(33, 119, 254))
						.setTitle("Odyssey Ticket System")
						.setDescription("Thank you for waiting " + event.getGuild().getMemberById(ticket.getOwner()).getUser().getAsMention() + "," +
								"\nYour ticket has been claimed by one of our Support Team Members (" + event.getAuthor().getAsMention() + "). We’re now ready to work with you to resolve any issues!")
						.build())
						.queue();

				ticket.getChannel().putPermissionOverride(this.bot.supportRole)
						.setDeny(Permission.EMPTY_PERMISSIONS)
						.setAllow(Permission.EMPTY_PERMISSIONS).queue();
				ticket.getChannel().putPermissionOverride(event.getMember())
						.setAllow(Permission.MESSAGE_READ).queue();

				this.sqlManager.updateTicketClaimer(ticket);

			}
		} else if (ticket.getType() == TicketType.COMMISSION && ticket.getState() == TicketState.OPEN
				&& event.getMember().getRoles().contains(this.bot.managerRole)) {

			if (ticket.getClaimer() == -1L) {

				ticket.setClaimer(event.getAuthor().getIdLong());

				ticket.getChannel().sendMessage(new EmbedBuilder()
						.setColor(new Color(33, 119, 254))
						.setTitle("Odyssey Ticket System")
						.setDescription("**" + event.getGuild().getMemberById(ticket.getOwner()).getUser().getAsMention() + " a Commission Manager has Claimed your Commission!**\n" +
								event.getAuthor().getAsMention() + " has claimed your commission, meaning they are now ready to work with you on finding a suitable price for the Commission. Once a price has been decided upon, a builder can be found.")
						.build())
						.queue();

				ticket.getChannel().putPermissionOverride(this.bot.managerRole)
						.setDeny(Permission.EMPTY_PERMISSIONS)
						.setAllow(Permission.EMPTY_PERMISSIONS).queue();
				ticket.getChannel().putPermissionOverride(event.getMember())
						.setAllow(Permission.MESSAGE_READ).queue();

				this.sqlManager.updateTicketClaimer(ticket);

			}
		}
	}

	public boolean hasRecentTickets(User user) {
		return this.tickets.stream()
				.filter(t -> t.getOwner().equals(user.getIdLong()))
				.anyMatch(t -> System.currentTimeMillis() - 1 * 60 * 1000 < t.getCreationTime()); //TODO set to 10
	}

	public boolean hasMaxOpen(User user) {
		return this.tickets.stream()
				.filter(t -> t.getOwner().equals(user.getIdLong()))
				.filter(Ticket::isOpen)
				.count() >= 3;
	}

	private void clearMessages(Ticket ticket) {
		ticket.getChannel().getHistory().retrievePast(10).queue(list -> {
			if (list.size() > 1) {
				ticket.getChannel().deleteMessages(list).queue();
			} else {
				list.get(0).delete().queue();
			}
		});
	}

	private void createCommissionChannel(Guild guild, Ticket ticket) {

		Role role = ticket.getCategory().equalsIgnoreCase("builder") ? this.bot.builderRole : this.bot.terraformerRole;
		Member manager = guild.getMemberById(ticket.getClaimer());

		this.bot.pricingCategory.createTextChannel("commission_" + ticket.getId())
				.addPermissionOverride(this.bot.everyoneRole, Collections.emptyList(), Collections.singletonList(Permission.MESSAGE_READ))
				.addPermissionOverride(this.bot.ownerRole, Collections.singletonList(Permission.MESSAGE_READ), Collections.emptyList())
				.addPermissionOverride(this.bot.generalManagerRole, Collections.singletonList(Permission.MESSAGE_READ), Collections.emptyList())
				.addPermissionOverride(this.bot.selfMember, Collections.singletonList(Permission.MESSAGE_READ), Collections.emptyList())
				.addPermissionOverride(role, Collections.singletonList(Permission.MESSAGE_READ), Collections.emptyList())
				.addPermissionOverride(manager, Collections.singletonList(Permission.MESSAGE_READ), Collections.emptyList())
				.queue(channel -> {
					TextChannel textChannel = (TextChannel) channel;

					ticket.setCommissionChannel(textChannel);
					this.sqlManager.setCommissionChannel(ticket);

				});
	}

	private void sendCommissionMessage(Ticket ticket) {
		this.bot.commissionChannel.sendMessage(new EmbedBuilder()
				.setColor(new Color(33,119,254))
				.setTitle("Odyssey Ticket System")
				.setDescription("New available commission:")
				.addField("**Type**", ticket.getCategory(), false)
				.addField("**Details**", ticket.getFinalDescription(), false)
				.addField("**Deadline**", ticket.getFinalDeadline(), false)
				.addField("**Price**", "$" + ticket.getPrice(), false)
				.addField("**Manager**", this.bot.getJda().getUserById(ticket.getClaimer()).getAsMention(), false)
				.addField("**Claim**", "React with " + this.bot.acceptEmote + " to claim this commission", false)
				.build())
				.queue(msg -> {
					msg.addReaction(this.bot.acceptEmote).queue();

					Role role = ticket.getCategory().equalsIgnoreCase("builder") ? this.bot.builderRole : this.bot.terraformerRole;
					msg.getChannel().sendMessage(role.getAsMention()).queue(m -> m.delete().queueAfter(1, TimeUnit.SECONDS));

					ticket.setCommissionMessage(msg.getIdLong());
					this.sqlManager.updateTicketCommissionMessage(ticket);

				});
	}

	private void removeStaff(Ticket ticket) {
		Guild guild = ticket.getChannel().getGuild();

		ticket.getBuilders().forEach(builder -> {
			ticket.getChannel().putPermissionOverride(guild.getMemberById(builder))
					.setAllow(Permission.EMPTY_PERMISSIONS)
					.setDeny(Permission.MESSAGE_READ)
					.queue();
		});

		ticket.getChannel().putPermissionOverride(guild.getMemberById(ticket.getClaimer()))
				.setAllow(Permission.EMPTY_PERMISSIONS)
				.setDeny(Permission.MESSAGE_READ)
				.queue();
	}

	private void addNumberEmotes(Message msg) {
		msg.addReaction(this.bot.oneEmote).queue();
		msg.addReaction(this.bot.twoEmote).queue();
		msg.addReaction(this.bot.threeEmote).queue();
		msg.addReaction(this.bot.fourEmote).queue();
		msg.addReaction(this.bot.fiveEmote).queue();
	}

	private Integer getRating(GuildMessageReactionAddEvent event) {
		if (event.getReactionEmote().getName().equals(this.bot.oneEmote)) {
			return 1;
		} else if (event.getReactionEmote().getName().equals(this.bot.twoEmote)) {
			return 2;
		} else if (event.getReactionEmote().getName().equals(this.bot.threeEmote)) {
			return 3;
		} else if (event.getReactionEmote().getName().equals(this.bot.fourEmote)) {
			return 4;
		} else if (event.getReactionEmote().getName().equals(this.bot.fiveEmote)) {
			return 5;
		}

		return -1;
	}

	private void startIdleThread() {

		this.idleThread = new Thread(() -> {

			while (!this.ending) {
				this.tickets.stream()
						.filter(t -> t.getState() == TicketState.CREATED || t.getState() == TicketState.SETUP)
						.forEach(t -> {

							try {

								long minutes = (System.currentTimeMillis() - t.getLastEdit()) / (60 * 1000);

								// Over delete threshold
								if (minutes >= 30) {

									MessageUtil.sendDM(this.bot.getJda().getUserById(t.getOwner()), ":wastebasket: Your ticket `ticket_" + t.getId() + "` has been deleted due to inactivity!");
									t.setState(TicketState.CLOSED);
									t.forceDelete();
									this.sqlManager.updateTicketState(t);

								} else if (minutes >= 20 && !t.hasSendAlert()) {

									t.getChannel().sendMessage(this.bot.getJda().getUserById(t.getOwner()).getAsMention() + ", this ticket will automatically close in 10 minutes due to inactivity.\n" +
											"\n" +
											"Please answer to the above question to continue.\n").queue();
									t.sendAlert();
								}

							} catch (Exception e) {
								logger.error("Error while checking inactivity!", e);
							}
						});

				Core.sleep(1000);
			}

		});

		this.idleThread.start();
	}
}

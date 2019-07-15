package net.gylliegyllie.odysseybot.tickets.entities;

import net.dv8tion.jda.core.Permission;
import net.dv8tion.jda.core.entities.TextChannel;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

public class Ticket {

	private final Long id;
	private final Long creationTime;
	private final Long owner;

	private TextChannel channel;

	private TicketState state = TicketState.CREATED;
	private TicketType type = TicketType.UNKNOWN;

	private String category = "";
	private String description = "";
	private String deadline = "";
	private String extra = "";
	private Long claimer = -1L;

	private ScheduledFuture<?> pendingDelete;

	public Ticket(Long id, Long creationTime, Long owner) {
		this.id = id;
		this.creationTime = creationTime;
		this.owner = owner;
	}

	public Ticket(ResultSet resultSet) throws SQLException {
		this.id = resultSet.getLong("id");
		this.creationTime = resultSet.getTimestamp("creation_time").getTime();
		this.owner = resultSet.getLong("owner");

		this.state = TicketState.valueOf(resultSet.getString("state"));
		this.type = TicketType.valueOf(resultSet.getString("type"));

		this.category = resultSet.getString("category");
		this.description = resultSet.getString("description");
		this.deadline = resultSet.getString("deadline");
		this.extra = resultSet.getString("extra");
		this.claimer = resultSet.getLong("claimer");
	}

	public Long getId() {
		return this.id;
	}

	public Long getCreationTime() {
		return this.creationTime;
	}

	public Long getOwner() {
		return this.owner;
	}

	public void setChannel(TextChannel channel) {
		this.channel = channel;
	}

	public TextChannel getChannel() {
		return this.channel;
	}

	public void setState(TicketState state) {
		this.state = state;
	}

	public TicketState getState() {
		return this.state;
	}

	public boolean isOpen() {
		return this.state !=  TicketState.CLOSED;
	}

	public void setType(TicketType type) {
		this.type = type;
	}

	public TicketType getType() {
		return this.type;
	}

	public void setCategory(String category) {
		this.category = category;
	}

	public String getCategory() {
		return this.category;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getDescription() {
		return this.description;
	}

	public void setDeadline(String deadline) {
		this.deadline = deadline;
	}

	public String getDeadline() {
		return this.deadline;
	}

	public void setExtra(String extra) {
		this.extra = extra;
	}

	public String getExtra() {
		return this.extra;
	}

	public void setClaimer(Long id) {
		this.claimer = id;
	}

	public Long getClaimer() {
		return this.claimer;
	}

	public void delete() {
		this.pendingDelete = this.channel.delete().queueAfter(10, TimeUnit.MINUTES);

		this.channel.putPermissionOverride(channel.getGuild().getMemberById(this.owner))
				.setDeny(Permission.MESSAGE_WRITE)
				.setAllow(Permission.MESSAGE_READ)
				.queue();
	}

	public void forceDelete() {
		this.channel.delete().queue();

		if (this.pendingDelete != null) {
			this.pendingDelete.cancel(true);
		}
	}
}

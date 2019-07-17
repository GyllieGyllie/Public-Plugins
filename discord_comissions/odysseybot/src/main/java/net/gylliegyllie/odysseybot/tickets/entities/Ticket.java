package net.gylliegyllie.odysseybot.tickets.entities;

import net.dv8tion.jda.core.Permission;
import net.dv8tion.jda.core.entities.TextChannel;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

public class Ticket {

	private final Long id;
	private final Long creationTime;
	private final Long owner;

	private TextChannel channel;
	private TextChannel commissionChannel;

	private Long quoteMessage = -1L;
	private Long commissionMessage = -1L;

	private TicketState state = TicketState.CREATED;
	private TicketType type = TicketType.UNKNOWN;

	private String category = "";
	private String description = "";
	private String deadline = "";
	private String extra = "";
	private Long claimer = -1L;

	private String finalDescription = "";
	private String finalDeadline = "";
	private Integer price = -1;
	private Boolean started = false;
	private Boolean completed = false;
	private String image = "";

	private Integer managerRating = -1;
	private Integer builderRating = -1;
	private Boolean showPics = false;

	private Map<Long, Integer> quotes = new HashMap<>();
	private Set<Long> builders = new HashSet<>();

	private Long lastEdit = System.currentTimeMillis();
	private boolean sendAlert = false;

	private ScheduledFuture<?> pendingDelete;

	public Ticket(Long id, Long creationTime, Long owner) {
		this.id = id;
		this.creationTime = creationTime;
		this.owner = owner;
	}

	public Ticket(ResultSet resultSet) throws SQLException {
		this.id = resultSet.getLong("id");
		this.creationTime = resultSet.getLong("creation_time");
		this.owner = resultSet.getLong("owner");

		this.quoteMessage = resultSet.getLong("quote_message");
		this.commissionMessage = resultSet.getLong("commission_message");

		this.state = TicketState.valueOf(resultSet.getString("state"));
		this.type = TicketType.valueOf(resultSet.getString("type"));

		this.category = resultSet.getString("category");
		this.description = resultSet.getString("description");
		this.deadline = resultSet.getString("deadline");
		this.extra = resultSet.getString("extra");
		this.claimer = resultSet.getLong("claimer");

		this.finalDescription = resultSet.getString("final_description");
		this.finalDeadline = resultSet.getString("final_deadline");
		this.price = resultSet.getInt("price");
		this.started = resultSet.getBoolean("started");
		this.completed = resultSet.getBoolean("completed");
		this.image = resultSet.getString("image");

		this.managerRating = resultSet.getInt("review_manager");
		this.builderRating = resultSet.getInt("review_builder");
		this.showPics = resultSet.getBoolean("review_show_image");
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

	public void setCommissionChannel(TextChannel channel) {
		this.commissionChannel = channel;
	}

	public TextChannel getCommissionChannel() {
		return this.commissionChannel;
	}

	public void setQuoteMessage(Long id) {
		this.quoteMessage = id;
	}

	public Long getQuoteMessage() {
		return this.quoteMessage;
	}

	public void setCommissionMessage(Long id) {
		this.commissionMessage = id;
	}

	public Long getCommissionMessage() {
		return this.commissionMessage;
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
		this.updateLastEdit();
	}

	public TicketType getType() {
		return this.type;
	}

	public void setCategory(String category) {
		this.category = category;
		this.updateLastEdit();
	}

	public String getCategory() {
		return this.category;
	}

	public void setDescription(String description) {
		this.description = description;
		this.updateLastEdit();
	}

	public String getDescription() {
		return this.description;
	}

	public void setDeadline(String deadline) {
		this.deadline = deadline;
		this.updateLastEdit();
	}

	public String getDeadline() {
		return this.deadline;
	}

	public void setExtra(String extra) {
		this.extra = extra;
		this.updateLastEdit();
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

	public void setFinalDescription(String description) {
		this.finalDescription = description;
	}

	public String getFinalDescription() {
		return this.finalDescription;
	}

	public void setFinalDeadline(String deadline) {
		this.finalDeadline = deadline;
	}

	public String getFinalDeadline() {
		return this.finalDeadline;
	}

	public void setPrice(Integer price) {
		this.price = price;
	}

	public Integer getPrice() {
		return this.price;
	}

	public void setStarted(Boolean started) {
		this.started = started;
	}

	public Boolean isStarted() {
		return this.started;
	}

	public void setCompleted(Boolean completed) {
		this.completed = completed;
	}

	public Boolean isCompleted() {
		return this.completed;
	}

	public void setImage(String image) {
		this.image = image;
	}

	public String getImage() {
		return this.image;
	}

	public void addQuote(Long builder, Integer quote) {
		this.quotes.put(builder, quote);
	}

	public Map<Long, Integer> getQuotes() {
		return this.quotes;
	}

	public void addBuilder(Long builder) {
		this.builders.add(builder);
	}

	public void removeBuilder(Long builder) {
		this.builders.remove(builder);
	}

	public Set<Long> getBuilders() {
		return new HashSet<>(this.builders);
	}

	public void setManagerRating(Integer rating) {
		this.managerRating = rating;
	}

	public Integer getManagerRating() {
		return this.managerRating;
	}

	public void setBuilderRating(Integer rating) {
		this.builderRating = rating;
	}

	public Integer getBuilderRating() {
		return this.builderRating;
	}

	public void setShowPics(Boolean show) {
		this.showPics = show;
	}

	public Boolean isShowPics() {
		return this.showPics;
	}

	public void updateLastEdit() {
		this.lastEdit = System.currentTimeMillis();
		this.sendAlert = false;
	}

	public Long getLastEdit() {
		return this.lastEdit;
	}

	public void sendAlert() {
		this.sendAlert = true;
	}

	public boolean hasSendAlert() {
		return this.sendAlert;
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

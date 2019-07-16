package net.gylliegyllie.odysseybot.configuration;

import net.gylliegyllie.servicecore.configuration.BaseConfiguration;

public class Configuration extends BaseConfiguration {

	private String botKey = "";

	private Long guildId = -1L;

	private Long supportRole = -1L;
	private Long builderRole = -1L;
	private Long terraformerRole = -1L;
	private Long managerRole = -1L;
	private Long generalManagerRole = -1L;
	private Long ownerRole = -1L;

	private Long ticketRequestChannel = -1L;
	private Long commissionChannel = -1L;
	private Long reviewsChannel = -1L;

	private Long ticketsCategory = -1L;
	private Long supportCategory = -1L;
	private Long pricingCategory = -1L;

	public String getBotKey() {
		return this.botKey;
	}

	public Long getGuildId() {
		return this.guildId;
	}

	public Long getSupportRole() {
		return this.supportRole;
	}

	public Long getBuilderRole() {
		return this.builderRole;
	}

	public Long getTerraformerRole() {
		return this.terraformerRole;
	}

	public Long getManagerRole() {
		return this.managerRole;
	}

	public Long getGeneralManagerRole() {
		return this.generalManagerRole;
	}

	public Long getOwnerRole() {
		return this.ownerRole;
	}

	public Long getTicketRequestChannel() {
		return this.ticketRequestChannel;
	}

	public Long getCommissionChannel() {
		return this.commissionChannel;
	}

	public Long getReviewsChannel() {
		return this.reviewsChannel;
	}

	public Long getTicketsCategory() {
		return this.ticketsCategory;
	}

	public Long getSupportCategory() {
		return this.supportCategory;
	}

	public Long getPricingCategory() {
		return this.pricingCategory;
	}
}

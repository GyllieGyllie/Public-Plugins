package net.gylliegyllie.odysseybot.configuration;

import net.gylliegyllie.servicecore.configuration.BaseConfiguration;

public class Configuration extends BaseConfiguration {

	private String botKey = "";
	private Long builderRole = -1L;
	private Long terraformerRole = -1L;
	private Long managerRole = -1L;
	private Long ownerRole = -1L;

	public String getBotKey() {
		return this.botKey;
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

	public Long getOwnerRole() {
		return this.ownerRole;
	}
}

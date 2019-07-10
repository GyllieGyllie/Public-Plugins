package net.gylliegyllie.rentingcraft.managers;

import net.gylliegyllie.rentingcraft.Plugin;
import net.milkbowl.vault.economy.Economy;
import org.bukkit.plugin.RegisteredServiceProvider;

public class EconomyManager {

	private final Plugin plugin;
	private final Economy economy;

	public EconomyManager(Plugin plugin) {
		this.plugin = plugin;

		if (this.plugin.getServer().getPluginManager().getPlugin("Vault") == null) {
			throw new RuntimeException("Missing Vault dependency!");
		}

		RegisteredServiceProvider<Economy> rsp = this.plugin.getServer().getServicesManager().getRegistration(Economy.class);

		if (rsp == null) {
			throw new RuntimeException("Failed to hook into Vault Economy! Do you have an economy plugin supporting Vault?");
		}

		this.economy = rsp.getProvider();
	}
}

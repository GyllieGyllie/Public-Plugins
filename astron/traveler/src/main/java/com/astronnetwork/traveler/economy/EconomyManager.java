package com.astronnetwork.traveler.economy;

import com.astronnetwork.traveler.Plugin;
import net.milkbowl.vault.economy.Economy;
import net.milkbowl.vault.economy.EconomyResponse;
import org.bukkit.entity.Player;
import org.bukkit.plugin.RegisteredServiceProvider;

public class EconomyManager {

	private final Plugin plugin;
	private final Economy economy;

	public EconomyManager(Plugin plugin) {
		this.plugin = plugin;

		if (this.plugin.getServer().getPluginManager().getPlugin("Vault") == null) {
			throw new RuntimeException("Missing Vault plugin!");
		}

		RegisteredServiceProvider<Economy> rsp = this.plugin.getServer().getServicesManager().getRegistration(Economy.class);
		if (rsp == null) {
			throw new RuntimeException("Failed to hook into Vault!");
		}

		this.economy = rsp.getProvider();
	}

	public boolean canAfford(Player player, int amount) {
		return this.economy.getBalance(player) >= amount;
	}

	public boolean pay(Player player, int amount) {
		EconomyResponse response = this.economy.withdrawPlayer(player, amount);

		return response.transactionSuccess();
	}
}

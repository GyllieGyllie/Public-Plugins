package com.astronnetwork.traveler.commands;

import com.astronnetwork.traveler.Plugin;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class TravelerCommand implements CommandExecutor {

	private final Plugin plugin;

	public TravelerCommand(Plugin plugin) {
		this.plugin = plugin;
	}

	@Override
	public boolean onCommand(CommandSender sender, Command command, String commandLabel, String[] args) {
		if (!(sender instanceof Player)) {
			sender.sendMessage("Player command only!");
			return true;
		}

		this.plugin.getItemManager().showShop((Player) sender);

		return true;
	}
}

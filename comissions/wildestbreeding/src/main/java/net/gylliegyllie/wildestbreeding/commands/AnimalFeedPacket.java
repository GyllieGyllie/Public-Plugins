package net.gylliegyllie.wildestbreeding.commands;

import net.gylliegyllie.wildestbreeding.Plugin;
import net.gylliegyllie.wildestbreeding.configuration.FeedConfig;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

public class AnimalFeedPacket implements CommandExecutor {

	private final Plugin plugin;

	public AnimalFeedPacket(Plugin plugin) {
		this.plugin = plugin;
	}

	@Override
	public boolean onCommand(CommandSender sender, Command command, String commandLabel, String[] args) {
		if (args.length != 3) {
			sender.sendMessage(ChatColor.BLUE + "Usage: /seedpacket <player> <mob> <quantity>");
			return true;
		}

		Player player = this.plugin.getServer().getPlayer(args[0]);

		if (player == null) {
			sender.sendMessage(ChatColor.RED + "Player not found!");
			return true;
		}

		EntityType mob;

		try {
			mob = EntityType.valueOf(args[1].toUpperCase());
		} catch (Exception ignore) {
			sender.sendMessage(ChatColor.RED + "Invalid mob entered!");
			return true;
		}

		if (!sender.hasPermission("animalfeed." + mob.name().toLowerCase())) {
			sender.sendMessage(ChatColor.RED + "No permissions!");
			return true;
		}

		FeedConfig config = this.plugin.getConfiguration().getForMob(mob);

		if (config == null) {
			sender.sendMessage(ChatColor.RED + "No config found for given mob?");
			return true;
		}

		int amount;

		try {
			amount = Integer.valueOf(args[2]);
		} catch (Exception ignore) {
			sender.sendMessage(ChatColor.RED + "Invalid quantity entered!");
			return true;
		}

		ItemStack itemStack = new ItemStack(config.packet, amount);
		ItemMeta itemMeta = itemStack.getItemMeta();
		itemMeta.setDisplayName(ChatColor.AQUA + config.packetName);
		itemStack.setItemMeta(itemMeta);

		this.plugin.addCustomFlag(itemStack, config.mob.name());

		player.getInventory().addItem(itemStack);

		sender.sendMessage(ChatColor.GREEN + "Successfully given packets!");
		player.sendMessage(ChatColor.GREEN + "You have received " + amount + " " + config.packetName);

		return true;
	}
}

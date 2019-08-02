package net.gylliegyllie.stellarsmp.commands;

import net.gylliegyllie.stellarsmp.Main;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class Record implements CommandExecutor {

	private final Main plugin;

	public Record(Main plugin) {
		this.plugin = plugin;
	}

	@Override
	public boolean onCommand(CommandSender sender, Command command, String commandLabel, String[] args) {
		if (!(sender instanceof Player)) {
			sender.sendMessage("Player command only!");
			return true;
		}

		this.plugin.getYoutubeThread().togglePlayer((Player) sender);
		return true;
	}
}

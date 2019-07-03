package net.gylliegyllie.rentingcraft.commands;

import net.gylliegyllie.gylliecore.gui.GuiScreen;
import net.gylliegyllie.rentingcraft.Plugin;
import net.gylliegyllie.rentingcraft.gui.AddOfferGUI;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class OfferCommand implements CommandExecutor {

	private final Plugin plugin;

	public OfferCommand(Plugin plugin) {
		this.plugin = plugin;
	}

	@Override
	public boolean onCommand(CommandSender sender, Command command, String commandLabel, String[] args) {
		if (!(sender instanceof Player)) {
			sender.sendMessage(this.plugin.getMessages().getMessage("player-command"));
			return true;
		}

		Player player = (Player) sender;
		ItemStack itemInHand = player.getInventory().getItemInMainHand();

		if (!this.plugin.getToolManager().isItemSupported(itemInHand.getType())) {
			this.plugin.getMessages().sendMessage(player, "commands.offer.not-supported");
			return true;
		}

		if (itemInHand.getAmount() > 1) {
			this.plugin.getMessages().sendMessage(player, "commands.offer.max1");
			return true;
		}

		new AddOfferGUI(this.plugin, player, itemInHand);

		return true;
	}
}

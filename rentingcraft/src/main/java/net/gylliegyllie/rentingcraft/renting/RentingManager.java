package net.gylliegyllie.rentingcraft.renting;

import net.gylliegyllie.rentingcraft.Plugin;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;

public class RentingManager {

	private final Plugin plugin;

	private final List<Rent> rents = new ArrayList<>();

	public RentingManager(Plugin plugin) {
		this.plugin = plugin;
	}

	public void createNewOffer(Player player, ItemStack itemStack, int price) {
		this.plugin.getServer().getScheduler().runTaskAsynchronously(this.plugin, () -> {
			Rent rent = new Rent(player.getUniqueId(), price, itemStack);

			if (this.plugin.getStorage().storeNewRent(rent)) {

				this.plugin.getMessages().sendMessage(player, "managers.renting.rent-added");

				this.rents.add(rent);
			} else {
				this.plugin.getMessages().sendMessage(player, "managers.renting.rent-add-fail");
			}
		});
	}
}

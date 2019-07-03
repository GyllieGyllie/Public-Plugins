package net.gylliegyllie.wildestfarming.listeners;

import net.gylliegyllie.wildestfarming.Plugin;
import net.gylliegyllie.wildestfarming.configuration.PlantConfig;
import net.gylliegyllie.wildestfarming.utils.ItemUtil;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.data.Ageable;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;

import java.util.List;

public class PlantingListener implements Listener {

	private final Plugin plugin;

	public PlantingListener(Plugin plugin) {
		this.plugin = plugin;
	}

	@EventHandler
	public void onBlockClick(PlayerInteractEvent event) {
		if (event.getAction() == Action.RIGHT_CLICK_BLOCK) {

			ItemStack itemInHand = event.getPlayer().getInventory().getItemInMainHand();

			// Don't allow seeds & plantable to be placed
			if (ItemUtil.SEEDS.contains(itemInHand.getType()) || ItemUtil.PLANTABLE.contains(itemInHand.getType())) {
				event.setCancelled(true);
			}

			// We trying to plant a packet
			else if (event.getBlockFace() == BlockFace.UP) {

				List<PlantConfig> configs = this.plugin.getConfiguration().getForPacket(itemInHand.getType());

				if (configs != null && configs.size() > 0 && itemInHand.hasItemMeta() && itemInHand.getItemMeta().hasDisplayName()) {
					Bukkit.broadcastMessage("possible configs");
					String name = ChatColor.stripColor(itemInHand.getItemMeta().getDisplayName());

					PlantConfig config = configs.stream().filter(c -> c.packetName.equals(name)).findFirst().orElse(null);

					if (config != null) {
						Bukkit.broadcastMessage("Found config");
						Block placing = event.getClickedBlock().getRelative(BlockFace.UP);

						switch (config.plant) {
							case WHEAT:
								placing.setType(Material.WHEAT);
								((Ageable) placing.getBlockData()).setAge(0);
								break;
						}
					}
				}
			}

		}
	}

	@EventHandler
	public void onBlockPlace(BlockPlaceEvent event) {

		ItemStack itemInHand = event.getItemInHand();

		// Don't allow seeds & plantable to be placed
		if (ItemUtil.SEEDS.contains(itemInHand.getType()) || ItemUtil.PLANTABLE.contains(itemInHand.getType())) {
			event.setCancelled(true);
		}
	}
}

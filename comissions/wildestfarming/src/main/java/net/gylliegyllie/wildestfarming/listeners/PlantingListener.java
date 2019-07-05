package net.gylliegyllie.wildestfarming.listeners;

import net.gylliegyllie.wildestfarming.Plugin;
import net.gylliegyllie.wildestfarming.configuration.PlantConfig;
import net.gylliegyllie.wildestfarming.utils.ItemUtil;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;

public class PlantingListener implements Listener {

	private final Plugin plugin;

	public PlantingListener(Plugin plugin) {
		this.plugin = plugin;
	}

	@EventHandler (priority = EventPriority.HIGHEST)
	public void onBlockClick(PlayerInteractEvent event) {
		if (event.getAction() == Action.RIGHT_CLICK_BLOCK && event.getClickedBlock() != null) {

			ItemStack itemInHand = event.getPlayer().getInventory().getItemInMainHand();

			// Don't allow seeds & plantable to be placed
			if (ItemUtil.SEEDS.contains(itemInHand.getType()) || ItemUtil.PLANTABLE.contains(itemInHand.getType())) {
				event.setCancelled(true);
			}

			// We trying to plant a packet
			else {

				String plant = this.plugin.getCustomFlag(itemInHand);

				if (plant != null) {

					PlantConfig config = this.plugin.getConfiguration().getForPlant(Material.valueOf(plant));

					// We have a possible packet
					if (config != null) {

						Block placing = event.getClickedBlock().getRelative(event.getBlockFace());

						// Verifying spot is empty, correct block & side clicked
						if (placing.getType() == Material.AIR && ItemUtil.isPlantable(config.plant, event.getClickedBlock())
								&& ItemUtil.isFaceSupported(config.plant, event.getBlockFace())) {

							itemInHand.setAmount(itemInHand.getAmount() - 1);
							event.getPlayer().getInventory().setItemInMainHand(itemInHand);

							ItemUtil.setBlock(config.plant, placing, event.getBlockFace().getOppositeFace());
						}

						event.setCancelled(true);
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

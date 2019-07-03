package net.gylliegyllie.wildestfarming.listeners;

import net.gylliegyllie.wildestfarming.Plugin;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.BlockState;
import org.bukkit.block.data.Directional;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockGrowEvent;

import java.util.Arrays;

public class PlantGrowListener implements Listener {

	private final Plugin plugin;

	public PlantGrowListener(Plugin plugin) {
		this.plugin = plugin;
	}

	@EventHandler
	public void onPlantGrow(BlockGrowEvent event) {
		if (event.getBlock().getType() == Material.AIR) {
			BlockState state = event.getNewState();

			if (state.getType() == Material.MELON) {
				this.findStem(event.getBlock(), Material.ATTACHED_MELON_STEM);
			} else if (state.getType() == Material.PUMPKIN) {
				this.findStem(event.getBlock(), Material.ATTACHED_PUMPKIN_STEM);
			}
		}
	}

	private void findStem(Block block, Material stemType) {

		Bukkit.getScheduler().runTaskLater(this.plugin, () -> {
			for (BlockFace blockFace : Arrays.asList(BlockFace.NORTH, BlockFace.EAST, BlockFace.SOUTH, BlockFace.WEST)) {
				Block relative = block.getRelative(blockFace);

				if (relative.getType() == stemType && relative.getState().getBlockData() instanceof Directional) {
					Directional directional = (Directional) relative.getState().getBlockData();

					if (relative.getRelative(directional.getFacing()).equals(block)) {
						relative.setType(Material.AIR);
					}
				}
			}
		}, 1L);

	}
}

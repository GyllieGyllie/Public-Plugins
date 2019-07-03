package net.gylliegyllie.wildestfarming.listeners;

import net.gylliegyllie.wildestfarming.utils.ItemUtil;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;

import java.util.Arrays;

public class VeinMineListener implements Listener {

	@EventHandler
	public void onBlockBreak(BlockBreakEvent event) {

		Block block = event.getBlock();
		Material material = block.getType();

		if (ItemUtil.VEIN_MINE.contains(material)) {
			block.breakNaturally();
			this.breakBlock(block, material);
		}
	}

	private void breakBlock(Block block, Material material) {
		for (BlockFace blockFace : Arrays.asList(BlockFace.DOWN, BlockFace.NORTH, BlockFace.EAST, BlockFace.SOUTH, BlockFace.WEST)) {
			Block relative = block.getRelative(blockFace);

			if (relative.getType() == material) {
				relative.breakNaturally();
				this.breakBlock(relative, material);
			}
		}
	}
}

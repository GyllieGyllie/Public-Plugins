package net.gylliegyllie.wildestfarming.listeners;

import net.gylliegyllie.wildestfarming.utils.ItemUtil;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.PistonMoveReaction;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockFromToEvent;
import org.bukkit.event.block.BlockPhysicsEvent;
import org.bukkit.event.block.BlockPistonExtendEvent;

import java.util.Arrays;

public class VeinMineListener implements Listener {

	@EventHandler (priority = EventPriority.HIGHEST, ignoreCancelled = true)
	public void onBlockBreak(BlockBreakEvent event) {

		Block block = event.getBlock();
		Material material = block.getType();

		if (ItemUtil.VEIN_MINE.contains(material)) {
			block.breakNaturally();
			this.breakBlock(block, material);
		}
	}

	@EventHandler (priority = EventPriority.HIGHEST, ignoreCancelled = true)
	public void onPistonExtend(BlockPistonExtendEvent event) {

		Block relative = event.getBlock().getRelative(event.getDirection());
		Material material = relative.getType();

		if (relative.getPistonMoveReaction() == PistonMoveReaction.BREAK && ItemUtil.VEIN_MINE.contains(material)) {
			relative.breakNaturally();
			this.breakBlock(relative, material);
		}
	}

	@EventHandler (priority = EventPriority.HIGHEST, ignoreCancelled = true)
	public void onWaterFlow(BlockFromToEvent event) {
		if (event.getBlock().getType() == Material.WATER) {

			Block relative = event.getToBlock();
			Material material = relative.getType();

			if (ItemUtil.VEIN_MINE.contains(material)) {
				relative.breakNaturally();
				this.breakBlock(relative, material);
			}

		}
	}

	@EventHandler (priority = EventPriority.HIGHEST, ignoreCancelled = true)
	public void onPhysics(BlockPhysicsEvent event) {
		if (ItemUtil.VEIN_MINE.contains(event.getChangedType())) {

			switch (event.getChangedType()) {
				case SUGAR_CANE:
					this.breakBlock(event.getBlock(), event.getChangedType());
					break;
			}
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

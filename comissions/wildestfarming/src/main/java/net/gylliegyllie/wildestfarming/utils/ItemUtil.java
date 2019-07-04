package net.gylliegyllie.wildestfarming.utils;

import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Ageable;

import java.util.Arrays;
import java.util.List;

public class ItemUtil {

	public final static List<Material> SEEDS = Arrays.asList(
			Material.BEETROOT_SEEDS,
			Material.MELON_SEEDS,
			Material.PUMPKIN_SEEDS,
			Material.WHEAT_SEEDS
	);

	public final static List<Material> PLANTABLE = Arrays.asList(
			Material.CARROT,
			Material.POTATO,
			Material.COCOA_BEANS,
			Material.NETHER_WART,
			Material.CHORUS_FLOWER,
			Material.SUGAR_CANE,
			Material.RED_MUSHROOM,
			Material.BROWN_MUSHROOM,
			Material.CACTUS
	);

	public final static List<Material> VEIN_MINE = Arrays.asList(
			Material.SUGAR_CANE
	);

	public static boolean isPlantable(Material material, Block block) {
		switch (material) {
			case BEETROOT:
			case CARROT:
			case MELON:
			case POTATO:
			case PUMPKIN:
			case WHEAT:
				return block.getType() == Material.FARMLAND;
			case COCOA_BEANS:
				return block.getType() == Material.JUNGLE_LOG;
			case NETHER_WART:
				return block.getType() == Material.SOUL_SAND;
			case CHORUS_FLOWER:
				return block.getType() == Material.END_STONE;
			case SUGAR_CANE:
				return block.getType() == Material.GRASS
						|| block.getType() == Material.DIRT
						|| block.getType() == Material.SAND;
			case BROWN_MUSHROOM:
			case RED_MUSHROOM:
				return block.getType() == Material.STONE;
			case CACTUS:
				return block.getType() == Material.SAND;
		}

		return false;
	}

	public static boolean isFaceSupported(Material material, BlockFace face) {
		switch (material) {
			case BEETROOT:
			case BROWN_MUSHROOM:
			case CACTUS:
			case CARROT:
			case CHORUS_FLOWER:
			case MELON:
			case NETHER_WART:
			case POTATO:
			case PUMPKIN:
			case RED_MUSHROOM:
			case SUGAR_CANE:
			case WHEAT:
				return face == BlockFace.UP;
			case COCOA_BEANS:
				return face == BlockFace.NORTH
						|| face == BlockFace.EAST
						|| face == BlockFace.SOUTH
						|| face == BlockFace.WEST;
		}

		return false;
	}

	public static void setBlock(Material material, Block block) {
		switch (material) {
			case BEETROOT:
				block.setType(Material.BEETROOT_SEEDS);
				break;
			case BROWN_MUSHROOM:
				block.setType(Material.BROWN_MUSHROOM);
				break;
			case CACTUS:
				block.setType(Material.CACTUS);
				break;
			case CARROT:
				block.setType(Material.CARROTS);
				break;
			case CHORUS_FLOWER:
				block.setType(Material.CHORUS_FLOWER);
				break;
			case COCOA_BEANS:
				block.setType(Material.COCOA);
				break;
			case MELON:
				block.setType(Material.MELON_STEM);
				break;
			case NETHER_WART:
				block.setType(Material.NETHER_WART);
				break;
			case POTATO:
				block.setType(Material.POTATOES);
				break;
			case PUMPKIN:
				block.setType(Material.PUMPKIN_STEM);
				break;
			case RED_MUSHROOM:
				block.setType(Material.RED_MUSHROOM);
				break;
			case SUGAR_CANE:
				block.setType(Material.SUGAR_CANE);
				break;
			case WHEAT:
				block.setType(Material.WHEAT);
				break;
		}

		setAge(block);
	}

	private static void setAge(Block block) {
		if (block.getBlockData() instanceof Ageable) {
			((Ageable) block.getBlockData()).setAge(0);
		}
	}
}

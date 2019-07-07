package net.gylliegyllie.wildestfarming.utils;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.data.Directional;
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
			Material.SUGAR_CANE,
			Material.CACTUS
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
				if (block.getType() == Material.GRASS_BLOCK
						|| block.getType() == Material.DIRT
						|| block.getType() == Material.SAND
						|| block.getType() == Material.PODZOL
						|| block.getType() == Material.COARSE_DIRT
						|| block.getType() == Material.RED_SAND) {
					for (BlockFace face : Arrays.asList(BlockFace.NORTH, BlockFace.EAST, BlockFace.SOUTH, BlockFace.WEST)) {
						Block relative = block.getRelative(face);

						if (relative.getType() == Material.WATER || relative.getType() == Material.FROSTED_ICE) {
							return true;
						}
					}
				}
			case BROWN_MUSHROOM:
			case RED_MUSHROOM:
				if (block.getType() == Material.MYCELIUM || block.getType() == Material.PODZOL) {
					return true;
				}

				if (block.getRelative(BlockFace.UP).getLightLevel() < 13) {

					Block b = block.getRelative(BlockFace.UP);

					while (b.getY() < 255) {
						if (b.getType() != Material.AIR) {
							return true;
						}

						b = b.getRelative(BlockFace.UP);
					}

				}

				return false;
			case CACTUS:
				if (block.getType() == Material.SAND) {
					Block placing = block.getRelative(BlockFace.UP);

					for (BlockFace face : Arrays.asList(BlockFace.NORTH, BlockFace.EAST, BlockFace.SOUTH, BlockFace.WEST)) {
						Block relative = placing.getRelative(face);

						if (relative.getType() != Material.AIR) {
							return false;
						}
					}

					return true;
				}
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

	public static void setBlock(Material material, Block block, BlockFace facing) {
		switch (material) {
			case BEETROOT:
				block.setType(Material.BEETROOTS);
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

				Directional directional = (Directional) block.getBlockData();
				directional.setFacing(facing);
				block.setBlockData(directional);

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

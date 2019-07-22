package net.gylliegyllie.wildestbreeding.utils;

import org.bukkit.Material;
import org.bukkit.entity.EntityType;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MobUtil {

	public final static List<EntityType> TAMABLE = Arrays.asList(
			EntityType.CHICKEN,
			EntityType.COW,
			EntityType.DONKEY,
			EntityType.HORSE,
			EntityType.LLAMA,
			EntityType.MUSHROOM_COW,
			EntityType.OCELOT,
			EntityType.PARROT,
			EntityType.PIG,
			EntityType.RABBIT,
			EntityType.SHEEP,
			EntityType.TURTLE,
			EntityType.WOLF
	);

	public final static Map<EntityType, List<Material>> BREED_MATERIAL = new HashMap<>();

	static {
		BREED_MATERIAL.put(EntityType.HORSE, Arrays.asList(Material.GOLDEN_APPLE, Material.GOLDEN_CARROT));
		BREED_MATERIAL.put(EntityType.DONKEY, Arrays.asList(Material.GOLDEN_APPLE, Material.GOLDEN_CARROT));
		BREED_MATERIAL.put(EntityType.SHEEP, Collections.singletonList(Material.WHEAT));
		BREED_MATERIAL.put(EntityType.COW, Collections.singletonList(Material.WHEAT));
		BREED_MATERIAL.put(EntityType.MUSHROOM_COW, Collections.singletonList(Material.WHEAT));
		BREED_MATERIAL.put(EntityType.PIG, Arrays.asList(Material.CARROT, Material.POTATO, Material.BEETROOT));
		BREED_MATERIAL.put(EntityType.CHICKEN, Arrays.asList(Material.WHEAT_SEEDS, Material.PUMPKIN_SEEDS, Material.MELON_SEEDS, Material.BEETROOT_SEEDS, Material.NETHER_WART));
		BREED_MATERIAL.put(EntityType.WOLF, Collections.singletonList(Material.BONE));
		BREED_MATERIAL.put(EntityType.OCELOT, Arrays.asList(Material.COD, Material.SALMON, Material.PUFFERFISH, Material.TROPICAL_FISH));
		BREED_MATERIAL.put(EntityType.RABBIT, Arrays.asList(Material.DANDELION, Material.CARROT, Material.GOLDEN_CARROT));
		BREED_MATERIAL.put(EntityType.LLAMA, Collections.singletonList(Material.HAY_BLOCK));
		BREED_MATERIAL.put(EntityType.TURTLE, Collections.singletonList(Material.SEAGRASS));
	}
}

package net.gylliegyllie.wildestfarming.listeners;

import net.gylliegyllie.wildestfarming.Plugin;
import net.gylliegyllie.wildestfarming.configuration.PlantConfig;
import net.gylliegyllie.wildestfarming.utils.ItemUtil;
import org.bukkit.ChatColor;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Item;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.entity.EntitySpawnEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.Random;

public class SeedDropListener implements Listener {

	private final Plugin plugin;
	private final Random random = new Random();

	public SeedDropListener(Plugin plugin) {
		this.plugin = plugin;
	}

	@EventHandler
	public void onEntityCreate(EntitySpawnEvent event) {
		if (event.getEntityType() == EntityType.DROPPED_ITEM) {
			Item item = (Item) event.getEntity();

			if (ItemUtil.SEEDS.contains(item.getItemStack().getType())) {
				event.setCancelled(true);
			}
		}
	}

	@EventHandler
	public void onBlockBreak(BlockBreakEvent event) {
		if (event.isDropItems()) {
			PlantConfig config = this.plugin.getConfiguration().getForPlant(event.getBlock().getType());

			if (config != null && config.maximum > 0) {
				int random = this.random.nextInt(config.maximum - config.minimum + 1) + config.minimum;

				ItemStack itemStack = new ItemStack(config.packet, random);
				ItemMeta itemMeta = itemStack.getItemMeta();
				itemMeta.setDisplayName(ChatColor.AQUA + config.packetName);
				itemStack.setItemMeta(itemMeta);

				event.getBlock().getWorld().dropItemNaturally(event.getBlock().getLocation(), itemStack);
			}
		}
	}
}

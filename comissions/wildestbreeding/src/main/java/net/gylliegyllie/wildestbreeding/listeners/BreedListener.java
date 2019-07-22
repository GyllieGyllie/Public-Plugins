package net.gylliegyllie.wildestbreeding.listeners;

import net.gylliegyllie.wildestbreeding.Plugin;
import net.gylliegyllie.wildestbreeding.configuration.FeedConfig;
import net.gylliegyllie.wildestbreeding.utils.MobUtil;
import org.bukkit.EntityEffect;
import org.bukkit.entity.Animals;
import org.bukkit.entity.Tameable;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityBreedEvent;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class BreedListener implements Listener {

	private final Plugin plugin;
	private final Random random = new Random();

	private List<Animals> breeding = new ArrayList<>();

	public BreedListener(Plugin plugin) {
		this.plugin = plugin;

		this.plugin.getServer().getScheduler().runTaskTimer(this.plugin, () -> {
			new ArrayList<>(this.breeding).forEach(entity -> {
				if (entity.isLoveMode()) {
					entity.playEffect(EntityEffect.LOVE_HEARTS);
				} else {
					breeding.remove(entity);
				}
			});
		}, 20L, 20L);
	}

	@EventHandler
	public void onRightClickMob(PlayerInteractEntityEvent event) {
		if (!MobUtil.TAMABLE.contains(event.getRightClicked().getType())) {
			return;
		}

		FeedConfig config = this.plugin.getConfiguration().getForMob(event.getRightClicked().getType());

		if (config == null) {
			return;
		}

		ItemStack hand = event.getPlayer().getInventory().getItemInMainHand();

		if (MobUtil.BREED_MATERIAL.get(event.getRightClicked().getType()).contains(hand.getType())) {
			event.setCancelled(true);
			return;
		}

		if (!config.mob.name().equals(this.plugin.getCustomFlag(hand))) {
			return;
		}

		event.setCancelled(true);

		Animals animal = (Animals) event.getRightClicked();

		boolean breedable = !animal.isLoveMode() && animal.isAdult() && animal.canBreed();

		if (breedable && animal instanceof Tameable) {
			breedable = ((Tameable) animal).isTamed();
		}

		if (breedable) {
			animal.setLoveModeTicks(30 * 20);
			animal.setBreedCause(event.getPlayer().getUniqueId());
			animal.playEffect(EntityEffect.LOVE_HEARTS);

			hand.setAmount(hand.getAmount() - 1);
			event.getPlayer().getInventory().setItemInMainHand(hand);

			this.breeding.add(animal);
		}
	}

	@EventHandler
	public void onBreed(EntityBreedEvent event) {
		if (!MobUtil.TAMABLE.contains(event.getEntity().getType())) {
			return;
		}

		FeedConfig config = this.plugin.getConfiguration().getForMob(event.getEntity().getType());

		if (config == null) {
			return;
		}

		int random = this.random.nextInt(100) + 1;

		Animals mother = (Animals) event.getMother();
		Animals father = (Animals) event.getFather();

		if (random > config.breedChance) {
			event.setCancelled(true);
			mother.setLoveModeTicks(0);
			father.setLoveModeTicks(0);
		}

		mother.setBreed(false);
		father.setBreed(false);

		this.plugin.getServer().getScheduler().runTaskLater(this.plugin, () -> {
			mother.setBreed(true);
			father.setBreed(true);
		}, config.breedCooldown * 20);
	}
}

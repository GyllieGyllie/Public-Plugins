package net.gylliegyllie.rentingcraft.renting;

import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.Damageable;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Rent {

	private Material material;
	private String name = "";
	private List<String> lore = new ArrayList<>();
	private Map<Enchantment, Integer> enchants = new HashMap<>();
	private Integer durability = 0;

	public Rent(ItemStack itemStack) {
		this.material = itemStack.getType();

		ItemMeta itemMeta = itemStack.getItemMeta();

		if (itemMeta != null) {
			this.name = itemMeta.getDisplayName();

			this.lore = itemMeta.getLore();
			this.enchants = itemMeta.getEnchants();

			if (itemMeta instanceof Damageable) {
				this.durability = ((Damageable) itemMeta).getDamage();
			}
		}

	}

	
}

package net.gylliegyllie.gylliecore.gui;

import org.bukkit.Color;
import org.bukkit.Material;
import org.bukkit.block.banner.Pattern;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.BannerMeta;
import org.bukkit.inventory.meta.Damageable;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.LeatherArmorMeta;
import org.bukkit.inventory.meta.SkullMeta;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ItemBuilder {

	private Material material = null;
	private Integer amount = 1;
	private Integer damage = null;
	private Byte data = 0;
	private String displayName = null;
	private List<String> lore = null;
	private Map<Enchantment, Integer> enchantments = new HashMap<>();
	private String skullName = null;
	private boolean flags = true;
	private Color leatherArmorColor = null;
	private List<Pattern> patterns = new ArrayList<>();

	public ItemBuilder() {}

	public Material getMaterial() {
		return this.material;
	}

	public Integer getAmount() {
		return this.amount;
	}

	public Byte getData() {
		return this.data;
	}

	public String getDisplayName() {
		return this.displayName;
	}

	public List<String> getLore() {
		return this.lore;
	}

	public Map<Enchantment, Integer> getEnchantments() {
		return this.enchantments;
	}

	public ItemBuilder type(Material material) {
		this.material = material;

		return this;
	}

	public ItemBuilder skull(String name) {
		this.material = Material.PLAYER_HEAD;
		this.data = 3;
		this.skullName = name;

		return this;
	}

	public ItemBuilder amount(Integer amount) {
		this.amount = amount;

		return this;
	}

	public ItemBuilder damage(Integer damage) {
		this.damage = damage;

		return this;
	}

	public ItemBuilder data(Byte data) {
		this.data = data;

		return this;
	}

	public ItemBuilder name(String displayName) {
		this.displayName = displayName;

		return this;
	}

	public ItemBuilder lore(String... strings) {
		this.lore = Arrays.asList(strings);

		return this;
	}

	public ItemBuilder lore(List<String> strings) {
		this.lore = strings;

		return this;
	}

	public ItemBuilder enchant(Enchantment enchantment, Integer level) {
		this.enchantments.put(enchantment, level);

		return this;
	}

	public ItemBuilder enchantments(Map<Enchantment, Integer> enchantments) {
		this.enchantments = enchantments;

		return this;
	}

	public ItemBuilder removeFlags() {
		this.flags = true;

		return this;
	}

	public ItemBuilder leatherArmor(Color color) {
		this.leatherArmorColor = color;

		return this;
	}

	public ItemBuilder pattens(List<Pattern> patterns) {
		this.patterns = patterns;

		return this;
	}

	public ItemStack build() {
		ItemStack itemStack = new ItemStack(getMaterial(), getAmount(), (short) getData());
		ItemMeta itemMeta;

		if ((this.displayName != null) || (this.lore != null) || (this.skullName != null) || (this.damage != null)) {
			itemMeta = itemStack.getItemMeta();

			if (itemMeta != null) {
				if (this.displayName != null) {
					itemMeta.setDisplayName(this.displayName);
				}

				if (this.lore != null) {
					itemMeta.setLore(this.lore);
				}

				if (this.skullName != null) {
					((SkullMeta) itemMeta).setOwner(this.skullName);
				}

				if (this.flags) {
					itemMeta.removeItemFlags(ItemFlag.HIDE_ATTRIBUTES);
					itemMeta.removeItemFlags(ItemFlag.HIDE_UNBREAKABLE);
					itemMeta.removeItemFlags(ItemFlag.HIDE_PLACED_ON);
					itemMeta.removeItemFlags(ItemFlag.HIDE_DESTROYS);
					itemMeta.removeItemFlags(ItemFlag.HIDE_ENCHANTS);
					itemMeta.removeItemFlags(ItemFlag.HIDE_POTION_EFFECTS);
				}

				if (this.damage != null && itemMeta instanceof Damageable) {
					((Damageable) itemMeta).setDamage(this.damage);
				}

				itemStack.setItemMeta(itemMeta);
			}
		}

		if (this.enchantments != null) {
			for (Enchantment ench : this.enchantments.keySet()) {
				int level = this.enchantments.get(ench);
				itemStack.addUnsafeEnchantment(ench, level);
			}
		}

		if(this.leatherArmorColor != null) {
			LeatherArmorMeta leatherArmorMeta = (LeatherArmorMeta) itemStack.getItemMeta();

			if (leatherArmorMeta != null) {
				leatherArmorMeta.setColor(this.leatherArmorColor);

				itemStack.setItemMeta(leatherArmorMeta);
			}
		}

		if(!this.patterns.isEmpty()) {
			BannerMeta bannerMeta = (BannerMeta) itemStack.getItemMeta();

			if (bannerMeta != null) {
				for (Pattern pattern : this.patterns) {
					bannerMeta.addPattern(pattern);
				}

				itemStack.setItemMeta(bannerMeta);
			}
		}

		return itemStack;
	}
}

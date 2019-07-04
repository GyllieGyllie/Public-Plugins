package net.gylliegyllie.rentingcraft.renting;

import net.gylliegyllie.gylliecore.gui.ItemBuilder;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.Damageable;
import org.bukkit.inventory.meta.ItemMeta;
import org.json.simple.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class Rent {

	private UUID ID;
	private UUID owner;
	private Integer price;

	private Material material;
	private String name = "";
	private List<String> lore = new ArrayList<>();
	private Map<Enchantment, Integer> enchants = new HashMap<>();
	private Integer durability = 0;

	public Rent(UUID owner, Integer price, ItemStack itemStack) {
		this.ID = UUID.randomUUID();
		this.owner = owner;
		this.price = price;

		this.material = itemStack.getType();

		ItemMeta itemMeta = itemStack.getItemMeta();

		if (itemMeta != null) {

			if (itemMeta.hasDisplayName()) {
				this.name = itemMeta.getDisplayName();
			}

			this.lore = itemMeta.getLore();
			this.enchants = itemMeta.getEnchants();

			if (itemMeta instanceof Damageable) {
				this.durability = ((Damageable) itemMeta).getDamage();
			}
		}

	}

	public UUID getID() {
		return this.ID;
	}

	public UUID getOwner() {
		return this.owner;
	}

	public Integer getPrice() {
		return this.price;
	}

	public ItemStack getItem() {
		ItemBuilder itemBuilder = new ItemBuilder().type(this.material);

		if (!this.name.isEmpty()) {
			itemBuilder.name(this.name);
		}

		itemBuilder.damage(this.durability);

		itemBuilder.lore(this.lore);
		itemBuilder.enchantments(this.enchants);

		return itemBuilder.build();
	}

	public String getJsonItem() {
		JSONObject jsonObject = new JSONObject();

		jsonObject.put("material", this.material);
		jsonObject.put("name", this.name);
		jsonObject.put("durability", this.durability);
		jsonObject.put("lore", this.lore);
		jsonObject.put("enchants", this.enchants);

		return jsonObject.toJSONString();
	}
	
}

package net.gylliegyllie.rentingcraft.managers;

import net.gylliegyllie.rentingcraft.Plugin;
import org.bukkit.Material;

import java.util.ArrayList;
import java.util.List;

public class ToolManager {

	private final Plugin plugin;

	private final List<Material> supported;

	public ToolManager(Plugin plugin) {
		this.plugin = plugin;

		List<Material> items = new ArrayList<>();

		for (String name : this.plugin.getConfiguration().getStringList("tools")) {
			try {
				items.add(Material.valueOf(name));
			} catch (Exception ignored) {}
		}

		this.supported = items;
	}

	public boolean isItemSupported(Material material) {
		return this.supported.contains(material);
	}
}

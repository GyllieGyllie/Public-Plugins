package net.gylliegyllie.example;

import org.bukkit.plugin.java.JavaPlugin;

public class Plugin extends JavaPlugin {

	@Override
	public void onEnable() {

		for (int i = 1; i <= 100; i++) {

			String message = "";

			if (i % 3 == 0) {
				message += "Fizz";
			}

			if (i % 5 == 0) {
				message += "Buzz";
			}

			if (message.isEmpty()) {
				message = String.valueOf(i);
			}

			this.getLogger().info(message);
		}

		this.getLogger().info("Plugin enabled!");
	}

	@Override
	public void onDisable() {
		this.getLogger().info("Plugin disabled!");
	}
}

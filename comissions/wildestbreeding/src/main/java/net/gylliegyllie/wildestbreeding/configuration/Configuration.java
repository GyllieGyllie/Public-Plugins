package net.gylliegyllie.wildestbreeding.configuration;

import com.google.common.reflect.TypeToken;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import net.gylliegyllie.wildestbreeding.Plugin;
import org.bukkit.entity.EntityType;

import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Configuration {

	private final Gson GSON = (new GsonBuilder()).setPrettyPrinting().enableComplexMapKeySerialization().create();

	private final Plugin plugin;

	private List<FeedConfig> configs;

	private Map<EntityType, FeedConfig> mobMapping = new HashMap<>();

	public Configuration(Plugin plugin) throws Exception {
		this.plugin = plugin;

		File file = new File(plugin.getDataFolder(), "config.json");

		if (!file.exists()) {

			file.getParentFile().mkdir();

			try (InputStream in = plugin.getResource("config.json");
			     OutputStream out = new FileOutputStream(file)) {

				byte[] buf = new byte[1024];
				int len;

				while ((len = in.read(buf)) > 0) {
					out.write(buf, 0, len);
				}

			}
		}

		try (FileReader fileReader = new FileReader(file)) {

			Type listType = new TypeToken<ArrayList<FeedConfig>>(){}.getType();
			this.configs = GSON.fromJson(fileReader, listType);

		}

		for (FeedConfig config : this.configs) {
			this.mobMapping.put(config.mob, config);
		}
	}

	public List<FeedConfig> getConfigs() {
		return this.configs;
	}

	public FeedConfig getForMob(EntityType mob) {
		return this.mobMapping.get(mob);
	}
}

package net.gylliegyllie.gylliecore.files;

import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;

public class YamlFile extends YamlConfiguration {

	public static YamlConfiguration getConfiguration(JavaPlugin plugin, String filename) {
		File file = new File(plugin.getDataFolder(), filename);

		if (!file.exists()) {
			if (file.getParentFile().exists() || file.getParentFile().mkdir()) {
				InputStream in = plugin.getResource(filename);

				if (in == null) {
					return null;
				}

				copy(in, file);
			}
		}

		return loadConfiguration(file);
	}

	private static void copy(InputStream in, File file) {

		try (OutputStream out = new FileOutputStream(file)) {
			byte[] buf = new byte[1024];
			int len;

			while ((len = in.read(buf)) > 0) {
				out.write(buf, 0, len);
			}

			out.close();
			in.close();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (in != null) {
				try {
					in.close();
				} catch (Exception ignore) {}
			}
		}

	}
}

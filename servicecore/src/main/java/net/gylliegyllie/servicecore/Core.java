package net.gylliegyllie.servicecore;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import net.gylliegyllie.servicecore.configuration.BaseConfiguration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

public class Core {

	private final static Logger logger = LoggerFactory.getLogger(Core.class);

	public static final Gson GSON = (new GsonBuilder()).setPrettyPrinting().enableComplexMapKeySerialization().create();
	public static final Gson N_GSON = (new GsonBuilder()).enableComplexMapKeySerialization().create();

	/**
	 * Load the baseConfiguration file from the root folder
	 *
	 * @param baseConfiguration BaseConfiguration class to what we need to cast to
	 * @return BaseConfiguration loaded
	 */
	public static BaseConfiguration loadConfiguration(BaseConfiguration baseConfiguration) {
		return loadConfiguration(baseConfiguration, "./");
	}

	/**
	 * Load the configuration from a certain location
	 *
	 * @param configuration Configuration class to what we need to cast to
	 * @param filePath Path from where we want to load the file
	 * @return Configuration loaded
	 */
	public static BaseConfiguration loadConfiguration(BaseConfiguration configuration, String filePath) {

		File file = new File(filePath + "config.json");
		Throwable th;

		if(!file.exists()) {
			try {

				FileWriter writer = new FileWriter(file);
				th = null;

				try {
					GSON.toJson(configuration, writer);
				} catch (Throwable th1) {
					th = th1;
					throw th1;
				} finally {

					if(th != null) {
						try {
							writer.close();
						} catch (Throwable th2) {
							th.addSuppressed(th2);
						}
					} else {
						writer.close();
					}
				}

			} catch (IOException th3) {
				logger.error("", th3);
			}

			logger.error("config.json configuration file not found, saving default and exiting...");
			System.exit(1);
		}

		try {
			FileReader fileReader = new FileReader(file);
			th = null;

			try {
				configuration = GSON.fromJson(fileReader, configuration.getClass());
			} catch (Throwable th1) {
				th = th1;
				throw th1;
			} finally {
				if(th != null) {
					try {
						fileReader.close();
					} catch (Throwable th2) {
						th.addSuppressed(th2);
					}
				} else {
					fileReader.close();
				}

			}
		} catch (Exception th3) {
			logger.error("", th3);
			System.exit(1);
		}

		return configuration;
	}

	/**
	 * Make the service sleep forever
	 */
	public static void sleepForever() {
		while (true) {
			try {
				Thread.sleep(Long.MAX_VALUE);
			} catch (InterruptedException var1) {
				var1.printStackTrace();
			}
		}
	}

	/**
	 * Make the thread this is called from sleep
	 * @param millis Amount of millis to sleep
	 */
	public static void sleep(long millis) {
		try {
			Thread.sleep(millis);
		} catch (Exception ignored) {}
	}
}

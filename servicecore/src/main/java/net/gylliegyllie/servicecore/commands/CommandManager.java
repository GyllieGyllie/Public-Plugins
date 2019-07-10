package net.gylliegyllie.servicecore.commands;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;
import java.util.regex.Pattern;

public class CommandManager {

	private final static Logger logger = LoggerFactory.getLogger(CommandManager.class);

	private static Map<String, CommandExecutor> commandMap = new HashMap<>();

	/**
	 * Register a command for the service
	 *
	 * @param syntax Command Syntax
	 * @param executor CommandExecutor handling the command
	 */
	public static void registerCommand(String syntax, CommandExecutor executor) {
		CommandManager.commandMap.put(syntax, executor);
	}

	/**
	 * Get a CommandExecutor for the given syntax
	 * @param syntax Syntax for the command
	 * @return CommandExecutor if existing, else null
	 */
	public static CommandExecutor getCommand(String syntax) {
		return CommandManager.commandMap.get(syntax);
	}

	/**
	 * Start listening to service commands
	 */
	public static void listenToCommands() {
		if (!CommandManager.commandMap.containsKey("help")) {
			CommandManager.registerCommand("help", (command, args) -> {
				System.out.println("Available commands :");
				CommandManager.commandMap.forEach((s, commandExecutor) -> System.out.println(" > " + s));
				return true;
			});
		}

		new Thread(() -> {

			Scanner scanner = new Scanner(System.in);

			logger.info("Listening to service commands");

			while (true) {
				try {
					String cmd = scanner.next();

					if (cmd != null && !cmd.isEmpty()) {
						String[] args = cmd.split(" ", Pattern.LITERAL);

						if (args.length >= 1 && CommandManager.commandMap.containsKey(args[0])) {

							try {
								// Get the executor
								CommandExecutor commandExecutor = CommandManager.commandMap.get(args[0]);

								// Call command function
								commandExecutor.onCommand(args[0], Arrays.copyOfRange(args, 1, args.length));

							} catch (Exception e) {
								logger.error("Error while running command", e);
							}
						} else {
							logger.error("Error: Command Not Found!");
						}
					}
				} catch (Exception e) {
					logger.error("", e);
				}
			}
		}
		).start();
	}

}

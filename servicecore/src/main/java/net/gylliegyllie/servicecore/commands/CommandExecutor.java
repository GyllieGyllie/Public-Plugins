package net.gylliegyllie.servicecore.commands;

public interface CommandExecutor {

	/**
	 * Executes the given command, returning its success
	 *
	 * @param command Command which was executed
	 * @param args Passed command arguments
	 * @return true if a valid command, otherwise false
	 */
	public boolean onCommand(String command, String[] args);

}
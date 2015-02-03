package me.micrjonas.grandtheftdiamond.command;

import me.micrjonas.grandtheftdiamond.GrandTheftDiamond;

import org.bukkit.command.CommandSender;

/**
 * Implementing classes listen to a specific sub command of the plugin.<br>
 * Register with {@link GrandTheftDiamond#registerCommand(CommandExecutor, String, String...)}
 */
public interface CommandExecutor {
	
	/**
	 * Executes the given command
	 * @param sender Source of the command
	 * @param alias Alias of the command which was used
	 * @param args Passed command arguments in lower cases to use {@link String#equals(Object)} instated of {@link String#equalsIgnoreCase(String)}
	 * @param originalArgs Original passed command arguments
	 */
	public abstract void onCommand(CommandSender sender, String alias, String[] args, String[] originalArgs);

}

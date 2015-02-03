package me.micrjonas.grandtheftdiamond.command;

import java.util.Collection;

import org.bukkit.command.CommandSender;

/**
 * Represents a class which can suggest tab completions for commands
 */
public interface TabCompleter {
	
	/**
	 * Returns a list of all valid arguments of the command with the passed arguments
	 * @param sender Source of the command
	 * @param args Passed command arguments in lower cases to use {@link String#equals(Object)}
	 * 	instated of {@link String#equalsIgnoreCase(String)}
	 * @return A list of all valid arguments of the command with the passed arguments.
	 * 	{@code null} for an empty suggestion list
	 */
	Collection<String> onTabComplete(CommandSender sender, String[] args);

}

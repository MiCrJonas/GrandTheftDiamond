package me.micrjonas.grandtheftdiamond.command;

import me.micrjonas.grandtheftdiamond.data.TemporaryPluginData;
import me.micrjonas.grandtheftdiamond.messenger.Messenger;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

/**
 * Manages command '/gtd wand'
 */
public class CommandWand implements CommandExecutor {

	@Override
	public void onCommand(CommandSender sender, String alias, String[] args, String[] originalArgs) {
		if (sender instanceof Player) {
			Player p = (Player) sender;
			if (TemporaryPluginData.getInstance().hasWandEnabled(p)) {
				TemporaryPluginData.getInstance().setWandEnabled(p, false);
				Messenger.getInstance().sendPluginMessage(sender, "wand.disabled");
			}
			else {	
				TemporaryPluginData.getInstance().setWandEnabled(p, true);
				Messenger.getInstance().sendPluginMessage(sender, "wand.enabled");
			}
		}
		else {
			Messenger.getInstance().sendPluginMessage(sender, "notAsConsole");
		}
	}

}

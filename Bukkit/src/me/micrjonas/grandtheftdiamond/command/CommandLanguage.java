package me.micrjonas.grandtheftdiamond.command;

import java.util.Collection;

import me.micrjonas.grandtheftdiamond.data.FileManager;
import me.micrjonas.grandtheftdiamond.data.PluginFile;
import me.micrjonas.grandtheftdiamond.messenger.LanguageManager;
import me.micrjonas.grandtheftdiamond.messenger.Messenger;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class CommandLanguage implements CommandExecutor, TabCompleter {

	
	@Override
	public void onCommand(CommandSender sender, String alias, String[] args, String[] originalArgs) {
		
		if (args.length >= 2) {
			
			if (LanguageManager.getInstance().getRegisteredLanguages().contains(args[1])) {
				
				if (sender instanceof Player) {
				
					FileManager.getInstance().getPlayerData((Player) sender).set("language", args[1]);
					Messenger.getInstance().sendPluginMessage(sender, "language.ownLanguageSet", "%language%", args[1]);
					
				}
				
				else {
					
					FileManager.getInstance().getFileConfiguration(PluginFile.CONFIG).set("language.defaultLanguage", args[1]);
					Messenger.getInstance().sendPluginMessage(sender, "language.defaultLanguageSet", "%language%", args[1]);
					
				}
				
			}
			
			else
				Messenger.getInstance().sendPluginMessage(sender, "language.noLanguage", "%language%", args[1]);
			
		}
		
		else {
			
			Messenger.getInstance().sendPluginMessage(sender, "wrongUsage");
			Messenger.getInstance().sendRightUsage(sender, alias, "<" + Messenger.getInstance().getPluginWord("language") + ">");
			
		}
		
	}
	
	
	@Override
	public Collection<String> onTabComplete(CommandSender sender, String[] args) {
		
		if (args.length == 2)
			return LanguageManager.getInstance().getRegisteredLanguages();
		
		return null;
		
	}

}

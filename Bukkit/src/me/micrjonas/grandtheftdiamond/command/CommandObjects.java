package me.micrjonas.grandtheftdiamond.command;

import me.micrjonas.grandtheftdiamond.item.pluginitem.ItemManager;
import me.micrjonas.grandtheftdiamond.messenger.Messenger;

import org.bukkit.command.CommandSender;

public class CommandObjects implements CommandExecutor {

	
	@Override
	public void onCommand(CommandSender sender, String alias, String[] args, String[] originalArgs) {
		
		sender.sendMessage(Messenger.getInstance().getFormat("header").replaceAll("%title%", Messenger.getInstance().getPluginWordStartsUpperCase("objects")));

		for (String item : ItemManager.getInstance().getAllItemsSorted()) {
			
			String objectName = item;
			String objectNameSub = item.substring(0, 1).toUpperCase();
			
			objectName = item.substring(1, item.length());
			
			sender.sendMessage(" §e- " + objectNameSub + objectName);
			
		}

	}

}

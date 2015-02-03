package me.micrjonas.grandtheftdiamond.command;

import me.micrjonas.grandtheftdiamond.GrandTheftDiamond;
import me.micrjonas.grandtheftdiamond.data.FileManager;
import me.micrjonas.grandtheftdiamond.messenger.Messenger;
import me.micrjonas.grandtheftdiamond.messenger.NoPermissionType;

import org.bukkit.command.CommandSender;

public class CommandSavedata implements CommandExecutor {

	
	@Override
	public void onCommand(CommandSender sender, String alias, String[] args, String[] originalArgs) {
		
		if (GrandTheftDiamond.checkPermission(sender, "savedata", true, NoPermissionType.COMMAND)) {

			FileManager.getInstance().saveAllDataFiles();
			Messenger.getInstance().sendPluginMessage(sender, "dataSaved");
			
		}
		
	}

}

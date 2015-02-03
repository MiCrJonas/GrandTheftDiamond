package me.micrjonas.grandtheftdiamond.command;

import me.micrjonas.grandtheftdiamond.GrandTheftDiamond;
import me.micrjonas.grandtheftdiamond.data.FileManager;
import me.micrjonas.grandtheftdiamond.data.PluginFile;
import me.micrjonas.grandtheftdiamond.messenger.Messenger;
import me.micrjonas.grandtheftdiamond.messenger.NoPermissionType;
import me.micrjonas.grandtheftdiamond.updater.Updater;

import org.bukkit.command.CommandSender;

public class CommandUpdate implements CommandExecutor {
	
	
	@Override
	public void onCommand(final CommandSender sender, String alias, String[] args, String[] originalArgs) {
		
		if (GrandTheftDiamond.checkPermission(sender, "update", true, NoPermissionType.COMMAND)) {
			
			if (FileManager.getInstance().getFileConfiguration(PluginFile.CONFIG).getBoolean("useUpdater")) {
				
				if (Updater.updateAvailable()) {
					
					Messenger.getInstance().sendPluginMessage(sender, "downloadingFile");
					
					GrandTheftDiamond.runTaskAsynchronously(new Runnable() {
						
						@Override
						public void run() {
							
							Updater.download();
							
							String version = Updater.getUpdateVersionName();
							version = version.replace("GrandTheftDiamond v", "");
								
							Messenger.getInstance().sendPluginMessage(sender, "updated", new String[]{"%version%"}, new String[]{version});
							
						}
						
					});
					
				}
				
				else 
					Messenger.getInstance().sendPluginMessage(sender, "noUpdate");
				
			}
			
			else 
				Messenger.getInstance().sendPluginMessage(sender, "updaterNotEnabled");
			
		}
		
	}

}

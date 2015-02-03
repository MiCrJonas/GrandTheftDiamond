package me.micrjonas.grandtheftdiamond.command;

import me.micrjonas.grandtheftdiamond.GrandTheftDiamond;
import me.micrjonas.grandtheftdiamond.data.FileManager;
import me.micrjonas.grandtheftdiamond.data.PluginData;
import me.micrjonas.grandtheftdiamond.data.PluginFile;
import me.micrjonas.grandtheftdiamond.messenger.Messenger;
import me.micrjonas.grandtheftdiamond.messenger.NoPermissionType;
import me.micrjonas.grandtheftdiamond.rob.RobManager;
import me.micrjonas.grandtheftdiamond.util.bukkit.Materials;

import org.bukkit.Material;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class CommandSetsafe implements CommandExecutor {

	private String safeMaterialName;
	
	public CommandSetsafe() {
		
		Material safeType = Materials.getMaterialFromConfig(FileManager.getInstance().getFileConfiguration(PluginFile.EVENT_CONFIG), "robbing.safe.block");
		safeMaterialName = safeType == null ? "{missconfigured in config file}" : safeType.name().toLowerCase();
		
	}
	
	
	@Override
	public void onCommand(CommandSender sender, String alias, String[] args, String[] originalArgs) {
		
		if (sender instanceof Player) {
			
			Player p = (Player) sender;
			
			if (GrandTheftDiamond.checkPermission(p, "setup.safe", true, NoPermissionType.COMMAND)) {
				
				if (PluginData.getInstance().arenaSet(true)) {
				
					if (args.length >= 2) {
						
						StringBuilder nameBuilder = new StringBuilder();
						
						for (int i = 1; i < originalArgs.length - 1; i++)
							nameBuilder.append(originalArgs[i]).append(' ');
							
						nameBuilder.append(originalArgs[originalArgs.length - 1]);
						
						String name = nameBuilder.toString();
					
						if (PluginData.getInstance().arenaSet(true)) {
							
							RobManager.getInstance().setCreatingSafe(p, name);
							Messenger.getInstance().sendPluginMessage(p, "clickToSetSafe", new String []{"%block%", "%name%"}, new String []{safeMaterialName, name});
							
						}
						
					}
					
					else
						Messenger.getInstance().onWrongUsage(sender, alias, "setsafe <" + Messenger.getInstance().getPluginWord("name") + " ...>");
					
				}	
				
				else {
					
					if (GrandTheftDiamond.checkPermission(p, "gta.setup.create"))
						Messenger.getInstance().sendPluginMessage(p, "youNeedSetupMap");
						
					else
						Messenger.getInstance().sendPluginMessage(p, "adminNeedSetupMap");
						
				}
				
			}
		
		}
		
		else {
			
			Messenger.getInstance().sendPluginMessage(sender, "notAsConsole");
			
		}
		
	}

}

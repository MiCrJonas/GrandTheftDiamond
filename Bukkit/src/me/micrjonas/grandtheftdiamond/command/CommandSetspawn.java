package me.micrjonas.grandtheftdiamond.command;

import java.util.Arrays;
import java.util.Collection;

import me.micrjonas.grandtheftdiamond.GrandTheftDiamond;
import me.micrjonas.grandtheftdiamond.data.PluginData;
import me.micrjonas.grandtheftdiamond.messenger.Messenger;
import me.micrjonas.grandtheftdiamond.messenger.NoPermissionType;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class CommandSetspawn implements CommandExecutor, TabCompleter {

	
	@Override
	public void onCommand(CommandSender sender, String alias, String[] args, String[] originalArgs) {
		
		if (sender instanceof Player) {
			
			Player p = (Player) sender;
			
			if (GrandTheftDiamond.checkPermission(p, "setup.spawn", true, NoPermissionType.COMMAND)) {
				
				if (PluginData.getInstance().inArena(p.getLocation(), false)) {
			
					if (args.length <= 1) {
						
						PluginData.getInstance().setDefaultSpawn(p.getLocation());
						Messenger.getInstance().sendPluginMessage(p, "defaultSpawnSet");
						
					}
					
					else {
						
						args[1] = args[1].toLowerCase();
						
						if (args[1].equals("civilian") || args[1].equals("cop") || args[1].equals("hospital")) {
							
							int id = PluginData.getInstance().setSpawn(p.getLocation(), args[1]);
							Messenger.getInstance().sendPluginMessage(p, "spawnSet", new String[]{"%spawnType%", "%spawn%"}, new String[]{args[1], String.valueOf(id)});
							
						}
						else {
							
							Messenger.getInstance().sendPluginMessage(p, "wrongUsage");
							Messenger.getInstance().sendRightUsage(p, alias, "setspawn [civilian|cop|hospital]");
							
						}
						
					}
					
				}
				
				else
					Messenger.getInstance().sendPluginMessage(p, "mustBeInArena");
			
			}
			
		}
		
		else {
			
			Messenger.getInstance().sendPluginMessage(sender, "notAsConsole");
			
		}
		
	}

	
	@Override
	public Collection<String> onTabComplete(CommandSender sender, String[] args) {
		
		if (args.length == 2)
			return Arrays.asList("", "civilian", "cop", "hospital", "car:");
		
		return null;
		
	}

}

package me.micrjonas.grandtheftdiamond.command;

import java.util.Arrays;
import java.util.Collection;

import me.micrjonas.grandtheftdiamond.GrandTheftDiamond;
import me.micrjonas.grandtheftdiamond.data.PluginData;
import me.micrjonas.grandtheftdiamond.jail.Jail;
import me.micrjonas.grandtheftdiamond.jail.JailManager;
import me.micrjonas.grandtheftdiamond.messenger.Messenger;
import me.micrjonas.grandtheftdiamond.messenger.NoPermissionType;
import me.micrjonas.util.Nameables;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class CommandSetjail implements CommandExecutor, TabCompleter {

	
	@Override
	public void onCommand(CommandSender sender, String alias, String[] args, String[] originalArgs) {
		
		if (sender instanceof Player) {
			
			Player p = (Player) sender;
			
			if (GrandTheftDiamond.checkPermission(p, "setup.jail", true, NoPermissionType.COMMAND)) {
		
				if (args.length >= 3) {
					
					if (PluginData.getInstance().inArena(p.getLocation())) {
						
						Jail jail = JailManager.getInstance().getJail(args[2]);

						if (args[1].equalsIgnoreCase("spawn")) {
								
							if (jail == null)
								JailManager.getInstance().createJail(args[2], p.getLocation(), null);
							
							else
								jail.setSpawn(p.getLocation());
								
							Messenger.getInstance().sendPluginMessage(p, "jailSpawnSet", new String[]{"%jail%"}, new String[]{args[2]});
								
						}
							
						else if (args[1].equalsIgnoreCase("cell")) {
								
							if (jail != null) {
								
								jail.addCell(p.getLocation());
								
								Messenger.getInstance().sendPluginMessage(p, "jailCellAdded", new String[]{"%jail%"}, new String[]{args[2]});
									
								
							}
							
							else
								Messenger.getInstance().sendPluginMessage(p, "jailSpawnNotSet", new String[]{"%jail%"}, new String[]{args[2]});
								
						}
							
						else {
								
							Messenger.getInstance().sendPluginMessage(p, "wrongUsage");
							Messenger.getInstance().sendRightUsage(p, alias, "setjail <cell|spawn> <" + Messenger.getInstance().getPluginWord("jailId") + ">");
								
						}
						
					}
					
					else {
						
						Messenger.getInstance().sendPluginMessage(p, "mustBeInArena");
						
					}
					
				}
				
				else {
					
					Messenger.getInstance().sendPluginMessage(p, "wrongUsage");
					Messenger.getInstance().sendRightUsage(p, alias, "setjail <cell|spawn> <" + Messenger.getInstance().getPluginWord("jailName") + ">");
					
				}
				
			}

		}
		
		else {
			
			Messenger.getInstance().sendPluginMessage(sender, "notAsConsole");
			
		}
		
	}

	@Override
	public Collection<String> onTabComplete(CommandSender sender, String[] args) {
		
		if (args.length == 2)
			return Arrays.asList("cell", "spawn");
		
		if (args.length == 3)
			return Nameables.getNameList(JailManager.getInstance().getAllObjects());
		
		return null;
		
	}

}

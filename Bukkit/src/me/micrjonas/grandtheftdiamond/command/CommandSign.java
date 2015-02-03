package me.micrjonas.grandtheftdiamond.command;

import java.util.Arrays;
import java.util.Collection;

import me.micrjonas.grandtheftdiamond.GrandTheftDiamond;
import me.micrjonas.grandtheftdiamond.data.TemporaryPluginData;
import me.micrjonas.grandtheftdiamond.messenger.Messenger;
import me.micrjonas.grandtheftdiamond.messenger.NoPermissionType;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class CommandSign implements CommandExecutor, TabCompleter {

	
	@Override
	public void onCommand(CommandSender sender, String alias, String[] args, String[] originalArgs) {
		
		if (sender instanceof Player) {
			
			Player p = (Player) sender;
		
			if (GrandTheftDiamond.checkPermission(p, "setup.sign", true, NoPermissionType.COMMAND)) {	
					
				if (args.length >= 3) {
									
					if (args[1].equalsIgnoreCase("setcooldown")) {
							
						try {
								
							int cooldown = Integer.parseInt(args[2]);
							
							TemporaryPluginData.getInstance().setNewCooldown(p, cooldown);
							
							Messenger.getInstance().sendPluginMessage(p, "clickSign");
							
						}
						
						catch(Exception e) {
							
							Messenger.getInstance().sendPluginMessage(p, "mustBeANumber", new String[]{"%argument%"}, new String[]{"3"});
							
						}
						
					}
					
					else {
						
						Messenger.getInstance().sendPluginMessage(p, "wrongUsage");
						Messenger.getInstance().sendRightUsage(p, alias, "sign setcooldown <" + Messenger.getInstance().getPluginWord("cooldown") + ">");
						
					}
					
				}
				
				else {
					
					Messenger.getInstance().sendPluginMessage(p, "wrongUsage");
					Messenger.getInstance().sendRightUsage(p, alias, "sign setcooldown <" + Messenger.getInstance().getPluginWord("cooldown") + ">");
					
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
			return Arrays.asList("setcooldown");
		
		return null;
		
	}

}

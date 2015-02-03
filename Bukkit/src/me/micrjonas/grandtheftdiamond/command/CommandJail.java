package me.micrjonas.grandtheftdiamond.command;

import java.util.Collection;
import me.micrjonas.grandtheftdiamond.GrandTheftDiamond;
import me.micrjonas.grandtheftdiamond.api.event.cause.JailReason;
import me.micrjonas.grandtheftdiamond.data.TemporaryPluginData;
import me.micrjonas.grandtheftdiamond.jail.Jail;
import me.micrjonas.grandtheftdiamond.jail.JailManager;
import me.micrjonas.grandtheftdiamond.messenger.Messenger;
import me.micrjonas.grandtheftdiamond.messenger.NoPermissionType;
import me.micrjonas.grandtheftdiamond.util.Nameables;

import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class CommandJail implements CommandExecutor, TabCompleter {

	@Override
	public void onCommand(CommandSender sender, String alias, String[] args, String[] originalArgs) {
		
		//gtd jail <player> <time> [jail]
		
		if (GrandTheftDiamond.checkPermission(sender, "jail", true, NoPermissionType.COMMAND)) {
			
			if (args.length >= 3) {
				
				Player p = Bukkit.getPlayer(args[1]);
				
				if (TemporaryPluginData.getInstance().isIngame(p)) {
					
					if (!JailManager.getInstance().isJailed(p)) {
					
						int time;
						
						try {
							time = Integer.parseInt(args[2]);
						}
						
						catch (NumberFormatException ex) {
							Messenger.getInstance().sendPluginMessage(sender, "mustBeANumber", "%argument%", "3");
							return;
						}
						
						Jail jail = null;
						
						if (args.length >= 4) {
							
							jail = JailManager.getInstance().getJail(args[3]);
							
							if (jail == null) {
								
								Messenger.getInstance().sendPluginMessage(sender, "notAJail", "%argument%", args[3]);
								return;
								
							}
							
						}
						
						else {
							
							jail = JailManager.getInstance().getRandomUsableJail();
							
							if (jail == null) {
								
								Messenger.getInstance().sendPluginMessage(sender, "noJailsAvailable");
								return;
								
							}
							
						}
					
						JailManager.getInstance().jailPlayer(p, JailReason.COMMAND, jail, time, 0);
						Messenger.getInstance().sendPluginMessage(sender, "jailedCommand", new Player[]{p},
								new String[]{"%time%", "%jail%"},
								new String[]{args[2], jail.getName()});
						
					}
					
					else
						Messenger.getInstance().sendPluginMessage(sender, "playerAlreadyJailed", new Player[]{p});
					
				}
				
				else
					Messenger.getInstance().sendPluginMessage(sender, "playerNotIngame");
				
			}
			
			else
				Messenger.getInstance().onWrongUsage(sender, alias, "jail <" + Messenger.getInstance().getPluginWord("player") 
						+ "> <" + Messenger.getInstance().getPluginWord("time")
						+ "> [" + Messenger.getInstance().getPluginWord("jail") + "]");
			
		}
		
	}
	
	
	@Override
	public Collection<String> onTabComplete(CommandSender sender, String[] args) {
		
		if (args.length == 2)
			return GrandTheftDiamond.getPlayerNames(TemporaryPluginData.getInstance().getIngamePlayers());
		
		if (args.length == 4)
			return Nameables.getNameList(JailManager.getInstance().getAllObjects());
		
		return null;
		
	}

}

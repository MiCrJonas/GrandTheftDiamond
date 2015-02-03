package me.micrjonas.grandtheftdiamond.command;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import me.micrjonas.grandtheftdiamond.GameManager;
import me.micrjonas.grandtheftdiamond.GrandTheftDiamond;
import me.micrjonas.grandtheftdiamond.api.event.cause.LeaveReason;
import me.micrjonas.grandtheftdiamond.data.TemporaryPluginData;
import me.micrjonas.grandtheftdiamond.messenger.Messenger;
import me.micrjonas.grandtheftdiamond.messenger.NoPermissionType;

import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class CommandKick implements CommandExecutor, TabCompleter {

	
	@Override
	public void onCommand(CommandSender sender, String alias, String[] args, String[] originalArgs) {
		
		if (GrandTheftDiamond.checkPermission(sender, "kick", true, NoPermissionType.COMMAND)) {
			
			if (args.length >= 2) {
				
				if (args[1].equalsIgnoreCase("all")) {
						
					for (Player p : TemporaryPluginData.getInstance().getIngamePlayers()) {

						GameManager.getInstance().leaveGame(p, LeaveReason.ALL_KICK);
						Messenger.getInstance().sendPluginMessage(p, "kicked");
						
					}
						
					Messenger.getInstance().sendPluginMessage(sender, "allKicked");
					
				}
				
				else {

					//try {
							
						Player p = Bukkit.getServer().getPlayer(args[1]);
								
						if (TemporaryPluginData.getInstance().isIngame(p)) {
										
							GameManager.getInstance().leaveGame(p, LeaveReason.KICK);
							
							Messenger.getInstance().sendPluginMessage(p, "kicked");
							Messenger.getInstance().sendPluginMessage(sender, "kickedOther", new CommandSender[]{p});
										
						}
									
						else 
							Messenger.getInstance().sendPluginMessage(sender, "playerNotIngame");
									
					/**}
							
					catch(NullPointerException e) {
								
						LOLMessenger.getInstance().sendPluginMessage(sender, "playerNotOnline");
								
					}*/
								
				}
						
			}
				
			else {
				
				Messenger.getInstance().sendPluginMessage(sender, "wrongUsage");
				Messenger.getInstance().sendRightUsage(sender, alias, "kick <all|<" + Messenger.getInstance().getPluginWord("player") + ">>");
				
				
			}
			
		}

	}

	
	@Override
	public Collection<String> onTabComplete(CommandSender sender, String[] args) {
		
		if (args.length == 2) {
			
			List<String> completions = new ArrayList<>(GrandTheftDiamond.getPlayerNames(TemporaryPluginData.getInstance().getIngamePlayers()));
			completions.add("all");
			Collections.sort(completions);
			
			return completions;
			
		}
		
		return null;
		
	}

}

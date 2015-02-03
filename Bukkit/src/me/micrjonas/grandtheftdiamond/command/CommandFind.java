package me.micrjonas.grandtheftdiamond.command;

import java.util.ArrayList;
import java.util.Collection;

import me.micrjonas.grandtheftdiamond.GrandTheftDiamond;
import me.micrjonas.grandtheftdiamond.data.TemporaryPluginData;
import me.micrjonas.grandtheftdiamond.messenger.Messenger;
import me.micrjonas.grandtheftdiamond.messenger.NoPermissionType;

import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

/**
 * Manages command '/gtd find *' and its {@link TabCompleter}
 */
public class CommandFind implements CommandExecutor, TabCompleter {

	
	@Override
	public void onCommand(CommandSender sender, String alias, String[] args, String[] originalArgs) {
		
		if (sender instanceof Player) {
			
			Player p = (Player) sender;
			
			if (GrandTheftDiamond.checkPermission(p, "find", true, NoPermissionType.COMMAND)) {
				
				if (TemporaryPluginData.getInstance().isIngame(p)) {
				
					if (args.length >= 2) {
						
						try {
							
							Player otherP = Bukkit.getServer().getPlayer(args[1]);
							
							if (TemporaryPluginData.getInstance().isIngame(otherP)) {
								
								TemporaryPluginData.getInstance().setTargetPlayer(p, otherP);
								p.setCompassTarget(otherP.getLocation());
								Messenger.getInstance().sendPluginMessage(p, "targetPlayerSet", new Player[]{otherP});
								
							}
							
							else
								Messenger.getInstance().sendPluginMessage(otherP, "playerNotIngame");
							
						}
						
						catch (NullPointerException e) {
							
							Messenger.getInstance().sendPluginMessage(p, "playerNotOnline");
							
						}
						
					}
					
					else {
						
						Messenger.getInstance().sendPluginMessage(p, "wrongUsage");
						Messenger.getInstance().sendRightUsage(p, alias, "find <" + Messenger.getInstance().getPluginWord("player") + ">");
						
					}
					
				}
				
				else
					Messenger.getInstance().sendPluginMessage(p, "mustBeIngame");
				
			}

		}
		
		else
			Messenger.getInstance().sendPluginMessage(sender, "notAsConsole");
		
	}

	@Override
	public Collection<String> onTabComplete(CommandSender sender, String[] args) {
		
		return new ArrayList<>(GrandTheftDiamond.getOnlinePlayerNames());
		
	}

}

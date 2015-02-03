package me.micrjonas.grandtheftdiamond.command;

import java.util.Arrays;
import java.util.Collection;

import me.micrjonas.grandtheftdiamond.GrandTheftDiamond;
import me.micrjonas.grandtheftdiamond.Team;
import me.micrjonas.grandtheftdiamond.data.PluginData;
import me.micrjonas.grandtheftdiamond.data.TemporaryPluginData;
import me.micrjonas.grandtheftdiamond.messenger.Messenger;
import me.micrjonas.grandtheftdiamond.messenger.NoPermissionType;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class CommandList implements CommandExecutor, TabCompleter {

	
	@Override
	public void onCommand(CommandSender sender, String alias, String[] args, String[] originalArgs) {
		
		if (GrandTheftDiamond.checkPermission(sender, "list", true, NoPermissionType.COMMAND)) {
			
			if (args.length >= 2) {
				
				args[1] = args[1].toLowerCase();
				
				if (!(args[1].equals("civilian") || args[1].equals("civilians") ||
						args[1].equals("gangster") || args[1].equals("wanted") ||
						args[1].equals("cop") || args[1].equals("cops"))) {
					
					Messenger.getInstance().sendPluginMessage(sender, "wrongUsage");
					Messenger.getInstance().sendRightUsage(sender, alias, "list [civilian|wanted|cop]");
					
					return;
					
				}
				
			}
			
			String civilians = "";
			String gangsters = "";
			String cops = "";
			
			int civilinCount = 0;
			int gangsterCount = 0;
			int copCount = 0;
			
			String playersFormat = Messenger.getInstance().getFormat("players");
			
			for (Player p : GrandTheftDiamond.getOnlinePlayers()) {
				
				if (TemporaryPluginData.getInstance().isIngame(p)) {
					
					if (PluginData.getInstance().getTeam(p) == Team.CIVILIAN) {
						
						if (PluginData.getInstance().getWantedLevel(p) > 0) {
							
							gangsters = gangsters + playersFormat.replaceAll("%player%", p.getName());
							gangsterCount++;
							
						}
						
						else {
							
							civilians = civilians + playersFormat.replaceAll("%player%", p.getName());
							civilinCount++;
							
						}
						
					}
					
					else {
						
						cops = cops + playersFormat.replaceAll("%player%", p.getName());
						copCount++;
						
					}
					
				}
				
			}
			
			if (civilians.equals(""))
				civilians = Messenger.getInstance().getPluginWord("none");
			
			
			if (gangsters.equals(""))
				gangsters = Messenger.getInstance().getPluginWord("none");
			
			
			if (cops.equals(""))
				cops = Messenger.getInstance().getPluginWord("none");
			
			String ingameListFormat = Messenger.getInstance().getFormat("ingameList");
			
			if (args.length == 1)
				Messenger.getInstance().sendPluginMessage(sender, "ingameList", new String[]{"%ingameCount%"}, new String[]{String.valueOf(civilinCount + gangsterCount + copCount)});
			
			if (args.length == 1 || args[1].equals("civilian") || args[1].equals("civilians"))
				sender.sendMessage(ingameListFormat.replaceAll("%team%", Messenger.getInstance().getPluginWordStartsUpperCase("civilian")).replaceAll("%teamCount%", String.valueOf(civilinCount)).replaceAll("%players%", civilians));
			
			if (args.length == 1 || args[1].equals("gangster") || args[1].equals("gangsters") || args[1].equals("wanted"))
				sender.sendMessage(ingameListFormat.replaceAll("%team%", Messenger.getInstance().getPluginWordStartsUpperCase("gangster")).replaceAll("%teamCount%", String.valueOf(gangsterCount)).replaceAll("%players%", gangsters));
			
			if (args.length == 1 || args[1].equals("cop") || args[1].equals("cops"))
				sender.sendMessage(ingameListFormat.replaceAll("%team%", Messenger.getInstance().getPluginWordStartsUpperCase("cop")).replaceAll("%teamCount%", String.valueOf(copCount)).replaceAll("%players%", cops));
			
		}

	}

	
	@Override
	public Collection<String> onTabComplete(CommandSender sender, String[] args) {
		
		if (args.length == 2)
			return Arrays.asList("civilian", "cop", "gangster");
		
		return null;
		
	}

}

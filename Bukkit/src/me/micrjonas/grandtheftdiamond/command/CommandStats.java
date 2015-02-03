package me.micrjonas.grandtheftdiamond.command;

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import me.micrjonas.grandtheftdiamond.GrandTheftDiamond;
import me.micrjonas.grandtheftdiamond.messenger.Messenger;
import me.micrjonas.grandtheftdiamond.stats.StatsManager;
import me.micrjonas.grandtheftdiamond.stats.StatsType;
import me.micrjonas.grandtheftdiamond.util.Enums;

import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class CommandStats implements CommandExecutor, TabCompleter {

	private List<String> statsTypes = Enums.namesAsList(StatsType.class);
	private Map<StatsType, String> statsNames = new LinkedHashMap<>();
	
	public CommandStats() {
		
		Collections.sort(statsTypes);
		
		for (int i = 0; i < statsTypes.size(); i++)
			statsTypes.set(i, statsTypes.get(i).toLowerCase().replace('_', '-'));
		
		for (StatsType type : StatsType.values())
			statsNames.put(type, Messenger.getInstance().getPluginWord("stats." + type.name().toLowerCase()));
		
	}
	
	
	@Override
	public void onCommand(CommandSender sender, String alias, String[] args, String[] originalArgs) {
		
		if (StatsManager.getInstance().loaded()) {
		
			if (args.length >= 2) {
				
			//set
				if (args[1].equals("set")) {
					
					if (args.length >= 4) {
						
						StatsType type = Enums.valueOf(StatsType.class, args[2]);
						
						if (type == null) {
		
							Messenger.getInstance().onWrongConsoleUsage(alias, "stats set "
									+ "<" + Messenger.getInstance().getPluginWord("statsType") + ">"
									+ "<" + Messenger.getInstance().getPluginWord("value") + ">"
									+ " <" + Messenger.getInstance().getPluginWord("player") + "<");
							
							return;
							
						}
						
						@SuppressWarnings("unused")
						int value;
						Player p;
						
						try {
							value = Integer.parseInt(args[3]);
						}
						
						catch (NumberFormatException ex) {
							Messenger.getInstance().sendPluginMessage(sender, "musteBeANumber", "%argument%", "3");
							return;
						}
						
						if (args.length == 4) {
							
							if (sender instanceof Player)
								p = (Player) sender;
							
							else {
								
								Messenger.getInstance().onWrongConsoleUsage(alias, "stats set "
										+ "<" + Messenger.getInstance().getPluginWord("statsType") + ">"
										+ "<" + Messenger.getInstance().getPluginWord("value") + ">"
										+ " <" + Messenger.getInstance().getPluginWord("player") + "<");
								
								return;
								
							}
							
						}
						
						else {
							
							p = Bukkit.getPlayer(args[4]);
							
							if (p == null) {
								
								Messenger.getInstance().sendPluginMessage(sender, "playerNotOnline");
								
								return;
								
							}
							
						}
						
					}
					
					else
						Messenger.getInstance().onWrongUsage(sender, alias, "stats set "
								+ "<" + Messenger.getInstance().getPluginWord("statsType") + ">"
								+ "<" + Messenger.getInstance().getPluginWord("value") + ">"
								+ " [" + Messenger.getInstance().getPluginWord("player") + "]");
					
				}
				
			//show
				else if (args[1].equals("show")) {
					
					if (args.length == 2) {
						
						if (sender instanceof Player) {
							Messenger.getInstance().sendHeader(sender, Messenger.getInstance().getPluginWord("stats.stats") + " (" + sender.getName() + ")");
							
							String statsFormat = Messenger.getInstance().getFormat("stats.self");
							
							for (Entry<StatsType, String> entry : statsNames.entrySet())
								sender.sendMessage(statsFormat.replaceAll("%statsType%", entry.getValue()).
										replace("%value%", String.valueOf(StatsManager.getInstance().getStats((Player) sender, entry.getKey()))));
							
							
							
						}
						
						else
							Messenger.getInstance().onWrongConsoleUsage(alias, "stats show <");
						
					}
					
				}
				
				else
					Messenger.getInstance().onWrongUsage(sender, alias, "stats <set|show|top>");
				
			}
			
			else
				Messenger.getInstance().onWrongUsage(sender, alias, "stats <set|show|top>");
			
		}
		
		else
			Messenger.getInstance().sendPluginMessage(sender, "stats.notLoaded");
		
	}


	@Override
	public Collection<String> onTabComplete(CommandSender sender, String[] args) {
		
		if (args.length == 2)
			return Arrays.asList("set", "show", "top");
		
		if (args.length == 3) {
			
			if (args[1].equals("set"))
				return statsTypes;
			
			else if (args[1].equals("show"))
				return GrandTheftDiamond.getOnlinePlayerNames();
			
		}
		
		return null;
		
	}

}

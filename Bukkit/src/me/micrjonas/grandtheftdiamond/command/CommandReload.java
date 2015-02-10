package me.micrjonas.grandtheftdiamond.command;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import me.micrjonas.grandtheftdiamond.GrandTheftDiamond;
import me.micrjonas.grandtheftdiamond.bukkit.BukkitGrandTheftDiamondPlugin;
import me.micrjonas.grandtheftdiamond.data.FileManager;
import me.micrjonas.grandtheftdiamond.data.PluginFile;
import me.micrjonas.grandtheftdiamond.messenger.Messenger;
import me.micrjonas.grandtheftdiamond.messenger.NoPermissionType;
import me.micrjonas.grandtheftdiamond.util.Enums;
import me.micrjonas.grandtheftdiamond.util.Nameables;

import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;

public class CommandReload implements CommandExecutor, TabCompleter {

	private String rightUsageFiles = "";
	
	public CommandReload() {
		
		rightUsageFiles = "<all|plugin|" + Nameables.nameablesAsString("|", PluginFile.values()).toLowerCase().replace('_', '-') + ">";
	
	}
	
	@Override
	public void onCommand(CommandSender sender, String alias, String[] args, String[] originalArgs) {
		
		if (GrandTheftDiamond.checkPermission(sender, "reload", true, NoPermissionType.COMMAND)) {
			
			if (args.length >= 2) {
				
				try {
					
					FileManager.getInstance().reloadFile(Enums.valueOf(PluginFile.class, args[1]));
					Messenger.getInstance().sendPluginMessage(sender, "reloaded.file", "%file%", args[1]);
					
					return;
					
				}
				
				catch (IllegalArgumentException ex) { }
				
				if (args[1].equalsIgnoreCase("all")) {
					
					FileManager.getInstance().reloadAllFiles();
					Messenger.getInstance().sendPluginMessage(sender, "reloaded.all");
					
				}
				
				else if(args[1].equalsIgnoreCase("plugin")) {
				
					Bukkit.getPluginManager().disablePlugin(Bukkit.getPluginManager().getPlugin(BukkitGrandTheftDiamondPlugin.getInstance().getDescription().getName()));
					Bukkit.getPluginManager().enablePlugin(Bukkit.getPluginManager().getPlugin(BukkitGrandTheftDiamondPlugin.getInstance().getDescription().getName()));
				
					Messenger.getInstance().sendPluginMessage(sender, "pluginReloaded");
					
				}
				
				else {
					
					Messenger.getInstance().sendPluginMessage(sender, "wrongUsage");
					Messenger.getInstance().sendRightUsage(sender, alias, "reload " + rightUsageFiles);	
					
				}
				
			}
			
			else {
				
				Messenger.getInstance().sendPluginMessage(sender, "wrongUsage");
				Messenger.getInstance().sendRightUsage(sender, alias, "reload " + rightUsageFiles);	
				
			}
			
		}

	}

	@Override
	public Collection<String> onTabComplete(CommandSender sender, String[] args) {
		if (args.length == 2) {
			List<String> files = new ArrayList<>(FileManager.getInstance().getAllFileNames());
			files.add(0, "plugin");
			files.add(0, "all");
			return files;
		}
		return null;
	}

}

package me.micrjonas.grandtheftdiamond.command;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import me.micrjonas.grandtheftdiamond.GrandTheftDiamond;
import me.micrjonas.grandtheftdiamond.data.FileManager;
import me.micrjonas.grandtheftdiamond.data.PluginFile;
import me.micrjonas.grandtheftdiamond.messenger.LanguageManager;
import me.micrjonas.grandtheftdiamond.messenger.Messenger;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;

public class CommandHelp implements CommandExecutor, TabCompleter {

	private String helpFormat = Messenger.getInstance().getFormat("help");
	private int maxLines = FileManager.getInstance().getFileConfiguration(PluginFile.CONFIG).getInt("maxLinesPerPage");
	
	private List<Help> help = new ArrayList<>();
	private Map<String, Help> helpByArgument = new HashMap<>();
	
	public CommandHelp() {
		FileConfiguration messageFile = LanguageManager.getInstance().getDefaultLanguageFile();
		String noDescription = messageFile.getString("Help.noCommandDescriptionAvailable");
		for (String command : GrandTheftDiamond.getRegisteredCommands()) {
			List<Help> argumentHelp = null;
			if (messageFile.isConfigurationSection("Help." + command)) {
				argumentHelp = new ArrayList<>();
				for (String arg : messageFile.getConfigurationSection("Help." + command).getKeys(false)) {
					if (!arg.equals("description")) {
						argumentHelp.add(new Help(command + " " + arg, 
								messageFile.getString("Help." + command + "." + arg)));
					}
				}
			}
			Help help = new Help(command, 
					messageFile.isString("Help." + command + ".description") ? 
							messageFile.getString("Help." + command + ".description") : noDescription, argumentHelp);
			CommandHelp.this.help.add(help);
			helpByArgument.put(command, help);
		}
	}
	
	
	@Override
	public void onCommand(CommandSender sender, String alias, String[] args, String[] originalArgs) {
		if (alias != null) {
			alias = alias + " ";
		}
		else {
			alias = "";
		}
		boolean pageIsNumber = true;
		int page = 1;
		if (args.length > 1) {
			try {
				int newPage = Integer.parseInt(args[1]);
				if (newPage >= 1) {
					page = newPage;
				}
			}
					
			catch (NumberFormatException e) {
				
				pageIsNumber = false;
				
			}
			
		}
		
		
		if (!pageIsNumber) {
			
			if (args.length > 2) {
				
				try {
					
					int newPage = Integer.parseInt(args[2]);
					
					if (newPage >= 1)
						page = newPage;
					
				}
				
				catch (NumberFormatException ex) {
					
					Messenger.getInstance().sendPluginMessage(sender, "pageNotFound", new String[]{"%page%"}, new String[]{args[1] + "/" + args[2]});
					return;
					
				}
				
			}
			
			Help help = helpByArgument.get(args[1]);
			
			if (help != null) {
				
				sender.sendMessage("" + page);
				
				if (page * (maxLines - 1) <= help.getArgumentHelp().size())
					sendHelp(sender, Messenger.getInstance().getPluginWordStartsUpperCase("help") + " - " + args[1], alias, help.getArgumentHelp(), page);
				
				else
					Messenger.getInstance().sendPluginMessage(sender, "pageNotFound", "%page%", args[1] + "/" + page);
				
			}
			
			else
				Messenger.getInstance().sendPluginMessage(sender, "pageNotFound", new String[]{"%page%"}, new String[]{args[1]});
			
		}
		
		
		else if (page * (maxLines - 1) <= help.size())
			sendHelp(sender, Messenger.getInstance().getPluginWordStartsUpperCase("help"), alias, help, page);
		
		else
			Messenger.getInstance().sendPluginMessage(sender, "pageNotFound", new String[]{"%page%"}, new String[]{String.valueOf(page)});
		
	}
	
	
	@Override
	public Collection<String> onTabComplete(CommandSender sender, String[] args) {	
		if (args.length == 2) {
			return GrandTheftDiamond.getRegisteredCommands();
		}
		return null;
	}
	
	
	private void sendHelp(CommandSender sender, String title, String alias, List<Help> help, int page) {
		
		if (help != null && help.size() > 0) {
		
			sender.sendMessage(
					Messenger.getInstance().getFormat("headerWithPages").replaceAll("%title%", title)
					.replaceAll("%currentPage%", "" + page)
					.replaceAll("%pageCount%", "" + (help.size() / maxLines + 1)));
			
			int end = page * maxLines - 1;
			
			for (int i = ((page - 1) * maxLines) - 1; i < end; i++) {
				
				if (i == -1)
					i = 0;
				
				try {
					
					sender.sendMessage(ChatColor.translateAlternateColorCodes('&', 
							helpFormat.replace("%command%", alias + help.get(i).getCommand())
							.replace("%description%", help.get(i).getDescription())));
					
				}
				
				catch (IndexOutOfBoundsException e) {
					
					break;
					
				}
				
			}
			
		}
		
		else
			Messenger.getInstance().sendMessage(sender, LanguageManager.getInstance().getLanguageFile("english").getString("Help.noCommandDescriptionAvailable"));
		
	}

	
	private class Help {
		
		private String command;
		private String description;
		private List<Help> argumentHelp;
		
		private Help(String command, String description) {
			
			this.command = command;
			this.description = description;
			
		}
		
		
		private Help(String command, String description, List<Help> argumentHelp) {
			
			this.command = command;
			this.description = description;
			
			this.argumentHelp = argumentHelp;
			
		}
		
		
		private String getCommand() {
			
			return command;
			
		}
		
		
		private String getDescription() {
			
			return description;
			
		}
		
		
		private List<Help> getArgumentHelp() {
			
			return argumentHelp;
			
		}
		
	}

}

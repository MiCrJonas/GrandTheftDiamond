package me.micrjonas.grandtheftdiamond.messenger;

import java.util.ArrayList;
import java.util.List;

import me.micrjonas.grandtheftdiamond.GrandTheftDiamond;
import me.micrjonas.grandtheftdiamond.data.FileReloadListener;
import me.micrjonas.grandtheftdiamond.data.PluginFile;
import me.micrjonas.grandtheftdiamond.manager.ChatManager;
import me.micrjonas.grandtheftdiamond.util.StringUtils;
import me.micrjonas.util.Nameable;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;

public class Messenger implements FileReloadListener {
	
	private static Messenger instance;
	
	public static Messenger getInstance() {
		
		if (instance == null)
			instance = new Messenger();
		
		return instance;
		
	}
	
	private final LanguageManager languageManager = LanguageManager.getInstance();
	private String chatPrefix;
	private int percentOfDefaultBalanceGTD;
	private int percentOfDefaultBalanceVault;
	private boolean useVaultEconomy;
	
	private Messenger() {
		
		GrandTheftDiamond.registerFileReloadListener(this);
		
	}
	
	
	@Override
	public void configurationReloaded(PluginFile file, FileConfiguration fileConfiguration) {
		
		if (file == PluginFile.CONFIG) {
		
			chatPrefix = fileConfiguration.getString("chatPrefix");
			
			if (chatPrefix == null)
				chatPrefix = "§6[GT-Diamond] §r";
				
			else
				chatPrefix = ChatColor.translateAlternateColorCodes('&',  chatPrefix);
			
			percentOfDefaultBalanceGTD = fileConfiguration.getInt("economy.percentOfDefaultBalance.GTD");
			percentOfDefaultBalanceVault = fileConfiguration.getInt("economy.percentOfDefaultBalance.Vault");
			useVaultEconomy = fileConfiguration.getBoolean("useVaultEconomy");
			
		}
		
	}
	
	
	public String getChatPrefix() {
		
		return chatPrefix;
		
	}
	
	
	public void sendMessageSection(CommandSender sender, String msgSection) {
		
		if (languageManager.getLanguageFile(sender).isConfigurationSection(msgSection)) {
		
			for (String msg : languageManager.getLanguageFile(sender).getConfigurationSection(msgSection).getKeys(false)) {
			
				if (!languageManager.getLanguageFile(sender).getString(msgSection + "." + msg).equalsIgnoreCase("null"))
					sendMessage(sender, languageManager.getLanguageFile(sender).getString(msgSection + "." + msg));
			
			}
			
		}
		
		else {
			
			sender.sendMessage("§cUnable to send plugin languageManager.getLanguageFile(sender) '" + msgSection + "'! Please report this to:");
			sender.sendMessage("http://dev.bukkit.org/bukkit-plugins/grand-theft-diamond/tickets/");
			
		}
			
	}
	
	
	public String getPluginMessage(String msg) {
		
		if (languageManager.getDefaultLanguageFile().getString("Messages." + msg) != null)
			return languageManager.getDefaultLanguageFile().getString("Messages." + msg);
		
		return null;
		
	}
	
	
	public void sendPluginMessage(CommandSender sender, String msg) {
		
		String newMsg = languageManager.getLanguageMessage(sender, "Messages." + msg);
		
		if (sender != null) {
			
			if (newMsg != null) {
				
				if (!newMsg.equalsIgnoreCase("null"))
					sendMessage(sender, newMsg);		
					
			}
		
			else {
				
				sender.sendMessage("§cThe messenger could not send the plugin's message '" + msg + "'.");
				sender.sendMessage("§cPlease tell the author of this plugin about the problem.");
				
			}
			
		}
		
	}
	
	
	public void onWrongUsage(CommandSender sender, String alias, String rightUsage) {
		
		sendPluginMessage(sender, "wrongUsage");
		sendRightUsage(sender, alias, rightUsage);
		
	}
	
	
	public void onWrongConsoleUsage(String alias, String rightUsage) {
		
		sendPluginMessage(Bukkit.getConsoleSender(), "wrongUsageAsConsole");
		sendRightUsage(Bukkit.getConsoleSender(), alias, rightUsage);
		
	}
	
	
	public void sendNoPermissionsMessage(CommandSender sender, NoPermissionType type, String permission) {
		
		sendPluginMessage(sender, type.getPath(), "%permission%", "gta." + permission);
		
	}
	
	
	public void sendPluginMessage(CommandSender sender, String msg, String toReplace, String replacement) {
		
		sendPluginMessage(sender, msg, new String[]{toReplace}, new String[]{replacement});
		
	}
	
	
	public void sendPluginMessage(CommandSender sender, String msg, String[] toReplace, String[] replacement) {
		
		String newMsg = languageManager.getLanguageMessage(sender, "Messages." + msg);
		
		if (newMsg != null) {
			
			if (sender != null && !newMsg.equalsIgnoreCase("null")) {
				
				newMsg = replaceArgs(newMsg, toReplace, replacement);
				
				sendMessage(sender, newMsg);
				
			}
			
		}
		
		else {
			
			sender.sendMessage("§cUnable to send plugin message '" + msg + "'! Please report this to:");
			sender.sendMessage("http://dev.bukkit.org/bukkit-plugins/grand-theft-diamond/tickets/");
			
		}
		
	}
	
	
	public void sendPluginMessage(CommandSender sender, String msg, String[] playersString, String[] toReplace, String[] replacement) {
		
		List<CommandSender> players = new ArrayList<>();
		
		for (String playerString : playersString) {
			
			try  {
				
				Player pToAdd = Bukkit.getServer().getPlayer(playerString);
				
				if (pToAdd != null)
					players.add(pToAdd);
				
			}
			
			catch (NullPointerException e) { }
			
		}

		sendPluginMessage(sender, msg, (CommandSender[]) players.toArray(), toReplace, replacement);
		
	}
	
	
	public void sendPluginMessage(CommandSender sender, String msg, CommandSender[] players) {
		
		String newMsg = languageManager.getLanguageMessage(sender, "Messages." + msg);
		
		if (newMsg != null) {
			
			if (sender != null && !newMsg.equalsIgnoreCase("null")) {
				
				newMsg = replacePlayers(newMsg, players);
				
				sendMessage(sender, newMsg);
				
			}
			
		}

		else {
			
			sender.sendMessage("§cUnable to send plugin message '" + msg + "'! Please report this to:");
			sender.sendMessage("http://dev.bukkit.org/bukkit-plugins/grand-theft-diamond/tickets/");
			
		}
		
	}
	
	
	public void sendPluginMessage(CommandSender sender, String msg, CommandSender[] players, String toReplace, String replacement) {
		
		sendPluginMessage(sender, msg, players, new String[]{toReplace}, new String[]{replacement});
		
	}
	
	
	public void sendPluginMessage(CommandSender sender, String msg, CommandSender[] players, String[] toReplace, String[] replacement) {
		
		String newMsg = languageManager.getLanguageMessage(sender, "Messages." + msg);

		if (newMsg != null) {
			
			if (sender != null && !newMsg.equalsIgnoreCase("null")) {
				
				newMsg = replacePlayers(newMsg, players);
				newMsg = replaceArgs(newMsg, toReplace, replacement);
				
				sendMessage(sender, newMsg);
				
			}
			
		}
		
		else {
			
			sender.sendMessage("§cUnable to send plugin message '" + msg + "'! Please report this to:");
			sender.sendMessage("§9http://dev.bukkit.org/bukkit-plugins/grand-theft-diamond/tickets/");
			
		}
		
	}
	
	
	public void sendRightUsage(CommandSender sender, String alias, String rightUsage) {
		
		if (alias != null)
			rightUsage = alias + " " + rightUsage;
		
		rightUsage = "/" + rightUsage;
		
		String rightUsageFormat = languageManager.getLanguageMessage(sender, "Formats.rightUsage");
		
		if (rightUsageFormat != null && !rightUsageFormat.equalsIgnoreCase("null"))
			sendMessage(sender, rightUsageFormat.replaceAll("%rightUsage%", rightUsage));
		
	}
	
	
	public CommandSender getSender(String name) {
		
		if (name.equalsIgnoreCase("console"))
			return Bukkit.getServer().getConsoleSender();
		
		try {
			
			return Bukkit.getServer().getPlayer(name);
			
		}
		
		catch (NullPointerException e) {
			
			return null;
			
		}
		
	}
	
	
	@SuppressWarnings("deprecation")
	public String getPlayerName(String p) {
		
		return Bukkit.getServer().getOfflinePlayer(p).getName();
		
	}
	
	
	public void sendMessage(CommandSender sender, String message) {
		
		message = ChatColor.translateAlternateColorCodes('&', message);
		String[] msgSplit = message.replace("\n", "%newLine%").split("%newLine%");
		String lastMessage = chatPrefix + msgSplit[0];
		
		sender.sendMessage(chatPrefix + msgSplit[0]);
		
		for (int i = 1; i < msgSplit.length; i++) {
			
			lastMessage = " " + ChatColor.getLastColors(lastMessage) + msgSplit[i];
			sender.sendMessage(lastMessage);
			
		}
		
	}
	
	
	public void sendMessage(CommandSender sender, String message, boolean sendPrefix) {
		
		message = ChatColor.translateAlternateColorCodes('&', message);
		
		String[] msgSplit = message.split("%newLine%");
		
		if (sendPrefix)
			sender.sendMessage(chatPrefix + msgSplit[0]);
		
		else
			sender.sendMessage(msgSplit[0]);
		
		for (int i = 1; i < msgSplit.length; i++)
			sender.sendMessage(" " + ChatColor.getLastColors(msgSplit[i-1]) + msgSplit[i]);
		
	}
	
	
	public String replacePlayers(String msg, CommandSender[] players) {
		
		if (players.length >= 1) {
			
			msg = msg.replaceAll("%player%", players[0].getName());
			
			if (players[0] instanceof Player) {
				
				msg = msg.replaceAll("%playerDisplay%", ((Player) players[0]).getDisplayName());
				
				if (ChatManager.getInstance().useVault())
					msg = msg.replaceAll("%playerChat%", ChatManager.getInstance().getChat().getPlayerPrefix(((Player) players[0])) + players[0].getName() + ChatManager.getInstance().getChat().getPlayerSuffix(((Player) players[0])));
				
				else
					msg = msg.replaceAll("%playerChat%", players[0].getName());
				
			}
			
			else
				msg = msg.replaceAll("%playerDisplay%", players[0].getName());
			
		}
		
		if (players.length >= 2) {
			
			msg = msg.replaceAll("%player%", players[1].getName());
			
			if (players[0] instanceof Player) {
				
				msg = msg.replaceAll("%playerDisplay%", ((Player) players[1]).getDisplayName());
				
				if (ChatManager.getInstance().useVault())
					msg = msg.replaceAll("%playerChat%", ChatManager.getInstance().getChat().getPlayerPrefix(((Player) players[1])) + players[1].getName() + ChatManager.getInstance().getChat().getPlayerSuffix(((Player) players[1])));
				
				else
					msg = msg.replaceAll("%playerChat%", players[1].getName());
				
			}
				
			
			else
				msg = msg.replaceAll("%playerDisplay%", players[1].getName());
			
		}
		
		return msg;
		
	}
	
	
	private String replaceArgs(String msg, String[] toReplace, String[] replacement) {
		
		for (int i = 0; i < toReplace.length; i++) {
			
			try {
				
				if (toReplace[i].equals("%time%")) {
					
					int time = 0;
					
					int days = 0;
					int hours = 0;
					int minutes = 0;
					int seconds = 0;
					
					try {
						
						time = Integer.parseInt(replacement[i]);
						
					}
					
					catch (NumberFormatException e) { }
					
					if (time > 0) {
						
						if (time >= 86400) {
							
							days = time/86400;
							
							time = time - (days * 86400);
						}
						
						if (time >= 3600) {
							
							hours = time/3600;
							
							time = time - (hours * 3600);
							
						}
						
						if (time >= 60) {
							
							minutes = time/60;
							
							time = time - (minutes * 60);
							
						}
						
						if (time >= 1)
							seconds = time;
						
					}
					
					String timeFormat = languageManager.getDefaultLanguageFile().getString("Formats.time");
					
					msg = msg.replaceAll("%time%", timeFormat.replaceAll("%days%", String.valueOf(days)).
							replaceAll("%hours%", String.valueOf(hours)).
							replaceAll("%minutes%", String.valueOf(minutes)).
							replaceAll("%seconds%", String.valueOf(seconds)));
					
				}
				
				else if (toReplace[i].equals("%amount%")) {
					
					try  {
						
						int amount = Integer.parseInt(replacement[i]);
					
						int amountGTD = (int) (((double) amount / 100) * percentOfDefaultBalanceGTD);
						int amountVault = 0;
						
						if (useVaultEconomy);
							amountVault = (int) (((double) amount / 100) * percentOfDefaultBalanceVault);
						
						msg = msg.replaceAll("%amountGTD%", String .valueOf(amountGTD)).replaceAll("%amountVault%", String.valueOf(amountVault)).replaceAll("%amount%", replacement[i]);
					
					}
					
					catch (NumberFormatException e) {
						
						msg = msg.replaceAll(toReplace[i], replacement[i]);
						
					}
					
					
						
				}
					
				else
					msg = msg.replaceAll(toReplace[i], replacement[i]);
				
			}
			
			catch (IndexOutOfBoundsException e) { }
			
		}
			
		return msg;
		
	}
	
	
	public String getPluginWord(String word) {

		if (word != null) {
		
			String newWord = word.replaceAll(" ", "_");
		
			languageManager.getDefaultLanguageFile().addDefault("SingleWords." + newWord, newWord);
			languageManager.getDefaultLanguageFile().options().copyDefaults(true);
		
			return languageManager.getDefaultLanguageFile().getString("SingleWords." + newWord);
			
		}
		
		else
			return ("ERROR");
		
	}
	
	
	public String getWordStartUpperCase(String word) {
		return word.substring(0, 1).toUpperCase() + word.substring(1, word.length()).toLowerCase();
	}
	
	
	public boolean sendMessageIfOnline(String playerName, String msg) {
		
		try {
			
			Player p = Bukkit.getServer().getPlayer(playerName);
			p.sendMessage(msg);
			return true;
			
			
		}
		
		catch (NullPointerException e) {
			
			return false;
			
		}
		
	}
	
	
	public void sendObjectList(CommandSender sender, Iterable<? extends Nameable> objects, String listName) {
		
		sendObjectList(sender, objects, listName, null);
		
	}
	
	
	public void sendObjectList(CommandSender sender, Iterable<? extends Nameable> objects, String listName, Iterable<String> additionalInformation) {
		
		String list = "";
		
		if (objects.iterator().hasNext()) {
		
			String format;
			
			if (additionalInformation == null)
				format = getFormat("listObject");
			
			else
				format = getFormat("listObjectAdditionalInformation");
			
			for (Object object : objects) {
				
				if (additionalInformation != null && additionalInformation.iterator().hasNext())
					list = list + format
							.replaceAll("%object%", getWordStartUpperCase(((Nameable) object).getName()))
							.replaceAll("%additionalInformation%", additionalInformation.iterator().next());
				
				else
					list = list + format.replaceAll("%object%", getWordStartUpperCase(((Nameable) object).getName()));
				
			}
			
		}
		
		else
			list = getPluginWord("none");
		
		sendMessage(sender, getFormat("list").replaceAll("%list%", list).replaceAll("%object%", getWordStartUpperCase(listName)), false);
		
	}
	
	
	public void sendHeader(CommandSender sender, String title) {
		
		sender.sendMessage(getFormat("header").replace("%title%", title.substring(0, 1).toUpperCase() + title.substring(1, title.length())));
		
	}
	
	
	public void sendHeader(CommandSender sender, String title, int currentPage, int pageCount) {
		
		sender.sendMessage(getFormat("headerWithPages")
				.replace("%title%", title.substring(0, 1).toUpperCase() + title.substring(1, title.length()))
				.replace("%currentPage%", String.valueOf(currentPage))
				.replace("%pageCount%", String.valueOf(pageCount)));
		
	}
	
	
	public String getFormat(String format) {
		
		return StringUtils.translateColors(LanguageManager.getInstance().getLanguageFile("english").getString("Formats." + format));
		
	}
	
	
	public String getPluginWordStartsUpperCase(String word) {

		return getPluginWord(word.substring(0, 1).toUpperCase() + word.substring(1).toLowerCase());
		
	}

}

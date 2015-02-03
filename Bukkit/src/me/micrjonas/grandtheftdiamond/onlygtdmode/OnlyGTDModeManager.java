package me.micrjonas.grandtheftdiamond.onlygtdmode;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import me.micrjonas.grandtheftdiamond.GrandTheftDiamond;
import me.micrjonas.grandtheftdiamond.Team;
import me.micrjonas.grandtheftdiamond.data.FileManager;
import me.micrjonas.grandtheftdiamond.data.PluginData;
import me.micrjonas.grandtheftdiamond.data.PluginFile;
import me.micrjonas.grandtheftdiamond.data.TemporaryPluginData;
import me.micrjonas.grandtheftdiamond.manager.EconomyManager;
import me.micrjonas.grandtheftdiamond.messenger.Messenger;

import org.bukkit.ChatColor;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;

public class OnlyGTDModeManager {
	
	private List<Integer> scheduler = new ArrayList<Integer>();
	
	public OnlyGTDModeManager() {

		if (FileManager.getInstance().getFileConfiguration(PluginFile.ONLY_GTD_MODE_CONFIG).getBoolean("use")) {
			
			new Broadcaster(this);
			new CommandListener(this);
			new EventListeners(this);
			new PlayerJoinAndQuitServer(this);
			
		}
		
	}

	
	public void unload() {
		
		for (int schedulerID : scheduler)
			GrandTheftDiamond.cancelTask(schedulerID);
		
		getConfig().set("broadcaster.currentMessages", null);
		
	}
	
	
	public FileConfiguration getConfig() {
		
		return FileManager.getInstance().getFileConfiguration(PluginFile.ONLY_GTD_MODE_CONFIG);
		
	}
	
	
	public List<Integer> getSchedulers() {
		
		return scheduler;
		
	}
	
	
	String replaceMessage(String message) {
		
		PluginData data = PluginData.getInstance();
		
		int ingameCount = TemporaryPluginData.getInstance().getIngamePlayers().size();
		int civilianCount = 0;
		int gangsterCount = 0;
		int copCount = 0;
		
		for (Player p : TemporaryPluginData.getInstance().getIngamePlayers()) {
			
			if (data.getTeam(p) == Team.CIVILIAN) {
				
				if (data.getWantedLevel(p) > 0)
					gangsterCount++;
				
				else
					civilianCount++;
				
			}
			
			else
				copCount++;
			
		}
		
		String listFormat = Messenger.getInstance().getFormat("players");
		
		if (message.contains("%ingameList%")) {
			
			String ingameList = "";
			
			for (Player p : TemporaryPluginData.getInstance().getIngamePlayers())
				ingameList = ingameList + listFormat.replaceAll("%player%", p.getName());
			
			message = message.replaceAll("%ingameList", ingameList);
			
		}
		
		
		for (Team team : Team.values()) {
			
			String teamName = team.name().toLowerCase();
			
			if (!(team == Team.CIVILIAN)) {
			
				if (message.contains("%" + teamName + "List%")) {
					
					String playerList = "";
					
					for (Player p : TemporaryPluginData.getInstance().getIngamePlayers()) {
						
						if (data.getTeam(p) == team)
							playerList = playerList + listFormat.replaceAll("%player%", p.getName());
						
					}
					
					if (playerList.equals(""))
						playerList = Messenger.getInstance().getPluginWord("none");
					
					message = message.replaceAll("%" + teamName + "List%", playerList);
					
				}
				
			}
			
			else {
				
				if (message.contains("%civilianList%")) {
					
					String playerList = "";
					
					for (Player p : TemporaryPluginData.getInstance().getIngamePlayers()) {
						
						if (data.getTeam(p) == team && data.getWantedLevel(p) == 0)
							playerList = playerList + listFormat.replaceAll("%player%", p.getName());
						
					}
					
					if (playerList.equals(""))
						playerList = Messenger.getInstance().getPluginWord("none");
					
					message = message.replaceAll("%civilianList%", playerList);
					
				}
				
				
				if (message.contains("%gangsterList%")) {
					
					String playerList = "";
					
					for (Player p : TemporaryPluginData.getInstance().getIngamePlayers()) {
						
						if (data.getTeam(p) == team && data.getWantedLevel(p) > 0)
							playerList = playerList + listFormat.replaceAll("%player%", p.getName());
						
					}
					
					if (playerList.equals(""))
						playerList = Messenger.getInstance().getPluginWord("none");
					
					message = message.replaceAll("%gangsterList%", playerList);
					
				}
				
			}
			
		}
		
		
		message = message.replaceAll("%ingameCount%", String.valueOf(ingameCount))
				.replaceAll("%civilianCount%", String.valueOf(civilianCount))
				.replaceAll("%gangsterCount%", String.valueOf(gangsterCount))
				.replaceAll("%copCount%", String.valueOf(copCount))
				.replaceAll("%date%", new Date().toString());
		
		message = ChatColor.translateAlternateColorCodes('&', message);
		
		return message;
		
	}
	
	
	String replacePlayerData(String message, Player p) {
		
		if (p != null) {
			
			message = message.replaceAll("%team%", PluginData.getInstance().getTeam(p).name().toLowerCase())
			.replaceAll("%balance%", String.valueOf(EconomyManager.getInstance().getBalance(p)));
			
			message = Messenger.getInstance().replacePlayers(message, new Player[]{p});
			
		}
		
		return message;
		
	}
	
	
	List<String> getMessages(List<String> messages) {
		
		List<String> newMessages = new ArrayList<String>();
		
		for (String newMessage : messages) {
			
			if (!newMessage.equals("")) {
			
				newMessage = replaceMessage(newMessage);
				
				String[] newMessageSplit = newMessage.split("%newLine%");
				
				for (String newMessagesSplit : newMessageSplit)
					newMessages.add(newMessagesSplit);
				
			}
			
			else
				newMessages.add("");
			
		}
		
		return newMessages;
		
	}

}

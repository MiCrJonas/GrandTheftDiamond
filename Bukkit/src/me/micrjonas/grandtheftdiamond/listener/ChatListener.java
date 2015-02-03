package me.micrjonas.grandtheftdiamond.listener;

import me.micrjonas.grandtheftdiamond.GrandTheftDiamond;
import me.micrjonas.grandtheftdiamond.Team;
import me.micrjonas.grandtheftdiamond.data.FileManager;
import me.micrjonas.grandtheftdiamond.data.FileReloadListener;
import me.micrjonas.grandtheftdiamond.data.PluginData;
import me.micrjonas.grandtheftdiamond.data.PluginFile;
import me.micrjonas.grandtheftdiamond.data.TemporaryPluginData;

import org.bukkit.ChatColor;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;

public class ChatListener implements Listener, FileReloadListener {
	
	private String msgPrefixGroup;
	private String msgPrefixLocal;
	private String msgPrefixGlobal;
	
	private String teamFormat;
	
	
	public ChatListener() {
		
		GrandTheftDiamond.registerFileReloadListener(this);
		
	}
	

	@Override
	public void configurationReloaded(PluginFile file, FileConfiguration fileConfiguration) {
		
		if (file == PluginFile.CONFIG) {
			
			msgPrefixGroup = fileConfiguration.getString("chat.team.messagePrefix");
			msgPrefixLocal = fileConfiguration.getString("chat.local.messagePrefix");
			msgPrefixGlobal = fileConfiguration.getString("chat.global.messagePrefix");
			
			teamFormat = fileConfiguration.getString("chat.team.messageFormat");
			
		}
		
	}
	
	
	@EventHandler (priority = EventPriority.HIGHEST)
	public void onChat(AsyncPlayerChatEvent e) {
		
		Player p = e.getPlayer();
		String msg = e.getMessage();
		
		if (TemporaryPluginData.getInstance().isIngame(p)) {
		
		
			if (msg.startsWith(msgPrefixGroup)) {
	
				e.setCancelled(true);

				Team team = PluginData.getInstance().getTeam(p);
				String teamMsg = team.name();
				teamMsg = teamMsg.substring(0, 1).toUpperCase() + teamMsg.substring(1).toLowerCase();
				
				String newMsg = msg.substring(msgPrefixGroup.length());
				String finishedMsg = teamFormat.replaceAll("%group%", teamMsg).replaceAll("%player%", p.getDisplayName()).replaceAll("%message%", newMsg);
				
				for (Player onP : GrandTheftDiamond.getOnlinePlayers()) {
					
					if (TemporaryPluginData.getInstance().isIngame(onP)) {
						
						if (team == PluginData.getInstance().getTeam(onP)) {
							
							onP.sendMessage(ChatColor.translateAlternateColorCodes('&', finishedMsg));
							
						}
						
					}
					
				}
			
			}
			
			else if (msg.startsWith(msgPrefixLocal)) {
				
				e.setMessage(msg.substring(msgPrefixLocal.length()));
				e.setFormat(ChatColor.translateAlternateColorCodes('&', FileManager.getInstance().getFileConfiguration(PluginFile.CONFIG).getString("chat.local.chatPrefix") + e.getFormat()));
				
				for (Player otherP : GrandTheftDiamond.getOnlinePlayers()) {
					
					if (!TemporaryPluginData.getInstance().isIngame(otherP))
						e.getRecipients().remove(otherP);
					
				}
				
			}
			
			else if (msg.startsWith(msgPrefixGlobal)) {
				
				e.setMessage(msg.substring(msgPrefixGlobal.length()));
				
				if (FileManager.getInstance().getFileConfiguration(PluginFile.CONFIG).getBoolean("chat.local.defaultForIngamePlayers")) {
					
					if (FileManager.getInstance().getFileConfiguration(PluginFile.CONFIG).getBoolean("chat.local.ifDefault.sendGlobalChatPrefix"))
						e.setFormat(ChatColor.translateAlternateColorCodes('&', FileManager.getInstance().getFileConfiguration(PluginFile.CONFIG).getString("chat.global.chatPrefix") + e.getFormat()));
					
				}
				
				e.setMessage(msg.substring(msgPrefixGlobal.length()));	
					
			}
			
			else if (FileManager.getInstance().getFileConfiguration(PluginFile.CONFIG).getBoolean("chat.local.defaultForIngamePlayers")) {
				
				for (Player otherP : GrandTheftDiamond.getOnlinePlayers()) {
					
					if (!TemporaryPluginData.getInstance().isIngame(otherP))
						e.getRecipients().remove(otherP);
					
				}
				
				if (FileManager.getInstance().getFileConfiguration(PluginFile.CONFIG).getBoolean("chat.local.ifDefault.sendLocalChatPrefix"))
					e.setFormat(ChatColor.translateAlternateColorCodes('&', FileManager.getInstance().getFileConfiguration(PluginFile.CONFIG).getString("chat.local.chatPrefix") + e.getFormat()));
				
			}
			
		}
		
	}

}

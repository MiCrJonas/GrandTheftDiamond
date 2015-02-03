package me.micrjonas.grandtheftdiamond.manager;

import me.micrjonas.grandtheftdiamond.GrandTheftDiamond;
import me.micrjonas.grandtheftdiamond.Team;
import me.micrjonas.grandtheftdiamond.data.FileManager;
import me.micrjonas.grandtheftdiamond.data.FileReloadListener;
import me.micrjonas.grandtheftdiamond.data.PluginData;
import me.micrjonas.grandtheftdiamond.data.PluginFile;
import me.micrjonas.grandtheftdiamond.data.TemporaryPluginData;
import net.milkbowl.vault.chat.Chat;

import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.plugin.RegisteredServiceProvider;

public class ChatManager implements FileReloadListener {
	
	private static ChatManager instance = null;
	
	public static ChatManager getInstance() {
		
		if (instance == null)
			instance = new ChatManager();
		
		return instance;
		
	}
	
	
	private Chat chat = null;
	private boolean useVault = false;
	
	private PluginData data = PluginData.getInstance();
	
	private String prefixCop;
	private String prefixCivilian;
	private String prefixGangster;
	private String suffixCop;
	private String suffixCivilian;
	private String suffixGangster;
	
	public ChatManager() {
		
		GrandTheftDiamond.registerFileReloadListener(this);
		
	}
	
	
	private void setupChat() {
		
		if (FileManager.getInstance().getFileConfiguration(PluginFile.CONFIG).getBoolean("useVaultChat") && Bukkit.getPluginManager().getPlugin("Vault") != null) {
		
	        RegisteredServiceProvider<Chat> chatProvider = Bukkit.getServer().getServicesManager().getRegistration(Chat.class);
	        
	        if (chatProvider != null) {
	        	
	            chat = chatProvider.getProvider();
	            
	        }
	
	        useVault = chat != null;
	        
		}
		
    }
	

	@Override
	public void configurationReloaded(PluginFile file, FileConfiguration fileConfiguration) {
		
		if (file == PluginFile.CONFIG) {
			
			setupChat();
			
			prefixCop = fileConfiguration.getString("chat.serverChat.prefix.cop");
			prefixCivilian = fileConfiguration.getString("chat.serverChat.prefix.civilian");
			prefixGangster = fileConfiguration.getString("chat.serverChat.prefix.gangster");
			suffixCop = fileConfiguration.getString("chat.serverChat.suffix.cop");
			suffixCivilian = fileConfiguration.getString("chat.serverChat.suffix.civilian");
			suffixGangster = fileConfiguration.getString("chat.serverChat.suffix.gangster");
			
		}
		
	}
	
	
	public boolean useVault() {
		
		return useVault;
		
	}
	
	
	public Chat getChat() {
		
		return chat;
		
	}
	
	
	private void setPrefix(Player p, String prefix) {
		
		if (chat == null)
			return;
		
		if (PluginData.getInstance().getArenaWorld() != null)
			chat.setPlayerPrefix(data.getArenaWorld().getName(), p, prefix);
		
		else
			chat.setPlayerPrefix(p, prefix);

	}
	
	
	private void setSuffix(Player p, String suffix) {
		
		if (chat == null)
			return;
		
		if (PluginData.getInstance().getArenaWorld() != null)
			chat.setPlayerSuffix(data.getArenaWorld().getName(), p, suffix);
		
		else
			chat.setPlayerSuffix(p, suffix);
		
	}
	
	
	private String replaceWantedLevel(Player p, String chatFormat) {
		
		return chatFormat.replaceAll("%wantedLevel%", String.valueOf(data.getWantedLevel(p)));
		
	}
	
	
	public void reset(Player p) {
		
		if (chat == null)
			return;

		setPrefix(p, "");
		setSuffix(p, "");
		
	}
	
	
	public void updateChat(Player p) {
		
		if (useVault() && TemporaryPluginData.getInstance().isIngame(p)) {
			
			if (data.getTeam(p) == Team.CIVILIAN) {
				
				if (data.getWantedLevel(p) < 1) {
					
					setPrefix(p, replaceWantedLevel(p, prefixCivilian));
					setSuffix(p, replaceWantedLevel(p, suffixCivilian));
					
				}
				
				else {
					
					setPrefix(p, replaceWantedLevel(p, prefixGangster));
					setSuffix(p, replaceWantedLevel(p, suffixGangster));
					
				}
				
			}
			
			else if (data.getTeam(p) == Team.COP) {
				
				setPrefix(p, replaceWantedLevel(p, prefixCop));
				setSuffix(p, replaceWantedLevel(p, suffixCop));
				
			}
			
		}
		
		else
			reset(p);
		
	}

}

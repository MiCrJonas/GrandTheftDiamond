package me.micrjonas.grandtheftdiamond.onlygtdmode;

import java.util.List;
import java.util.concurrent.TimeUnit;

import me.micrjonas.grandtheftdiamond.GrandTheftDiamond;
import me.micrjonas.grandtheftdiamond.BukkitGrandTheftDiamondPlugin;
import me.micrjonas.grandtheftdiamond.data.FileManager;
import me.micrjonas.grandtheftdiamond.data.PluginFile;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.server.ServerListPingEvent;

public class EventListeners implements Listener {
	
	private OnlyGTDModeManager manager;
	
	public EventListeners(OnlyGTDModeManager manager) {
		
		this.manager = manager;
		Bukkit.getPluginManager().registerEvents(this, BukkitGrandTheftDiamondPlugin.getInstance());
		
	}
	
	
	@EventHandler (priority = EventPriority.HIGHEST)
	public void onPing(ServerListPingEvent e) {
		
		if (FileManager.getInstance().getFileConfiguration(PluginFile.ONLY_GTD_MODE_CONFIG).getBoolean("motd.multiplayerMenu.change")) {
			
			List<String> messages = FileManager.getInstance().getFileConfiguration(PluginFile.ONLY_GTD_MODE_CONFIG).getStringList("motd.multiplayerMenu.text");
			
			String message = messages.get((int) (Math.random() * messages.size()));
			
			e.setMotd(manager.replaceMessage(message).
					replaceAll("%newLine%", "\n"));
			
		}
		
	}
	
	
	@EventHandler
	public void onJoin(PlayerJoinEvent e) {
		
		final Player p = e.getPlayer();
		
		if (manager.getConfig().getBoolean("motd.chat.use")) {
			
			GrandTheftDiamond.scheduleSyncDelayedTask(new Runnable() {
				
				@Override
				public void run() {
					
					for (String message : manager.getMessages(manager.getConfig().getStringList("motd.chat.text"))) {
						
						p.sendMessage(manager.replacePlayerData(message, p));
						
					}
					
				}
				
			}, manager.getConfig().getLong("motd.chat.delayInTicks") * 20, TimeUnit.SECONDS);
			
		}
		
	}

}

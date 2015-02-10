package me.micrjonas.grandtheftdiamond.onlygtdmode;

import me.micrjonas.grandtheftdiamond.GrandTheftDiamond;
import me.micrjonas.grandtheftdiamond.BukkitGrandTheftDiamondPlugin;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

public class PlayerJoinAndQuitServer implements Listener {
	
	private OnlyGTDModeManager manager;
	
	public PlayerJoinAndQuitServer(OnlyGTDModeManager manager) {
		
		this.manager = manager;
	
		Bukkit.getPluginManager().registerEvents(this, BukkitGrandTheftDiamondPlugin.getInstance());
		
	}
	
	
	@EventHandler (priority = EventPriority.HIGHEST)
	public void onJoin(PlayerJoinEvent e) {
		
		if (manager.getConfig().getBoolean("joinMessage.disableMessage"))
			e.setJoinMessage(null);
		
		else if (manager.getConfig().getBoolean("joinMessage.change")) {
			
			String message = manager.getConfig().getString("joinMessage.message");
			message = manager.replaceMessage(message);
			message = manager.replacePlayerData(message, e.getPlayer());
			
			if (manager.getConfig().getBoolean("joinMessage.sendMessageToJoinedPlayer"))
				e.setJoinMessage(message);
			
			else {
				
				for (Player p : GrandTheftDiamond.getOnlinePlayers()) {
					
					if (p != e.getPlayer())
						p.sendMessage(message);
					
				}
				
				e.setJoinMessage(null);
				
			}
			
		}
		
	}
	
	
	@EventHandler (priority = EventPriority.HIGHEST)
	public void onQuite(PlayerQuitEvent e) {
		
		if (manager.getConfig().getBoolean("quitMessage.disableMessage"))
			e.setQuitMessage(null);
		
		else if (manager.getConfig().getBoolean("quitMessage.change")) {
			
			String message = manager.getConfig().getString("quitMessage.message");
			message = manager.replaceMessage(message);
			message = manager.replacePlayerData(message, e.getPlayer());
			
			e.setQuitMessage(message);
			
		}
		
	}

}

package me.micrjonas.grandtheftdiamond.listener.player;

import java.util.HashSet;
import java.util.Set;

import me.micrjonas.grandtheftdiamond.GrandTheftDiamond;
import me.micrjonas.grandtheftdiamond.data.FileReloadListener;
import me.micrjonas.grandtheftdiamond.data.PluginData;
import me.micrjonas.grandtheftdiamond.data.PluginFile;
import me.micrjonas.grandtheftdiamond.data.TemporaryPluginData;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerKickEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.player.PlayerTeleportEvent;

public class PlayerTeleportListener implements Listener, FileReloadListener {

	private Set<Player> ignoredPlayers = new HashSet<>();
	
	private boolean disableToOutside;
	private boolean disableToInside;
	
	public PlayerTeleportListener() {
		
		GrandTheftDiamond.registerFileReloadListener(this);
		
	}
	
	
	@Override
	public void configurationReloaded(PluginFile file, FileConfiguration fileConfiguration) {
		
		if (file == PluginFile.CONFIG) {
			
			disableToOutside = fileConfiguration.getBoolean("disableTeleport.fromInsideToOutsideArena");
			disableToInside = fileConfiguration.getBoolean("disableTeleport.fromInsideToInsideArena");
			
		}
		
	}
	
	
	/**
	 * If you call this method, the next teleport event of the Player won't be cancelled by the plugin
	 * @param p The involved Player
	 */
	public void ignorePlayerOnNextTeleport(Player p) {
		
		ignoredPlayers.add(p);
		
	}
	
	
	/**
	 * Do not call
	 */
	@Deprecated
	@EventHandler (priority = EventPriority.HIGH)
	public void onTeleport(PlayerTeleportEvent e) {
		
		Player p = e.getPlayer();
		
		if (!ignoredPlayers.remove(p)) {
		
			if (TemporaryPluginData.getInstance().isIngame(p)) {
				
				if (!PluginData.getInstance().inArena(e.getTo())) {
					
					if (disableToOutside)
						e.setCancelled(true);
					
				}
				
				else if (disableToInside)
					e.setCancelled(true);
				
			}
			
		}
		
	}
	
	
	/**
	 * Do not call
	 */
	@Deprecated
	@EventHandler
	public void onLeave(PlayerQuitEvent e) {
		
		ignoredPlayers.remove(e.getPlayer());
		
	}
	
	
	/**
	 * Do not call
	 */
	@Deprecated
	@EventHandler (priority = EventPriority.HIGH, ignoreCancelled = true)
	public void onKick(PlayerKickEvent e) {
		
		ignoredPlayers.remove(e.getPlayer());
		
	}

}

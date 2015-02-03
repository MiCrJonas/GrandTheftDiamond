package me.micrjonas.grandtheftdiamond.jail;

import me.micrjonas.grandtheftdiamond.api.event.player.PlayerJoinGameEvent;
import me.micrjonas.grandtheftdiamond.api.event.player.PlayerLeaveGameEvent;

import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;

class JailListener implements Listener {
	
	private final JailManager manager;
	
	JailListener(JailManager manager) {
		this.manager = manager;
	}
	
	@EventHandler (priority = EventPriority.MONITOR, ignoreCancelled = true)
	public void onJoin(final PlayerJoinGameEvent e) {
		manager.onJoin(e);
	}
	
	@EventHandler (priority = EventPriority.MONITOR, ignoreCancelled = true)
	public void onLeave(PlayerLeaveGameEvent e) {
		manager.onLeave(e);
	}

}

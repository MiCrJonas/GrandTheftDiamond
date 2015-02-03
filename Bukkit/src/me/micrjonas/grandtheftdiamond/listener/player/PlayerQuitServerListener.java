package me.micrjonas.grandtheftdiamond.listener.player;

import me.micrjonas.grandtheftdiamond.GameManager;
import me.micrjonas.grandtheftdiamond.api.event.cause.LeaveReason;
import me.micrjonas.grandtheftdiamond.data.FileManager;
import me.micrjonas.grandtheftdiamond.data.TemporaryPluginData;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;

public class PlayerQuitServerListener implements Listener{
	
	@EventHandler (priority = EventPriority.MONITOR)
	public void onPlayerQuit(PlayerQuitEvent e) {
		Player p = e.getPlayer();
		if (TemporaryPluginData.getInstance().isIngame(p)) {
			GameManager.getInstance().leaveGame(p, LeaveReason.QUIT_SERVER);
		}
		FileManager.getInstance().unloadPlayerData(p);
	}
	
}

package me.micrjonas.grandtheftdiamond.listener;

import me.micrjonas.grandtheftdiamond.item.pluginitem.ItemManager;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

/**
 * Not used by the published plugin
 */
public class TestListener implements Listener {

	@EventHandler
	public void onJoin(PlayerJoinEvent e) {
		ItemManager.getInstance().getItem("handcuffs").giveToPlayer(e.getPlayer(), 5);
	}
	
}

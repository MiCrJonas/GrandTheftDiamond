package me.micrjonas.grandtheftdiamond.listener.player;

import me.micrjonas.grandtheftdiamond.inventory.menu.InventoryManager;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;

public class PlayerInventoryListener implements Listener {
	
	@SuppressWarnings("deprecation")
	@EventHandler
	public void inventoryClicked(InventoryClickEvent e) {
		InventoryManager.getInstance().inventoryClicked(e);
	}

}

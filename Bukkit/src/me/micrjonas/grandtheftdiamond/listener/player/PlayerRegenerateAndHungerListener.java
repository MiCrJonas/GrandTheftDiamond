package me.micrjonas.grandtheftdiamond.listener.player;

import me.micrjonas.grandtheftdiamond.data.FileManager;
import me.micrjonas.grandtheftdiamond.data.PluginFile;
import me.micrjonas.grandtheftdiamond.data.TemporaryPluginData;

import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityRegainHealthEvent;
import org.bukkit.event.entity.EntityRegainHealthEvent.RegainReason;
import org.bukkit.event.entity.FoodLevelChangeEvent;

public class PlayerRegenerateAndHungerListener implements Listener {
	
	@EventHandler
	public void onAutoHeal(EntityRegainHealthEvent e) {
		
		Entity ent =  e.getEntity();
		
		if (ent instanceof Player) {
			
			Player p = (Player) e.getEntity();
		
			if (TemporaryPluginData.getInstance().isIngame(p) && FileManager.getInstance().getFileConfiguration(PluginFile.CONFIG).getBoolean("disableAutoheal") && e.getRegainReason() == RegainReason.SATIATED)
				e.setCancelled(true);
			
		}
		
	}
	
	
	@EventHandler
	public void onFoodLevelChange(FoodLevelChangeEvent e) {
		
		Entity ent = e.getEntity();
		
		if (ent instanceof Player) {
			
			Player p = (Player) ent;
			
			if (FileManager.getInstance().getFileConfiguration(PluginFile.CONFIG).getBoolean("disableHunger") && TemporaryPluginData.getInstance().isIngame(p))
				e.setFoodLevel(20);
			
		}
		
	}
	
}

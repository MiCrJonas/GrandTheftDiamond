package me.micrjonas.grandtheftdiamond.listener;

import me.micrjonas.grandtheftdiamond.data.PluginData;
import me.micrjonas.grandtheftdiamond.jail.JailManager;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityDeathEvent;

public class EntityDamageListener implements Listener {
	
	@EventHandler
	public void onEntityDeath(EntityDeathEvent e) {
		if (PluginData.getInstance().inArena(e.getEntity().getLocation()))
			e.getDrops().clear();
	}
	
	@EventHandler
	public void onDamage(EntityDamageEvent e) {
		if (e.getEntity() instanceof Player && JailManager.getInstance().isJailed((Player) e.getEntity()))
			e.setCancelled(true);
	}

}

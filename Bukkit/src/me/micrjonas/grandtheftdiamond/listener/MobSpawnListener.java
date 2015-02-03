package me.micrjonas.grandtheftdiamond.listener;

import me.micrjonas.grandtheftdiamond.data.FileManager;
import me.micrjonas.grandtheftdiamond.data.PluginData;
import me.micrjonas.grandtheftdiamond.data.PluginFile;

import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.CreatureSpawnEvent;

public class MobSpawnListener implements Listener {
	
	@EventHandler (priority = EventPriority.HIGH)
	public void onMobSpawn(CreatureSpawnEvent e) {
		
		if (FileManager.getInstance().getFileConfiguration(PluginFile.CONFIG).getBoolean("disableMobspawningInArena") && PluginData.getInstance().inArena(e.getEntity().getLocation())) {
			
			e.setCancelled(true);
			
		}
		
	}
	
}

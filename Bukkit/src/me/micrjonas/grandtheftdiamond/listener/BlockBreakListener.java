package me.micrjonas.grandtheftdiamond.listener;

import me.micrjonas.grandtheftdiamond.GrandTheftDiamond;
import me.micrjonas.grandtheftdiamond.data.FileReloadListener;
import me.micrjonas.grandtheftdiamond.data.PluginFile;
import me.micrjonas.grandtheftdiamond.messenger.Messenger;
import me.micrjonas.grandtheftdiamond.rob.RobManager;
import me.micrjonas.grandtheftdiamond.util.bukkit.Materials;

import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;

public class BlockBreakListener implements Listener, FileReloadListener {

	private Material safeType;
	
	public BlockBreakListener() {
		
		GrandTheftDiamond.registerFileReloadListener(this);
		
	}

	@Override
	public void configurationReloaded(PluginFile file, FileConfiguration fileConfiguration) {
		
		if (file == PluginFile.EVENT_CONFIG)
			safeType = Materials.getMaterialFromConfig(fileConfiguration, "robbing.safe.block");
		
	}
	
	
	@EventHandler
	public void onBlockBreak(BlockBreakEvent e) {
		
		if (e.getBlock().getType() == safeType) {
			
			if (RobManager.getInstance().isSafe(e.getBlock())) {
					
				if (GrandTheftDiamond.checkPermission(e.getPlayer(), "setup.safe")) {
					
					Messenger.getInstance().sendPluginMessage(e.getPlayer(), "safeRemoved");
					RobManager.getInstance().removeSafe(e.getBlock());
					
				}
				
				else {
					
					e.setCancelled(true);
					Messenger.getInstance().sendPluginMessage(e.getPlayer(), "noPermissonsBreak");
					
				}
				
			}
			
		}
		
	}

}

package me.micrjonas.grandtheftdiamond.listener;

import me.micrjonas.grandtheftdiamond.GrandTheftDiamond;
import me.micrjonas.grandtheftdiamond.data.TemporaryPluginData;
import me.micrjonas.grandtheftdiamond.messenger.Messenger;

import org.bukkit.entity.Player;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;

public class SetPositionsWithItem {
	
	public void setPositions(PlayerInteractEvent e) {

		Player p = e.getPlayer();
		
		if (e.getAction() == Action.LEFT_CLICK_BLOCK) {
			
			if (GrandTheftDiamond.checkPermission(p, "create")) {
					
				e.setCancelled(true);
					
				TemporaryPluginData.getInstance().setCreatePosition(p, 1, e.getClickedBlock().getLocation());
				Messenger.getInstance().sendPluginMessage(p, "posCreated", new String[]{"%position%"}, new String[]{"1"});
				
			}
			
		}
		
		if (e.getAction() == Action.RIGHT_CLICK_BLOCK) {
			
			if (GrandTheftDiamond.checkPermission(p, "create")) {
					
				e.setCancelled(true);
				
				TemporaryPluginData.getInstance().setCreatePosition(p, 2, e.getClickedBlock().getLocation());
				Messenger.getInstance().sendPluginMessage(p, "posCreated", new String[]{"%position%"}, new String[]{"2"});
				
			}
			
		}
		
	}

}

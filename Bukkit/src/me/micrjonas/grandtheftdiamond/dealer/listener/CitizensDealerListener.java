package me.micrjonas.grandtheftdiamond.dealer.listener;

import me.micrjonas.grandtheftdiamond.GrandTheftDiamondPlugin;
import me.micrjonas.grandtheftdiamond.dealer.Dealer;
import me.micrjonas.grandtheftdiamond.dealer.DealerManager;
import net.citizensnpcs.api.event.NPCDespawnEvent;
import net.citizensnpcs.api.event.NPCRightClickEvent;

import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;

public class CitizensDealerListener implements Listener {
	
	public CitizensDealerListener() {
		
		Bukkit.getPluginManager().registerEvents(this, GrandTheftDiamondPlugin.getInstance());
		
	}

	
	@EventHandler
	public void npcClicked(NPCRightClickEvent e) {
		
		Dealer dealer = DealerManager.getInstance().getDealerById(e.getNPC().getUniqueId());
		
		if (dealer != null)
			dealer.playerClickedDealer(e.getClicker());
		
	}
	
	
	@EventHandler (priority = EventPriority.MONITOR, ignoreCancelled = true)
	public void onDespawn(NPCDespawnEvent e) {
		
		Dealer dealer = DealerManager.getInstance().getDealerById(e.getNPC().getUniqueId());
		
		if (dealer != null)
			DealerManager.getInstance().removeDealer(dealer);
		
	}
	
}

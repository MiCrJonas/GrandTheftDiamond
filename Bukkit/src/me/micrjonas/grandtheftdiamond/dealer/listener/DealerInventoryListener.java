package me.micrjonas.grandtheftdiamond.dealer.listener;

import java.util.ArrayList;
import java.util.List;

import me.micrjonas.grandtheftdiamond.BukkitGrandTheftDiamondPlugin;
import me.micrjonas.grandtheftdiamond.dealer.Dealer;
import me.micrjonas.grandtheftdiamond.dealer.DealerManager;
import me.micrjonas.grandtheftdiamond.inventory.merchant.Offer;

import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.Inventory;

public class DealerInventoryListener implements Listener {
	
	public DealerInventoryListener() {
		
		Bukkit.getPluginManager().registerEvents(this, BukkitGrandTheftDiamondPlugin.getInstance());
		
	}
	
	
	@EventHandler (priority = EventPriority.MONITOR, ignoreCancelled = true)
	public void inventoryClosed(InventoryCloseEvent e) {
		
		Inventory inv = e.getInventory();
		Dealer dealer = DealerManager.getInstance().getDealerByEditPane(inv);
		
		if (dealer != null) {
		
			List<Offer> offers = new ArrayList<>();
			
			for (int i = 0; i < 9; i++) {
				
				int slot1 = i + 9;
				int slot2 = i + 18;
				 
				if (inv.getItem(i) != null && inv.getItem(slot2) != null)
					offers.add(new Offer(inv.getItem(i), inv.getItem(slot1), inv.getItem(slot2)));
				
			}
			
			dealer.setOffers(offers);

		}
		
	}

}

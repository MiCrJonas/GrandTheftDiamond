package me.micrjonas.grandtheftdiamond.dealer;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

import me.micrjonas.grandtheftdiamond.GrandTheftDiamond;
import me.micrjonas.grandtheftdiamond.data.FileManager;
import me.micrjonas.grandtheftdiamond.data.PluginFile;
import me.micrjonas.grandtheftdiamond.data.player.PlayerDataUser;
import me.micrjonas.grandtheftdiamond.data.storage.StorableManager;
import me.micrjonas.grandtheftdiamond.inventory.merchant.Offer;
import me.micrjonas.grandtheftdiamond.item.ItemManager;
import me.micrjonas.grandtheftdiamond.util.bukkit.Locations;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

public class DealerManager implements StorableManager<Dealer>, PlayerDataUser {
	
	//private static DealerManager instance = new DealerManager();
	
	public static DealerManager getInstance() {
		
		throw new UnsupportedOperationException("dealers are not implemented yet");
		
		//return instance;
		
	}
	
	private DealerService service;
	private final Map<UUID, Dealer> dealers = new HashMap<>();
	private final Map<Inventory, Dealer> editPanes = new HashMap<>();
	private final Set<UUID> removingPlayers = new HashSet<>();
	
	private DealerManager() {
		
		GrandTheftDiamond.registerStorableManager(this, PluginFile.DEALERS);
		GrandTheftDiamond.registerPlayerDataUser(this);

		findService();
		
	}
	
	
	@Override
	public Collection<Dealer> getAllObjects() {
		
		return Collections.unmodifiableCollection(dealers.values());
		
	}


	@Override
	public void loadObjects(FileConfiguration dataFile) {
		
		if (apiAvailable()) {
			
			for (String idString : dataFile.getConfigurationSection("").getKeys(false)) {
				
				UUID id;
				
				try {
					id = UUID.fromString(idString);
				}
				
				catch (IllegalArgumentException ex) {
					dataFile.set(idString, null);
					return;
				}
				
				Location loc = Locations.getLocationFromFile(dataFile, idString + ".location", false);
				String name = dataFile.getString(idString + ".name");
				
				List<Offer> offers = null;
				
				if (dataFile.isConfigurationSection(idString + ".offers")) {
					
					offers = new ArrayList<>();
					
					for (String offerId : dataFile.getConfigurationSection(idString + ".offers").getKeys(false)) {
						
						String price0Path = idString + ".offers." + offerId + ".price0";
						String price1Path = idString + ".offers." + offerId + ".price1";
						String resultPath = idString + ".offers." + offerId + ".result";
						
						ItemStack price0 = ItemManager.loadItemFromFile(PluginFile.DEALERS, price0Path, true);
						ItemStack price1 = ItemManager.loadItemFromFile(PluginFile.DEALERS, price1Path, true);
						ItemStack result = ItemManager.loadItemFromFile(PluginFile.DEALERS, resultPath, true);
						
						if (!(price0 == null || result == null))
							offers.add(new Offer(price0, price1, result));
						
					}
					
				}
				
				if (!(loc == null || name == null))
					createDealer(loc, name, id, offers, true);
				
				else
					dataFile.set(idString, null);
				
			}
			
		}
		
	}


	@Override
	public void saveObjects(FileConfiguration dataFile) {
		FileManager.clearFile(dataFile);
		for (Dealer dealer : dealers.values())
			FileManager.getInstance().store(dataFile, dealer, dealer.getUniqueId().toString());
		
	}
	
	
	@Override
	public void clearPlayerData(UUID player) {
		
		removingPlayers.remove(player);
		
	}


	@Override
	public void loadPlayerData(UUID player) { /* Nothing to load */ }
	
	
	private void findService() {
		
		if (Bukkit.getPluginManager().getPlugin("Citizens") != null)
			service = DealerService.CITIZENS;
		
		else
			GrandTheftDiamond.getLogger().info("Could not find any API for dealers, disabling dealer part");
		
	}
	
	
	public boolean apiAvailable() {
		
		return service != null;
		
	}
	
	
	public Dealer createDealer(Location loc, String name) {
		
		return createDealer(loc, name, UUID.randomUUID(), null, false);
		
	}
	
	
	private Dealer createDealer(Location loc, String name, UUID id, Collection<Offer> offer, boolean onStartUp) {
		
		if (!onStartUp) {
			
			if (service == null)
				throw new IllegalStateException("no dealer API available; Dealers not usable");
			
			if (loc == null)
				throw new IllegalArgumentException("loc is not allowed to be null");
			
			if (loc.getWorld() == null)
				throw new IllegalArgumentException("World of loc is not allowed to be null");
			
			if (name == null)
				throw new IllegalArgumentException("name is not allowed to be null");
			
			if (name.length() == 0)
				throw new IllegalArgumentException("length of name cannot be 0");	
			
		}
		
		Dealer dealer = null;
		
		switch (service) {
		
			case CITIZENS: {
				
				dealer = new CitizensDealer(id, loc, name, offer);
				
			} break;
				
			case SHOPKEEPERS:
				break;
		
		}
		
		if (dealer != null) {
			
			dealers.put(id, dealer);
			editPanes.put(dealer.getEditPane(), dealer);
			
		}
		
		return dealer;
		
	}
	
	
	public Dealer getDealerByEditPane(Inventory editPane) {
		
		return editPanes.get(editPane);
		
	}
	
	public Dealer getDealerById(UUID id) {
		
		return dealers.get(id);
		
	}
	
	
	public void removeDealer(Dealer dealer) {
		
		dealer.destroy();
		editPanes.remove(dealer.getEditPane());
		dealers.remove(dealer);
		
	}
	
	
	public void removeAllDealers() {
		
		for (Dealer dealer : dealers.values())
			removeDealer(dealer);

	}
	
	
	public void setIsRemovingDealer(Player p, boolean removing) {
		
		if (removing)
			removingPlayers.add(p.getUniqueId());
		
		else
			removingPlayers.remove(p.getUniqueId());
		
	}
	
	
	public boolean isRemovingDealer(Player p) {
		
		return removingPlayers.contains(p.getUniqueId());
		
	}

}

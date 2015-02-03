package me.micrjonas.grandtheftdiamond.dealer;

import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import me.micrjonas.grandtheftdiamond.GrandTheftDiamond;
import me.micrjonas.grandtheftdiamond.data.storage.Storable;
import me.micrjonas.grandtheftdiamond.inventory.merchant.Merchant;
import me.micrjonas.grandtheftdiamond.inventory.merchant.Offer;
import me.micrjonas.grandtheftdiamond.item.ItemManager;
import me.micrjonas.grandtheftdiamond.messenger.Messenger;
import me.micrjonas.grandtheftdiamond.messenger.NoPermissionType;
import me.micrjonas.grandtheftdiamond.util.Nameable;
import me.micrjonas.grandtheftdiamond.util.bukkit.Locations;
import me.micrjonas.grandtheftdiamond.util.bukkit.WorldStorage;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;

public abstract class Dealer implements Nameable, Storable {
	
	private final UUID id;
	private Location loc;
	private String name;
	private Merchant merchant = new Merchant();
	private final Map<UUID, Offer> offers = new LinkedHashMap<>();
	private final Inventory editPane;
	
	protected Dealer(UUID id, Location loc, String name, Collection<Offer> offers) {
		
		this.id = id;
		this.loc = loc;
		this.name = name;
		merchant.setTitle(name);
		editPane = Bukkit.createInventory(null, 3 * 9, getName() + " - " 
				+ Messenger.getInstance().getPluginWordStartsUpperCase("editor"));
		
		setOffers(offers);
		spawn(loc);
		
	}
	
	
	@Override
	public String getName() {
		
		return name;
		
	}
	
	
	@Override
	public Map<String, Object> getStoreData() {
		
		Map<String, Object> data = new LinkedHashMap<>();
		
		data.put("name", name);
		data.put("location", Locations.toMap(loc, WorldStorage.NAME, false));
		
		List<Offer> offers = new ArrayList<>(this.offers.values());
		
		for (int i = 0; i < offers.size(); i++) {
			
			data.put("offers." + i + ".price0", ItemManager.toMap(offers.get(i).getPrice0()));
			
			if (offers.get(i).getPrice1() != null)
				data.put("offers." + i + ".price1", ItemManager.toMap(offers.get(i).getPrice1()));
			
			data.put("offers." + i + ".result", ItemManager.toMap(offers.get(i).getResult()));
			
		}
		
		return data;
		
	}
	
	
	/**
	 * Method, sub classes need to implement to teleport the dealer
	 * @param loc The Location, the Dealer should get teleported to
	 */
	protected abstract void updateLocation(Location loc);
	
	
	/**
	 * Spawns the dealer
	 */
	protected abstract void spawn(Location loc);
	
	
	/**
	 * Despawns and removes the Dealer
	 */
	protected abstract void destroy();
	
	
	Inventory getEditPane() {
		
		return editPane;
		
	}
	
	
	/**
	 * Returns the unique id of the dealer
	 * @return The UUID of the player
	 */
	public UUID getUniqueId() {
		
		return id;
		
	}
	
	
	/**
	 * Teleports the dealer to the given Location
	 * @param loc The Location, the Dealer should get teleported to
	 */
	public void teleport(Location loc) {
		
		if (loc == null)
			throw new IllegalArgumentException("loc is not allowed to be null");
		
		if (loc.getWorld() == null)
			throw new IllegalArgumentException("World of loc is not allowed to be null");
		
		this.loc = loc;
		
		updateLocation(loc);
		
	}
	
	
	/**
	 * Returns the location of the Dealer
	 * @return The Location of the Dealer
	 */
	public Location getLocation() {
		
		return loc;
		
	}
	
	
	/**
	 * Returns the recipe by the given id<br>
	 * @param id The id of the recipe
	 * @return The recipe by the given id
	 */
	public Offer getOffer(UUID id) {
		
		return offers.get(id);
		
	}
	
	
	public void setOffers(Collection<Offer> offers) {
		
		this.offers.clear();
		
		if (offers != null) {
			
			for (Offer offer : offers) {
				
				if (offer != null)
					this.offers.put(offer.getUniqueId(), offer);
					
			}
			
		}
		
		List<Offer> filteredOffers = new ArrayList<>(this.offers.values());
		
		for (int i = 0; i < 9; i++) {
			
			int slot1 = i + 9;
			int slot2 = i + 18;
			
			if (i < filteredOffers.size()) {
			
				editPane.setItem(i, filteredOffers.get(i).getPrice0());
				editPane.setItem(slot1, filteredOffers.get(i).getPrice1());
				editPane.setItem(slot2, filteredOffers.get(i).getResult());
				
				merchant.addOffer(filteredOffers.get(i));
				
			}
			
			else {
				
				editPane.setItem(i, null);
				editPane.setItem(slot1, null);
				editPane.setItem(slot2, null);	
				
			}
			
		}
		
	}
	
	
	/**
	 * Adds a new offer to the Dealer
	 * @param offer The new Offer
	 */
	public void addOffer(Offer offer) {
		
		offers.put(offer.getUniqueId(), offer);
		
	}
	
	
	/**
	 * Removed the recipe by the given id
	 * @param id The id of the recipe
	 */
	public void removeOffer(UUID id) {
		
		offers.remove(id);
		
	}
	
	
	/**
	 * Call this, when a player clicked a dealer<br>
	 * Do not try to open the trade inventory or check permissions for the dealer
	 * @param p The player which should see the inventory
	 */
	public void playerClickedDealer(Player p) {
		
		if (p.isSneaking()) {
			
			if (GrandTheftDiamond.checkPermission(p, "dealer.edittrades", true, NoPermissionType.EDIT))
				p.openInventory(editPane);
				
			
		}
		
		else if (GrandTheftDiamond.checkPermission(p, "dealer.trade", true, NoPermissionType.OPEN))
			merchant.openOfferInventory(p);
		
	}

}

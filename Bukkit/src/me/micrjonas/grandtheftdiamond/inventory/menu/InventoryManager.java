package me.micrjonas.grandtheftdiamond.inventory.menu;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;

/**
 * Manages the clickable {@link Inventory}'s
 */
public class InventoryManager {
	
//Start of static
	private static InventoryManager instance = new InventoryManager();
	
	/**
	 * Returns the loaded instance
	 * @return The loaded instance
	 */
	public static InventoryManager getInstance() {
		return instance;
	}
//End of static
	
	private Map<Inventory, PopUpInventory> inventories = new HashMap<>();
	
	private InventoryManager() { }
	
	/**
	 * @deprecated Do not call
	 */
	@Deprecated
	public void inventoryClicked(InventoryClickEvent e) {
		PopUpInventory inv = inventories.get(e.getInventory());
		if (inv != null) {
			e.setCancelled(true);
			if (e.getRawSlot() < inv.getInventory().getSize())
			inv.itemClicked((Player) e.getWhoClicked(), inv, e.getSlot());
		}
	}
	
	public PopUpInventory createPopUpInventory(String name, int lines) throws IllegalArgumentException {
		return createPopUpInventory(name, lines, null);
	}
	
	public PopUpInventory createPopUpInventory(String name, int lines, Collection<Entry<Integer, PopUpItem>> items) throws IllegalArgumentException {
		if (lines < 1)
			throw new IllegalArgumentException("lines cannot be < 1");
		
		if (lines > 6)
			throw new IllegalArgumentException("lines cannot be > 6");
		
		PopUpInventory inv = new PopUpInventory(lines, name);
		if (items != null) {
			for (Entry<Integer, PopUpItem> item : items) {
				if (item != null && inv.validSlot(item.getKey()) && item.getValue() != null)
					inv.set(item.getKey(), item.getValue());
			}	
		}
		inventories.put(inv.getInventory(), inv);
		
		return inv;
	}

}

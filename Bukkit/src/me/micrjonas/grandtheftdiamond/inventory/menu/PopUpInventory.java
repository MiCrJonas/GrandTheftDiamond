package me.micrjonas.grandtheftdiamond.inventory.menu;

import java.util.HashMap;
import java.util.Map;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

public class PopUpInventory {
	
	private Inventory inv;
	private Map<Integer, PopUpItem> items = new HashMap<>();
	
	public PopUpInventory(int rows, String name) {
		inv = Bukkit.createInventory(null, rows * 9, name);
	}
	
	
	void itemClicked(Player p, PopUpInventory inv, int slot) {
		PopUpItem item = items.get(slot);
		if (item != null)
			item.itemClicked(inv, p);
	}
	
	
	Inventory getInventory() {
		return inv;
	}
	
	/**
	 * Opens the inventory for the Player
	 * @param p The Player who should see the inventory
	 */
	public void open(Player p) {
		p.openInventory(inv);
	}
	
	/**
	 * Closes the inventory for the Player if open
	 * @param p The Player to close the inventory
	 * @return True if the Player had the Inventory open, else false
	 */
	public boolean close(Player p) {
		if (p.getOpenInventory() == inv) {
			p.closeInventory();
			return true;
		}
		return false;
	}
	
	/**
	 * Check whether the slot is a valid slot
	 * @param slot Slot to check
	 * @return True if the slot is a valid slot to set an item, else false
	 */
	public boolean validSlot(int slot) {
		return slot >= 0 && slot < inv.getSize();
	}
	
	/**
	 * Sets a clickable item in the inventory
	 * @param x The x position. First is 0
	 * @param y The y position. First is 0
	 * @param item The item to set, allowed to be null
	 * @throws IllegalArgumentException If x or y is to low or to high
	 */
	public void set(int x, int y, PopUpItem item) throws IllegalArgumentException {
		if (x < 0 || x > 8)
			throw new IllegalArgumentException("x must be >= 0 and <= 8, but x is: " + x);
		
		int maxY = inv.getSize() / 9 - 1;
		if (y < 0 || y > maxY)
			throw new IllegalArgumentException("y must be >= 0 and <= " + maxY + ", but y is: " + y);
		
		set(9 * y + x, item);
	}
	
	/**
	 * Sets a clickable item in the inventory
	 * @param slot The slot to set the item
	 * @param item The item to set, allowed to be null
	 * @throws IllegalArgumentException If the slot is not a valid slot
	 */
	public void set(int slot, PopUpItem item) throws IllegalArgumentException {
		if (slot < 0)
			throw new IllegalArgumentException("slot cannot be < 0");
		
		if (slot >= inv.getSize())
			throw new IllegalArgumentException("slot cannot be > " + inv.getSize() + " (Inventory size)");
		
		if (item == null) {
			inv.setItem(slot, null);
			items.remove(slot);	
		}
		
		else {
			inv.setItem(slot, item.getItem());
			items.put(slot, item);	
		}
	}
	
	/**
	 * Sets a dummy item which does nothing on click
	 * @param x The x position. First is 0
	 * @param y The y position. First is 0
	 * @param item The dummy item, allowed to be null
	 * @throws IllegalArgumentException If the slot is not valid
	 */
	public void setDummy(int x, int y, ItemStack item) throws IllegalArgumentException {
		if (x < 0 || x > 8)
			throw new IllegalArgumentException("x must be >= 0 and <= 8, but x is: " + x);
		
		int maxY = inv.getSize() / 9 - 1;
		if (y < 0 || y > maxY)
			throw new IllegalArgumentException("y must be >= 0 and <= " + maxY + ", but y is: " + y);
		
		setDummy(9 * y + x, item);
	}
	
	/**
	 * Sets a dummy item to the slot
	 * @param slot The slot to set
	 * @param item The item to set, allowed to be null
	 * @throws IllegalArgumentException If the slot is not valid
	 */
	public void setDummy(int slot, ItemStack item) throws IllegalArgumentException {
		if (slot < 0)
			throw new IllegalArgumentException("slot cannot be < 0");
		
		if (slot >= inv.getSize())
			throw new IllegalArgumentException("slot cannot be > " + inv.getSize() + " (Inventory size)");
		
		if (item == null)
			items.remove(slot);	
	
		inv.setItem(slot, item);
	}

}

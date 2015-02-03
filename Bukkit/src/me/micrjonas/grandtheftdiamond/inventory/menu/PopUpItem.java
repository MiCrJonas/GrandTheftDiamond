package me.micrjonas.grandtheftdiamond.inventory.menu;

import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public abstract class PopUpItem {
	
	private ItemStack item;
	
	/**
	 * Creates a new PopUpItem
	 * @param item The involved ItemStack, gets stored as cloned object
	 * @throws IllegalArgumentException If item is {@code null}
	 */
	public PopUpItem(ItemStack item) throws IllegalArgumentException {
		if (item == null)
			throw new IllegalArgumentException("item is not allowed to be null");
		
		this.item = item.clone();
	}
	
	/**
	 * Returns a cloned object of the involved ItemStack
	 * @return A copy of the involved ItemStack
	 */
	public ItemStack getItem() {
		return item.clone();
	}
	
	/**
	 * Sets the involved ItemStack
	 * @param item The item, gets stored as cloned object
	 * @throws IllegalArgumentException If item is {@code null}
	 */
	public void setItem(ItemStack item) throws IllegalArgumentException {
		if (item == null)
			throw new IllegalArgumentException("item is not allowed to be null");
		
		this.item = item.clone();
	}
	
	/**
	 * Gets called after a InventoryHolder clicked the item
	 * @param inv The clicked Inventory
	 * @param holder The InventoryHolder who clicked
	 */
	public abstract void itemClicked(PopUpInventory inv, Player holder);

}

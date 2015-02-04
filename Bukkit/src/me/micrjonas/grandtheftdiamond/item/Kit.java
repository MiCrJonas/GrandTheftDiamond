package me.micrjonas.grandtheftdiamond.item;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import me.micrjonas.grandtheftdiamond.util.Nameable;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class Kit implements Nameable {
	
	private final String name;
	private final List<ItemStack> items;
	private int delay;
	
	/**
	 * Default constructor. Initializing with an empty item {@link List} and delay 0
	 * @param name The {@code Kit}'s name
	 * @throws IllegalArgumentException Thrown if {@code name} is {@code null}
	 */
	public Kit(String name) throws IllegalArgumentException {
		if (name == null) {
			throw new IllegalArgumentException("Name is not allowed to be null");
		}
		this.name = name;
		items = new ArrayList<>();
	}
	
	/**
	 * Initializing with an empty item {@link List} and delay 0
	 * @param name The {@code Kit}'s name
	 * @param delay The time, a {@link Player} must wait until he can receive this {@code Kit} again in seconds.
	 * 	{@link #giveToPlayer(Player)} does not check this value
	 * @param items The {@code Kit}'s items. May be {@code null}
	 * @throws IllegalArgumentException Thrown if {@code name} is {@code null}
	 */
	public Kit(String name, int delay, Collection<ItemStack> items) throws IllegalArgumentException {
		this(name);
		if (items != null) {
			for (ItemStack item : items) {
				this.items.add(item.clone());
			}	
		}
	}
	
	@Override
	public String getName() {
		return name;
	}
	
	/**
	 * Returns the time, a {@link Player} must wait until he can receive this {@code Kit} again in seconds.
	 * 	{@link #giveToPlayer(Player)} does not check this value
	 * @return The time, a {@link Player} must wait until he can receive this {@code Kit} again in seconds
	 */
	public int getDelay() {
		return delay;
	}
	
	/**
	 * Sets the time, a {@link Player} must wait until he can receive this {@code Kit} again in seconds.
	 * 	{@link #giveToPlayer(Player)} does not check this value
	 * @param delay The time, a {@link Player} must wait until he can receive this {@code Kit} again in seconds
	 */
	public void setDelay(int delay) {
		if (delay < -1) {
			throw new IllegalArgumentException("Delay is not allowed to be < -1");
		}
	}
	
	/**
	 * Returns a new {@code List} which contains all items registered in the {@code Kit}. The {@link Link} is not
	 * 	modifiable
	 * @return Returns a new {@code List} which contains all items registered in the {@code Kit}
	 * @see Collections#unmodifiableList(List)
	 */
	public List<ItemStack> getItems() {
		return Collections.unmodifiableList(items);
	}
	
	/**
	 * Adds an item to the {@code Kit}. Stores a copy of the {@link ItemStack}
	 * @param item The item to add
	 * @throws IllegalArgumentException Thrown if {@code item} is {@code null} or type of {@code item} is {@link Material#AIR}
	 */
	public void addItem(ItemStack item) throws IllegalArgumentException {
		if (item == null) {
			throw new IllegalArgumentException("Item is not allowed to be null");
		}
		if (item.getType() == Material.AIR) {
			throw new IllegalArgumentException("Item's type is not allowed to be AIR");
		}
		items.add(item.clone());
	}
	
	/**
	 * Removes all items with a specific {@link Material} from the {@code Kit}
	 * @param type The type to remove
	 */
	public void removeItemsOfType(Material type) {
		for (Iterator<ItemStack> iter = items.iterator(); iter.hasNext(); ) {
			ItemStack next = iter.next();
			if (next.getType() == type) {
				iter.remove();
			}
		}
	}
	
	/**
	 * Removes all items where {@link ItemStack#equals(Object)} returns {@code true} for {@code item}
	 * @param item The item to remove
	 * @param ignoreAmount True if the method should ignore the amount of the {@code ItemStack}.
	 * 	See {@link ItemStack#getAmount()}
	 */
	public void removeEqualItems(ItemStack item, boolean ignoreAmount) {
		if (ignoreAmount) {
			for (int i = 0; i < items.size(); i++) {
				ItemStack itemToCheck = items.get(i);
				boolean remove = true;
				if (item.hasItemMeta() != itemToCheck.hasItemMeta()) {
					remove = false;
				}
				else if (item.hasItemMeta() != itemToCheck.hasItemMeta()) {
					remove = false;
				}
				else if (item.hasItemMeta() && !item.getItemMeta().equals(itemToCheck.getItemMeta())) {
					remove = false;
				}
				if (remove) {
					items.remove(i);
					i--;
				}
			}
		}
		else {
			for (Iterator<ItemStack> iter = items.iterator(); iter.hasNext(); ) {
				ItemStack next = iter.next();
				if (next.equals(item)) {
					iter.remove();
				}
			}
		}
	}

	/**
	 * Adds the {@code Kit} to a {@link Player}'s inventory. The method ignores the value of {@link #getDelay()}
	 * @param p The {@link Player} to add
	 */
	public void giveToPlayer(Player p) {
		for (ItemStack item : items) {
			p.getInventory().addItem(item);
		}
	}
	
}

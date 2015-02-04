package me.micrjonas.grandtheftdiamond.item;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
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
	
	public int getDelay() {
		return delay;
	}
	
	public void setDelay(int delay) {
		if (delay < -1) {
			throw new IllegalArgumentException("Delay is not allowed to be < -1");
		}
	}
	
	public List<ItemStack> getItems() {
		return Collections.unmodifiableList(items);
	}
	
	public void addItem(ItemStack item) {
		if (item != null) {
			items.add(item.clone());
		}
	}
	
	public void removeItemsOfType(Material type) {
		for (int i = 0; i < items.size(); i++) {
			if (items.get(i).getType() == type) {
				items.remove(i);
				i--;
			}
		}
	}
	
	public void removeEqualItems(ItemStack item, boolean ignoreAmount) {
		if (!ignoreAmount) {
			for (int i = 0; i < items.size(); i++) {
				if (items.get(i).equals(item)) {
					items.remove(i);
					i--;
				}
			}
		}
		else {
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
	}

	public void giveToPlayer(Player p) {
		for (ItemStack item : items) {
			p.getInventory().addItem(item);
		}
	}
	
}

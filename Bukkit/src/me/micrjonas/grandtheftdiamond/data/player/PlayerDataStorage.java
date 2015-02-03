package me.micrjonas.grandtheftdiamond.data.player;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;

import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;

class PlayerDataStorage {
	
	private final Player p;
	
	private double health;
	private int foodLevel;
	private int fireTicks;
	private Collection<PotionEffect> potionEffects;
	private ItemStack[] inventory;
	private ItemStack[] armor;
	
	public PlayerDataStorage(Player p) {
		this.p = p;
	}
	
	/**
	 * Returns the related {@code Player}
	 * @return The related {@code Player}
	 */
	public Player getPlayer() {
		return p;
	}
	
	/**
	 * Returns the {@code Player}'s health
	 * @return The {@code Player}'s health
	 */
	public double getHealth() {
		return health;
	}
	
	/**
	 * Sets the {@code Player}'s health
	 * @param health The new {@code Player}'s health
	 */
	public void setHealth(double health) {
		this.health = health;
	}

	/**
	 * Returns the {@code Player}'s food level
	 * @return The {@code Player}'s food level
	 */
	public int getFoodLevel() {
		return foodLevel;
	}

	/**
	 * Sets the {@code Player}'s food level
	 * @param foodLevel The new {@code Player}'s food level
	 */
	public void setFoodLevel(int foodLevel) {
		this.foodLevel = foodLevel;
	}
	
	/**
	 * Returns the Player's fire ticks 
	 * @return The Player's fire ticks
	 */
	public int getFireTicks() {
		return fireTicks;
	}
	
	/**
	 * Sets the Player's fire ticks
	 * @param ticks The fire ticks
	 * @throws IllegalArgumentException If ticks is < 0
	 */
	public void setFireTicks(int ticks) throws IllegalArgumentException {
		if (ticks < 0)
			throw new IllegalArgumentException("ticks cannot be < 0");
		
		fireTicks = ticks;
	}

	/**
	 * Returns the Player's PotionEffects
	 * @return The Player's PotionEffects
	 */
	public Collection<PotionEffect> getPotionEffects() {
		return Collections.unmodifiableCollection(potionEffects);
	}

	/**
	 * Sets the Player's PotionEffects
	 */
	public void setPotionEffects(Collection<PotionEffect> potionEffects) {
		setPotionEffects(potionEffects, true);
	}
	

	void setPotionEffects(Collection<PotionEffect> potionEffects, boolean copy) {
		if (copy) {
			this.potionEffects = new ArrayList<>(potionEffects);
		}
		else {
			this.potionEffects = potionEffects;
		}
	}

	/**
	 * Returns the {@code Player}'s inventory
	 * @return The {@code Player}'s inventory
	 */
	public ItemStack[] getInventory() {
		return inventory;
	}

	/**
	 * Sets the {@code Player}'s inventory
	 * @param ingameInventory The new inventory
	 */
	public void setInventory(ItemStack[] ingameInventory) {
		this.inventory = ingameInventory.clone();
	}

	/**
	 * Returns the {@code Player}'s armor
	 * @return The {@code Player}'s armor
	 */
	public ItemStack[] getArmor() {
		return armor;
	}

	/**
	 * Sets the {@code Player}'s armor contents
	 * @param ingameArmor The {@code Player}'s new armor contents
	 */
	public void setArmor(ItemStack[] ingameArmor) {
		this.armor = ingameArmor.clone();
	}

}

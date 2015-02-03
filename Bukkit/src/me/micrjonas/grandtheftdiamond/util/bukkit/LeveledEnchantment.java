package me.micrjonas.grandtheftdiamond.util.bukkit;

import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemStack;

/**
 * An {@link Enchantment} which can be added to an {@link ItemStack} directly. Each object
 * 	is immutable
 */
public class LeveledEnchantment extends org.bukkit.enchantments.EnchantmentWrapper {

	private final int level;
	
	/**
	 * Creates a new {@code AddableEnchantment} with the {@code Enchantment} of {@code id} and a level
	 * @param id The id of the {@code Enchantment}
	 * @param level The level
	 */
	public LeveledEnchantment(int id, int level) {
		super(id);
		this.level = level;
	}
	
	/**
	 * Creates a new {@code AddableEnchantment} with an {@code Enchantment} and a level
	 * @param enchantment The enchantment type
	 * @param level The level
	 */
	@SuppressWarnings("deprecation")
	public LeveledEnchantment(Enchantment enchantment, int level) {
		this(enchantment.getId(), level);
	}
	
	/**
	 * Returns the level of the enchantment
	 * @return The level of the enchantment
	 */
	public int getLevel() {
		return level;
	}
	
	/**
	 * Adds the enchantment to the item.
	 * This method is unsafe and will ignore level restrictions or item type. Use at your own discretion.
	 * @param item Item which should get the enchantment
	 * @param allowUnsafeEnchantment If true, item type and level will be ignored. {@link ItemStack#addUnsafeEnchantment(Enchantment, int)}
	 * @return True if enchantment was added successfully, else false
	 */
	public boolean addToItem(ItemStack item, boolean allowUnsafeEnchantment) {
		if (allowUnsafeEnchantment) {
			item.addUnsafeEnchantment(getEnchantment(), level);
		}
		else {
			try {
				item.addEnchantment(getEnchantment(), level);
			}
			catch (IllegalArgumentException ex) {
				return false;
			}	
		}
		return true;
	}
	
}

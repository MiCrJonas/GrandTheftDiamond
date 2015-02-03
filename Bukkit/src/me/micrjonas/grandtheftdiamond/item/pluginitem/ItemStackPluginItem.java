package me.micrjonas.grandtheftdiamond.item.pluginitem;

import me.micrjonas.grandtheftdiamond.item.ItemManager;
import me.micrjonas.grandtheftdiamond.util.Immutable;

import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;


/**
 * A {@link PluginItem} which is represented by an {@link ItemStack}. The represented {@link ItemStack} is immutable
 */
public abstract class ItemStackPluginItem implements Immutable, PluginItem {
	
	private final ItemStack item;
	
	/**
	 * @param configSection The {@code ConfigurationSection} where the item should be load from
	 * @throws IllegalArgumentException Thrown if {@code configSection} is {@code null} or if the
	 * 	{@link ConfigurationSection} does not contain an {@link ItemStack} configuration
	 */
	protected ItemStackPluginItem(ConfigurationSection configSection) throws IllegalArgumentException {
		if (configSection == null) {
			throw new IllegalArgumentException("Config section to load is not allowed to be null");
		}
		item = ItemManager.getItemFromSection(configSection, false);
		if (item == null) {
			throw new IllegalArgumentException("ConfigurationSection does not represent an ItemStack");
		}
	}

	@Override
	public void giveToPlayer(Player p, int amount) {
		p.getInventory().addItem(getItem(amount));
	}
	
	/**
	 * Returns a clone of the representing item
	 * @param amount The amount of the item
	 * @return A clone of the representing item
	 */
	public ItemStack getItem(int amount) {
		ItemStack item = this.item.clone();
		item.setAmount(amount);
		return item;
	}
	
}

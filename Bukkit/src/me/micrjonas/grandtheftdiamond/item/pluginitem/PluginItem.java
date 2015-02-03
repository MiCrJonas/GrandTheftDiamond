package me.micrjonas.grandtheftdiamond.item.pluginitem;

import me.micrjonas.grandtheftdiamond.util.Nameable;

import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;



/**
 * Represents an item used by the plugin
 */
public interface PluginItem extends Nameable {
	
	/**
	 * Adds the item to the {@link Player}'s {@link Inventory} or spawns it for him
	 * @param p The {@code Player} to give
	 * @param amount The amount of items. Doesn't have to be used if the amount of added items will be 1 and if it does
	 *  not make sense if you add or spawn more than one item
	 */
	void giveToPlayer(Player p, int amount);
	
	/**
	 * Returns the identifier of this item. Does not return the related {@code ItemStack}'s display name.
	 * 	The identifier is the item's path in the configuration file
	 */
	@Override
	public String getName();
	
}

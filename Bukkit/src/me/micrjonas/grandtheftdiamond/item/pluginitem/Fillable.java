package me.micrjonas.grandtheftdiamond.item.pluginitem;

import org.bukkit.entity.Player;

/**
 * Represents a {@link PluginItem} which can have a fuel
 */
public interface Fillable extends PluginItem {
	
	/**
	 * Checks whether the {@code Player} has fuel to use the object
	 * @param player The player to check
	 * @return True if the {@code Player} has enough fuel, else false
	 */
	public boolean hasFuel(Player player);

}

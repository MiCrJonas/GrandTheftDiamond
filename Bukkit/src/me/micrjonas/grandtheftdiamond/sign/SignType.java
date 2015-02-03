package me.micrjonas.grandtheftdiamond.sign;

import me.micrjonas.grandtheftdiamond.arena.Arena;
import me.micrjonas.grandtheftdiamond.item.pluginitem.PluginItem;

/**
 * Represents a type if a plugin sign
 */
public enum SignType {
	
	/**
	 * Sign to buy or sell houses or to teleport to it.<br>
	 * Name of the house is required to create the sign
	 */
	HOUSE(true, 1),
	
	/**
	 * Sign to get a {@link PluginItem}.<br>
	 * The name of the house is required to create the sign
	 */
	ITEM(true, 1),
	
	/**
	 * Sign to jail a {@code Player}.<br>
	 * The name of the jail is required to create the sign
	 */
	JAIL(true, 1),
	
	/**
	 * Sign to join the game.<br>
	 */
	JOIN(false, 0),
	
	/**
	 * Sign to leave the game.<br>
	 */
	LEAVE(true, 0),
	
	/**
	 * Sign to buy a {@link PluginItem}.<br>
	 * The name of the item is required in the first line and the price is required in the second line
	 */
	SHOP(true, 2);
	
	private boolean mustBeInArena;
	private int linesRequired;
	
	private SignType(boolean mustBeInArena, int linesRequired) {
		this.mustBeInArena = mustBeInArena;
		this.linesRequired = linesRequired;
	}
	
	/**
	 * Whether the {@code Location} of the sign must be in an {@link Arena} to create it
	 * @return True if the sign must be in an {@link Arena}, else false
	 */
	public boolean mustBeInArena() {
		return mustBeInArena;
	}
	
	/**
	 * Returns the count of required lines to create the sign except the first line
	 * @return The count of required set lines for the sign's type
	 */
	public int getRequiredLines() {
		return linesRequired;
	}

}

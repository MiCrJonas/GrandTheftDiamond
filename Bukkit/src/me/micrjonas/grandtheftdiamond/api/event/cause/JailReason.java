package me.micrjonas.grandtheftdiamond.api.event.cause;

import org.bukkit.entity.Player;


/**
 * The reason why a {@link Player} gets jailed
 */
public enum JailReason implements EventCause {

	/**
	 * Player was jailed with a command
	 */
	COMMAND,
	
	/**
	 * Player was detained by a cop
	 */
	DETAIN,
	
	/**
	 * Player was arrested by a cop
	 */
	ARREST,
	
	/**
	 * A cop killed a civilian
	 */
	COP_KILLED_CIVILIAN,
	
	/**
	 * Custom jail reason
	 */
	CUSTOM;

}
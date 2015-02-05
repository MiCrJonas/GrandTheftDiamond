package me.micrjonas.grandtheftdiamond.api.event.cause;

import org.bukkit.entity.Player;


/**
 * The reason why a {@link Player} gets jailed
 */
public enum JailReason implements EventCause {

	/**
	 * Player was arrested by a cop
	 */
	ARREST,
	
	/**
	 * Player was jailed with a command
	 */
	COMMAND,
	
	/**
	 * A cop killed a civilian
	 */
	COP_KILLED_CIVILIAN,
	
	/**
	 * Custom jail reason
	 */
	CUSTOM,
	
	/**
	 * Player was detained by a cop
	 */
	DETAIN;

}
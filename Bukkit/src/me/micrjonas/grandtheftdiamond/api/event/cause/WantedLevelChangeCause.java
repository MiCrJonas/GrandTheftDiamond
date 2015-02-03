package me.micrjonas.grandtheftdiamond.api.event.cause;

/**
 * A reason for a wanted level change
 */
public enum WantedLevelChangeCause implements EventCause {
	
	/**
	 * Someone used a command to change the wanted level
	 */
	COMMAND,
	
	/**
	 * The player changed his team to cop
	 */
	COP,
	
	/**
	 * The player killed a cop
	 */
	COP_KILLED,
	
	/**
	 * Custom reason, not directly used by the plugin
	 */
	CUSTOM,
	
	/**
	 * The player died, normally wanted level reset
	 */
	DEATH,
	
	/**
	 * The player was jailed
	 */
	JAILED,
	
	/**
	 * The player robbed a safe
	 */
	SAFE_ROBBED

}

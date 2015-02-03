package me.micrjonas.grandtheftdiamond.api.event.cause;

/**
 * A reason for a stats change
 */
public enum StatsChangeCause implements EventCause {
	
	/**
	 * Custom reason
	 */
	CUSTOM,
	
	/**
	 * The player died, some stats changed
	 */
	DEATH,
	
	/**
	 * The player was jailed
	 */
	JAILED,
	
	/**
	 * The player jailed an other player
	 */
	JAILED_OTHER,
	
	/**
	 * The player killed a civilian
	 */
	KILLED_CIVILIAN,
	
	/**
	 * The player killed a cop
	 */
	KILLED_COP,
	
	/**
	 * The player killed a gangster
	 */
	KILLED_GANGSTER,
	
	/**
	 * The player changed his team (e.g. wanted level reset for cops)
	 */
	TEAM_CHANGED

}

package me.micrjonas.grandtheftdiamond.api.event.cause;

/**
 * A reason for leaving the game
 */
public enum LeaveReason implements EventCause {
	
	/**
	 * All players were kicked
	 */
	ALL_KICK, 
	
	/**
	 * The player was banned from the game
	 */
	BAN, 
	
	/**
	 * The player left by using a command
	 */
	COMMAND,
	
	/**
	 * A custom leave reason
	 */
	CUSTOM,
	
	/**
	 * The player was kicked out of the game
	 */
	KICK, 

	/**
	 * The player quit the server
	 */
	QUIT_SERVER, 
	
	/**
	 * The plugin gets disabled. (e.g. on server shutdown)
	 */
	PLUGIN_DISABLE, 
	
	/**
	 * The player left by using a sign
	 */
	SIGN

}

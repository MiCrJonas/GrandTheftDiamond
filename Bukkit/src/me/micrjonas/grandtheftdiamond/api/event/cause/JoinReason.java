package me.micrjonas.grandtheftdiamond.api.event.cause;

/**
 * A reason for joining the game
 */
public enum JoinReason implements EventCause {

	/**
	 * The player joined the game by using a command
	 */
	COMMAND,
	
	/**
	 * A custom join reason
	 */
	CUSTOM,
	
	/**
	 * The player joined the game by using a sign
	 */
	SIGN
	
}

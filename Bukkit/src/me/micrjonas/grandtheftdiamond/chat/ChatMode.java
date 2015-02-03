package me.micrjonas.grandtheftdiamond.chat;

public enum ChatMode {
	
	/**
	 * All players on the server can read the message
	 */
	GLOBAL,
	
	/**
	 * All players in the game can read the message
	 */
	LOCAL,
	
	/**
	 * Only players of the same team can read the message
	 */
	TEAM

}

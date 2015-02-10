package me.micrjonas.minecraft.server;

import java.util.UUID;

import me.micrjonas.minecraft.Wrapper;


/**
 * Represents a player on a server
 */
public interface User extends Wrapper {
	
	/**
	 * Checks whether the represented player is online
	 * @return True if the player is online, else {@code false}
	 */
	boolean isOnline();
	
	/**
	 * Returns the unique and persistent id for the represented player
	 * @return The unique id of the player
	 */
	UUID getUniqueId();
	
	/**
	 * Returns the last used name of the represented player
	 * @return The last name of the player
	 */
	String getLastName();
	
}

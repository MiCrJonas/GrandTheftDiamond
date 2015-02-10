package me.micrjonas.minecraft.server;

import java.util.UUID;

import me.micrjonas.grandtheftdiamond.util.Direction;
import me.micrjonas.grandtheftdiamond.util.Position;
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
	
	/**
	 * Teleports the player to a specific {@link Position}
	 * @param pos The {@link Position} to teleport the player to
	 * @return Whether teleportation was successful
	 */
	boolean teleport(Position pos);
	
	/**
	 * Teleports the player to a specific {@link Position} and sets a specific {@link Direction}
	 * @param pos The {@link Position} to teleport the player to
	 * @param dir The{@link Direction} of the player after teleporting
	 * @return Whether teleportation was successful
	 */
	boolean teleport(Position pos, Direction dir);
	
}

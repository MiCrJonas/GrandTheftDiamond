package me.micrjonas.grandtheftdiamond.arena;

/**
 * Type of a Grand Theft Diamond arena.
 * All arenas are from bottom of the world to top of the world
 */
public enum ArenaType {
	
	/**
	 * The arena is a cubed shape and requires two positions
	 */
	CUBOID, 
	
	/**
	 * The arena is a cylinder and requires a center location and a radius
	 */
	CYLINDER, 
	
	/**
	 * The whole server is the plugin's arena. No more data required
	 */
	WHOLE_SERVER, 
	
	/**
	 * A whole world is the arena. Requires the world name
	 */
	WHOLE_WORLD

}

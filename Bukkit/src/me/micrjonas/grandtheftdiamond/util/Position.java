package me.micrjonas.grandtheftdiamond.util;


/**
 * Represents a 3d position on a server with a world. Each object is immutable
 */
public class Position implements Immutable {
	
	private final String world;
	private final double x, y, z;
	
	/**
	 * Creates a new object with a world name and 0 for all other values
	 * @param world The name of the {@code Position}'s world
	 */
	public Position(String world) {
		this(world, 0, 0, 0);
	}
	
	/**
	 * Creates a new object with a world name and all 3 coordinate
	 * @param world The name of the {@code Position}'s world
	 * @param x The x-coordinate
	 * @param y The y-coordinate
	 * @param z The z-coordinate
	 */
	public Position(String world, double x, double y, double z) {
		if (world == null) {
			throw new IllegalArgumentException("World name is not allowed to be null");
		}
		this.world = world;
		this.x = x;
		this.y = y;
		this.z = z;
	}
	
	/**
	 * Returns the name of the {@code Position}s world
	 * @return The name of the {@code Position}s world. Not {@code null}
	 */
	String getWorld() {
		return world;
	}
	
	/**
	 * Returns the x-coordinate
	 * @return The x-coordinate
	 */
	double getX() {
		return x;
	}
	
	/**
	 * Returns the y-coordinate
	 * @return The y-coordinate
	 */
	double getY() {
		return y;
	}
	
	/**
	 * Returns the z-coordinate
	 * @return The z-coordinate
	 */
	double getZ() {
		return z;
	}
	
}

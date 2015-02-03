package me.micrjonas.grandtheftdiamond.util;

import org.bukkit.Location;

/**
 * A 2d position with x and z integer value. Each object is immutable
 */
public class SquareLocation implements Immutable {
	
	private final int x;
	private final int z;
	
	/**
	 * Default constructor
	 * @param x The x-coordinate
	 * @param z The z-coordinate
	 */
	public SquareLocation(int x, int z) {
		this.x = x;
		this.z = z;
	}
	
	/**
	 * Creates a new object with the {@code Location#getBlockX()} and {@code Location#getBlockZ()} values.<br>
	 *	The new object will no longer be related to the bukkit {@link Location}
	 * @param loc The bukkit location
	 * @throws IllegalArgumentException Thrown if {@code loc} is null
	 */
	public SquareLocation(Location loc) throws IllegalArgumentException {
		if (loc == null) {
			throw new IllegalArgumentFormatException("Location is not allowed to be null");
		}
		x = loc.getBlockX();
		z = loc.getBlockZ();
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + x;
		result = prime * result + z;
		return result;
	}

	/**
	 * Checks whether the x-coordinate and z-coordinate of both locations are the same
	 * @return True if the objects have the same class and the x-coordinate and z-coordinate
	 * 	of both locations are the same, else {@code false}
	 */
	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj == null) {
			return false;
		}
		if (getClass() != obj.getClass()) {
			return false;
		}
		SquareLocation other = (SquareLocation) obj;
		if (x != other.x) {
			return false;
		}
		if (z != other.z) {
			return false;
		}
		return true;
	}
	
	/**
	 * Returns the x-coordinate
	 * @return The x-coordinate
	 */
	public int getX() {
		return x;
	}
	
	/**
	 * Returns the z-coordinate
	 * @return The z-coordinate
	 */
	public int getZ() {
		return z;
	}

}

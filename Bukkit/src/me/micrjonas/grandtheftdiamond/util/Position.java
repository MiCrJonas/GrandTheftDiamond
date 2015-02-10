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
	
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((world == null) ? 0 : world.hashCode());
		long temp;
		temp = Double.doubleToLongBits(x);
		result = prime * result + (int) (temp ^ (temp >>> 32));
		temp = Double.doubleToLongBits(y);
		result = prime * result + (int) (temp ^ (temp >>> 32));
		temp = Double.doubleToLongBits(z);
		result = prime * result + (int) (temp ^ (temp >>> 32));
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Position other = (Position) obj;
		if (world == null) {
			if (other.world != null)
				return false;
		}
		else if (!world.equals(other.world))
			return false;
		if (Double.doubleToLongBits(x) != Double.doubleToLongBits(other.x))
			return false;
		if (Double.doubleToLongBits(y) != Double.doubleToLongBits(other.y))
			return false;
		if (Double.doubleToLongBits(z) != Double.doubleToLongBits(other.z))
			return false;
		return true;
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

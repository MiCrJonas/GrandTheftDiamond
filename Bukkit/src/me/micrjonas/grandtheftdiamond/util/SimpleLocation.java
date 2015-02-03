package me.micrjonas.grandtheftdiamond.util;

import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.block.Block;

/**
 * Represents a 3d location with integer coordinates and a world. Each object is immutable
 */
public class SimpleLocation extends SquareLocation implements Immutable {
	
	private World world;
	private int y;
	
	/**
	 * Creates a new object with the values of a bukkit's {@link Location}
	 * @param loc The bukkit {@link Location} top "copy"
	 * @throws IllegalArgumentException Thrown if the {@link World} of {@code loc} is {@code null}
	 * @throws NullPointerException Thrown if {@code loc} is {@code null}
	 */
	public SimpleLocation(Location loc) throws IllegalArgumentException, NullPointerException  {
		this(loc.getWorld(), loc.getBlockX(), loc.getBlockY(), loc.getBlockZ());
	}
	
	/**
	 * Default constructor
	 * @param world The location's {@link World}
	 * @param x The x-coordinate
	 * @param y The y-coordinate
	 * @param z The z-coordinate
	 * @throws IllegalArgumentException Thrown if {@code world} is {@code null}
	 */
	public SimpleLocation(World world, int x, int y, int z) throws IllegalArgumentException {
		super(x, z);
		if (world == null) {
			throw new IllegalArgumentException("World (of location) is not allowed to be null");
		}
		this.world = world;
		this.y = y;
	}
	
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((world == null) ? 0 : world.getName().hashCode());
		result = prime * result + getX();
		result = prime * result + y;
		result = prime * result + getZ();
		return result;
	}

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
		SimpleLocation other = (SimpleLocation) obj;
		if (world != other.world) {
			return false;
		}
		if (getX() != other.getX()) {
			return false;
		}
		if (y != other.y) {
			return false;
		}
		if (getZ() != other.getZ()) {
			return false;
		}
		return true;
	}

	/**
	 * Returns the {@code World} of the location
	 * @return The location's {@code World}. Never {@code null}
	 */
	public World getWorld() {
		return world;
	}

	/**
	 * Returns the y-coordinate
	 * @return The y-coordinate
	 */
	public int getY() {
		return y;
	}
	
	/**
	 * Returns a new {@code SimpleLocation} which represents the location over this location
	 * @return A new {@code SimpleLocation} as representation of the location over this location
	 */
	public SimpleLocation getLocationOver() {
		return new SimpleLocation(world, getX(), y + 1, getZ());
	}
	
	/**
	 * Returns a new {@code SimpleLocation} which represents the location below this location
	 * @return A new {@code SimpleLocation} as representation of the location below this location
	 */
	public SimpleLocation getLocationBelow() {
		return new SimpleLocation(world, getX(), y - 1, getZ());
	}
	
	/**
	 * Returns a new bukkit {@link Location} as a copy of this location
	 * @return A new bukkit {@link Location} as a copy of this location
	 */
	public Location toBukkitLocation() {
		return new Location(world, getX(), y, getZ());
	}
	
	/**
	 * Returns the bukkit {@link Block} at the represented location
	 * @return The bukkit {@link Block} at the represented location
	 * @see World#getBlockAt(int, int, int)
	 */
	public Block getBlock() {
		return world.getBlockAt(getX(), y, getZ());
	}

}

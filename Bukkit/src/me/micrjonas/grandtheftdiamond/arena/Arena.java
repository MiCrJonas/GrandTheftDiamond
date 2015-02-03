package me.micrjonas.grandtheftdiamond.arena;

import java.awt.Rectangle;
import java.util.EnumMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;

import me.micrjonas.grandtheftdiamond.data.storage.Storable;
import me.micrjonas.grandtheftdiamond.jail.Jail;
import me.micrjonas.grandtheftdiamond.util.Nameable;
import me.micrjonas.grandtheftdiamond.util.SquareLocation;
import me.micrjonas.grandtheftdiamond.util.bukkit.Locations;
import me.micrjonas.grandtheftdiamond.util.bukkit.WorldStorage;
import me.micrjonas.grandtheftdiamond.util.collection.Collections;

import org.bukkit.Location;
import org.bukkit.World;


/**
 * Represents a Grand Theft Diamond arena
 */
public class Arena implements Nameable, Storable {
	
	private final String name;
	
	private final ArenaType type;
	private final SquareLocation[] bounds = new SquareLocation[2];
	private World world;
	private int radius = -1;
	
	private Location defaultSpawn;
	private final Map<SpawnType, Set<Spawn>> spawns = new EnumMap<>(SpawnType.class);
	
	Arena(String name, ArenaType type, SquareLocation loc0, SquareLocation loc1, World world, int radius) {
		this.name = name;
		if (type == null) {
			throw new IllegalArgumentException("type is not allowed to be null");
		}
		if (type != ArenaType.WHOLE_SERVER) {
			if (world == null) {
				throw new IllegalArgumentException("world is not allowed to be null");
			}
			this.world = world;
			if (type == ArenaType.CYLINDER) {
				if (loc0 == null) {
					throw new IllegalArgumentException("loc0 is not allowed to be null if type is ArenaType.CYLINDER");
				}
				if (radius < 1) {
					throw new IllegalArgumentException("radius must be > 0");
				}
				bounds[0] = loc0;
				this.radius = radius;
			}
			else if (type == ArenaType.CUBOID) {
				bounds[0] = new SquareLocation(Math.min(loc0.getX(), loc1.getX()), Math.min(loc0.getZ(), loc1.getZ()));
				bounds[1] = new SquareLocation(Math.max(loc0.getX(), loc1.getX()), Math.max(loc0.getZ(), loc1.getZ()));
			}
		}
		for (SpawnType spawnType : SpawnType.multipleSpawnPossibles()) {
			spawns.put(spawnType, new HashSet<Spawn>());
		}
		this.type = type;
	}

	@Override
	public String getName() {
		return name;
	}
	
	@Override
	public Map<String, Object> getStoreData() {
		Map<String, Object> data = new LinkedHashMap<>();
		//Bounds and arena type
		data.put("type", type.name());
		if (type != ArenaType.WHOLE_SERVER) {
			data.put("world", world.getName());
			if (type != ArenaType.WHOLE_WORLD) {
				data.put("pos1.x", bounds[0].getX());
				data.put("pos1.z", bounds[0].getZ());
				if (type == ArenaType.CUBOID) {
					data.put("pos2.x", bounds[1].getX());
					data.put("pos2.z", bounds[1].getZ());
				}
			}
		}
		// Spawns
		data.put("spawns.default", Locations.toMap(defaultSpawn, WorldStorage.NAME, false));
		for (SpawnType spawnType : SpawnType.multipleSpawnPossibles()) {
			for (Spawn spawn : spawns.get(spawnType)) {
				data.put("spawns." + spawnType.name() + "." + spawn.getName(), 
						Locations.toMap(spawn.getLocation(), WorldStorage.NAME, true));
			}
		}
		return data;
	}
	
	/**
	 * Creates a new {@link Spawn} for the {@code Arena}
	 * @param name The name of the {@link Spawn}
	 * @param type The type of the {@link Spawn}
	 * @param loc The location of the {@link Spawn}
	 * @return The new creates {@link Spawn}
	 * @throws IllegalArgumentException Thrown if:
	 * 	<ul>
	 * 		<li>{@code name} is {@code null}</li>
	 * 		<li>{@code type} is {@code null}</li>
	 * 		<li>
	 * 			{@code type} is {@link SpawnType#JAIL}
	 * 			<ul>
	 * 				<li>Use {@link Jail#setSpawn(Location)}</li>
	 * 			</ul>
	 * 		</li>
	 * 		<li>{@code loc} is {@code null}</li>
	 * 		<li>{@link World} of {@code loc} is {@code null}</li>
	 * 	</ul>
	 */
	public Spawn createSpawn(String name, SpawnType type, Location loc) throws IllegalArgumentException {
		if (name == null) {
			throw new IllegalArgumentException("name is not allowed to be null");
		}
		if (type == null) {
			throw new IllegalArgumentException("type is not allowed to be null");
		}
		if (type == SpawnType.JAIL) {
			throw new IllegalArgumentException("cannt directly set a jail spawn");
		}
		if (loc.getWorld() == null) {
			throw new IllegalArgumentException("location's world is not allowed to be null");
		}
		if (!inArena(loc, false)) {
			throw new IllegalArgumentException("location is not in the arena");
		}
		Spawn spawn = new Spawn(name, type, loc.clone());
		Set<Spawn> spawnSet = spawns.get(type);
		if (spawnSet == null) {
			spawnSet = new HashSet<>(5);
			spawns.put(type, spawnSet);
		}
		spawnSet.add(spawn);
		return spawn;
	}
	
	/**
	 * Removes the {@code Arena}
	 * @see ArenaManager#removeArena(String)
	 */
	public void remove() {
		ArenaManager.getInstance().removeArena(getName());
	}
	
	/**
	 * Returns the type of the arena
	 * @return The type of the arena
	 */
	public ArenaType getType() {
		return type;
	}
	
	/**
	 * Returns the minimum location or the center of the map.<br>
	 * Returns null if arena type is WHOLE_SERVER or WHOLE_WORLD
	 * @return The minimum location of the arena
	 */
	public SquareLocation getMinOrCenter() {
		if (bounds[0] != null) {
			return bounds[0];
		}
		return null;
	}
	
	/**
	 * Returns the second position of the arena
	 * @return the maximum position of the arena if arena type is {@link ArenaType#CUBOID}, else {@code null}
	 */
	public SquareLocation getMax() {
		if (bounds[1] != null) {
			return bounds[1];
		}
		return null;
	}
	
	/**
	 * Returns the world of the arena, {@code null} if arena type is {@link ArenaType#WHOLE_SERVER}
	 * @return The arena's world
	 */
	public World getWorld() {
		return world;
	}
	
	/**
	 * Returns the radius of the arena
	 * @return The radius of the arena, -1 if the arena is not a cylinder
	 */
	public int getRadius() {
		return radius;	
	}
	
	/**
	 * Sets the default spawn
	 * @param spawn The new spawn
	 * @throws IllegalArgumentException Thrown if {@code spawn} is {@code null}
	 */
	public void setDefaultSpawn(Location spawn) throws IllegalArgumentException {
		if (spawn == null) {
			throw new IllegalArgumentException("Spawn cannot be null");
		}
		defaultSpawn = spawn.clone();
		if (spawn.getWorld() == null) {
			defaultSpawn.setWorld(getWorld());
		}
	}
	
	/**
	 * Returns the default spawn of the arena
	 * @return The default spawn of the arena. May be {@code null}
	 */
	public Location getDefaultSpawn() {
		if (defaultSpawn != null) {
			return defaultSpawn.clone();
		}
		return null;
	}
	
	/**
	 * Returns a new Map of all spawns with their keys of the given spawn type
	 * @param type The type of spawns
	 * @return A new Map of all spawns of the given type. The spawn identifiers are stored in lower cases
	 * @throws IllegalArgumentException Thrown if {@code type} is {@code null} or {@link SpawnType#DEFAULT}
	 */
	public Set<Spawn> getSpawns(SpawnType type) throws IllegalArgumentException {
		if (type == null) {
			throw new IllegalArgumentException("Spawn type is not allowed to be null");
		}
		if (type == SpawnType.DEFAULT) {
			throw new IllegalArgumentException("Cannot return a Map with default spawn(s). SpawnType.DEFAULT is invalid");
		}
		Set<Spawn> spawns = this.spawns.get(type);
		if (spawns == null) {
			return new HashSet<Spawn>(0);
		}
		return Collections.filledSet(this.spawns.get(type));
	}
	
	/**
	 * Returns the hospital {@link Spawn} which is the nearest to {@code loc}
	 * @param loc The {@code Location}
	 * @return The next hospital {@link Spawn} to {@code loc}. {@code null} if no hospital {@link Spawn}s are registered
	 * 	for the {@code Arena}
	 * @throws IllegalArgumentException Thrown if {@code loc} is {@code null}
	 */
	public Spawn getNearestHospital(Location loc) throws IllegalArgumentException {
		if (loc == null) {
			throw new IllegalArgumentException("Cannot get next hospital of null location");
		}
		Set<Spawn> hospitals = spawns.get(SpawnType.HOSPITAL);
		if (!hospitals.isEmpty()) {
			Iterator<Spawn> iter = spawns.get(SpawnType.HOSPITAL).iterator();
			Spawn next = iter.next();
			double distance = distance(loc, next.getUnclonedLocation());
			while (iter.hasNext()) {
				Spawn tmpNext = iter.next();
				double tmpDistance = distance(loc, tmpNext.getUnclonedLocation());
				if (tmpDistance < distance) {
					next = tmpNext;
					distance = tmpDistance;
				}
			}
			return next;
		}
		return null;
	}
	
	/**
	 * Checks whether the Location is in the arena
	 * @param loc The Location to check
	 * @param defaultSpawnMustBeSet Set to true if the default spawn must be set, else set to false
	 * @return True if the Location is in the arena, else false
	 * @throws IllegalArgumentException Thrown if {@code loc} is {@code null}
	 */
	public boolean inArena(Location loc, boolean defaultSpawnMustBeSet) throws IllegalArgumentException {
		if (loc == null) {
			throw new IllegalArgumentException("location is not allowed to be null");
		}
		if (defaultSpawnMustBeSet && defaultSpawn == null) {
			return false;
		}
		switch (type) {
			case CUBOID:
				return insideRectangle(bounds[0], bounds[1], new SquareLocation(loc));
				
			case CYLINDER:
				return radius >= distance(bounds[0], new SquareLocation(loc));
				
			case WHOLE_SERVER:
				return true;
				
			case WHOLE_WORLD:
				return world == loc.getWorld();
		}
		return false;
	}
	
	/**
	 * Equivalent to {@link Arena#inArena(loc, true)}
	 * @param loc The {@code Location} to check
	 * @return True if the {@link Location} is in the {@code Arena}, else {@code false}
	 * @throws IllegalArgumentException 
	 * @see Arena#inArena(Location, boolean)
	 */
	public boolean inArena(Location loc) throws IllegalArgumentException{
		return inArena(loc, true);
	}
	
	/**
	 * Checks whether the arena collides with {@code other}
	 * @param other The other arena
	 * @return True if the arena collides with the other arena
	 * @throws IllegalArgumentException Thrown if {@code other} is {@code null}
	 */
	public boolean intersects(Arena other) throws IllegalArgumentException {
		if (other == null) {
			throw new IllegalArgumentException("Other arena is not allowed to be null");
		}
		if (type == ArenaType.WHOLE_SERVER || other.type == ArenaType.WHOLE_SERVER) {
			return true;
		}
		if (type == ArenaType.WHOLE_WORLD || other.type == ArenaType.WHOLE_WORLD) {
			return world == other.world;
		}
		switch (type) {
			case CUBOID: {
				if (other.type == ArenaType.CUBOID) {
					return new Rectangle(bounds[0].getX(), bounds[0].getZ(), bounds[1].getX() - bounds[0].getX(), bounds[1].getZ() - bounds[0].getZ())
							.intersects(new Rectangle(other.bounds[0].getX(), other.bounds[0].getZ(), other.bounds[1].getX() - other.bounds[0].getX(), other.bounds[1].getZ() - other.bounds[0].getZ()));
				}
				else { //type == CYLINDER
					return cylinderCollidesCuboid(bounds[0], bounds[1], other.bounds[0], other.radius);
				}
			} 
			case CYLINDER: {
				if (other.type == ArenaType.CUBOID) {
					return cylinderCollidesCuboid(other.bounds[0], other.bounds[1], bounds[0], radius);
				}
				else { //type == CYLINDER
					return distance(bounds[0], other.bounds[0]) <= radius + other.radius;
				}
			} 
			default:
				break;
		}
		return false;
	}
	
	private boolean insideRectangle(SquareLocation min, SquareLocation max, SquareLocation toCheck) {
		return toCheck.getX() >= min.getX() && toCheck.getX() <= max.getX() && 
				toCheck.getZ() >= min.getZ() && toCheck.getZ() <= max.getZ();
	}
	
	private boolean cylinderCollidesCuboid(SquareLocation min, SquareLocation max, SquareLocation center, int radius) {
		if (insideRectangle(min, max, center)) {
			return true;
		}
		SquareLocation[] corners = new SquareLocation[4];
		corners[0] = new SquareLocation(min.getX(), max.getX());
		corners[1] = new SquareLocation(min.getZ(), max.getZ());
		corners[2] = new SquareLocation(min.getX(), max.getZ());
		corners[3] = new SquareLocation(min.getZ(), max.getX());
		for (SquareLocation corner : corners) {
			if (distance(corner, center) <= radius) {
				return true;
			}
		}
		if (center.getX() > max.getX()) {
			if (center.getZ() >= min.getZ() && center.getZ() <= max.getZ() && center.getX() - max.getX() <= radius) {
				return true;
			}
		}
		else if (center.getX() < min.getX()) {
			if (center.getZ() >= min.getZ() && center.getZ() <= max.getX() && min.getX() - center.getX() <= radius) {
				return true;
			}
		}
		if (center.getZ() > max.getZ()) {
			if (center.getX() >= min.getX() && center.getX() <= max.getX() && center.getZ() - max.getZ() <= radius) {
				return true;
			}
		}
		
		else if (center.getZ() < min.getZ()) {
			if (center.getX() >= min.getX() && center.getX() <= max.getX() && min.getZ() - center.getZ() <= radius) {
				return true;
			}
		}
		return false;
	}
	
	private double distance(SquareLocation loc0, SquareLocation loc1) {
		return distance(loc0.getX(), loc1.getX(), loc0.getZ(), loc1.getZ());
	}
	
	private double distance(Location loc0, Location loc1) {
		return distance(loc0.getX(), loc1.getX(), loc0.getZ(), loc1.getZ());
	}
	
	private double distance(double x0, double x1, double z0, double z1) {
		double x = x0 - x1;
		double z = z0 - z1;
		return Math.sqrt(x * x + z * z);
	}

}

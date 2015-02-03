package me.micrjonas.grandtheftdiamond.arena;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import me.micrjonas.grandtheftdiamond.GrandTheftDiamond;
import me.micrjonas.grandtheftdiamond.data.PluginFile;
import me.micrjonas.grandtheftdiamond.data.storage.StorableManager;
import me.micrjonas.grandtheftdiamond.util.Enums;
import me.micrjonas.grandtheftdiamond.util.SquareLocation;

import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.configuration.file.FileConfiguration;

/**
 * Manages all {@link Arena} related things like creating or removing an {@link Arena}
 */
public class ArenaManager implements StorableManager<Arena> {
	
	private static final ArenaManager instance = new ArenaManager();
	
	/**
	 * Returns the loaded instance
	 * @return The loaded instance
	 */
	public static ArenaManager getInstance() {
		return instance;
	}
	
	
	private final Map<String, Arena> arenas = new HashMap<>();
	
	private ArenaManager() {
		GrandTheftDiamond.registerStorableManager(this, PluginFile.ARENA);
	}

	
	@Override
	public Collection<Arena> getAllObjects() {
		return arenas.values();
	}

	
	@Override
	public void loadObjects(FileConfiguration dataFile) {
		arenas.clear();
		for (String arena : dataFile.getConfigurationSection("").getKeys(false)) {
			ArenaType type = Enums.getEnumFromConfig(ArenaType.class, dataFile, arena + ".type");
			SquareLocation loc1 = null;
			SquareLocation loc2 = null;
			World world = null;
			if (dataFile.isInt(arena + ".pos1.x") && dataFile.isInt("pos1.z")) {
				loc1 = new SquareLocation(dataFile.getInt(arena + ".pos1.x"), dataFile.getInt(arena + ".pos1.z"));
			}
			if (dataFile.isInt(arena + ".pos2.x") && dataFile.isInt("pos2.z")) {
				loc1 = new SquareLocation(dataFile.getInt(arena + ".pos2.x"), dataFile.getInt(arena + ".pos2.z"));
			}
			if (dataFile.isString(arena + ".world")) {
				world = Bukkit.getWorld(dataFile.getString(arena + ".world"));
			}
			try {
				createArena(arena, type, loc1, loc2, world, dataFile.getInt(arena + ".radius"));
			}
			catch (ArenaIntersectsException e) {
				GrandTheftDiamond.getLogger().warning("Arena '" + arena + "' intersects with an other arena. Deleting arena...");
				dataFile.set(arena, null);
			}
			catch (IllegalArgumentException e) {
				GrandTheftDiamond.getLogger().warning("Some data for arena '" + arena + "' is missing: " + e.getMessage());
				GrandTheftDiamond.getLogger().warning("Deleting arena...");
				dataFile.set(arena, null);
			}
		}
	}
	
	/**
	 * Creates a new {@link Arena}
	 * @param name The name of the {@link Arena}, requires to be non null
	 * @param type The type of the {@link Arena}, requires to be non null
	 * @param loc1 The first Location of the {@link Arena}, required for CUBOID and CYLINDER
	 * @param loc2 The second Location of the {@link Arena}, required for CUBOID
	 * @param world The {@link World} of the {@link Arena}, required for CUBOID, CYLINDER and WHOLE_WORLD
	 * @param radius The radius of the {@link Arena}, required for CYLINDER
	 * @return The new {@link Arena}
	 * @throws IllegalArgumentException If some needed arguments are null or the {@link Arena} does already exist
	 * @throws ArenaIntersectsException If the arena intersects one or more other arenas
	 */
	public Arena createArena(String name, ArenaType type, SquareLocation loc1, SquareLocation loc2, World world, int radius) {
		String identifier = name.toLowerCase();
		if (arenas.containsKey(identifier)) {
			throw new IllegalArgumentException("Arena '" + identifier + "' does already exist");
		}
		Arena newArena = new Arena(name, type, loc1, loc2, world, radius);
		List<Arena> intersects = new ArrayList<>();
		for (Arena arena : arenas.values()) {
			if (arena.intersects(newArena)) {
				intersects.add(arena);
			}
		}
		if (intersects.size() > 0) {
			throw new ArenaIntersectsException(intersects);
		}
		arenas.put(identifier, newArena);
		return newArena;
	}
	
	/**
	 * Checks whether an {@link Arena} exists
	 * @param name The name of the arena, ignores case sensitive
	 * @return True if an {@link Arena} with {@code name} exists, else false
	 * @throws IllegalArgumentException Thrown if {@code name} is {@code null}
	 */
	public boolean isArena(String name) throws IllegalArgumentException {
		return arenas.containsKey(name.toLowerCase());
	}
	
	/**
	 * Returns the {@link Arena} by the passed name
	 * @param name The name of the {@link Arena}
	 * @return The {@link Arena} with the passed name, null if the {@link Arena} does not exist, ignores case sensitive
	 * @throws IllegalArgumentException Thrown if {@code name} is {@code null}
	 */
	public Arena getArena(String name) throws IllegalArgumentException {
		if (name == null) {
			throw new IllegalArgumentException("Arena name is not allowed to be null");
		}
		return arenas.get(name.toLowerCase());
	}
	
	/**
	 * Removes an {@link Arena}
	 * @param name The name of the {@link Arena} to remove
	 * @throws IllegalArgumentException Thrown if {@code name} is {@code null} or an {@link Arena} with the name does not exist
	 */
	public void removeArena(String name) throws IllegalArgumentException {
		if (name == null) {
			throw new IllegalArgumentException("Arena name is not allowed to be null");
		}
		if (!isArena(name = name.toLowerCase())) {
			throw new IllegalArgumentException("Arena with name '" + name + "' does not exist");
		}
		arenas.remove(name);
	}
		
}

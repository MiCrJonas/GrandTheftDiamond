package me.micrjonas.grandtheftdiamond.jail;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import me.micrjonas.grandtheftdiamond.data.storage.Storable;
import me.micrjonas.grandtheftdiamond.util.Nameable;
import me.micrjonas.grandtheftdiamond.util.bukkit.Locations;
import me.micrjonas.grandtheftdiamond.util.bukkit.WorldStorage;

import org.bukkit.Location;
import org.bukkit.entity.Player;

/**
 * Represents a jail
 */
public class Jail implements Nameable, Storable {
	
	private final String name;
	private Location spawn;
	private Set<Location> cells = new HashSet<>();
	private Set<Player> players = new HashSet<>();
	
	Jail(String name, Location spawn, List<Location> cells) {
		this.name = name;
		this.spawn = spawn;
		if (cells != null) {
			cells.addAll(cells);
		}
	}
	
	@Override
	public Map<String, Object> getStoreData() {
		Map<String, Object> storageData = new LinkedHashMap<>();
		List<Map<String, Object>> cells = new ArrayList<>();
		storageData.put("spawn.world", spawn.getWorld().getName());
		storageData.put("spawn.x", spawn.getX());
		storageData.put("spawn.y", spawn.getY());
		storageData.put("spawn.z", spawn.getZ());
		storageData.put("spawn.pitch", spawn.getPitch());
		storageData.put("spawn.yaw", spawn.getYaw());
		for (Location cell : this.cells) {
			cells.add(Locations.toMap(cell, WorldStorage.NAME, false));
		}
		storageData.put("cells", cells);
		return storageData;
	}
	
	/**
	 * Returns whether a player can be jailed in this jail
	 * There must be at least one cell
	 * @return True if a player could be jailed in this jail, else false
	 */
	public boolean isUsable() {
		return cells.size() > 0;
	}
	
	/**
	 * Returns the name of the jail
	 * @return The name of the jail
	 */
	@Override
	public String getName() {
		return name;
	}
	
	/**
	 * Returns the spawn location of the jail where players spawn after they get released from jail
	 * @return The spawn location of the jail
	 */
	public Location getSpawn() {
		return spawn.clone();
	}
		
	/**
	 * Sets the spawn location of the jail
	 * @param newSpawn The new spawn location of the jail, requires non-null
	 * @throws IllegalArgumentException if newSpawn is {@code null}
	 */
	public void setSpawn(Location newSpawn) {
		if (newSpawn == null) {
			throw new IllegalArgumentException("newSpawn is not allowed to be null");
		}
		spawn = newSpawn.clone();
	}
	
	/**
	 * Returns a list of all cells of the jail
	 * @return A list of all cells of the jail. The {@code Set} is not modifiable
	 */
	public Set<Location> getCells() {
		return Collections.unmodifiableSet(cells);
	}
	
	/**
	 * Returns a random cell
	 * @return A random cell of the jail. {@code null} if the {@code Jail} does not have any cells
	 */
	public Location getRandomCell() {
		return me.micrjonas.grandtheftdiamond.util.collection.Collections.getRandomElement(cells).clone();
	}
	
	/**
	 * Adds a cell to the jail
	 * @param newCell The cell which should be added to the jail
	 * @throws IllegalArgumentException Thrown if {@code newCell} is {@code null}
	 */
	public void addCell(Location newCell) throws IllegalArgumentException {
		if (newCell == null) {
			throw new IllegalArgumentException("newCell is not allowed to be null");
		}
		cells.add(newCell.clone());
	}
	
	/**
	 * Removes all cells of the jail
	 */
	public void clearCells() {
		cells.clear();
	}
	
	/**
	 * Returns all players jailed in this jail
	 * @return All players jailed in this jail. The {@code Set} is not modifiable
	 */
	public Set<Player> getJailedPlayers() {
		return Collections.unmodifiableSet(players);
	}
	
	void addPlayer(Player p) {
		players.add(p);
	}
	
	void removePlayer(Player player) {
		players.remove(player);
	}
	
}

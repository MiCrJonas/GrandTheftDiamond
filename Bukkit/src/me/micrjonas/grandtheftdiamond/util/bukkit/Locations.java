package me.micrjonas.grandtheftdiamond.util.bukkit;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import me.micrjonas.grandtheftdiamond.util.Objects;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.configuration.file.FileConfiguration;

public class Locations {
	
	private Locations() { }
	
	public static boolean isSameBlock(Location loc1, Location loc2) {
		return loc1.getWorld() == loc2.getWorld() &&
				loc1.getBlockX() == loc2.getBlockX() &&
				loc1.getBlockY() == loc2.getBlockY() &&
				loc1.getBlockZ() == loc2.getBlockZ();
	}
	
	
	public static void saveLocationToFile(Location loc, FileConfiguration file, String path, boolean ignoreDirection) throws IllegalArgumentException {
		if (loc == null) {
			throw new IllegalArgumentException("loc is not allowed to be null");
		}
		if (loc.getWorld() == null) {
			throw new IllegalArgumentException("World of loc is not allowed to be null");
		}
		if (file == null) {
			throw new IllegalArgumentException("file is not allowed to be null");
		}
		if (path == null) {
			throw new IllegalArgumentException("path is not allowed to be null");
		}
		file.set(path + ".world", loc.getWorld().getName());
		file.set(path + ".x", loc.getX());
		file.set(path + ".y", loc.getY());
		file.set(path + ".z", loc.getZ());
		if (!ignoreDirection) {
			file.set(path + ".yaw", loc.getYaw());
			file.set(path + ".pitch", loc.getPitch());	
		}
	}
	
	public static Location getLocationFromFile(FileConfiguration file, String path, boolean ignoreDirection) {
		if (!file.isConfigurationSection(path)) {
			return null;
		}
		String worldName = file.getString(path + ".world");
		if (worldName == null) {
			return null;
		}
		World world = Bukkit.getWorld(worldName);
		if (world == null) {
			return null;
		}
		return new Location(world, 
				file.getDouble(path + ".x"), 
				file.getDouble(path + ".y"), 
				file.getDouble(path + ".z"), 
				ignoreDirection ? 0 : (float) file.getDouble(path + ".yaw"), 
				ignoreDirection ? 0 : (float) file.getDouble(path + ".pitch"));
	}
	
	
	@SuppressWarnings("unchecked")
	public static List<Location> getLocationsFromFile(FileConfiguration file, String path, boolean ignoreDirection) {
		if (!file.isList(path)) {
			Location loc = getLocationFromFile(file, path, ignoreDirection);
			if (loc != null) {
				return Arrays.asList(loc);
			}
			return null;
		}
		List<Location> locs = new ArrayList<>();
		for (Object loc : file.getList(path)) {
			if (loc instanceof Map) {
				
				try {
					locs.add(getFromMap((Map<String, Object>) loc, ignoreDirection));
				}
				
				catch (IllegalArgumentException ex) { }
				
			}
			
		}
		
		return locs;
			
	}
	
	
// storage
	public static Map<String, Object> toMap(String subPath, Location loc, WorldStorage worldStorage, boolean ignoreDirection) {
	
		Map<String, Object> data = new LinkedHashMap<>();
	
		if (subPath.length() > 0)
			subPath += ".";
		
		if (worldStorage == WorldStorage.WORLD)
			data.put(subPath + "world", loc.getWorld());
		
		else if (worldStorage == WorldStorage.WORLD_AND_NAME) {
			
			data.put(subPath + "world", loc.getWorld());
			data.put(subPath + "worldname", loc.getWorld().getName());
			
		}
		
		else if (worldStorage == WorldStorage.NAME)
			data.put(subPath + "world", loc.getWorld().getName());
			
		data.put(subPath + "x", loc.getX());
		data.put(subPath + "y", loc.getY());
		data.put(subPath + "z", loc.getZ());
		
		if (!ignoreDirection) {
			
			data.put(subPath + "pitch", loc.getPitch());
			data.put(subPath + "yaw", loc.getYaw());	
			
		}
		
		return data;
		
	}
	
	public static Map<String, Object> toMap(Location loc, WorldStorage worldStorage, boolean ignoreDirection) {
		
		return toMap("", loc, worldStorage, ignoreDirection);
		
	}
	
	
	public static List<Map<String, Object>> toMapList(Collection<Location> locs, boolean ignoreDirection) {
		
		List<Map<String, Object>> maps = new ArrayList<>();
		
		for (Location loc : locs)
			maps.add(toMap(loc, WorldStorage.NAME, ignoreDirection));
		
		return maps;
		
	}
	
	
	public static Location getFromMap(Map<String, Object> loc, boolean ignoreDirection) {
		
		Object worldData = loc.get("world");
		World world;
		
		if (worldData == null)
			worldData = loc.get("worldname");
		
		if (worldData == null)
			throw new IllegalArgumentException("loc has no valid world data (no data defined)");
		
		if (worldData instanceof World)
			world = (World) worldData;
		
		else if (worldData instanceof String)
			world = Bukkit.getWorld((String) worldData);
		
		else
			throw new IllegalArgumentException("loc has no valid world data");
		
		float yaw = ignoreDirection ? 0 : (float) Objects.getDouble(loc.get("yaw"));
		float pitch = ignoreDirection ? 0 : (float) Objects.getDouble(loc.get("pitch"));
		
		return new Location(world, Objects.getIntValue(loc.get("x")), 
				Objects.getIntValue(loc.get("y")), 
				Objects.getIntValue(loc.get("z")), 
				yaw, 
				pitch);
		
	}
	
	
	public static List<Location> getFromMapList(List<Map<String, Object>> locs, boolean ignoreDirection) {
		
		List<Location> list = new ArrayList<>();
		
		for (Map<String, Object> loc : locs)
			list.add(getFromMap(loc, ignoreDirection));
		
		return list;
		
	}

}

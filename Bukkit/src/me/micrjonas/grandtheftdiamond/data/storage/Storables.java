package me.micrjonas.grandtheftdiamond.data.storage;

import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Map.Entry;

import me.micrjonas.grandtheftdiamond.util.IllegalArgumentFormatException;
import me.micrjonas.grandtheftdiamond.util.Objects;

import org.bukkit.configuration.file.FileConfiguration;

public class Storables {
	
	private final static char ASSIGNMENT_MARK = '=';
	private final static String ASSIGNMENT_MARK_STRING = String.valueOf(ASSIGNMENT_MARK);
	
	private Storables() { /*private constructor*/ }
	
	/**
	 * Stores all objects of a {@link StorableManager} into a {@link FileConfiguration}. Every object is stored at
	 * 	its own sub-path with the {@link Storable}'s name
	 * @param manager The registering manager
	 * @param file The file to store the data in
	 * @param path The path in the {@code file}
	 */
	public static <T extends Storable> void saveRegisteredObjects(StorableManager<T> manager, FileConfiguration file, String path) {
		for (T obj : manager.getAllObjects()) {
			String finalPath = path == null || path.isEmpty() ? "" : path + "." + obj.getName();
			file.set(finalPath, obj.getStoreData());
		}
	}
	
	public static String serialize(Storable s) throws IllegalArgumentException {
		if (s == null)
			throw new IllegalArgumentException("s is not allowed to be null");
		
		StringBuilder builder = new StringBuilder();
		Iterator<Entry<String, Object>> iter = s.getStoreData().entrySet().iterator();
		Entry<String, Object> entry;
		while (iter.hasNext()) {
			entry = iter.next();
			builder.append(entry.getKey()).append(ASSIGNMENT_MARK).append(entry.getValue());
			if (iter.hasNext()) {
				builder.append(",");
			}
		}
		return builder.toString();
	}
	
	public static Map<String, Object> deserialize(String s) throws IllegalArgumentException, IllegalArgumentFormatException {
		if (s == null) {
			throw new IllegalArgumentException("s is not allowed to be null");
		}
		Map<String, Object> data = new LinkedHashMap<>();
		String[] entries = s.split(",");
		for (String entry : entries) {
			String[] parts = entry.split(ASSIGNMENT_MARK_STRING);
			if (parts.length == 2) {
				data.put(parts[0], Objects.belongingObject(s));
			}
			else {
				throw new IllegalArgumentFormatException("Invalid input String: " + s);
			}
		}
		return data;
	}

}

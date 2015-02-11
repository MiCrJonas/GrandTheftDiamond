package me.micrjonas.grandtheftdiamond.data.storage;

import java.sql.SQLException;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Map.Entry;

import me.micrjonas.util.Nameable;

import org.bukkit.configuration.file.FileConfiguration;

import com.mysql.jdbc.Statement;

public class ObjectStorage implements Nameable {
	
// Start of static
	/**
	 * The separator of an entry
	 */
	public static final String SEPARATOR = ";";
	
	/**
	 * The separator of two objects in an array
	 */
	public static final String ARRAY_VALUE_SEPARATOR = ",";
	
	/**
	 * Serializes an {@code Object} array to a {@code String}
	 * @param arr The array to serialize
	 * @return The serialized {@code String}
	 * @see ObjectStorage#SEPARATOR
	 * @see ObjectStorage#ARRAY_VALUE_SEPARATOR
	 * @throws IllegalArgumentException Thrown if {@code arr} is {@code null}
	 */
	public static String serialize(Object[] arr) throws IllegalArgumentException {
		if (arr == null) {
			throw new IllegalArgumentException("Cannot serialize null array");
		}
		if (arr.length == 0) {
			return "{}";
		}
		StringBuilder builder = new StringBuilder();
		builder.append("{");
		int end = arr.length - 1;
		for (int i = 0; i < end; i++) {
			builder.append(arr[i]).append(ARRAY_VALUE_SEPARATOR);
		}
		builder.append(ARRAY_VALUE_SEPARATOR).append(arr[arr.length - 1]).append("}");
		return builder.toString();
	}
	
	/**
	 * Deserializes a {@code String} array stored as {@code String}
	 * @param s The {@code String} array
	 * @return The deserialized {@code String} array
	 * @throws IllegalArgumentException Thrown if {@code s} is {@code null}
	 */
	public static String[] deserializeStringArray(String s) throws IllegalArgumentException {
		if (s == null) {
			throw new IllegalArgumentException("Cannot deserialize null string");
		}
		if (!s.startsWith("{") || !s.startsWith("}")) {
			throw new IllegalArgumentException("Cannot deserialize string. String is not an array");
		}
		s = s.substring(1, s.length() - 1);
		return s.split(ARRAY_VALUE_SEPARATOR);
	}
// End of static
	
	private final String identifier;
	private final Map<String, Object> entries;
	
	public ObjectStorage(String identifier, Map<String, Object> entries) throws IllegalArgumentException {
		if (identifier == null) {
			throw new IllegalArgumentException("Identifier is not allowed to be null");
		}
		if (entries == null) {
			throw new IllegalArgumentException("Entry map is not allowed to be null");
		}
		this.identifier = identifier;
		this.entries = new LinkedHashMap<>(entries);
		this.entries.remove(null);
		for (Entry<String, Object> entry : new HashSet<>(this.entries.entrySet())) {
			Object value = entry.getValue();
			if (value instanceof Collection) {
				Collection<?> coll = (Collection<?>) value;
				entries.put(entry.getKey(), coll.toArray(new Object[coll.size()]));
			}
		}
	}
	
	@Override
	public String getName() {
		return getIdentifier();
	}
	
	public String getIdentifier() {
		return identifier;
	}
	
	public Object get(String key) {
		return getOrDefault(key, null);
	}
	
	public Object getOrDefault(String key, Object dflt) {
		return entries.getOrDefault(key, dflt);
	}
	
	public String[] getStringArray(String key) {
		return getStringArrayOrDefault(key, null);
	}
	
	public String[] getStringArrayOrDefault(String key, String[] dflt) {
		Object obj = get(key);
		if (obj instanceof String[]) {
			return (String[]) obj;
		}
		return dflt;
	}
	
	public boolean getBoolean(String key) {
		return getBooleanOrDefault(key, false);
	}
	
	public boolean getBooleanOrDefault(String key, boolean dflt) {
		Object obj = get(key);
		if (obj instanceof Boolean) {
			return (boolean) obj;
		}
		return dflt;
	}
	
	public byte getByte(String key) {
		return getByteOrDefault(key, (byte) 0);
	}
	
	public byte getByteOrDefault(String key, byte dflt) {
		Object obj = get(key);
		if (obj instanceof Byte) {
			return (byte) obj;
		}
		return dflt;
	}
	
	public double getDouble(String key) {
		return getDoubleOrDefault(key, 0);
	}
	
	public double getDoubleOrDefault(String key, double dflt) {
		Object obj = get(key);
		if (obj instanceof Double) {
			return (double) obj;
		}
		return dflt;
	}
	
	public int getInt(String key) {
		return getIntOrDefault(key, 0);
	}
	
	public int getIntOrDefault(String key, int dflt) {
		Object obj = get(key);
		if (obj instanceof Integer) {
			return (int) obj;
		}
		return dflt;
	}
	
	public String getString(String key) {
		return getStringOrDefault(key, null);
	}
	
	public String getStringOrDefault(String key, String dflt) {
		Object obj = get(key);
		if (obj instanceof String) {
			return (String) obj;
		}
		return dflt;
	}
	
	public void set(String key, Object value) {
		entries.put(key, value);
	}
	
	public Object remove(String key) {
		return entries.remove(key);
	}
	
	public void store(FileConfiguration data, boolean clearOld) {
		if (clearOld) {
			if (entries.isEmpty()) {
				for (String path : data.getConfigurationSection(getIdentifier()).getKeys(false)) {
					data.set(getIdentifier() + "." + path, null);
				}
			}
			else {
				data.set(getIdentifier(), null);
			}
		}
		for (Entry<String, Object> entry : entries.entrySet()) {
			data.set(getIdentifier() + "." + entry.getKey(), entry.getValue());
		}
	}
	
	public void store(Statement data, String tableName, boolean clearOld) throws SQLException {
		StringBuilder keys = new StringBuilder();
		StringBuilder values = new StringBuilder();
		Iterator<Entry<String, Object>> iter = entries.entrySet().iterator();
		while (iter.hasNext()) {
			Entry<String, Object> entry = iter.next();
			Object obj = entry.getValue();
			if (obj instanceof Object[]) {
				obj = serialize((Object[]) obj);
			}
			keys.append(entry.getKey());
			values.append(obj);
			if (iter.hasNext()) {
				keys.append(SEPARATOR);
				values.append(SEPARATOR);
			}
		}
		data.execute("INSERT INTO " + tableName + "(" + keys.toString() + ") VALUES(" + values.toString() + ")");
	}

}

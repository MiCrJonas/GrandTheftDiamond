package me.micrjonas.grandtheftdiamond.data.storage;

import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.bukkit.configuration.file.FileConfiguration;

public class ObjectStorageSet implements Iterable<ObjectStorage> {
	
	private final Map<String, ObjectStorage> entries;
	
	public static ObjectStorageSet fromSqlResult(ResultSet data) throws SQLException {
		Map<String, ObjectStorage> entries = new LinkedHashMap<>();
		ResultSetMetaData meta = data.getMetaData();
		List<String> columns = new ArrayList<>(meta.getColumnCount());
		boolean containsNameColumn = false;
		for (int i = 1; i <= meta.getColumnCount(); i++) {
			String columnName = meta.getColumnName(i);
			if (columnName.equals("name")) {
				containsNameColumn = true;
			}
			else {
				columns.add(columnName);
			}
		}
		if (!containsNameColumn) {
			throw new IllegalArgumentException("SQL result does not contain column 'name'");
		}
		while(data.next()) {
			Map<String, Object> tmpEntries = new LinkedHashMap<>();
			for (String column : columns) {
				Object obj = data.getObject(column);
				if (obj instanceof String) {
					try {
						tmpEntries.put(column, ObjectStorage.deserializeStringArray((String) obj));
					}
					catch (IllegalArgumentException ex) {
						tmpEntries.put(column, obj);
					}
				}
				else {
					tmpEntries.put(column, obj);
				}
			}
			String name = data.getString("name");
			if (name != null) {
				entries.put(name, new ObjectStorage(name, tmpEntries));
			}
		}
		return new ObjectStorageSet(entries);
	}
	
	public static ObjectStorageSet fromFileConfiguration(FileConfiguration data, boolean deep) {
		Map<String, ObjectStorage> entries = new LinkedHashMap<>();
		for (String name : data.getConfigurationSection("").getKeys(false)) {
			Map<String, Object> tmpEntries = new LinkedHashMap<>();
			for (String entry : data.getConfigurationSection(name).getKeys(deep)) {
				tmpEntries.put(entry, data.get(name + "." + entry));
			}
			entries.put(name, new ObjectStorage(name, tmpEntries));
		}
		return new ObjectStorageSet(entries);
	}
	
	private ObjectStorageSet(Map<String, ObjectStorage> entries) {
		if (entries != null) {
			this.entries = new LinkedHashMap<>(entries);
		}
		else {
			this.entries = new LinkedHashMap<>();
		}
	}
	
	public ObjectStorage get(String name) {
		return entries.get(name);
	}
	
	public Set<Entry<String, ObjectStorage>> entrySet() {
		return new LinkedHashSet<>(entries.entrySet());
	}

	@Override
	public Iterator<ObjectStorage> iterator() {
		return entries.values().iterator();
	}

}

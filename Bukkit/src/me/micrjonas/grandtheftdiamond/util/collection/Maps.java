package me.micrjonas.grandtheftdiamond.util.collection;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

/**
 * Contains some utility methods for {@link Map} related classes
 */
public class Maps {
	
	private Maps() { /* private constructor, no instance possible */ }
	
	
	/**
	 * Returns a new sorted {@code LinkedHashMap} with the elements of {@code map}. The method sorts the
	 * 	elements by its value
	 * @param map The map to sort
	 * @return The new and sorted {@code LinkedHashMap}
	 * @throws IllegalArgumentException Thrown if {@code map} is {@code null}
	 */
	public static <K, V extends Comparable<V>> LinkedHashMap<K, V> getSortedMap(Map<K, V> map) throws IllegalArgumentException {
		if (map == null) {
			throw new IllegalArgumentException("Map is not allowed to be null");
		}
		List<Map.Entry<K, V>> entries = new ArrayList<>(map.entrySet());
		Collections.sort(entries, new Comparator<Map.Entry<K, V>>() {
			@Override
			public int compare(Map.Entry<K, V> entry0, Map.Entry<K, V> entry1) {
				return entry0.getValue().compareTo(entry1.getValue());
			}
		});
		LinkedHashMap<K, V> sortedMap = new LinkedHashMap<>(entries.size());
		for (Entry<K, V> entry : entries) {
			sortedMap.put(entry.getKey(), entry.getValue());
		}
		return sortedMap;
	}

}

package me.micrjonas.grandtheftdiamond.util;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

/**
 * Util class for {@link Nameable}
 */
public class Nameables {
	
	private Nameables() { } 
	
	public static String nameablesAsString(String seperator, Nameable... nameables) {
		return nameablesAsString(Arrays.asList(nameables), seperator);
	}
	
	/**
	 * Creates a {@code String} containing all names separated by {@code separator}
	 * A returned {@code String} with {@code ", "} as separator may look like this: {@code "Obj1, Obj2"}
	 * @param nameables The {@link Nameable}s to list
	 * @param separator The separator of an element
	 * @return A {@code String} containing all names separated by {@code separator}
	 */
	public static String nameablesAsString(Collection<? extends Nameable> nameables, String separator) {
		StringBuilder list = new StringBuilder();
		Iterator<? extends Nameable> iterator = nameables.iterator();
		while (iterator.hasNext()) {
			list.append(iterator.next().getName());
			if (iterator.hasNext()) {
				list.append(separator);
			}
		}
		return list.toString();
	}
	
	/**
	 * Returns a {@code List} with the names of all {@code nameables}
	 * @param nameables The {@link Nameable}s
	 * @return A {@code List} with the names of all {@code nameables}. The names are sorted alphabetic
	 */
	public static List<String> getNameList(Collection<? extends Nameable> nameables) {
		List<String> names = new ArrayList<>();
		for (Nameable object : nameables) {
			names.add(object.getName());
		}
		Collections.sort(names);
		return names;
	}

}

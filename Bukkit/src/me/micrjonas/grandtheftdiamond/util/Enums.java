package me.micrjonas.grandtheftdiamond.util;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.bukkit.configuration.ConfigurationSection;

/**
 * Contains some utilities for {@link Enum}s
 */
public class Enums {
	
	private Enums() { /*private constructor*/ }
	
	public static <T extends Enum<T>> List<T> getEnumListFromConfig(Class<T> enumType, ConfigurationSection config, String path) {
		if (config.isList(path)) {
			List<T> enums = new ArrayList<>();
			for (String listEnum : config.getStringList(path)) {
				T enumm = valueOf(enumType, listEnum);
				if (enumm != null) {
					enums.add(enumm);
				}
			}
			return enums;
		}
		return null;
	}
	
	public static <T extends Enum<T>> T getEnumFromConfig(Class<T> enumType, ConfigurationSection config, String path) {
		return valueOf(enumType, config.getString(path));
	}
	
	public static <T extends Enum<T>> T valueOf(Class<T> enumType, String value) {
		if (value == null) {
			return null;
		}
		try {
			return Enum.valueOf(enumType, value.toUpperCase().replace('-', '_'));
		}
		catch (IllegalArgumentException ex) {
			return null;
		}
	}
	
	public static <T extends Enum<T>> List<String> namesAsList(Class<T> enumType) {
		List<String> names = new ArrayList<>();
		for (T elem : enumType.getEnumConstants()) {
			names.add(elem.name());
		}
		Collections.sort(names);
		return names;
	}
	
	public static <T extends Enum<T>> String nameToMessageString(T value) {
		return value.name().replace('_', ' ').toLowerCase();
	}
	
	public static <T extends Enum<?>> List<String> nameList(Class<T> enumType) {
		T[] enums = enumType.getEnumConstants();
		List<String> names = new ArrayList<>(enums.length);
		for (T value : enums) {
			names.add(value.name());
		}
		return names;
	}
	
}

package me.micrjonas.grandtheftdiamond.data.storage;

import java.util.Map;

import me.micrjonas.grandtheftdiamond.util.Nameable;

/**
 * A class which represents an object you can store to a file implement this interface
 */
public interface Storable extends Nameable {
	
	/**
	 * Returns all data as Map
	 * @return String is the sub path, Object is the object to store at the specific path
	 */
	public Map<String, Object> getStoreData();

}

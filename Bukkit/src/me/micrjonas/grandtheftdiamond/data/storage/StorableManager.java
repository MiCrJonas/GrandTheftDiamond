package me.micrjonas.grandtheftdiamond.data.storage;

import me.micrjonas.grandtheftdiamond.manager.Manager;

import org.bukkit.configuration.file.FileConfiguration;

/**
 * All classes which manage storable plugin elements implement this interface
 * @param <T> The type which gets managed by the implementing manager
 */
public interface StorableManager<T extends Storable> extends Manager<T> {
	
	/**
	 * Loads all objects from the data file
	 * @param dataFile The file where the data of the objects is stored
	 */
	public void loadObjects(FileConfiguration dataFile);
	
}

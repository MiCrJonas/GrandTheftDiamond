package me.micrjonas.grandtheftdiamond.util;

import me.micrjonas.grandtheftdiamond.manager.Manager;


/**
 * Represents an object which can be destroyed/invalid
 */
public interface Removeable {
	
	/**
	 * Unregisters the object from it's {@link Manager} and makes it invalid.<br>
	 * Invalid/removed objects may not work right
	 */
	void remove();
	
	/**
	 * Checks whether the object is destroyed/invalid
	 * @return True if the object is destroyed/unused, else {@code false}
	 */
	boolean isRemoved();
	
}

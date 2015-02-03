package me.micrjonas.grandtheftdiamond.util;

/**
 * Classes with objects which have a name implement this interface
 */
public interface Nameable {
	
	/**
	 * Returns the name of the object
	 * @return The object's name. Not allowed to be {@code null}
	 */
	public abstract String getName();

}

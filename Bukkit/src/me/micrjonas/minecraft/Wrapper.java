package me.micrjonas.minecraft;


/**
 * An object which is used as bridge to the server software
 */
public interface Wrapper {
	
	/**
	 * Returns the server-used object handle
	 * @return The server-used handle. Not allowed to be {@code null}
	 */
	Object getHandle();
	
}

package me.micrjonas.grandtheftdiamond.manager;

import java.util.Collection;
import java.util.Collections;

/**
 * Interface to mark classes which manage a specific type of object
 * @param <T> The type to manage
 */
public interface Manager<T> {
	
	/**
	 * Returns a {@link Collection} with all registered objects of the manager's type.<br>
	 * 	The {@link Collection} is immutable and a "read-only"-representation of the intern-used {@code Collection}.
	 * 	Trying to edit the returned {@code Collection} will throw an {@link UnsupportedOperationException}.<br>
	 * 	The returned {@link Collection} is related to the original intern {@link Collection} for its whole
	 * 	livetime. Changes in the original {@link Collection} are transmitted to the returned {@link Collection}.<br>
	 * 	The method does <i>not</i> return the original/intern {@link Collection}.<br>
	 * 	It's recommended to use {@link Collections#unmodifiableCollection(Collection)} to realize.
	 * @return A {@code Collection} with all registered objects of the manager's type.
	 * @see Collections#unmodifiableCollection(Collection)
	 */
	public Collection<T> getAllObjects();
	
}

package me.micrjonas.grandtheftdiamond.arena;

import java.util.Collection;
import java.util.Collections;
import java.util.List;


/**
 * Thrown to indicate that an {@link Arena} some method would create, intersects an other {@code Arena}
 */
public class ArenaIntersectsException extends IllegalArgumentException {

	private static final long serialVersionUID = 4737892723361730606L;
	
	private final List<Arena> intersects = null;
	
	/**
	 * Creates a new {@code Exception} without a message
	 */
	public ArenaIntersectsException() { }
	
	/**
	 * @param message The {@link Exception}'s message
	 */
	public ArenaIntersectsException(String message) {
		super(message);
	}
	
	/**
	 * @param intersects A {@code Collection} containing all {@link Arena}s which intersect with a new creating {@link Arena}
	 */
	public ArenaIntersectsException(Collection<Arena> intersects) {
		this(null, intersects);
	}
	
	/**
	 * @param message The {@link Exception}'s message
	 * @param intersects A {@code Collection} containing all {@link Arena}s which intersect with a new creating {@link Arena}
	 */
	public ArenaIntersectsException(String message, Collection<Arena> intersects) {
		super(message);
		if (intersects != null) {
			intersects = Collections.unmodifiableCollection(intersects);
		}
	}
	
	/**
	 * Returns a Collection of all intersecting {@code Arena}s
	 * @return A Collection of all intersecting {@code Arena}s. May be null
	 */
	public Collection<Arena> getIntersects() {
		return intersects;
	}

}

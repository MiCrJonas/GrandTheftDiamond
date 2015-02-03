package me.micrjonas.grandtheftdiamond.util.collection;

import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Contains some utility methods for {@link Collection} related classes
 */
public class Collections {
	
	private Collections() { /* private constructor, no instance possible */ }
	
	/**
	 * Returns a random element of {@code c}
	 * @param c The involved Collection
	 * @return A random element of {@code c} with the generic type of c
	 */
	public static <E> E getRandomElement(Collection<E> c) {
		int index = (int) (Math.random() * c.size());
		if (c instanceof List) {
			return ((List<E>) c).get(index);
		}
		int i = 0;
		for (E elem : c) {
		    if (i == index) {
		        return elem;
		    }
		    i++;
		}
		return null; // Never reached
	}
	
	/**
	 * Returns a new {@link Set} (implemented as {@link HashSet}) with all elements of {@code elems}. The new
	 * 	{@link Set} has the same size as {@code elems}
	 * @param elems A {@code Collection} with all elements which should be added to the new {@link Set}. If
	 *  {@code null}, an empty {@link Set} will be returned
	 * @return The new {@code Set}
	 */
	public static <E> Set<E> filledSet(Collection<E> elems) {
		if (elems != null) {
			Set<E> toFill = new HashSet<E>(elems.size());
			toFill.addAll(elems);
			return toFill;
		}
		return new HashSet<E>(0);
	}
	
	/**
	 * Copies all non-{@code null} elements from {@code from} to {@code to}
	 * @param from The element's source
	 * @param to The element's target
	 * @throws IllegalArgumentException Thrown if {@code from} or {@code to} is {@code null}
	 */
	public static <T> void addNonNulls(Collection<? extends T> from, Collection<T> to) throws IllegalArgumentException {
		if (from == null) {
			throw new IllegalArgumentException("From Collection is not allowed to be null");
		}
		if (to == null) {
			throw new IllegalArgumentException("To Collection is not allowed to be null");
		}
		for (T elem : from) {
			if (elem != null) {
				to.add(elem);
			}
		}
	}

}

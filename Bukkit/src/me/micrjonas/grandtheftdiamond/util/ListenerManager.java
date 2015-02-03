package me.micrjonas.grandtheftdiamond.util;

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;


/**
 * An util class for all sub classes which can register a {@link Listener}
 * @param <K> The key of the listener
 * @param <T> The {@link Listener} type
 */
public abstract class ListenerManager<K, T extends Listener> {
	
	private final Map<K, T> listeners = new HashMap<K, T>();
	
	/**
	 * Default constructor
	 */
	protected ListenerManager() { }
	
	/**
	 * Registers a {@link Listener} with a key. Replaces a {@link Listener} if it is already associated
	 * 	with the {@code key}
	 * @param key The {@link Listener}'s key
	 * @param listener The {@link Listener} to register
	 * @param additionalKeys Additional other keys for the same {@link Listener}
	 * @throws IllegalArgumentException Thrown if {@code key} or {@code listener} is {@code null}
	 */
	public void registerListener(K key, T listener) throws IllegalArgumentException {
		if (key == null) {
			throw new IllegalArgumentException("Key is not allowed to be null");
		}
		if (listener == null) {
			throw new IllegalArgumentException("Listener to register is not allowed to be null");
		}
		listeners.put(key, listener);
	}
	
	/**
	 * Unregisters a {@link Listener} and all its keys. The method uses the '==' operator to find the keys to
	 * 	the {@link Listener} to unregister
	 * @param listener the {@link Listener} to unregister
	 * @throws IllegalArgumentException Thrown if {@code listener} is {@code null}
	 */
	public void unregisterListener(T listener) throws IllegalArgumentException {
		if (listener == null) {
			throw new IllegalArgumentException("Listener to unregister is not allowed to be null");
		}
		synchronized (listeners) {
			for (Iterator<Entry<K, T>> iter = listeners.entrySet().iterator(); iter.hasNext(); ) {
				Entry<K, T> next = iter.next();
				if (next.getValue() == listener) {
					iter.remove();
				}
			}	
		}
	}
	
	/**
	 * Returns a registered {@link Listener} by its {@code key}. The method calls {@link Object#hashCode()} and
	 *  {@link Object#equals(Object)} on the {@code key} to find its {@link Listener}
	 * @param key The {@link Listener}'s key
	 * @return The registered {@link Listener}, {@code null} if no {@link Listener} is registered by the key
	 * @throws IllegalArgumentException Thrown if {@code key} is {@code null}
	 */
	public T getListener(K key) throws IllegalArgumentException {
		if (key == null) {
			throw new IllegalArgumentException("Key is not allowed to be null");
		}
		return listeners.get(key);
	}
	
	/**
	 * Returns a "read-only" {@link Collection} with all registered {@link Listeners}
	 * @return A "read-only" {@link Collection} with all registered {@link Listeners}. Throws an
	 * 	{@link UnsupportedOperationException} when you try to edit the returned {@link Collection}
	 */
	public Collection<T> getRegisteredListeners() {
		return Collections.unmodifiableCollection(listeners.values());
	}
	
}

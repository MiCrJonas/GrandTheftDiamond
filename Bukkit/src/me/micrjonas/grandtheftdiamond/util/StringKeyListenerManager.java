package me.micrjonas.grandtheftdiamond.util;


/**
 * A {@link ListenerManager} specified for {@link String}s as key. Uses {@code String#equalsIgnoreCase(String)}
 * 	to get a {@link Listener} by its key
 * @param <T> The {@link Listener} type
 */
public abstract class StringKeyListenerManager<T extends Listener> extends ListenerManager<String, T> {
	
	/**
	 * Default constructor. Protected visibility
	 */
	protected StringKeyListenerManager() { }
	
	@Override
	public void registerListener(String key, T listener) {
		if (key == null) {
			throw new IllegalArgumentException("Key is not allowed to be null");
		}
		super.registerListener(key.toLowerCase(), listener);
	}
	
	@Override
	public T getListener(String key) throws IllegalArgumentException {
		if (key == null) {
			throw new IllegalArgumentException("Key is not allowed to be null");
		}
		return super.getListener(key.toLowerCase());
	}
	
}

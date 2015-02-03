package me.micrjonas.grandtheftdiamond.pluginsupport;

import org.bukkit.plugin.Plugin;

/**
 * Interface for managers which manage the support of other {@link Plugin}s. If {@link #isEnabled()} returns {@code false},
 * 	then all other methods of the implementing class return {@code null} except a static {@code getInstance()-method} (if implemented).
 * 	No method does throw any {@link Exception} of the basis of a disabled {@code Plugin}
 * @param <T> The plugin's class
 */
public interface PluginSupport<T extends Plugin> {
	
	/**
	 * Returns whether the plugin support is enabled
	 * @return True, if the support is enabled, else {@code false}
	 */
	boolean isEnabled();
	
	/**
	 * Returns the associated {@link Plugin}
	 * @return The associated plugin. May be {@code null} if {@link #isEnabled()} returns {@code false}
	 */
	T getPlugin();

}

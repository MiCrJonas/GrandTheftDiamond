package me.micrjonas.grandtheftdiamond.item.pluginitem;

import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.event.player.PlayerInteractEvent;

/**
 * Represents a {@link PluginItem} with an action on interacting
 */
public interface InteractablePluginItem extends PluginItem {
	
	/**
	 * Called when a {@code Player} interacts with the item. No permission check is required
	 * @param e The bukkit-fired event
	 * @return True if the {@code Player} is allowed to use the interacted item, else false
	 */
	public boolean onInteract(PlayerInteractEvent e);
	
	/**
	 * Called when a {@code Player} interacts with the item and clicks an {@code Entity}. No permission check is required
	 * @param e The bukkit-fired event
	 * @return True if the {@code Player} is allowed to use the interacted item, else false
	 */
	public boolean onEntityInteract(PlayerInteractEntityEvent e);

}
package me.micrjonas.grandtheftdiamond.api.event;

import me.micrjonas.grandtheftdiamond.api.event.player.CancellablePlayerEvent;

import org.bukkit.Bukkit;
import org.bukkit.event.Cancellable;

/**
 * Super class of all cancellable events
 */
public abstract class CancellableEvent extends GrandTheftDiamondEvent implements Cancellable {
	
// Start of static
	/**
	 * Fires the event
	 * @param e The event to fire
	 * @return True if event was not cancelled, else false
	 */
	public static boolean fireEvent(CancellablePlayerEvent e) {
		Bukkit.getPluginManager().callEvent(e);
		return !e.isCancelled();
	}
	
	/**
	 * Fires the event
	 * @param e The involved event
	 * @return True if event was not cancelled, else false
	 */
	public static boolean fireEvent(CancellableEvent e) {
		Bukkit.getPluginManager().callEvent(e);
		return !e.isCancelled();
	}
//End of static
	
	private boolean cancelled;
	
	/**
	 * Creates a new {@code Event} which is not cancelled by default
	 */
	protected CancellableEvent() {
		this(false);
	}
	
	/**
	 * @param isCancelled Whether the {@code Event} is cancelled by default
	 */
	protected CancellableEvent(boolean isCancelled) {
		cancelled = isCancelled;
	}
	
	@Override
	public boolean isCancelled() {
		return cancelled;
	}
	
	@Override
	public void setCancelled(boolean newCancelled) {
		cancelled = newCancelled;
	}

}

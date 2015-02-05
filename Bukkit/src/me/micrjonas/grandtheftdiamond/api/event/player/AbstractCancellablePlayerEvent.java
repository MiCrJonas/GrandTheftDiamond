package me.micrjonas.grandtheftdiamond.api.event.player;

import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;

/**
 * Super class of all cancellable player events
 */
public abstract class AbstractCancellablePlayerEvent extends AbstractPlayerEvent implements Cancellable {

	private boolean cancelled;
	
	/**
	 * Creates a new instance of the event which is not cancelled by default
	 * @param who The involved Player
     * @throws IllegalArgumentException Thrown if {@code who} is {@code null}
	 */
	protected AbstractCancellablePlayerEvent(Player who) throws IllegalArgumentException {
		this(who, false);
	}
	
	/**
	 * @param who The involved Player
	 * @param isCancelled Sets the default cancellation state of this event
     * @throws IllegalArgumentException Thrown if {@code who} is {@code null}
	 */
	protected AbstractCancellablePlayerEvent(Player who, boolean isCancelled) throws IllegalArgumentException {
		super(who);
		cancelled = isCancelled;
	}
	
	/**
	 * Returns the cancellation state of this event.
	 * @return True if the event is currently cancelled, else false
	 */
	@Override
	public boolean isCancelled() {
		return cancelled;
	}

	/**
	 * Sets the cancellation state of this event.
	 * @param newCancelled True if you wish to cancel this event
	 */
	@Override
	public void setCancelled(boolean newCancelled) {
		cancelled = newCancelled;
	}

}

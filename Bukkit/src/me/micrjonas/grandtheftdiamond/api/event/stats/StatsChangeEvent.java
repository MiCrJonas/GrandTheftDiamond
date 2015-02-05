package me.micrjonas.grandtheftdiamond.api.event.stats;

import me.micrjonas.grandtheftdiamond.api.event.player.AbstractCancellablePlayerEvent;

import org.bukkit.entity.Player;


/**
 * Super class of all events which get fired when some {@link Player}'s stats change
 */
public abstract class StatsChangeEvent extends AbstractCancellablePlayerEvent {
	
	private final int before;
	private int after;
	
	/**
	 * Default constructor
	 * @param p The {@link Player} to change stats
	 * @param before The stats value before this event
	 * @param after The stats value after this event
	 */
	protected StatsChangeEvent(Player p, int before, int after) {
		super(p);
		this.before = before;
		this.after = after;
	}
	
	/**
	 * Checks whether the passed value is valid for the event's stats type.<br>
	 * E.g.: A wanted level less than 0 is not allowed, a money balance below 0 is allowed
	 * @param value The value to check
	 * @return True if the value is valid, else {@code false}
	 */
	public abstract boolean isValidValue(int value);
	
	/**
	 * Returns the stats value before this event
	 * @return The stats value before this event
	 */
	public int getBefore() {
		return before;
	}
	
	/**
	 * Returns the stats value after this event
	 * @return The stats value after this event
	 */
	public int getAfter() {
		return after;
	}
	
	/**
	 * Sets the new stats value of the {@link Player}
	 * @param value The new stats value after this event
	 * @throws IllegalArgumentException Thrown if {@link #isValidValue(int)} returns {@code false} for {@code value}
	 */
	public void setAfter(int value) throws IllegalArgumentException {
		if (!isValidValue(value)) {
			throw new IllegalArgumentException("Value not valid: " + value);
		}
		after = value;
	}
	
}

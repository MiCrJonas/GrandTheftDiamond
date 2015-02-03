package me.micrjonas.grandtheftdiamond.api.event.stats;

import org.bukkit.entity.Player;


/**
 * Fired when the wanted level of a {@link Player} changes
 */
public class WantedLevelChangeEvent extends StatsChangeEvent {

	/**
	 * Default constructor
	 * @param p The {@link Player} to change stats
	 * @param before The stats value before this event
	 * @param after The stats value after this event
	 */
	public WantedLevelChangeEvent(Player p, int before, int after) {
		super(p, before, after);
	}

	@Override
	public boolean isValidValue(int value) {
		return value >= 0;
	}
}

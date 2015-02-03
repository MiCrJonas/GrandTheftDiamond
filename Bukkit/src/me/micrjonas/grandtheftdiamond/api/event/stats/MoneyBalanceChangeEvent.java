package me.micrjonas.grandtheftdiamond.api.event.stats;

import org.bukkit.entity.Player;


/**
 * Fired when the money balance of a {@link Player} changes
 */
public class MoneyBalanceChangeEvent extends StatsChangeEvent {

	/**
	 * Default constructor
	 * @param p The {@link Player} to change stats
	 * @param before The balance before this event
	 * @param after The balance after this event
	 */
	public MoneyBalanceChangeEvent(Player p, int before, int after) {
		super(p, before, after);
	}

	@Override
	public boolean isValidValue(int value) {
		return true;
	}
	
}

package me.micrjonas.grandtheftdiamond.stats.board;

import java.util.EnumMap;
import java.util.Map;

import me.micrjonas.grandtheftdiamond.stats.StatsManager;
import me.micrjonas.grandtheftdiamond.stats.StatsType;

import org.bukkit.entity.Player;
import org.bukkit.scoreboard.DisplaySlot;

public class PlayerStatsBoard extends StatsBoard {
	
	private final Player p;
	private final Map<StatsType, String> shownStats;
	
	/**
	 * Creates a new PlayerStatsBoard
	 * @param p The player of the board
	 * @param name The name of the board
	 * @param shownStats All shown stats, map key is the stats type, value is the shown name of the stats type
	 */
	public PlayerStatsBoard(Player p, String name, Map<StatsType, String> shownStats) {
		super(name, DisplaySlot.SIDEBAR);
		this.p = p;
		shownStats.remove(null);
		this.shownStats = new EnumMap<>(shownStats);
	}
	
	/**
	 * Returns the player of this board
	 * @return The board's player
	 */
	public Player getPlayer() {
		return p;
	}
	
	/**
	 * Updates the stats if registered
	 * @param type The stats to update
	 * @param score The new score
	 * @return True if the stats type was registered and updated, else false
	 */
	public boolean updateStats(StatsType type, int score) {
		if (shownStats.containsKey(type)) {
			getObjective().getScore(shownStats.get(type)).setScore(score);
			return true;
		}
		return false;
	}
	
	
	/**
	 * Updates all stats
	 */
	public void updateAll() {
		for (StatsType type : shownStats.keySet()) {
			updateStats(type, StatsManager.getInstance().getStats(type).getValue(p));
		}
	}
	
}

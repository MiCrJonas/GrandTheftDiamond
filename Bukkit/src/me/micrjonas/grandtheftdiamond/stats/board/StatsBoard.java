package me.micrjonas.grandtheftdiamond.stats.board;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import me.micrjonas.grandtheftdiamond.GrandTheftDiamond;
import me.micrjonas.grandtheftdiamond.data.player.PlayerDataUser;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.DisplaySlot;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Scoreboard;

public class StatsBoard implements PlayerDataUser {
	
	private final Scoreboard handle;
	private final DisplaySlot slot;
	private final Map<UUID, Scoreboard> latestBoards = new HashMap<>();
	
	protected StatsBoard(String name, DisplaySlot slot) {
		GrandTheftDiamond.registerPlayerDataUser(this);
		handle = Bukkit.getScoreboardManager().getNewScoreboard();
		Objective obj = handle.registerNewObjective(name, "Criteria?");
		obj.setDisplaySlot(slot);
		this.slot = slot;
	}
	
	@Override
	public void clearPlayerData(UUID player) {
		latestBoards.remove(player);
	}

	@Override
	public void loadPlayerData(UUID player) { /* Nothing to do */}
	
	/**
	 * Shows the score board for the player
	 * @param p The player to show
	 */
	public void showScoreboard(Player p) {
		p.setScoreboard(handle);
	}
	
	/**
	 * Removes the score board from the player, does nothing if the player doesn't see this score board
	 * @param p The player to remove
	 * @param showLatest If true and if the player had another score board before, the score board before will be shown
	 */
	public void unshowScoreboard(Player p, boolean showLatest) {
		if (p.getScoreboard() == getHandle()) {
			if (showLatest) {
				p.setScoreboard(latestBoards.get(p.getUniqueId()));
			}
			else {
				p.setScoreboard(null);
			}
		}
	}
	
	/**
	 * Returns the represented score board
	 * @return The represented score board
	 */
	public Scoreboard getHandle() {
		return handle;
	}
	
	/**
	 * Returns the objective used by the board
	 * @return The used objective
	 */
	protected Objective getObjective() {
		return handle.getObjective(getDisplaySlot());
	}
	
	/**
	 * Returns the used display slot
	 * @return The used display slot
	 */
	protected DisplaySlot getDisplaySlot() {
		return slot;
	}

}

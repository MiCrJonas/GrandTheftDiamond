package me.micrjonas.grandtheftdiamond.stats.board;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.UUID;

import me.micrjonas.grandtheftdiamond.GrandTheftDiamond;
import me.micrjonas.grandtheftdiamond.api.event.player.PlayerStatsChangeEvent;
import me.micrjonas.grandtheftdiamond.bukkit.BukkitGrandTheftDiamondPlugin;
import me.micrjonas.grandtheftdiamond.data.FileReloadListener;
import me.micrjonas.grandtheftdiamond.data.PluginFile;
import me.micrjonas.grandtheftdiamond.data.TemporaryPluginData;
import me.micrjonas.grandtheftdiamond.stats.StatsType;

import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;

public class StatsBoardManager implements FileReloadListener, Listener {
	
// Start of static
	private static final StatsBoardManager instance = new StatsBoardManager();
	
	public static StatsBoardManager getInstance() {
		return instance;
	}
// End of static

	private boolean use;
	private String boardTitle;
	private final Map<StatsType, String> shownStats = new LinkedHashMap<>();
	private final Map<UUID, PlayerStatsBoard> statsBoards = new HashMap<>();
	
	private StatsBoardManager() {
		Bukkit.getPluginManager().registerEvents(this, BukkitGrandTheftDiamondPlugin.getInstance());
		GrandTheftDiamond.registerFileReloadListener(this);
	}
	
	@Override
	public void configurationReloaded(PluginFile file, FileConfiguration fileConfiguration) {
		if (file == PluginFile.CONFIG) {
			use = fileConfiguration.getBoolean("scoreboard.stats.use");
			shownStats.clear();
			statsBoards.clear();
			if (use) {
				boardTitle = fileConfiguration.getString("scoreboard.stats.title");
				for (String path : fileConfiguration.getConfigurationSection("scoreboard.stats.shownStats").getKeys(false)) {
					try {
						shownStats.put(StatsType.valueOf(path), fileConfiguration.getString("scoreboard.stats.shownStats." + path));
					}
					catch (IllegalArgumentException ex) {
						fileConfiguration.set("scoreboard.stats.shownStats." + path, "INVALID VALUE");
					}
				}
				for (Player p : TemporaryPluginData.getInstance().getIngamePlayers()) {
					show(p);
				}
			}
			else {
				for (UUID playerId : statsBoards.keySet()) {
					unshow(Bukkit.getPlayer(playerId));
				}
			}
		}
	}
	
	public void show(Player p) {
		if (use) {
			PlayerStatsBoard playerBoard = new PlayerStatsBoard(p, boardTitle, shownStats);
			playerBoard.updateAll();
			playerBoard.showScoreboard(p);
			statsBoards.put(p.getUniqueId(), playerBoard);
		}
	}
	
	public void unshow(Player p) {
		if (use) {
			PlayerStatsBoard playerBoard = statsBoards.get(p.getUniqueId());
			if (playerBoard != null && p.getScoreboard() == playerBoard.getHandle()) {
				playerBoard.unshowScoreboard(p, true);
				statsBoards.remove(p.getUniqueId());
			}
		}
	}
	
	/*public void unshowAll() {
		for (Entry<UUID, PlayerStatsBoard> entry : statsBoards.entrySet()) {
			statsBoards.remove(entry.getKey());
			entry.getValue().unshowScoreboard(entry.getValue().getPlayer(), true);
		}
	}*/
	
	/**
	 * @deprecated Do not call
	 */
	@Deprecated
	@EventHandler (priority = EventPriority.MONITOR, ignoreCancelled = true)
	public void onStatsChange(PlayerStatsChangeEvent e) {
		if (use) {
			Player p = e.getPlayer();
			PlayerStatsBoard playerBoard = statsBoards.get(p.getUniqueId());
			if (playerBoard != null) {
				if (p.getScoreboard() == playerBoard.getHandle()) {
					playerBoard.updateStats(e.getChangedStats(), e.getNewValue());
				}
				else {
					statsBoards.remove(p.getUniqueId());
				}
			}
		}
	}
	
}

package me.micrjonas.grandtheftdiamond.stats;

import java.io.File;
import java.util.EnumMap;
import java.util.Map;
import java.util.UUID;

import me.micrjonas.grandtheftdiamond.GrandTheftDiamond;
import me.micrjonas.grandtheftdiamond.data.TemporaryPluginData;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;

public class StatsManager {
	
	private static StatsManager instance = new StatsManager();
	
	public static StatsManager getInstance() {
		
		return instance;
		
	}

	private Map<StatsType, Stats> stats = null;
	
	private StatsManager() {
		loadData();	
	}
	
	
	private void loadData() {
		if (!loaded()) {
			GrandTheftDiamond.runTaskAsynchronously(new Runnable() {
				@Override
				public void run() {
					Map<StatsType, Stats> stats = new EnumMap<>(StatsType.class);
					for (StatsType type : StatsType.values()) {
						stats.put(type, new Stats(type));
					}
					for (File f : new File(GrandTheftDiamond.getDataFolder() + File.separator + "userdata").listFiles()) {
						if (f.getName().endsWith(".yml")) {
							FileConfiguration data = YamlConfiguration.loadConfiguration(f);
							if (data.isString("lastName")) {
								UUID playerId;
								try {
									playerId = UUID.fromString(f.getName().substring(0, f.getName().length() - 4));
								}
								catch (IllegalArgumentException ex) {
									continue;
								}
								for (StatsType type : StatsType.values()) {
									stats.get(type).set(playerId, data.getInt("stats." + type.name()));
								}
							}
						}
					}
					StatsManager.this.stats = stats;
				}
			});
		}	
	}
	
	
	public void saveStats() {
		
		
		
	}
	
	
	public boolean loaded() {
		return stats != null;
	}
	
	
	public Stats getStats(StatsType type) {
		return stats.get(type);
	}
	
	
	public Map<StatsType, Integer> getStats(UUID playerId) {
		Map<StatsType, Integer> stats = new EnumMap<>(StatsType.class);
		for (StatsType type : StatsType.values()) {
			stats.put(type, getStats(playerId, type));
		}
		return null;
	}
	
	
	public Map<StatsType, Integer> getStats(Player p) {
		return getStats(p.getUniqueId());
	}
	
	
	public int getStats(Player p, StatsType type) {
		return getStats(p.getUniqueId(), type);
	}
	
	
	public int getStats(UUID playerId, StatsType type) {
		return stats.get(type).getValue(playerId);
	}
	
	
	void update(Player p, StatsType type, int value) {
		if (TemporaryPluginData.getInstance().isIngame(p)) {
			switch (type) {
				case WANTED_LEVEL: {
					p.setLevel(value);
					getStats(StatsType.TOTAL_WANTED_LEVEL).set(p, getStats(StatsType.TOTAL_WANTED_LEVEL).getValue(p) + value);
				} break;
				default:
					break;
			}
		}
	}

}

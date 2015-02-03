package me.micrjonas.grandtheftdiamond.stats;

import java.util.AbstractMap.SimpleEntry;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

public class Stats {
	
	private final StatsType type;
	
	private final List<Entry<UUID, Integer>> rankValues = new ArrayList<>();
	private final Map<UUID, Integer> ranks = new HashMap<>();
	
	public Stats(StatsType type) {
		
		this.type = type;
		
	}
	
	public void set(UUID playerId, int value) {
		
		if (!ranks.containsKey(playerId)) {
			
			int rank = calculateRank(value);
			
			rankValues.add(rank, new SimpleEntry<UUID, Integer>(playerId, value));
			ranks.put(playerId, rank);
			
		}
		
		else {
			
			int rank = ranks.get(playerId);
			Entry<UUID, Integer> entry = rankValues.get(rank);
			entry.setValue(value);
			
			rankValues.remove(rank); 
			
			rankValues.add(calculateRank(value), entry);
			ranks.put(playerId, rank);
			
		}
		
		StatsManager.getInstance().update(Bukkit.getPlayer(playerId), type, value);
		
	}
	
	
	public void set(Player p, int value) {
		
		set(p.getUniqueId(), value);
		
	}
	
	
	public void add(UUID playerId, int value) {
		
		set(playerId, getValue(playerId) + value);
		
	}
	
	
	public void add(Player p, int value) {
		
		add(p.getUniqueId(), value);
		
	}
	
	
	public int getRank(UUID playerId) {
		Integer rank = ranks.get(playerId);
		if (rank == null) {
			return -1;
		}
		return rank + 1;
	}
	
	
	public int getRank(Player p) {
		return getRank(p.getUniqueId());
	}
	
	
	public int getValue(UUID playerId) {
		int rank = getRank(playerId);
		if (rank > 0) {
			return rankValues.get(rank - 1).getValue();
		}
		return 0;
	}
	
	
	public int getValue(Player p) {
		return getValue(p.getUniqueId());
	}
	
	
	public List<Entry<UUID, Integer>> getRanks(int start, int end) {
		if (start < 1) {
			throw new IndexOutOfBoundsException("Rank: " + start + ", Size: " + rankValues.size());
		}
		if (end > rankValues.size()) {
			throw new IndexOutOfBoundsException("Rank: " + end + ", Size: " + rankValues.size());
		}
		if (start > end) {
			throw new IllegalArgumentException("start (" + start + ") cannot be > end (" + end + ")");
		}
		List<Entry<UUID, Integer>> ranks = new ArrayList<>();
		for (int rank = start - 1; rank < end; rank++) {
			ranks.add(new SimpleEntry<UUID, Integer>(rankValues.get(rank).getKey(), rankValues.get(rank).getValue()));
		}
		return ranks;
	}
	
	
	public int getRankCount() {
		return rankValues.size();
	}

	
	private int calculateRank(int value) {
		
		if (rankValues.size() == 0)
			return 0;
		
		if (value >= rankValues.get(0).getValue())
			return 0;
		
		if (value <= rankValues.get(rankValues.size() - 1).getValue())
			return rankValues.size();
		
		int rank = 1;
		
		while (rankValues.get(rank).getValue() > value)
			rank++;
		
		return rank;
		
	}
	
}

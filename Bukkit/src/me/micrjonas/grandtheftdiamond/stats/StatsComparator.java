package me.micrjonas.grandtheftdiamond.stats;

import java.util.Comparator;
import java.util.Map.Entry;
import java.util.UUID;

public class StatsComparator implements Comparator<Entry<UUID, Integer>> {

	@Override
	public int compare(Entry<UUID, Integer> arg0, Entry<UUID, Integer> arg1) {
		
		return arg0.getValue().compareTo(arg1.getValue());
		
	}

}

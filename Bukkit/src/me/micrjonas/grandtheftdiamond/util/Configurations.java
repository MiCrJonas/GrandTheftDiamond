package me.micrjonas.grandtheftdiamond.util;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.bukkit.configuration.file.FileConfiguration;

public class Configurations {
	
	private Configurations() { }
	
	@SuppressWarnings("unchecked")
	public static List<Map<String, Object>> sectionToMapList(FileConfiguration config, String path) {
		
		if (!config.isConfigurationSection(path)) {
			
			if (config.isList(path) 
					&& config.getList(path).size() > 0
					&& config.getList(path).get(0) instanceof Map)
				return (List<Map<String, Object>>) config.getList(path);
			
			else
				return null;
			
		}
		
		List<Map<String, Object>> list = new ArrayList<>();
		
		for (String localPath : config.getConfigurationSection(path).getKeys(true)) {
			
			if (config.isConfigurationSection(path + "." + localPath)) {
				
				Map<String, Object> map = new LinkedHashMap<>();
				
				for (String element : config.getConfigurationSection(path + "." + localPath).getKeys(true))
					map.put(element, config.get(path + "." + localPath + "." + element));
				
				list.add(map);
				
			}
			
		}
		
		return list;
		
	}
	
	public static void convertSectionToMap(FileConfiguration config, String path) {
		config.set(path, sectionToMapList(config, path));
	}

}

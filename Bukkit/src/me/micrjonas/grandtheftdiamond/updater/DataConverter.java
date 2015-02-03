package me.micrjonas.grandtheftdiamond.updater;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import me.micrjonas.grandtheftdiamond.data.FileManager;
import me.micrjonas.grandtheftdiamond.data.PluginFile;
import me.micrjonas.grandtheftdiamond.stats.StatsType;
import me.micrjonas.grandtheftdiamond.util.Configurations;

import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.potion.PotionEffectType;

public class DataConverter {
	
	private DataConverter() { }
	
	
	public static void convertAll(boolean saveFiles) {
		convertHandcuffEffects();
		convertTaserEffects();
		convertFirearmZoom();
		convertGangs();
		convertChatConfig();
		if (saveFiles) {
			FileManager.getInstance().saveFileConfiguration(PluginFile.CONFIG);
		}
	}
	
	private static void convertChatConfig() {
		FileConfiguration config = FileManager.getInstance().getFileConfiguration(PluginFile.CONFIG);
		
		if (config.isConfigurationSection("chat.globalChat")) {
			
			config.set("chat.global", config.get("chat.globalChat"));	
			config.set("chat.globalChat", null);
			
		}
		
		if (config.isConfigurationSection("chat.localChat")) {
			
			config.set("chat.local", config.get("chat.localChat"));
			config.set("chat.localChat", null);
			
		}
		
		if (config.isConfigurationSection("chat.groupChat")) {
			
			config.set("chat.team", config.get("chat.groupChat"));
			config.set("chat.groupChat", null);
			
		}
		
	}


	private static void convertGangs() {
		
		FileConfiguration gangs = FileManager.getInstance().getFileConfiguration(PluginFile.GANGS);
		
		if (gangs.isConfigurationSection("gangs")) {
			
			for (String gang : gangs.getConfigurationSection("gangs").getKeys(false)) {
				
				if (gangs.isList("gangs." + gang + ".members")) {
					
					List<String> ids = new ArrayList<>();
					
					for (String memberName : gangs.getStringList("gangs." + gang + ".members")) {
						
						try {
							ids.add(UUID.fromString(memberName).toString());
						}
						
						catch (IllegalArgumentException ex) {
							
							@SuppressWarnings("deprecation")
							OfflinePlayer member = Bukkit.getOfflinePlayer(memberName);
							
							if (member != null && member.getUniqueId() != null)
								ids.add(member.getUniqueId().toString());	
							
						}
						
					}
					
					gangs.set("gangs." + gang + ".members", ids);
					
				}
			
				gangs.set(gang, gangs.get("gangs." + gang));
				
			}
			
			gangs.set("gangs", null);
			
		}
		
		FileManager.getInstance().saveFileConfiguration(PluginFile.GANGS);
		
	}


	private static void convertHandcuffEffects() {
		
		Configurations.convertSectionToMap(FileManager.getInstance().getFileConfiguration(PluginFile.CONFIG), "objects.handcuffs.effects");
		
	}
	
	
	private static void convertTaserEffects() {
		
		Configurations.convertSectionToMap(FileManager.getInstance().getFileConfiguration(PluginFile.CONFIG), "objects.taser.effects");
		
	}
	
	
	private static void convertFirearmZoom() {
		
		FileConfiguration config = FileManager.getInstance().getFileConfiguration(PluginFile.CONFIG);
		
		for (String path : config.getConfigurationSection("objects.firearms").getKeys(false)) {
			
			String listPath = "objects.firearms." + path + ".zoom.effects";
			
			if (config.isList(listPath) 
					&& config.getList(listPath).size() > 0
					&& !(config.getList(listPath).get(0) instanceof Map)) {
				
				List<Map<String, Object>> list = new ArrayList<>();
				
				for (String effect : config.getStringList("objects.firearms." + path + ".zoom.effects")) {
					
					String[] split = effect.split(" ");
					PotionEffectType type = PotionEffectType.getByName(split[0]);
					
					if (type != null) {
						
						Map<String, Object> map = new LinkedHashMap<>();
						
						map.put("type", split[0]);
						
						if (split.length > 1) {
							
							try {
								map.put("amplifier", Integer.parseInt(split[1]));
							}
							
							catch (NumberFormatException ex) { }
							
						}
						
						list.add(map);
						
					}
					
				}
				
				config.set("objects.firearms." + path + ".zoom.effects", list);
				
			}
			
		}
		
	}
	
	
	public static void convertStats(FileConfiguration data) {
		
		if (!data.isConfigurationSection("stats")) {
			
			data.set("stats." + StatsType.DEATHS, data.getInt("deathCount"));
			data.set("deathCount", null);
			
			data.set("stats." + StatsType.EXP, data.getInt("experience"));
			data.set("experience", null);
			
			data.set("stats." + StatsType.KILLED_CIVILIANS, data.getInt("killedCivilians.cop") + data.getInt("killedCivilians.civilian"));
			data.set("killedCivilians", null);
			
			data.set("stats." + StatsType.KILLED_COPS, data.getInt("killedCops.cop") + data.getInt("killedCops.civilian"));
			data.set("killedCops", null);
			
			data.set("stats." + StatsType.KILLED_GANGSTERS, data.getInt("killedGangsters.cop") + data.getInt("killedGangsters.civilian"));
			data.set("killedGangsters", null);
			
			data.set("stats." + StatsType.TOTAL_WANTED_LEVEL, data.getInt("totalWantedLevel"));
			data.set("totalWantedLevel", null);
			
			data.set("stats." + StatsType.WANTED_LEVEL, data.getInt("wantedLevel"));
			data.set("wantedLevel", null);
			
		}
		
	}
	
	
	public static void convertSafeData(FileConfiguration safeData) {
		
		for (String safe : safeData.getConfigurationSection("").getKeys(false)) {
			
			if (safeData.isString(safe + ".name")) {
				
				String name = safeData.getString(safe + ".name");
				
				safeData.set(safe + ".name", null);
				safeData.set(name, safeData.get(safe));
				safeData.set(safe, null);
				
			}
			
		}
		
	}

}

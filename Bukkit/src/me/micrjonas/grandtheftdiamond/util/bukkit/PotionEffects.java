package me.micrjonas.grandtheftdiamond.util.bukkit;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.TimeUnit;

import me.micrjonas.grandtheftdiamond.util.Objects;

import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

public class PotionEffects {
	
	private PotionEffects() { }
	
	@SuppressWarnings("unchecked")
	public static Set<PotionEffect> getEffectsFromConfig(ConfigurationSection configSection, String path) {
		
		Set<PotionEffect> effects = new HashSet<>();
		
		if (configSection.isList(path)) {
			
			for (Object effectMap : configSection.getList(path)) {
				
				if (effectMap instanceof Map) {
					
					PotionEffect effect = getFromMap((Map<String, Object>) effectMap);
					
					if (effect != null)
						effects.add(effect);
					
				}
					
			}
			
		}
		
		return effects;
		
	}
	
	
	@Deprecated
	public static PotionEffect parseEffect(String effect) {
		
		return parseEffect(effect, -1);
		
	}
	
	
	@Deprecated
	public static PotionEffect parseEffect(String effect, int duration) {
		
		String[] parts = effect.split(" ");
		
		PotionEffectType type = PotionEffectType.getByName(parts[0]);
		int duration1 = duration;
		int amplifier = 0;
		
		if (type == null)
			return null;
		
		if (parts.length >= 2 && duration <= 0) {
			
			try {
				duration1 = Integer.parseInt(parts[1]);
			}
			
			catch (NumberFormatException ex) { }
			
		}
		
		if (parts.length >= 3) {
			
			try {
				amplifier = Integer.parseInt(parts[2]);
			}
			
			catch (NumberFormatException ex) { }
			
		}
		
		return new PotionEffect(type, duration1 * 20, amplifier);
		
	}
	
	
// Storage
	
	public static Map<String, Object> toMap(PotionEffect effect) {
		
		Map<String, Object> map = new LinkedHashMap<>();
		
		map.put("type", effect.getType().getName());
		
		if (effect.getDuration() > 0)
			map.put("duration", effect.getDuration() / 20);
		
		map.put("amplifier", effect.getAmplifier());
		
		if (effect instanceof TimedPotionEffect) {
			
			Map<String, Object> timeMap = new LinkedHashMap<>();
			
			timeMap.put("min", (int) ((TimedPotionEffect) effect).getTimeUnit().convert(((TimedPotionEffect) effect).getTimeMin(), TimeUnit.SECONDS));
			timeMap.put("max", (int) ((TimedPotionEffect) effect).getTimeUnit().convert(((TimedPotionEffect) effect).getTimeMax(), TimeUnit.SECONDS));
			
			map.put("timeToEffect", timeMap);
			
		}
		
		return map;
		
	}
	
	
	public static List<Map<String, Object>> toMapList(Collection<PotionEffect> effects) {
		
		List<Map<String, Object>> maps = new ArrayList<>();
		
		for (PotionEffect effect : effects)
			maps.add(toMap(effect));
		
		return maps;
		
	}
	
	
	@SuppressWarnings("unchecked")
	public static PotionEffect getFromMap(Map<String, Object> effect) {
		
		Object typeName = effect.get("type");
		
		if (typeName == null || !(typeName instanceof String))
			return null;
		
		PotionEffectType type = PotionEffectType.getByName((String) typeName);
		
		int duration = Objects.getIntValue(effect.get("duration")) * 20;
		int amplifier = Objects.getIntValue(effect.get("amplifier"));
		
		Object timeToEffect = effect.get("timeToEffect");
		
		if (timeToEffect instanceof Map) {
			
			int timeMin = Objects.getIntValue(((Map<String, Object>) timeToEffect).get("min"));
			
			if (timeMin > 0) {
			
				int timeMax = Objects.getIntValue(((Map<String, Object>) timeToEffect).get("max"));
				
				return new TimedPotionEffect(type, duration, amplifier, timeMin, timeMax);	
				
			}
			
		}
		
		
		return new PotionEffect(type, duration > 0 ? duration : Integer.MAX_VALUE, amplifier > 0 ? amplifier : 0, Objects.getBooleanOrDefault(effect.get("ambient"), true));
		
	}
	
//Storage end
	
	public static void removeFromPlayer(Player p) {
		
		for (PotionEffect effect : p.getActivePotionEffects())
			p.removePotionEffect(effect.getType());
		
	}
	
	
	public static void addToPlayer(Player p, Collection<PotionEffect> effects) {
		
		for (PotionEffect effect : effects)
			p.addPotionEffect(effect);
		
	}

}

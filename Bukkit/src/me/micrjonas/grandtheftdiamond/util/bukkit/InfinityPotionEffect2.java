package me.micrjonas.grandtheftdiamond.util.bukkit;

import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

public class InfinityPotionEffect2 extends PotionEffect {

	public InfinityPotionEffect2(PotionEffectType type, int duration, int amplifier) {
		
		super(type, Integer.MAX_VALUE, amplifier);
		
	}
	
	
	public InfinityPotionEffect2(PotionEffectType type, int duration, int amplifier, boolean ambient) {
		
		super(type, Integer.MAX_VALUE, amplifier, ambient);
		
	}

}

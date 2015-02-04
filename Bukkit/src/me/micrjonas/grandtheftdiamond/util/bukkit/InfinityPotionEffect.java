package me.micrjonas.grandtheftdiamond.util.bukkit;

import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

/**
 * Represents a {@link PotionEffect} with the duration {@link Integer#MAX_VALUE}
 */
public class InfinityPotionEffect extends PotionEffect{

	/**
	 * Default constructor
	 * @param type The effect's type
	 * @param amplifier The effects amplifier
	 */
	public InfinityPotionEffect(PotionEffectType type, int amplifier) {
		super(type, Integer.MAX_VALUE, amplifier);
	}
	
}

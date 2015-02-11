package me.micrjonas.grandtheftdiamond.util.bukkit;

import java.util.concurrent.TimeUnit;

import me.micrjonas.grandtheftdiamond.GrandTheftDiamond;
import me.micrjonas.util.Utils;

import org.bukkit.entity.LivingEntity;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

public class TimedPotionEffect extends PotionEffect {
	
	private TimeUnit unit;
	private int timeMin;
	private int timeMax;

	public TimedPotionEffect(PotionEffectType type, int duration, int amplifier, int timeMin, int timeMax, boolean ambient) {
		
		super(type, duration, amplifier, ambient);
		
		setTimeData(timeMin, timeMax, TimeUnit.SECONDS);
		
	}

	
	public TimedPotionEffect(PotionEffectType type, int duration, int amplifier, int timeMin, int timeMax) {
		
		this(type, duration, amplifier, timeMin, timeMax, true);
		
	}
	
	
	@Override
	public boolean apply(final LivingEntity ent) {
		
		GrandTheftDiamond.runTaskLater(new Runnable() {
			
			@Override
			public void run() {
				
				TimedPotionEffect.super.apply(ent);
			}
			
		}, Utils.random(timeMin, timeMax), unit);
		
		return true;
		
	}
	
	
	public TimeUnit getTimeUnit() {
		
		return unit;
		
	}
	
	
	public int getTimeMin() {
		
		return timeMin;
		
	}
	
	
	public int getTimeMax() {
		
		return timeMax;
		
	}
	
	
	public void setTimeData(int timeMin, int timeMax, TimeUnit unit) {

		if (timeMin < 0)
			throw new IllegalArgumentException("timeMin cannot be < 0");
		
		if (timeMax < 0)
			throw new IllegalArgumentException("timeMax cannot be < 0");
		
		if (unit == null)
			throw new IllegalArgumentException("unit is not allowed to be null");
		
		this.timeMin = timeMin;
		this.timeMax = timeMax;
		this.unit = unit;
		
	}
	
}

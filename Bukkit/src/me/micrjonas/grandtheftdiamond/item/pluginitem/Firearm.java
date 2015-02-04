package me.micrjonas.grandtheftdiamond.item.pluginitem;

import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import me.micrjonas.grandtheftdiamond.util.Enums;
import me.micrjonas.grandtheftdiamond.util.Immutable;
import me.micrjonas.grandtheftdiamond.util.bukkit.Materials;
import me.micrjonas.grandtheftdiamond.util.bukkit.PotionEffects;

import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.potion.PotionEffect;

/**
 * Represents a firearm as {@link InteractablePluginItem}. Each object is immutable
 */
public class Firearm extends ItemStackPluginItem implements Fillable, Immutable, InteractablePluginItem {
	
	private final Firearms manager;
	private final String name;
	private final FirearmAmmo ammo;
	private final double shotsPerSecond;
	private final int projectilesPerShot;
	private final double damage;
	private final double power;
	private final int accuracy;
	private final Material projectile;
	private final double knockback;
	private final boolean disabled;
	private final boolean useAmmo;
	private final boolean useZoom;
	private final Set<PotionEffect> zoomEffects; 
	private final List<Sound> soundsOnShot;
	private final List<Sound> soundsOnHit;
	
	Firearm(String name, ConfigurationSection configSection, Firearms manager) {
		super(configSection);
		this.manager = manager;
		this.name = name.toLowerCase();
		ammo = new FirearmAmmo(this, configSection.getConfigurationSection("ammo"));
		ItemManager.getInstance().registerItem(ammo, ammo.getName());
		shotsPerSecond = configSection.getDouble("shotsPerSecond");
		projectilesPerShot = configSection.getInt("projectilesPerShot");
		damage = configSection.getDouble("damage");
		accuracy = Integer.parseInt(configSection.getString("accuracy").replace("%", ""));
		knockback = configSection.getDouble("knockback");
		projectile = Materials.getMaterialFromConfig("projectile");
		soundsOnShot = Enums.getEnumListFromConfig(Sound.class, configSection, "sounds.onShot");
		soundsOnHit = Enums.getEnumListFromConfig(Sound.class, configSection, "sounds.onHit");
		disabled = configSection.getBoolean("disabled");
		useAmmo = configSection.getBoolean("ammo.use");
		useZoom = configSection.getBoolean("zoom.use");
		if (useZoom && configSection.isList("zoom.effects")) {
			zoomEffects = PotionEffects.getEffectsFromConfig(configSection, "zoom.effects");
		}
		else {
			zoomEffects = new HashSet<>(0);
		}
		double power = Integer.parseInt(configSection.getString("power").replace("%", ""));
		if (power > 100) {
			power = 100;
		}
		this.power = power / 20;
	}

	@Override
	public String getName() {
		return name;
	}


	@Override
	public boolean hasFuel(Player player) {
		return player.getInventory().containsAtLeast(ammo.getItem(1), 1);
	}
	
	@Override
	public boolean onInteract(PlayerInteractEvent e) {
		return manager.useOn(e.getPlayer(), this);
	}

	@Override
	public boolean onEntityInteract(PlayerInteractEntityEvent e) {
		return manager.useOn(e.getPlayer(), this);
	}

	/**
	 * Returns the amounts of shots per seconds of this firearm
	 * @return The amounts of shots per seconds of this firearm
	 */
	public double getShotsPerSecond() {
		return shotsPerSecond;
	}
	
	/**
	 * Returns how many projectiles the firearm shots on each shot
	 * @return The amount of projectiles, the firearm shots on each shot
	 */
	public int getProjectilesPerShot() {
		return projectilesPerShot;
	}
	
	/**
	 * Returns the damage value of the firearm
	 * @return The firearm's damage value
	 */
	public double getDamage() {
		return damage;
	}
	
	/**
	 * Returns the power value of the firearm
	 * @return The firearm's power value
	 */
	public double getPower() {
		return power;
	}
	
	/**
	 * Returns the accuracy value of the firearm
	 * @return The firearm's accuracy value
	 */
	public int getAccuracy() {
		return accuracy;
	}
	
	/**
	 * Returns the {@link Material} of the {@code Firearm}'s {@link Projectile}
	 * @return The {@link Material} of the {@code Firearm}'s {@link Projectile}
	 */
	public Material getProjectile() {
		return projectile;
	}
	
	/**
	 * Returns the knockback value of the firearm
	 * @return The firearm's knockback value
	 */
	public double getKnockback() {
		return knockback;
	}
	
	/**
	 * Returns whether the {@code Firearm} is disabled
	 * @return True if disabled, else {@code false}
	 */
	public boolean isDisabled() {
		return disabled;
	}
	
	/**
	 * Returns the ammo of the {@link Firearm}
	 * @return The ammo of the {@link Firearm}
	 */
	public FirearmAmmo getAmmo() {
		return ammo;
	}
	
	/**
	 * Returns whether ammo should be used for this {@code Firearm}
	 * @return True if ammo is enabled for this {@code Firearm}, else {@code false}
	 */
	public boolean useAmmo() {
		return useAmmo;
	}
	
	public boolean useZoom() {
		return useZoom;
	}
	
	public Collection<PotionEffect> getZoomEffects() {
		return Collections.unmodifiableSet(zoomEffects);
	}
	
	public void addZoomEffects(Player p) {
		for (PotionEffect effect : zoomEffects) {
			p.addPotionEffect(effect);
		}
	}
	
	public void removeZoomEffects(Player p) {
		for (PotionEffect effect : zoomEffects) {
			p.removePotionEffect(effect.getType());
		}
	}
	
	public List<Sound> getOnShotSounds() {
		return soundsOnShot;
	}
	
	public List<Sound> getOnHitSounds() {
		return soundsOnHit;
	}
	
}

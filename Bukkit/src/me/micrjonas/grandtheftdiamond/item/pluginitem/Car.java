package me.micrjonas.grandtheftdiamond.item.pluginitem;

import me.micrjonas.grandtheftdiamond.Team;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Horse;
import org.bukkit.entity.Horse.Color;
import org.bukkit.entity.Horse.Style;
import org.bukkit.entity.Horse.Variant;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

/**
 * Represents cars as {@link PluginItem}
 */
public class Car implements PluginItem {
	
	private final String name;
	private String customName;
	private Team team;
	private Color color;
	private Style style;
	private Variant variant;
	private double maxHealth;
	private double jumpStrength;
	private PotionEffect speedEffect = null;
	
	Car(String name, ConfigurationSection configSection) {
		this.name = name;
		
	//Name
		customName = configSection.getString("name");
		if (customName == null) {
			customName = name;
		}
		
	//Team
		if (Team.isTeamIgnoreCase(configSection.getString("team"))) {
			team = Team.valueOf(configSection.getString("team").toUpperCase());
		}
		else {
			team = Team.EACH_TEAM;
		}
		
	//Color
		try {
			color = Color.valueOf(configSection.getString("horseColor").toUpperCase());
		}
		catch (IllegalArgumentException ex) {
			color = Color.BROWN;
		}
		
	//Style
		try {
			style = Style.valueOf(configSection.getString("horseStyle").toUpperCase());
		}
		catch (IllegalArgumentException ex) {
			style = Style.NONE;
		}
		
	//Variant
		try {
			variant = Variant.valueOf(configSection.getString("variant").toUpperCase());
		}
		catch (IllegalArgumentException ex) {
			variant = Variant.HORSE;
		}
		
	//Health
		maxHealth = configSection.getDouble("maxHealth");
		if (maxHealth <= 0) {
			maxHealth = 20.0;
		}
		
	//Jump strength
		jumpStrength = configSection.getDouble("jumpStrength");
		if (jumpStrength < 0) {
			jumpStrength = 0;
		}
		else if (jumpStrength > 2) {
			jumpStrength = 2.0;
		}
		
	//Speed
		int speed = configSection.getInt("speed");
		if (speed < 0) {
			speedEffect = new PotionEffect(PotionEffectType.SLOW, Integer.MAX_VALUE, speed * -1);
		}
		else if (speed > 0) {
			speedEffect = new PotionEffect(PotionEffectType.SPEED, Integer.MAX_VALUE, speed);
		}
	}
	
	@Override
	public void giveToPlayer(Player p, int amount) {
		spawnCar(p.getLocation(), p);
	}
	
	@Override
	public String getName() {
		return name;
	}
	
	/**
	 * Returns the car related {@code Team}
	 * @return The {@code Team}, the car is used for
	 */
	public Team getTeam() {
		return team;
	}
	
	/**
	 * Spawns a new {@code Horse} with the car's data
	 * @param loc The location to spawn the {@code Horse} at
	 * @param owner The owner of the new {@code Car}. Allowed to be null
	 * @return The spawned {@code Horse} or {@code null} if it was unsuccessful (e.g. the {@code EntitySpawnEvent} was cancelled)
	 * @throws IllegalArgumentException Thrown if {@code loc} is {@code null} or its {@code World} is {@code null}
	 */
	public Horse spawnCar(Location loc, Player owner) throws IllegalArgumentException {
		if (loc == null) {
			throw new IllegalArgumentException("Location cannot be null");
		}
		if (loc.getWorld() == null) {
			throw new IllegalArgumentException("World of the location cannot be null");
		}
		Horse car = (Horse) loc.getWorld().spawnEntity(loc, EntityType.HORSE);
		if (car == null) {
			return null;
		}
		if (owner != null) {
			car.setOwner(owner);
		}
		car.setAdult();
		car.setTamed(true);
		car.setCustomName(customName);
		car.setCustomNameVisible(true);
		car.setColor(color);
		car.setStyle(style);
		car.setVariant(variant);
		car.setMaxHealth(maxHealth);
		car.setJumpStrength(jumpStrength);
		if (speedEffect != null) {
			car.addPotionEffect(speedEffect);
		}
		car.getInventory().setSaddle(new ItemStack(Material.SADDLE, 1));
		return car;
	}

}

package me.micrjonas.grandtheftdiamond.manager;

import me.micrjonas.grandtheftdiamond.data.FileManager;
import me.micrjonas.grandtheftdiamond.data.PluginFile;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Horse;
import org.bukkit.entity.Horse.Style;
import org.bukkit.entity.Horse.Variant;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

public class PluginObjectManager {
	
	private static PluginObjectManager instance = null;
	
	public static PluginObjectManager getInstance() {
		
		if (instance == null)
			instance = new PluginObjectManager();
		
		return instance;
		
	}
	
	private PluginObjectManager() { }
	
	
	public Horse spawnCar(Player owner, Location loc, String type) {
		
		loc.setPitch(90);
		
		FileConfiguration config = FileManager.getInstance().getFileConfiguration(PluginFile.CONFIG);
		
		Horse car = (Horse) loc.getWorld().spawnEntity(loc, EntityType.HORSE);
		
		car.setAdult();
		
		car.setTamed(true);
		
		if (owner != null)
			car.setOwner(owner);
		
		car.setCustomName(config.getString("objects.cars." + type + ".name"));
		car.setCustomNameVisible(true);
		
		car.setColor(Horse.Color.valueOf(config.getString("objects.cars." + type + ".horseColor").toUpperCase()));
		car.setStyle(Style.valueOf(config.getString("objects.cars." + type + ".horseStyle").toUpperCase()));
		car.setVariant(Variant.valueOf(config.getString("objects.cars." + type + ".variant")));
		
		car.setMaxHealth(config.getDouble("objects.cars." + type + ".maxHealth"));
		
		
		double jumpStrength = config.getDouble("objects.cars." + type + ".jumpStrength");
		
		if (jumpStrength < 0)
			car.setJumpStrength(0.5);
		
		else if (jumpStrength > 2)
			car.setJumpStrength(2.0);
		
		else
			car.setJumpStrength(jumpStrength);
		
		int speed = config.getInt("objects.cars." + type + ".speed");
		
		if (speed > 0)
			car.addPotionEffect(new PotionEffect(PotionEffectType.SPEED, Integer.MAX_VALUE, speed));
		
		else if (speed < 0)
			car.addPotionEffect(new PotionEffect(PotionEffectType.SLOW, Integer.MAX_VALUE, speed * -1));
		
		car.getInventory().setSaddle(new ItemStack(Material.SADDLE, 1));
		
		return car;
		
	}

}

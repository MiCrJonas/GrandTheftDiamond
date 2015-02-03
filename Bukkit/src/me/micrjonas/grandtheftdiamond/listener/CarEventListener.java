package me.micrjonas.grandtheftdiamond.listener;

import java.util.concurrent.TimeUnit;

import me.micrjonas.grandtheftdiamond.GrandTheftDiamond;
import me.micrjonas.grandtheftdiamond.data.FileManager;
import me.micrjonas.grandtheftdiamond.data.PluginData;
import me.micrjonas.grandtheftdiamond.data.PluginFile;
import me.micrjonas.grandtheftdiamond.messenger.Messenger;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Fireball;
import org.bukkit.entity.Horse;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.entity.Snowball;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.vehicle.VehicleEnterEvent;
import org.bukkit.event.vehicle.VehicleExitEvent;
import org.bukkit.inventory.HorseInventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

public class CarEventListener implements Listener {
	
	@EventHandler
	public void onVehicleEnter(VehicleEnterEvent e) {
		
		if (e.getVehicle().getType() == EntityType.HORSE && e.getEntered() instanceof Player) {
			
			final Player p = (Player) e.getEntered();
			Horse car = (Horse) e.getVehicle();
			
			if (PluginData.getInstance().inArena(car.getLocation())) { 
				
				if (car.getOwner() != p && car.getOwner() != null) {
					
					final Location loc = p.getLocation();
					
					e.setCancelled(true);
					
					Messenger.getInstance().sendPluginMessage(p, "car.notOwner", new String[]{"%owner%"}, new String[]{((OfflinePlayer) car.getOwner()).getName()});
					
					GrandTheftDiamond.scheduleSyncDelayedTask(new Runnable() {
						
						@Override
						public void run() {
							
							p.teleport(loc);
							
						}
						
					}, 20, TimeUnit.MILLISECONDS);
					
				}
				
				/*else
					setNormalSpeed(car);*/
				
			}
			
		}
		
	}
	
	
	@EventHandler
	public void onVehicleExit(VehicleExitEvent e) {

		if (e.getVehicle() instanceof Horse && PluginData.getInstance().inArena(e.getVehicle().getLocation()))
			((LivingEntity) e.getVehicle()).addPotionEffect(new PotionEffect(PotionEffectType.SLOW, Integer.MAX_VALUE, Integer.MAX_VALUE));
		
	}
	
	
	@EventHandler
	public void onHorseInventoryChange(InventoryClickEvent e) {

		if (e.getInventory() instanceof HorseInventory) {

			Horse car = (Horse) e.getInventory().getHolder();

			if (PluginData.getInstance().inArena(car.getLocation()) && FileManager.getInstance().getFileConfiguration(PluginFile.CONFIG).getBoolean("objects.cars.disableInventoryChange")) {
				
				e.setCancelled(true);
				
				if (car.getPassenger() instanceof Player)
					Messenger.getInstance().sendPluginMessage((Player) car.getPassenger(), "car.canNotChangeInventory");
				
			}
				
		}
		
	}
	
	
	@SuppressWarnings("deprecation")
	@EventHandler (priority = EventPriority.HIGH)
	public void onHorseDamageByEntity(EntityDamageByEntityEvent e) {
		
		if (e.getEntity() instanceof Horse && PluginData.getInstance().inArena(e.getEntity().getLocation())) {
			
			Horse car = (Horse) e.getEntity();
			Player p;
			
			if (e.getDamager() instanceof Player)
				p = (Player) e.getDamager();
			
			else if (e.getDamager() instanceof Snowball || e.getDamager() instanceof Arrow || e.getDamager() instanceof Fireball) {
				
				Projectile pr = (Projectile) e.getDamager();
				
				if (pr.getShooter() instanceof Player)
					p = (Player) pr.getShooter();
				
				else
					return;
				
			}
			
			else
				return;
			
			FileConfiguration config = FileManager.getInstance().getFileConfiguration(PluginFile.CONFIG);
				
			if (!config.getBoolean("objects.cars.playersCanDamageOwnCar") && car.getOwner() == p) {
					
				e.setCancelled(true);
				Messenger.getInstance().sendPluginMessage(p, "car.canNotDamageOwn");
					
				return;
					
			}
				
			if (!config.getBoolean("objects.cars.playersCanDamageOwnedCar") && car.getOwner() != p && car.getOwner() != null) {
					
				e.setCancelled(true);
				Messenger.getInstance().sendPluginMessage(p, "car.canNotDamage", new String[]{"%owner%"}, new String[]{((OfflinePlayer) car.getOwner()).getName()});
				
				return;
				
			}
			
			if (!config.getBoolean("objects.cars.playersCanDamageNotOwnedCar") && car.getOwner() == null) {
				
				e.setCancelled(true);
				Messenger.getInstance().sendPluginMessage(p, "car.canNotDamageNoOwner", new Player[]{(Player) car.getOwner()});
				
				return;
				
			}
			
		}
		
	}
	
	
	@EventHandler
	public void onHorseDamage(EntityDamageEvent e) {
		
		if (e.getEntity() instanceof Horse && !(e.getCause() == DamageCause.ENTITY_ATTACK) && PluginData.getInstance().inArena(e.getEntity().getLocation()) && FileManager.getInstance().getFileConfiguration(PluginFile.CONFIG).getBoolean("objects.cars.disableAllCarDamage"))
			e.setCancelled(true);
		
	}
	
	
	@EventHandler
	public void onHorseDeath(EntityDeathEvent e) {
		
		if (e.getEntityType() == EntityType.HORSE) {
			
			if (PluginData.getInstance().inArena(e.getEntity().getLocation())) {
				
				for (ItemStack item : e.getDrops()) {
					
					if (item.getType() == Material.SADDLE)
						e.getDrops().remove(item);
					
				}
				
			}
			
		}
		
	}
	
	
	@SuppressWarnings("unused")
	private void setNormalSpeed(Horse horse) {

		FileConfiguration config = FileManager.getInstance().getFileConfiguration(PluginFile.CONFIG);
		String horseName = horse.getCustomName();
		
		if (!horseName.equals("Horse")) {
			
			for (String car : config.getConfigurationSection("objects.cars").getKeys(false)) {
				
				if (!config.isConfigurationSection("objects.cars." + car))
					continue;
				
				if (horseName.equals(config.getString("objects.cars." + car + ".name"))) {
					
					for (PotionEffect effect : horse.getActivePotionEffects()) 
						horse.removePotionEffect(effect.getType());
					
					int speed = FileManager.getInstance().getFileConfiguration(PluginFile.CONFIG).getInt("objects.cars." + car + ".speed");
					
					if (speed > 0)
						horse.addPotionEffect(new PotionEffect(PotionEffectType.SPEED, Integer.MAX_VALUE, speed));
					
					else if (speed < 0)
						horse.addPotionEffect(new PotionEffect(PotionEffectType.SLOW, Integer.MAX_VALUE, speed * -1));
					
				}
				
			}
			
		}
		
	}

}

package me.micrjonas.grandtheftdiamond.item.pluginitem;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import me.micrjonas.grandtheftdiamond.GrandTheftDiamond;
import me.micrjonas.grandtheftdiamond.GrandTheftDiamondPlugin;
import me.micrjonas.grandtheftdiamond.api.event.player.PlayerUseItemEvent;
import me.micrjonas.grandtheftdiamond.data.FileManager;
import me.micrjonas.grandtheftdiamond.data.PluginFile;
import me.micrjonas.grandtheftdiamond.messenger.Messenger;

import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.util.Vector;

/**
 * Represents a jetpack as {@link InteractablePluginItem}
 */
public class Jetpack extends ItemStackPluginItem implements Listener, InteractablePluginItem {
	
	/**
	 * The identifier name of each {@code Jetpack}, used for {@link PlayerUseItemEvent#getItemName()}
	 */
	public final static String NAME = "jetpack";
	
	private Map<Player, Long> flyCooldown = new HashMap<Player, Long>();
	
	private boolean cdNoFuel;
	
	/**
	 * Default constructor
	 * @param configSection The section where the item should be load from
	 */
	public Jetpack(ConfigurationSection configSection) {
		super(configSection);
		Bukkit.getPluginManager().registerEvents(this, GrandTheftDiamondPlugin.getInstance());
	}
	
	@Override
	public String getName() {
		return NAME;
	}
	
    @SuppressWarnings("deprecation")
    @Override
	public boolean onInteract(PlayerInteractEvent e) {
		final Player p = e.getPlayer();
		if (p.getInventory().getChestplate() != null) {
			short damage = p.getInventory().getChestplate().getDurability();
			if (p.getInventory().getChestplate().getType() == Material.CHAINMAIL_CHESTPLATE &&
					p.getInventory().getChestplate().hasItemMeta() &&
					p.getInventory().getChestplate().getItemMeta().hasDisplayName() &&
					p.getInventory().getChestplate().getItemMeta().getDisplayName().equals(FileManager.getInstance().getFileConfiguration(PluginFile.CONFIG).getString("objects.jetpack.name"))) {
				double power = FileManager.getInstance().getFileConfiguration(PluginFile.CONFIG).getDouble("objects.jetpack.power");
				if ((power < 0 /*&& (int) p.getLocation().getPitch() > 20) || (power > 0 && (int) p.getLocation().getPitch() < -20*/)) {
					boolean useFuel = FileManager.getInstance().getFileConfiguration(PluginFile.CONFIG).getBoolean("objects.jetpack.useFuel");
					if (!cdNoFuel) {
						Messenger.getInstance().sendPluginMessage(p, "noFuel");
						cdNoFuel = true;
						GrandTheftDiamond.scheduleSyncDelayedTask(new Runnable() {
							@Override
							public void run() {
								cdNoFuel = false;
							}
						}, 1500, TimeUnit.MILLISECONDS);
						return true;
					}
					if (p.getLocation().getBlockY() < FileManager.getInstance().getFileConfiguration(PluginFile.CONFIG).getInt("objects.jetpack.maxFlightHeight")) {
						if (useFuel) {
							p.getInventory().getChestplate().setDurability((short) (damage + 1));
							p.updateInventory();
						}
						p.setAllowFlight(true);
						Vector vec = p.getLocation().getDirection().multiply(power);
						vec = vec.setX(vec.getX() * -1).setZ(vec.getZ() * -1);
						p.setVelocity(vec);
						flyCooldown.put(p, System.currentTimeMillis() + 1000);
						GrandTheftDiamond.scheduleSyncDelayedTask(new Runnable() {
							@Override
							public void run() {
								if (flyCooldown.get(p) < System.currentTimeMillis() && !(p.getGameMode() == GameMode.CREATIVE || p.isOp() || p.hasPermission("essentials.fly"))) {
									p.setAllowFlight(false);
								}
							}
						}, 1500, TimeUnit.MILLISECONDS);
						return true;
					}
					else {
						Messenger.getInstance().sendPluginMessage(p, "maxFlightHeightReached");
					}
				}
			}
		}
		return false;
	}
    
	@Override
	public boolean onEntityInteract(PlayerInteractEntityEvent e) {
		return false;
	}

	/**
	 * Listens to {@link EntityDamageEvent}
	 * @param e The {@link Event} to listen
	 */
	@EventHandler
    public void onEntityDamage(EntityDamageEvent e) {
        if (e.getEntity() instanceof Player) {
            Player p = (Player) e.getEntity();
            if (p.getInventory().getChestplate() != null) {
    	        if (e.getCause().equals(DamageCause.FALL) &&
    	        		FileManager.getInstance().getFileConfiguration(PluginFile.CONFIG).getBoolean("objects.jetpack.disableFallDamage") &&
    	        		p.getInventory().getChestplate().getType() == Material.CHAINMAIL_CHESTPLATE && 
    	        		p.getInventory().getChestplate().getDurability() < 240)
    	        	e.setCancelled(true);
            }	
        }
	}

}

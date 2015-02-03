package me.micrjonas.grandtheftdiamond.item.pluginitem;

import java.util.HashMap;
import java.util.Map;

import me.micrjonas.grandtheftdiamond.GrandTheftDiamondPlugin;
import me.micrjonas.grandtheftdiamond.api.event.GrenadeExplodeEvent;
import me.micrjonas.grandtheftdiamond.api.event.player.PlayerUseItemEvent;
import me.micrjonas.grandtheftdiamond.data.FileManager;
import me.micrjonas.grandtheftdiamond.data.PluginData;
import me.micrjonas.grandtheftdiamond.data.PluginFile;
import me.micrjonas.grandtheftdiamond.data.TemporaryPluginData;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Egg;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.CreatureSpawnEvent;
import org.bukkit.event.entity.CreatureSpawnEvent.SpawnReason;
import org.bukkit.event.entity.ProjectileHitEvent;
import org.bukkit.event.entity.ProjectileLaunchEvent;

/**
 * Represents a grenade item representing an {@link Egg} as {@link InteractablePluginItem}
 */
public class Grenade extends ItemStackPluginItem implements Listener, PluginItem {
	
	/**
	 * The identifier name of each {@code Grenade}, used for {@link PlayerUseItemEvent#getItemName()}
	 */
	public final static String NAME = "grenade";
	
	private Map<Egg, Player> grenades = new HashMap<Egg, Player>();
	
	private double explosionRadius;
	private boolean breakBlocks;
	private boolean setFire;
	
	/**
	 * Default constructor
	 * @param configSection The section where the item should be load from
	 */
	public Grenade(ConfigurationSection configSection) {
		super(configSection);
		explosionRadius = configSection.getDouble("explosionRadius");
		setFire = configSection.getBoolean("setFire");
		breakBlocks = configSection.getBoolean("breakBlocks");	
		Bukkit.getPluginManager().registerEvents(this, GrandTheftDiamondPlugin.getInstance());
	}
	
	@Override
	public String getName() {
		return NAME;
	}
	
	/**
	 * Listens to {@link ProjectileLaunchEvent}
	 * @param e The {@link Event} to listen
	 */
	@SuppressWarnings("deprecation")
	@EventHandler
	public void onEggThrow(ProjectileLaunchEvent e) {
		Projectile pr = e.getEntity();
		if (pr instanceof Egg && pr.getShooter() instanceof Player) {
			Player p = (Player) pr.getShooter();
			Egg egg = (Egg) pr;
			if (TemporaryPluginData.getInstance().isIngame(p) &&
					p.getItemInHand().getType() == Material.EGG &&
					p.getItemInHand().getItemMeta().hasDisplayName() &&
					p.getItemInHand().getItemMeta().getDisplayName().equals(FileManager.getInstance().getFileConfiguration(PluginFile.CONFIG).getString("objects.grenade.name")) && 
					!FileManager.getInstance().getFileConfiguration(PluginFile.CONFIG).getBoolean("objects.grenade.disable")) {
				grenades.put(egg, p);
			}
		}
	}
	
	/**
	 * Listens to {@link ProjectileHitEvent}
	 * @param e The {@link Event} to listen
	 */
	@EventHandler 
	public void onEggHit(ProjectileHitEvent e) {
		Projectile pr = e.getEntity();
		if (pr instanceof Egg && grenades.containsKey(pr)) {
			Egg grenade = (Egg) pr;
			Location loc = pr.getLocation();
			grenades.remove(pr);
			@SuppressWarnings("deprecation")
			GrenadeExplodeEvent e2 = new GrenadeExplodeEvent((Player) pr.getShooter(), grenade, loc, 
					explosionRadius, breakBlocks, setFire);
			Bukkit.getPluginManager().callEvent(e2);
			if (!e2.isCancelled()) {
				loc.getWorld().createExplosion(loc.getX(), loc.getY(), loc.getZ(), 
						(float) e2.getExplosionRadius(), 
						e2.setFire(),
						e2.breakBlocks());	
			}
		}
	}
	
	/**
	 * Listens to {@link CreatureSpawnEvent}
	 * @param e The {@link Event} to listen
	 */
	@EventHandler
	public void onCreatureSpawn(CreatureSpawnEvent e) {
		if (e.getSpawnReason() == SpawnReason.EGG && PluginData.getInstance().inArena(e.getEntity().getLocation())) {
			e.setCancelled(true);
		}
	}

}

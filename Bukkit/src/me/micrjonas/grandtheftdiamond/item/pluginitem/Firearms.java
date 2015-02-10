package me.micrjonas.grandtheftdiamond.item.pluginitem;

import java.util.HashMap;
import java.util.Map;

import me.micrjonas.grandtheftdiamond.bukkit.BukkitGrandTheftDiamondPlugin;
import me.micrjonas.grandtheftdiamond.data.FileManager;
import me.micrjonas.grandtheftdiamond.data.PluginFile;
import me.micrjonas.grandtheftdiamond.data.TemporaryPluginData;
import me.micrjonas.grandtheftdiamond.messenger.Messenger;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.Fireball;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.entity.Snowball;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityExplodeEvent;
import org.bukkit.event.entity.ProjectileHitEvent;
import org.bukkit.event.entity.ProjectileLaunchEvent;
import org.bukkit.event.player.PlayerItemHeldEvent;
import org.bukkit.event.player.PlayerToggleSneakEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.util.Vector;

/**
 * Manager of all {@link Firearm}s
 */
public class Firearms implements Listener {
	
	private final Map<Projectile, Firearm> projectiles = new HashMap<>();
	private final Map<Player, Map<Firearm, Long>> cooldowns = new HashMap<>();
	//private final Map<String, Firearm> firearms = new HashMap<>();
	//private final Map<Player, Firearm> lastFirearmZoom = new HashMap<>();
	
	Firearms() {
		Bukkit.getPluginManager().registerEvents(this, BukkitGrandTheftDiamondPlugin.getInstance());
	}
	
	private double random(double coord, double power, int accuracy) {
		return Math.random() < 0.5 ? coord + (Math.random() * accuracy * power) / 500 : coord - (Math.random() * accuracy * power) / 500;
	}
	
	@SuppressWarnings("deprecation")
	boolean useOn(Player p, Firearm firearm) {
		if (TemporaryPluginData.getInstance().isIngame(p)) {
			if (!cooldowns.containsKey(p) || !cooldowns.get(p).containsKey(firearm) || cooldowns.get(p).get(firearm) < System.currentTimeMillis()) {
				Map<Firearm, Long> tmpCooldown = cooldowns.get(p);
				if (tmpCooldown == null) {
					tmpCooldown = new HashMap<>();
					cooldowns.put(p, tmpCooldown);
				}
				tmpCooldown.put(firearm, (long) (System.currentTimeMillis() + 1000L / firearm.getShotsPerSecond()));
				cooldowns.put(p, tmpCooldown);
				ItemStack ammo = firearm.getAmmo().getItem(1);
				Vector v = p.getLocation().getDirection().multiply(firearm.getPower());
				for (int i = 1; i <= firearm.getProjectilesPerShot(); i++) {
					if (firearm.getAccuracy() < 100) {
						v.setX(random(v.getX(), firearm.getPower(), firearm.getAccuracy()));
						v.setY(random(v.getY(), firearm.getPower(), firearm.getAccuracy()));
						v.setZ(random(v.getZ(), firearm.getPower(), firearm.getAccuracy()));
					}
					Projectile pr;
					if (firearm.getProjectile() == Material.FIREBALL) {
						pr = p.launchProjectile(Fireball.class, v);
					}
					else if (firearm.getProjectile() == Material.ARROW) {
						pr = p.launchProjectile(Arrow.class, v);
					}
					else {
						pr = p.launchProjectile(Snowball.class, v);
					}
					projectiles.put(pr, firearm);
				}
				p.setVelocity(p.getLocation().getDirection().multiply(firearm.getKnockback() * -1));
				if (firearm.useAmmo()) {
					Material ammoMaterial = ammo.getType();
					PlayerInventory inv = p.getInventory();
					int slot = 0;
					for (int i = 0; i < p.getInventory().getSize(); i++) {
						ItemStack tmpItem = inv.getItem(i);
						if (tmpItem != null && tmpItem.getType() == ammoMaterial) {
							if (tmpItem.getItemMeta().equals(ammo.getItemMeta())) {
								slot = i;
								break;
							}
						}
					}
					int oldCount = p.getInventory().getItem(slot).getAmount();
					if (oldCount > 1) {
						ammo.setAmount(oldCount - 1);
						p.getInventory().setItem(slot, ammo);
					}
					else {
						p.getInventory().setItem(slot, null);
					}
					p.updateInventory();
				}
			}
			return true;
		}
		return false;
	}
	
	/**
	 * Listens to {@link PlayerToggleSneakEvent}
	 * @param e The {@link Event} to listen
	 */
	@EventHandler
	public void onToogleSneak(PlayerToggleSneakEvent e) {
		/*Player p = e.getPlayer();
		if (TemporaryPluginData.getInstance().isIngame(p) && p.getItemInHand() != null) {
			String name = PluginItemManager.getInstance().getFirearmName(p.getItemInHand());
			if (name != null) {
				Firearm firearm = firearms.get(name);
				if (!firearm.isDisabled() && firearm.useAmmo()) {
					if (e.isSneaking()) {
						lastFirearmZoom.put(p, firearm);
						firearm.addZoomEffects(p);
					}
					else {
						lastFirearmZoom.remove(p.getUniqueId());
						firearm.removeZoomEffects(p);
					}
				}
			}
		}*/
	}
	
	/**
	 * Listens to {@link PlayerItemHeldEvent}
	 * @param e The {@link Event} to listen
	 */
	@EventHandler
	public void onSlotChange(PlayerItemHeldEvent e) {
		/*if (e.getPlayer().isSneaking() && TemporaryPluginData.getInstance().isIngame(e.getPlayer())) {
			Firearm lastSlot = lastFirearmZoom.get(e.getPlayer().getUniqueId());
			if (lastSlot != null) {
				lastFirearmZoom.remove(e.getPlayer().getUniqueId());
				lastSlot.removeZoomEffects(e.getPlayer());
			}
			if (e.getPlayer().getInventory().getItem(e.getNewSlot()) != null) {
				String firearmName = PluginItemManager.getInstance().getFirearmName(e.getPlayer().getInventory().getItem(e.getNewSlot()));
				if (firearmName != null) {
					Firearm newSlot = firearms.get(firearmName);
					lastFirearmZoom.put(e.getPlayer(), newSlot);
					newSlot.addZoomEffects(e.getPlayer());	
				}
			}
		}*/
	}
	
	/**
	 * Listens to {@link EntityDamageByEntityEvent}
	 * @param e The {@link Event} to listen
	 */
	@EventHandler
	public void onProjectileDamage(EntityDamageByEntityEvent e) {
		if (e.getDamager() instanceof Snowball || e.getDamager() instanceof Arrow || e.getDamager() instanceof Fireball) {
			Projectile pr = (Projectile) e.getDamager();
			Firearm firearm = projectiles.get(pr);
			if (firearm != null) {
				if (e.getEntity() instanceof LivingEntity) {
					e.setDamage(firearm.getDamage());
				}
				projectiles.remove(pr);
			}
		}
	}
	
	/**
	 * Listens to {@link EntityExplodeEvent}
	 * @param e The {@link Event} to listen
	 */
	@EventHandler
	public void entityExploded(EntityExplodeEvent e) {
		if (e.getEntity() instanceof Fireball && projectiles.containsKey(e.getEntity())) {
			e.blockList().clear();
		}
	}
	
	/**
	 * Listens to {@link ProjectileHitEvent}
	 * @param e The {@link Event} to listen
	 */
	@EventHandler
	public void onProjectileHit(ProjectileHitEvent e) {
		projectiles.remove(e.getEntity());
	}
	
	/**
	 * Listens to {@link ProjectileLaunchEvent}
	 * @param e The {@link Event} to listen
	 */
	@SuppressWarnings("deprecation")
	@EventHandler
	public void projectileLaunched(ProjectileLaunchEvent e) {
		if (e.getEntity() instanceof Snowball && e.getEntity().getShooter() instanceof Player) {
			Player p = (Player) e.getEntity().getShooter();
			ItemStack itemInHand = p.getItemInHand();
			if (TemporaryPluginData.getInstance().isIngame(p) && 
					itemInHand.getType() == Material.SNOW_BALL && 
					itemInHand.hasItemMeta() && 
					itemInHand.getItemMeta().hasDisplayName()) {
				String itemInHandName = itemInHand.getItemMeta().getDisplayName();
				for (String firearm : FileManager.getInstance().getFileConfiguration(PluginFile.CONFIG).getConfigurationSection("objects.firearms").getKeys(false)) {
					if (FileManager.getInstance().getFileConfiguration(PluginFile.CONFIG).isConfigurationSection("objects.firearms." + firearm) && FileManager.getInstance().getFileConfiguration(PluginFile.CONFIG).getString("objects.firearms." + firearm + ".ammo.name").equals(itemInHandName)) {
						p.getItemInHand().setAmount(p.getItemInHand().getAmount() + 1);
						p.updateInventory();
						Messenger.getInstance().sendPluginMessage(p, "useGun");
						e.setCancelled(true);
						return;
					}
				}
			}
		}
	}
	
}

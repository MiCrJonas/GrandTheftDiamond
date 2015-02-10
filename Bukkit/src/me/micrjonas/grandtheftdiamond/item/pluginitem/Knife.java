package me.micrjonas.grandtheftdiamond.item.pluginitem;

import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.TimeUnit;

import me.micrjonas.grandtheftdiamond.GrandTheftDiamond;
import me.micrjonas.grandtheftdiamond.bukkit.BukkitGrandTheftDiamondPlugin;
import me.micrjonas.grandtheftdiamond.data.FileManager;
import me.micrjonas.grandtheftdiamond.data.FileReloadListener;
import me.micrjonas.grandtheftdiamond.data.PluginFile;

import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.inventory.ItemStack;

/**
 * Represents a firearm as {@link PluginItem}
 */
public class Knife implements FileReloadListener, Listener {
	
	private final Set<Player> knifeCooldowns = new HashSet<>();
	private boolean useCooldown;
	
	/**
	 * Default constructor
	 */
	public Knife() {
		GrandTheftDiamond.registerFileReloadListener(this);
		Bukkit.getPluginManager().registerEvents(this, BukkitGrandTheftDiamondPlugin.getInstance());
	}
	
	@Override
	public void configurationReloaded(PluginFile file, FileConfiguration fileConfiguration) {
		if (file == PluginFile.CONFIG) {
			useCooldown = fileConfiguration.getBoolean("objects.knife.useCooldown");
		}
	}
	
	/**
	 * Listens to {@link EntityDamageByEntityEvent}
	 * @param e The {@link Event} to listen
	 */
	// This method is a shit
	@EventHandler (ignoreCancelled = true)
	public void onHit(EntityDamageByEntityEvent e) {
		if (e.getDamager() instanceof Player) {
			final Player p = (Player) e.getDamager();
			ItemStack handItem = p.getItemInHand();
			if (handItem.hasItemMeta() &&
					handItem.getItemMeta().hasDisplayName() &&
					handItem.getItemMeta().getDisplayName().equals(FileManager.getInstance().getFileConfiguration(PluginFile.CONFIG).getString("objects.knife.name")) &&
					handItem.getType().name().equals(FileManager.getInstance().getFileConfiguration(PluginFile.CONFIG).getString("objects.knife.item"))) {
				if (!FileManager.getInstance().getFileConfiguration(PluginFile.CONFIG).getBoolean("objects.knife.disable") && (!knifeCooldowns.contains(p) || !useCooldown)) {
					e.setDamage(FileManager.getInstance().getFileConfiguration(PluginFile.CONFIG).getDouble("objects.knife.damage"));
					if (useCooldown) {
						knifeCooldowns.add(p);
						GrandTheftDiamond.scheduleSyncDelayedTask(new Runnable() {
							@Override
							public void run() {
								knifeCooldowns.remove(p);
							}
						}, FileManager.getInstance().getFileConfiguration(PluginFile.CONFIG).getInt("objects.knife.cooldownInTicks") * 20, TimeUnit.SECONDS);
					}
				}
			}
		}
	}

}

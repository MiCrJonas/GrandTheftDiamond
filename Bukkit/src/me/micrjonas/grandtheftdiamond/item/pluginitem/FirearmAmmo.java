package me.micrjonas.grandtheftdiamond.item.pluginitem;

import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.event.player.PlayerInteractEvent;


/**
 * Represents ammo for a {@link Firearm} represented as {@link PluginItem}
 */
public class FirearmAmmo extends ItemStackPluginItem implements InteractablePluginItem {

	private final Firearm firearm;
	private final String name;
	
	/**
	 * Default constructor
	 * @param firearm The related {@link Firearm}
	 * @param configSection The section where the item should be load from
	 */
	protected FirearmAmmo(Firearm firearm, ConfigurationSection configSection) {
		super(configSection);
		this.firearm = firearm;
		name = "ammo:" + firearm.getName();
	}

	@Override
	public String getName() {
		return name;
	}
	
	@Override
	public void giveToPlayer(Player p, int amount) {
		super.giveToPlayer(p, 16);
	}
	
	@Override
	public boolean onInteract(PlayerInteractEvent e) {
		return true; // Just cancel event
	}

	@Override
	public boolean onEntityInteract(PlayerInteractEntityEvent e) {
		return true; // Just cancel event
	}
	
	/**
	 * Returns the related {@link Firearm}
	 * @return The related {@code Firearm}
	 */
	public Firearm getFirearm() {
		return firearm;
	}
	
}

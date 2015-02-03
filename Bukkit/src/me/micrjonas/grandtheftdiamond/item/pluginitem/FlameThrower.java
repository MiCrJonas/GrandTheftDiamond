package me.micrjonas.grandtheftdiamond.item.pluginitem;

import java.util.Collection;
import java.util.concurrent.TimeUnit;

import me.micrjonas.grandtheftdiamond.GrandTheftDiamond;
import me.micrjonas.grandtheftdiamond.api.event.player.PlayerUseItemEvent;
import me.micrjonas.grandtheftdiamond.util.Enums;

import org.bukkit.Effect;
import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.util.Vector;

/**
 * Represents a flame thrower as {@link InteractablePluginItem}
 */
public class FlameThrower extends ItemStackPluginItem implements InteractablePluginItem {
	
	/**
	 * The identifier name of each {@code FlameThrower}, used for {@link PlayerUseItemEvent#getItemName()}
	 */
	public final static String NAME = "flamethrower";
	
	private int burnTime;
	private Collection<Effect> effects;
	private Collection<Sound> sounds;	
	
	/**
	 * Default constructor
	 * @param configSection The section where the item should be load from
	 */
	public FlameThrower(ConfigurationSection configSection) {
		super(configSection);
		burnTime = (int) (configSection.getDouble("burnTime") * 20);
		effects = Enums.getEnumListFromConfig(Effect.class, configSection, "effects");
		sounds = Enums.getEnumListFromConfig(Sound.class, configSection, "sounds");
	}
	
	@Override
	public String getName() {
		return NAME;
	}
	
	@Override
	public boolean onInteract(PlayerInteractEvent e) {
		Location loc = e.getPlayer().getLocation();
		if (e.getAction() == Action.RIGHT_CLICK_AIR) {
			Vector v = loc.getDirection();
			v.multiply(2.5);
			loc.setX(loc.getX() + v.getX());
			loc.setY(loc.getY() + v.getY() + 1.5);
			loc.setZ(loc.getZ() + v.getZ());
		}
		else {
			loc = e.getClickedBlock().getLocation();
		}
		playEffectsTwoTimes(loc);
		return true;
	}
	
	
	@Override
	public boolean onEntityInteract(PlayerInteractEntityEvent e) {
		Entity ent = e.getRightClicked();
		Location loc = ent.getLocation();
		loc.setY(loc.getY() + 0.8);
		playEffectsTwoTimes(loc);
		if (e.getRightClicked() instanceof LivingEntity) {
			ent.setFireTicks(burnTime);
		}
		return true;
	}
	
	private void playEffects(Location loc) {
		for (Effect effect : effects) {
			loc.getWorld().playEffect(loc, effect, 10);
		}
		for (Sound sound : sounds) {
			loc.getWorld().playSound(loc, sound, 10, 10);
		}
	}
	
	private void playEffectsTwoTimes(final Location loc) {
		playEffects(loc);
		GrandTheftDiamond.scheduleSyncDelayedTask(new Runnable() {
			@Override
			public void run() {
				playEffects(loc);
			}
		}, 0.1, TimeUnit.SECONDS);
	}

}

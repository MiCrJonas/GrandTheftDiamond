package me.micrjonas.grandtheftdiamond.item.pluginitem;

import java.util.Set;
import java.util.concurrent.TimeUnit;

import me.micrjonas.grandtheftdiamond.GrandTheftDiamond;
import me.micrjonas.grandtheftdiamond.util.bukkit.PotionEffects;
import me.micrjonas.util.Utils;

import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;

/**
 * Represents a drug
 */
public class Drug extends ItemStackPluginItem implements InteractablePluginItem  {
	
	private final String name;
	private final Set<PotionEffect> effects;
	private final int timeToEffectMin;
	private final int timeToEffectMax;
	
	Drug (String name, ConfigurationSection configSection) {
		super(configSection);
		if (configSection.isString("name")) {
			this.name = configSection.getString("name");
		}
		else {
			this.name = name;
		}
		effects = PotionEffects.getEffectsFromConfig(configSection, "effects"); 
		timeToEffectMin = configSection.getInt("timeToEffect.min");
		timeToEffectMax = configSection.getInt("timeToEffect.max");
	}
	
	@Override
	public String getName() {
		return name;
	}

	@Override
	public boolean onInteract(PlayerInteractEvent e) {
		return useDrug(e.getPlayer());
	}

	@Override
	public boolean onEntityInteract(PlayerInteractEntityEvent e) {
		return useDrug(e.getPlayer());
	}
	
	private boolean useDrug(final Player p) {
		GrandTheftDiamond.runTaskLater(new Runnable() {
			@Override
			public void run() {
				for (PotionEffect effect : effects) {
					effect.apply(p);
				}
			}
		}, Utils.random(timeToEffectMin, timeToEffectMax), TimeUnit.SECONDS);
		ItemStack inHand = p.getItemInHand();
		inHand.setAmount(inHand.getAmount() - 1);
		p.setItemInHand(inHand);
		return true;
	}

}

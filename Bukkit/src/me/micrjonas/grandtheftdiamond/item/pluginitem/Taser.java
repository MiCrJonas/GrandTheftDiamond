package me.micrjonas.grandtheftdiamond.item.pluginitem;

import java.util.Set;

import me.micrjonas.grandtheftdiamond.Team;
import me.micrjonas.grandtheftdiamond.api.event.player.PlayerUseItemEvent;
import me.micrjonas.grandtheftdiamond.data.FileManager;
import me.micrjonas.grandtheftdiamond.data.PluginData;
import me.micrjonas.grandtheftdiamond.data.PluginFile;
import me.micrjonas.grandtheftdiamond.data.TemporaryPluginData;
import me.micrjonas.grandtheftdiamond.messenger.Messenger;
import me.micrjonas.grandtheftdiamond.util.bukkit.PotionEffects;

import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.potion.PotionEffect;

/**
 * Represents a taser as {@link InteractablePluginItem}
 */
public class Taser extends ItemStackPluginItem implements InteractablePluginItem {
	
	/**
	 * The identifier name for every {@code Taser}, used for {@link PlayerUseItemEvent#getItemName()}
	 */
	public final static String NAME = "taser";
	
	private boolean civilianCanTase;
	private Set<PotionEffect> effects;
	private double damage;
	
	/**
	 * Default constructor
	 * @param configSection The section where the item should be load from
	 */
	public Taser(ConfigurationSection configSection) {
		super(configSection);
		civilianCanTase = configSection.getBoolean("civilianCanUsetaser");
		effects = PotionEffects.getEffectsFromConfig(configSection, "effects");
		damage = configSection.getDouble("damage");	
	}
	
	@Override
	public String getName() {
		return NAME;
	}
	
	@Override
	public boolean onEntityInteract(PlayerInteractEntityEvent e) {
		Player p = e.getPlayer();
		if (PluginData.getInstance().getTeam(p) == Team.COP || civilianCanTase) {
			if (e.getRightClicked() instanceof Player) {
				Player otherP = (Player) e.getRightClicked();
				if (TemporaryPluginData.getInstance().isIngame(otherP)) {
					if (PluginData.getInstance().getTeam(otherP) == Team.CIVILIAN || FileManager.getInstance().getFileConfiguration(PluginFile.CONFIG).getBoolean("objects.taser.copsCanGetTased")) {
						if (damage > 0) {
							otherP.damage(damage, p);
						}
						PotionEffects.addToPlayer(otherP, effects);
						Messenger.getInstance().sendPluginMessage(otherP, "getTased", new Player[]{p});
						Messenger.getInstance().sendPluginMessage(p, "tasedOther", new Player[]{otherP});
						return true;
					}
					else {
						Messenger.getInstance().sendPluginMessage(p, "cannotTaseCops");
					}
				}
			}
		}
		else {
			Messenger.getInstance().sendPluginMessage(p, "cannotTaseAsCivilian");
		}
		return false;
	}
	
	@Override
	public boolean onInteract(PlayerInteractEvent e) {
		return false;
	}

}

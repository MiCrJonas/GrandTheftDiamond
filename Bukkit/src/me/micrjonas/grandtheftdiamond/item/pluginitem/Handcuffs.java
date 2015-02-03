package me.micrjonas.grandtheftdiamond.item.pluginitem;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.TimeUnit;

import me.micrjonas.grandtheftdiamond.GrandTheftDiamond;
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
 * Represents handcuffs as {@link InteractablePluginItem}
 */
public class Handcuffs extends ItemStackPluginItem implements InteractablePluginItem {

	/**
	 * The identifier name for every {@code Handcuffs}, used for {@link PlayerUseItemEvent#getItemName()}
	 */
	public final static String NAME = "handcuffs";
	
	private final Map<Player, Integer> clickedCount = new HashMap<Player, Integer>();
	private final Map<Player, Long> timeSinceLastClick = new HashMap<Player, Long>();
	private final List<Player> hasCooldownAlreadyHandcuffed = new ArrayList<Player>();
	
	private Set<PotionEffect> effects;
	
	/**
	 * Default constructor
	 * @param configSection The section where the item should be load from
	 */
	public Handcuffs(ConfigurationSection configSection) {
		super(configSection);
	}
	
	@Override
	public String getName() {
		return NAME;
	}
	
	@Override
	public boolean onEntityInteract(PlayerInteractEntityEvent e) {
		final Player p = e.getPlayer();
		if (e.getRightClicked() instanceof Player &&
				TemporaryPluginData.getInstance().isIngame((Player) e.getRightClicked())) {
			final Player otherP = (Player) e.getRightClicked();
			if (PluginData.getInstance().getTeam(p) == Team.COP) {
				if (PluginData.getInstance().getTeam(otherP) == Team.CIVILIAN) {
					if (!TemporaryPluginData.getInstance().isHandcuffed(otherP)) {
						
						long oldMills = System.currentTimeMillis();
							
						if (timeSinceLastClick.containsKey(p))
							oldMills = timeSinceLastClick.get(p);
							
						int requiredClickDuration = FileManager.getInstance().getFileConfiguration(PluginFile.CONFIG).getInt("objects.handcuffs.requiredClickDuration");
							
						if ((System.currentTimeMillis() - oldMills) < 250 || requiredClickDuration == 0) {
								
							int oldClickedCount = 0;
									
							if (clickedCount.containsKey(p))		
								oldClickedCount = clickedCount.get(p);	
								
							clickedCount.put(p, oldClickedCount + 1);
							timeSinceLastClick.put(p, System.currentTimeMillis());
								
							int newClickedCount = clickedCount.get(p);
								
							if (newClickedCount >= requiredClickDuration * 4 || requiredClickDuration == 0) {	
									
								clickedCount.remove(p);
								timeSinceLastClick.remove(p);
									
								TemporaryPluginData.getInstance().setIsHandcuffed(otherP, true);
								
								PotionEffects.addToPlayer(otherP, effects);
								
								hasCooldownAlreadyHandcuffed.add(p);
								
								GrandTheftDiamond.scheduleSyncDelayedTask(new Runnable() {
									
									@Override
									public void run() {
										
										if (hasCooldownAlreadyHandcuffed.contains(p))
											hasCooldownAlreadyHandcuffed.remove(p);
											
									}
										
								}, 2, TimeUnit.SECONDS);
								
								Messenger.getInstance().sendPluginMessage(p, "handcuffedOther", new Player[]{otherP});
								Messenger.getInstance().sendPluginMessage(otherP, "getHandcuffed", new Player[]{p});
								
								GrandTheftDiamond.scheduleSyncDelayedTask(new Runnable() {
									@Override
									public void run() {
										TemporaryPluginData.getInstance().setIsHandcuffed(otherP, false);
										Messenger.getInstance().sendPluginMessage(otherP, "noLongerHandcuffed");
									}
								}, FileManager.getInstance().getFileConfiguration(PluginFile.CONFIG).getInt("objects.handcuffs.handcuffedTime"), TimeUnit.SECONDS);
							
							}
							
						}
							
						else {
								
							clickedCount.remove(p);
							timeSinceLastClick.remove(p);
								
						}
						
						return true;
						
					}
					
					else {
						if (!hasCooldownAlreadyHandcuffed.contains(p)) {
							Messenger.getInstance().sendPluginMessage(p, "alreadyHandcuffed", new Player[]{otherP});
							hasCooldownAlreadyHandcuffed.add(p);
							GrandTheftDiamond.scheduleSyncDelayedTask(new Runnable() {
								@Override
								public void run() {
									if (hasCooldownAlreadyHandcuffed.contains(p)) {
										hasCooldownAlreadyHandcuffed.remove(p);
									}
								}
							}, 2, TimeUnit.SECONDS);
						}
					}
				}
				else {
					Messenger.getInstance().sendPluginMessage(p, "cannotHandcuffCops");
				}
			}
			else {
				Messenger.getInstance().sendPluginMessage(p, "cannotHandcuffAsCivilian");
			}
		}
		return false;
	}

	@Override
	public boolean onInteract(PlayerInteractEvent e) {
		return false; 
	}

}

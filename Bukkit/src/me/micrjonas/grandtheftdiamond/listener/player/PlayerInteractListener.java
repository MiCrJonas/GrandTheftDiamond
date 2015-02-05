package me.micrjonas.grandtheftdiamond.listener.player;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;

import me.micrjonas.grandtheftdiamond.GrandTheftDiamond;
import me.micrjonas.grandtheftdiamond.Team;
import me.micrjonas.grandtheftdiamond.api.event.AbstractCancellableEvent;
import me.micrjonas.grandtheftdiamond.api.event.cause.JailReason;
import me.micrjonas.grandtheftdiamond.api.event.player.PlayerUseItemEvent;
import me.micrjonas.grandtheftdiamond.data.FileManager;
import me.micrjonas.grandtheftdiamond.data.FileReloadListener;
import me.micrjonas.grandtheftdiamond.data.PluginData;
import me.micrjonas.grandtheftdiamond.data.PluginFile;
import me.micrjonas.grandtheftdiamond.data.TemporaryPluginData;
import me.micrjonas.grandtheftdiamond.house.HouseManager;
import me.micrjonas.grandtheftdiamond.item.pluginitem.Fillable;
import me.micrjonas.grandtheftdiamond.item.pluginitem.InteractablePluginItem;
import me.micrjonas.grandtheftdiamond.jail.JailManager;
import me.micrjonas.grandtheftdiamond.listener.SetPositionsWithItem;
import me.micrjonas.grandtheftdiamond.messenger.Messenger;
import me.micrjonas.grandtheftdiamond.messenger.NoPermissionType;
import me.micrjonas.grandtheftdiamond.rob.RobManager;
import me.micrjonas.grandtheftdiamond.sign.SignManager;
import me.micrjonas.grandtheftdiamond.util.bukkit.Doors;
import me.micrjonas.grandtheftdiamond.util.bukkit.Materials;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.block.Sign;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.Event.Result;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;

public class PlayerInteractListener implements Listener, FileReloadListener {
	
	private final SetPositionsWithItem setPositionsWithItem = new SetPositionsWithItem();
	private final Map<ItemStack, InteractablePluginItem> registeredItems = new HashMap<ItemStack, InteractablePluginItem>();
	
	private Material wandType;
	private Material safeType;
	private String signTitle;
	private boolean ignoreCancelledInteracts;
	private boolean cancelHandcuffedInteracts;
	
	public PlayerInteractListener() {
		GrandTheftDiamond.registerFileReloadListener(this);
	}
	
	@Override
	public void configurationReloaded(PluginFile file, FileConfiguration fileConfiguration) {
		if (file == PluginFile.CONFIG) {
			wandType = Materials.getMaterialFromConfig("wandItem");
			safeType = Materials.getMaterialFromConfig(FileManager.getInstance().getFileConfiguration(PluginFile.EVENT_CONFIG), "robbing.safe.block");
			signTitle = ChatColor.translateAlternateColorCodes('&', fileConfiguration.getString("signs.signTitle"));
			ignoreCancelledInteracts = fileConfiguration.getBoolean("ignoreCancelledInteracts");
			cancelHandcuffedInteracts = fileConfiguration.getBoolean("object.handcuffs.disableAllInteractsWhileHandcuffed");
		}
	}
	
	/**
	 * Registers a {@link InteractablePluginItem}. The listener does now listen to an interaction with it
	 * @param item The item to register
	 * @param itemStack The related {@code ItemStack}
	 * @throws IllegalArgumentException Thrown if {@code item} or {@code itemStack} is {@code null}
	 */
	public void regiserItem(InteractablePluginItem item, ItemStack itemStack) throws IllegalArgumentException {
		if (item == null) {
			throw new IllegalArgumentException("Item to register is not allowed to be null");
		}
		if (itemStack == null) {
			throw new IllegalArgumentException("Related ItemStack to register is not allowed to be null");
		}
		itemStack = itemStack.clone();
		itemStack.setAmount(1);
		registeredItems.put(itemStack, item);
	}
	
	/**
	 * Unregisters a {@link InteractablePluginItem}. The listener does no longer listen to an interaction with it
	 * @param item The item to unregister
	 * @throws IllegalArgumentException Thrown if {@code item} is {@code null}
	 */
	public void unregisterItem(InteractablePluginItem item) throws IllegalArgumentException {
		if (item == null) {
			throw new IllegalArgumentException("Cannot unregister null-item");
		}
		for (Iterator<Entry<ItemStack, InteractablePluginItem>> iter = registeredItems.entrySet().iterator(); iter.hasNext(); ) {
			Entry<ItemStack, InteractablePluginItem> next = iter.next();
			if (item.equals(next.getValue())) {
				iter.remove();
			}
		}
	}
	
	@SuppressWarnings("deprecation")
	@EventHandler (priority = EventPriority.HIGHEST, ignoreCancelled = false)
	public void onInteract(PlayerInteractEvent e) {
		if (e.isCancelled() && ignoreCancelledInteracts) {
			return;
		}
		Player p = e.getPlayer();
		if (cancelHandcuffedInteracts && TemporaryPluginData.getInstance().isHandcuffed(p)) {
			e.setCancelled(true);
		}
		else {
			ItemStack handItem = p.getItemInHand();
			if (e.getAction() == Action.RIGHT_CLICK_AIR || e.getAction() == Action.RIGHT_CLICK_BLOCK) {
				InteractablePluginItem interacted = playerInteracted(p, handItem);
				if (interacted != null) {
					if (interacted.onInteract(e)) {
						e.setCancelled(true);
						return;
					}
				}
				if (e.getAction() == Action.RIGHT_CLICK_BLOCK) {
					if (Doors.isDoor(e.getClickedBlock())) {
						if (!HouseManager.getInstance().doorClicked(e)) {
							e.setUseInteractedBlock(Result.DENY);
						}
					}
					else if (e.getClickedBlock().getType() == Material.SIGN_POST || e.getClickedBlock().getType() == Material.WALL_SIGN) {
						Sign s = (Sign) e.getClickedBlock().getState();
						if (s.getLine(0).equals(signTitle)) {
							SignManager.getInstance().onSignClick(s, p);
						}
					}
					else if (e.getClickedBlock().getType() == safeType) {
						RobManager.getInstance().safeRightClicked(e);
					}
					else if (handItem.getType() == wandType && TemporaryPluginData.getInstance().hasWandEnabled(p)) {
						setPositionsWithItem.setPositions(e);
						e.setCancelled(true);
					}
				}
				else if (e.getAction() == Action.LEFT_CLICK_BLOCK) {
					if (handItem.getType() == wandType) {
						if (TemporaryPluginData.getInstance().hasWandEnabled(p)) {
							setPositionsWithItem.setPositions(e);
							e.setCancelled(true);
						}
					}
				}
			}
		}
	}
	
	@EventHandler
	public void onEntityInteract(PlayerInteractEntityEvent e) {
		Player p = e.getPlayer();
		if (TemporaryPluginData.getInstance().isIngame(p)) {
			InteractablePluginItem interacted = playerInteracted(p, p.getItemInHand());
			if (interacted != null) {
				if (interacted.onEntityInteract(e)) {
					e.setCancelled(true);
					return;
				}
			}
			else if (e.getRightClicked() instanceof Player) {
				Player otherP = (Player) e.getRightClicked();
				if (TemporaryPluginData.getInstance().isArrestingPlayer(p)) {
					if (TemporaryPluginData.getInstance().isHandcuffed(otherP)) {
						TemporaryPluginData.getInstance().setIsArrestingPlayer(p, false);
						JailManager.getInstance().jailPlayer(p, JailReason.ARREST, otherP);
					}
					else {
						Messenger.getInstance().sendPluginMessage(p, "otherMustBeHandcuffed");
					}
				}
				else if (TemporaryPluginData.getInstance().isDetainingPlayer(p)) {
					if (TemporaryPluginData.getInstance().isHandcuffed(otherP)) {
						TemporaryPluginData.getInstance().setPassengerToJail(p, otherP);
						TemporaryPluginData.getInstance().setIsDetainingPlayer(p, false);
						
						Messenger.getInstance().sendPluginMessage(p, "otherDetained", new Player[]{otherP});
						Messenger.getInstance().sendPluginMessage(otherP, "detained", new Player[]{p});
					}
					else {
						Messenger.getInstance().sendPluginMessage(p, "otherMustBeHandcuffed");
					}
				}
				else if (JailManager.getInstance().isHandcuffed(otherP)) {
					if (PluginData.getInstance().getTeam(otherP) == Team.COP) {
						
					}
				}
			}
		}
	}
	
	private InteractablePluginItem playerInteracted(Player p, ItemStack handItem) {
		if (handItem.getType() != Material.AIR) {					
			if (TemporaryPluginData.getInstance().isIngame(p)) {
				handItem = handItem.clone();
				handItem.setAmount(1);
				InteractablePluginItem interactable = registeredItems.get(handItem);
				if (interactable != null) {
					if (GrandTheftDiamond.checkPermission(p, "use.item." + interactable.getName(), true, NoPermissionType.USE_OBJECT)) {
						boolean cancelledNoFuel = interactable instanceof Fillable
								&& !((Fillable) interactable).hasFuel(p);
						PlayerUseItemEvent e2 = new PlayerUseItemEvent(p, interactable, cancelledNoFuel);
						if (AbstractCancellableEvent.fireEvent(e2)) {
							return interactable;
						}
						else if (e2.isCancelledNoFuel()) {
							Messenger.getInstance().sendPluginMessage(p, "noFuel");
						}
					}
				}
			}
		}
		return null; 
	}

}

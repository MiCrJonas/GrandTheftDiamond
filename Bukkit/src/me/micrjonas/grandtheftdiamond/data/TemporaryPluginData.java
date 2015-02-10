package me.micrjonas.grandtheftdiamond.data;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

import me.micrjonas.grandtheftdiamond.GrandTheftDiamond;
import me.micrjonas.grandtheftdiamond.BukkitGrandTheftDiamondPlugin;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerKickEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;

public class TemporaryPluginData implements Listener {
	
	private final static TemporaryPluginData instance = new TemporaryPluginData();
	
	public static TemporaryPluginData getInstance() {
		
		return instance;
		
	}
	
	private final boolean clearPlayerDataOnLeave = FileManager.getInstance().getFileConfiguration(PluginFile.CONFIG).getBoolean("temporaryPlayerData.clearDataOnDisconnect");
	private final int timeToClearPlayerDataAfterLeave = FileManager.getInstance().getFileConfiguration(PluginFile.CONFIG).getInt("temporaryPlayerData.timeToClearDataOnDisconnect") * 20;
	
	private final Set<Player> inGame = new HashSet<>();
	
	private final Map<UUID, Location> oldLoc = new HashMap<>();
	private final Map<UUID, ItemStack[]> oldInv = new HashMap<>();
	private final Map<UUID, ItemStack[]> oldArmor = new HashMap<>();
	private final Map<UUID, Integer> oldLevel = new HashMap<>();
	private final Map<UUID, Float> oldExp = new HashMap<>();
	private final Map<UUID, Double> oldHealth = new HashMap<>();
	private final Map<UUID, Integer> oldFoodlevel = new HashMap<>();
	private final Map<UUID, Collection<PotionEffect>> oldPotionEffects = new HashMap<>();
	
	private final Map<UUID, UUID> targetPlayer = new HashMap<>();
	
	private final Map<UUID, Location> createPos1 = new HashMap<>();
	private final Map<UUID, Location> createPos2 = new HashMap<>();
	
	private final Set<UUID> unableToJoin = new HashSet<>();
	
	private final Set<UUID> joiningPlayers = new HashSet<>();
	
	private final Map<UUID, Integer> signCooldown = new HashMap<>();
	
	private final Set<UUID> handcuffedPlayers = new HashSet<>();
	
	private final Set<UUID> arrestingPlayers = new HashSet<>();
	private final Set<UUID> detainingPlayers = new HashSet<>();
	
	private final Map<UUID, UUID> passengerForJailList = new HashMap<>();
	
	private final Set<UUID> canAcceptCorrupt = new HashSet<>();
	private final Map<UUID, UUID> gangsterToCopCorrupt = new HashMap<>();
	private final Map<UUID, Integer> corruptMoneyAmount = new HashMap<>();
	
	private final Map<UUID, List<String>> gangInvitesToAccept = new HashMap<>();
	
	private final Set<UUID> wandEnabled = new HashSet<>();
	
	private final Map<UUID, String> houseDoorCreatingPlayers = new HashMap<>();
	
	
	private TemporaryPluginData() {
		
		if (BukkitGrandTheftDiamondPlugin.getInstance().isEnabled())
			Bukkit.getPluginManager().registerEvents(this, BukkitGrandTheftDiamondPlugin.getInstance());
		
	}
	
	
	/**
	 * @deprecated Do not call
	 */
	@Deprecated
	public void onLeave(PlayerQuitEvent e) {
		
		onLeave(e.getPlayer());
		
	}
	
	
	/**
	 * @deprecated Do not call
	 */
	@Deprecated
	@EventHandler (ignoreCancelled = true, priority = EventPriority.MONITOR)
	public void onLeave(PlayerKickEvent e) {
		
		onLeave(e.getPlayer());
		
	}
	
	
	private void onLeave(final Player p) {
		
		if (clearPlayerDataOnLeave) {
			
			if (timeToClearPlayerDataAfterLeave >= 0) {
				
				GrandTheftDiamond.scheduleSyncDelayedTask(new Runnable() {
						
					@Override
					public void run() {
							
						if (!p.isOnline())
							clearCache(p.getUniqueId());
							
					}
						
				}, timeToClearPlayerDataAfterLeave, TimeUnit.SECONDS);
				
			}
			
			else
				clearCache(p.getUniqueId());
			
		}
		
	}
	
	
	@SuppressWarnings("deprecation")
	private void clearCache(UUID playerId) {
		
		BukkitGrandTheftDiamondPlugin.getInstance().clearPlayerData(playerId);
		
		targetPlayer.remove(playerId);
		createPos1.remove(playerId);
		createPos2.remove(playerId);
		unableToJoin.remove(playerId);
		joiningPlayers.remove(playerId);
		signCooldown.remove(playerId);
		handcuffedPlayers.remove(playerId);
		arrestingPlayers.remove(playerId);
		detainingPlayers.remove(playerId);
		passengerForJailList.remove(playerId);
		canAcceptCorrupt.remove(playerId);
		gangsterToCopCorrupt.remove(playerId);
		corruptMoneyAmount.remove(playerId);
		gangInvitesToAccept.remove(playerId);
		wandEnabled.remove(playerId);
		houseDoorCreatingPlayers.remove(playerId);
		
	}
	
	
	/**
	 * @param p The player
	 * @deprecated Do not call this method
	 */
	@Deprecated
	public void clearOldPlayerData(Player p) {
		
		UUID playerId = p.getUniqueId();
		
		oldLoc.remove(playerId);
		oldInv.remove(playerId);
		oldArmor.remove(playerId);
		oldLevel.remove(playerId);
		oldExp.remove(playerId);
		oldHealth.remove(playerId);
		oldFoodlevel.remove(playerId);
		oldPotionEffects.remove(playerId);
		
	}
	
	
	private Player getPlayer(UUID p) {
		
		try {
			
			return Bukkit.getServer().getPlayer(p);	
			
		}
		
		catch (NoSuchMethodError err) {
			
			for (Player p2 : GrandTheftDiamond.getOnlinePlayers()) {
				
				if (p == p2.getUniqueId())
					return p2;
				
			}
			
		}
		
		return null;
		
	}
	
	
	public int getIngameCount() {
		
		return inGame.size();
		
	}
	
	
	public boolean isIngame(Player p) {
		
		return inGame.contains(p);
		
	}
	
	
	public Collection<Player> getIngamePlayers() {
		
		return Collections.unmodifiableCollection(inGame);
		
	}
	
	
	public void setIngame(Player p, boolean newIngame) {
		
		if (newIngame && !isIngame(p))
			inGame.add(p);
		
		else if (!newIngame && isIngame(p))
			inGame.remove(p);
		
		setIsDetainingPlayer(p, false);
		setIsArrestingPlayer(p, false);
		
	}
	
	
	public void setOldLocation(Player p, Location loc) {
		
		oldLoc.put(p.getUniqueId(), loc);
		
	}
	
	
	public Location getOldLocation(Player p) {

		return oldLoc.get(p.getUniqueId());
		
	}
	
	
	public void setOldInventory(Player p, ItemStack[] contents) {
		
		oldInv.put(p.getUniqueId(), contents);
		
	}
	
	
	public ItemStack[] getOldInventory(Player p) {
		
		return oldInv.get(p.getUniqueId());

	}
	
	
	public void setOldArmor(Player p, ItemStack[] armorContents) {
		
		oldArmor.put(p.getUniqueId(), armorContents);
		
	}
	
	
	public ItemStack[] getOldArmor(Player p) {
		
		return oldArmor.get(p.getUniqueId());
		
	}
	
	
	public int getOldLevel(Player p) {
		
		return oldLevel.get(p.getUniqueId());
		
	}
	
	
	public void setOldLevel(Player p, int level) {
		
		oldLevel.put(p.getUniqueId(), level);
		
	}
	
	
	public float getOldExp(Player p) {
		
		return oldExp.get(p.getUniqueId());
		
	}
	
	
	public void setOldExp(Player p, float exp) {
		
		oldExp.put(p.getUniqueId(), exp);
		
	}
	
	
	public double getOldHealth(Player p) {
		
		return oldHealth.get(p.getUniqueId());
		
	}
	
	
	public void setOldHealth(Player p, double health) {
		
		oldHealth.put(p.getUniqueId(), health);
		
	}
	
	
	public int getOldFoodLevel(Player p) {
		
		return oldFoodlevel.get(p.getUniqueId());
		
	}
	
	
	public void setOldFoodLevel(Player p, int foodLevel) {
		
		oldFoodlevel.put(p.getUniqueId(), foodLevel);
		
	}
	
	
	public void setOldPotionEffects(Player p, Collection<PotionEffect> effects) {
		
		oldPotionEffects.put(p.getUniqueId(), new HashSet<PotionEffect>(effects));
		
	}
	
	
	public Collection<PotionEffect> getOldPotionEffects(Player p) {
		
		return oldPotionEffects.get(p.getUniqueId());
		
	}
	
	
	public boolean hasTargetPlayer(Player p) {
		
		return targetPlayer.containsKey(p);
				
	}
	
	
	public Player getTargetPlayer(Player p) {
		
		return getPlayer(targetPlayer.get(p.getUniqueId()));
		
	}
	
	
	public void setTargetPlayer(Player p, Player target) {
		
		if (target != null) {
			
			targetPlayer.put(p.getUniqueId(), target.getUniqueId());
			p.setCompassTarget(target.getLocation());
			
		}
		
		else {
		
			targetPlayer.remove(p.getUniqueId());
			p.setCompassTarget(p.getLocation().getWorld().getSpawnLocation());
			
		}
		
	}
	
	
	public void setCreatePosition(Player p, int pos, Location loc) {
		
		if (pos == 1) 
			createPos1.put(p.getUniqueId(), loc);
		
		else if (pos == 2)
			createPos2.put(p.getUniqueId(), loc);
		
	}

	
	
	public Location getCreatePosition(Player p, int pos) {
		
		if (pos == 1)
			return createPos1.get(p.getUniqueId());
				
		if (pos == 2)
			return createPos2.get(p.getUniqueId());
		
		return null;
		
	}
	
	
	public void setCanJoin(Player p, boolean canJoin) {
		
		if (canJoin)
			unableToJoin.remove(p.getUniqueId());
		
		else
			unableToJoin.add(p.getUniqueId());
		
	}
	
	
	public boolean canJoin(Player p) {
		
		return !unableToJoin.contains(p.getUniqueId());
		
	}
	
	
	
	public void setIsJoining(Player p, boolean newIsJoining) {
		
		if (newIsJoining)
			joiningPlayers.add(p.getUniqueId());
		
		else
			joiningPlayers.remove(p.getUniqueId());
		
	}
	
	
	public boolean isJoining(Player p) {
		
		return joiningPlayers.contains(p.getUniqueId());
		
	}
	
	
	public void setNewCooldown(Player p, int newCooldown) {
		
		signCooldown.put(p.getUniqueId(), newCooldown);
		
	}
	
	
	public void resetNewSignCooldown(Player p) {
		
		signCooldown.remove(p.getUniqueId());
		
	}
	
	
	public boolean hasNewCooldown(Player p) {
		
		if (signCooldown.containsKey(p)) {
			
			return true;
			
		}
		
		return false;
		
	}
	
	
	public int getNewCooldown(Player p) {
		
		if (hasNewCooldown(p)) {
			
			return signCooldown.get(p.getUniqueId());
			
		}
		
		return 0;
		
	}
	
	
	public void setIsHandcuffed(Player p, boolean newCuffed) {
		
		handcuffedPlayers.add(p.getUniqueId());
		
	}
	
	
	public boolean isHandcuffed(Player p) {
		
		return handcuffedPlayers.contains(p.getUniqueId());
		
	}
	
	
	public void setPassengerToJail(Player p, Player passenger) {
		
		if (passenger != null) {
		
			p.setPassenger(passenger);
			passengerForJailList.put(p.getUniqueId(), passenger.getUniqueId());
			
		}
		
		else {
			
			p.setPassenger(null);
			passengerForJailList.remove(p.getUniqueId());
			
		}
		
	}
	
	
	public boolean hasPassengerToJail(Player p) {
		
		if (passengerForJailList.containsKey(p) && p.getPlayer() != null)
			return true;
		
		return false;
	
	}
	
	
	public boolean isPassengerToJail(Player p) {
		
		return passengerForJailList.keySet().contains(p.getUniqueId());
		
	}
	
	
	public Player getCopWhoDetains(Player p)  {
		
		for (UUID otherP : passengerForJailList.keySet()) {
			
			if (passengerForJailList.get(otherP) == p.getUniqueId())
				return getPlayer(passengerForJailList.get(otherP));
			
		}
		
		return null;
		
	}
	
	
	public Player getPassengerToJail(Player p) {
		
		return getPlayer(passengerForJailList.get(p.getUniqueId()));
		
	}
	
	
	public void setIsArrestingPlayer(Player p, boolean newIsArresting) {
		
		detainingPlayers.remove(p.getUniqueId());
		
		if (newIsArresting)
			arrestingPlayers.add(p.getUniqueId());
		
		else
			arrestingPlayers.remove(p.getUniqueId());
		
	}
	
	
	public boolean isArrestingPlayer(Player p) {
		
		return arrestingPlayers.contains(p.getUniqueId());
		
	}
	
	
	public void setIsDetainingPlayer(Player p, boolean newIsDetaining) {
		
		arrestingPlayers.remove(p.getUniqueId());
		
		if (newIsDetaining)
			detainingPlayers.add(p.getUniqueId());
		
		else
			detainingPlayers.remove(p.getUniqueId());
		
	}
	
	
	public boolean isDetainingPlayer(Player p) {
		
		return detainingPlayers.contains(p.getUniqueId());
		
	}
	
	
	public void setNewCorrupt(Player p, Player cop, int moneyAmount) {
		
		canAcceptCorrupt.add(p.getUniqueId());
		gangsterToCopCorrupt.put(cop.getUniqueId(), p.getUniqueId());
		corruptMoneyAmount.put(cop.getUniqueId(), moneyAmount);
		
	}
	
	
	public void removeCorrupt(Player cop) {
		
		canAcceptCorrupt.remove(cop);
		gangsterToCopCorrupt.remove(cop);
		corruptMoneyAmount.remove(cop);
		
	}
	
	
	public boolean canAcceptCorrupt(Player p) {

		return canAcceptCorrupt.contains(p.getUniqueId());
		
	}
	
	
	public Player getGangsterToCopCorrupt(Player p) {
		
		if (gangsterToCopCorrupt.containsKey(p))
			return getPlayer(gangsterToCopCorrupt.get(p.getUniqueId()));
		
		return null;
		
	}
	
	
	public int getCorruptMoneyAmount(Player p) {
		
		if (corruptMoneyAmount.containsKey(p))
				return corruptMoneyAmount.get(p.getUniqueId());
		
		return 0;
		
	}
	
	
	public void addGangInviteToAccept(Player p, String gangName) {
		
		if (gangInvitesToAccept.containsKey(p)) {
		
			List<String> toAccept = gangInvitesToAccept.get(p.getUniqueId());
			
			if (!toAccept.contains(gangName.toLowerCase())) {
				
				if (toAccept.size() > 0)
					toAccept.add(gangName.toLowerCase());
				
				else
					toAccept = Arrays.asList(gangName.toLowerCase());
				
			}
			
			gangInvitesToAccept.put(p.getUniqueId(), toAccept);
			
		}
		
		else {
			
			List<String> newGang = new ArrayList<String>();
			newGang.add(gangName);
			gangInvitesToAccept.put(p.getUniqueId(), newGang);
			
		}
		
	}
	
	
	public void removeGangInviteToAccept(Player p, String gangName) {
		
		if (gangInvitesToAccept.containsKey(p)) {
			
			List<String> toAccept = gangInvitesToAccept.get(p.getUniqueId());
			if (toAccept.contains(gangName.toLowerCase()) && toAccept.size() == 1)
				gangInvitesToAccept.remove(p.getUniqueId());
			
			else if (toAccept.contains(gangName.toLowerCase())) {
				
				toAccept.remove(gangName);
				gangInvitesToAccept.put(p.getUniqueId(), toAccept);
				
			}
			
		}
		
	}
	
	
	public boolean canAcceptGangInvites(Player p) {
		
		if (gangInvitesToAccept.containsKey(p))
			return true;
		
		return false;
		
	}
	
	
	public List<String> getGangInvitesToAccept(Player p) {

		if (gangInvitesToAccept.containsKey(p))
			return gangInvitesToAccept.get(p.getUniqueId());
		
		return null;
		
	}
	
	
	public void setWandEnabled(Player p, boolean newEnabled) {
		
		if (newEnabled)
			wandEnabled.add(p.getUniqueId());
		
		else
			wandEnabled.remove(p.getUniqueId());
		
	}
	
	
	public boolean hasWandEnabled(Player p) {
		
		return wandEnabled.contains(p.getUniqueId());
		
	}
	
	
	public void setIsCreatingDoorOfHouse(Player p, boolean isCreating, String identifier) {
		
		if (isCreating)
			houseDoorCreatingPlayers.put(p.getUniqueId(), identifier);
		
		else
			houseDoorCreatingPlayers.remove(p.getUniqueId());
		
	}
	
	
	public String getDoorCreatingIdentifier(Player p) {
		
		return houseDoorCreatingPlayers.get(p.getUniqueId());
		
	}
	
	
	public boolean isCreatingDoorOfHouse(Player p) {
		
		return houseDoorCreatingPlayers.containsKey(p.getUniqueId());
		
	}
	
}

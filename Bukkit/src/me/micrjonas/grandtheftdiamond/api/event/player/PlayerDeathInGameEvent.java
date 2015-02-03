package me.micrjonas.grandtheftdiamond.api.event.player;

import java.util.ArrayList;
import java.util.Collection;

import me.micrjonas.grandtheftdiamond.api.event.GrandTheftDiamondEvent;
import me.micrjonas.grandtheftdiamond.data.PluginData;

import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.HandlerList;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;
import org.bukkit.inventory.ItemStack;

/**
 * Fired when a player dies in game. {@code org.bukkit.event.player.PlayerDeathEvent} won't be thrown
 */
public class PlayerDeathInGameEvent extends GrandTheftDiamondPlayerEvent {
	
	/**
	 * Returns the {@link Event}'s {@link HandlerList}
	 * @return The {@link Event}'s {@link HandlerList}
	 * @see GrandTheftDiamondEvent#getHandlers(Class)
	 * @see GrandTheftDiamondEvent#getHandlers()
	 */
	public static HandlerList getHandlerList() {
		return getHandlers(PlayerDeathInGameEvent.class);
	}
	
	private final Entity killer;
	private final DamageCause cause;
	private final Collection<ItemStack> drops = new ArrayList<>();
	private Location respawnLocation;
	
	/**
	 * @param who The died {@link Player}
	 * @param cause The death's reason
	 * @param respawnLocation The {@code Location} where the {@link Player} should respawn. Set to {@code null} if you want
	 * 	the plugin to manage the {@link Location}
	 * @throws IllegalArgumentException Thrown if {@code who} or {@code cause} is {@code null}
	 */
	public PlayerDeathInGameEvent(Player who, DamageCause cause, Location respawnLocation) throws IllegalArgumentException {
		super(who);
		killer = null;
		this.cause = cause == null ? DamageCause.CUSTOM : cause;
		for (ItemStack item : getPlayer().getInventory().getContents()) {
			if (item != null) {
				drops.add(item);
			}
		}
		for (ItemStack item : getPlayer().getInventory().getArmorContents()) {
			if (item != null) {
				drops.add(item);
			}
		}
		this.respawnLocation = respawnLocation.clone();
	}
	
	/**
	 * @param who The died {@link Player}
	 * @param cause The death's reason
	 * @param respawnLocation The {@code Location} where the {@link Player} should respawn. Set to {@code null} if you want
	 * 	the plugin to manage the {@link Location}
	 * @param killer An other {@link Player} who killed the died {@link Player}
	 * @throws IllegalArgumentException Thrown if {@code who} or {@code cause} is {@code null}
	 */
	public PlayerDeathInGameEvent(Player who, DamageCause cause, Location respawnLocation, Entity killer) throws IllegalArgumentException {
		super(who);
		this.killer = killer;
		this.cause = cause == null ? DamageCause.CUSTOM : cause;
		for (ItemStack item : getPlayer().getInventory().getContents()) {
			if (item != null) {
				drops.add(item);
			}
		}
		for (ItemStack item : getPlayer().getInventory().getArmorContents()) {
			if (item != null) {
				drops.add(item);
			}
		}
		this.respawnLocation = respawnLocation.clone();
	}
	
	/**
	 * Returns the killer of the Player
	 * @return The killer of the Player, may be null
	 */
	public Entity getKiller() {
		return killer;
	}
	
	/**
	 * Returns the cause of the damage/death
	 * @return A DamageCause value detailing the cause of the damage
	 */
	public DamageCause getCause() {
		return cause;
	}
	
	/**
	 * Returns all dropped items. The Collection is editable to edit the drops
	 * @return All dropped items
	 */
	public Collection<ItemStack> getDrops() {
		return drops;
	}
	
	/**
	 * Returns the respawn location of the player after this death
	 * @return The player's respawn location
	 */
	public Location getRespawnLocation() {
		return respawnLocation;
	}

	/**
	 * Sets the respawn location after this death
	 * @param loc The player's respawn location. Gets stored as a clone
	 * @throws IllegalArgumentException Thrown if the respawn location is {@code null} or not in the arena
	 */
	public void setRespawnLocation(Location loc) throws IllegalArgumentException {
		if (loc == null) {
			throw new IllegalArgumentException("Respawn location is not allowed to be null");
		}
		if (!PluginData.getInstance().inArena(loc)) {
			throw new IllegalArgumentException("Respawn location must be in the arena");
		}
		respawnLocation = loc.clone();
	}
	
}

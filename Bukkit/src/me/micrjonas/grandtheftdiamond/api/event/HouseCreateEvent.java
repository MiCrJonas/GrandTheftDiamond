package me.micrjonas.grandtheftdiamond.api.event;

import me.micrjonas.grandtheftdiamond.house.House;

import org.bukkit.Location;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.bukkit.event.HandlerList;


/**
 * Fired when a {@link Player} tries to create a {@link House}
 */
public class HouseCreateEvent extends AbstractCreateEvent {

	/**
	 * Returns the {@link Event}'s {@link HandlerList}
	 * @return The {@link Event}'s {@link HandlerList}
	 * @see AbstractEvent#getHandlers(Class)
	 * @see AbstractEvent#getHandlers()
	 */
	public static HandlerList getHandlerList() {
		return getHandlers(HouseCreateEvent.class);
	}
	
	private OfflinePlayer owner;
	private Location spawn;
	
	/**
	 * @param creator The {@link Player} who is creating the {@link House}
	 * @param owner The new owner of the {@link House}
	 * @param spawn The spawn {@link Location} of the {@link House}
	 */
	public HouseCreateEvent(Player creator, OfflinePlayer owner, Location spawn) {
		super(creator);
		this.owner = owner;
		this.spawn = spawn.clone();
	}
	
	@Override
	public Player getCreator() {
		return (Player) super.getCreator();
	}
	
	/**
	 * Returns the new owner of the house
	 * @return The new owner of the house
	 */
	public OfflinePlayer getNewOwner() {
		return owner;
	}
	
	/**
	 * Sets the new owner of the house
	 * @param owner The new owner of the house
	 */
	public void setNewOwner(OfflinePlayer owner) {
		this.owner = owner;
	}
	
	/**
	 * Returns a clone of the house's spawn location
	 * @return A clone of the house's spawn location
	 */
	public Location getSpawnLocation() {
		return spawn.clone();
	}
	
	/**
	 * Sets the spawn location of the house
	 * @param spawn The new spawn location. Cannot be {@code null}
	 */
	public void setSpawnLocation(Location spawn) {
		if (spawn != null) {
			this.spawn = spawn.clone();
		}
	}

}

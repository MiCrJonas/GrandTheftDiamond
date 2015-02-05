package me.micrjonas.grandtheftdiamond.api.event.player;

import me.micrjonas.grandtheftdiamond.api.event.AbstractEvent;
import me.micrjonas.grandtheftdiamond.jail.Jail;

import org.bukkit.entity.Player;
import org.bukkit.event.HandlerList;

/**
 * Gets fired when a player gets released from jail
 */
public class PlayerReleaseFromJailEvent extends AbstractPlayerEvent {
	
	/**
	 * Returns the {@link Event}'s {@link HandlerList}
	 * @return The {@link Event}'s {@link HandlerList}
	 * @see AbstractEvent#getHandlers(Class)
	 * @see AbstractEvent#getHandlers()
	 */
	public static HandlerList getHandlerList() {
		return getHandlers(PlayerReleaseFromJailEvent.class);
	}
	
	private final Jail jail;
	private final int jailedTime;
	
	public PlayerReleaseFromJailEvent(Player who, Jail jail, int jailedTime) {
		super(who);
		
		this.jail = jail;
		this.jailedTime = jailedTime;
	}
	
    
    /**
     * Returns the jail the player was jailed in
     * @return The jail the player was jailed in
     */
    public Jail getJail() {
    	return jail;
    }
    
    
    /**
     * Returns the time the player was jailed
     * @return The time the player was jailed
     */
    public int getTimeJailed() {
    	return jailedTime;
    }

}

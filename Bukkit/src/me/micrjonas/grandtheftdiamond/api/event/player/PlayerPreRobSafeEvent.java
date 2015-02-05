package me.micrjonas.grandtheftdiamond.api.event.player;

import me.micrjonas.grandtheftdiamond.api.event.AbstractEvent;
import me.micrjonas.grandtheftdiamond.api.event.RobEvent;
import me.micrjonas.grandtheftdiamond.rob.Robable;

import org.bukkit.entity.Player;
import org.bukkit.event.HandlerList;

/**
 * Gets fired before a player starts to rob a safe
 */
public class PlayerPreRobSafeEvent extends CancellablePlayerEvent implements RobEvent {
	
	/**
	 * Returns the {@link Event}'s {@link HandlerList}
	 * @return The {@link Event}'s {@link HandlerList}
	 * @see AbstractEvent#getHandlers(Class)
	 * @see AbstractEvent#getHandlers()
	 */
	public static HandlerList getHandlerList() {
		return getHandlers(PlayerPreRobSafeEvent.class);
	}
	
	private final Robable robbed;
	private int time;
	
	public PlayerPreRobSafeEvent(Player who, Robable robbed, int time) {
		super(who);
		this.robbed = robbed;
		this.time = time;
	}
	
	@Override
	public Robable getRobbed() {
		return robbed;
	}
	
	/**
	 * Returns the time how many time a player needs to rob the safe
	 * @return The time a player need to rob the safe
	 */
	public int getRobTime() {
		return time;
	}
	
	
	/**
	 * Sets the time a player needs to rob the safe
	 * @param newTime The new time a player needs to rob the safe
	 * @throws IllegalArgumentException Thrown if the time is lower than 0
	 */
	public void setRobTime(int newTime)  throws IllegalArgumentException {
		time = newTime;
	}
	
}

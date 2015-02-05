package me.micrjonas.grandtheftdiamond.api.event.player;

import me.micrjonas.grandtheftdiamond.api.event.AbstractEvent;
import me.micrjonas.grandtheftdiamond.api.event.cause.CauseEvent;
import me.micrjonas.grandtheftdiamond.api.event.cause.WantedLevelChangeCause;

import org.bukkit.entity.Player;
import org.bukkit.event.HandlerList;


/**
 * Gets fired when the wanted level of a player gets changed
 */
public class PlayerWantedLevelChangeEvent extends AbstractCancellablePlayerEvent implements CauseEvent<WantedLevelChangeCause> {

	/**
	 * Returns the {@link Event}'s {@link HandlerList}
	 * @return The {@link Event}'s {@link HandlerList}
	 * @see AbstractEvent#getHandlers(Class)
	 * @see AbstractEvent#getHandlers()
	 */
	public static HandlerList getHandlerList() {
		return getHandlers(PlayerWantedLevelChangeEvent.class);
	}
	
	private final WantedLevelChangeCause cause;
	private final int oldWantedLevel;
	private int newWantedLevel;
	
	public PlayerWantedLevelChangeEvent(Player who,
			WantedLevelChangeCause cause,
			int oldWantedLevel, 
			int newWantedLevel) {
		
		super(who);
		this.cause = cause == null ? WantedLevelChangeCause.CUSTOM : cause;
		this.oldWantedLevel = oldWantedLevel;
		this.newWantedLevel = newWantedLevel;
	}
	
	@Override
	public WantedLevelChangeCause getCause() {
		return cause;
	}
	
	/**
	 * Returns the wanted level before fire the event
	 * @return The wanted level before fire the event
	 */
	public int getOldWantedLevel() {
		return oldWantedLevel;
	}
	
	
	/**
	 * Returns the wanted level after the event
	 * @return
	 */
	public int getNewWantedLevel() {
		return newWantedLevel;
	}
	
	
	/**
	 * Sets the new wanted level for the event
	 * @param newWantedLevel The new wanted level after the event
	 */
	public void setNewWantedLevel(int newWantedLevel) {
		this.newWantedLevel = newWantedLevel;
	}

}

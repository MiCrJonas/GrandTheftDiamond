package me.micrjonas.grandtheftdiamond.api.event.player;

import me.micrjonas.grandtheftdiamond.api.event.AbstractEvent;
import me.micrjonas.grandtheftdiamond.api.event.cause.CauseEvent;
import me.micrjonas.grandtheftdiamond.api.event.cause.LeaveReason;

import org.bukkit.entity.Player;
import org.bukkit.event.HandlerList;


/**
 * Gets fired when a player tries to leave the game or when a player gets kicked out of the game
 */
public class PlayerLeaveGameEvent extends AbstractCancellablePlayerEvent implements CauseEvent<LeaveReason> {

	/**
	 * Returns the {@link Event}'s {@link HandlerList}
	 * @return The {@link Event}'s {@link HandlerList}
	 * @see AbstractEvent#getHandlers(Class)
	 * @see AbstractEvent#getHandlers()
	 */
	public static HandlerList getHandlerList() {
		return getHandlers(PlayerLeaveGameEvent.class);
	}

	private final boolean couldBeCancelled;
	private boolean isCancelledBecauseOnlyGTDMode;
	
	private final LeaveReason reason;
	private String leaveMessageGlobal;
	private String leaveMessagePlayer;
	
	public PlayerLeaveGameEvent(Player who,
			boolean isCancelledBecauseOnlyGTDMode,
			boolean couldBeCancelled,
			LeaveReason reason,
			String leaveMessageGlobal,
			String leaveMessagePlayer) {
		
		super(who, isCancelledBecauseOnlyGTDMode);
		
		if (couldBeCancelled) {
			this.isCancelledBecauseOnlyGTDMode = isCancelledBecauseOnlyGTDMode;
		}
		this.couldBeCancelled = couldBeCancelled;
		this.reason = reason;
		this.leaveMessageGlobal = leaveMessageGlobal;
		this.leaveMessagePlayer = leaveMessagePlayer;
		
		if (this.isCancelledBecauseOnlyGTDMode) {
			setCancelled(true);
		}
	}

	
	@Override
	public void setCancelled(boolean newCancelled) {
		if (couldBeCancelled && newCancelled) {
			super.setCancelled(true);
		}
	}

	@Override
	public LeaveReason getCause() {
		return reason;
	}
	
	/**
	 * Return whether the event is cancellable
	 * @return True if the event is cancellable, else false
	 */
    public boolean isCancellable() {
    	return couldBeCancelled;
    }
	
	
    /**
     * Returns whether the event is cancelled because you cannot leave the game in only GTD mode
     * @return True if the event is cancelled because you cannot leave the game in GTD mode, else false
     */
	public boolean isCancelledBecauseOnlyGTDMode() {
		return isCancelledBecauseOnlyGTDMode;
	}
	
	
	/**
	 * Returns the leave message which will be send to all ingame players
	 * @return The leave message which will be send to all ingame players
	 */
	public String getLeaveMessageGlobal() {
		return leaveMessageGlobal;
	}
	
	
	/**
	 * Sets the leave message which will be send to all ingame players
	 * Set to null to disable the message
	 * @param msg The new global leave message
	 */
	public void setLeaveMessageGlobal(String msg) {
		leaveMessageGlobal = msg;
	}
	
	
	/**
	 * Returns the leave message which will be send to the player
	 * @return The leave message which will be send to the player
	 */
	public String getLeaveMessagePlayer() {
		return leaveMessagePlayer;
	}
	
	
	/**
	 * Sets the leave message which will be send to the player
	 * Set to null to disable the message
	 * @param msg The new leave message which will be send to the player
	 */
	public void setLeaveMessagePlayer(String msg) {
		leaveMessagePlayer = msg;
	}

}

package me.micrjonas.grandtheftdiamond.api.event.player;

import me.micrjonas.grandtheftdiamond.api.event.AbstractEvent;
import me.micrjonas.grandtheftdiamond.api.event.cause.CauseEvent;
import me.micrjonas.grandtheftdiamond.api.event.cause.JailReason;
import me.micrjonas.grandtheftdiamond.jail.Jail;

import org.bukkit.entity.Player;
import org.bukkit.event.HandlerList;


/**
 * Gets fixed when a player gets jailed
 */
public class PlayerJailEvent extends AbstractCancellablePlayerEvent implements CauseEvent<JailReason> {
	
	/**
	 * Returns the {@link Event}'s {@link HandlerList}
	 * @return The {@link Event}'s {@link HandlerList}
	 * @see AbstractEvent#getHandlers(Class)
	 * @see AbstractEvent#getHandlers()
	 */
	public static HandlerList getHandlerList() {
		return getHandlers(PlayerJailEvent.class);
	}
	
	private final Player cop;
	private int time;
	private Jail jail;
	private final JailReason reason;

	/**
	 * Default constructor
	 * @param who The jailed {@link Player}
	 * @param cop The {@link Player} who jailed
	 * @param jail The {@link Jail}
	 * @param time The jail time in seconds
	 * @param reason The event's reason. {@code null} means {@link JailReason#CUSTOM}
	 */
	public PlayerJailEvent(Player who, Player cop, Jail jail, int time, JailReason reason) {
		super(who);
		this.cop = cop;
		this.jail = jail;
		this.time = time;
		this.reason = reason == null ? JailReason.CUSTOM : reason;
	}
	
	
	@Override
	public JailReason getCause() {
		return reason;
	}
	
	/**
	 * Returns whether the player was jailed by a cop
	 * @return True if the player was jailed by a cop, else false
	 */
	public boolean jailedByCop() {
		return cop != null;
	}
	
	/**
	 * Returns the cop which jailed the player; null if player was not jailed by a cop
	 * @return The cop which jailed the player; null if player was not jailed by a cop
	 */
	public Player getCop() {
		return cop;
	}
	
	/**
	 * Returns the jail the player will be jailed in
	 * @return The jail the player will be jailed in
	 */
	public Jail getJail() {
		return jail;
	}
	
	/**
	 * Sets the jail the player will be jailed in
	 * @param newJail The jail the player will be jailed in
	 */
	public void setJail(Jail newJail) {
		jail = newJail;
	}
	
	/**
	 * Returns the time the player will be jailed
	 * @return The time the player will be jailed
	 */
	public int getTime() {
		return time;
	}
	
	/**
	 * Sets the time the player will be jailed
	 * @param newTime The new jail time
	 */
	public void setTime(int newTime) {
		time = newTime;
	}

}

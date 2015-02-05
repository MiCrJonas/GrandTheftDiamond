package me.micrjonas.grandtheftdiamond.api.event.player;

import java.util.ArrayList;
import java.util.List;

import me.micrjonas.grandtheftdiamond.Team;
import me.micrjonas.grandtheftdiamond.api.event.AbstractEvent;
import me.micrjonas.grandtheftdiamond.api.event.cause.CauseEvent;
import me.micrjonas.grandtheftdiamond.api.event.cause.JoinReason;
import me.micrjonas.grandtheftdiamond.item.Kit;

import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.HandlerList;


/**
 * Gets fired when a player tries to join the game
 */
public class PlayerJoinGameEvent extends AbstractCancellablePlayerEvent implements CauseEvent<JoinReason> {

	/**
	 * Returns the {@link Event}'s {@link HandlerList}
	 * @return The {@link Event}'s {@link HandlerList}
	 * @see AbstractEvent#getHandlers(Class)
	 * @see AbstractEvent#getHandlers()
	 */
	public static HandlerList getHandlerList() {
		return getHandlers(PlayerJoinGameEvent.class);
	}
	
	
	private boolean isCancelledBecauseBanned = false;
	
	private Location joinLocation;
	private String joinMessageGlobal;
	private String joinMessagePlayer;
	private Team team;
	private final JoinReason reason;
	private final List<Kit> kits;
	
	public PlayerJoinGameEvent(Player who,
			boolean defaultCancelled,
			boolean isCancelledBecauseBanned,
			Location joinLocation,
			String joinMessageGlobal,
			String joinMessagePlayer,
			Team team, 
			JoinReason reason,
			List<Kit> kits) 
	{
		
		super(who, isCancelledBecauseBanned);
		
		this.isCancelledBecauseBanned = isCancelledBecauseBanned;
		this.joinLocation = joinLocation;
		this.joinMessageGlobal = joinMessageGlobal.toLowerCase().equals("null") ? null : joinMessageGlobal;
		this.joinMessagePlayer = joinMessagePlayer.toLowerCase().equals("null") ? null : joinMessagePlayer;
		this.team = team;
		this.reason = reason;
		this.kits = kits != null ? kits : new ArrayList<>();
	}
	
	@Override
	public JoinReason getCause() {
		return reason;
	}
	
	
    /**
     * Gets whether the event is cancelled because the player is banned
     * @return True if the event is cancelled because the player is banned, else false
     */
	public boolean isCancelledBecauseBanned() {
		return isCancelledBecauseBanned;
	}
	
	
	/**
	 * Returns the location where the player spawns in the arena
	 * @return The spawn location of the player in the arena
	 */
	public Location getJoinLocation() {
		return joinLocation;
	}
	
	
	/**
	 * Sets the location where the player spawns in the arena
	 * @param joinLocation The new spawn {@link Location} of the player in the arena
	 */
	public void setJoinLocation(Location joinLocation) {
		if (joinLocation == null) {
			throw new IllegalArgumentException("njoinLocation is not allowed to be null");
		}
		this.joinLocation = joinLocation;
	}
	
	
	/**
	 * Returns the join message which will be send to all ingame players
	 * Set to null to disable the message
	 * @return The global join message
	 */
	public String getJoinMessageGlobal() {
		return joinMessageGlobal;
	}
	
	
	/**
	 * Sets the global join message
	 * @param msg The new global join message
	 */
	public void setJoinMessageGlobal(String msg) {
		joinMessageGlobal = msg;
	}
	
	
	/**
	 * Returns the join message which will be send to the player
	 * Set to null to disable the message
	 * @return The global join message
	 */
	public String getJoinMessagePlayer() {
		return joinMessagePlayer;
	}
	
	
	/**
	 * Sets the join message which will be send to the player
	 * @param msg The new global join message
	 */
	public void setJoinMessagePlayer(String msg) {
		joinMessagePlayer = msg;
	}
	
	
	/**
	 * Returns the (new) team of the player
	 * @return The (new) team of the player
	 */
	public Team getNewTeam() {
		return team;
	}
	
	
	/**
	 * Sets the (new) team of the player
	 * @param team The new team of the player
	 * @throws IllegalArgumentException Thrown if team is {@code null}
	 */
	public void setNewTeam(Team team) throws IllegalArgumentException {
		if (team == null) {
			throw new IllegalArgumentException("team is not allowed to be null");
		}
		this.team = team;
	}
	
	
	/**
	 * Returns a {@link Link} of all {@link Kit}s the player will receive after joining.<br>
	 * 	The player only gets the {@link Kit}s if this is his first time joining the game. The {@link Link} is editable
	 * @return A list of the name of all {@link Kit}s the player will get after joining
	 */
	public List<Kit> getKits() {
		return kits;
	}

}

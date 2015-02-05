package me.micrjonas.grandtheftdiamond.api.event.player;

import me.micrjonas.grandtheftdiamond.api.event.AbstractEvent;
import me.micrjonas.grandtheftdiamond.api.event.RobEvent;
import me.micrjonas.grandtheftdiamond.rob.Robable;

import org.bukkit.entity.Player;
import org.bukkit.event.HandlerList;

public class PlayerRobCancelEvent extends GrandTheftDiamondPlayerEvent implements RobEvent {

	/**
	 * Returns the {@link Event}'s {@link HandlerList}
	 * @return The {@link Event}'s {@link HandlerList}
	 * @see AbstractEvent#getHandlers(Class)
	 * @see AbstractEvent#getHandlers()
	 */
	public static HandlerList getHandlerList() {
		return getHandlers(PlayerRobCancelEvent.class);
	}
	
	private final Robable robbed;
	
	/**
	 * Default constructor
	 * @param robber The {@code Player} who robbed the object
	 * @param robbed The robbed object
	 * @throws IllegalArgumentException Thrown if {@code robber} or {@code robbed} is {@code null}
	 */
	public PlayerRobCancelEvent(Player robber, Robable robbed) throws IllegalArgumentException {
		super(robber);
		if (robbed == null) {
			throw new IllegalArgumentException("Robbed object cannot be null");
		}
		this.robbed = robbed;
	}

	@Override
	public Robable getRobbed() {
		return robbed;
	}
	
}

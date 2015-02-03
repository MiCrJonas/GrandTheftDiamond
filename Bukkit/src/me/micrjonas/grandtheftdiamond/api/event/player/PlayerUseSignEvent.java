package me.micrjonas.grandtheftdiamond.api.event.player;

import me.micrjonas.grandtheftdiamond.api.event.GrandTheftDiamondEvent;
import me.micrjonas.grandtheftdiamond.sign.SignType;

import org.bukkit.entity.Player;
import org.bukkit.event.HandlerList;

public class PlayerUseSignEvent extends CancellablePlayerEvent {

	/**
	 * Returns the {@link Event}'s {@link HandlerList}
	 * @return The {@link Event}'s {@link HandlerList}
	 * @see GrandTheftDiamondEvent#getHandlers(Class)
	 * @see GrandTheftDiamondEvent#getHandlers()
	 */
	public static HandlerList getHandlerList() {
		return getHandlers(PlayerUseSignEvent.class);
	}
	
	private final SignType signType;
	
	public PlayerUseSignEvent(Player who, SignType signType) {
		super(who);	
		this.signType = signType;
	}

	
	public SignType getSignType() {
		return signType;
	}
	
}

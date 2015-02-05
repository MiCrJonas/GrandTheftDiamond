package me.micrjonas.grandtheftdiamond.api.event.player;

import me.micrjonas.grandtheftdiamond.api.event.AbstractEvent;
import me.micrjonas.grandtheftdiamond.item.pluginitem.InteractablePluginItem;

import org.bukkit.entity.Player;
import org.bukkit.event.HandlerList;


/**
 * Gets fired when a player tries to use an plugin item
 */
public class PlayerUseItemEvent extends CancellablePlayerEvent {

	/**
	 * Returns the {@link Event}'s {@link HandlerList}
	 * @return The {@link Event}'s {@link HandlerList}
	 * @see AbstractEvent#getHandlers(Class)
	 * @see AbstractEvent#getHandlers()
	 */
	public static HandlerList getHandlerList() {
		return getHandlers(PlayerUseItemEvent.class);
	}
	
	
	private final InteractablePluginItem interacted;
	private final boolean cancelledNoFuel;
	
	public PlayerUseItemEvent(Player p, InteractablePluginItem interacted, boolean cancelledNoFuel) {
		super(p, cancelledNoFuel);
		this.cancelledNoFuel = cancelledNoFuel;
		this.interacted = interacted;
	}

	/**
	 * Returns whether the event is cancelled because the player do not have any fuel/ammo to use the object
	 * @return True if the event is cancelled because the player does not have any fuel/ammo, else false
	 */
	public boolean isCancelledNoFuel() {
		return cancelledNoFuel;
	}
	
	public InteractablePluginItem getInteracted() {
		return interacted;
	}

}

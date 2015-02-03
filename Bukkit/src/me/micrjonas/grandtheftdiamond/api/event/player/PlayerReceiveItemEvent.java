package me.micrjonas.grandtheftdiamond.api.event.player;

import me.micrjonas.grandtheftdiamond.api.event.GrandTheftDiamondEvent;
import me.micrjonas.grandtheftdiamond.item.pluginitem.PluginItem;

import org.bukkit.entity.Player;
import org.bukkit.event.HandlerList;

public class PlayerReceiveItemEvent extends CancellablePlayerEvent {

	/**
	 * Returns the {@link Event}'s {@link HandlerList}
	 * @return The {@link Event}'s {@link HandlerList}
	 * @see GrandTheftDiamondEvent#getHandlers(Class)
	 * @see GrandTheftDiamondEvent#getHandlers()
	 */
	public static HandlerList getHandlerList() {
		return getHandlers(PlayerReceiveItemEvent.class);
	}
	
	private final PluginItem item;
	private int amount;
	
	public PlayerReceiveItemEvent(Player who, PluginItem item, int amount) {
		super(who);
		this.item = item;
		this.amount = amount;
	}
	
	
	public PluginItem getItem() {
		return item;
	}
	
	public int getAmount() {
		return amount;
	}
	
	public void setAmount(int amount) throws IllegalArgumentException {
		if (amount < 1 || amount > 64)  {
			throw new IllegalArgumentException("Amount must be between 1 and 64. Given: " + amount);
		}
		this.amount = amount;
	}

}

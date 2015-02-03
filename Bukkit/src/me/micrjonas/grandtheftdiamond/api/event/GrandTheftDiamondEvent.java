package me.micrjonas.grandtheftdiamond.api.event;

import java.util.HashMap;
import java.util.Map;

import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

/**
 * Super class of all Grand Theft Diamond events
 */
public abstract class GrandTheftDiamondEvent extends Event {

	private final static Map<Class<?>, HandlerList> handlers = new HashMap<>();
	
	/**
	 * Returns the {@code HandlerList} of the given Grand Theft Diamond event class
	 * @param e The class of the event
	 * @return The {@code HandlerList} of the given Grand Theft Diamond event class
	 */
	public static HandlerList getHandlers(Class<? extends GrandTheftDiamondEvent> e) {
		HandlerList handlers = GrandTheftDiamondEvent.handlers.get(e);
		if (handlers == null) {
			handlers = new HandlerList();
			GrandTheftDiamondEvent.handlers.put(e, handlers);
		}
		return handlers;
	}
	
	/**
	 * Returns the {@link Event}'s {@link HandlerList}. Each {@link Event} has its own {@link HandlerList}
	 * @return The {@link Event}'s {@link HandlerList}
	 * @see GrandTheftDiamondEvent#getHandlers(Class)
	 */
	@Override
	public HandlerList getHandlers() {
		return getHandlers(getClass());
	}

}

package me.micrjonas.grandtheftdiamond.api.event;

import org.bukkit.command.CommandSender;

/**
 * Super class of all Events that get fired when something gets created
 */
public abstract class CreateEvent extends CancellableEvent {
	
	private final CommandSender creator;
	
	/**
	 * Default constructor
	 * @param creator The creator of the new created object
	 * @throws IllegalArgumentException Thrown if {@code creator} is null
	 */
	protected CreateEvent(CommandSender creator) throws IllegalArgumentException {
		if (creator  == null) {
			throw new IllegalArgumentException("Creator is not allowed to be null");
		}
		this.creator = creator;
	}
	
	/**
	 * Returns the creator of the created object
	 * @return The creator of the created object, never null
	 */
	protected CommandSender getCreator() {
		return creator;
	}

}

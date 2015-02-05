package me.micrjonas.grandtheftdiamond.api.event;

import me.micrjonas.grandtheftdiamond.rob.Robable;

/**
 * Interface for all {@code Event}s related to the plugin's robbing system
 */
public interface RobEvent {
	
	/**
	 * Returns the (currently) robbed object
	 * @return The robbed object. Not allowed to be {@code null}
	 */
	Robable getRobbed();

}

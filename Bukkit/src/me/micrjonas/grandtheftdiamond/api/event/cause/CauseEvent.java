package me.micrjonas.grandtheftdiamond.api.event.cause;

/**
 * Represents an event with a reason of a specific type
 * @param <T> The reason's type
 */
public interface CauseEvent<T extends EventCause> {

	/**
	 * Returns the cause of the event
	 * @return The event's cause
	 */
	public T getCause();
	
}

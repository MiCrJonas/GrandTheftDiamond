package me.micrjonas.grandtheftdiamond.util;


/**
 * Represents a direction with rotation and pitch value. Each object is immutable
 */
public class Direction implements Immutable {
	
	/**
	 * {@code Direction} with value 0 for rotation and pitch. (South)
	 */
	public final static Direction NULL_DIRECTION = new Direction(0, 0);
	
	private final float rotation, pitch;
	
	/**
	 * Creates a new object with a rotation and a pitch value
	 * @param rotation The rotation value
	 * @param pitch The pitch value
	 */
	public Direction(float rotation, float pitch) {
		this.rotation = rotation;
		this.pitch = pitch;
	}

	/**
	 * Returns the rotation value of the {@code Direction}
	 * @return The rotation value
	 */
	public float getRotation() {
		return rotation;
	}
	
	/**
	 * Returns the pitch value of the {@code Direction}
	 * @return The pitch value
	 */
	public float getPitch() {
		return pitch;
	}
	
}

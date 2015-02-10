package me.micrjonas.grandtheftdiamond.util;


/**
 * Represents a direction with rotation and pitch value in degrees.<br>
 * 	The pitch value goes from -90 degrees to 90 degrees (-90 degrees is downward, 90 degrees is upward).
 * 	The to rotation value goes from -180 degrees to 180 degrees (Minecraft default). The values will be converted
 * 	to this format when the constructor.<br>
 * 	Each object is immutable
 */
public class Direction implements Immutable {
	
	/**
	 * {@code Direction} with value 0 for rotation and pitch. (South)
	 */
	public final static Direction NULL_DIRECTION = new Direction(0, 0);
	
	private final float rotation, pitch;
	
	/**
	 * Creates a new object with a rotation and a pitch value
	 * @param rotation The rotation value. The value will be converted to the -180 to 180 degrees format
	 * @param pitch The pitch value. The value will be converted to the -90 to 90 degrees format. 100 degrees will be converted to
	 * 	80 degrees. 190 degrees will be converted to -10 degrees
	 */
	public Direction(float rotation, float pitch) {
		if (rotation > 180) {
			rotation -= 360;
			while (rotation > 180) {
				rotation -= 360;
			}
		}
		else if (rotation < 180) {
			rotation += 360;
			while (rotation < 180) {
				rotation += 360;
			}
		}
		this.rotation = rotation;
		this.pitch = (float) Math.toDegrees(Math.asin(Math.sin(Math.toRadians(pitch)))); // Makes 100° to 80° and 460° to 80°
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + Float.floatToIntBits(pitch);
		result = prime * result + Float.floatToIntBits(rotation);
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj == null) {
			return false;
		}
		if (getClass() != obj.getClass()) {
			return false;
		}
		Direction other = (Direction) obj;
		if (Float.floatToIntBits(pitch) != Float.floatToIntBits(other.pitch)) {
			return false;
		}
		if (Float.floatToIntBits(rotation) != Float.floatToIntBits(other.rotation)) {
			return false;
		}
		return true;
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

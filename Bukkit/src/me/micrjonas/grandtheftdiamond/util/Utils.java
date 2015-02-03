package me.micrjonas.grandtheftdiamond.util;

/**
 * Contains some unspecified utility classes
 */
public class Utils {
	
	private Utils() { }
	
	
	/**
	 * Returns a random number from inclusive {@code min} to inclusive {@code max}
	 * @param min The minimum value of the random number
	 * @param max The maximum number of the random number
	 * @return The random number from inclusive {@code min} to inclusive {@code max}. Returns {@code min} if {@code min}
	 * 	is larger than {@code max}
	 */
	public static int random(int min, int max) {
		if (max <= min) {
			return min;
		}
		return min + (int) (Math.round(Math.random() * (max - min)));
	}
	
	/**
	 * Returns a random double from inclusive {@code min} to inclusive {@code max}
	 * @param min The minimum value of the random double
	 * @param max The maximum number of the random double
	 * @return The random double from inclusive {@code min} to inclusive {@code max}. Returns {@code min} if {@code min}
	 * 	is larger than {@code max}
	 */
	public static double random(double min, double max) {
		if (max <= min) {
			return min;
		}
		return min + Math.random() * (max - min);
	}

}

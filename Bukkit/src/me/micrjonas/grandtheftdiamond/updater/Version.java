package me.micrjonas.grandtheftdiamond.updater;

import java.util.Arrays;

import me.micrjonas.grandtheftdiamond.util.Immutable;
import me.micrjonas.grandtheftdiamond.util.Nameable;


/**
 * Represents a version of the plugin. Each object is immutable.
 * Includes some methods to compare two versions, used for the updater
 */
public class Version implements Comparable<Version>, Immutable, Nameable {
	
	private final int[] parts = new int[3];
	private ReleaseType type;
	private String asString;
	
	/**
	 * Creates a new object with the given release type and an integer array for the main version and sub versions
	 * @param type The release type of the version
	 * @param parts The main version and sub versions {@code new int[]&#123;1, 2, 3&#125;} => '1.2.3'
	 * @throws IllegalArgumentException Thrown if
	 * <ul>
	 * 	<li>{@code type} is {@code null}</li>
	 * 	<li>{@code parts} is {@code null}</li>
	 * 	<li>Length of {@code parts} is != 3</li>
	 * 	<li>Any index of {@code parts} is < 0</li>
	 * </ul>
	 */
	public Version(ReleaseType type, int[] parts) throws IllegalArgumentException {
		if (type == null) {
			throw new IllegalArgumentException("type is not allowed to be null");
		}
		if (parts.length != 3) {
			throw new IllegalArgumentException("length of parts must be 3");
		}
		for (int i = 0; i < 3; i++) {
			if (parts[i] >= 0) {
				this.parts[i] = parts[i];
			}
			else {
				throw new IllegalArgumentException("parts[" + i + "] must be >= 0");
			}
		}
		this.type = type;
	}
	
	/**
	 * Creates a new object with the value of the given String
	 * @param s Version as String
	 */
	public Version(String s) {
		if (s.contains("_")) {
			try {
				type = ReleaseType.valueOf(s.substring(0, s.indexOf("_")).toUpperCase());
			}
			catch (IllegalArgumentException ex) {
				throw new IllegalArgumentException("Input string: " + s);
			}
		}
		else {
			type = ReleaseType.RELEASE;
		}
		s = s.substring(s.indexOf("_") + 1, s.length());
		String[] split = s.split("\\.");
		if (split.length == 3) {
			for (int i = 0; i < 3; i++) {
				try {
					parts[i] = Integer.parseInt(split[i]);
				}
				catch (NumberFormatException ex) {
					throw new IllegalArgumentException("Input string: " + s);
				}
			}
		}
		else {
			throw new IllegalArgumentException("Input string: " + s);
		}
	}
	
	@Override
	public String getName() {
		if (asString == null) {
			asString = (type == ReleaseType.RELEASE ? "" : (type.name().substring(0, 1) + type.name().substring(1, type.name().length()) + "_")) + parts[0] + "." + parts[1] + "." + parts[2];
		}
		return asString;
	}
	
	@Override
	public String toString() {
		return getName();
	}
	
	
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + Arrays.hashCode(parts);
		result = prime * result + ((type == null) ? 0 : type.hashCode());
		return result;
	}
	
	/**
	 * Checks whether the versions represent the same plugin version
	 * @param v The version to check
	 * @return True if the versions are equal, else false
	 */
	@Override
	public boolean equals(Object obj) {
		if (obj == this) {
			return true;
		}
		if (obj.getClass() != getClass()) {
			return false;
		}
		Version other = (Version) obj;
		for (int i = 0; i < parts.length; i++) {
			if (parts[i] != other.parts[i]) {
				return false;
			}
		}
		return type == other.type;
	}

	/**
	 * Returns the {@code Version}'s {@link ReleaseType}
	 * @return the {@link ReleaseType} of the {@code Version}
	 */
	public ReleaseType getReleaseType() {
		return type;
	}
	
	/**
	 * Checks whether this version is before v
	 * @param v Version to check
	 * @return True if this is before v
	 */
	public boolean before(Version v) {
		if (this.type != v.type) {
			return this.type.compareTo(v.type) < 0;
		}
		if (v.parts[0] > this.parts[0]) {
			return true;
		}
		if (v.parts[0] == this.parts[0]) {
			if (v.parts[1] > this.parts[1]) {
				return true;
			}
			if (v.parts[1] == this.parts[1]) {
				if (v.parts[2] > this.parts[2]) {
					return true;
				}
			}
		}
		return false;
	}
	
	/**
	 * Equivalent to {@code v.before(this)}
	 * @param v Version to check
	 * @return True if this is after v
	 */
	public boolean after(Version v) {
		return v.before(this);
	}

	@Override
	public int compareTo(Version v) throws NullPointerException {
		if (v == null) {
			throw new NullPointerException("v is not allowed to be null");
		}
		if (type != v.type) {
			return type.compareTo(v.type);
		}
		for (int i = 0; i < parts.length; i++) {
			if (parts[i] != v.parts[i]) {
				return parts[i] - v.parts[i];
			}
		}
		return 0;
	}

}

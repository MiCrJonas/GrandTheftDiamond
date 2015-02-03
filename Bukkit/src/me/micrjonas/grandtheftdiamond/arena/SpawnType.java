package me.micrjonas.grandtheftdiamond.arena;

import me.micrjonas.grandtheftdiamond.jail.Jail;

/**
 * A type of a spawn point
 */
public enum SpawnType {
	
	/**
	 * Civilian spawn, multiple spawns are possible
	 */
	CIVILIAN(true), 
	
	/**
	 * Cop spawn, multiple spawns are possible
	 */
	COP(true), 
	
	/**
	 * Default spawn, multiple spawns are not possible
	 */
	DEFAULT(false), 
	
	/**
	 * Hospitals/hospital spawns, multiple spawns are possible
	 */
	HOSPITAL(true),
	
	/**
	 * A {@link Jail}'s, multiple spawns are not possible
	 */
	JAIL(false); 
	
	
	/**
	 * Returns an array of all types which can have multiple spawns
	 * @return An array of all types which can have multiple spawns
	 */
	public static SpawnType[] multipleSpawnPossibles() {
		return new SpawnType[]{CIVILIAN, COP, HOSPITAL};
	}
	
	private boolean multipleSpawnsPossible;
	
	private SpawnType(boolean multipleSpawnsPossible) {
		this.multipleSpawnsPossible = multipleSpawnsPossible;
	}
	
	/**
	 * Returns whether multiple spawns are allowed or not for this {@code SpawnType}
	 * @return True if multiple spawns are allowed, else {@code false}
	 */
	public boolean multipleSpawnsPossible() {
		return multipleSpawnsPossible;
	}

}

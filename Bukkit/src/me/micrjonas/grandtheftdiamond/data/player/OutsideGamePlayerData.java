package me.micrjonas.grandtheftdiamond.data.player;

import org.bukkit.entity.Player;

public class OutsideGamePlayerData extends PlayerDataStorage {

	private int level;
	private float exp;
	
	public OutsideGamePlayerData(Player p) {
		super(p);
	}
	
	/**
	 * Returns the Player's experience level
	 * @return The Player's experience level
	 */
	public int getLevel() {
		return level;
	}
	
	/**
	 * Sets the Player's exp level
	 * @param level The Player's exp level
	 * @throws IllegalArgumentException If level is < 0
	 */
	public void setLevel(int level) throws IllegalArgumentException {
		if (level < 0)  {
			throw new IllegalArgumentException("Level cannot be < 0");
		}
		this.level = level;
	}
	
	/**
	 * Returns the Player's experience
	 * @return The Player's experience
	 */
	public float getExp() {
		return exp;
	}
	
	/**
	 * Sets the Player's exp
	 * @param exp The Player's exp
	 * @throws IllegalArgumentException If exp is < 0 or > 1
	 */
	public void setExp(float exp) throws IllegalArgumentException {
		if (exp < 0) {
			throw new IllegalArgumentException("exp cannot be < 0");
		}
		if (exp > 1) {
			throw new IllegalArgumentException("exp cannot be > 1");
		}
		this.exp = exp;
	}

}

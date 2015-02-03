package me.micrjonas.grandtheftdiamond.data.player;

import java.util.UUID;

/**
 * A class which saves some player data should implement this interface and register itself with
 * {@code GrandTheftDiamond#registerPlayerDataUser(PlayerDataUser)}
 */
public interface PlayerDataUser {
	
	/**
	 * Loads the player data used in the implementing class
	 * @param player The UUID of the Player
	 */
	public void clearPlayerData(UUID playerId);
	
	
	/**
	 * Unloads/clears the player data used in the implementing class
	 * @param player The UUID of the Player
	 */
	public void loadPlayerData(UUID playerId);

}

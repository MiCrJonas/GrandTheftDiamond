package me.micrjonas.grandtheftdiamond.data.player;

import java.util.Collection;
import java.util.HashSet;

import me.micrjonas.grandtheftdiamond.GameManager;
import me.micrjonas.grandtheftdiamond.gang.Gang;
import me.micrjonas.grandtheftdiamond.house.House;
import me.micrjonas.grandtheftdiamond.util.bukkit.PotionEffects;

import org.bukkit.Location;
import org.bukkit.entity.Damageable;
import org.bukkit.entity.Player;

public class PlayerSessionData {
	
	private final Player p;
	private final OutsideGamePlayerData outsideGameData;
	
	private boolean inGame;
	private boolean joining;
	
	private boolean handcuffed;
	private boolean arresting;
	private boolean detaining;
	
	private boolean canAcceptCorrupt;
	
	private Player targetPlayer;
	private Player passengerToJail;
	
	private final Collection<Gang> gangInvitesToAccept = new HashSet<>();
	
//Arena set up
	private boolean wandEnabled;
	private int nextSignCooldown;
	private Location arenaSetupLocation0;
	private Location arenaSetupLocation1;
	private House creatingHouseDoor;
//End of arena set up
	
	PlayerSessionData(Player p) {
		this.p = p;
		outsideGameData = new OutsideGamePlayerData(p);
	}
	
	/**
	 * Saves the data of the Player when he is outside the game (Inventory, experience level, PotionEffects)
	 * @throws IllegalStateException If the Player is in game
	 */
	public void saveOusideGameData() {
		if (inGame) {
			throw new IllegalStateException("Cannot store outside game data: Player is in game");
		}
		outsideGameData.setLevel(p.getLevel());
		outsideGameData.setExp(p.getExp());
		outsideGameData.setInventory(p.getInventory().getContents());
		outsideGameData.setArmor(p.getInventory().getArmorContents());
		outsideGameData.setHealth(((Damageable) p).getHealth());
		outsideGameData.setFoodLevel(p.getFoodLevel());
		outsideGameData.setFireTicks(p.getFireTicks());
		outsideGameData.setPotionEffects(p.getActivePotionEffects(), false);
	}
	
	/**
	 * Restores the data of the Player when he is outside the game (Inventory, experience level, PotionEffects)
	 */
	public void restoreOutsideGameData() {
		p.setLevel(outsideGameData.getLevel());
		p.setExp(outsideGameData.getExp());
		p.getInventory().setContents(outsideGameData.getInventory());
		p.getInventory().setArmorContents(outsideGameData.getArmor());
		p.setHealth(outsideGameData.getHealth());
		p.setFoodLevel(outsideGameData.getFoodLevel());
		p.setFireTicks(outsideGameData.getFireTicks());
		
		PotionEffects.removeFromPlayer(p);
		PotionEffects.addToPlayer(p, outsideGameData.getPotionEffects());
	}
	
	/**
	 * Returns the saved player data from outside the game
	 * @return The Player's outside game (default) data
	 */
	public OutsideGamePlayerData getOutsideGameData() {
		return outsideGameData;
	}

	/**
	 * Returns whether the Player is in game or not
	 * @return True if the Player is in game, else false
	 */
	public boolean isInGame() {
		return inGame;
	}

	/**
	 * Sets whether the Player is in game or not
	 * @param inGame True if the Player is in game, else false
	 * @deprecated Use {@link GameManager#joinGame(Player, me.micrjonas.grandtheftdiamond.Team, me.micrjonas.grandtheftdiamond.api.event.cause.JoinReason)}
	 */
	@Deprecated
	public void setInGame(boolean inGame) {
		this.inGame = inGame;
	}
	
	/**
	 * Returns whether the Player is joining the game and waits until the cool down is one
	 * @return True if Player is joining, else false
	 */
	public boolean isJoining() {
		return joining;
	}
	
	/**
	 * Sets whether the Player is joining the game
	 * @param joining True if the Player wants to join, else false
	 */
	public void setJoining(boolean joining) {
		this.joining = joining;
	}

	/**
	 * Returns whether the Player is handcuffed or not
	 * @return True if the Player is cuffed, else false
	 */
	public boolean isHandcuffed() {
		return handcuffed;
	}

	/**
	 * Sets whether the Player is handcuffed or not
	 * @param handcuffed True if Player is cuffed, else false
	 */
	public void setHandcuffed(boolean handcuffed) {
		this.handcuffed = handcuffed;
	}
	
	/**
	 * Returns whether the Player is arresting an other Player
	 * @return True if the Player is arresting someone, else false
	 */
	public boolean isArresting() {
		return arresting;
	}

	/**
	 * Sets whether the Player is arresting someone
	 * @param arresting True if the Player is arresting someone, else false
	 */
	public void setArresting(boolean arresting) {
		this.arresting = arresting;
	}

	/**
	 * Returns whether the Player is detaining an other Player
	 * @return True if the Player is detaining someone, else false
	 */
	public boolean isDetaining() {
		return detaining;
	}

	/**
	 * Sets whether the Player is detaining someone
	 * @param arresting True if the Player is detaining someone, else false
	 */
	public void setDetaining(boolean detaining) {
		this.detaining = detaining;
	}

	/**
	 * Checks whether the Player can accept an corrupt
	 * @return True if the Player can accept an corrupt, else false
	 */
	public boolean canAcceptCorrupt() {
		return canAcceptCorrupt;
	}

	/**
	 * Sets whether the Player can accept an corrupt
	 * @param canAcceptCorrupt True if the Player can accept an corrupt, else false
	 */
	public void setCanAcceptCorrupt(boolean canAcceptCorrupt) {
		this.canAcceptCorrupt = canAcceptCorrupt;
	}

	/**
	 * Returns the target player of the Player
	 * @return The target player of the Player, null if the Player does not have a target player
	 */
	public Player getTargetPlayer() {
		return targetPlayer;
	}

	/**
	 * Sets the target player
	 * @param targetPlayer The new target player; null is allowed, too
	 */
	public void setTargetPlayer(Player targetPlayer) {
		this.targetPlayer = targetPlayer;
	}

	/**
	 * Returns the passenger the Player is taking to the jail
	 * @return The Player's passenger to bring to the jail, null if the Player does not have a passenger
	 */
	public Player getPassengerToJail() {
		return passengerToJail;
	}

	/**
	 * Sets the passenger the Player is taking to the jail
	 * @param passengerToJail The new passenger; null is allowed, too
	 */
	public void setPassengerToJail(Player passengerToJail) {
		this.passengerToJail = passengerToJail;
	}

	/**
	 * Returns a Collection of {@link Gang}s a Player can join/accept<br>
	 * To change/edit/remove the Gangs, edit the Collection. 
	 * There are no methods in this class to remove/add acceptable {@link Gang}s
	 * @return A Collection of {@link Gang}s a Player can join/accept
	 */
	public Collection<Gang> getGangInvitesToAccept() {
		return gangInvitesToAccept;
	}

	/**
	 * Returns whether the Player's wand is enabled or not
	 * @return True if the Player has enabled his wand, else false; By default it's false
	 */
	public boolean isWandEnabled() {
		return wandEnabled;
	}

	/**
	 * Enables or disables the Player's wand
	 * @param wandEnabled True if the wand should be enabled, else false
	 */
	public void setWandEnabled(boolean wandEnabled) {
		this.wandEnabled = wandEnabled;
	}

	/**
	 * Returns whether the Player is creating a door of a house or not
	 * @return True if the Player is creating a door, else false
	 */
	public boolean isCreatingHouseDoor() {
		return creatingHouseDoor != null;
	}

	/**
	 * Sets whether the Player is creating a door of a house
	 * @param house he {@link House} the Player wants to create the door for
 	 */
	public void setCreatingHouseDoor(House house) {
		this.creatingHouseDoor = house;
	}

	/**
	 * Returns the cool down the Player wants to set on the next sign he'll click
	 * @return The cool down the Player wants to set on the next sign he'll click, -1 if the Player is not setting a cool down
	 */
	public int getNextSignCooldown() {
		return nextSignCooldown;
	}

	/**
	 * Sets the new cool down for the sign the Player clicks next
	 * @param nextSignCooldown The new cool down for the sign the Player clicks next; < 1 to disable
	 */
	public void setNextSignCooldown(int nextSignCooldown) {
		if (nextSignCooldown > 0) {
			this.nextSignCooldown = nextSignCooldown;
		}
		else {
			this.nextSignCooldown = -1;
		}
	}

	/**
	 * Returns the Player's first/min setup Location
	 * @return The Player's first arena set up Location; null if the Player has not set the Location
	 */
	public Location getArenaSetupLocation0() {
		return arenaSetupLocation0;
	}

	/**
	 * Sets the Player's first/min arena setup Location
	 * @param arenaSetupLocation0 The Player's first set up Location; null to remove
	 */
	public void setArenaSetupLocation0(Location arenaSetupLocation0) {
		this.arenaSetupLocation0 = arenaSetupLocation0;
	}

	/**
	 * Returns the Player's second/max setup Location
	 * @return The Player's second arena set up Location; null if the Player has not set the Location
	 */
	public Location getArenaSetupLocation1() {
		return arenaSetupLocation1;
	}

	/**
	 * Sets the Player's second/max arena setup Location
	 * @param arenaSetupLocation0 The Player's second set up Location; null to remove
	 */
	public void setArenaSetupLocation1(Location arenaSetupLocation1) {
		this.arenaSetupLocation1 = arenaSetupLocation1;
	}

}

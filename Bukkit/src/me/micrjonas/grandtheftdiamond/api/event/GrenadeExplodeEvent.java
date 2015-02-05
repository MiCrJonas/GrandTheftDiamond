package me.micrjonas.grandtheftdiamond.api.event;

import org.bukkit.Location;
import org.bukkit.entity.Egg;
import org.bukkit.entity.Player;
import org.bukkit.event.HandlerList;

/**
 * Fired when a grenade explodes
 */
public class GrenadeExplodeEvent extends AbstractCancellableEvent {
	
	/**
	 * Returns the {@link Event}'s {@link HandlerList}
	 * @return The {@link Event}'s {@link HandlerList}
	 * @see AbstractEvent#getHandlers(Class)
	 * @see AbstractEvent#getHandlers()
	 */
	public static HandlerList getHandlerList() {
		return getHandlers(GrenadeExplodeEvent.class);
	}
	
	private final Player thrower;
	private final Egg grenade;
	private final Location explosionLocation;
	private double explosionRadius;
	private boolean breakBlocks;
	private boolean setFire;
	
	/**
	 * @param thrower The Player who thrown the grenade
	 * @param grenade The thrown grenade
	 * @param explosionLocation The Location of the explosion
	 * @param explosionRadius The radius of the explosion
	 * @param breakBlocks True if the explosion shall break blocks, else false
	 * @param setFire True if the explosion should set fire to nearby blocks, else false
	 */
	public GrenadeExplodeEvent(Player thrower, Egg grenade, Location explosionLocation, 
			double explosionRadius, boolean breakBlocks, boolean setFire) {
		
		this.thrower = thrower;
		this.grenade = grenade;
		this.explosionLocation = explosionLocation;
		this.explosionRadius = explosionRadius;
		this.breakBlocks = breakBlocks;
		this.setFire = setFire;
	}

	/**
	 * Returns the player who threw the grenade
	 * @return The player who threw the grenade
	 */
	public Player getThrower() {
		return thrower;
	}
  
	/**
	 * Returns the egg which was used as grenade
	 * @return The egg which was used as grenade
	 */
	public Egg getGrenade() {
		return grenade;
	}
	
	/**
	 * Returns a copy of the location of the explosion
	 * @return A copy of the Location of the explosion
	 */
	public Location getExplosionLocation() {
		return explosionLocation.clone();
	}
	
	/**
	 * Returns the radius of the explosion
	 * @return The radius of the explosion
	 */
	public double getExplosionRadius() {
		return explosionRadius;
	}
	
	/**
	 * Sets the explosion radius of the grenade explosion
	 * @param explosionRadius The new explosion radius
	 * @throws IllegalArgumentException Thrown if {@code explosionRadius} is < 0
	 */
	public void setExplosionRadius(int explosionRadius) throws IllegalArgumentException {
		this.explosionRadius = explosionRadius;
	}
	
	/**
	 * Returns whether the explosion breaks blocks
	 * @return True if the explosion of the grenade breaks blocks, else false
	 */
	public boolean breakBlocks() {
		return breakBlocks;
	}
	
	/**
	 * Sets whether blocks should be braked by the grenade explosion
	 * @param breakBlocks Should the explosion break blocks?
	 */
	public void setBreakBlocks(boolean breakBlocks) {
		this.breakBlocks = breakBlocks;
	}
	
	/**
	 * Returns whether the explosion of the grenade should set fire to nearby blocks
	 * @return True if the explosion should set fire to nearby blocks, else false
	 */
	public boolean setFire() {
		return setFire;
	}
	
	/**
	 * Sets whether blocks should set on fire by the explosion
	 * @param setFire Set nearby blocks in fire?
	 */
	public void setFire(boolean setFire) {
		this.setFire = setFire;
	}

}

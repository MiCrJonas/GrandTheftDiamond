package me.micrjonas.grandtheftdiamond.rob;

import java.util.Map;

import me.micrjonas.grandtheftdiamond.data.storage.Storable;
import me.micrjonas.grandtheftdiamond.pluginsupport.dynmap.DynmapDisplayable;
import me.micrjonas.grandtheftdiamond.pluginsupport.dynmap.MarkerSymbol;
import me.micrjonas.grandtheftdiamond.util.Removeable;
import me.micrjonas.grandtheftdiamond.util.bukkit.Locations;
import me.micrjonas.grandtheftdiamond.util.bukkit.WorldStorage;

import org.bukkit.Location;
import org.bukkit.block.Block;

/**
 * Represents a safe
 */
public class Safe implements Removeable, DynmapDisplayable, Robable, Storable {

	private String name;
	private String markerLabel;
	private final Block block;
	private long unrobbableUntil = -1;
	private boolean removed;
	
	Safe (String name, Block block) {
		this.name = name;
		this.block = block;
	}

	@Override
	public MarkerSymbol getMarkerSymbol() {
		return MarkerSymbol.SAFE;
	}

	@Override
	public String getMarkerLabel() {
		if (markerLabel == null) {
			String label = RobManager.getInstance().getSafeMarkerLabel();
			if (label != null) {
				markerLabel = label.replace("%name%", getName());
			}
		}
		return markerLabel;
	}

	@Override
	public Location getMarkerLocation() {
		if (!isRemoved() && RobManager.getInstance().getSafeMarkerLabel() != null) {
			return getBlock().getLocation();
		}
		return null; // Disabled
	}
	
	@Override
	public String getName() {
		return name;
	}
	
	@Override
	public Map<String, Object> getStoreData() {
		return Locations.toMap(block.getLocation(), WorldStorage.NAME, true);
	}
	
	@Override
	public void remove() {
		if (!isRemoved()) {
			RobManager.getInstance().removeSafe(getBlock());
			removed = true;
		}
	}
	
	@Override
	public boolean isRemoved() {
		return removed;
	}
	
	void setInvalid() {
		removed = true;
	}
	
	/**
	 * Sets the name of the safe
	 * @param name The new name
	 * @throws IllegalArgumentException Thrown if {@code name} is {@code null}
	 */
	public void setName(String name) throws IllegalArgumentException {
		if (name == null) {
			throw new IllegalArgumentException("Name is not allowed to be null");
		}
		if (!getName().equals(name)) {
			markerLabel = null;
			this.name = name;	
		}
	}	
	
	private void checkTime() {
		if (unrobbableUntil <= System.currentTimeMillis()) {
			unrobbableUntil = -1;
		}
	}
	
	/**
	 * Returns the block of the safe
	 * @return The safe block
	 */
	public Block getBlock() {
		return block;
	}
	
	/**
	 * Checks whether the safe is robable
	 * @return True if a player can rob the safe, else false
	 */
	public boolean robable() {
		checkTime();
		return unrobbableUntil <= System.currentTimeMillis();
	}
	
	/**
	 * Returns when the safe is robable again
	 * @return The time in mills when the safe is robable again. Time started at January 1st 1970 -1 if a player can rob the safe
	 * @see System#currentTimeMillis()
	 */
	public long unrobableUntil() {
		checkTime();
		return unrobbableUntil;
	}
	
	/**
	 * Sets whether a player can rob the safe
	 * @param robable True if robable, else false
	 * @param seconds The time in seconds the safe cannot be robbed, only required if {@code robable} is false
	 * @throws IllegalArgumentException Thrown if {@code robable} is false and {@code seconds} is < 1
	 */
	public void setRobable(boolean robable, int seconds) throws IllegalArgumentException {
		if (robable) {
			unrobbableUntil = -1;
		}
		else {
			if (seconds < 1) {
				throw new IllegalArgumentException("Time cannot be < 1 if robable is false");
			}
			unrobbableUntil = System.currentTimeMillis() + seconds * 1000L;
		}
	}

}

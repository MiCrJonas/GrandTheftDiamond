package me.micrjonas.grandtheftdiamond.pluginsupport.dynmap;

import java.io.File;

import me.micrjonas.grandtheftdiamond.GrandTheftDiamond;
import me.micrjonas.grandtheftdiamond.arena.Spawn;
import me.micrjonas.grandtheftdiamond.house.House;
import me.micrjonas.grandtheftdiamond.jail.Jail;
import me.micrjonas.grandtheftdiamond.rob.Safe;

/**
 * The icon of a {@link DynmapDisplayable}
 */
public enum MarkerSymbol {

	/**
	 * DynMap icon of an {@link House} without owner<br>
	 * Stored in: ../images/dynmap-markers/house_buyable.png
	 */
	HOUSE_BUYABLE,
	
	/**
	 * DynMap icon of an owned {@link House}<br>
	 * Stored in: ../images/dynmap-markers/house_owned.png
	 */
	HOUSE_OWNED,
	
	/**
	 * DynMap icon of a {@link Safe}<br>
	 * Stored in: ../images/dynmap-markers/safe.png
	 */
	SAFE,
	
	/**
	 * DynMap icon of a civilian {@link Spawn}<br>
	 * Stored in: ../images/dynmap-markers/spawn_civilian.png
	 */
	SPAWN_CIVILIAN,
	
	/**
	 * DynMap icon of a cop {@link Spawn}<br>
	 * Stored in: ../images/dynmap-markers/spawn_cop.png
	 */
	SPAWN_COP,
	
	/**
	 * DynMap icon of the default {@link Spawn}<br>
	 * Stored in: ../images/dynmap-markers/spawn_default.png
	 */
	SPAWN_DEFAULT,
	
	/**
	 * DynMap icon of an hospital {@link Spawn}<br>
	 * Stored in: ../images/dynmap-markers/spawn_hospital.png
	 */
	SPAWN_HOSPITAL,
	
	/**
	 * DynMap icon of a {@link Jail}'s {@link Spawn}<br>
	 * Stored in: ../images/dynmap-markers/spawn_civilian.png
	 */
	SPAWN_JAIL;
	
	private final File source;
	
	private MarkerSymbol() {
		this.source = new File(GrandTheftDiamond.getDataFolder() + File.separator + "images" + File.separator + "dynmap-markers", name().toLowerCase() + ".png");
	}
	
	/**
	 * Returns the image's source {@link File}
	 * @return The images source {@code File}
	 */
	public File getSource() {
		return source;
	}
	
}

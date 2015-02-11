package me.micrjonas.grandtheftdiamond.arena;

import me.micrjonas.grandtheftdiamond.pluginsupport.dynmap.DynmapDisplayable;
import me.micrjonas.grandtheftdiamond.pluginsupport.dynmap.MarkerSymbol;
import me.micrjonas.grandtheftdiamond.util.Immutable;
import me.micrjonas.util.Nameable;

import org.bukkit.Location;


/**
 * Represents an {@link Arena}'s spawn. Each object is immutable
 */
public class Spawn implements DynmapDisplayable, Immutable, Nameable {
	
	private final String name;
	private final SpawnType type;
	private final Location loc;
	
	Spawn(String name, SpawnType type, Location loc) {
		this.name = name;
		this.type = type;
		this.loc = loc;
	}
	
	@Override
	public MarkerSymbol getMarkerSymbol() {
		switch (type) {
			case CIVILIAN:
				return MarkerSymbol.SPAWN_CIVILIAN;
			case COP:
				return MarkerSymbol.SPAWN_COP;
			case DEFAULT:
				return MarkerSymbol.SPAWN_DEFAULT;
			case HOSPITAL:
				return MarkerSymbol.SPAWN_HOSPITAL;
			case JAIL:
				return MarkerSymbol.SPAWN_JAIL;
			default:
				break;
		}
		return null;
	}

	@Override
	public String getName() {
		return name;
	}
	
	@Override
	public String getMarkerLabel() {
		return null;
	}
	
	@Override
	public Location getMarkerLocation() {
		return getLocation();
	}
	
	Location getUnclonedLocation() {
		return loc;
	}
	
	/**
	 * Returns the {@link SpawnType}
	 * @return The {@code Spawn}'s type
	 */
	public SpawnType getType() {
		return type;
	}
	
	/**
	 * Return the {@code Spawn}'s {@link Location}
	 * @return The {@code Spawn}'s {@link Location}
	 */
	public Location getLocation() {
		return loc.clone();
	}
	
}

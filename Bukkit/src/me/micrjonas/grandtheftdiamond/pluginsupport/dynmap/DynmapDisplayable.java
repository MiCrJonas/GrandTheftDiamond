package me.micrjonas.grandtheftdiamond.pluginsupport.dynmap;

import org.bukkit.Location;

/**
 * All objects of classes which implement this interface can be shown on the DynMap
 */
public interface DynmapDisplayable {

	/**
	 * Returns the symbol of the objects marker
	 * @return The objects marker symbol. Not allowed to be Only allowed to be {@code null} if {@link #getMarkerLocation()}
	 * 	returns {@code null}, too
	 */
	MarkerSymbol getMarkerSymbol();
	
	/**
	 * Returns the label of the object to show on the DynMap
	 * @return The objects marker label. HTML code is allowed. Only allowed to be {@code null} if {@link #getMarkerLocation()}
	 * 	returns {@code null}, too
	 */
	String getMarkerLabel();
	
	/**
	 * Returns the object's marker location on the DynMap. A {@code null}-return shows that the object is not allowed
	 * 	to be shown on the DynMap
	 * @return The location of the objects marker. {@code null} if the object is not allowed to be shown of the DynMap
	 */
	Location getMarkerLocation();
	
}

package me.micrjonas.grandtheftdiamond.pluginsupport.dynmap;

import java.awt.Color;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.EnumMap;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;

import me.micrjonas.grandtheftdiamond.GrandTheftDiamond;
import me.micrjonas.grandtheftdiamond.arena.Arena;
import me.micrjonas.grandtheftdiamond.arena.ArenaType;
import me.micrjonas.grandtheftdiamond.data.FileReloadListener;
import me.micrjonas.grandtheftdiamond.data.PluginFile;
import me.micrjonas.grandtheftdiamond.pluginsupport.PluginSupport;
import me.micrjonas.grandtheftdiamond.util.SquareLocation;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.configuration.file.FileConfiguration;
import org.dynmap.bukkit.DynmapPlugin;
import org.dynmap.markers.AreaMarker;
import org.dynmap.markers.CircleMarker;
import org.dynmap.markers.Marker;
import org.dynmap.markers.MarkerAPI;
import org.dynmap.markers.MarkerIcon;
import org.dynmap.markers.MarkerSet;


/**
 * Manages the plugin's markers on the DynMap. If {@link #isEnabled()} returns {@code false},
 * 	then all methods which are not a void-method return {@code null} except {@link #getInstance()}
 */
public class DynmapMarkerManager implements FileReloadListener, PluginSupport<DynmapPlugin> {
	
	private final static String MARKER_SET_ID = "grandtheftdiamond";
	private final static DynmapMarkerManager instance = new DynmapMarkerManager();
	
	/**
	 * Returns the loaded instance
	 * @return The loaded instance
	 */
	public static DynmapMarkerManager getInstance() {
		return instance;
	}
	
	private static int toHex(Color c) {
		return (c.getAlpha() & 0xFF << 24) | ((c.getRed() & 0xFF) << 16) | ((c.getGreen() & 0xFF) << 8) | (c.getBlue() & 0xFF);
	}
// End of static
	
	private Color getColor(String color, double opacy) {
		String[] split = color.split(" ");
		int r = 0;
		int g = 0;
		int b = 0;
		try {
			r = Integer.parseInt(split[0]);
			g = Integer.parseInt(split[1]);
			b = Integer.parseInt(split[2]);
		}
		catch (ArrayIndexOutOfBoundsException | NumberFormatException ex) {
			return new Color(r, g, b, (int) (opacy * 255));
		}
		return new Color(r, g, b, (int) (opacy * 255));
	}
	
	private final DynmapPlugin plugin = (DynmapPlugin) Bukkit.getPluginManager().getPlugin("dynmap");
	private final Map<MarkerSymbol, MarkerSet> markerSets = new EnumMap<>(MarkerSymbol.class);
	private final Map<Arena, Marker> arenaMarkers = new HashMap<>();
	private final Map<DynmapDisplayable, Marker> displayables = new HashMap<>();
	private MarkerSet arenas;
	
	private boolean enabled;
	private Color arenaBorderColor;
	private Color arenaFillColor;
	private int arenaBorderWeight;
	private String arenaNameFormat;
	
	private DynmapMarkerManager() {
		GrandTheftDiamond.registerFileReloadListener(this);
	}

	@Override
	public void configurationReloaded(PluginFile file, FileConfiguration fileConfiguration) {
		if (file == PluginFile.CONFIG) {
			enabled = plugin != null && fileConfiguration.getBoolean("dynmap.enabled");
			if (enabled) {
				arenas = getMarkerAPI().createMarkerSet(MARKER_SET_ID + "-arenas", MARKER_SET_ID + "-arenas", null, false);
				for (MarkerSymbol symbol : MarkerSymbol.values()) {
					MarkerSet set = getMarkerAPI()
							.createMarkerSet(MARKER_SET_ID + "-" + symbol.name().toLowerCase(), symbol.name(), null, false);
					if (set == null) {
						set = getMarkerAPI().getMarkerSet(MARKER_SET_ID + "-" + symbol.name().toLowerCase());
					}
					set.setDefaultMarkerIcon(getIcon(symbol));
					markerSets.put(symbol, set);
				}
				arenaBorderColor = getColor(fileConfiguration.getString("dynmap.markers.arena.border.color"), 
						fileConfiguration.getDouble("dynmap.markers.arena.border.opacy"));
				arenaFillColor = getColor(fileConfiguration.getString("dynmap.markers.arena.fill.color"), 
						fileConfiguration.getDouble("dynmap.markers.arena.fill.opacy"));
				arenaBorderWeight = fileConfiguration.getInt("dynmap.markers.arena.border.weight");
				arenaNameFormat = fileConfiguration.getString("dynmap.markers.arena.name");
			}
			else {
				Entry<MarkerSymbol, MarkerSet> next;
				for (Iterator<Entry<MarkerSymbol, MarkerSet>> iter = markerSets.entrySet().iterator(); iter.hasNext(); ) {
					next = iter.next();
					MarkerSet set = next.getValue();
					for (Marker marker : set.getMarkers()) {
						marker.deleteMarker();
					}
					for (AreaMarker marker : set.getAreaMarkers()) {
						marker.deleteMarker();
					}
					for (CircleMarker marker : set.getCircleMarkers()) {
						marker.deleteMarker();
					}
					set.deleteMarkerSet();
					iter.remove();
				}
			}
		}
	}

	@Override
	public boolean isEnabled() {
		return enabled;
	}

	@Override
	public DynmapPlugin getPlugin() {
		if (isEnabled()) {
			return plugin;	
		}
		return null;
	}
	
	/**
	 * Returns the DynMap's marker API
	 * @return DynMap's marker API
	 */
	public MarkerAPI getMarkerAPI() {
		if (isEnabled()) {
			return getPlugin().getMarkerAPI();	
		}
		return null;
	}
	
	/**
	 * Updates the {@link DynmapDisplayable}'s marker on the dynmap or creates a new one if no marker exists. If the method 
	 * 	creates a new {@link Marker}, then all components get updated
	 * @param toShow The object to display on the dynmap
	 * @param updateIcon Whether the {@link MarkerSymbol} should be updated
	 * @param updateLabel Whether the label should be updated
	 * @param updateLocation Whether the {@link Location} should be updated
	 * @return The created marker of the object. May be {@code null}, if the creation failed for some reason or the
	 * 	{@link DynmapDisplayable} is not allowed to be shown on the DynMap
	 * @throws IllegalArgumentException Thrown if {@code toShow} is null
	 * @see #updateMarker(DynmapDisplayable)
	 */
	public Marker updateMarker(DynmapDisplayable toShow, boolean updateIcon, boolean updateLabel, boolean updateLocation) throws IllegalArgumentException {
		if (isEnabled()) {
			Location loc = toShow.getMarkerLocation();
			if (loc != null) {
				Marker marker = displayables.get(toShow);
				if (marker == null) {
					updateIcon = true;
					updateLabel = true;
					updateLocation = true;
					marker = createMarker(toShow.getMarkerLabel(), toShow.getMarkerSymbol(), toShow.getMarkerLocation());
					displayables.put(toShow, marker);
				}
				else {
					if (updateLocation) {
						marker.setLocation(loc.getWorld().getName(), loc.getX(), loc.getY(), loc.getZ());	
					}
				}
				if (updateLabel) {
					marker.setLabel(toShow.getMarkerLabel(), true);	// True -> markup
				}
				if (updateIcon) {
					marker.setMarkerIcon(getIcon(toShow.getMarkerSymbol()));	
				}
				return marker;	
			}
			else {
				Marker oldMarker = displayables.remove(toShow);
				if (oldMarker != null) {
					oldMarker.deleteMarker();
				}
			}
		}
		return null;
	}
	
	/**
	 * Updates the {@link DynmapDisplayable}'s marker on the dynmap or creates a new one if no marker
	 * 	exists
	 * @param toShow The object to display on the dynmap
	 * @return The created marker of the object. May be {@code null}, if the creation failed for some reason or the
	 * 	{@link DynmapDisplayable} is not allowed to be shown on the DynMap
	 * @throws IllegalArgumentException Thrown if {@code toShow} is null
	 * @see #updateMarker(DynmapDisplayable, boolean, boolean, boolean)
	 */
	public Marker updateMarker(DynmapDisplayable toShow) throws IllegalArgumentException {
		return updateMarker(toShow, true, true, true);
	}
	
	/**
	 * Creates new {@link Marker} on the DynMap
	 * @param label The label of the marker. Allowed to be a HTML formatted text
	 * @param symbol The marker's symbol
	 * @param loc The marker's location
	 * @return The created marker. {@code null} if creation failed for some reason
	 * @throws IllegalArgumentException Thrown if one of the elements is {@code null}
	 */
	public Marker createMarker(String label, MarkerSymbol symbol, Location loc) throws IllegalArgumentException {
		if (isEnabled()) {
			if (label == null) {
				throw new IllegalArgumentException("Label cannot be null");
			}
			if (symbol == null) {
				throw new IllegalArgumentException("Symbol cannot be null");
			}
			if (loc == null) {
				throw new IllegalArgumentException("Location cannot be null");
			}
			return markerSets.get(symbol).createMarker(label, label, true, loc.getWorld().getName(), loc.getX(), loc.getY(), loc.getZ(), null, false);
		}
		return null;
	}
		
	/**
	 * Removes a marker from the DynMap
	 * @param marker The marker to remove
	 * @throws IllegalArgumentException Thrown if {@code marker} is null
	 * @see Marker#deleteMarker()
	 */
	public void removeMarker(Marker marker) throws IllegalArgumentException {
		if (isEnabled()) {
			if (marker == null) {
				throw new IllegalArgumentException("Marker to remove is not allowed to be null");
			}
			marker.deleteMarker();	
		}
	}
	
	/**
	 * Updates the {@link Arena}'s border on the dynmap or creates a new one if no border
	 * 	exists for the arena. Only creates a dynmap marker if the {@code arena}'s type is
	 * 	{@link ArenaType#CUBOID} or {@link ArenaType#CYLINDER}
	 * @param arena The arena to update 
	 * @param borderColor The color of the border. RGB and alpha channel are used
	 * @param fillColor The fill color of the area. RGB and alpha channel are used
	 * @param borderWidth The width of the border
	 * @throws IllegalArgumentException Thrown if {@code arena} is {@code null}
	 */
	public void updateArena(Arena arena) throws IllegalArgumentException {
		if (arena == null) {
			throw new IllegalArgumentException("Arena is not allowed to be null");
		}
		if (isEnabled()) {
			Marker arenaMarker = arenaMarkers.get(arena);
			if (arenaMarker != null) {
				arenaMarker.deleteMarker();
			}
			switch (arena.getType()) {
				case CUBOID: {
					SquareLocation loc0 = arena.getMinOrCenter();
					SquareLocation loc1 = arena.getMax();
					double[] x = new double[2];
					double[] z = new double[2];
					x[0] = loc0.getX();
					x[1] = loc1.getX();
					z[0] = loc0.getX();
					z[1] = loc1.getX();
					AreaMarker marker = arenas.createAreaMarker(MARKER_SET_ID + "-" + arena.getName(), arenaNameFormat.replace("%name%", arena.getName()), true, arena.getWorld().getName(), x, z, false);
					marker.setLineStyle(arenaBorderWeight >= 0 ? arenaBorderWeight : 0, arenaBorderColor.getAlpha() / 255D, toHex(arenaBorderColor));
					marker.setFillStyle(arenaFillColor.getAlpha() / 255D, toHex(arenaFillColor));
				} break;
				
				case CYLINDER: {
					SquareLocation center = arena.getMinOrCenter();
					CircleMarker marker = arenas
							.createCircleMarker(MARKER_SET_ID + "-" + arena.getName(),
							arenaNameFormat.replace("%name%", arena.getName()), true, arena.getWorld().getName(), 
					center.getX(), arena.getDefaultSpawn().getY(), center.getZ(), 
					arena.getRadius(), arena.getRadius(), false);
					marker.setLineStyle(arenaBorderWeight >= 0 ? arenaBorderWeight : 0, arenaBorderColor.getAlpha() / 255D, toHex(arenaBorderColor));
					marker.setFillStyle(arenaFillColor.getAlpha() / 255D, toHex(arenaFillColor));
				} break;
				
				default:
					return;
			}	
		}
	}
	
	/**
	 * Removes an {@link Arena} from the DynMap
	 * @param arena The {@code Arena} to remove from the DynMap
	 * @throws IllegalArgumentException Thrown if {@code arena} is {@code null}
	 */
	public void removeArenaBorder(Arena arena) throws IllegalArgumentException {
		if (arena == null) {
			throw new IllegalArgumentException("Arena is not allowed to be null");
		}
		if (isEnabled()) {
			Marker arenaMarker = arenaMarkers.get(arena);
			if (arenaMarker != null) {
				arenaMarker.deleteMarker();
			}	
		}
	}
	
	private MarkerIcon getIcon(MarkerSymbol symbol) {
		InputStream imageStream = null;
		try {
			imageStream = new FileInputStream(symbol.getSource());
		}
		catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		if (imageStream != null) {
			MarkerIcon icon = getMarkerAPI().createMarkerIcon(MARKER_SET_ID + "-" + symbol.name(), symbol.name(), imageStream);	
			if (icon != null) {
				return icon;
			}
			else {
				icon = getMarkerAPI().getMarkerIcon(MARKER_SET_ID + "-" + symbol.name());
				icon.setMarkerIconImage(imageStream);
			}
			try {
				imageStream.close();
			}
			catch (IOException e) { } // Can't do anything
			return icon;
		}
		return null;
	}
	
}

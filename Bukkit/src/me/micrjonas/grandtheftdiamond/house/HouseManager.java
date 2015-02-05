package me.micrjonas.grandtheftdiamond.house;

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.UUID;

import me.micrjonas.grandtheftdiamond.GrandTheftDiamond;
import me.micrjonas.grandtheftdiamond.api.event.AbstractCancellableEvent;
import me.micrjonas.grandtheftdiamond.api.event.HouseCreateEvent;
import me.micrjonas.grandtheftdiamond.data.FileManager;
import me.micrjonas.grandtheftdiamond.data.FileReloadListener;
import me.micrjonas.grandtheftdiamond.data.PluginFile;
import me.micrjonas.grandtheftdiamond.data.TemporaryPluginData;
import me.micrjonas.grandtheftdiamond.data.storage.StorableManager;
import me.micrjonas.grandtheftdiamond.messenger.Messenger;
import me.micrjonas.grandtheftdiamond.messenger.NoPermissionType;
import me.micrjonas.grandtheftdiamond.pluginsupport.dynmap.DynmapMarkerManager;
import me.micrjonas.grandtheftdiamond.util.SimpleLocation;
import me.micrjonas.grandtheftdiamond.util.bukkit.Doors;
import me.micrjonas.grandtheftdiamond.util.bukkit.Locations;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.OfflinePlayer;
import org.bukkit.block.Block;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerInteractEvent;

/**
 * Manages all registered {@link House}s and can create new ones
 */
public class HouseManager implements StorableManager<House>, FileReloadListener {
	
	private static HouseManager instance = new HouseManager();
	
	/**
	 * Returns the loaded instance
	 * @return The loaded instance
	 */
	public static HouseManager getInstance() {
		return instance;
	}
	
	private FileConfiguration houseData = FileManager.getInstance().getFileConfiguration(PluginFile.HOUSES);
	private final Map<String, House> houses = new HashMap<>();
	private final Map<UUID, Set<House>> playerHouses = new HashMap<>();
	private final Map<SimpleLocation, House> doors = new HashMap<>();
	
	private String houseMarkerLabel;
	private String houseMarkerLabelOwned;
	
	private HouseManager() {
		GrandTheftDiamond.registerFileReloadListener(this);
		GrandTheftDiamond.registerStorableManager(this, PluginFile.HOUSES);
	}

	@Override
	public Collection<House> getAllObjects() {
		return Collections.unmodifiableCollection(houses.values());
	}
	
	@Override
	public void loadObjects(FileConfiguration dataFile) {
		houses.clear();
		playerHouses.clear();
		houseData = dataFile;
		for (String identifier : houseData.getConfigurationSection("").getKeys(false)) {
			Location doorBelow = Locations.getLocationFromFile(houseData, identifier + ".doorBelow", true);
			Block doorBelowBlock = doorBelow != null ? doorBelow.getBlock() : null;
			Set<OfflinePlayer> members = new HashSet<>();
			if (houseData.isList(identifier + ".members")) {
				for (String member : houseData.getStringList(identifier + ".members")) {
					members.add(Bukkit.getServer().getOfflinePlayer(UUID.fromString(member)));
				}
			}
			OfflinePlayer owner = houseData.isString(identifier + ".owner") ? Bukkit.getServer().getOfflinePlayer(UUID.fromString(houseData.getString(identifier + ".owner"))) : null;
			createNewHouse(null, identifier, Locations.getLocationFromFile(houseData, identifier + ".spawn", false), doorBelowBlock, owner, members, houseData.getInt(identifier + ".price"), true);
		}
	}

	@Override
	public void configurationReloaded(PluginFile file, FileConfiguration fileConfiguration) {
		if (file == PluginFile.CONFIG) {
			if (fileConfiguration.getBoolean("dynmap.markers.house.show")) {
				houseMarkerLabel = fileConfiguration.getString("dynmap.markers.house.name.buyable");
				houseMarkerLabelOwned = fileConfiguration.getString("dynmap.markers.house.name.owned");
			}
			else {
				houseMarkerLabel = null;
				houseMarkerLabelOwned = null;
			}
		}
	}
	
	/**
	 * Removes a {@link House} by its name. Does nothing if the name is not registered
	 * @param name The name of the {@link House} to remove
	 * @throws IllegalArgumentException Thrown if {@code name} is {@code null}
	 */
	public void removeHouse(String name) throws IllegalArgumentException {
		if (name == null) {
			throw new IllegalArgumentException("Name is no allowed to be null");
		}
		House removed = houses.remove(name.toLowerCase());
		if (removed != null) {
			removed.setInvalid();
			for (Iterator<Entry<UUID, Set<House>>> iter = playerHouses.entrySet().iterator(); iter.hasNext(); ) {
				Entry<UUID, Set<House>> next = iter.next();
				if (next.getValue().remove(removed) && next.getValue().isEmpty()) {
					iter.remove();
				}
			}
		}
	}

	// Null if disabled
	String getHouseMarkerLabel() {
		return houseMarkerLabel;
	}
	
	// Null if disabled
	String getHouseMarkerLabelOwned() {
		return houseMarkerLabelOwned;
	}
	
	void updateOwner(House house, OfflinePlayer oldOwner, OfflinePlayer newOwner) {
		if (oldOwner != newOwner) {
			if (oldOwner != null) {
				Set<House> houses = playerHouses.get(oldOwner.getUniqueId());
				if (houses != null) {
					houses.remove(house);
					if (houses.isEmpty()) {
						playerHouses.remove(oldOwner.getUniqueId());
					}
				}
			}
			if (newOwner != null) {
				if (!playerHouses.containsKey(newOwner.getUniqueId())) {
					playerHouses.put(newOwner.getUniqueId(), new HashSet<House>(5));
				}
				playerHouses.get(newOwner.getUniqueId()).add(house);	
			}
			DynmapMarkerManager.getInstance().updateMarker(house, true, true, false);
		}
	}
	
	void updadeDoor(House house, SimpleLocation[] oldDoors, SimpleLocation[] newDoors) {
		for (int i = 0; i < 2; i++) {
			if (oldDoors != null && oldDoors[i] != null) {
				doors.remove(oldDoors[i]);
			}
			if (newDoors[i] != null) {
				doors.put(newDoors[i], house);
			}
		}
	}
	
	/**
	 * Creates a new house and stores it to the house.yml
	 * @param creator The {@code Player} who wants to create the house
	 * @param identifier The identifier of this house
	 * @param spawn The spawn of the house
	 * @param door The {@code Block} of the house's door. May be {@code null}
	 * @param owner The owner of the house. May be {@code null}
	 * @param members The members of the house. . May be {@code null} for no members
	 * @param price The house's buy price
	 * @return The new house, null if the {@link HouseCreateEvent} was cancelled
	 */
	public House createNewHouse(Player creator, String identifier, Location spawn, Block door, OfflinePlayer owner, Set<OfflinePlayer> members, int price) {
		return createNewHouse(creator, identifier, spawn, door, owner, members, price, false);
	}
	
	
	/**
	 * Creates a new house and stores it to the house.yml
	 * @param creator The {@code Player} who wants to create the house
	 * @param identifier The identifier of this house
	 * @param spawn The spawn of the house
	 * @return The new house, null if HouseCreateEvent was cancelled
	 */
	public House createNewHouse(Player creator, String identifier, Location spawn) {
		return createNewHouse(creator, identifier, spawn, null, null, null, 0, false);
	}
	
	private House createNewHouse(Player creator, String identifier, Location spawn, Block door, OfflinePlayer owner, Set<OfflinePlayer> members, int price, boolean loadedFromConfig) {
		if (!loadedFromConfig) {
			if (houseData.isConfigurationSection(identifier)) {
				throw new IllegalArgumentException("house with identifier " + identifier + " does already exist");	
			}
			if (spawn == null) {
				throw new IllegalArgumentException("spawn is not allowed to be null");
			}
			if (spawn.getWorld() == null) {
				throw new IllegalArgumentException("world of pawn is not allowed to be null");
			}
		}
		if (loadedFromConfig || AbstractCancellableEvent.fireEvent(new HouseCreateEvent(creator, owner, spawn))) {
			House house = new House(identifier, spawn, owner, door == null ? null : new SimpleLocation(door.getLocation()), members, price);
			if (owner != null) {
				if (!playerHouses.containsKey(owner.getUniqueId())) {
					playerHouses.put(owner.getUniqueId(), new HashSet<House>());
				}
				playerHouses.get(owner.getUniqueId()).add(house);
			}
			houses.put(identifier, house);
			if (loadedFromConfig) {
				updadeDoor(house, null, Doors.getDoorLocations(door == null ? null : new SimpleLocation(door.getLocation())));
			}
			DynmapMarkerManager.getInstance().updateMarker(house);
			return house;
		}
		return null;
	}
	
	/**
	 * Returns the house with the given identifier. Ignores case-sensitive
	 * @param identifier The identifier of the house
	 * @return The house with the given identifier. {@code null} if no house exists with the given identifier
	 */
	public House getHouse(String identifier) {
		return houses.get(identifier.toLowerCase());
	}
	
	/**
	 * Returns whether there is a house with the given identifier
	 * @param idenifier The identifier of the house
	 * @return True if there is a house with the given identifier, else false
	 */
	public boolean isHouse(String idenifier) {
		return houses.containsKey(idenifier.toLowerCase());
	}
	
	/**
	 * Returns a new {@link Set} with all current {@link House}s of a {@link Player}
	 * @param p The {@link House}s' owner
	 * @return A new {@link Set} with all {@link Player}'s {@link House}s
	 * @throws IllegalArgumentException Thrown if {@code p} is {@code null}
	 */
	public Set<House> getPlayerHouses(Player p) throws IllegalArgumentException {
		if (p == null) {
			throw new IllegalArgumentException("Player is not allowed to be null");
		}
		Set<House> houses = playerHouses.get(p);
		if (houses != null) {
			return me.micrjonas.grandtheftdiamond.util.collection.Collections.filledSet(houses);
		}
		return new HashSet<>(0);
	}
	
	/**
	 * Checks whether a {@link Block} is a door of an {@link House}
	 * @param door The {@code Block} to check
	 * @return True if b is a door of an house, else false
	 */
	public boolean isDoorOfHouse(Block door) {
		return getHouseToDoor(door) != null;
	}
	
	/**
	 * Returns the {@link House} to a door {@link Block}
	 * @param door The door of the {@link House}
	 * @return Returns the {@code House} related to the door. {@code null} if the {@link Block} is not a {@code Block} of a {@link House}
	 */
	public House getHouseToDoor(Block door) {
		return doors.get(new SimpleLocation(door.getLocation()));
	}
	
	/**
	 * Called when a {@link Player} clicked a door
	 * @deprecated Do not call this method
	 * @param e The event
	 * @return True if the player can open the door, else false
	 */
	@Deprecated
	public boolean doorClicked(PlayerInteractEvent e) {
		if (isCreatingDoor(e.getPlayer())) {
			if (!isDoorOfHouse(e.getClickedBlock())) {
				House house = getHouse(getDoorCreatingIdentifier(e.getPlayer()));
				if (house != null) {
					house.setDoor(e.getClickedBlock());
					Messenger.getInstance().sendPluginMessage(e.getPlayer(), "house.doorCreated", "%house%", getDoorCreatingIdentifier(e.getPlayer()));
				}
				else {
					Messenger.getInstance().sendPluginMessage(e.getPlayer(), "house.noLongerHouse", "%house%", getDoorCreatingIdentifier(e.getPlayer()));
				}
			}
			else {
				Messenger.getInstance().sendPluginMessage(e.getPlayer(), "house.alreadyDoor");
			}
			setIsCreatingDoor(e.getPlayer(), false, null);
			return false;
		}
		else {
			House house = getHouseToDoor(e.getClickedBlock());
			if (house != null) {
				if (house.getOwner() == e.getPlayer() || GrandTheftDiamond.checkPermission(e.getPlayer(), "use.house.door.other")) {
					return true;
				}	
				else {
					Messenger.getInstance().sendNoPermissionsMessage(e.getPlayer(), NoPermissionType.OPEN, "use.house.door.other");
					return false;
				}
			}
		}
		return true;
	}
	
	/**
	 * Sets whether a player is creating a door
	 * @param p The involved player
	 * @param isCreating Set true if player is creating a door, else false
	 * @param identifier The {@link House}'s identifier
	 */
	public void setIsCreatingDoor(Player p, boolean isCreating, String identifier) {
		TemporaryPluginData.getInstance().setIsCreatingDoorOfHouse(p, isCreating, identifier);
	}
	
	/**
	 * Returns the identifier of the {@link House}, the {@link Player} is setting a door for
	 * @param p The {@link Player}
	 * @return The {@link House}'s identifier. {@code null} if the {@link Player} is not creating a door
	 * @see #isCreatingDoor(Player)
	 */
	public String getDoorCreatingIdentifier(Player p) {
		return TemporaryPluginData.getInstance().getDoorCreatingIdentifier(p);
	}
	
	/**
	 * Checks whether a player is creating a door
	 * @param p The involved player
	 * @return True if the {@link Player} is creating a door, else {@code false}
	 */
	public boolean isCreatingDoor(Player p) {
		return TemporaryPluginData.getInstance().isCreatingDoorOfHouse(p);
	}
	
}

package me.micrjonas.grandtheftdiamond.house;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import me.micrjonas.grandtheftdiamond.data.storage.Storable;
import me.micrjonas.grandtheftdiamond.pluginsupport.dynmap.DynmapDisplayable;
import me.micrjonas.grandtheftdiamond.pluginsupport.dynmap.DynmapMarkerManager;
import me.micrjonas.grandtheftdiamond.pluginsupport.dynmap.MarkerSymbol;
import me.micrjonas.grandtheftdiamond.util.Removeable;
import me.micrjonas.grandtheftdiamond.util.SimpleLocation;
import me.micrjonas.grandtheftdiamond.util.bukkit.Doors;
import me.micrjonas.grandtheftdiamond.util.bukkit.Locations;
import me.micrjonas.grandtheftdiamond.util.bukkit.WorldStorage;

import org.bukkit.Effect;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.block.Block;

/**
 * Represents a Grand Theft Diamond house
 */
public class House implements Removeable, DynmapDisplayable, Storable {
	
	private final String name;
	private final Set<OfflinePlayer> members;
	private String markerLabel;
	private String markerLabelOwned;
	private Location spawn;
	private final Set<SimpleLocation> chests = new HashSet<>();
	private SimpleLocation[] doors = new SimpleLocation[2];
	private OfflinePlayer owner = null;
	private int price;
	private boolean removed;
	
	House(String name, Location spawn, OfflinePlayer owner, SimpleLocation door, Set<OfflinePlayer> members, int price) {
		this.name = name;
		this.spawn = spawn;
		this.owner = owner;
		setDoor(door, true);
		if (members != null) {
			members.remove(null);
			this.members = new HashSet<>(members);
		}
		else {
			this.members = new HashSet<>();
		}
		this.price = price;
	}
	
	House(String identifier, Location spawn) {
		this(identifier, spawn, null, null, null, 0);
	}
	
	void destroy() {
		removed = true;
	}
	
	@Override
	public MarkerSymbol getMarkerSymbol() {
		if (getOwner() != null) {
			return MarkerSymbol.HOUSE_OWNED;
		}
		else {
			return MarkerSymbol.HOUSE_BUYABLE;
		}
	}


	@Override
	public String getMarkerLabel() {
		if (getMarkerLocation() != null) {
			if (getOwner() == null) {
				if (markerLabel == null) {
					markerLabel = HouseManager.getInstance().getHouseMarkerLabel()
							.replace("%name%", getName())
							.replace("%price%", String.valueOf(getPrice()));
				}
				return markerLabel;
			}
			else {
				if (markerLabelOwned == null) {
					markerLabelOwned = HouseManager.getInstance().getHouseMarkerLabelOwned()
							.replace("%name%", getName())
							.replace("%owner%", getOwner().getName())
							.replace("%price%", String.valueOf(getPrice()));
				}
				return markerLabelOwned;
			}	
		}
		return null; // Disabled
	}

	@Override
	public Location getMarkerLocation() {
		if (!isRemoved() && HouseManager.getInstance().getHouseMarkerLabel() != null) {
			return getSpawn().clone();
		}
		return null;
	}
	
	@Override
	public String getName() {
		return name;
	}
	
	@Override
	public Map<String, Object> getStoreData() {
		Map<String, Object> data = new LinkedHashMap<>();
		List<String> members = new ArrayList<>();
		for (OfflinePlayer member : this.members) {
			members.add(member.getName());
		}
		data.put("price", price);
		data.put("members", members);
		data.putAll(Locations.toMap("spawn", spawn, WorldStorage.NAME, false));
		
		return data;
	}
	
	@Override
	public void remove() {
		if (!isRemoved()) {
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
	 * @deprecated Not implemented yet
	 */
	@SuppressWarnings("javadoc")
	@Deprecated
	public boolean isUsable() {
		return true;
	}
	
	/**
	 * Returns the spawn of the house
	 * @return The spawn of the house
	 */
	public Location getSpawn() {
		return spawn;
	}
	
	/**
	 * Sets the spawn of the house
	 * @param spawn The new spawn, requires non null
	 * @throws IllegalArgumentException if spawn is {@code null}
	 */
	public void setSpawn(Location spawn) {
		if (spawn == null) {
			throw new IllegalArgumentException("spawn is not allowed to be null");
		}
		this.spawn = spawn.clone();
	}
	
	/**
	 * Adds a chest to the house which is only available for the house owner and house members
	 * @param block The {@code Block} of the chest
	 * @return True if the chest was added successfully, {@code false} if the chest was already registered for this house
	 * @throws IllegalArgumentException Thrown if the {@code Block} is not a {@link Material.CHEST} or {@link Material.TRAPPED_CHEST}
	 */
	public boolean addChest(Block block) throws IllegalArgumentException {
		if (!(block.getType() == Material.CHEST || block.getType() == Material.TRAPPED_CHEST)) {
			throw new IllegalArgumentException("Block is not a chest");
		}
		return chests.contains(new SimpleLocation(block.getLocation()));
	}
	
	private void setDoor(SimpleLocation loc, boolean updateAtHouseManager) {
		SimpleLocation[] oldDoors = doors.clone();
		doors = Doors.getDoorLocations(loc);
		if (!updateAtHouseManager) {
			HouseManager.getInstance().updadeDoor(this, oldDoors, doors);
		}
	}
	
	/**
	 * Sets the door of the house
	 * @param door The new door, requires a door as block
	 * @throws IllegalArgumentException if door block is not a door
	 */
	public void setDoor(Block door) {
		if (!Doors.isDoor(door)) {
			throw new IllegalArgumentException("door is not a door block");
		}
		setDoor(new SimpleLocation(door.getLocation()));
	}
	
	/**
	 * Sets the door of the house at the given location
	 * @param loc The location of the door, block at location requires to be a door
	 */
	public void setDoor(SimpleLocation loc) {
		if (!Doors.isDoor(loc.getBlock())) {
			throw new IllegalArgumentException("Block at given location is not a door block");
		}
		setDoor(loc, false);
	}
	
	/**
	 * Returns the below block of the door
	 * @return The below block of the door. {@code null} if no door was set
	 */
	public Block getDoorBelow() {
		return doors[0] == null ? null : doors[0].getBlock();
	}
	
	/**
	 * Returns the over lock of the door
	 * @return The over block of the door. {@code null} if no door was set
	 */
	public Block getDoorOver() {
		return doors[1] == null ? null : doors[1].getBlock();
	}
	
	/**
	 * Opens or close the door of the house
	 * @param isOpen True if the door should be open, else false
	 */
	@SuppressWarnings("deprecation")
	public void setDoorOpen(boolean isOpen) {
		if (((doors[0].getBlock().getData() & 0x4) == 0x4) != isOpen) {
			doors[0].getBlock().getWorld().playEffect(doors[0].getBlock().getLocation(), Effect.DOOR_TOGGLE, 0);
		}
		doors[0].getBlock().setData((byte) (isOpen ? (doors[0].getBlock().getData() | 0x4) : (doors[0].getBlock().getData() & ~0x4)));
	}
	
	/**
	 * Returns the owner of the house
	 * @return The owner of the house. {@code null} if no owner exists
	 */
	public OfflinePlayer getOwner() {
		return owner;
	}
	
	/**
	 * Sets the owner of the house
	 * @param owner The new owner of the house. May be {@code null}
	 */
	public void setOwner(OfflinePlayer owner) {
		if (this.owner != owner) {
			markerLabelOwned = null;
			OfflinePlayer oldOwner = this.owner;
			this.owner = owner;
			HouseManager.getInstance().updateOwner(this, oldOwner, owner);
		}
	}
	
	/**
	 * Returns a {@link Collection} of all members of the house.
	 * Returns an unmodifiable view of the specified collection as "read-only"
	 * @return A {@code Collection} of all house members
	 * @see Collections#unmodifiableCollection(Collection)
	 */
	public Collection<OfflinePlayer> getMembers() {
		return Collections.unmodifiableCollection(members);
	}
	
	/**
	 * Removes all members and adds all members of the given Collection
	 * @param members The new members of the house. {@code null} to just remove all members
	 */
	public void setMembers(Collection<? extends OfflinePlayer> members) {
		this.members.clear();
		if (members != null) {
			this.members.addAll(members);
		}
	}
	
	/**
	 * Adds a member to the house
	 * @param member The new member
	 * @return True if member was added, false if the OfflinePlayer is alredy a member
	 */
	public boolean addMember(OfflinePlayer member) {
		return members.add(member);
	}
	
	/**
	 * Removes a member from the house
	 * @param member The member to remove
	 * @return True if member was removed, false if the OfflinePlayer was not a member
	 */
	public boolean removeMember(OfflinePlayer member) {
		return members.remove(member);
	}
	
	/**
	 * Returns the price of the house
	 * @return The price of the house
	 */
	public int getPrice() {
		return price;
	}
	
	/**
	 * Sets the price of the house
	 * @param price The new price of the house, requires to be >= 0
	 * @throws IllegalArgumentException if price is < 0
	 */
	public void setPrice(int price) {
		if (price < 0) {
			throw new IllegalArgumentException("price cannot be < 0");
		}
		if (this.price != price) {
			markerLabel = null;
			this.price = price;
			DynmapMarkerManager.getInstance().updateMarker(this, false, true, false);
		}
	}

}

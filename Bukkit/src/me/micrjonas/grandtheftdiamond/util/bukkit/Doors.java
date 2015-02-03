package me.micrjonas.grandtheftdiamond.util.bukkit;

import me.micrjonas.grandtheftdiamond.util.SimpleLocation;

import org.bukkit.Effect;
import org.bukkit.Location;
import org.bukkit.block.Block;

/**
 * Contains some utility methods for doors
 */
public class Doors {
	
	private Doors() { }
	
	
	/**
	 * Checks whether {@code door} is a door block
	 * @param door Block to check
	 * @return True if {@code door} is a door block, else false
	 */
	@SuppressWarnings("incomplete-switch")
	public static boolean isDoor(Block door) {
		if (door != null) {
			switch (door.getType()) {
				case WOODEN_DOOR:
				case IRON_DOOR_BLOCK:
					return true;
			}
		}
		return false;
	}
	
	
	public static boolean isDoorAt(Location loc) {
		return loc != null && isDoor(loc.getBlock());
	}
	
	public static boolean isDoorAt(SimpleLocation loc) {
		return loc != null && isDoor(loc.getBlock());
	}
	
	public static SimpleLocation[] getDoorLocations(SimpleLocation loc) {
		SimpleLocation[] doors = new SimpleLocation[2];
		if (isDoorAt(loc)) {
			if (isDoorAt(loc.getLocationOver())) {
				doors[0] = loc;
				doors[1] = loc.getLocationOver();
			}
			else {
				doors[0] = loc.getLocationBelow();
				doors[1] = loc;
			}
		}
		return doors;
	}
	
	@SuppressWarnings("deprecation")
	public static boolean isOpen(SimpleLocation loc) {
		return (loc.getBlock().getData() & 0x4) == 4;
	}
	
	@SuppressWarnings("deprecation")
	public static void setOpen(SimpleLocation loc, boolean isOpen) {
		loc.getBlock().setData((byte)(isOpen ? loc.getBlock().getData() | 0x4 : loc.getBlock().getData() & 0xFFFFFFFB));
	}
	
	@SuppressWarnings("deprecation")
	public static void changeDoorState(SimpleLocation loc) {
		Block block = loc.getBlock();
		if (isDoor(block)) {
			if (block.getData() < 4) {
				block.setData((byte) (block.getData() + 4));
			}
			else {
				block.setData((byte) (block.getData() - 4));
			}
			block.getWorld().playEffect(block.getLocation(), Effect.DOOR_TOGGLE, 0);
		}
	}

}

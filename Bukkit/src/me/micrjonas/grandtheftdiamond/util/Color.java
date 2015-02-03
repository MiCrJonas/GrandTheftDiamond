package me.micrjonas.grandtheftdiamond.util;

import org.bukkit.ChatColor;
import org.bukkit.DyeColor;

/**
 * Represents a {@link ChatColor} and a {@link DyeColor}
 */
public enum Color {
	
	BLACK('0', ChatColor.BLACK, (short) DyeColor.BLACK.ordinal()),
	BLUE('1', ChatColor.DARK_BLUE,(short) DyeColor.BLUE.ordinal()),
	BRIGHT_GREEN('a', ChatColor.GREEN,(short) DyeColor.LIME.ordinal()),
	CYAN('3', ChatColor.DARK_AQUA,(short) DyeColor.CYAN.ordinal()),
	DARK_GRAY('8', ChatColor.DARK_GRAY,(short) DyeColor.GRAY.ordinal()),
	GRAY('7', ChatColor.GRAY,(short) DyeColor.SILVER.ordinal()),
	GREEN('2', ChatColor.DARK_GREEN,(short) DyeColor.GREEN.ordinal()),
	LIGHT_BLUE('9', ChatColor.BLUE,(short) DyeColor.LIGHT_BLUE.ordinal()),
	MAGENTA('c', ChatColor.RED,(short) DyeColor.MAGENTA.ordinal()),
	ORANGE('6', ChatColor.GOLD,(short) DyeColor.ORANGE.ordinal()),
	PINK('d', ChatColor.LIGHT_PURPLE,(short) DyeColor.PINK.ordinal()),
	PURPLE('5', ChatColor.DARK_PURPLE,(short) DyeColor.PURPLE.ordinal()),
	RED('4', ChatColor.DARK_RED,(short) DyeColor.RED.ordinal()),
	WHITE('f', ChatColor.WHITE,(short) DyeColor.WHITE.ordinal()),
	YELLOW('e', ChatColor.YELLOW,(short) DyeColor.YELLOW.ordinal());
	
	private final char chatValue;
	private final ChatColor chatColor;
	private final short itemValue;
	
	private Color(char chatValue, ChatColor chatColor, short itemValue) {
		this.chatValue = chatValue;
		this.chatColor = chatColor;
		this.itemValue = itemValue;
	}
	
	/**
	 * Returns the char of the related {@link ChatColor}
	 * @return The char symbol of the color
	 */
	public char getChatValue() {
		return chatValue;
	}
	
	/**
	 * Returns the related {@link ChatColor}
	 * @return The related {@link ChatColor}
	 */
	public ChatColor getChatColor() {
		return chatColor;
	}
	
	/**
	 * Returns the damage value of colored wool, related to this color
	 * @return The related item damage value
	 */
	public short getItemDamageValue() {
		return itemValue;	
	}

}

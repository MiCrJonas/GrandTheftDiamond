package me.micrjonas.grandtheftdiamond.sign.handler;

import me.micrjonas.grandtheftdiamond.jail.JailManager;

import org.bukkit.Sound;
import org.bukkit.entity.Player;

/**
 * Listens to the interaction of a {@link Player} with a jail sign
 */
public class JailSignHandler implements SignHandler {

	@Override
	public void signClicked(Player clicker, String[] lines, String[] parsedLines) {
		clicker.sendMessage("Poof, jails are not implementet yet!");
		clicker.playSound(clicker.getLocation(), Sound.EXPLODE, 1, 1);
	}

	@Override
	public boolean isValid(String[] lines, String[] parsedLines) {
		return JailManager.getInstance().isJail(parsedLines[2]);
	}
}

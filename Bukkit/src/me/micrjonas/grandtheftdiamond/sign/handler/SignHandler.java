package me.micrjonas.grandtheftdiamond.sign.handler;

import me.micrjonas.grandtheftdiamond.sign.SignManager;
import me.micrjonas.grandtheftdiamond.util.Listener;

import org.bukkit.entity.Player;


/**
 * Listens to the interaction of a {@link Player} with a sign
 */
public interface SignHandler extends Listener {
	
	/**
	 * Listens to a {@link Player} interaction with a sign. It's required to do a permissions check for
	 * 	the {@code clicker}. The {@link SignManager} calls {@link #isValid(String[], String[])} first, and only
	 * 	if it returns {@code true}, this method will be called. Editing the passed lines doesn't change the sign
	 * @param clicker The {@link Player} who clicked the sign
	 * @param lines A copy of the original lines of the sign. Does only contain line 2-4,
	 *  so length is 3. Empty lines are represented as empty String (""). No array element is {@code null}
	 * @param parsedLines The lines of the sign without any color codes and in lower-cases. Does only contain line 2-4,
	 *  so length is 3. Empty lines are represented as empty String (""). No array element is {@code null}
	 *  so length is 3
	 */
	void signClicked(Player clicker, String[] lines, String[] parsedLines);
	
	/**
	 * Checks whether a sign is valid to use or whether there are invalid lines. The {@link SignManager} doesn't call
	 * 	{@link #signClicked(Player, String[], String[])} if this method returns {@code false} for a specific sign.<br>
	 * 	Only line 3 and 4 (array index 1 and 2) are required to check
	 * @param lines A copy of the original lines of the sign. Does only contain line 2-4,
	 *  so length is 3. Empty lines are represented as empty String (""). No array element is {@code null}
	 * @param parsedLines The lines of the sign without any color codes and in lower-cases. Does only contain line 2-4,
	 *  so length is 3. Empty lines are represented as empty String (""). No array element is {@code null}
	 * @return True if the sign is valid, else {@code false}. A {@code false}-return removes the sign
	 */
	boolean isValid(String[] lines, String[] parsedLines);
	
}

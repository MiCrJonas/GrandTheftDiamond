package me.micrjonas.grandtheftdiamond.sign;

import me.micrjonas.grandtheftdiamond.listener.SignListener;
import me.micrjonas.grandtheftdiamond.sign.handler.HouseSignHandler;
import me.micrjonas.grandtheftdiamond.sign.handler.ItemSignHandler;
import me.micrjonas.grandtheftdiamond.sign.handler.JailSignHandler;
import me.micrjonas.grandtheftdiamond.sign.handler.JoinSignHandler;
import me.micrjonas.grandtheftdiamond.sign.handler.LeaveSignHandler;
import me.micrjonas.grandtheftdiamond.sign.handler.ShopSignHandler;
import me.micrjonas.grandtheftdiamond.sign.handler.SignHandler;
import me.micrjonas.grandtheftdiamond.util.StringKeyListenerManager;
import me.micrjonas.grandtheftdiamond.util.StringUtils;

import org.bukkit.block.Sign;
import org.bukkit.entity.Player;

/**
 * Manages the signs of the plugin
 */
public class SignManager extends StringKeyListenerManager<SignHandler> {

	private final static SignManager instance = new SignManager();
	
	/**
	 * Returns the loaded instance
	 * @return The loaded instance
	 */
	public static SignManager getInstance() {
		return instance;
	}
	
	private SignManager() {
		registerListener("house", new HouseSignHandler());
		registerListener("item", new ItemSignHandler());
		registerListener("jail", new JailSignHandler());
		registerListener("join", new JoinSignHandler());
		registerListener("leave", new LeaveSignHandler());
		registerListener("shop", new ShopSignHandler());
	}
	
	/**
	 * Called when a {@link Player} clicked a plugin sign
	 * @param sign The clicked sign
	 * @param p The {@link Player} who clicked
	 * @throws IllegalArgumentException Thrown if {@code sign} or {@code p} are null
	 */
	public void onSignClick(Sign sign, Player p) throws IllegalArgumentException {
		String[] lines = new String[3];
		String[] parsedLines = new String[3];
		for (int i = 0; i < 3; i++) {
			lines[i] = sign.getLine(i + 1);
			parsedLines[i] = StringUtils.removeColors(lines[i]).toLowerCase();
		}
		SignHandler handler = getListener(parsedLines[0]);
		if (handler != null && handler.isValid(lines, parsedLines)) {
			handler.signClicked(p, lines, parsedLines);
		}
		else {
			SignListener.setValid(sign, false);
		}
	}

}

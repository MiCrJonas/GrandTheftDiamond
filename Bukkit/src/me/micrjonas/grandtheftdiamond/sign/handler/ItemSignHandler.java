package me.micrjonas.grandtheftdiamond.sign.handler;

import me.micrjonas.grandtheftdiamond.GrandTheftDiamond;
import me.micrjonas.grandtheftdiamond.item.pluginitem.ItemManager;
import me.micrjonas.grandtheftdiamond.item.pluginitem.PluginItem;
import me.micrjonas.grandtheftdiamond.messenger.NoPermissionType;

import org.bukkit.entity.Player;


/**
 * Listens to the interaction of a {@link Player} with an item sign
 */
public class ItemSignHandler implements SignHandler {

	private final String PERMISSION = "use.sign.item.";
	
	@Override
	public void signClicked(Player clicker, String[] lines, String[] parsedLines) {
		String itemName = lines[1];
		if (GrandTheftDiamond.checkPermission(clicker, PERMISSION + itemName, true, NoPermissionType.USE)) {
			PluginItem item = ItemManager.getInstance().getItem(itemName);
			if (item != null) {
				item.giveToPlayer(clicker, 1);
			}	
		}
	}

	@Override
	public boolean isValid(String[] lines, String[] parsedLines) {
		return ItemManager.getInstance().getItem(lines[1]) != null;
	}
	
}

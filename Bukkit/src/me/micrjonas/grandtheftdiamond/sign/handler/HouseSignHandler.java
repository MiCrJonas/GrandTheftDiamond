package me.micrjonas.grandtheftdiamond.sign.handler;

import me.micrjonas.grandtheftdiamond.GrandTheftDiamond;
import me.micrjonas.grandtheftdiamond.data.FileReloadListener;
import me.micrjonas.grandtheftdiamond.data.PluginFile;
import me.micrjonas.grandtheftdiamond.house.House;
import me.micrjonas.grandtheftdiamond.house.HouseManager;
import me.micrjonas.grandtheftdiamond.manager.EconomyManager;
import me.micrjonas.grandtheftdiamond.messenger.Messenger;
import me.micrjonas.grandtheftdiamond.messenger.NoPermissionType;
import me.micrjonas.grandtheftdiamond.sign.SignType;
import me.micrjonas.grandtheftdiamond.sign.SignUpdater;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;


/**
 * Listens to the interaction of a {@link Player} with an house sign
 */
public class HouseSignHandler implements FileReloadListener, SignHandler {

	private final String PERMISSION = "use.sign.house.";
	
	private boolean teleportToOwnHouse;
	
	/**
	 * Default constructor
	 */
	public HouseSignHandler() {
		GrandTheftDiamond.registerFileReloadListener(this);
	}
	
	@Override
	public void configurationReloaded(PluginFile file, FileConfiguration fileConfiguration) {
		if (file == PluginFile.CONFIG) {
			teleportToOwnHouse = fileConfiguration.getBoolean("signs.house.teleportToHouseIfOwn");
		}
	}
	
	@Override
	public void signClicked(Player clicker, String[] lines, String[] parsedLines) {
		House house = HouseManager.getInstance().getHouse(lines[1]);
		if (house != null) {
			if (house.getOwner() == null) {
				if (GrandTheftDiamond.checkPermission(clicker, PERMISSION + "buy." + lines[1], true, NoPermissionType.USE)) {
					if (EconomyManager.getInstance().hasBalance(clicker, house.getPrice())) {
						EconomyManager.getInstance().withdraw(clicker, house.getPrice(), true);
						house.setOwner(clicker);
						SignUpdater.getInstance().updateSigns(SignType.HOUSE);
						Messenger.getInstance().sendPluginMessage(clicker, "house.bought", "%house%", house.getName());
					}
					else {
						Messenger.getInstance().sendPluginMessage(clicker, "notEnoughMoney");
					}
				}
			}
			else if (teleportToOwnHouse) {
				if ((house.getOwner() == clicker && GrandTheftDiamond.checkPermission(clicker, "use.sign.house.teleport.own")) ||
						GrandTheftDiamond.checkPermission(clicker, PERMISSION + "teleport.other")) {
					clicker.teleport(house.getSpawn());
					Messenger.getInstance().sendPluginMessage(clicker, "house.teleported", "%house%", house.getName());
				}
				else {
					Messenger.getInstance().sendPluginMessage(clicker, "noPermissionsUse");
				}
			}
			else {
				Messenger.getInstance().sendPluginMessage(clicker, "house.notPurchasable");
			}
		}
		else {
			Messenger.getInstance().sendPluginMessage(clicker, "sign.invalidSignClicked");
		}
	}

	@Override
	public boolean isValid(String[] lines, String[] parsedLines) {
		// TODO Auto-generated method stub
		return false;
	}

}

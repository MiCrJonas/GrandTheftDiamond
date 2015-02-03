package me.micrjonas.grandtheftdiamond.sign;

import me.micrjonas.grandtheftdiamond.GameManager;
import me.micrjonas.grandtheftdiamond.GrandTheftDiamond;
import me.micrjonas.grandtheftdiamond.Team;
import me.micrjonas.grandtheftdiamond.api.event.cause.JoinReason;
import me.micrjonas.grandtheftdiamond.api.event.cause.LeaveReason;
import me.micrjonas.grandtheftdiamond.data.FileReloadListener;
import me.micrjonas.grandtheftdiamond.data.PluginFile;
import me.micrjonas.grandtheftdiamond.data.TemporaryPluginData;
import me.micrjonas.grandtheftdiamond.house.House;
import me.micrjonas.grandtheftdiamond.house.HouseManager;
import me.micrjonas.grandtheftdiamond.item.pluginitem.ItemManager;
import me.micrjonas.grandtheftdiamond.item.pluginitem.PluginItem;
import me.micrjonas.grandtheftdiamond.listener.SignListener;
import me.micrjonas.grandtheftdiamond.manager.EconomyManager;
import me.micrjonas.grandtheftdiamond.messenger.Messenger;
import me.micrjonas.grandtheftdiamond.messenger.NoPermissionType;
import me.micrjonas.grandtheftdiamond.sign.handler.SignHandler;
import me.micrjonas.grandtheftdiamond.util.StringKeyListenerManager;
import me.micrjonas.grandtheftdiamond.util.StringUtils;

import org.bukkit.Sound;
import org.bukkit.block.Sign;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;

/**
 * Manages when a {@code Player} clicks a sign
 */
public class SignManager extends StringKeyListenerManager<SignHandler> implements FileReloadListener {

	private boolean useVaultEconomy;
	private String currencySymbol;
	private boolean teleportToOwnHouse;
	
	public SignManager() {
		GrandTheftDiamond.registerFileReloadListener(this);
	}
	

	@Override
	public void configurationReloaded(PluginFile file, FileConfiguration fileConfiguration) {
		if (file == PluginFile.CONFIG) {
			useVaultEconomy = fileConfiguration.getBoolean("signs.shop.useVaultEconomy");
			currencySymbol = fileConfiguration.getString("economy.currency.symbol");
			teleportToOwnHouse = fileConfiguration.getBoolean("signs.house.teleportToHouseIfOwn");
		}
	}
	
	/**
	 * Called when a {@link Player} clicked a sign
	 * @param lines A copy of the original lines of the sign
	 * @param clicker The {@link Player} who clicked the sign
	 */
	public void signClicked(String[] lines, Player clicker) {
		String[] parsedLines = new String[3];
		for (int i = 0; i < 3; i++) {
			parsedLines[i] = StringUtils.removeColors(lines[i + 1]).toLowerCase();
		}
		SignHandler handler = getListener(parsedLines[0]);
		if (handler != null) {
			if (handler.isValid(lines, parsedLines)) {
				handler.signClicked(clicker, lines, parsedLines);
			}
		}
	}
	
	/**
	 * Called when a player clicked a plugin sign
	 * @param sign The clicked sign
	 * @param p The player who clicked
	 * @throws IllegalArgumentException Thrown if {@code sign} or {@code p} are null
	 */
	public void onSignClick(Sign sign, Player p) throws IllegalArgumentException {
		String[] lines = new String[3];
		for (int i = 1; i < 4; i++) {
			lines[i - 1] = StringUtils.removeColors(sign.getLine(i)).toLowerCase();
		}
		if (lines[0].contains("house")) {
			House house = HouseManager.getInstance().getHouse(lines[1]);
			if (house != null) {
				if (house.getOwner() == null) {
					if (GrandTheftDiamond.checkPermission(p, "use.sign.house.buy." + lines[1], true, NoPermissionType.USE)) {
						if (EconomyManager.getInstance().hasBalance(p, house.getPrice())) {
							EconomyManager.getInstance().withdraw(p, house.getPrice(), true);
							house.setOwner(p);
							SignUpdater.getInstance().updateSigns(SignType.HOUSE);
							Messenger.getInstance().sendPluginMessage(p, "house.bought", "%house%", house.getName());
						}
						else {
							Messenger.getInstance().sendPluginMessage(p, "notEnoughMoney");
						}
					}
				}
				else if (teleportToOwnHouse) {
					if ((house.getOwner() == p && GrandTheftDiamond.checkPermission(p, "use.sign.house.teleport.own")) ||
							GrandTheftDiamond.checkPermission(p, "use.sign.house.teleport.other")) {
						p.teleport(house.getSpawn());
						Messenger.getInstance().sendPluginMessage(p, "house.teleported", "%house%", house.getName());
					}
					else {
						Messenger.getInstance().sendPluginMessage(p, "noPermissionsUse");
					}
				}
				else {
					Messenger.getInstance().sendPluginMessage(p, "house.notPurchasable");
				}
			}
			else {
				SignListener.setValid(sign, false);
				Messenger.getInstance().sendPluginMessage(p, "sign.invalidSignClicked");
			}
		}
		else if (lines[0].contains("item")) {
			PluginItem item = ItemManager.getInstance().getItem(lines[1]);
			if (item != null) {
				item.giveToPlayer(p, 1);
			}
		}
		else if (lines[0].contains("jail")) {
			p.sendMessage("Poof, jails are not implementet yet!");
			p.playSound(p.getLocation(), Sound.EXPLODE, 1, 1);
		}
		else if (lines[0].contains("join")) {
			if (TemporaryPluginData.getInstance().isIngame(p)) {
				Messenger.getInstance().sendPluginMessage(p, "alreadyIngame");
				return;
			}
			if (lines[1].contains("cop")) {
				if (GrandTheftDiamond.checkPermission(p, "use.sign.join.cop", true, NoPermissionType.USE)) {
					GameManager.getInstance().joinGame(p, Team.COP, JoinReason.SIGN);
				}
			}
				
			else if (lines[1].contains("civilian")) {
				if (GrandTheftDiamond.checkPermission(p, "use.sign.join.civilian", true, NoPermissionType.USE)) {
					GameManager.getInstance().joinGame(p, Team.CIVILIAN, JoinReason.SIGN);
				}
			}
			
		}
		else if (lines[0].contains("leave")) {
			if (GrandTheftDiamond.checkPermission(p, "use.sign.leave", true, NoPermissionType.USE)) {
				if (TemporaryPluginData.getInstance().isIngame(p)) {
					GameManager.getInstance().leaveGame(p, LeaveReason.SIGN);
				}
				else {
					Messenger.getInstance().sendPluginMessage(p, "notIngame");
				}
			}
		}
		else if (lines[0].contains("shop")) {
			if (lines[2].startsWith(currencySymbol)) {
				lines[2] = lines[2].substring(currencySymbol.length(), lines[2].length());
			}
			int price;
			try {
				//price = Integer.parseInt(ChatColor.translateAlternateColorCodes('&', lines[2]).replaceAll("(?i)&([a-f0-9])", "").replaceAll("[^0-9]", ""));
				price = Integer.parseInt(lines[2]);
			}
			catch (NumberFormatException ex) {
				GrandTheftDiamond.getLogger().warning("Cannot convert '" + lines[2] + "' to a number. Please report it to the plugin page.");
				return;
			}
			if (useVaultEconomy && !EconomyManager.getInstance().hasVaultBalance(p, price)) {
				Messenger.getInstance().sendPluginMessage(p, "notEnoughVaultMoney");
				return;
			}
			PluginItem item = ItemManager.getInstance().getItem(lines[1]);
			if (item != null) {
				item.giveToPlayer(p, 1);
				EconomyManager.getInstance().withdraw(p, price, useVaultEconomy);
			}
		}
	}

}

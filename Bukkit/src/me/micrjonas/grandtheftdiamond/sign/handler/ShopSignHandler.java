package me.micrjonas.grandtheftdiamond.sign.handler;

import me.micrjonas.grandtheftdiamond.GrandTheftDiamond;
import me.micrjonas.grandtheftdiamond.data.FileReloadListener;
import me.micrjonas.grandtheftdiamond.data.PluginFile;
import me.micrjonas.grandtheftdiamond.item.pluginitem.ItemManager;
import me.micrjonas.grandtheftdiamond.item.pluginitem.PluginItem;
import me.micrjonas.grandtheftdiamond.manager.EconomyManager;
import me.micrjonas.grandtheftdiamond.messenger.Messenger;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;

/**
 * Listens to the interaction of a {@link Player} with a shop sign
 */
public class ShopSignHandler implements FileReloadListener, SignHandler {

	private boolean useVaultEconomy;
	private String currencySymbol;
	
	/**
	 * Default constructor
	 */
	public ShopSignHandler() {
		GrandTheftDiamond.registerFileReloadListener(this);
	}
	
	@Override
	public void configurationReloaded(PluginFile file, FileConfiguration fileConfiguration) {
		if (file == PluginFile.CONFIG) {
			useVaultEconomy = fileConfiguration.getBoolean("signs.shop.useVaultEconomy");
			currencySymbol = fileConfiguration.getString("economy.currency.symbol");
		}
	}
	
	@Override
	public void signClicked(Player clicker, String[] lines, String[] parsedLines) {
		if (parsedLines[2].startsWith(currencySymbol)) {
			parsedLines[2] = parsedLines[2].substring(currencySymbol.length(), parsedLines[2].length());
		}
		int price;
		try {
			//price = Integer.parseInt(ChatColor.translateAlternateColorCodes('&', parsedLines[2]).replaceAll("(?i)&([a-f0-9])", "").replaceAll("[^0-9]", ""));
			price = Integer.parseInt(parsedLines[2]);
		}
		catch (NumberFormatException ex) {
			GrandTheftDiamond.getLogger().warning("Cannot convert '" + parsedLines[2] + "' to a number. Please report it to the plugin page.");
			return;
		}
		if (useVaultEconomy && !EconomyManager.getInstance().hasVaultBalance(clicker, price)) {
			Messenger.getInstance().sendPluginMessage(clicker, "notEnoughVaultMoney");
			return;
		}
		PluginItem item = ItemManager.getInstance().getItem(parsedLines[1]);
		if (item != null) {
			item.giveToPlayer(clicker, 1);
			EconomyManager.getInstance().withdraw(clicker, price, useVaultEconomy);
		}
	}

	@Override
	public boolean isValid(String[] lines, String[] parsedLines) {
		return ItemManager.getInstance().isItem(parsedLines[1]);
	}

}

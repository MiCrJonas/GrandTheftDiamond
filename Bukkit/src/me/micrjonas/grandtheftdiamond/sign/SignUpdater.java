package me.micrjonas.grandtheftdiamond.sign;

import java.util.HashSet;
import java.util.Set;

import me.micrjonas.grandtheftdiamond.GrandTheftDiamond;
import me.micrjonas.grandtheftdiamond.BukkitGrandTheftDiamondPlugin;
import me.micrjonas.grandtheftdiamond.Team;
import me.micrjonas.grandtheftdiamond.data.FileManager;
import me.micrjonas.grandtheftdiamond.data.FileReloadListener;
import me.micrjonas.grandtheftdiamond.data.PluginData;
import me.micrjonas.grandtheftdiamond.data.PluginFile;
import me.micrjonas.grandtheftdiamond.data.TemporaryPluginData;
import me.micrjonas.grandtheftdiamond.house.House;
import me.micrjonas.grandtheftdiamond.house.HouseManager;
import me.micrjonas.grandtheftdiamond.listener.SignListener;
import me.micrjonas.grandtheftdiamond.util.StringUtils;
import me.micrjonas.grandtheftdiamond.util.bukkit.Locations;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Sign;
import org.bukkit.configuration.file.FileConfiguration;

/**
 * Updates the signs on a file reload (signs.yml)
 */
public class SignUpdater implements FileReloadListener {
	
	private static SignUpdater instance = new SignUpdater();
	
	public static SignUpdater getInstance() {
		return instance;
	}
	
	private final FileConfiguration signData;
	
	private String signTitle;
	private String currencySymbol;
	private String houseForFree;
	
	private SignUpdater() {
		GrandTheftDiamond.registerFileReloadListener(this);
		configurationReloaded(PluginFile.CONFIG, FileManager.getInstance().getFileConfiguration(PluginFile.CONFIG));
		signData = FileManager.getInstance().getFileConfiguration(PluginFile.SIGNS);
	}
	

	@Override
	public void configurationReloaded(PluginFile file, FileConfiguration fileConfiguration) {
		if (file == PluginFile.CONFIG) {
			currencySymbol = fileConfiguration.getString("economy.currency.symbol");
			houseForFree = ChatColor.translateAlternateColorCodes('&', fileConfiguration.getString("signs.house.free"));
			String newSignTitle = ChatColor.translateAlternateColorCodes('&', fileConfiguration.getString("signs.signTitle"));
			if (!newSignTitle.equals(signTitle)) {
				signTitle = newSignTitle;
				updateAllSigns(true);
			}
		}
	}
	
	/**
	 * Updates all signs of the given {@link SignType} and removes them, if they no longer exist
	 * @param type The type to update
	 */
	public void updateSigns(final SignType type) {
		updateSigns(type, false);
	}
	
	
	private void updateSigns(final SignType type, final boolean onlyTitle) {
		Runnable toRun = new Runnable() {
			@Override
			public void run() {
				if (signData.isConfigurationSection(type.name())) {
					Set<Sign> signs = new HashSet<>();
					for (String sign : signData.getConfigurationSection(type.name()).getKeys(false)) {
						Location loc = Locations.getLocationFromFile(signData, type.name() + "." + sign, true);
						if (loc.getWorld() == null 
								|| loc.getBlock() == null 
								|| !(loc.getBlock().getType() == Material.WALL_SIGN  || loc.getBlock().getType() == Material.SIGN_POST)
								|| (type.mustBeInArena() && !PluginData.getInstance().inArena(loc))) {
							
							removeSign(sign, type);
						}
						else {
							Sign s = (Sign) loc.getBlock().getState();
							if (!s.getLine(0).equals(signTitle)) {
								s.setLine(0, signTitle);
							}
							if (s.getLine(1).toUpperCase().contains(type.name())) {
								signs.add(s);
							}
							else {
								removeSign(sign, type);
							}
						}
					}
					if (!onlyTitle) {
						switch (type) {
							case HOUSE: {
								for (Sign s : signs) {
									House house = HouseManager.getInstance().getHouse(StringUtils.removeColors(s.getLine(2).toLowerCase()));
									if (house != null) {
										if (house.getOwner() == null) {
											if (house.getPrice() == 0) {
												s.setLine(3, houseForFree);
											}
											else {
												s.setLine(3, currencySymbol + house.getPrice());
											}
										}
										else {
											s.setLine(3, house.getOwner().getName());
										}
										s.update();
									}
									else {
										removeSign(s, type);
										SignListener.setValid(s, false);
									}
								}
							} break;
							
							case ITEM: {
								// Implement
							} break;
							
							case JAIL: {
								// Implement
							} break;
							
							case JOIN: {
								for (Sign s : signs) {
									boolean isTeam = false;
									for (Team team : Team.values()) {
										if (s.getLine(2).toUpperCase().contains(team.name())) {
											isTeam = true;
											break;
										}
									}
									if (!isTeam) {
										s.setLine(2, "CIVILIAN");
									}
									s.setLine(3, "Ingame: " + TemporaryPluginData.getInstance().getIngameCount());
									s.update();
								}
							} break;
							
							case LEAVE: {
								// Implement
							} break;
							
							case SHOP: {
								// Implement
							} break;
							
							default:
								break;
						}
					}
				}
			}
		};
		if (BukkitGrandTheftDiamondPlugin.getInstance().isEnabled()) {
			GrandTheftDiamond.runTask(toRun);
		}
		else {
			toRun.run();
		}
	}
	
	public void updateAllSigns() {
		updateAllSigns(false);
	}
	
	private void updateAllSigns(boolean onlyTitle) {
		for (SignType type : SignType.values()) {
			updateSigns(type, onlyTitle);
		}
	}
	
	private void removeSign(String sign, SignType type) {
		signData.set(type.name() + "." + sign, null);
	}
	
	private void removeSign(Sign sign, SignType type) {
		Location loc = sign.getLocation();
		signData.set(type.name() + "." + loc.getBlockX() + "_" + loc.getBlockY() + "_" + loc.getBlockZ() + "_" + loc.getWorld(), null);
		SignListener.setValid(sign, false);
	}

}

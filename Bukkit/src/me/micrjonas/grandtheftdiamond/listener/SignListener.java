package me.micrjonas.grandtheftdiamond.listener;

import me.micrjonas.grandtheftdiamond.GrandTheftDiamond;
import me.micrjonas.grandtheftdiamond.Team;
import me.micrjonas.grandtheftdiamond.data.FileManager;
import me.micrjonas.grandtheftdiamond.data.FileReloadListener;
import me.micrjonas.grandtheftdiamond.data.PluginData;
import me.micrjonas.grandtheftdiamond.data.PluginFile;
import me.micrjonas.grandtheftdiamond.data.TemporaryPluginData;
import me.micrjonas.grandtheftdiamond.house.House;
import me.micrjonas.grandtheftdiamond.house.HouseManager;
import me.micrjonas.grandtheftdiamond.item.pluginitem.ItemManager;
import me.micrjonas.grandtheftdiamond.messenger.Messenger;
import me.micrjonas.grandtheftdiamond.messenger.NoPermissionType;
import me.micrjonas.grandtheftdiamond.sign.SignType;
import me.micrjonas.grandtheftdiamond.util.StringUtils;

import org.bukkit.Location;
import org.bukkit.block.Sign;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.SignChangeEvent;

public class SignListener implements Listener, FileReloadListener {

	private final FileConfiguration signData;
	
	private String signTitleAlternare;
	private static String signTitle;
	private String currencySymbol;
	private String houseForFree;
	
	private String cooldownInactive;
	
	public SignListener() {
		GrandTheftDiamond.registerFileReloadListener(this);
		signData = FileManager.getInstance().getFileConfiguration(PluginFile.SIGNS);
	}
	
	

	@Override
	public void configurationReloaded(PluginFile file, FileConfiguration fileConfiguration) {
		if (file == PluginFile.CONFIG) {
			signTitleAlternare = fileConfiguration.getString("signs.signTitle");
			signTitle = StringUtils.translateColors(signTitleAlternare);
			currencySymbol = fileConfiguration.getString("economy.currency.symbol");
			cooldownInactive = StringUtils.translateColors(fileConfiguration.getString("signs.item.cooldown.inactive"));
			houseForFree = StringUtils.translateColors(fileConfiguration.getString("signs.house.free"));
		}
	}
	
	
	@EventHandler
	public void onSignSet(SignChangeEvent e) {
		if (e.getLine(0).toUpperCase().equals("[GTD]") || e.getLine(0).equals(signTitleAlternare)) {
			if (PluginData.getInstance().arenaSet(true)) {
				if (!e.getLine(1).equals("")) {
					SignType type = null;
					for (SignType tmpType : SignType.values()) {
						if (e.getLine(1).toUpperCase().contains(tmpType.name())) {
							type = tmpType;
							break;
						}
					}
					
					if (type != null) {
						if (GrandTheftDiamond.checkPermission(e.getPlayer(), "setup.sign." + type.name().toLowerCase(), true, NoPermissionType.CREATE)) {
							
							if (!type.mustBeInArena() || PluginData.getInstance().inArena(e.getBlock().getLocation())) {
							
								for (int i = 0; i < type.getRequiredLines(); i++) {
									
									if (e.getLine(i + 2).equals("")) {
										
										Messenger.getInstance().sendPluginMessage(e.getPlayer(), "signLineEmpty", new String[]{"%line%"}, new String[]{String.valueOf(i + 2)});
										return;
										
									}
									
								}
								
								if (create(e, type)) {
									
									Location loc = e.getBlock().getLocation();
									String path = loc.getBlockX() + "_" + loc.getBlockY() + "_" + loc.getBlockZ() + "_" + loc.getWorld().getName();
									
									signData.set(type.name() + "." + path + ".world", loc.getWorld().getName());
									signData.set(type.name() + "." + path + ".x", loc.getBlockX());
									signData.set(type.name() + "." + path + ".y", loc.getBlockY());
									signData.set(type.name() + "." + path + ".z", loc.getBlockZ());
									
									setValid(e, true);
									Messenger.getInstance().sendPluginMessage(e.getPlayer(), "signCreated");
									
								}
								
								else
									setValid(e, false);
								
							}
							
							else  {
								
								Messenger.getInstance().sendPluginMessage(e.getPlayer(), "mustBeInArena");
								setValid(e, false);
								
							}
							
						}
						
						else 
							setValid(e, false);
						
					}
					
					else {
						
						setValid(e, false);
						Messenger.getInstance().sendPluginMessage(e.getPlayer(), "signLineWrong", new String[]{"%line%"}, new String[]{"2"});
						
					}
					
				}
				
				else {
					
					setValid(e, false);
					Messenger.getInstance().sendPluginMessage(e.getPlayer(), "signLineEmpty", new String[]{"%line%"}, new String[]{"2"});
					
				}
				
			}
			
			else {
				
				if (GrandTheftDiamond.checkPermission(e.getPlayer(), "setup.create")) {
					
					setValid(e, false);
					Messenger.getInstance().sendPluginMessage(e.getPlayer(), "youNeedSetupMap");
					
				}
				
				else {
					
					setValid(e, false);
					Messenger.getInstance().sendPluginMessage(e.getPlayer(), "adminNeedSetupMap");
					
				}
				
			}
			
		}
		
	}
	
	@EventHandler
	public void onSignBreak(BlockBreakEvent e) {
		if (e.getBlock().getState() instanceof Sign) {
			Sign s = (Sign) e.getBlock().getState();
			if (s.getLine(0).equals(signTitle)) {
				if (!GrandTheftDiamond.checkPermission(e.getPlayer(), "setup.sign." + s.getLine(1), true, NoPermissionType.BREAK))
					e.setCancelled(true);
			}
		}
	}
	
	private boolean create(SignChangeEvent e, SignType type) {
		
		if (type == SignType.HOUSE) {
			
			if (HouseManager.getInstance().isHouse(e.getLine(2))) {
				
				House house = HouseManager.getInstance().getHouse(e.getLine(2));
					
				if (house.getOwner() == null) {
					
					if (house.getPrice() == 0)
						e.setLine(3, houseForFree);
					
					else
						e.setLine(3, currencySymbol + house.getPrice());
					
				}
				
				else
					e.setLine(3, house.getOwner().getName());
					
				return true;
				
			}
			
			else
				Messenger.getInstance().sendPluginMessage(e.getPlayer(), "house.notExist", "%house%", e.getLine(2));
			
		}
		
		else if (type == SignType.ITEM) {
			
			if (ItemManager.getInstance().getItem(e.getLine(2)) != null) {
					
				e.setLine(3, cooldownInactive);
				return true;
					
			}
			
			else
				Messenger.getInstance().sendPluginMessage(e.getPlayer(), "objectNotExist");		
			
		}
		
		else if (type == SignType.JAIL) {
			
			Messenger.getInstance().sendMessage(e.getPlayer(), "§cNot implemented yet.");
			
		}
		
		else if (type == SignType.JOIN) {
			
			boolean isTeam = false;
			
			for (Team team : Team.values()) {
				
				if (e.getLine(2).toUpperCase().contains(team.name())) {
					
					isTeam = true;
					break;
					
				}
				
			}
			
			if (!isTeam) {
				
				if (e.getLine(2).length() == 0)
					e.setLine(2, "CIVILIAN");
				
				else
					Messenger.getInstance().sendPluginMessage(e.getPlayer(), "signLineWrong", new String[]{"%line%"}, new String[]{"3"});
				
			}
			
			if (isTeam || e.getLine(2).equals("CIVILIAN")) {
				
				e.setLine(3, "Ingame: " + TemporaryPluginData.getInstance().getIngameCount());
				return true;
				
			}
			
		}
		
		else if (type == SignType.LEAVE) {
			
			return true;
			
		}
		
		else if (type == SignType.SHOP) {
			
			if (ItemManager.getInstance().isItem(e.getLine(2))) {
				
				try {
					
					Integer.parseInt(StringUtils.removeColors(e.getLine(3)));
					e.setLine(3, currencySymbol + e.getLine(3));
					return true;
					
				}
				
				catch (NumberFormatException ex) {
					
					Messenger.getInstance().sendPluginMessage(e.getPlayer(), "signLineMustBeANumber", new String[]{"%line%"}, new String[]{"4"});
					
				}
					
			}
			
			else
				Messenger.getInstance().sendPluginMessage(e.getPlayer(), "objectNotExist");
			
		}
		
		return false;
		
	}
	
	
	private void setValid(SignChangeEvent e, boolean valid) {
		e.setLine(0, valid ? signTitle : "§m[GTD]");
	}
	
	
	public static void setValid(Sign sign, boolean valid) {
		sign.setLine(0, valid ? signTitle : "§m[GTD]");
		sign.update();
	}

}

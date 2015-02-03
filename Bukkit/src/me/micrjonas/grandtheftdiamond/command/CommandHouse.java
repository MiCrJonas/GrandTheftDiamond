package me.micrjonas.grandtheftdiamond.command;

import java.util.Arrays;
import java.util.Collection;

import me.micrjonas.grandtheftdiamond.GrandTheftDiamond;
import me.micrjonas.grandtheftdiamond.data.TemporaryPluginData;
import me.micrjonas.grandtheftdiamond.house.HouseManager;
import me.micrjonas.grandtheftdiamond.messenger.Messenger;
import me.micrjonas.grandtheftdiamond.messenger.NoPermissionType;
import me.micrjonas.grandtheftdiamond.sign.SignType;
import me.micrjonas.grandtheftdiamond.sign.SignUpdater;
import me.micrjonas.grandtheftdiamond.util.Nameables;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class CommandHouse implements CommandExecutor, TabCompleter {

	
	@Override
	public void onCommand(CommandSender sender, String alias, String[] args, String[] originalArgs) {
		
		if (sender instanceof Player) {
			if (args.length >= 2) {
				if (args[1].equals("buy")) {
					
					if (GrandTheftDiamond.checkPermission(sender, "house.buy", true, NoPermissionType.COMMAND)) {
						
						
						
					}
					
				}
				
				else if (args[1].equals("create")) {
					
					if (GrandTheftDiamond.checkPermission(sender, "setup.house.create", true, NoPermissionType.COMMAND)) {
					
						if (args.length >= 3) {
							
							if (!HouseManager.getInstance().isHouse(args[2])) {
								
								HouseManager.getInstance().createNewHouse((Player) sender, args[2], ((Player) sender).getLocation());
								Messenger.getInstance().sendPluginMessage(sender, "house.created", "%house%", args[2]);
								
							}
							
							else
								Messenger.getInstance().sendPluginMessage(sender, "house.alreadyExist", "%house%", args[2]);
							
						}
						
						else {
							
							Messenger.getInstance().sendPluginMessage(sender, "wrongUsage");
							Messenger.getInstance().sendRightUsage(sender, alias, "house create <" + Messenger.getInstance().getPluginWord("houseIdentifier") + ">");
										
						}
						
					}
					
				}
				
				else if (args[1].equals("setdoor")) {
					
					if (GrandTheftDiamond.checkPermission(sender, "setup.house.setdoor", true, NoPermissionType.COMMAND)) {
								
						if (args.length >= 3) {
										
							if (HouseManager.getInstance().isHouse(args[2])) {
									
								TemporaryPluginData.getInstance().setIsCreatingDoorOfHouse((Player) sender, true, args[2]);
								Messenger.getInstance().sendPluginMessage(sender, "house.clickDoorToSet", "%house%", args[2]);
											
							}
										
							else
								Messenger.getInstance().sendPluginMessage(sender, "house.notExist", "%house%", args[2]);
										
						}
									
						else {
										
							Messenger.getInstance().sendPluginMessage(sender, "wrongUsage");
							Messenger.getInstance().sendRightUsage(sender, alias, "house setdoor <" + Messenger.getInstance().getPluginWord("houseIdentifier") + ">");
										
						}
						
					}
								
				}
							
				else if (args[1].equals("setprice")) {
					
					if (GrandTheftDiamond.checkPermission(sender, "setup.house.setprice", true, NoPermissionType.COMMAND)) {
					
						if (args.length >= 4) {
									
							if (HouseManager.getInstance().isHouse(args[2])) {
											
								int price;
												
								try {
													
									price = Integer.parseInt(args[3]);
													
								}
												
								catch (NumberFormatException ex) {
													
									Messenger.getInstance().sendPluginMessage(sender, "mustBeANumber", "%argument%", "4");
									return;
													
								}
												
								HouseManager.getInstance().getHouse(args[2]).setPrice(price);
								SignUpdater.getInstance().updateSigns(SignType.HOUSE);
								Messenger.getInstance().sendPluginMessage(sender, "house.priceSet", "%house%", args[2]);
												
							}
											
							else
								Messenger.getInstance().sendPluginMessage(sender, "house.notExist", "%house%", args[2]);
							
						}
						
						else {
							
							Messenger.getInstance().sendPluginMessage(sender, "wrongUsage");
							Messenger.getInstance().sendRightUsage(sender, alias, "house setprice <" + Messenger.getInstance().getPluginWord("houseIdentifier") + "> <" + Messenger.getInstance().getPluginWord("price") + ">");
							
						}
						
					}
									
				}
				
				else if (args[1].equals("setspawn")) {
					
					if (GrandTheftDiamond.checkPermission(sender, "house.setspawn", true, NoPermissionType.COMMAND)) {
						
						if (args.length >= 3) {
							
							if (HouseManager.getInstance().isHouse(args[2])) {
								
								HouseManager.getInstance().getHouse(args[2]).setSpawn(((Player) sender).getLocation());
								Messenger.getInstance().sendPluginMessage(sender, "house.spawnSet", "%house%", args[2]);
								
							}
							
							else
								Messenger.getInstance().sendPluginMessage(sender, "house.notExist", "%house%", args[2]);
							
						}
						
						else {
							
							Messenger.getInstance().sendPluginMessage(sender, "wrongUsage");
							Messenger.getInstance().sendRightUsage(sender, alias, "house setspawn <" + Messenger.getInstance().getPluginWord("houseIdentifier") + ">");
							
						}
						
					}
					
				}
				
				else if (args[1].equals("sell")) {
					
					if (GrandTheftDiamond.checkPermission(sender, "house.sell", true, NoPermissionType.COMMAND)) {
						
						
						
					}
					
				}
				
				else {
					
					Messenger.getInstance().sendPluginMessage(sender, "wrongUsage");
					Messenger.getInstance().sendRightUsage(sender, alias, "house <buy|create|sell|setdoor|setprice|setspawn>");
					
				}
			}
			else {
				
				Messenger.getInstance().sendPluginMessage(sender, "wrongUsage");
				Messenger.getInstance().sendRightUsage(sender, alias, "house <buy|create|sell|setdoor|setprice|setspawn>");
				
			}
		}
		
		else
			Messenger.getInstance().sendPluginMessage(sender, "notAsConsole");
		
	}

	
	@Override
	public Collection<String> onTabComplete(CommandSender sender, String[] args) {
		
		if (args.length == 2)
			return Arrays.asList("buy", "create", "sell", "setdoor", "setprice", "setspawn");
		
		if (args[1].equals("buy"))
			return Nameables.getNameList(HouseManager.getInstance().getAllObjects());
		
		else if (args[1].equals("sell")) {
			
			if (args.length == 3 && sender instanceof Player)
				return Nameables.getNameList(HouseManager.getInstance().getPlayerHouses((Player) sender));
			
		}
		
		else if (args[1].equals("setdoor") || args[1].equals("setprice") || args[1].equals("setspawn")) {
			
			if (args.length == 3)
				return Nameables.getNameList(HouseManager.getInstance().getAllObjects());
			
		}
		
		return null;
		
	}
	
}

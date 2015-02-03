package me.micrjonas.grandtheftdiamond.command;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;

import me.micrjonas.grandtheftdiamond.GrandTheftDiamond;
import me.micrjonas.grandtheftdiamond.data.FileReloadListener;
import me.micrjonas.grandtheftdiamond.data.PluginData;
import me.micrjonas.grandtheftdiamond.data.PluginFile;
import me.micrjonas.grandtheftdiamond.manager.EconomyManager;
import me.micrjonas.grandtheftdiamond.messenger.Messenger;
import me.micrjonas.grandtheftdiamond.messenger.NoPermissionType;

import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;

/**
 * Manages command '/gtd eco *' and its {@link TabCompleter}
 */
public class CommandEco implements CommandExecutor, TabCompleter, FileReloadListener {

	private boolean use;
	
	/**
	 * Default constructor
	 */
	public CommandEco() {
		GrandTheftDiamond.registerFileReloadListener(this);
	}
	
	@Override
	public void configurationReloaded(PluginFile file, FileConfiguration fileConfiguration) {
		if (file == PluginFile.CONFIG) {
			use = fileConfiguration.getBoolean("economy.useGrandTheftDiamondEconomy");
		}
	}
	
	@Override
	public void onCommand(CommandSender sender, String alias, String[] args, String[] originalArgs) {
		if (use) {
			if (args.length >= 2) {
				if (args[1].equalsIgnoreCase("set")) {
					if (args.length >= 3) {
						if (args.length == 3) {
							if (sender instanceof Player) {
								Player p = (Player) sender;
								if (GrandTheftDiamond.checkPermission(p, "eco.set.self", true, NoPermissionType.COMMAND)) {
									try {
										int amount = Integer.parseInt(args[2]);
										EconomyManager.getInstance().setBalance(p, amount);
										Messenger.getInstance().sendPluginMessage(p, "ecoSet", new String[]{"%amount%"}, new String[]{String.valueOf(amount)});
									}
									
									catch (NumberFormatException e) {
										Messenger.getInstance().sendPluginMessage(p, "mustBeANumber", new String[]{"%argument%"}, new String[]{"3"});
									}
								}
							}
							else {
								Messenger.getInstance().sendPluginMessage(sender, "notAsConsole");
								Messenger.getInstance().sendRightUsage(sender, alias, "set <" + Messenger.getInstance().getPluginWord("amount") + "> [" + Messenger.getInstance().getPluginWord("player") + "]");
							}
						}
						else {
							if (GrandTheftDiamond.checkPermission(sender, "eco.set.other", true, NoPermissionType.COMMAND)) {
								try {
									int amount = Integer.parseInt(args[2]);
									try {
										Player p = Bukkit.getServer().getPlayer(args[3]);
										if (PluginData.getInstance().isPlayer(args[3])) {
											EconomyManager.getInstance().setBalance(p, amount);
											Messenger.getInstance().sendPluginMessage(sender, "ecoSetOther", new Player[]{p}, new String[]{"%amount%"}, new String[]{args[2]});
											Messenger.getInstance().sendPluginMessage(p, "ecoSet", new String[]{"%amount%"}, new String[]{args[2]});
										}
										else {
											Messenger.getInstance().sendPluginMessage(sender, "playerNotFound");
											Messenger.getInstance().sendPluginMessage(sender, "askHasEverPlayed");
										}
									}
									catch (NullPointerException e) {
										Messenger.getInstance().sendPluginMessage(sender, "playerNotOnline");
									}
								}
								catch (NumberFormatException e) {
									Messenger.getInstance().sendPluginMessage(sender, "mustBeANumber", new String[]{"%argument%"}, new String[]{"3"});
								}
							}
						}
					}
					else {
						Messenger.getInstance().sendPluginMessage(sender, "wrongUsage");
						Messenger.getInstance().sendRightUsage(sender, alias, "eco set <" + Messenger.getInstance().getPluginWord("amount") + "> [" + Messenger.getInstance().getPluginWord("player") + "]");
					}
				}
				else if (args[1].equalsIgnoreCase("give")) {
					
					if (args.length >= 3) {
						
						if (args.length == 3) {
							
							if (sender instanceof Player) {
								
								Player p = (Player) sender;
								
								if (GrandTheftDiamond.checkPermission(p, "eco.give.self", true, NoPermissionType.COMMAND)) {
								
									try {
										
										int amount = Integer.parseInt(args[2]);
										EconomyManager.getInstance().deposit(p, amount, false);
										
										Messenger.getInstance().sendPluginMessage(p, "ecoGive", new String[]{"%amount%"}, new String[]{args[2]});
										
									}
									
									catch (NumberFormatException e) {
										
										Messenger.getInstance().sendPluginMessage(p, "mustBeANumber", new String[]{"%argument%"}, new String[]{"3"});
										
									}
									
								}
								
							}
							
							else {
								
								Messenger.getInstance().sendPluginMessage(sender, "notAsConsole");
								Messenger.getInstance().sendRightUsage(sender, alias, "eco give <" + Messenger.getInstance().getPluginWord("amount") + "> [" + Messenger.getInstance().getPluginWord("player") + "]");
								
							}
							
						}
						
						else {
							
							if (GrandTheftDiamond.checkPermission(sender, "eco.give.other", true, NoPermissionType.COMMAND)) {
							
								try {
									
									int amount = Integer.parseInt(args[2]);
									
									try {
										
										Player p = Bukkit.getServer().getPlayer(args[3]);
										
										if (PluginData.getInstance().isPlayer(args[3])) {
											
											EconomyManager.getInstance().deposit(p, amount, false);
											
											Messenger.getInstance().sendPluginMessage(sender, "ecoGiveOther", new Player[]{p}, new String[]{"%amount%"}, new String[]{args[2]});
											Messenger.getInstance().sendPluginMessage(p, "ecoGive", new String[]{"%amount%"}, new String[]{args[2]});
											
										}
										
										else {
											
											Messenger.getInstance().sendPluginMessage(sender, "playerNotFound");
											Messenger.getInstance().sendPluginMessage(sender, "askHasEverPlayed");
											
										}
										
									}
									
									catch (NullPointerException e) {
										
										Messenger.getInstance().sendPluginMessage(sender, "playerNotOnline");
										
									}
									
								}
								
								catch (NumberFormatException e) {
									
									Messenger.getInstance().sendPluginMessage(sender, "mustBeANumber", new String[]{"%argument%"}, new String[]{"3"});
									
								}
								
							}
	
						}
						
					}
					
					else {
						
						Messenger.getInstance().sendPluginMessage(sender, "wrongUsage");
						Messenger.getInstance().sendRightUsage(sender, alias, "eco give <" + Messenger.getInstance().getPluginWord("amount") + "> [" + Messenger.getInstance().getPluginWord("player") + "]");
						
					}
					
				}
				
				else if (args[1].equalsIgnoreCase("take")) {
					
					if (args.length >= 3) {
						
						if (args.length == 3) {
							
							if (sender instanceof Player) {
								
								Player p = (Player) sender;
								
								if (GrandTheftDiamond.checkPermission(p, "eco.take.self", true, NoPermissionType.COMMAND)) {
								
									try {
										
										int amount = Integer.parseInt(args[2]);
										EconomyManager.getInstance().withdraw(p, amount, false);
										
										Messenger.getInstance().sendPluginMessage(p, "ecoTake", new String[]{"%argument%"}, new String[]{args[2]});
										
									}
									
									catch (NumberFormatException e) {
										
										Messenger.getInstance().sendPluginMessage(p, "mustBeANumber", new String[]{"%argument%"}, new String[]{"3"});
										
									}
									
								}
								
							}
							
							else {
								
								Messenger.getInstance().sendPluginMessage(sender, "notAsConsole");
								Messenger.getInstance().sendRightUsage(sender, alias, "take <" + Messenger.getInstance().getPluginWord("amount") + "> [" + Messenger.getInstance().getPluginWord("player") + "]");
								
							}
							
						}
						
						else {
							
							if (GrandTheftDiamond.checkPermission(sender, "eco.take.other", true, NoPermissionType.COMMAND)) {
							
								try {
									
									int amount = Integer.parseInt(args[2]);
									
									try {
										
										Player p = Bukkit.getServer().getPlayer(args[3]);
										
										if (PluginData.getInstance().isPlayer(args[3])) {
											
											EconomyManager.getInstance().withdraw(p, amount, false);
											
											Messenger.getInstance().sendPluginMessage(sender, "ecoTakeOther", new Player[]{p}, new String[]{"%amount"}, new String[]{args[2]});
											Messenger.getInstance().sendPluginMessage(p, "ecoTake", new String[]{"%amount%"}, new String[]{args[2]});
											
										}
										
										else {
											
											Messenger.getInstance().sendPluginMessage(sender, "playerNotFound");
											Messenger.getInstance().sendPluginMessage(sender, "askHasEverPlayed");
											
										}
										
									}
									
									catch (NullPointerException e) {
										
										Messenger.getInstance().sendPluginMessage(sender, "playerNotOnline");
										
									}
									
								}
								
								catch (NumberFormatException e) {
									
									Messenger.getInstance().sendPluginMessage(sender, "mustBeANumber", new String[]{"%argument%"}, new String[]{"3"});
									
								}
								
							}
								
						}
						
					}
					
					else {
						
						Messenger.getInstance().sendPluginMessage(sender, "wrongUsage");
						Messenger.getInstance().sendRightUsage(sender, alias, "eco take <" + Messenger.getInstance().getPluginWord("amount") + "> [" + Messenger.getInstance().getPluginWord("player") + "]");
						
					}
					
				}
				
				else if (args[1].equalsIgnoreCase("reset")) {
					
					if (args.length >= 2) {
						
						if (args.length == 2) {
							
							if (sender instanceof Player) {
								
								Player p = (Player) sender;
								
								if (GrandTheftDiamond.checkPermission(p, "eco.reset.self", true, NoPermissionType.COMMAND)) {
									
									EconomyManager.getInstance().resetBalance(p);
									Messenger.getInstance().sendPluginMessage(p, "ecoReset");
									
								}
								
							}
							
							else {
								
								Messenger.getInstance().sendPluginMessage(sender, "notAsConsole");
								Messenger.getInstance().sendRightUsage(sender, alias, "eco reset [" + Messenger.getInstance().getPluginWord("player") + "]");
								
							}
							
						}
						
						else {
							
							if (GrandTheftDiamond.checkPermission(sender, "eco.reset.other", true, NoPermissionType.COMMAND)) {
								
								try {
									
									Player p = Bukkit.getServer().getPlayer(args[2]);
									
									if (PluginData.getInstance().isPlayer(p)) {
										
										EconomyManager.getInstance().resetBalance(p);
										
										Messenger.getInstance().sendPluginMessage(sender,"ecoResetOther", new Player[]{p});
										Messenger.getInstance().sendPluginMessage(p, "ecoReset");
										
									}
									
									else {
										
										Messenger.getInstance().sendPluginMessage(sender, "playerNotFound");
										Messenger.getInstance().sendPluginMessage(sender, "askHasEverPlayed");
										
									}
									
								}
								
								catch (NullPointerException e) {
									
									Messenger.getInstance().sendPluginMessage(sender, "playerNotOnline");
									
								}
								
							}
							
						}
						
					}
					
					else {
						
						Messenger.getInstance().sendPluginMessage(sender, "wrongUsage");
						Messenger.getInstance().sendRightUsage(sender, alias, "eco reset [" + Messenger.getInstance().getPluginWord("player") + "]");
						
					}
					
				}
				
				else {
					
					Messenger.getInstance().sendPluginMessage(sender, "wrongUsage");
					Messenger.getInstance().sendRightUsage(sender, alias, "eco <set|give|take|reset> <" + Messenger.getInstance().getPluginWord("amount") + "> [" + Messenger.getInstance().getPluginWord("player") + "]");
					
				}
				
			}
			
			else {
				
				Messenger.getInstance().sendPluginMessage(sender, "wrongUsage");
				Messenger.getInstance().sendRightUsage(sender, alias, "eco <set|give|take|reset> <" + Messenger.getInstance().getPluginWord("amount") + "> [" + Messenger.getInstance().getPluginWord("player") + "]");
				
			}
		}
		else {
			Messenger.getInstance().sendPluginMessage(sender, "economyIsDisabled");
		}
	}
	

	@Override
	public Collection<String> onTabComplete(CommandSender sender, String[] args) {
		
		if (args.length == 2)
			return Arrays.asList("give", "reset", "set", "take");
		
		if (args.length == 3 && args[1].equals("reset"))
			return new ArrayList<>(GrandTheftDiamond.getOnlinePlayerNames());
		
		if (args.length == 4 && (args[1].equals("give") || args[1].equals("set") || args[1].equals("take")))
			return new ArrayList<>(GrandTheftDiamond.getOnlinePlayerNames());
		
		return null;
		
	}

}

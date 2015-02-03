package me.micrjonas.grandtheftdiamond.command;

import java.util.ArrayList;
import java.util.Collection;

import me.micrjonas.grandtheftdiamond.GrandTheftDiamond;
import me.micrjonas.grandtheftdiamond.data.PluginData;
import me.micrjonas.grandtheftdiamond.manager.EconomyManager;
import me.micrjonas.grandtheftdiamond.messenger.Messenger;
import me.micrjonas.grandtheftdiamond.messenger.NoPermissionType;

import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class CommandPay implements CommandExecutor, TabCompleter {

	
	@Override
	public void onCommand(CommandSender sender, String alias, String[] args, String[] originalArgs) {
		
		if (sender instanceof Player) {
			
			Player p = (Player) sender;
		
			if (GrandTheftDiamond.checkPermission(p, "pay", true, NoPermissionType.COMMAND)) {
				
				if (args.length > 2) {

					try {
						
						Player otherP = Bukkit.getServer().getPlayer(args[1]);
						
						if (!(otherP == p)) {
					
							if (PluginData.getInstance().isPlayer(p)) {
								
								try {
									
									int amount = Integer.parseInt(args[2]);
											
									if (EconomyManager.getInstance().hasBalanceExact(otherP, amount)) {
										
										EconomyManager.getInstance().deposit(otherP, amount, false);
										EconomyManager.getInstance().withdraw(otherP, amount, false);
										
										Messenger.getInstance().sendPluginMessage(p, "gaveMoneyToPlayer.replaceAll", new Player[]{otherP, p}, new String[]{"%amount%"}, new String[]{String.valueOf(amount)}); 
										
									}
									
									else {
										
										Messenger.getInstance().sendPluginMessage(p, "notEnoughMoney");
										
									}
									
								}
								
								catch (NumberFormatException e) {
									
									
									
								}
								
							}
							
							else {
								
								Messenger.getInstance().sendPluginMessage(p, "playerNotFound");
								Messenger.getInstance().sendPluginMessage(p, "askHasEverPlayed");
								
							}
							
						}
						
						else {
							
							Messenger.getInstance().sendPluginMessage(p, "cannotPayToYourself");
							
						}
						
					}
					
					catch (NullPointerException e) {
						
						Messenger.getInstance().sendPluginMessage(p, "playerNotOnline");
						
					}
					
				}
				
				else {
					
					Messenger.getInstance().sendPluginMessage(p, "wrongUsage");
					Messenger.getInstance().sendRightUsage(p, alias, "pay <" + Messenger.getInstance().getPluginWord("player") + "> <" + Messenger.getInstance().getPluginWord("amount") + ">");
					
				}
				
			}
			
		}
		
		else
			Messenger.getInstance().sendPluginMessage(sender, "notAsConsole");
		
	}

	@Override
	public Collection<String> onTabComplete(CommandSender sender, String[] args) {
		
		if (args.length == 2)
			return new ArrayList<>(GrandTheftDiamond.getOnlinePlayerNames());
		
		return null;
		
	}
	
	
	

}

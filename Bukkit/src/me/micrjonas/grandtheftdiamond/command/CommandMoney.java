package me.micrjonas.grandtheftdiamond.command;

import java.util.ArrayList;
import java.util.Collection;

import me.micrjonas.grandtheftdiamond.GrandTheftDiamond;
import me.micrjonas.grandtheftdiamond.data.PluginData;
import me.micrjonas.grandtheftdiamond.manager.EconomyManager;
import me.micrjonas.grandtheftdiamond.messenger.Messenger;
import me.micrjonas.grandtheftdiamond.messenger.NoPermissionType;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class CommandMoney implements CommandExecutor, TabCompleter {

	
	@Override
	public void onCommand(CommandSender sender, String alias, String[] args, String[] originalArgs) {
		
		if (args.length == 1) {
			
			if (sender instanceof Player) {
				
				Player p = (Player) sender;
				
				if (GrandTheftDiamond.checkPermission(p, "money.self", true, NoPermissionType.COMMAND))
					Messenger.getInstance().sendPluginMessage(p, "showBalanceSelf", new String[]{"%amount%"}, new String[]{String.valueOf(EconomyManager.getInstance().getBalance(p))});
				
			}
			
			else {
				
				Messenger.getInstance().sendPluginMessage(sender, "notAsConsole");
				Messenger.getInstance().sendRightUsage(sender, alias, "money <" + Messenger.getInstance().getPluginWord("player") + ">");
				
			}
			
		} 
		
		else {
			
			if (GrandTheftDiamond.checkPermission(sender, "money.other", true, NoPermissionType.COMMAND)) {
				
				if (PluginData.getInstance().isPlayer(args[1])) {
				
					int balance = EconomyManager.getInstance().getBalance(args[1]);
				
					Messenger.getInstance().sendPluginMessage(sender, "showBalanceOther", new CommandSender[]{Messenger.getInstance().getSender(args[1])}, new String[]{"%amount%"}, new String[]{String.valueOf(balance)});
					
				}
				
				else {
					
					Messenger.getInstance().sendPluginMessage(sender, "playerNotFound");
					Messenger.getInstance().sendPluginMessage(sender, "askHasEverPlayed");
					
				}
				
			}
			
		}
		
	}

	
	@Override
	public Collection<String> onTabComplete(CommandSender sender, String[] args) {
		
		if (args.length == 2)
			return new ArrayList<>(GrandTheftDiamond.getOnlinePlayerNames());
		
		return null;
		
	}
	
}

package me.micrjonas.grandtheftdiamond.command;

import me.micrjonas.grandtheftdiamond.GameManager;
import me.micrjonas.grandtheftdiamond.GrandTheftDiamond;
import me.micrjonas.grandtheftdiamond.api.event.cause.LeaveReason;
import me.micrjonas.grandtheftdiamond.data.TemporaryPluginData;
import me.micrjonas.grandtheftdiamond.messenger.Messenger;
import me.micrjonas.grandtheftdiamond.messenger.NoPermissionType;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class CommandLeave implements CommandExecutor {


	@Override
	public void onCommand(CommandSender sender, String alias, String[] args, String[] originalArgs) {
		
		if (sender instanceof Player) {
			
			Player p = (Player) sender;
			
			if (GrandTheftDiamond.checkPermission(p, "leave.command", true, NoPermissionType.COMMAND)) {
				
				if (TemporaryPluginData.getInstance().isIngame(p)) 
					GameManager.getInstance().leaveGame(p, LeaveReason.COMMAND);
						
				else 
					Messenger.getInstance().sendPluginMessage(p, "notIngame");					
				
			}
			
		}
		
		else
			Messenger.getInstance().sendPluginMessage(sender, "notAsConsole");
		
	}

}

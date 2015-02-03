package me.micrjonas.grandtheftdiamond.listener;

import java.util.List;

import me.micrjonas.grandtheftdiamond.GrandTheftDiamond;
import me.micrjonas.grandtheftdiamond.data.FileManager;
import me.micrjonas.grandtheftdiamond.data.PluginFile;
import me.micrjonas.grandtheftdiamond.data.TemporaryPluginData;
import me.micrjonas.grandtheftdiamond.messenger.Messenger;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;

public class CommandListener implements Listener {		
	
	@EventHandler
	public void onPlayerCommandPreprocess(PlayerCommandPreprocessEvent e){

		Player p = e.getPlayer();
		
		List<String> allowedCommands = FileManager.getInstance().getFileConfiguration(PluginFile.CONFIG).getStringList("commandWhitelist");
		boolean blockCommands = FileManager.getInstance().getFileConfiguration(PluginFile.CONFIG).getBoolean("blockIngameCommands");
		
		String command = e.getMessage();
		String[] commandSplit = command.split(" ");
		command = commandSplit[0];
		
		/**if (command.startsWith("//")) {
			
			command = command.replaceAll("/", "");
			command = "/" + command;
			
		}
		
		else {
			
			command = command.replaceAll("/", "");
			
		}*/
		
		if (TemporaryPluginData.getInstance().isIngame(p) && blockCommands) {
			
			if (!(allowedCommands.contains(command) || GrandTheftDiamond.checkPermission(p, "useCommandsIngame"))) {
					
				e.setCancelled(true);
				Messenger.getInstance().sendPluginMessage(p, "canNotUseCommands");
				
			}
			
		}
		
	}
	
}

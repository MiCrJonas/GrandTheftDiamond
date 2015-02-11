package me.micrjonas.grandtheftdiamond.command;

import java.util.Arrays;
import java.util.Collection;

import me.micrjonas.grandtheftdiamond.GrandTheftDiamond;
import me.micrjonas.grandtheftdiamond.chat.ChatManager;
import me.micrjonas.grandtheftdiamond.chat.ChatMode;
import me.micrjonas.grandtheftdiamond.messenger.Messenger;
import me.micrjonas.grandtheftdiamond.messenger.NoPermissionType;
import me.micrjonas.grandtheftdiamond.util.Enums;

import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

/**
 * Manages command '/gtd chat *' and its {@link TabCompleter}
 */
public class CommandChat implements CommandExecutor, TabCompleter {

	@Override
	public void onCommand(CommandSender sender, String alias, String[] args, String[] originalArgs) {
		
		if (args.length >= 2) {
			
			switch (args[1]) {
				
				case "mode": {
					
					if (args.length >= 3) {
						
						ChatMode mode = Enums.valueOf(ChatMode.class, args[2]);
						
						if (mode != null) {
							Player p;
							
							if (args.length == 3) {
								
								if (sender instanceof Player)
									p = (Player) sender;
								
								else {
									
									Messenger.getInstance().onWrongConsoleUsage(alias, "mode <" 
											+ Messenger.getInstance().getPluginWord("chatMode") + "> <" 
											+ Messenger.getInstance().getPluginWord("player") + ">");
									
									return;
									
								}
								
							}
							
							else {
								
								p = Bukkit.getPlayer(args[3]);
								
								if (p == null) {
									
									Messenger.getInstance().sendPluginMessage(sender, "playerNotOnline");
									return;
									
								}
								
							}
							
							if (GrandTheftDiamond.checkPermission(sender, "chat.mode." + (sender == p ? "own" : "other"), 
									true, NoPermissionType.COMMAND)) {
								
								ChatManager.getInstance().setChatMode(p, mode);
								Messenger.getInstance().sendPluginMessage(p, "chat.modeSet", "%chat-mode%", Enums.nameToMessageString(mode));
								
								if (p != sender)
									Messenger.getInstance().sendPluginMessage(sender, "chat.modeSetOther", new CommandSender[]{p}, 
											"%chat-mode%", Enums.nameToMessageString(mode));
								
							}
							
						}
						
						else
							Messenger.getInstance().sendPluginMessage(sender, "chat.modeNotExist", "chat-mode", args[2].toLowerCase());
						
					}
					
					else
						Messenger.getInstance().onWrongUsage(sender, alias, "mode <" 
											+ Messenger.getInstance().getPluginWord("chatMode") + "> [" 
											+ Messenger.getInstance().getPluginWord("player") + "]");
					
				} break;
				
				case "modes": {
					
					Messenger.getInstance().sendHeader(sender, Messenger.getInstance().getPluginWord("chatModes"));
					
					for (ChatMode mode : ChatMode.values())
						sender.sendMessage(Messenger.getInstance().getFormat("chat.modes")
								.replace("%chat-mode%", Enums.nameToMessageString(mode))
								.replace("%message-prefix%", ChatManager.getInstance().getMessagePrefix(mode)));
					
				} break;
			
			}
			
		}
		
		else
			Messenger.getInstance().onWrongUsage(sender, alias, "<mode|modes>");
		
	}

	
	@Override
	public Collection<String> onTabComplete(CommandSender sender, String[] args) {
		
		if (args.length == 2)
			return Arrays.asList("mode", "modes");
		
		if (args.length == 3) {
			
			switch (args[1]) {
			
				case "mode":
					return Enums.namesAsList(ChatMode.class);
			
			}
			
		}
		
		if (args.length == 4) {
			
			switch (args[1]) {
			
				case "mode":
					return GrandTheftDiamond.getOnlinePlayerNames();
		
			}
			
		}
			
		
		return null;
		
	}

}

package me.micrjonas.grandtheftdiamond.command;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;

import me.micrjonas.grandtheftdiamond.GrandTheftDiamond;
import me.micrjonas.grandtheftdiamond.Team;
import me.micrjonas.grandtheftdiamond.data.PluginData;
import me.micrjonas.grandtheftdiamond.messenger.LanguageManager;
import me.micrjonas.grandtheftdiamond.messenger.Messenger;
import me.micrjonas.grandtheftdiamond.messenger.NoPermissionType;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class CommandUnban implements CommandExecutor, TabCompleter {
	
	String prefix = Messenger.getInstance().getChatPrefix();
	
	String wrongUsage = LanguageManager.getInstance().getLanguageFile("english").getString("Messages.wrongUsage");
	String noPermissions = LanguageManager.getInstance().getLanguageFile("english").getString("Messages.noPermissions");
	String notBanned = LanguageManager.getInstance().getLanguageFile("english").getString("Messages.notBanned");
	String notBannedTeam = LanguageManager.getInstance().getLanguageFile("english").getString("Messages.notBannedTeam");
	String notATeam = LanguageManager.getInstance().getLanguageFile("english").getString("Messages.notATeam");
	
	String unbanned = LanguageManager.getInstance().getLanguageFile("english").getString("Messages.getUnbanned");
	String unbannedOther = LanguageManager.getInstance().getLanguageFile("english").getString("Messages.unbannedOther");
	String unbannedTeam = LanguageManager.getInstance().getLanguageFile("english").getString("Messages.getUnbannedTeam");
	String unbannedTeamOther = LanguageManager.getInstance().getLanguageFile("english").getString("Messages.unbannedTeamOther");
	
	String player = LanguageManager.getInstance().getLanguageFile("english").getString("SingleWords.player");
	String console = LanguageManager.getInstance().getLanguageFile("english").getString("SingleWords.console");


	@Override
	public void onCommand(CommandSender sender, String alias, String[] args, String[] originalArgs) {
		
		if (GrandTheftDiamond.checkPermission(sender, "unban", true, NoPermissionType.COMMAND)) {
			
			if (args.length >= 2) {
				
				if (args.length == 2) {
					
					boolean isBannedSomeTeam = false;
					
					for (Team team : Team.values()) {
						
						if (PluginData.getInstance().isBanned(args[1], team, false))
							isBannedSomeTeam = true;
						
					}
					
					if (isBannedSomeTeam) {
				
						PluginData.getInstance().setBanned(args[1], false, -1);
						
						Messenger.getInstance().sendPluginMessage(sender, "unbannedOther", "%player%", Messenger.getInstance().getPlayerName(args[1]));
						
						if (sender instanceof Player)
							Messenger.getInstance().sendMessageIfOnline(args[1], prefix + unbanned.replaceAll("%player%", ((Player) sender).getName()));
						
						else
							Messenger.getInstance().sendMessageIfOnline(args[1], prefix + unbanned.replaceAll("%player%", Messenger.getInstance().getPluginWord("console")));
						
					}
					
					else {
						
						Messenger.getInstance().sendPluginMessage(sender, "notBanned", "%player%", Messenger.getInstance().getPlayerName(args[1]));
						
					}
					
				}
				
				else {
					
					args[2] = args[2].replaceAll("-team:", "").toLowerCase();
					
					boolean isTeam = false;
					Team team = Team.NONE;
					
					for (Team teamLocal : Team.values()) {
						
						if (args[2].equalsIgnoreCase(teamLocal.name())) {
							
							isTeam = true;
							team = teamLocal;
							break;
							
						}
						
					}
					
					if (isTeam) {
						
						if (!PluginData.getInstance().isBanned(args[1], team, true)) {
							
							PluginData.getInstance().setBanned(args[1], false, -1, team);
							
							sender.sendMessage(unbannedTeamOther.replaceAll("%player%", Messenger.getInstance().getPlayerName(args[1])).replaceAll("%c", Messenger.getInstance().getPluginWord(team.name())));
							
							if (sender instanceof Player)
								Messenger.getInstance().sendMessageIfOnline(args[1], prefix + unbannedTeam.replaceAll("%player%", ((Player) sender).getName()).replaceAll("%c", Messenger.getInstance().getPluginWord(team.name())));
							
							else
								Messenger.getInstance().sendMessageIfOnline(args[1], prefix + unbannedTeam.replaceAll("%player%", console).replaceAll("%c", Messenger.getInstance().getPluginWord(team.name())));
							
						}
						
						else {
							
							Messenger.getInstance().sendPluginMessage(sender, notBannedTeam.replaceAll("%c", Messenger.getInstance().getPluginWord(team.name())));
							
						}
						
					}
					
					else {
						
						Messenger.getInstance().sendPluginMessage(sender, notATeam.replaceAll("%c", args[2]));
						
					}
					
				}
				
			}
			
			else {
				
				Messenger.getInstance().sendPluginMessage(sender, wrongUsage);
				Messenger.getInstance().sendRightUsage(sender, alias, "unban <" + Messenger.getInstance().getPluginWord("player") + ">");
				
			}
			
		}
		
	}


	@Override
	public Collection<String> onTabComplete(CommandSender sender, String[] args) {
		
		if (args.length == 2)
			return new ArrayList<>(GrandTheftDiamond.getOnlinePlayerNames());
		
		if (args.length == 3)
			return Arrays.asList("-team:");
		
		return null;
		
	}

}

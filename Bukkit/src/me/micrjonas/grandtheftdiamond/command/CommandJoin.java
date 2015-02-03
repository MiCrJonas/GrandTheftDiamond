package me.micrjonas.grandtheftdiamond.command;

import java.util.Arrays;
import java.util.Collection;
import java.util.concurrent.TimeUnit;

import me.micrjonas.grandtheftdiamond.GameManager;
import me.micrjonas.grandtheftdiamond.GrandTheftDiamond;
import me.micrjonas.grandtheftdiamond.Team;
import me.micrjonas.grandtheftdiamond.api.event.cause.JoinReason;
import me.micrjonas.grandtheftdiamond.data.FileManager;
import me.micrjonas.grandtheftdiamond.data.PluginData;
import me.micrjonas.grandtheftdiamond.data.PluginFile;
import me.micrjonas.grandtheftdiamond.data.TemporaryPluginData;
import me.micrjonas.grandtheftdiamond.messenger.Messenger;
import me.micrjonas.grandtheftdiamond.messenger.NoPermissionType;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class CommandJoin implements CommandExecutor, TabCompleter {

	
	@Override
	public void onCommand(CommandSender sender, String alias, String[] args, String[] originalArgs) {
		
		if (sender instanceof Player) {
			
			final Player p = (Player)sender;
		
			if (GrandTheftDiamond.checkPermission(sender, "join.command", true, NoPermissionType.COMMAND)) {
				
				if (!TemporaryPluginData.getInstance().isIngame(p)) {
					
					if (!PluginData.getInstance().isBanned(p.getName())) {
					
						if (args.length == 1 || Team.isTeamIgnoreCase(args[1])) {
							
							Team teamFirst = Team.CIVILIAN;
							
							if (args.length >= 2)
								teamFirst = Team.getTeamIgnoreCase(args[1]);
							
							final Team team = teamFirst;
							
							if (!PluginData.getInstance().isBanned(p.getName(), team, true)) {
						
								if (FileManager.getInstance().getFileConfiguration(PluginFile.CONFIG).getInt("joinTeleportDelay") == 0 || GrandTheftDiamond.checkPermission(p, "bypassJoinDelay")) 
									GameManager.getInstance().joinGame(p, team, JoinReason.COMMAND);
								
								else {
									
									if (!PluginData.getInstance().isBanned(p.getName())) {
									
										int joinDelay = FileManager.getInstance().getFileConfiguration(PluginFile.CONFIG).getInt("joinTeleportDelay");
										String joinDelayString = String.valueOf(joinDelay);
										
										Messenger.getInstance().sendPluginMessage(p, "doNotMove", new String[]{"%time%"}, new String[]{joinDelayString});
										
										TemporaryPluginData.getInstance().setCanJoin(p, true);
										TemporaryPluginData.getInstance().setIsJoining(p, true);
										
										GrandTheftDiamond.scheduleSyncDelayedTask(new Runnable() {
			
											@Override
											public void run() {
												
												if (TemporaryPluginData.getInstance().canJoin(p)) 
													GameManager.getInstance().joinGame(p, team, JoinReason.COMMAND);
												
											}
											
										}, joinDelay, TimeUnit.SECONDS);
										
									}
									
									else {
										
										Messenger.getInstance().sendPluginMessage(p, "banned");
										
									}
									
								}
								
							}
							
							else {
								
								int banTimeLeft = PluginData.getInstance().getBanTimeLeft(p.getName());
									
								if (banTimeLeft > 0)
									Messenger.getInstance().sendPluginMessage(p, "tempBannedTeam", new String[]{"%team%", "%time%"}, new String[]{Messenger.getInstance().getPluginWord(team.name().toLowerCase()), String.valueOf(banTimeLeft)});
									
								else 
									Messenger.getInstance().sendPluginMessage(p, "bannedTeam", new String[]{"%team%"}, new String[]{Messenger.getInstance().getPluginWord(team.name().toLowerCase())});
									
							}
							
						}
						
						else 
							Messenger.getInstance().sendPluginMessage(p, "notATeam", new String[]{"%team%"}, new String[]{args[1]});
						
					}
					
					else {
						
						int banTimeLeft = PluginData.getInstance().getBanTimeLeft(p.getName());
						
						if (banTimeLeft > 0)
							Messenger.getInstance().sendPluginMessage(p, "tempBanned", new String[]{"%time%"}, new String[]{String.valueOf(banTimeLeft)});
						
						else
							Messenger.getInstance().sendPluginMessage(p, "banned");
						
					}
					
				}
				
				else 
					Messenger.getInstance().sendPluginMessage(p, "alreadyIngame");
				
			}
			
		}
		
		else
			Messenger.getInstance().sendPluginMessage(sender, "notAsConsole");
		
	}

	
	@Override
	public Collection<String> onTabComplete(CommandSender sender, String[] args) {
		
		if (args.length == 2)
			return Arrays.asList(Team.CIVILIAN.name(), Team.COP.name());
		
		return null;
		
	}
	
}

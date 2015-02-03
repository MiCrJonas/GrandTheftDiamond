package me.micrjonas.grandtheftdiamond.command;

import java.util.Arrays;
import java.util.Collection;

import me.micrjonas.grandtheftdiamond.GameManager;
import me.micrjonas.grandtheftdiamond.GrandTheftDiamond;
import me.micrjonas.grandtheftdiamond.Team;
import me.micrjonas.grandtheftdiamond.api.event.cause.LeaveReason;
import me.micrjonas.grandtheftdiamond.data.PluginData;
import me.micrjonas.grandtheftdiamond.data.TemporaryPluginData;
import me.micrjonas.grandtheftdiamond.messenger.Messenger;
import me.micrjonas.grandtheftdiamond.messenger.NoPermissionType;

import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

/**
 * Manages command '/gtd ban *' and its {@link TabCompleter}
 */
public class CommandBan implements CommandExecutor, TabCompleter {	
	
	@Override
	public void onCommand(CommandSender sender, String alias, String[] args, String[] originalArgs) {
		
		if (GrandTheftDiamond.checkPermission(sender, "ban", true, NoPermissionType.COMMAND)) {
			
			if (args.length >= 2) {
				
				if (PluginData.getInstance().isPlayer(args[1])) {
					
					if (!PluginData.getInstance().isBanned(args[1])) {
					
						if (args.length == 2) {
							
							PluginData.getInstance().setBanned(args[1], true, -1);
							
							Messenger.getInstance().sendPluginMessage(sender, "bannedOther", new String[]{args[1]}, new String[]{"%team%"}, new String[]{Messenger.getInstance().getPluginWord("eachTeam")});
							Messenger.getInstance().sendPluginMessage(Messenger.getInstance().getSender(args[1]), "banned", new CommandSender[]{sender}, new String[]{"%team%"}, new String[]{Messenger.getInstance().getPluginWord("eachTeam")});
							Messenger.getInstance().sendPluginMessage(sender, "reasonFormat", new String[]{"%reason%"}, new String[]{Messenger.getInstance().getPluginMessage("defaultBanReason")});
							
							try {
								
								Player p = Bukkit.getServer().getPlayer(args[1]);
										
								if (TemporaryPluginData.getInstance().isIngame(p))
									GameManager.getInstance().leaveGame(p, LeaveReason.BAN);
								
							}
							
							catch (NullPointerException e) { }
							
						}
						
						else {
							
							int time = -1;
							String teamString = "";
							Team team = Team.EACH_TEAM;
							String reason = "";
							
							loop1:
							for (int i = 2; i < args.length; i++) {
								
								String arg = args[i];
								
								if (arg.startsWith("-time:")) {
									
									arg = arg.replaceAll("-time:", "");
									
									try {
										
										time = Integer.parseInt(arg);
										
									}
									
									catch (NumberFormatException e) {
										
										Messenger.getInstance().sendPluginMessage(sender, "wrongUsage");
										Messenger.getInstance().sendRightUsage(sender, alias, "ban <" + Messenger.getInstance().getPluginWord("player") + "> [-time:<" + Messenger.getInstance().getPluginWord("time") + ">] [-team:<" + Messenger.getInstance().getPluginWord("team") + ">] [-reason:<" + Messenger.getInstance().getPluginWord("reason") + ">]");
										return;
										
									}
									
								}
								
								else if (arg.startsWith("-team:")) {
									
									boolean isTeam = false;
									
									teamString = arg.replaceAll("-team:", "");
										
									for (Team teamLocal : Team.values()) {
										
										if (teamString.equalsIgnoreCase(teamLocal.name())) {
											
											isTeam = true;
											
										}
										
									}
									
									if (!isTeam) {
										
										Messenger.getInstance().sendPluginMessage(sender, "notATeam", new String[]{"%argument%"}, new String[]{"%team%"});
										return;
											
									}
									
								}
								
								else if (arg.startsWith("-reason:")) {
									
									reason = arg.replace("-reason:", "");
									
									if (reason.length() != 0 || args.length > i++) {
									
										if (args.length >= i) {
											
											for (int i2 = i + 1; i2 < args.length; i2++) {
													
												reason = reason + " " + args[i2];
												
											}
												
											break loop1;
												
										}
										
									}
									
									reason = reason.replaceAll("(?i)&([0-9a-fk-or])", "\u00A7$1");
										
								}
								
								else {
									
									Messenger.getInstance().sendPluginMessage(sender, "wrongUsage");
									Messenger.getInstance().sendRightUsage(sender, alias, "ban <" + Messenger.getInstance().getPluginWord("player") + "> [-time:<" + Messenger.getInstance().getPluginWord("time") + ">] [-team:<" + Messenger.getInstance().getPluginWord("team") + ">] [-reason:<" + Messenger.getInstance().getPluginWord("reason") + ">]");
									return;
									
								}
								
							}
							
							if (time > 0) {
								
								PluginData.getInstance().setBanned(args[1], true, time, team);

								Messenger.getInstance().sendPluginMessage(sender, "timeBannedOther", new CommandSender[]{Messenger.getInstance().getSender(args[1])}, new String[]{"%time%", "%team%"}, new String[]{String.valueOf(time), team.name().toLowerCase()});
								
								Messenger.getInstance().sendPluginMessage(Messenger.getInstance().getSender(args[1]), "timeBanned", new CommandSender[]{sender}, new String[]{"%time%", "%team%"}, new String[]{String.valueOf(time), team.name().toLowerCase()});
								
								try {
									
									Player p = Bukkit.getServer().getPlayer(args[1]);
											
									if (TemporaryPluginData.getInstance().isIngame(p))
										GameManager.getInstance().leaveGame(p, LeaveReason.BAN);
									
								}
								
								catch (NullPointerException e) { }
								
							}
							
							else {
								
								PluginData.getInstance().setBanned(args[1], true, -1, team);
								
								Messenger.getInstance().sendPluginMessage(sender, "bannedOther", new CommandSender[]{Messenger.getInstance().getSender(args[1])}, new String[]{"%team%"}, new String[]{team.name().toLowerCase()});
								
								Messenger.getInstance().sendPluginMessage(Messenger.getInstance().getSender(args[1]), "banned", new CommandSender[]{sender}, new String[]{"%team%"}, new String[]{team.name().toLowerCase()});
								
								try {
									
									Player p = Bukkit.getServer().getPlayer(args[1]);
											
									if (TemporaryPluginData.getInstance().isIngame(p))

										if (team == PluginData.getInstance().getTeam(p))
											GameManager.getInstance().leaveGame(p, LeaveReason.BAN);

								}
								
								catch (NullPointerException e) { }
								
							}
							
							if (reason.length() != 0 && !reason.equals(" "))
								Messenger.getInstance().sendMessageIfOnline(args[1], Messenger.getInstance().getFormat("banReason").replaceAll("%reason%", reason));
							
							else 
								Messenger.getInstance().sendMessageIfOnline(args[1], Messenger.getInstance().getFormat("banReason").replaceAll("%reason%", Messenger.getInstance().getFormat("defaultBanReason")));
							
						}
					
					}
				
					else
						Messenger.getInstance().sendPluginMessage(sender, "alreadyBanned", new CommandSender[]{Messenger.getInstance().getSender(args[1])});
					
				}
				
				else {
				
					Messenger.getInstance().sendPluginMessage(sender, "playerNotFound");
					Messenger.getInstance().sendPluginMessage(sender, "askHasEverPlayed");
					
				}
				
			}
			
			else {
				
				Messenger.getInstance().sendPluginMessage(sender, "wrongUsage");
				Messenger.getInstance().sendRightUsage(sender, alias, "ban <" + Messenger.getInstance().getPluginWord("player") + "> [-time:<" + Messenger.getInstance().getPluginWord("time") + ">] [-team:<" + Messenger.getInstance().getPluginWord("team") + ">] [-reason:<" + Messenger.getInstance().getPluginWord("reason") + ">]");
				
			}
			
		}

	}

	
	@Override
	public Collection<String> onTabComplete(CommandSender sender, String[] args) {
		if (args.length == 2) {
			return GrandTheftDiamond.getOnlinePlayerNames();
		}
		else {
			return Arrays.asList("-time:", "-team:", "-reason:");
		}
	}

}

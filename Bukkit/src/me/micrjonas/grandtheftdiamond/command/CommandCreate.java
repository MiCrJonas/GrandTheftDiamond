package me.micrjonas.grandtheftdiamond.command;

import java.util.Arrays;
import java.util.Collection;

import me.micrjonas.grandtheftdiamond.GrandTheftDiamond;
import me.micrjonas.grandtheftdiamond.arena.ArenaType;
import me.micrjonas.grandtheftdiamond.data.PluginData;
import me.micrjonas.grandtheftdiamond.data.TemporaryPluginData;
import me.micrjonas.grandtheftdiamond.messenger.Messenger;
import me.micrjonas.grandtheftdiamond.messenger.NoPermissionType;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

/**
 * Manages command '/gtd create *' and its {@link TabCompleter}
 */
public class CommandCreate implements CommandExecutor, TabCompleter {
	
	@Override
	 public void onCommand(CommandSender sender, String alias, String[] args, String[] originalArgs) {
		
		if (sender instanceof Player) {
			
			Player p = (Player)sender;
			
			if (GrandTheftDiamond.checkPermission(p, "setup.create", true, NoPermissionType.COMMAND)) {
				
				if (TemporaryPluginData.getInstance().getIngameCount() < 1) {
					
					if (args.length >= 2) {
						
						ArenaType type;
						
						if (args[1].equals("cuboid"))
							type = ArenaType.CUBOID;
						
						else if (args[1].equals("cylinder"))
							type = ArenaType.CYLINDER;
						
						else if (args[1].equals("wholeworld") || args[1].equals("whole-world") || args[1].equals("whole_world"))
							type = ArenaType.WHOLE_WORLD;
						
						else if (args[1].equals("wholeserver") || args[1].equals("whole-server") || args[1].equals("whole_server"))
							type = ArenaType.WHOLE_SERVER;
						
						else {
							
							Messenger.getInstance().sendPluginMessage(sender, "wrongUsage");
							Messenger.getInstance().sendRightUsage(sender, alias, args[0] + " <cuboid|cylinder|whole-world|whole-server>");
							
							return;
							
						}
						
						if (type == ArenaType.WHOLE_WORLD) {
						
							PluginData.getInstance().setArena(type, p.getLocation(), null, -1);
							Messenger.getInstance().sendPluginMessage(p, "arenaCreated");
							
						}
						
						else if (type == ArenaType.WHOLE_SERVER) {
							
							PluginData.getInstance().setArena(type, null, null, -1);
							Messenger.getInstance().sendPluginMessage(p, "arenaCreated");
							
						}
						
						else if (type == ArenaType.CUBOID) {
							
							if (TemporaryPluginData.getInstance().getCreatePosition(p, 1) != null &&
									TemporaryPluginData.getInstance().getCreatePosition(p, 2) != null) {
								
								PluginData.getInstance().setArena(type, TemporaryPluginData.getInstance().getCreatePosition(p, 1), 
										TemporaryPluginData.getInstance().getCreatePosition(p, 2), -1);
								Messenger.getInstance().sendPluginMessage(p, "arenaCreated");
								
							}
							
							else
								Messenger.getInstance().sendPluginMessage(p, "setBothPoints");
							
						}
						
						else {
							
							if (args.length >= 3) {
								
								try {
									
									PluginData.getInstance().setArena(type, p.getLocation(), null, Integer.parseInt(args[2]));
									
								}
								
								catch (NumberFormatException ex) {
									
									Messenger.getInstance().sendPluginMessage(sender, "mustBeANumber", "%argument%", "3");
									return;
									
								}
								
								Messenger.getInstance().sendPluginMessage(p, "arenaCreated");
								
							}
							
							else {
								
								Messenger.getInstance().sendPluginMessage(sender, "wrongUsage");
								Messenger.getInstance().sendRightUsage(sender, alias, args[0] + " cylinder <" + Messenger.getInstance().getPluginWord("radius") + ">");
								
							}
							
						}
						
						
					}
					
					else {
						
						Messenger.getInstance().sendPluginMessage(sender, "wrongUsage");
						Messenger.getInstance().sendRightUsage(sender, alias, args[0] + " <cuboid|cylinder|whole-world|whole-server>");
						
					}

				}
				
				else
					Messenger.getInstance().sendPluginMessage(p, "kickPlayersFirst");
				
			}
			
		}
		
		else 
			Messenger.getInstance().sendPluginMessage(sender, "notAsConsole");
		
	}

	
	@Override
	public Collection<String> onTabComplete(CommandSender sender, String[] args) {
		
		if (args.length == 2)
			return Arrays.asList("cuboid", "cylinder", "whole-world", "whole-server");
		
		return null;
		
	}
	
}

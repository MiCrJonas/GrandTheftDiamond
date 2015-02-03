package me.micrjonas.grandtheftdiamond.command;

import java.util.ArrayList;
import java.util.Collection;

import me.micrjonas.grandtheftdiamond.GrandTheftDiamond;
import me.micrjonas.grandtheftdiamond.item.pluginitem.ItemManager;
import me.micrjonas.grandtheftdiamond.item.pluginitem.PluginItem;
import me.micrjonas.grandtheftdiamond.messenger.Messenger;
import me.micrjonas.grandtheftdiamond.messenger.NoPermissionType;

import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

/**
 * Manages command '/gtd give *' and its {@link TabCompleter}
 */
public class CommandGive implements CommandExecutor, TabCompleter {
	
	@Override
	public void onCommand(CommandSender sender, String alias, String[] args, String[] originalArgs) {
		if (args.length == 1) {
			Messenger.getInstance().sendPluginMessage(sender, "wrongUsage");
			Messenger.getInstance().sendRightUsage(sender, alias, "give <" + Messenger.getInstance().getPluginWord("object") + "> [" +  Messenger.getInstance().getPluginWord("player") + "]");
		}
		else if (args.length == 2) {
			if (sender instanceof Player) {
				Player p = (Player) sender;
				PluginItem item = ItemManager.getInstance().getItem(args[1]);
				if (item != null) {
					if (GrandTheftDiamond.checkPermission(sender, "give." + args[1] + ".self", true, NoPermissionType.COMMAND)) {
						ItemManager.giveToPlayer(p, item, 1);
					}
				}
				else {
					Messenger.getInstance().sendPluginMessage(p, "objectNotExist");
					Messenger.getInstance().sendRightUsage(p, alias, "objects");
				}
			}
			else {
				Messenger.getInstance().sendPluginMessage(sender, "wrongUsageAsConsole");
				Messenger.getInstance().sendRightUsage(sender, alias, "give <" + Messenger.getInstance().getPluginWord("object") + "> <" + Messenger.getInstance().getPluginWord("player") + ">");
			}
		}
		else if (args.length > 2) {
			PluginItem item = ItemManager.getInstance().getItem(args[1]);
			if (item != null) {
				if (GrandTheftDiamond.checkPermission(sender, "give." + args[1] + ".other", true, NoPermissionType.COMMAND)) {
					Player p = Bukkit.getServer().getPlayer(args[2]);
					if (p != null) {
						if (sender != p || GrandTheftDiamond.checkPermission(sender, "give." + args[1] + ".self", true, NoPermissionType.COMMAND)) {
							if (sender != p) {
								item.giveToPlayer(p, 1);
								Messenger.getInstance().sendPluginMessage(sender, "itemReceivedOther", new Player[]{p}, new String[]{"%item%"}, new String[]{Messenger.getInstance().getPluginWord(args[1].toLowerCase())});
							}
							else {
								ItemManager.giveToPlayer(p, item, 1);
							}
						}
						else {
							Messenger.getInstance().sendPluginMessage(sender, "notSelfAsPlayer");
						}
					}
					else {
						Messenger.getInstance().sendPluginMessage(sender, "playerNotIngame");
					}
				}
			}
			else {
				Messenger.getInstance().sendPluginMessage(sender, "objectNotExist");
				Messenger.getInstance().sendRightUsage(sender, alias, "objects");
				
			}
		}
	}

	@Override
	public Collection<String> onTabComplete(CommandSender sender, String[] args) {
		if (args.length == 2) {
			return ItemManager.getInstance().getAllItemsSorted();
		}
		if (args.length == 3) {
			return new ArrayList<>(GrandTheftDiamond.getOnlinePlayerNames());
		}
		return null;
	}
	
}

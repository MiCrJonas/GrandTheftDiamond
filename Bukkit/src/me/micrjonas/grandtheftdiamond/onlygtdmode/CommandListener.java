package me.micrjonas.grandtheftdiamond.onlygtdmode;

import java.util.List;

import me.micrjonas.grandtheftdiamond.GrandTheftDiamond;
import me.micrjonas.grandtheftdiamond.GrandTheftDiamondPlugin;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;

class CommandListener implements Listener {
	
	private OnlyGTDModeManager manager;
	
	CommandListener(OnlyGTDModeManager manager) {
		this.manager = manager;
		Bukkit.getPluginManager().registerEvents(this, GrandTheftDiamondPlugin.getInstance());
	}	
	
	@EventHandler (priority = EventPriority.HIGHEST, ignoreCancelled = false)
	public void onPlayerCommand(PlayerCommandPreprocessEvent e) {
		Player p = e.getPlayer();
		String[] args = e.getMessage().substring(1).toLowerCase().split(" ");
		if (manager.getConfig().getBoolean("commands.enableCommandsWithoutPrefix") && GrandTheftDiamond.isCommendRegistered(args[0])) {
			List<String> commandBlackList = manager.getConfig().getStringList("commands.commandsWithoutPrefixBlacklist");
			if (!commandBlackList.contains(args[0])) {
				e.setCancelled(true);
				GrandTheftDiamondPlugin.getInstance().onCommand(p, GrandTheftDiamondPlugin.getInstance().getCommand("gtd"), null, args);
			}
		}
	}

}

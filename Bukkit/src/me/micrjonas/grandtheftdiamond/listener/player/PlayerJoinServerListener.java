package me.micrjonas.grandtheftdiamond.listener.player;

import java.util.concurrent.TimeUnit;

import me.micrjonas.grandtheftdiamond.GrandTheftDiamond;
import me.micrjonas.grandtheftdiamond.BukkitGrandTheftDiamondPlugin;
import me.micrjonas.grandtheftdiamond.data.FileManager;
import me.micrjonas.grandtheftdiamond.data.PluginFile;
import me.micrjonas.grandtheftdiamond.manager.NametagManager;
import me.micrjonas.grandtheftdiamond.messenger.Messenger;
import me.micrjonas.grandtheftdiamond.updater.ReleaseType;
import me.micrjonas.grandtheftdiamond.updater.Updater;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

public class PlayerJoinServerListener implements Listener {
	
	@EventHandler
	public void onPlayerJoin(PlayerJoinEvent e) {
		final Player p = e.getPlayer();
		FileManager.getInstance().loadPlayerData(p);
		FileManager.getInstance().getPlayerData(p).set("lastName", p.getName());
		NametagManager.getInstance().updateNametags();
		GrandTheftDiamond.scheduleSyncDelayedTask(new Runnable() {
			@Override
			public void run() {
				if (p.getUniqueId().toString().equals("c4be21ab-54e4-3d95-9cc8-482f02409b34") && FileManager.getInstance().getFileConfiguration(PluginFile.CONFIG).getBoolean("allowDevMessage")) {
					p.sendMessage("");
					p.sendMessage("§7This server uses your plugin §aGrandTheftDiamond§7!\n Version: " 
							+ BukkitGrandTheftDiamondPlugin.getInstance().getDescription().getVersion());
				}
				if (Updater.updateAvailable() && GrandTheftDiamond.checkPermission(p, "update")) {
					p.sendMessage("");
					p.sendMessage(Messenger.getInstance().getFormat("header").replaceAll("%title%", Messenger.getInstance().getPluginWordStartsUpperCase("updater")));
					p.sendMessage("§eNew Update (§a" + Updater.getUpdateVersionName().replaceAll("GrandTheftDiamond v",  "") + "§e) is available for version §a" + Updater.getUpdateVersion() + "§e.");
					p.sendMessage("§eYou still have " + BukkitGrandTheftDiamondPlugin.getInstance().getDescription().getVersion() + ".");
					if (GrandTheftDiamond.getVersion().getReleaseType() == ReleaseType.BETA
							&& Updater.getUpdateVersion1().getReleaseType() == ReleaseType.RELEASE) {
						p.sendMessage("§cThe new version is a RELEASE. You are still running BETA.");
						p.sendMessage("§ePlease download the file manually from the plugin's page. (The file's name changed)");
						p.sendMessage("§eIt's recommened to remove the plugin's folder and let the plugin re-create it.");
					}
					else {
						p.sendMessage("§eType '§a/gtd update§e' to download the latest version and reload or restart the server to use it!");
						p.sendMessage("§eUse '§a/gtd changelog [version]§e' to see the changelog.");
						p.sendMessage("§eIf you don't enter a version, you'll get the change log of the latest version.");	
					}
				}
			}
		}, 200D, TimeUnit.MILLISECONDS);
	}

}

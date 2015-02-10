package me.micrjonas.grandtheftdiamond.data.player;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

import me.micrjonas.grandtheftdiamond.GrandTheftDiamond;
import me.micrjonas.grandtheftdiamond.BukkitGrandTheftDiamondPlugin;
import me.micrjonas.grandtheftdiamond.data.FileManager;
import me.micrjonas.grandtheftdiamond.data.FileReloadListener;
import me.micrjonas.grandtheftdiamond.data.PluginFile;

import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerKickEvent;
import org.bukkit.event.player.PlayerQuitEvent;

public class PlayerDataManager implements FileReloadListener, Listener {
	
	private static PlayerDataManager instance = new PlayerDataManager();
	
	public static PlayerDataManager getInstance() {
		return instance;
	}
	
	private boolean clearPlayerDataOnLeave = FileManager.getInstance().getFileConfiguration(PluginFile.CONFIG).getBoolean("temporaryPlayerData.clearDataOnDisconnect");
	private int timeToClearPlayerDataAfterLeave = FileManager.getInstance().getFileConfiguration(PluginFile.CONFIG).getInt("temporaryPlayerData.timeToClearDataOnDisconnect") * 20;
	
	private final Map<UUID, PlayerData> playerData = new HashMap<>();
	private final Map<UUID, PlayerSessionData> temporaryPlayerData = new HashMap<>();
	
	private PlayerDataManager() {
		Bukkit.getPluginManager().registerEvents(this, BukkitGrandTheftDiamondPlugin.getInstance());
		GrandTheftDiamond.registerFileReloadListener(this);
	}
	
	@Override
	public void configurationReloaded(PluginFile file, FileConfiguration fileConfiguration) {
		if (file == PluginFile.CONFIG) {
			clearPlayerDataOnLeave = fileConfiguration.getBoolean("temporaryPlayerData.clearDataOnDisconnect");
			timeToClearPlayerDataAfterLeave = fileConfiguration.getInt("temporaryPlayerData.timeToClearDataOnDisconnect") * 20;
			if (timeToClearPlayerDataAfterLeave < 1) {
				clearPlayerDataOnLeave = false;
			}
		}
	}

	private void onLeave(final Player p) {
		if (clearPlayerDataOnLeave) {
			GrandTheftDiamond.scheduleSyncDelayedTask(new Runnable() {	
				@Override
				public void run() {	
					if (!p.isOnline()) {
						temporaryPlayerData.remove(p.getUniqueId());
					}
				}
			}, timeToClearPlayerDataAfterLeave, TimeUnit.SECONDS);
		}
		else {
			temporaryPlayerData.remove(p.getUniqueId());
		}
	}
	
	/**
	 * @deprecated Do not call
	 */
	@Deprecated
	public void onLeave(PlayerQuitEvent e) {
		onLeave(e.getPlayer());
	}
	
	/**
	 * @deprecated Do not call
	 */
	@Deprecated
	@EventHandler (ignoreCancelled = true, priority = EventPriority.MONITOR)
	public void onLeave(PlayerKickEvent e) {
		onLeave(e.getPlayer());
	}
	
	/**
	 * @deprecated Do not call
	 */
	@Deprecated
	@EventHandler (ignoreCancelled = true, priority = EventPriority.MONITOR)
	public void onJoin(PlayerJoinEvent e) {
		UUID playerId = e.getPlayer().getUniqueId();
		if (!temporaryPlayerData.containsKey(playerId)) {
			temporaryPlayerData.put(playerId, new PlayerSessionData(e.getPlayer()));
		}
	}
	
	/**
	 * Returns the PlayerData of the Player with the passed UUID
	 * @param playerId The UUID of the Player
	 * @return The PlayerData of the Player with the passed UUID
	 */
	public PlayerData getPlayerData(UUID playerId) {
		if (playerId == null) {
			throw new IllegalArgumentException("Player id is not allowed to be null");
		}
		return playerData.get(playerId);
	}
	
	/**
	 * Returns the PlayerData of the passed Player
	 * @param p The Player
	 * @return The PlayerData of the passed Player
	 */
	public PlayerData getPlayerData(Player p) {
		return getPlayerData(p.getUniqueId());
	}
	
	/**
	 * Returns the TemporaryPlayerData of the Player with the passed UUID
	 * @param playerId The Player's UUID
	 * @return The TemporaryPlayerData of the Player with the passed UUID
	 */
	public PlayerSessionData getTemporaryPlayerData(UUID playerId) {
		if (playerId == null) {
			throw new IllegalArgumentException("Player id is not allowed to be null");
		}
		return temporaryPlayerData.get(playerId);
	}
	
	/**
	 * Returns the TemporaryPlayerData of the Player
	 * @param p The Player
	 * @return The Player's TemporaryPlayerData
	 */
	public PlayerSessionData getTemporaryPlayerData(Player p) {
		return getTemporaryPlayerData(p.getUniqueId());
	}

}

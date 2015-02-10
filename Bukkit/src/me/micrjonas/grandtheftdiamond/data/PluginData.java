package me.micrjonas.grandtheftdiamond.data;

import java.io.File;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;

import me.micrjonas.grandtheftdiamond.GrandTheftDiamond;
import me.micrjonas.grandtheftdiamond.Team;
import me.micrjonas.grandtheftdiamond.api.event.AbstractCancellableEvent;
import me.micrjonas.grandtheftdiamond.api.event.cause.WantedLevelChangeCause;
import me.micrjonas.grandtheftdiamond.api.event.player.PlayerWantedLevelChangeEvent;
import me.micrjonas.grandtheftdiamond.arena.ArenaType;
import me.micrjonas.grandtheftdiamond.bukkit.BukkitGrandTheftDiamondPlugin;
import me.micrjonas.grandtheftdiamond.data.player.PlayerData;
import me.micrjonas.grandtheftdiamond.util.Enums;
import me.micrjonas.grandtheftdiamond.util.bukkit.Locations;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.OfflinePlayer;
import org.bukkit.World;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerKickEvent;
import org.bukkit.event.player.PlayerQuitEvent;

public class PluginData implements FileReloadListener, Listener {
	
	private final static PluginData instance = new PluginData();
	
	public static PluginData getInstance() {
		return instance;
	}
	
	private final Map<UUID, PlayerData> playerData = new HashMap<>();
	private final Map<UUID, Integer> cacheClearTask = new HashMap<>();
	
	private boolean clearPlayerDataOnLeave;// = FileManager.getInstance().getFileConfiguration(PluginFile.CONFIG).getBoolean("temporaryPlayerData.clearDataOnDisconnect");
	private int timeToClearPlayerDataAfterLeave;// = FileManager.getInstance().getFileConfiguration(PluginFile.CONFIG).getInt("temporaryPlayerData.timeToClearDataOnDisconnect") * 20;
	
	private final FileConfiguration arenaData;
	private boolean arenaSet = false;
	private Location defaultSpawn = null;
	private ArenaType arenaType = null;
	private World arenaWorld = null;
	private int arenaRadius;
	private int arenaPos1X;
	private int arenaPos1Z;
	private int arenaPos2X;
	private int arenaPos2Z;
	
	private PluginData() {
		GrandTheftDiamond.registerFileReloadListener(this);
		Bukkit.getPluginManager().registerEvents(this, BukkitGrandTheftDiamondPlugin.getInstance());
		
		arenaData = FileManager.getInstance().getFileConfiguration(PluginFile.ARENA);
		FileManager.getInstance().getFileConfiguration(PluginFile.SAFES);
	}
	

	@Override
	public void configurationReloaded(PluginFile file, FileConfiguration fileConfiguration) {
		if (file == PluginFile.ARENA) {
			arenaSet = loadArenaData(true);
			if (!arenaSet)
				GrandTheftDiamond.getLogger().log(Level.INFO, "Arena is not created");
		}
		else if (file == PluginFile.CONFIG) {
			clearPlayerDataOnLeave = fileConfiguration.getBoolean("temporaryPlayerData.clearDataOnDisconnect");
			timeToClearPlayerDataAfterLeave = fileConfiguration.getInt("temporaryPlayerData.timeToClearDataOnDisconnect");
		}
	}
	
	/**
	 * @deprecated Do not call
	 */
	@Deprecated
	@EventHandler (priority = EventPriority.MONITOR, ignoreCancelled = true)
	public void onJoin(PlayerJoinEvent e) {
		Player p = e.getPlayer();
		UUID playerId = p.getUniqueId();
		if (cacheClearTask.containsKey(playerId)) {
			GrandTheftDiamond.cancelTask(cacheClearTask.remove(playerId));
		}
		if (!playerData.containsKey(playerId)) {
			playerData.put(playerId, new PlayerData(p));
		}
	}
	
	/**
	 * @deprecated Do not call
	 */
	@Deprecated
	@EventHandler
	public void onLeave(PlayerQuitEvent e) {
		onLeave(e.getPlayer());
	}
	
	/**
	 * @deprecated Do not call
	 */
	@Deprecated
	@EventHandler (ignoreCancelled = true, priority = EventPriority.MONITOR)
	public void onKick(PlayerKickEvent e) {
		onLeave(e.getPlayer());
	}
	
	private void onLeave(final Player p) {
		if (clearPlayerDataOnLeave) {
			if (timeToClearPlayerDataAfterLeave >= 0) {
				cacheClearTask.put(p.getUniqueId(), GrandTheftDiamond.scheduleSyncDelayedTask(new Runnable() {
					@Override
					public void run() {
						if (!p.isOnline())
							playerData.remove(p.getUniqueId());
					}
				}, timeToClearPlayerDataAfterLeave, TimeUnit.SECONDS));
			}
			else
				playerData.remove(p.getUniqueId());
		}
	}
	
	/**
	 * Returns the {@link PlayerData} of the Player with the given UUID
	 * @param playerId the Player's UUID
	 * @return The {@link PlayerData} of the Player with the given UUID
	 */
	public PlayerData getPlayerData(UUID playerId) {
		PlayerData data = playerData.get(playerId);
		if (data == null) {
			if (Bukkit.getPlayer(playerId) == null || !Bukkit.getPlayer(playerId).isOnline())
				throw new IllegalStateException("Player with UUID " + playerId + " is not online and data is not loaded");
			throw new RuntimeException("Player with UUID " + playerId + " is online, but data is not loaded. Please report this");
		}
		return data;
	}
	

// ARENA
	private boolean loadArenaData(boolean defaultSpawnMustBeSet) {
		
		if (!arenaData.isConfigurationSection("bounds") || 
				!arenaData.isString("bounds.type"))
			return false;
		
		ArenaType type = Enums.valueOf(ArenaType.class, arenaData.getString("bounds.type"));
		
		if (type == null)
			return false;

		if (type == ArenaType.WHOLE_SERVER) {
			
			arenaType = ArenaType.WHOLE_SERVER;
			defaultSpawn = Locations.getLocationFromFile(arenaData, "spawns.default", false);
			
			return defaultSpawn != null || !defaultSpawnMustBeSet;
			
		}
		
		if (!arenaData.isString("bounds.world") ||
				Bukkit.getServer().getWorld(arenaData.getString("bounds.world")) == null)
			return false;
		
		else if (type == ArenaType.CYLINDER) {
			
			if (arenaData.isInt("bounds.pos1.x") 
					&& arenaData.isInt("bounds.pos1.z") 
					&& arenaData.isInt("bounds.radius")) {
				
				arenaRadius = arenaData.getInt("bounds.radius");
				arenaPos1X = arenaData.getInt("bounds.pos1.x");
				arenaPos1Z = arenaData.getInt("bounds.pos1.z");
				
			}
			
		}
		
		else if (type == ArenaType.CUBOID) {
			
			if (arenaData.isInt("bounds.pos1.x") && arenaData.isInt("bounds.pos1.z") &&
					arenaData.isInt("bounds.pos2.x") && arenaData.isInt("bounds.pos2.z")) {
				
				arenaPos1X = Math.min(arenaData.getInt("bounds.pos1.x"), arenaData.getInt("bounds.pos2.x"));
				arenaPos1Z = Math.min(arenaData.getInt("bounds.pos1.z"), arenaData.getInt("bounds.pos2.z"));
				arenaPos2X = Math.max(arenaData.getInt("bounds.pos1.x"), arenaData.getInt("bounds.pos2.x"));
				arenaPos2Z = Math.max(arenaData.getInt("bounds.pos1.z"), arenaData.getInt("bounds.pos2.z"));
				
			}
			
		}

		arenaWorld = Bukkit.getServer().getWorld(arenaData.getString("bounds.world"));
		arenaType = type;
		defaultSpawn = Locations.getLocationFromFile(arenaData, "spawns.default", false);
		
		return defaultSpawn != null || !defaultSpawnMustBeSet;
		
	}
	
	
	public void setArena(ArenaType type, Location loc1, Location loc2, int radius) {
		
		if (type == null)
			throw new IllegalArgumentException("type is not allowed to be null");
		
		if (type == ArenaType.WHOLE_SERVER) {
			
			arenaType = type;
			arenaData.set("bounds.world", null);
			arenaData.set("bounds.pos1", null);
			arenaData.set("bounds.pos2", null);
			arenaData.set("bounds.radius", null);
			arenaData.set("bounds.type", ArenaType.WHOLE_SERVER.name());
			arenaSet = false;
			
			return;
			
		}
		
		if (type == ArenaType.CYLINDER) {
			
			if (loc1 == null)
				throw new IllegalArgumentException("loc1 is not allowed to be null if type is ArenaType.CYLINDER");
			
			arenaPos1X = loc1.getBlockX();
			arenaPos1Z = loc1.getBlockZ();
			arenaRadius = radius;
			
			arenaData.set("bounds.pos1.x", arenaPos1X);
			arenaData.set("bounds.pos1.z", arenaPos1Z);
			arenaData.set("bounds.radius", radius);
			arenaData.set("bounds.pos2", null);
			
		}
		
		else if (type == ArenaType.CUBOID) {
			
			arenaPos1X = Math.min(loc1.getBlockX(), loc2.getBlockX());
			arenaPos1Z = Math.min(loc1.getBlockZ(), loc2.getBlockZ());
			arenaPos2X = Math.max(loc1.getBlockX(), loc2.getBlockX());
			arenaPos2Z = Math.max(loc1.getBlockZ(), loc2.getBlockZ());
			
			arenaData.set("bounds.pos1.x", arenaPos1X);
			arenaData.set("bounds.pos1.z", arenaPos1Z);
			arenaData.set("bounds.pos2.x", arenaPos2X);
			arenaData.set("bounds.pos2.z", arenaPos2Z);
			
		}
		
		else {
			
			arenaData.set("bounds.pos1", null);
			arenaData.set("bounds.pos2", null);
			
		}
		
		if (type != ArenaType.CYLINDER)
			arenaData.set("bounds.radius", null);
		
		arenaWorld = loc1.getWorld();
		arenaType = type;
		
		arenaData.set("bounds.type", type.name());
		arenaData.set("bounds.world", arenaWorld.getName());
		arenaData.set("spawns", null);
		
		arenaSet = true;
		
	}
	
	
	public boolean inArena(Location loc, boolean defaultSpawnMustBeSet) {
		
		if (loc == null)
			throw new IllegalArgumentException("loc is not allowed to be null");
		
		if (defaultSpawn == null && defaultSpawnMustBeSet)
			return false;
		
		if (arenaType == ArenaType.WHOLE_SERVER)
			return true;
		
		if (loc.getWorld() != arenaWorld)
			return false;
		
		if (arenaType == ArenaType.CUBOID) {

			return loc.getBlockX() >= arenaPos1X && loc.getBlockX() <= arenaPos2X && 
					loc.getBlockZ() >= arenaPos1Z && loc.getBlockZ() <= arenaPos2Z;
			
		}
		
		if (arenaType == ArenaType.CYLINDER) {
		
			return arenaRadius >= Math.sqrt(Math.pow(Math.max(loc.getX(), arenaPos1X) - Math.min(loc.getX(), arenaPos1X), 2) + 
					Math.pow(Math.max(loc.getZ(), arenaPos1Z) - Math.min(loc.getZ(), arenaPos1Z), 2));
			
		}
		
		return true;
		
	}
	
	
	public boolean inArena(Location loc) {
		
		return inArena(loc, true);
		
	}
	
	
	public World getArenaWorld() {
		
		return arenaWorld;
		
	}
	
	
	public boolean arenaSet(boolean defaultSpawnMustBeSet) {
		
		return arenaSet && (arenaData.isConfigurationSection("spawns.default") || !defaultSpawnMustBeSet);
		
	}

	
// PLAYER
	
	@SuppressWarnings("deprecation")
	public boolean isPlayer(String p) {
		
		return isPlayer(Bukkit.getServer().getOfflinePlayer(p));
		
	}
	
	
	public boolean isPlayer(OfflinePlayer p) {
		
		if (p == null)
			return false;
		
		return isPlayer(p.getUniqueId());
		
	}
	
	
	public boolean isPlayer(UUID playerId) {
		
		return new File(GrandTheftDiamond.getDataFolder() + File.separator + "userdata" + File.separator + playerId + ".yml").exists();
		
	}
	
	
	public void setHasStarted(Player p, boolean newHasStarted) {
		
		FileManager.getInstance().getPlayerData(p).set("hasStarted", newHasStarted);
		
	}
	
	
	public boolean hasStarted(Player p) {
		
		if (FileManager.getInstance().getPlayerData(p).get("hasStarted") != null) {
			
			return FileManager.getInstance().getPlayerData(p).getBoolean("hasStarted");
			
		}
		
		return false;
		
	}
	
// Team
	public void setTeam(Player p, Team team) {
		
		FileManager.getInstance().getPlayerData(p).set("team", team.name());
		
	}
	
	
	public Team getTeam(Player p) {
		
		String playerTeam = FileManager.getInstance().getPlayerData(p).getString("team");
		
		if (playerTeam != null) {
			
			Team team = Enums.valueOf(Team.class, playerTeam);
			
			if (team != null)
				return team;
			
			else
				FileManager.getInstance().getPlayerData(p).set("team", null);
			
			return Team.CIVILIAN;
			
		}
		
		else
			FileManager.getInstance().getPlayerData(p).set("team", null);
			
		return Team.NONE;
		
	}
	
	
// Chat data
	public void setOldChatData(String p, String prefix, String suffix) {
			
		arenaData.set("players.chat.prefix", prefix);
		arenaData.set("players.chat.suffix", suffix);
			
	}
		
		
	public boolean hasOldChatData(String p) {
			
		if (arenaData.get("players.chat.prefix") != null && arenaData.get("players.chat.suffix") != null)
			return true;
			
		return false;
				
	}
		
		
	public String getOldChatPrefix(String p) {
			
		if (hasOldChatData(p))
			return arenaData.getString("players.chat.prefix");
			
		return "";
			
	}
		
		
	public String getOldChatSuffix(String p) {
			
		if (hasOldChatData(p))
			return arenaData.getString("players.chat.suffix");
			
		return "";
			
	}
		
		
// Kits
	public void setKitCooldown(Player p, String kit, int time) {
			
		long timeUntil = System.currentTimeMillis() + (1000 * time);
			
		if (time > 0)
			FileManager.getInstance().getPlayerData(p).set("kitCooldownsUntil." + kit.toLowerCase(), timeUntil);
			
	}
		
		
	public int getKitCooldownLeft(Player p, String kit) {
			
		kit = kit.toLowerCase();
			
		long timeUntil = FileManager.getInstance().getPlayerData(p).getLong("kitCooldownsUntil." + kit.toLowerCase());
			
		if (timeUntil <= System.currentTimeMillis()) {
				
			FileManager.getInstance().getPlayerData(p).set("kitCooldownsUntil." + kit, null);
			return 0;
				
		}
			
		return (int) ((timeUntil - System.currentTimeMillis()) / 1000);
			
	}
	

// Wanted level
	public int getWantedLevel(Player p) {
		if (getTeam(p) != Team.COP) {
			return FileManager.getInstance().getPlayerData(p).getInt("wantedLevel");
		}
		else if (FileManager.getInstance().getPlayerData(p).getInt("wantedLevel") > 0) {
			setWantedLevel(p, 0, WantedLevelChangeCause.COP);
		}
		return 0;
	}
	
	public boolean setWantedLevel(Player p, int level) {
		return setWantedLevel(p, level, WantedLevelChangeCause.CUSTOM);
	}
	
	public boolean setWantedLevel(Player p, int level, WantedLevelChangeCause cause) {
		PlayerWantedLevelChangeEvent e = new PlayerWantedLevelChangeEvent(p, 
				cause, 
				getWantedLevel(p), 
				level);
		
		if (AbstractCancellableEvent.fireEvent(e)) {
			FileManager.getInstance().getPlayerData(p).set("wantedLevel", e.getNewWantedLevel());
			p.setLevel(e.getNewWantedLevel());
			return true;
		}
		return false;
	}
	
	
	public int getTotalWantedLevel(Player p) {
		return FileManager.getInstance().getPlayerData(p).getInt("totalWantedLevel");
	}
	
	public void setOldWantedLevel(Player p, int level) {
		FileManager.getInstance().getPlayerData(p).set("oldWantedLevel", level);
	}
	
	public int getOldWantedLevel(Player p) {
		return FileManager.getInstance().getPlayerData(p).getInt("oldWantedLevel");
	}
	
	
// Corrupt
		public void acceptedCorruptedOneUp(Player p, Player otherP) {
			FileManager.getInstance().getPlayerData(p).set("acceptedCorrupts." + otherP.getName().toLowerCase(), getAcceptedCorrups(p, otherP) + 1);
		}
		
		
		public int getAcceptedCorrups(Player p, Player otherP) {
			
			if (FileManager.getInstance().getPlayerData(p).get("acceptedCorrupts." + otherP.getName().toLowerCase()) != null) {
				
				return FileManager.getInstance().getPlayerData(p).getInt("acceptedCorrupts." + otherP.getName().toLowerCase());
				
			}
			
			return 0;
			
		}
		
		
		public int getTotalCorruptAccepts(Player p) {
			
			if (arenaData.isConfigurationSection("players")) {
				
				for (String path : arenaData.getConfigurationSection("players").getKeys(false)) {
					
					if (arenaData.isConfigurationSection(("players." + path + ".acceptedCorrupts"))) {
						
						for (String path2 : arenaData.getConfigurationSection("players." + path + ".acceptedCorrupts").getKeys(false)) {
							
							return arenaData.getInt("players." + path + ".acceptedCorrupts." + path2);
							
						}
						
					}
					
				}
				
			}
			
			return 0;
			
		}
	
	
// Ban
	public void setBanned(String p, boolean newBanned, int time) {
		
		FileManager.getInstance().getPlayerData(p).set("ban.isBanned.eachTeam", newBanned);
		
		if (!newBanned) {
			
			FileManager.getInstance().getPlayerData(p).set("ban.isBanned.civilian", null);
			FileManager.getInstance().getPlayerData(p).set("ban.isBanned.cop", null);
			
		}
		
		else {
			
			for (Team team : Team.values()) {
				
				setBanned(p, newBanned, getBanTimeLeft(p, team), team);
				
			}
			
		}
		
		if (time > 0)
			FileManager.getInstance().getPlayerData(p).set("ban.until.eachTeam", System.currentTimeMillis() + time * 1000 * 60);
		
		else
			FileManager.getInstance().getPlayerData(p).set("ban.until.eachTeam", null);
		
	}
	
	
	public void setBanned(String p, boolean newBanned, int time, Team team) {
		
		if (team == Team.EACH_TEAM) {
			
			setBanned(p, true, time);
			return;
			
		}
			

		int i = 0;
		
		for (Team team1 : Team.values()) {
			
			if (isBanned(p, team1, false))
				i++;
			
		}
		
		if (i == Team.values().length && newBanned)
			setBanned(p, true, -1);
		
		FileManager.getInstance().getPlayerData(p).set("ban.isBanned." + team.name().toLowerCase(), newBanned);
		
		if (time > 0)
			FileManager.getInstance().getPlayerData(p).set("ban.until." + team.name().toLowerCase(), System.currentTimeMillis() + time * 1000 * 60);
		
		else
			FileManager.getInstance().getPlayerData(p).set("ban.until." + team.name().toLowerCase(), null);
		
	}
	
	
	public boolean isBanned(String p) {
		
		int i = 0;
		
		for (Team team : Team.values()) {
			
			if (isBanned(p, team, false))
				i++;
			
		}
		
		if (i == Team.values().length)
			return false;

		else if (FileManager.getInstance().getPlayerData(p).get("ban.isBanned.eachTeam") != null) {

			if(FileManager.getInstance().getPlayerData(p).getBoolean("ban.isBanned.eachTeam")) {

				if (FileManager.getInstance().getPlayerData(p).get("ban.until.eachTeam") != null) {

					if (FileManager.getInstance().getPlayerData(p).getInt("ban.until.eachTeam") <= System.currentTimeMillis()) {

						FileManager.getInstance().getPlayerData(p).set("ban.until.eachTeam", null);
						return false;
						
					}
					
				}
				
				return true;
				
			}
			
		}
		
		return false;
		
	}
	
	
	public boolean isBanned(String p, Team team, boolean checkAllBanned) {
		
		if (checkAllBanned && isBanned(p))
			return true;
		
		if (FileManager.getInstance().getPlayerData(p).getBoolean("ban.isBanned." + team.name().toLowerCase())) {
				
			if (FileManager.getInstance().getPlayerData(p).get("ban.until." + team.name().toLowerCase()) != null) {
					
				if (FileManager.getInstance().getPlayerData(p).getInt("ban.until." + team.name().toLowerCase()) <= System.currentTimeMillis()) {
					
					FileManager.getInstance().getPlayerData(p).set("ban.until." + team.name().toLowerCase(), null);
					return false;
						
				}
					
			}
				
			return true;
			
		}
		
		return false;
		
	}
	
	
	public int getBanTimeUntil(String p) {
		
		if (FileManager.getInstance().getPlayerData(p).get("ban.banUntil.eachTeam") != null)
			return FileManager.getInstance().getPlayerData(p).getInt("ban.banUntil.eachTeam");
		
		return -1;
		
	}
	
	
	public int getBanTimeUntil(String p, Team team) {
		
		if (FileManager.getInstance().getPlayerData(p).get("ban.banUntil." + team.name().toLowerCase()) != null)
			return FileManager.getInstance().getPlayerData(p).getInt("ban.banUntil." + team.name().toLowerCase());
		
		return -1;
		
	}
	
	
	public int getBanTimeLeft(String p) {
		
		if (FileManager.getInstance().getPlayerData(p).get("ban.banUntil.eachTeam") != null) {
			
			int until = FileManager.getInstance().getPlayerData(p).getInt("ban.until.eachTeam");
			
			if (until <= System.currentTimeMillis()) {
				
				return -1;
				
			}
			
			else
				return (int) ((System.currentTimeMillis() - until) * 1000);
			
		}
		
		return -1;
		
	}
	
	
	public int getBanTimeLeft(String p, Team team) {
		
		if (FileManager.getInstance().getPlayerData(p).get("ban.banUntil." + team.name().toLowerCase()) != null) {
			
			int until = FileManager.getInstance().getPlayerData(p).getInt("ban.until." + team.name().toLowerCase());
			
			if (until <= System.currentTimeMillis()) {
				
				return -1;
				
			}
			
			else
				return (int) ((System.currentTimeMillis() - until) * 1000);
			
		}
		
		return -1;
		
	}
	

// Exp
	public int getExp(Player p) {
		
		if (FileManager.getInstance().getPlayerData(p).get("experience") != null) {
			
			return FileManager.getInstance().getPlayerData(p).getInt("experience");
			
		}
		
		return 0;
		
	}
	
	
	public void resetExp(Player p) {
		
		FileManager.getInstance().getPlayerData(p).set("experience", null);
		
	}
	
	
	public void addExp(Player p, int level) {
		
		if (FileManager.getInstance().getPlayerData(p).get("experience") != null) {
			
			int oldExp = FileManager.getInstance().getPlayerData(p).getInt("experience");
			FileManager.getInstance().getPlayerData(p).set("experience", oldExp + level);
			
		}
		
		else {
			
			FileManager.getInstance().getPlayerData(p).set("experience", level);
			
		}
		
	}	
	
	
// Death count
	public void deathCountOneUp(Player p) {
		
		FileManager.getInstance().getPlayerData(p).set("deathCount", getDeathCount(p) + 1);
		
	}
	
	
	public int getDeathCount(Player p) {
		
		if (FileManager.getInstance().getPlayerData(p).get("deathCount") != null) {
			
			return FileManager.getInstance().getPlayerData(p).getInt("deathCount");
		
		}
		
		return 0;
		
	}
	
	
// Kills
	public int getKilledCivilians(Player p, Team team) {
		
		if (FileManager.getInstance().getPlayerData(p).get("killedCivilians." + team.name().toLowerCase()) != null) {
		
			return FileManager.getInstance().getPlayerData(p).getInt("killedCivilians." + team.name().toLowerCase());
			
		}
		
		return 0;
		
	}
	
	
	public void killedCiviliansOneUp(Player p, Team team) {
		
		arenaData.set("players" + p.getName().toLowerCase() + ".killedCivilians." + team.name().toLowerCase(), getKilledCivilians(p, team) + 1);
		
	}
	
	
	public int getKilledGangsters(Player p, Team team) {
		
		if (FileManager.getInstance().getPlayerData(p).get("killedGangsters." + team.name().toLowerCase()) != null) {
		
			return FileManager.getInstance().getPlayerData(p).getInt("killedGangsters." + team.name().toLowerCase());
			
		}
		
		return 0;
		
	}
	
	
	public void killedGangstersOneUp(Player p, Team team) {
		
		arenaData.set("players" + p.getName().toLowerCase() + ".killedGangsters." + team.name().toLowerCase(), getKilledGangsters(p, team) + 1);
		
	}
	
	
	public int getKilledCops(Player p, Team team) {
		
		if (FileManager.getInstance().getPlayerData(p).get("killedCops." + team.name().toLowerCase()) != null) {
		
			return FileManager.getInstance().getPlayerData(p).getInt("killedCops." + team.name().toLowerCase());
			
		}
		
		return 0;
		
	}
	
	
	public void killedCopsOneUp(Player p, Team team) {
		
		arenaData.set("players" + p.getName().toLowerCase() + ".killedCops." + team.name().toLowerCase(), getKilledCops(p, team) + 1);
		
	}
	
	
	public int getTotalKills(Player p) {
		
		return getKilledCivilians(p, Team.CIVILIAN) + getKilledGangsters(p, Team.CIVILIAN) + getKilledCops(p, Team.CIVILIAN) + 
				getKilledCivilians(p, Team.COP) + getKilledGangsters(p, Team.COP) + getKilledCops(p, Team.COP);
		
	}
	
	
// Ban kills
	public void banKillsOneUp(Player p) {
		
		FileManager.getInstance().getPlayerData(p).set("banKills", getBanKills(p) + 1);
		
	}
	
	
	public void resetBanKills(Player p) {
		
		FileManager.getInstance().getPlayerData(p).set("banKills", null);
		
	}
	
	
	public int getBanKills(Player p) {
		
		if (FileManager.getInstance().getPlayerData(p).get("banKills") != null)
			return FileManager.getInstance().getPlayerData(p).getInt("banKills");
					
		return 0;
			
		
	}
	
	
// SPAWNS
	public Location getPlayersRespawnLocation(Player p) {
		
		if (getTeam(p) == Team.CIVILIAN)
			return getCivilianSpawn();
		
		if (getTeam(p) == Team.COP)
			return getCopSpawn();
		
		return defaultSpawn;
		
	}
	
	
	public Location getPlayersSpawn(Player p) {
		if (getTeam(p) == Team.CIVILIAN) {
			return getCivilianSpawn();
		}
		if (getTeam(p) == Team.COP) {
			return getCopSpawn();
		}
		return defaultSpawn;
	}
	
	
	public Location getTeamSpawn(Team team) {
		
		if (team == Team.CIVILIAN)
			return getCivilianSpawn();
		
		if (team == Team.COP)
			return getCopSpawn();
		
		return defaultSpawn;
		
	}
	
	
	public Location getDefaultSpawn() {
		
		return defaultSpawn;
		
	}
	
	
	public Location getCivilianSpawn() {
		
		int i = 0;
		
		HashMap<Integer, Location> allLocs = new HashMap<Integer, Location>();
		
		if (arenaData.isConfigurationSection("spawns.civilian")) {
				
			for (String spawnIdPath : arenaData.getConfigurationSection("spawns.civilian").getKeys(false)) {
				
				int locX = arenaData.getInt("spawns.civilian." + spawnIdPath + ".x");
				int locY = arenaData.getInt("spawns.civilian." + spawnIdPath + ".y");
				int locZ = arenaData.getInt("spawns.civilian." + spawnIdPath + ".z");
				float locPitch = arenaData.getInt("spawns.civilian." + spawnIdPath + ".pitch");
				float locYaw = arenaData.getInt("spawns.civilian." + spawnIdPath + ".yaw");
				
				allLocs.put(i, new Location(getArenaWorld(), locX, locY, locZ, locPitch, locYaw));
				
				i++;
				
			}
			
		}
		
		else
			return defaultSpawn;
		
		
		if (i >= 1)
			return allLocs.get((int) (Math.random() * i));
		
		return defaultSpawn;
		
	}
	
	
	public Location getCopSpawn() {
		
		int i = 0;
		
		HashMap<Integer, Location> allLocs = new HashMap<Integer, Location>();
		
		if (arenaData.isConfigurationSection("spawns.cop")) {
				
			for (String spawnIdPath : arenaData.getConfigurationSection("spawns.cop").getKeys(false)) {
				
				int locX = arenaData.getInt("spawns.cop." + spawnIdPath + ".x");
				int locY = arenaData.getInt("spawns.cop." + spawnIdPath + ".y");
				int locZ = arenaData.getInt("spawns.cop." + spawnIdPath + ".z");
				float locPitch = arenaData.getInt("spawns.cop." + spawnIdPath + ".pitch");
				float locYaw = arenaData.getInt("spawns.cop." + spawnIdPath + ".yaw");
				
				allLocs.put(i, new Location(getArenaWorld(), locX, locY, locZ, locPitch, locYaw));
				
				i++;
				
			}
			
		}
		
		else
			return defaultSpawn;
		
		
		if (i >= 1)
			return allLocs.get((int) (Math.random() * i));
		
		return defaultSpawn;
		
	}
	
	
	public void setDefaultSpawn(Location loc) {
		
		if (!inArena(loc, false))
			throw new IllegalArgumentException("loc is not in the arena");
		
		Locations.saveLocationToFile(loc, arenaData, "spawns.default", false);
		defaultSpawn = loc;
		
	}
	
	
	public int setSpawn(Location loc, String spawnName) {
		
		spawnName = spawnName.toLowerCase();
		
		boolean isCar = false;
		String carType = "";
		
		if (spawnName.startsWith("car:")) {
			
			carType = spawnName.substring(4);
			isCar = true;
			spawnName = "car";
			
		}
		
		int i = 1;
		
		if (arenaData.get("spawns." + spawnName) != null) {
			
			while (i <= 100) {
				
				if (arenaData.get("spawns." + spawnName + "." + i) == null) {
					
					arenaData.set("spawns." + spawnName + "." + i + ".x", loc.getBlockX());
					arenaData.set("spawns." + spawnName + "." + i + ".y", loc.getBlockY());
					arenaData.set("spawns." + spawnName + "." + i + ".z", loc.getBlockZ());
					arenaData.set("spawns." + spawnName + "." + i + ".yaw", loc.getYaw());
					arenaData.set("spawns." + spawnName + "." + i + ".pitch", loc.getPitch());
					
					if (isCar)
						arenaData.set("spawns." + spawnName + "." + i + ".carType", carType);
					
					return i;

				}
				
				i++;
				
			}
			
		}
		
		else {
			
			arenaData.set("spawns." + spawnName + ".1.x", loc.getBlockX());
			arenaData.set("spawns." + spawnName + ".1.y", loc.getBlockY());
			arenaData.set("spawns." + spawnName + ".1.z", loc.getBlockZ());
			arenaData.set("spawns." + spawnName + ".1.yaw", loc.getYaw());
			arenaData.set("spawns." + spawnName + ".1.pitch", loc.getPitch());
			
			if (isCar)
				arenaData.set("spawns." + spawnName + ".1.carType", carType);
			
		}
		
		return 1;
		
	}

}

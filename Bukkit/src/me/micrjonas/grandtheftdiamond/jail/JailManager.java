package me.micrjonas.grandtheftdiamond.jail;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.TimeUnit;

import me.micrjonas.grandtheftdiamond.GrandTheftDiamond;
import me.micrjonas.grandtheftdiamond.GrandTheftDiamondPlugin;
import me.micrjonas.grandtheftdiamond.Team;
import me.micrjonas.grandtheftdiamond.api.event.cause.JailReason;
import me.micrjonas.grandtheftdiamond.api.event.player.PlayerJailEvent;
import me.micrjonas.grandtheftdiamond.api.event.player.PlayerJoinGameEvent;
import me.micrjonas.grandtheftdiamond.api.event.player.PlayerLeaveGameEvent;
import me.micrjonas.grandtheftdiamond.api.event.player.PlayerReleaseFromJailEvent;
import me.micrjonas.grandtheftdiamond.data.FileManager;
import me.micrjonas.grandtheftdiamond.data.FileReloadListener;
import me.micrjonas.grandtheftdiamond.data.PluginData;
import me.micrjonas.grandtheftdiamond.data.PluginFile;
import me.micrjonas.grandtheftdiamond.data.TemporaryPluginData;
import me.micrjonas.grandtheftdiamond.data.storage.StorableManager;
import me.micrjonas.grandtheftdiamond.listener.player.PlayerTeleportListener;
import me.micrjonas.grandtheftdiamond.messenger.Messenger;
import me.micrjonas.grandtheftdiamond.util.Calculator;
import me.micrjonas.grandtheftdiamond.util.bukkit.Locations;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;


public class JailManager implements Listener, FileReloadListener, StorableManager<Jail> {
	
	private static JailManager instance = new JailManager();
	
	/**
	 * Returns the loaded instance
	 * @return The loaded instance
	 */
	public static JailManager getInstance() {
		return instance;
	}
	
	private final Map<String, Jail> jails = new HashMap<>();
	private final HashMap<Player, Integer> jailTimeScheduler = new HashMap<>();
	private final Set<Player> handcuffedPlayers = new HashSet<>();
	private final Map<Player, Player> arrestingPlayers = new HashMap<>();
	
	private String jailTimeCalculation;
	private int jailTimeCopKilledCivilian;
	
	private JailManager() {
		Bukkit.getPluginManager().registerEvents(this, GrandTheftDiamondPlugin.getInstance());
		GrandTheftDiamond.registerFileReloadListener(this);
		GrandTheftDiamond.registerStorableManager(this, PluginFile.JAILS);
		Bukkit.getPluginManager().registerEvents(new JailListener(this), GrandTheftDiamondPlugin.getInstance());
	}
	
	@Override
	public void configurationReloaded(PluginFile file, FileConfiguration fileConfiguration) {
		if (file == PluginFile.EVENT_CONFIG) {
			jailTimeCalculation = fileConfiguration.getString("jail.jailTime");
			jailTimeCopKilledCivilian = fileConfiguration.getInt("kill.copKilled.jailTime");
		}
	}
	
	@Override
	public Collection<Jail> getAllObjects() {
		return Collections.unmodifiableCollection(jails.values());
	}
	
	@Override
	public void loadObjects(FileConfiguration dataFile) {
		jails.clear();
		if (dataFile.isConfigurationSection("")) {
			for (String jail : dataFile.getConfigurationSection("").getKeys(false)) {
				List<Location> cells = Locations.getLocationsFromFile(dataFile, jail + ".cells", false);
				try {
					createJail(jail, Locations.getLocationFromFile(dataFile, jail + ".spawn", false),
							cells);
				}
				catch (IllegalArgumentException ex) {
					dataFile.set(jail, null);
				}
			}
		}		
	}
	
	@Override
	public void saveObjects(FileConfiguration dataFile) {
		FileManager.clearFile(dataFile);
		for (Jail jail : jails.values()) {
			FileManager.getInstance().store(dataFile, jail);
		}
	}
	
	void onLeave(PlayerLeaveGameEvent e) {
		if (isJailed(e.getPlayer())) {
			FileConfiguration playerData = FileManager.getInstance().getPlayerData(e.getPlayer());
			playerData.set("jailInformation.jailTimeLeft", (playerData.getLong("jailInformation.jailedUntil") - System.currentTimeMillis()) / 1000);
			playerData.set("jailInformation.jailedUntil", null);
			GrandTheftDiamond.cancelTask(jailTimeScheduler.get(e.getPlayer()));
			jailTimeScheduler.remove(e.getPlayer());
		}
	}
	
	void onJoin(final PlayerJoinGameEvent e) {
		FileConfiguration playerData = FileManager.getInstance().getPlayerData(e.getPlayer());
		if (playerData.getBoolean("jailInformation.isJailed")) {
			int jailTimeLeft = playerData.getInt("jailInformation.jailTimeLeft");
			if (jailTimeLeft > 0) {
				playerData.set("jailInformation.jailedUntil", System.currentTimeMillis() + jailTimeLeft * 1000L);
				String currentJail = playerData.getString("currentJail");
				Jail jail = null;
				if (currentJail != null) {
					jail = getJail(currentJail);
				}
				if (jail == null) {
					jail = getRandomUsableJail();
				}
				if (jail == null) {
					playerData.set("jailInformation", null);
					return;
				}
				GrandTheftDiamondPlugin.getInstance().getRegisteredListener(PlayerTeleportListener.class).ignorePlayerOnNextTeleport(e.getPlayer());
				e.setJoinLocation(jail.getRandomCell());
				Messenger.getInstance().sendPluginMessage(e.getPlayer(), "jailedOnJoin", "%time%", String.valueOf(jailTimeLeft));
				jailTimeScheduler.put(e.getPlayer(), GrandTheftDiamond.scheduleSyncDelayedTask(new Runnable() {
					@Override
					public void run() {
						releasePlayerFromJail(e.getPlayer());
					}
				}, jailTimeLeft, TimeUnit.SECONDS));
			}
		}
	}
	
	/**
	 * Creates a new jail
	 * @param name The name of the jail
	 * @param spawn The spawn location of the jail
	 * @param cells A Set with all locations of the cells
	 * @return The registered jail
	 * @throws IllegalArgumentException Thrown if {@code name} or {@code spawn} is {@code null} or if a jail with {@code name}
	 * 	already exists
	 */
	public Jail createJail(String name, Location spawn, List<Location> cells) throws IllegalArgumentException {
		if (isJail(name)) {
			throw new IllegalArgumentException("jail '" + name +  "' does already exist");
		}
		if (name == null) {
			throw new IllegalArgumentException("name is not allowed to be null");
		}
		if (spawn  == null) {
			throw new IllegalArgumentException("spawn is not allowed to be null");
		}
		name = name.toLowerCase();
		Jail jail = new Jail(name, spawn, cells);
		jails.put(name, jail);
		return jail;
	}
	
	private IllegalArgumentException getIncompatibleJailArgumentExcepion(Player p, JailReason reason) {
		return new IllegalArgumentException("Incompatible jail arguments: Player team Team." + PluginData.getInstance().getTeam(p) + " and reason JailReason." + reason);
	}
	
	/**
	 * Jail a player
	 * @param p Player which should get jailed
	 * @param reason The jail reason
	 * @param cop The cop who jailed the player
	 * @param jail The jail the player should be jailed in; Set to null for a random jail
	 * @param time The time the player should be jailed in seconds; Set to -1 if the manager should calculate the time
	 * @param moneyForJailedPlayer The money the player who gets jailed should get; Number need to be negative if he should loose money
	 * @param moneyForCop The money balance a cop should get for jailing the player; Set to -1 if the manager should calculate the time
	 * @return True if the player could get jailed; False if jailing failed (e.g. {@code PlayerJailEvent} was cancelled)
	 * @throws IllegalArgumentException Thrown if {@code p} is null or if {@code cop} is {@code null} AND {@code reason}
	 * 	is != {@link JailReason#COP_KILLED_CIVILIAN} or {@link JailReason#COMMAND} or {@link JailReason#CUSTOM}
	 * @throws IllegalStateException Thrown if {@code jail.isUsable()} returns {@code false} or {@code jail} is {@code null}
	 * 	AND no other {@code Jail} is usable
	 */
	public boolean jailPlayer(final Player p, JailReason reason, Player cop, Jail jail,
			int time, int moneyForJailedPlayer, int moneyForCop) throws IllegalArgumentException, IllegalStateException {
		if (p == null) {
			throw new IllegalArgumentException("Player is not allowed to be null");
		}
		if (reason == null) {
			reason = JailReason.CUSTOM;
		}
		if (cop == null && !(reason == JailReason.COP_KILLED_CIVILIAN || reason == JailReason.COMMAND || reason == JailReason.CUSTOM)) {
			throw new IllegalArgumentException("Cop is not allowed to be null if jail reason != JailReason.COP_KILLED_CIVILIAN");
		}
		if (jail == null) {
			jail = getRandomUsableJail();
			if (jail == null) {
				throw new IllegalStateException("No usable jail is registered");
			}
		}
		else if (!jail.isUsable()) {
			throw new IllegalStateException("Jail is not usable (Maybe no cells?)");
		}
		if (time < 0) {
			if (reason == JailReason.COP_KILLED_CIVILIAN) {
				if (PluginData.getInstance().getTeam(p) == Team.COP) {
					time = jailTimeCopKilledCivilian;
				}
				else {
					throw getIncompatibleJailArgumentExcepion(cop, reason);
				}
			}
			else if (PluginData.getInstance().getTeam(p) != Team.COP) {
				time = (int) Calculator.calculateOrDefault(jailTimeCalculation.replaceAll("%wantedLevel%", String.valueOf(PluginData.getInstance().getWantedLevel(p))), -1);
			}
			else {
				throw getIncompatibleJailArgumentExcepion(cop, reason);
			}
		}
		if (time < 0) {
			return false;
		}
		PlayerJailEvent e = new PlayerJailEvent(p, cop, jail, time, reason);
		Bukkit.getPluginManager().callEvent(e);
		if (e.isCancelled()) {
			return false;
		}
		FileConfiguration playerData = FileManager.getInstance().getPlayerData(p);
		playerData.set("jailInformation.isJailed", true);
		playerData.set("jailInformation.currentJail", jail.getName());
		playerData.set("jailInformation.jailedUntil", System.currentTimeMillis() + time * 1000);
		playerData.set("jailInformation.lastJailedTime", time);
		jailTimeScheduler.put(p, GrandTheftDiamond.scheduleSyncDelayedTask(new Runnable() {
			@Override
			public void run() {
				releasePlayerFromJail(p);
			}
		}, time, TimeUnit.SECONDS));
		
		GrandTheftDiamondPlugin.getInstance().getRegisteredListener(PlayerTeleportListener.class).ignorePlayerOnNextTeleport(p);
		p.teleport(jail.getRandomCell());
		if (!(reason == JailReason.COP_KILLED_CIVILIAN || reason == JailReason.COMMAND)) {
			Messenger.getInstance().sendPluginMessage(p, "arrested", new Player[]{cop}, new String[]{"%time%", "%amount%"}, new String[]{String.valueOf(time), String.valueOf(moneyForJailedPlayer * -1)});
			Messenger.getInstance().sendPluginMessage(cop, "arrestedOther", new Player[]{p}, new String[]{"%time%", "%amount%"}, new String[]{String.valueOf(time), String.valueOf(moneyForJailedPlayer * -1)});
		}
		else {
			Messenger.getInstance().sendPluginMessage(p, "jailed", new Player[]{p}, new String[]{"%time%", "%amount%"}, new String[]{String.valueOf(time), String.valueOf(moneyForJailedPlayer * -1)});
		}
		return true;
	}
	
	/**
	 * Jail a player 
	 * @param p Player to jail
	 * @param reason Reason why the player should get jailed
	 * @return True if the player could get jailed; False if jailing failed (e.g. no jail set)
	 * @throws IllegalArgumentException Thrown if {@code p} is {@code null}
	 */
	public boolean jailPlayer(Player p, JailReason reason) throws IllegalArgumentException {
		return jailPlayer(p, reason, null, null, -1, -1, -1);
	}
	
	/**
	 * Jail a player
	 * @param p Player which should get jailed
	 * @param reason The jail reason
	 * @param jail The jail the player should be jailed in; Set to null for a random jail
	 * @param time The time the player should be jailed in seconds; Set to -1 if you want the manager should calculate the time
	 * @param moneyForJailedPlayer The money the player who gets jailed should get; Number need to be negative if he should loose money
	 * @return True if the player could get jailed. {@code false} if jailing failed (e.g. no jail set)
	 * @throws IllegalArgumentException 
	 */
	public boolean jailPlayer(Player p, JailReason reason, Jail jail, int time, int moneyForJailedPlayer)
			throws IllegalArgumentException, IllegalStateException {
		return jailPlayer(p, reason, null, jail, time, moneyForJailedPlayer, -1);
	}
	
	/**
	 * Jail a player
	 * @param p Player which should get jailed
	 * @param cop The cop who jailed the player
	 * @param reason The jail reason
	 * @return True if the player could get jailed; False if jailing failed (e.g. no jail set)
	 */
	public boolean jailPlayer(Player p, JailReason reason, Player cop) {
		return jailPlayer(p, reason, cop, null, -1, -1, -1);
	}
	
	/**
	 * Release a player from the jail. This will happen automatically after the set jail time is past
	 * @param p The player who should get released from the jail
	 * @return True if the player was jailed, else false
	 * @throws IllegalArgumentException Thrown if {@code p} is {@code null}
	 */
	public boolean releasePlayerFromJail(Player p) throws IllegalArgumentException, IllegalStateException {
		if (p == null) {
			throw new IllegalArgumentException("Player cannot be null");
		}
		if (!isJailed(p)) {
			return false;
		}
		Jail jail = getJail(FileManager.getInstance().getPlayerData(p).getString("jailInformation.currentJail"));
		if (jail == null) {
			return false;
		}
		Bukkit.getPluginManager().callEvent(new PlayerReleaseFromJailEvent(p, jail, FileManager.getInstance().getPlayerData(p).getInt("jailInformation.lastJailedTime")));
		FileManager.getInstance().getPlayerData(p).set("jailInformation", null);
		GrandTheftDiamondPlugin.getInstance().getRegisteredListener(PlayerTeleportListener.class).ignorePlayerOnNextTeleport(p);
		p.teleport(jail.getSpawn());
		Messenger.getInstance().sendPluginMessage(p, "releasedFromJail");
		return true;
	}
	
	/**
	 * Returns whether a player is jailed
	 * @param p The involved player
	 * @return True if the player is jailed, else false
	 */
	public boolean isJailed(Player p) {
		return FileManager.getInstance().getPlayerData(p).getBoolean("jailInformation.isJailed");
	}

	/**
	 * Returns the left jail time of a player in seconds. -1 if the player is not jailed 
	 * @param p The involved player
	 * @return The left jail time of a player in seconds
	 */
	public int getJailTimeLeft(Player p) {
		if (!isJailed(p)) {
			return -1;
		}
		int toReturn;
		if (TemporaryPluginData.getInstance().isIngame(p)) {
			toReturn = (int) ((FileManager.getInstance().getPlayerData(p).getLong("jailInformation.jailedUntil") - System.currentTimeMillis()) / 1000);
		}
		else {
			toReturn = FileManager.getInstance().getPlayerData(p).getInt("jailInformation.jailTimeLeft");
		}
		if (toReturn < 1) {
			return -1;
		}
		return toReturn;
	}
	
	/**
	 * Checks whether a jail with the given name exists. Ignores case sensitive
	 * @param name The name to check
	 * @return True, if a jail with the given name exists, else false
	 */
	public boolean isJail(String name) {
		return jails.containsKey(name.toLowerCase());
	}
	
	/**
	 * Returns the jail with the given name
	 * @param name The name of the jail
	 * @return The jail with the given name; Returns null if the jail is not registered
	 */
	public Jail getJail(String name) {
		if (name == null) {
			throw new IllegalArgumentException("Name is not allowed to be null");
		}
		return jails.get(name.toLowerCase());
	}
	
	/**
	 * Returns all players which are jailed in the given jail
	 * @param jail The jail to check the jailed players
	 * @return A Set of all jailed players in the given jail
	 */
	public Set<Player> getJailedPlayers(Jail jail) {
		return jail.getJailedPlayers();
	}
	
	/**
	 * Returns a random {@link Jail} of all registered jails
	 * @return A random {@link Jail} of all registered jails; null if no jails are registered or no {@link Jail} is usable
	 */
	public Jail getRandomUsableJail() {
		if (jails.size() == 0) {
			return null;
		}
		List<Jail> jails = new ArrayList<>(this.jails.values());
		Collections.shuffle(jails);
		for (Jail jail : jails) {
			if (jail.isUsable()) {
				return jail;
			}
		}
		return null;
	}
	
	/**
	 * Handcuffs a {@code Player}
	 * @param p The player to handcuff
	 * @param cop The cop who handcuffed
	 * @throws IllegalArgumentException Thrown if {@code p} or {@code cop} is {@code null}
	 * @throws IllegalStateException Thrown if:
	 * 	<ul>
	 * 		<li>one of the players is not in game</li>
	 * 		<li>player to handcuff is not in {@link Team#CIVILIAN}</li>
	 * 		<li>player who handcuffed is not in {@link Team#COP}</li>
	 * 	</ul>
	 */
	public void handcuffPlayer(Player p, Player cop) throws IllegalArgumentException, IllegalStateException {
		if (p == null) {
			throw new IllegalArgumentException("Player to handcuff cannot be null");
		}
		if (cop == null) {
			throw new IllegalArgumentException("Player who handcuffed cannot be null");
		}
		if (!TemporaryPluginData.getInstance().isIngame(p)) {
			throw new IllegalStateException("Player to handcuff is not in game");
		}
		if (!TemporaryPluginData.getInstance().isIngame(cop)) {
			throw new IllegalStateException("Player who handcuffed is not in game");
		}
		if (PluginData.getInstance().getTeam(p) != Team.CIVILIAN) {
			throw new IllegalStateException("Player to handcuff must be in team CIVILIAN. But is in " + PluginData.getInstance().getTeam(p));
		}
		if (PluginData.getInstance().getTeam(cop) != Team.COP) {
			throw new IllegalStateException("Player who handcuffed must be in team COP. But is in " + PluginData.getInstance().getTeam(cop));
		}
		// Much to do, yeah!
		handcuffedPlayers.add(p);
	}
	
	/**
	 * Checks whether a {@code Player} is handcuffed
	 * @param p The player to check
	 * @return True if the {@code Player} is handcuffed, else false. If {@code true}, the {@code Player}
	 * 	is in {@link Team#CIVILIAN}
	 * @throws IllegalArgumentException Thrown if {@code p} is null
	 */
	public boolean isHandcuffed(Player p) throws IllegalArgumentException {
		if (p == null) {
			throw new IllegalArgumentException("Player to check cannot be null");
		}
		return handcuffedPlayers.contains(p);
	}
	
	public void setArrestingPlayer(Player cop, Player p) throws IllegalArgumentException, IllegalStateException {
		if (p == null) {
			throw new IllegalArgumentException("Player to arrest cannot be null");
		}
		if (cop == null) {
			throw new IllegalArgumentException("Player who arrests cannot be null");
		}
		if (!TemporaryPluginData.getInstance().isIngame(p)) {
			throw new IllegalStateException("Player to arrest is not in game");
		}
		if (!TemporaryPluginData.getInstance().isIngame(cop)) {
			throw new IllegalStateException("Player who arrests is not in game");
		}
		if (PluginData.getInstance().getTeam(p) != Team.CIVILIAN) {
			throw new IllegalStateException("Player to arrest must be in team CIVILIAN. But is in " + PluginData.getInstance().getTeam(p));
		}
		if (PluginData.getInstance().getTeam(cop) != Team.COP) {
			throw new IllegalStateException("Player who arrests must be in team COP. But is in " + PluginData.getInstance().getTeam(cop));
		}
		cop.setPassenger(p);
		arrestingPlayers.put(cop, p);
	}
	
	/**
	 * Checks whether a cop is arresting a civilian/gangster
	 * @param cop The cop to check
	 * @return True if the {@code cop} is arresting an other {@code Player}, else false. If {@code true},
	 * 	you can be sure, that the {@code cop} is in {@link Team#CIVILIAN}
	 * @throws IllegalArgumentException
	 */
	public boolean isArrestingPlayer(Player cop) throws IllegalArgumentException {
		if (cop == null) {
			throw new IllegalArgumentException("Player to check cannot be null");
		}
		return arrestingPlayers.containsKey(cop);
	}

}

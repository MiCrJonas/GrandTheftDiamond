package me.micrjonas.grandtheftdiamond.gang;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.UUID;

import me.micrjonas.grandtheftdiamond.GrandTheftDiamond;
import me.micrjonas.grandtheftdiamond.api.event.GangCreateEvent;
import me.micrjonas.grandtheftdiamond.data.FileManager;
import me.micrjonas.grandtheftdiamond.data.FileReloadListener;
import me.micrjonas.grandtheftdiamond.data.PluginData;
import me.micrjonas.grandtheftdiamond.data.PluginFile;
import me.micrjonas.grandtheftdiamond.data.storage.StorableManager;

import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;

@SuppressWarnings("deprecation")
public class GangManager implements FileReloadListener, StorableManager<Gang> {

	private static GangManager instance = new GangManager();
	
	public static GangManager getInstance() {
		return instance;
	}
	
	private final Map<String, Gang> gangs = new HashMap<>();
	private final Map<UUID, Gang> playerGangs = new HashMap<>();
	private int requiredExpPerMember;
	
	private GangManager() {
		GrandTheftDiamond.registerStorableManager(this, PluginFile.GANGS);
	}
	
	@Override
	public void configurationReloaded(PluginFile file, FileConfiguration fileConfiguration) {
		if (file == PluginFile.CONFIG) {
			requiredExpPerMember = fileConfiguration.getInt("gangs.memberCount.requiredExpPerMember");
		}
	}
	
	@Override
	public Collection<Gang> getAllObjects() {
		return new HashSet<>(gangs.values());
	}

	@Override
	public void loadObjects(FileConfiguration dataFile) {
		gangs.clear(); 
		for (String gang : dataFile.getConfigurationSection("").getKeys(false)) {
			OfflinePlayer leader = null;
			String leaderId = dataFile.getString(gang + ".leader");
			if (leaderId != null) {
				try {
					leader = Bukkit.getOfflinePlayer(UUID.fromString(leaderId));
				}
				catch (IllegalArgumentException ex) {
					dataFile.set(gang, null);
					continue;
				}	
			}
			else {
				continue;
			}
			if (leader == null) {
				dataFile.set(gang, null);
				continue;
			}
			List<OfflinePlayer> members = null;
			if (dataFile.isList(gang + ".members")) {
				members = new ArrayList<>();
				List<String> configList = dataFile.getStringList(gang + ".members");
				for (String uuid : dataFile.getStringList(gang + ".members")) {
					try {
						members.add(Bukkit.getOfflinePlayer(UUID.fromString(uuid)));
					}
					catch (IllegalArgumentException ex) {
						configList.remove(uuid);
					}
				}
				dataFile.set(gang + ".members", configList);
			}
			Map<GangOption<?>, Object> options = null;
			if (dataFile.get(gang + ".options") instanceof Map) {
				options = new HashMap<>();
				@SuppressWarnings("unchecked")
				Map<String, Object> optionConfiguration = (Map<String, Object>) dataFile.get(gang + ".options");
				for (Entry<String, Object> option : new HashSet<>(optionConfiguration.entrySet())) {
					try {
						options.put(GangOption.valueOf(option.getKey().toUpperCase()), option.getValue());
					}
					catch (IllegalArgumentException ex) {
						optionConfiguration.remove(option.getKey());
					}
				}
				dataFile.set(gang + ".options", optionConfiguration);
			}
			createGang(null, gang, leader, members, options, true);
		}
	}

	@Override
	public void saveObjects(FileConfiguration dataFile) {
		FileManager.clearFile(dataFile);
		for (Gang gang : getAllObjects()) {
			FileManager.getInstance().store(dataFile, gang);
		}
	}
	
	public int getRequiredExpPerMember() {
		return requiredExpPerMember;
	}
	
	/**
	 * Returns how much members a gang can have with {@code p} as leader. Perceives player-specific
	 * 	member count permissions and his exp. Does not perceive the permission to create a gang
	 * @param p The gang's leader
	 * @return The count of members he gang can have in addition to {@code p} as the leader. 0 if the {@code Player} cannot
	 *  create a gang
	 */
	public int getAllowedGangMembers(Player p) {
		int exp = PluginData.getInstance().getExp(p);
		int allowed = exp / getRequiredExpPerMember();
		// TODO Check for player-specific permissions
		return allowed;
	}
	
	/**
	 * Equivalent to {@link GangManager#createGang(creator, name, leader, null, null, false)}
	 * @see GangManager#createGang(CommandSender, String, OfflinePlayer, Collection, Map, boolean)
	 */
	@SuppressWarnings("javadoc")
	public Gang createGang(CommandSender creator, String name, OfflinePlayer leader) {
		return createGang(creator, name, leader, null, null, false);	
	}
	
	/**
	 * 
	 * @param creator The creator of the {@link Gang}
	 * @param name The name of the gang
	 * @param leader The leader of the gang, may be null
	 * @param members The members of the gang, may be null or empty
	 * @param maxMembers The maximum member count
	 * @param friendlyFire True, if friendly fire should be enabled, else false
	 * @param leaderMustInvite Whether the leader must invite new members
	 * @return The new created gang
	 * @throws IllegalArgumentException If {@code name}, {@code leader} or{@code  creator} is {@code null} or a gang with the name does already exist
	 */
	public Gang createGang(CommandSender creator, String name, OfflinePlayer leader, Collection<? extends OfflinePlayer> members, int maxMembers, boolean friendlyFire, boolean leaderMustInvite) {
		return createGang(creator, name, leader, members, null, false);
	}
	
	private Gang createGang(CommandSender creator, String name, OfflinePlayer leader, Collection<? extends OfflinePlayer> members, Map<GangOption<?>, Object> options, boolean onStartup) {
		if (creator == null && !onStartup) {
			throw new IllegalArgumentException("Creator is not allowed to be null");
		}
		if (name == null) {
			throw new IllegalArgumentException("Name is not allowed to be null");
		}
		if (isGang(name)) {
			throw new IllegalArgumentException("Gang with name '" + name + "' does already exist");
		}
		if (leader == null) {
			throw new IllegalArgumentException("Leader cannot be null");
		}
		if (!onStartup) {
			GangCreateEvent e = new GangCreateEvent(creator, name, leader, members);
			Bukkit.getPluginManager().callEvent(e);
			if (e.isCancelled()) {
				return null;
			}
		}
		Gang gang = new Gang(name, leader, members, null);
		gangs.put(name.toLowerCase(), gang);
		return gang;
	}
	
	
	/**
	 * Checks whether a gang with the given name exists. Ignores case sensitive
	 * @param name Name to check
	 * @return True if a gang exists with the given name, else false
	 */
	public boolean isGang(String name) {
		return gangs.containsKey(name.toLowerCase());
	}
	
	/**
	 * Removes a gang
	 * @param name Name of the gang to remove
	 * @throws IllegalArgumentException If game does not exist
	 */
	public void removeGang(String name) throws IllegalArgumentException {
		if (!isGang(name)) {
			throw new IllegalArgumentException("Game with name '" + name + "' does not exist");
		}
		gangs.remove(name.toLowerCase());
	}
	
	/**
	 * Returns the gang by the given name, null if gang does not exist, ignores case sensitive
	 * @param name The name of the gang
	 * @return The gang by the given name, {@code null} if the gang does not exist
	 */
	public Gang getGang(String name) {
		return gangs.get(name.toLowerCase());
	}
	
	void setGang(OfflinePlayer p, Gang gang) throws IllegalArgumentException {
		if (p == null) {
			throw new IllegalArgumentException("Player cannot be null");
		}
		if (gang == null) {
			playerGangs.remove(p);
		}
		else {
			playerGangs.put(p.getUniqueId(), gang);
		}
	}
	
	/**
	 * Returns the player's gang. The {@code Player} does not have to be the leader
	 * @param p The player to get the gang
	 * @return The player's gang, {@code null} if he isn't in a gang
	 * @throws IllegalArgumentException Thrown if {@code p} is {@code null}
	 */
	public Gang getPlayerGang(OfflinePlayer p) throws IllegalArgumentException {
		if (p == null) {
			throw new IllegalArgumentException("Player cannot be null");
		}
		return playerGangs.get(p.getUniqueId());
	}
	
	/**
	 * Checks whether the tow players can attack themselves. If they are in the same gang and friendly fire is disabled, it returns false
	 * @param p0 First involved Player
	 * @param p1 Second involved Player
	 * @return True if the players can attack themselves, else false
	 */
	public boolean canAttack(Player p0, Player p1) {
		Gang gang0 = getPlayerGang(p0);
		Gang gang1;
		if (gang0 == null) {
			return true;
		}
		gang1 = getPlayerGang(p1);
		if (gang1 == null) {
			return true;
		}
		if (gang0 != gang1) {
			return true;
		}
		return gang0.getOptionValue(GangOption.FRIENDLY_FIRE);
	}

}

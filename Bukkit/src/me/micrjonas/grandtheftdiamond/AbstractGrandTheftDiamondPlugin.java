package me.micrjonas.grandtheftdiamond;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.UUID;
import java.util.logging.Level;

import me.micrjonas.grandtheftdiamond.command.CommandExecutor;
import me.micrjonas.grandtheftdiamond.data.FileManager;
import me.micrjonas.grandtheftdiamond.data.FileReloadListener;
import me.micrjonas.grandtheftdiamond.data.PluginFile;
import me.micrjonas.grandtheftdiamond.data.player.PlayerDataUser;
import me.micrjonas.grandtheftdiamond.data.storage.Storable;
import me.micrjonas.grandtheftdiamond.data.storage.StorableManager;
import me.micrjonas.grandtheftdiamond.data.storage.Storables;
import me.micrjonas.grandtheftdiamond.messenger.NoPermissionType;
import me.micrjonas.grandtheftdiamond.updater.Version;
import me.micrjonas.minecraft.command.CommandSource;

import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;


/**
 * This class provides a skeletal implementation of {@link GrandTheftDiamondPlugin}
 */
public abstract class AbstractGrandTheftDiamondPlugin implements GrandTheftDiamondPlugin {
	
	private final Map<String, CommandExecutor> commands = new HashMap<>();
	private final Set<PlayerDataUser> playerDataUser = new HashSet<>();
	private final Set<FileReloadListener> fileReloadListener = new HashSet<>();
	private final Map<PluginFile, StorableManager<? extends Storable>> storableManager = new HashMap<>();
	private final Set<String> playPermissions = new HashSet<>();
	private final Version version;
	
	/**
	 * @param version The plugin's version
	 */
	protected AbstractGrandTheftDiamondPlugin(String version) {
		this.version = new Version(version);
	}

	@Override
	public Version getVersion() {
		return version;
	}
	
	/**
	 * Checks whether the sender has the given permissions.
	 * All checked permissions start with "gta.". If {@code perm} does not start with it, the method will add it
	 * @param sender The involved CommandSender
	 * @param perm The permission
	 * @return True if the player have the given permission, else false
	 */
	@Override
	public boolean checkPermission(CommandSource sender, String perm) {
		if (sender instanceof Player) {
			if (perm.startsWith("gta.")) {
				perm = perm.substring(4, perm.length());
			}
			if (sender.hasPermission(perm) || sender.hasPermission("gta.*")) {
				return true;
			}
			String[] splitPerm = perm.split("\\.");
			perm = "gta." + perm;
			boolean hasPlayPermission = sender.hasPermission("gta.play");
			for (int i = splitPerm.length - 1; i >= 0; i--) {
				if (checkDirectPermission(sender, perm, hasPlayPermission)) {
					return true;
				}
				else if ((!checkDirectPermission(sender, perm, hasPlayPermission) && i == splitPerm.length) || 
						(!checkDirectPermission(sender, perm + "\\.\\*", hasPlayPermission))) {
					return false;
				}
				perm = perm.replaceAll("\\.\\*", "").replaceAll(splitPerm[i], "\\*");
			}
			return false;
		}
		return true;
	}
	
	/**
	 * Checks whether the sender has the given permissions and sends a no permission message if not
	 * All checked permissions start with "gta." 
	 * @param sender The involved command sender
	 * @param perm The permission
	 * @param sendNoPermissionMessage Set to true if you want to send a message if sender does not have the permission
	 * @param type {@link NoPermissionType} of the permission. Used to send the no permissions message
	 * @return True if the player have the given permission, else false
	 * @see GrandTheftDiamond#checkPermission(CommandSender, String)
	 */
	@Override
	public boolean checkPermission(CommandSource sender, String perm, boolean sendNoPermissionMessage, NoPermissionType type) {
		if (checkPermission(sender, perm)) {
			return true;
		}
		if (sendNoPermissionMessage) {
			// TODO Messenger.getInstance().sendNoPermissionsMessage(sender, type, perm);
		}
		return false;
	}
	
	private boolean checkDirectPermission(CommandSource sender, String perm, boolean hasPlayPermission) {
		return (hasPlayPermission && playPermissions.contains(perm)) || sender.hasPermission(perm);
	}
	
	/**
	 * Returns the command executor of the command
	 * @param name The name of the command, ignores case sensitive
	 * @return The registered command executor, null if the command is not registered
	 * @throws IllegalArgumentException Thrown if the command name is {@code null}
	 */
	@Override
	public CommandExecutor getCommandExecutor(String name) throws IllegalArgumentException {
		if (name == null) {
			throw new IllegalArgumentException("Command name is not allowed to be null");
		}
		return commands.get(name.toLowerCase());
	}
	
	/**
	 * Registers a sub command of the Grand Theft Diamond super command
	 * @param executor The command executor
	 * @param name The name of the command, ignores case sensitive
	 * @param optionalAliases Optional command aliases
	 * @throws IllegalArgumentException Thrown if the executor or the command name is {@code null}
	 */
	@Override
	public void registerCommand(CommandExecutor executor, String name, String... optionalAliases) throws IllegalArgumentException {
		if (executor == null) {
			throw new IllegalArgumentException("Executor is not allowed to be null");
		}
		if (name == null) {
			throw new IllegalArgumentException("Command name is not allowed to be null");
		}
		synchronized (commands) {
			commands.put(name.toLowerCase(), executor);
			if (optionalAliases != null) {
				for (String alias : optionalAliases) {
					commands.put(alias.toLowerCase(), executor);
				}
			}
		}
	}
	
	/**
	 * Unregisters all aliases for the given command executor
	 * @param executor The executor to unregister
	 * @return A Set of all aliases used for the unregistered executor
	 * @throws IllegalArgumentException Thrown if {@code executor} is {@code null}
	 */
	@Override
	public Set<String> unregisterCommand(CommandExecutor executor) throws IllegalArgumentException {
		if (executor == null) {
			throw new IllegalArgumentException("Executor to unregister is not allowed to be null");
		}
		synchronized (commands) {
			Iterator<Entry<String, CommandExecutor>> iter = commands.entrySet().iterator();
			Set<String> aliases = new HashSet<>();
			while (iter.hasNext()) {
				Entry<String, CommandExecutor> entry = iter.next();
				if (entry.getValue() == executor) {
					aliases.remove(entry.getKey());
					iter.remove();
				}
			}
			return aliases;	
		}
	}
	
	/**
	 * Unregisters a command
	 * @param alias The alias of the command
	 * @param unregisterAll If true, all aliases with the same command executor will be unregistered, else only the given alias will be unregistered
	 * @return A Set of all unregistered command aliases, empty if the given alias was not registered
	 * @throws IllegalArgumentException Thrown if alias is {@code null}
	 */
	@Override
	public Set<String> unregisterCommand(String alias, boolean unregisterAll) throws IllegalArgumentException {
		if (alias == null) {
			throw new IllegalArgumentException("Command alias is not allowed to be null");
		}
		if (unregisterAll) {
			CommandExecutor executor = getCommandExecutor(alias);
			if (executor != null) {
				return unregisterCommand(executor);
			}
			return new HashSet<>(0);
		}
		else {
			commands.remove(alias.toLowerCase());
			return new HashSet<>(Arrays.asList(alias.toLowerCase()));
		}
	}
	
	/**
	 * Returns a {@link List} with all registered commands. The {@link List} is unmodifiable.
	 * @return A {@link List} with all registered commands and its aliases, the command names are not sorted naturally
	 * @see Collections#unmodifiableSet(Set)
	 */
	@Override
	public Set<String> getRegisteredCommands() {
		return Collections.unmodifiableSet(commands.keySet());
	}
	
	/**
	 * Checks whether a command name is registered
	 * @param name The name of the command, ignores case sensitive
	 * @return True if the command is registered, else false
	 * @throws IllegalArgumentException Thrown if the command name is {@code null}
	 */
	@Override
	public boolean isCommandRegistered(String name) throws IllegalArgumentException {
		if (name == null) {
			throw new IllegalArgumentException("Name is not allowed to be null");
		}
		return commands.containsKey(name.toLowerCase());
	}
	
	/**
	 * Registers a player data user which get called if a player joins/leave to load/unload the player data
	 * @param user The player data user
	 * @return True if the registration was successful, else false (already registered)
	 * @throws IllegalArgumentException Thrown if the user is {@code null}
	 */
	@Override
	public boolean registerPlayerDataUser(PlayerDataUser user) throws IllegalArgumentException {
		if (user == null) {
			throw new IllegalArgumentException("User is not allowed to be null");
		}
		return playerDataUser.add(user);
	}
	
	/**
	 * Unregisters a player data user
	 * @param user User to unregister
	 * @return True if unregister was successful, else false (not registered)
	 * @throws IllegalArgumentException Thrown if the user is {@code null}
	 */
	@Override
	public boolean unregisterPlayerDataUser(PlayerDataUser user) throws IllegalArgumentException  {
		if (user == null) {
			throw new IllegalArgumentException("User is not allowed to be null");
		}
		return playerDataUser.remove(user);
	}
	
	/**
	 * Gets called when a file was reloaded. Reloads all its listeners and its storable manager
	 * @param file The file which was reloaded
	 * @deprecated Do not call
	 */
	@Override
	@Deprecated
	public synchronized void fileReloaded(PluginFile file) {
		reloadFileReloadListeners(file);
		reloadStorableManager(file);
	}
	
	/**
	 * Gets called when all files were reloaded. Reloads all file reload listeners and storable managers
	 * @deprecated Do not call
	 */
	@Override
	@Deprecated
	public synchronized void allFilesReloaded() {
		for (PluginFile file : PluginFile.values()) {
			fileReloaded(file);
		}
	}
	
	/**
	 * Gets called before the {@link FileManager} stores the data to a file. The registered storable manager needs to store its data into the file
	 * @param file The file
	 * @throws IllegalArgumentException Thrown if the file is {@code null}
	 */
	@Override
	@Deprecated
	public synchronized void saveData(PluginFile file) throws IllegalArgumentException {
		FileConfiguration dataFile = FileManager.getInstance().getFileConfiguration(file);
		StorableManager<? extends Storable> manager = getStorableManager(file);
		if (manager != null) {
			Storables.saveRegisteredObjects(manager, dataFile, "");
		}
	}
	
	/**
	 * Gets called after a player joined the game to load all player data for the player
	 * @param playerId he player's id
	 * @throws IllegalArgumentException If the player id is {@code null}
	 */
	@Override
	public void loadPlayerData(UUID playerId) throws IllegalArgumentException {
		for (PlayerDataUser dataUser : playerDataUser) {
			try {
				dataUser.loadPlayerData(playerId);
			}
			catch (Throwable t) {
				getLogger().log(Level.WARNING, "Failed to load player data of " + dataUser.getClass().getName() + ":", t);
			}
		}
	}
	
	/**
	 * Clears the stored player data of all registered player data users 
	 * @param playerId he player's id
	 * @throws IllegalArgumentException If the player id is {@code null}
	 */
	@Override
	@Deprecated
	public void clearPlayerData(UUID playerId) throws IllegalArgumentException {
		for (PlayerDataUser dataUser : playerDataUser) {
			try {
				dataUser.clearPlayerData(playerId);
			}
			catch (Throwable t) {
				getLogger().log(Level.WARNING, "Failed to clear player data of " + dataUser.getClass().getName() + ":", t);
			}
		}
	}
	
	/**
	 * Registers a file reload listener which gets called when a file gets reloaded
	 * @param l The listener
	 * @return True if the registration was successful, else false (already registered)
	 * @throws IllegalArgumentException Thrown if the listener is {@code null}
	 */
	@Override
	public boolean registerFileReloadListener(FileReloadListener l) throws IllegalArgumentException {
		if (l == null) {
			throw new IllegalArgumentException("Listener is not allowed to be null");
		}
		return fileReloadListener.add(l);
	}
	
	/**
	 * Unregisters a file reload listener
	 * @param l The listener
	 * @return True if unregistration was successful, else false (not registered)
	 * @throws IllegalArgumentException Thrown if the listener is {@code null}
	 */
	@Override
	public boolean unregisterFileReloadListener(FileReloadListener l) throws IllegalArgumentException {
		if (l == null) {
			throw new IllegalArgumentException("Listener is not allowed to be null");
		}
		return fileReloadListener.remove(l);
	}
	
	/**
	 * Reloads all listeners with the given file
	 * @param file The file to reload 
	 * @throws IllegalArgumentException Thrown if the plugin file is {@code null}
	 */
	@Override
	public void reloadFileReloadListeners(PluginFile file) throws IllegalArgumentException {
		if (file == null) {
			throw new IllegalArgumentException("Cannot reload null file");
		}
		FileConfiguration fileConfiguration = FileManager.getInstance().getFileConfiguration(file);
		for (FileReloadListener l : fileReloadListener) {
			try {
				l.configurationReloaded(file, fileConfiguration);
			}
			catch (Throwable t) {
				getLogger().log(Level.WARNING, "Failed to reload " + l.getClass().getName() + ":", t);
			}
		}
	}
	
	/**
	 * Registers a storable manager, overrides the old one if exist
	 * @param manager The manager to register
	 * @param file The involved plugin file
	 * @return True if the registration was successful, else false (already registered)
	 * @throws IllegalArgumentException Thrown if the manager or the plugin file is {@code null}
	 */
	@Override
	public boolean registerStorableManager(StorableManager<? extends Storable> manager, PluginFile file) throws IllegalArgumentException {
		if (manager == null) {
			throw new IllegalArgumentException("Manager is not allowed to be null");
		}
		if (file == null) {
			throw new IllegalArgumentException("Plugin file is not allowed to be null");
		}
		return storableManager.put(file, manager) != manager;
	}
	
	/**
	 * Unregisters the listener of the given plugin file
	 * @param file The file to unregister
	 * @return True if a listener was registered, else false
	 * @throws IllegalArgumentException Thrown if the file is {@code null}
	 */
	@Override
	public boolean unregisterStorableManager(PluginFile file) throws IllegalArgumentException {
		if (file == null) {
			throw new IllegalArgumentException("File is not allowed to be null");
		}
		return storableManager.remove(file) != null;
	}
	
	/**
	 * Returns the registered manager for the file
	 * @param file The file to get the manager
	 * @return The registered manager, null if no listener is registered
	 * @throws IllegalArgumentException Thrown if the file is {@code null}
	 */
	@Override
	public StorableManager<? extends Storable> getStorableManager(PluginFile file) throws IllegalArgumentException {
		if (file == null) {
			throw new IllegalArgumentException("File is not allowed to be null");
		}
		return storableManager.get(file);
	}
	
	/**
	 * Reloads the storable manager for the given file
	 * @param file The file
	 * @return True if reload was successful, else false (e.g. exception while loading or no manager registered)
	 * @throws IllegalArgumentException
	 */
	@Override
	public boolean reloadStorableManager(PluginFile file) throws IllegalArgumentException {
		StorableManager<?> manager = getStorableManager(file);
		FileConfiguration data = FileManager.getInstance().getFileConfiguration(file);
		if (manager == null) {
			return false;
		}
		try {
			manager.loadObjects(data);
			return true;
		}
		catch (Throwable t) {
			getLogger().log(Level.WARNING, "Failed to reload " + manager.getClass().getName() + ":", t);
			return false;
		}
	}
	
}

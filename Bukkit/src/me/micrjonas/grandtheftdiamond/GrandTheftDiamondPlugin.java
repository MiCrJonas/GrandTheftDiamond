package me.micrjonas.grandtheftdiamond;

import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.UUID;

import me.micrjonas.grandtheftdiamond.command.CommandExecutor;
import me.micrjonas.grandtheftdiamond.data.FileManager;
import me.micrjonas.grandtheftdiamond.data.FileReloadListener;
import me.micrjonas.grandtheftdiamond.data.PluginFile;
import me.micrjonas.grandtheftdiamond.data.player.PlayerDataUser;
import me.micrjonas.grandtheftdiamond.data.storage.Storable;
import me.micrjonas.grandtheftdiamond.data.storage.StorableManager;
import me.micrjonas.grandtheftdiamond.messenger.NoPermissionType;
import me.micrjonas.minecraft.command.CommandSource;
import me.micrjonas.minecraft.server.plugin.ServerPlugin;

import org.bukkit.command.CommandSender;


/**
 * Interface to mark the main class of GrandTheftDiamond for a specific server-software implementation
 */
public interface GrandTheftDiamondPlugin extends ServerPlugin {
	
	/**
	 * Checks whether the sender has the given permissions.
	 * All checked permissions start with "gta.". If {@code perm} does not start with it, the method will add it
	 * @param sender The involved CommandSender
	 * @param perm The permission
	 * @return True if the player have the given permission, else false
	 */
	boolean checkPermission(CommandSource sender, String perm);
	
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
	boolean checkPermission(CommandSource sender, String perm, boolean sendNoPermissionMessage, NoPermissionType type);
	
	/**
	 * Returns the command executor of the command
	 * @param name The name of the command, ignores case sensitive
	 * @return The registered command executor, null if the command is not registered
	 * @throws IllegalArgumentException Thrown if the command name is {@code null}
	 */
	CommandExecutor getCommandExecutor(String name) throws IllegalArgumentException;
	
	/**
	 * Registers a sub command of the Grand Theft Diamond super command
	 * @param executor The command executor
	 * @param name The name of the command, ignores case sensitive
	 * @param optionalAliases Optional command aliases
	 * @throws IllegalArgumentException Thrown if the executor or the command name is {@code null}
	 */
	void registerCommand(CommandExecutor executor, String name, String... optionalAliases) throws IllegalArgumentException;
	
	/**
	 * Unregisters all aliases for the given command executor
	 * @param executor The executor to unregister
	 * @return A Set of all aliases used for the unregistered executor
	 * @throws IllegalArgumentException Thrown if {@code executor} is {@code null}
	 */
	Set<String> unregisterCommand(CommandExecutor executor) throws IllegalArgumentException;
	
	/**
	 * Unregisters a command
	 * @param alias The alias of the command
	 * @param unregisterAll If true, all aliases with the same command executor will be unregistered, else only the given alias will be unregistered
	 * @return A Set of all unregistered command aliases, empty if the given alias was not registered
	 * @throws IllegalArgumentException Thrown if alias is {@code null}
	 */
	Set<String> unregisterCommand(String alias, boolean unregisterAll) throws IllegalArgumentException;
	
	/**
	 * Returns a {@link List} with all registered commands. The {@link List} is unmodifiable.
	 * @return A {@link List} with all registered commands and its aliases, the command names are not sorted naturally
	 * @see Collections#unmodifiableSet(Set)
	 */
	Set<String> getRegisteredCommands();
	
	/**
	 * Checks whether a command name is registered
	 * @param name The name of the command, ignores case sensitive
	 * @return True if the command is registered, else false
	 * @throws IllegalArgumentException Thrown if the command name is {@code null}
	 */
	boolean isCommandRegistered(String name) throws IllegalArgumentException;
	
	/**
	 * Registers a player data user which get called if a player joins/leave to load/unload the player data
	 * @param user The player data user
	 * @return True if the registration was successful, else false (already registered)
	 * @throws IllegalArgumentException Thrown if the user is {@code null}
	 */
	boolean registerPlayerDataUser(PlayerDataUser user) throws IllegalArgumentException;
	
	/**
	 * Unregisters a player data user
	 * @param user User to unregister
	 * @return True if unregister was successful, else false (not registered)
	 * @throws IllegalArgumentException Thrown if the user is {@code null}
	 */
	boolean unregisterPlayerDataUser(PlayerDataUser user) throws IllegalArgumentException;
	
	/**
	 * Gets called when a file was reloaded. Reloads all its listeners and its storable manager
	 * @param file The file which was reloaded
	 */
	void fileReloaded(PluginFile file);
	
	/**
	 * Gets called when all files were reloaded. Reloads all file reload listeners and storable managers
	 */
	void allFilesReloaded();
	
	/**
	 * Gets called before the {@link FileManager} stores the data to a file. The registered storable manager needs to store its data into the file
	 * @param file The file
	 * @throws IllegalArgumentException Thrown if the file is {@code null}
	 */
	public void saveData(PluginFile file) throws IllegalArgumentException;
	
	/**
	 * Gets called after a player joined the game to load all player data for the player
	 * @param playerId he player's id
	 * @throws IllegalArgumentException If the player id is {@code null}
	 */
	void loadPlayerData(UUID playerId) throws IllegalArgumentException;
	
	/**
	 * Clears the stored player data of all registered player data users 
	 * @param playerId he player's id
	 * @throws IllegalArgumentException If the player id is {@code null}
	 */
	void clearPlayerData(UUID playerId) throws IllegalArgumentException;
	
	/**
	 * Registers a file reload listener which gets called when a file gets reloaded
	 * @param l The listener
	 * @return True if the registration was successful, else false (already registered)
	 * @throws IllegalArgumentException Thrown if the listener is {@code null}
	 */
	boolean registerFileReloadListener(FileReloadListener l) throws IllegalArgumentException;
	
	/**
	 * Unregisters a file reload listener
	 * @param l The listener
	 * @return True if unregistration was successful, else false (not registered)
	 * @throws IllegalArgumentException Thrown if the listener is {@code null}
	 */
	boolean unregisterFileReloadListener(FileReloadListener l) throws IllegalArgumentException;
	
	/**
	 * Reloads all listeners with the given file
	 * @param file The file to reload 
	 * @throws IllegalArgumentException Thrown if the plugin file is {@code null}
	 */
	void reloadFileReloadListeners(PluginFile file) throws IllegalArgumentException;
	
	/**
	 * Registers a storable manager, overrides the old one if exist
	 * @param manager The manager to register
	 * @param file The involved plugin file
	 * @return True if the registration was successful, else false (already registered)
	 * @throws IllegalArgumentException Thrown if the manager or the plugin file is {@code null}
	 */
	boolean registerStorableManager(StorableManager<? extends Storable> manager, PluginFile file) throws IllegalArgumentException;
	
	/**
	 * Unregisters the listener of the given plugin file
	 * @param file The file to unregister
	 * @return True if a listener was registered, else false
	 * @throws IllegalArgumentException Thrown if the file is {@code null}
	 */
	boolean unregisterStorableManager(PluginFile file) throws IllegalArgumentException;
	
	/**
	 * Returns the registered manager for the file
	 * @param file The file to get the manager
	 * @return The registered manager, null if no listener is registered
	 * @throws IllegalArgumentException Thrown if the file is {@code null}
	 */
	StorableManager<? extends Storable> getStorableManager(PluginFile file) throws IllegalArgumentException;
	
	/**
	 * Reloads the storable manager for the given file
	 * @param file The file
	 * @return True if reload was successful, else false (e.g. exception while loading or no manager registered)
	 * @throws IllegalArgumentException
	 */
	boolean reloadStorableManager(PluginFile file) throws IllegalArgumentException;
	
}

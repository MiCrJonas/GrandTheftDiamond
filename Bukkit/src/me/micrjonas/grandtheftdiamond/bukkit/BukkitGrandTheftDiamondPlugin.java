package me.micrjonas.grandtheftdiamond.bukkit;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;

import me.micrjonas.grandtheftdiamond.ConfigInizializer;
import me.micrjonas.grandtheftdiamond.GameManager;
import me.micrjonas.grandtheftdiamond.GrandTheftDiamond;
import me.micrjonas.grandtheftdiamond.Team;
import me.micrjonas.grandtheftdiamond.api.ListenerLoader;
import me.micrjonas.grandtheftdiamond.api.event.cause.LeaveReason;
import me.micrjonas.grandtheftdiamond.chat.ChatManager;
import me.micrjonas.grandtheftdiamond.command.CommandBan;
import me.micrjonas.grandtheftdiamond.command.CommandChangelog;
import me.micrjonas.grandtheftdiamond.command.CommandCreate;
import me.micrjonas.grandtheftdiamond.command.CommandEco;
import me.micrjonas.grandtheftdiamond.command.CommandExecutor;
import me.micrjonas.grandtheftdiamond.command.CommandFind;
import me.micrjonas.grandtheftdiamond.command.CommandGang;
import me.micrjonas.grandtheftdiamond.command.CommandGive;
import me.micrjonas.grandtheftdiamond.command.CommandHelp;
import me.micrjonas.grandtheftdiamond.command.CommandHouse;
import me.micrjonas.grandtheftdiamond.command.CommandInfo;
import me.micrjonas.grandtheftdiamond.command.CommandJail;
import me.micrjonas.grandtheftdiamond.command.CommandJoin;
import me.micrjonas.grandtheftdiamond.command.CommandKick;
import me.micrjonas.grandtheftdiamond.command.CommandLanguage;
import me.micrjonas.grandtheftdiamond.command.CommandLeave;
import me.micrjonas.grandtheftdiamond.command.CommandList;
import me.micrjonas.grandtheftdiamond.command.CommandMoney;
import me.micrjonas.grandtheftdiamond.command.CommandObjects;
import me.micrjonas.grandtheftdiamond.command.CommandPay;
import me.micrjonas.grandtheftdiamond.command.CommandReload;
import me.micrjonas.grandtheftdiamond.command.CommandSavedata;
import me.micrjonas.grandtheftdiamond.command.CommandSetjail;
import me.micrjonas.grandtheftdiamond.command.CommandSetsafe;
import me.micrjonas.grandtheftdiamond.command.CommandSetspawn;
import me.micrjonas.grandtheftdiamond.command.CommandSign;
import me.micrjonas.grandtheftdiamond.command.CommandUnban;
import me.micrjonas.grandtheftdiamond.command.CommandUpdate;
import me.micrjonas.grandtheftdiamond.command.CommandWand;
import me.micrjonas.grandtheftdiamond.command.TabCompleter;
import me.micrjonas.grandtheftdiamond.data.FileManager;
import me.micrjonas.grandtheftdiamond.data.FileReloadListener;
import me.micrjonas.grandtheftdiamond.data.PluginData;
import me.micrjonas.grandtheftdiamond.data.PluginFile;
import me.micrjonas.grandtheftdiamond.data.TemporaryPluginData;
import me.micrjonas.grandtheftdiamond.data.player.PlayerDataUser;
import me.micrjonas.grandtheftdiamond.data.storage.Storable;
import me.micrjonas.grandtheftdiamond.data.storage.StorableManager;
import me.micrjonas.grandtheftdiamond.data.storage.Storables;
import me.micrjonas.grandtheftdiamond.gang.GangManager;
import me.micrjonas.grandtheftdiamond.house.HouseManager;
import me.micrjonas.grandtheftdiamond.item.pluginitem.ItemManager;
import me.micrjonas.grandtheftdiamond.item.pluginitem.Knife;
import me.micrjonas.grandtheftdiamond.jail.JailManager;
import me.micrjonas.grandtheftdiamond.listener.BlockBreakListener;
import me.micrjonas.grandtheftdiamond.listener.CarEventListener;
import me.micrjonas.grandtheftdiamond.listener.ChatListener;
import me.micrjonas.grandtheftdiamond.listener.CommandListener;
import me.micrjonas.grandtheftdiamond.listener.MobDeathListener;
import me.micrjonas.grandtheftdiamond.listener.MobSpawnListener;
import me.micrjonas.grandtheftdiamond.listener.SignListener;
import me.micrjonas.grandtheftdiamond.listener.TestListener;
import me.micrjonas.grandtheftdiamond.listener.player.PlayerDamageListener;
import me.micrjonas.grandtheftdiamond.listener.player.PlayerInteractListener;
import me.micrjonas.grandtheftdiamond.listener.player.PlayerInventoryListener;
import me.micrjonas.grandtheftdiamond.listener.player.PlayerJoinServerListener;
import me.micrjonas.grandtheftdiamond.listener.player.PlayerMoveListener;
import me.micrjonas.grandtheftdiamond.listener.player.PlayerQuitServerListener;
import me.micrjonas.grandtheftdiamond.listener.player.PlayerRegenerateAndHungerListener;
import me.micrjonas.grandtheftdiamond.listener.player.PlayerTeleportListener;
import me.micrjonas.grandtheftdiamond.manager.EconomyManager;
import me.micrjonas.grandtheftdiamond.manager.NametagManager;
import me.micrjonas.grandtheftdiamond.messenger.LanguageManager;
import me.micrjonas.grandtheftdiamond.messenger.Messenger;
import me.micrjonas.grandtheftdiamond.messenger.NoPermissionType;
import me.micrjonas.grandtheftdiamond.onlygtdmode.OnlyGTDModeManager;
import me.micrjonas.grandtheftdiamond.rob.RobManager;
import me.micrjonas.grandtheftdiamond.sign.SignManager;
import me.micrjonas.grandtheftdiamond.sign.SignUpdater;
import me.micrjonas.grandtheftdiamond.stats.board.StatsBoardManager;
import me.micrjonas.grandtheftdiamond.updater.ChangeLog;
import me.micrjonas.grandtheftdiamond.updater.DataConverter;
import me.micrjonas.grandtheftdiamond.updater.Updater;
import me.micrjonas.util.Version;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;
import org.mcstats.Metrics;
import org.mcstats.Metrics.Graph;
import org.mcstats.Metrics.Plotter;

/**
 * The plugin's main class
 */
public class BukkitGrandTheftDiamondPlugin extends JavaPlugin {
	
//Start of static
	private static BukkitGrandTheftDiamondPlugin instance = null;
	
	/**
	 * Returns the plugin instance if loaded. The plugin may be disabled by the bukkit's {@link PluginManager}
	 * @return The loaded plugin instance, loaded by the bukkit's class loader. Never {@code null}
	 * @throws IllegalStateException Thrown if the plugin is not loaded by the bukkit's plugin manager
	 */
	public static BukkitGrandTheftDiamondPlugin getInstance() throws IllegalStateException {
		if (instance == null) {
			throw new IllegalStateException("Plugin not loaded");
		}
		return instance;
	}
//End of static
	
	private final Map<Class<? extends Listener>, Listener> listeners = new HashMap<>();
	private final Map<String, CommandExecutor> commands = new HashMap<>();
	private final Set<PlayerDataUser> playerDataUser = new HashSet<>();
	private final Set<FileReloadListener> fileReloadListener = new HashSet<>();
	private final Map<PluginFile, StorableManager<? extends Storable>> storableManager = new HashMap<>();
	private final Version version = new Version(getDescription().getVersion());
	private final ChangeLog changeLog = new ChangeLog();
	private final Set<String> playPermissions = new HashSet<>();
	private boolean serverFullyStarted = false;
	
	@Override
	public void onEnable() {
		if (instance != getServer().getPluginManager().getPlugin(getDescription().getName())) {
			instance = this;
		}
		playPermissions.add("gta.arrest");
		playPermissions.add("gta.corrupt");
		playPermissions.add("gta.detain");
		playPermissions.add("gta.find");
		playPermissions.add("gta.gang.accept");
		playPermissions.add("gta.gang.create");
		playPermissions.add("gta.gang.delete.own");
		playPermissions.add("gta.gang.info");
		playPermissions.add("gta.gang.invite");
		playPermissions.add("gta.gang.list");
		playPermissions.add("gta.gang.options");
		playPermissions.add("gta.house.buy");
		playPermissions.add("gta.house.sell");
		playPermissions.add("gta.job.*");
		playPermissions.add("gta.join.command");
		playPermissions.add("gta.leave.command");
		playPermissions.add("gta.list");
		playPermissions.add("gta.mission.*");
		playPermissions.add("gta.money.*");
		playPermissions.add("gta.pay");
		playPermissions.add("gta.stats.show");
		playPermissions.add("gta.use.*");
		new ConfigInizializer();
		FileManager.getInstance().loadOnlinePlayerData();
		DataConverter.convertAll(true);

		ChatManager.getInstance();
		//DynmapMarkerManager.getInstance();
		EconomyManager.getInstance();
		GangManager.getInstance();
		HouseManager.getInstance();
		ItemManager.getInstance();
		JailManager.getInstance();
		LanguageManager.getInstance();
		PluginData.getInstance();
		RobManager.getInstance();
		SignManager.getInstance();
		SignUpdater.getInstance();
		StatsBoardManager.getInstance();
		
		loadClasses();
		registerCommands();
		startMetrics();
		startCompassUpdater();
		startAutoSave();
		startUpdater();
		
		try {
			File folder = new File(getDataFolder() + File.separator + "listeners");
			folder.mkdir();
			ListenerLoader loader = new ListenerLoader(getClassLoader(), folder.toURI().toURL());
			loader.loadListeners();
			try {
				loader.close();
			}
			catch (IOException e) {
				e.printStackTrace();
			}
		} 
		catch (MalformedURLException e) {
			e.printStackTrace();
		}
		
		allFilesReloaded();
		SignUpdater.getInstance().updateAllSigns();
		serverFullyStarted = true;
		/*Arena arena = new Arena("name", ArenaType.CYLINDER, 
			new SquareLocation(-50, -50), null, Bukkit.getWorld("world_11"), 40);
		arena.setDefaultSpawn(new Location(null, 0, 20, 0));
		DynmapMarkerManager.getInstance().updateArena(arena);
				
		Arena arena2 = new Arena("name2", ArenaType.CUBOID, 
			new SquareLocation(200, 200), new SquareLocation(300, 300), Bukkit.getWorld("world_11"), -1);
		arena2.setDefaultSpawn(new Location(null, 150, 20, 150));
		DynmapMarkerManager.getInstance().updateArena(arena2);*/
		GrandTheftDiamond.runTaskAsynchronously(new Runnable() {
			@Override
			public void run() {
				try {
					changeLog.updateChangeLog();
				}
				catch (IllegalStateException ex) { }
			}
		});
		getLogger().info("Plugin enabled");
	}

	@Override
	public void onDisable() {
		boolean kickedSomeone = false;
		for (Player p : TemporaryPluginData.getInstance().getIngamePlayers()) {
				GameManager.getInstance().leaveGame(p, LeaveReason.PLUGIN_DISABLE);
				kickedSomeone = true;
		}
		
		if (kickedSomeone) {
			getLogger().info("All players were kicked out of the game");
		}
		NametagManager.getInstance().removeGtdTeams();
		FileManager.getInstance().saveAllDataFiles();
		FileManager.getInstance().unloadAllPlayerData();
		getLogger().info("Data saved");
		getLogger().info("Plugin disabled");
	}
	
	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String alias, String[] args) {
		if (serverFullyStarted) {
			if (args.length > 0) {
				CommandExecutor executor = getCommandExecutor(args[0].toLowerCase());
				if (executor != null) {
					String[] oldArgs = new String[args.length];
					for (int i = 0; i < args.length; i++) {
						oldArgs[i] = args[i];
						args[i] = args[i].toLowerCase();
					}
					executor.onCommand(sender, alias, args, oldArgs);
				}
				else {
					GrandTheftDiamond.getMessenger().sendPluginMessage(sender, "noCommand");
					GrandTheftDiamond.getMessenger().sendRightUsage(sender, alias, "? [" + GrandTheftDiamond.getMessenger().getPluginWord("page") + "]");
				}
			}
			else {
				GrandTheftDiamond.getMessenger().sendRightUsage(sender, alias, "? [" + GrandTheftDiamond.getMessenger().getPluginWord("page") + "]");
			}
		}
		else {
			sender.sendMessage("[GrandTheftDiamond] Please wait until the server is fully started.");
		}
		return true;
	}
	
	@Override
	public List<String> onTabComplete(CommandSender sender, Command cmd, String alias, String[] args) {
		args[0] = args[0].toLowerCase();
		Collection<String> completions = null;
		if (args.length == 1) {
			completions = getRegisteredCommands();
		}
		else {
			CommandExecutor executor = getCommandExecutor(args[0]);
			if (executor != null && executor instanceof TabCompleter) {
				for (int i = 1; i < args.length; i++) {
					args[i] = args[i].toLowerCase();
				}
				completions = (((TabCompleter) executor).onTabComplete(sender, args));
			}
			if (completions == null) {
				return new ArrayList<>(0);
			}
		}
		List<String> toReturn = new ArrayList<>(completions.size());
		if (args[0].length() > 0 || args.length == 1) {
			int last = args.length - 1;
			for (String completion : completions) {
				if (completion.startsWith(args[last])) {
					toReturn.add(completion);
				}
			}	
		}
		Collections.sort(toReturn);
		return toReturn;
	}
	
	@Override
	public FileConfiguration getConfig() {
		return FileManager.getInstance().getFileConfiguration(PluginFile.CONFIG);
	}
	
	@Override
	public void reloadConfig() {
		FileManager.getInstance().reloadFile(PluginFile.CONFIG);
	}
	
	@Override
	public void saveConfig() {
		FileManager.getInstance().saveFileConfiguration(PluginFile.CONFIG);
	}
	
	@Override
	public void saveDefaultConfig() {
		saveConfig();
	}

	//Extended visibility
	@Override
	public File getFile() {
		return super.getFile();
	}
	
	/**
	 * Checks whether the sender has the given permissions.
	 * All checked permissions start with "gta.". If {@code perm} does not start with it, the method will add it
	 * @param sender The involved CommandSender
	 * @param perm The permission
	 * @return True if the player have the given permission, else false
	 */
	public boolean checkPermission(CommandSender sender, String perm) {
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
				else if ((!checkDirectPermission(sender, perm, hasPlayPermission) && sender.isPermissionSet(perm) && i == splitPerm.length) || 
						(!checkDirectPermission(sender, perm + "\\.\\*", hasPlayPermission) && sender.isPermissionSet(perm + "\\.\\*"))) {
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
	public boolean checkPermission(CommandSender sender, String perm, boolean sendNoPermissionMessage, NoPermissionType type) {
		if (checkPermission(sender, perm)) {
			return true;
		}
		if (sendNoPermissionMessage) {
			Messenger.getInstance().sendNoPermissionsMessage(sender, type, perm);
		}
		return false;
	}
	
	private boolean checkDirectPermission(CommandSender sender, String perm, boolean hasPlayPermission) {
		return (hasPlayPermission && playPermissions.contains(perm)) || sender.hasPermission(perm);
	}
	
	/**
	 * Returns the command executor of the command
	 * @param name The name of the command, ignores case sensitive
	 * @return The registered command executor, null if the command is not registered
	 * @throws IllegalArgumentException Thrown if the command name is {@code null}
	 */
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
	public Set<String> getRegisteredCommands() {
		return Collections.unmodifiableSet(commands.keySet());
	}
	
	/**
	 * Checks whether a command name is registered
	 * @param name The name of the command, ignores case sensitive
	 * @return True if the command is registered, else false
	 * @throws IllegalArgumentException Thrown if the command name is {@code null}
	 */
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
	@Deprecated
	public synchronized void fileReloaded(PluginFile file) {
		reloadFileReloadListeners(file);
		reloadStorableManager(file);
	}
	
	/**
	 * Gets called when all files were reloaded. Reloads all file reload listeners and storable managers
	 * @deprecated Do not call
	 */
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
	
	/**
	 * Returns the change log of Grand Theft Diamond
	 * @return The plugin's change log
	 */
	public ChangeLog getChangeLog() {
		return changeLog;
	}
	
	/**
	 * Returns the loaded instance of {@code clazz}
	 * @param clazz The class of the registered org.bukkit.event.Listener
	 * @return The loaded instance of {@code clazz}
	 */
	@SuppressWarnings("unchecked")
	public <T extends Listener> T getRegisteredListener(Class<T> clazz) {
		return (T) listeners.get(clazz);
	}
	
	/**
	 * Returns the version of the plugin
	 * @return The version of the plugin
	 */
	public Version getVersion() {
		return version;
	}
	
	/**
	 * Copies a resource from the JAR file to a file outside of the JAR file
	 * @param from The path of the resource in the JAR file. Relative to the JAR file root
	 * @param to The file the resource should be copied to. If the file does not exist, the method will create a new one
	 * @param override Set to {@code true} if the method can override an existing file. If {@code false}, the method will end if the {@code to} exists
	 * @return True if copying was successful or the file already exists, else false
	 */
	public boolean copyFromJar(String from, File to, boolean override) {
		if (override || !to.exists()) {
			try {
				if (!to.exists()) {
					to.getParentFile().mkdir();
					to.createNewFile();
				}
				InputStream in = getResource(from);
				if (in == null) {
					getLogger().log(Level.WARNING, "Failed to copy resource '" + from + "' to '" + to + "': Resource not found");
					return false;
				}
				OutputStream out = new FileOutputStream(to);
				byte[] buf = new byte[1024];
				int len;
				while ((len = in.read(buf)) > 0) {
					out.write(buf, 0, len);
				}
				out.close();
				in.close();
			}
			catch (IOException ex) {
				getLogger().log(Level.SEVERE, "Failed to copy '" + to + "' from plugin resources:", ex);
				ex.printStackTrace();
			}
		}
		return true;
	}
	
	private void loadClasses() {

	//Listener
		registerSaveListener(new BlockBreakListener());
		registerSaveListener(new CarEventListener());
		registerSaveListener(new ChatListener());
		registerSaveListener(new CommandListener());
		// registerSave(new DealerInventoryListener());
		registerSaveListener(new MobDeathListener());
		registerSaveListener(new MobSpawnListener());
		registerSaveListener(new PlayerDamageListener());
		registerSaveListener(new PlayerInteractListener());
		registerSaveListener(new PlayerInventoryListener());
		registerSaveListener(new PlayerJoinServerListener());
		registerSaveListener(new PlayerMoveListener());
		registerSaveListener(new PlayerRegenerateAndHungerListener());
		registerSaveListener(new PlayerTeleportListener());
		registerSaveListener(new PlayerQuitServerListener());
		registerSaveListener(new SignListener());
		registerSaveListener(new TestListener());

	//Objects
		new Knife();
		
	//Manager
		new OnlyGTDModeManager();
	}
	
	/**
	 * Registers a {@code Listener} used by the plugin to get it with {@link BukkitGrandTheftDiamondPlugin#getRegisteredListener(Class)}.
	 * 	Also registers the {@code Listener} to the bukkit's {@link PluginManager}
	 * @param l The listener to register
	 * @throws IllegalArgumentException Thrown if {@code l} is {@code null}
	 * @throws IllegalStateException Thrown if a {@code Listener} with the same class of {@code l} is already registered
	 */
	public void registerListener(Listener l) throws IllegalArgumentException, IllegalStateException {
		if (l == null) {
			throw new IllegalArgumentException("Listener to register cannot be null");
		}
		if (listeners.containsKey(l.getClass())) {
			throw new IllegalStateException("A listener of class '" + l.getClass() + "' is already registered");
		}
		registerSaveListener(l);
	}
	
	private void registerSaveListener(Listener l) {
		listeners.put(l.getClass(), l);
		getServer().getPluginManager().registerEvents(l, this);
	}
	
	private void registerCommands() {
		registerCommand(new CommandBan(), "ban");
		registerCommand(new CommandChangelog(), "changelog");
		//registerCommand(new CommandChat(), "chat");
		registerCommand(new CommandCreate(), "create");
		//registerCommand(new CommandDealer(), "dealer");
		registerCommand(new CommandEco(), "eco");
		registerCommand(new CommandFind(), "find");
		registerCommand(new CommandGang(), "gang");
		registerCommand(new CommandGive(), "give");
		registerCommand(new CommandHelp(), "help", "?");
		registerCommand(new CommandHouse(), "house");
		registerCommand(new CommandInfo(), "info");
		registerCommand(new CommandJail(), "jail");
		registerCommand(new CommandJoin(), "join", "j");
		registerCommand(new CommandKick(), "kick");
		registerCommand(new CommandLanguage(), "language");
		registerCommand(new CommandLeave(), "leave", "l");
		registerCommand(new CommandList(), "list");
		registerCommand(new CommandMoney(), "money");
		registerCommand(new CommandObjects(), "objects");
		registerCommand(new CommandPay(), "pay");
		registerCommand(new CommandReload(), "reload");
		registerCommand(new CommandSavedata(), "savedata");
		registerCommand(new CommandSetjail(), "setjail");
		registerCommand(new CommandSetsafe(), "setsafe");
		registerCommand(new CommandSetspawn(), "setspawn");
		registerCommand(new CommandSign(), "sign");
		//registerCommand(new CommandStats(), "stats");
		registerCommand(new CommandUnban(), "unban");
		registerCommand(new CommandUpdate(), "update");
		registerCommand(new CommandWand(), "wand");
	}
	
	private void startCompassUpdater() {
		int updateRate = getConfig().getInt("compassUpdateRate");
		GrandTheftDiamond.scheduleRepeatingTask(new Runnable() {
			@Override
			public void run() {
				for (Player p : TemporaryPluginData.getInstance().getIngamePlayers()) {
					if (TemporaryPluginData.getInstance().hasTargetPlayer(p)) {
						p.setCompassTarget(TemporaryPluginData.getInstance().getTargetPlayer(p).getLocation());	
					}
					else {
						p.setCompassTarget(p.getWorld().getSpawnLocation());
					}
				}
			}
		}, 0, updateRate > 0 ? updateRate : 10, TimeUnit.SECONDS);
	}
	
	private void startAutoSave() {
		int intervall = getConfig().getInt("autoSaveIntervall");
		if (intervall >= 1) {
			GrandTheftDiamond.scheduleRepeatingTask(new Runnable() {
				@Override
				public void run() {
					FileManager.getInstance().saveAllDataFiles();
					getLogger().info("Data saved!");
				}
			}, intervall, intervall, TimeUnit.SECONDS);
		}
	}
	
	private void startMetrics() {
		Metrics metrics;
		try {
			metrics = new Metrics(this);
		} 
		catch (IOException ex) {
			getLogger().warning("Error Submitting stats! Server or McStats offline?");
			return;
		}
		Graph playersIngame = metrics.createGraph("Players ingame");
		for (final Team team : Team.values()) {
			if (team.isRealTeam()) {
				playersIngame.addPlotter(new Plotter(team.name().substring(0, 1) + team.name().substring(1).toLowerCase()) {
					@Override
					public int getValue() {
						int count = 0;
						for (Player p : TemporaryPluginData.getInstance().getIngamePlayers()) {
							if (PluginData.getInstance().getTeam(p) == team) {
								count++;
							}
						}
						return count;
					}
				});
			}
		}
		metrics.start();
	}
	
	private void startUpdater() {
		GrandTheftDiamond.runTaskAsynchronously(new Runnable() {
			@Override
			public void run() {
				if (getConfig().getBoolean("useUpdater")) {
					Updater.updateUpdater();
					if (Updater.updateAvailable()) {
						getLogger().info("New update available (" + Updater.getUpdateVersionName() + ")");
						getLogger().info("Use '/gtd update' to download");	
					}
				}
				else {
					getLogger().info("Updater is disabled");
				}
			}
		});
	}

}

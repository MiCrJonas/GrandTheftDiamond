package me.micrjonas.grandtheftdiamond;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import java.util.logging.Logger;

import me.micrjonas.grandtheftdiamond.arena.ArenaManager;
import me.micrjonas.grandtheftdiamond.bukkit.BukkitGrandTheftDiamondPlugin;
import me.micrjonas.grandtheftdiamond.command.CommandExecutor;
import me.micrjonas.grandtheftdiamond.data.FileManager;
import me.micrjonas.grandtheftdiamond.data.FileReloadListener;
import me.micrjonas.grandtheftdiamond.data.PluginData;
import me.micrjonas.grandtheftdiamond.data.PluginFile;
import me.micrjonas.grandtheftdiamond.data.TemporaryPluginData;
import me.micrjonas.grandtheftdiamond.data.player.PlayerDataUser;
import me.micrjonas.grandtheftdiamond.data.storage.Storable;
import me.micrjonas.grandtheftdiamond.data.storage.StorableManager;
import me.micrjonas.grandtheftdiamond.gang.GangManager;
import me.micrjonas.grandtheftdiamond.house.HouseManager;
import me.micrjonas.grandtheftdiamond.item.pluginitem.ItemManager;
import me.micrjonas.grandtheftdiamond.jail.JailManager;
import me.micrjonas.grandtheftdiamond.manager.ChatManager;
import me.micrjonas.grandtheftdiamond.manager.EconomyManager;
import me.micrjonas.grandtheftdiamond.manager.NametagManager;
import me.micrjonas.grandtheftdiamond.messenger.LanguageManager;
import me.micrjonas.grandtheftdiamond.messenger.Messenger;
import me.micrjonas.grandtheftdiamond.messenger.NoPermissionType;
import me.micrjonas.grandtheftdiamond.rob.RobManager;
import me.micrjonas.grandtheftdiamond.sign.SignManager;
import me.micrjonas.grandtheftdiamond.updater.ChangeLog;
import me.micrjonas.grandtheftdiamond.updater.Version;

import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;

/**
 * Basic methods for the plugin's API. This class only contains static methods
 */
@SuppressWarnings("javadoc")
public final class GrandTheftDiamond {
	
	private GrandTheftDiamond() { /* Private constructor, no instance possible */}

	/**
	 * Returns the loaded plugin instance
	 * @return The loaded plugin instance
	 * @throws IllegalStateException Thrown if the plugin is not loaded by the bukkit's plugin manager
	 * @see BukkitGrandTheftDiamondPlugin#getInstance()
	 */
	public static BukkitGrandTheftDiamondPlugin getPlugin() throws IllegalStateException {
		return BukkitGrandTheftDiamondPlugin.getInstance();
	}
	
	/**
	 * Returns a list of all currently logged in players
	 * @return A list of Players that are currently online. The Collection is immutable
	 */
	@SuppressWarnings("deprecation")
	public static Collection<Player> getOnlinePlayers() {
		return Arrays.asList(Bukkit.getOnlinePlayers());
	}
	
	/**
	 * Returns a list of all names of currently logged in players
	 * @return A list of Player's names that are currently online
	 */
	public static Collection<String> getOnlinePlayerNames() {
		return getPlayerNames(getOnlinePlayers());
	}
	
	/**
	 * Returns a list with the names of {@code players}
	 * @param players A Collection with the names of the {@code players}
	 * @return A list with the names of players
	 */
	public static Collection<String> getPlayerNames(Collection<? extends OfflinePlayer> players) {
		List<String> names = new ArrayList<>(players.size());
		for (OfflinePlayer p : players) {
			names.add(p.getName());
		}
		return names;
	}
	
	/**
	 * Returns a List with the UUID of every given player as string
	 * @param players The players to add
	 * @return A new List with the UUIDs of every given player
	 */
	public static List<String> getPlayerUniqueIds(Collection<? extends OfflinePlayer> players) {
		List<String> ids = new ArrayList<>(players.size());
		for (OfflinePlayer p : players) {
			ids.add(p.getUniqueId().toString());
		}
		return ids;
	}
	
	/**
	 * Returns the plugin's version
	 * @return e Plugin's version
	 */
	public static Version getVersion() {
		return getPlugin().getVersion();
	}
	
	/**
	 * Returns the plugin's logger
	 * @return The plugin's logger
	 */
	public static Logger getLogger() {
		return getPlugin().getLogger();
	}
	
	/**
	 * Equivalent to {@link BukkitGrandTheftDiamondPlugin#getDataFolder()}
	 */
	public static File getDataFolder() {
		return getPlugin().getDataFolder();
	}
	
	/**
	 * Equivalent to {@link BukkitGrandTheftDiamondPlugin#registerFileReloadListener(FileReloadListener)}
	 */
	public static boolean registerFileReloadListener(FileReloadListener l) throws IllegalArgumentException {
		return getPlugin().registerFileReloadListener(l);
	}
	
	/**
	 * Equivalent to {@link BukkitGrandTheftDiamondPlugin#unregisterFileReloadListener(FileReloadListener)}
	 */
	public static boolean unregisterFileReloadListener(FileReloadListener l) throws IllegalArgumentException {
		return getPlugin().unregisterFileReloadListener(l);
	}
	
	/**
	 * Equivalent to {@link BukkitGrandTheftDiamondPlugin#reloadFileReloadListeners(PluginFile)}
	 */
	public static void reloadFileReloadListeners(PluginFile file) {
		getPlugin().reloadFileReloadListeners(file);
	}
	
	/**
	 * Equivalent to {@link BukkitGrandTheftDiamondPlugin#registerPlayerDataUser(PlayerDataUser)}
	 */
	public static void registerPlayerDataUser(PlayerDataUser user) {
		getPlugin().registerPlayerDataUser(user);
	}
	
	/**
	 * Equivalent to {@link BukkitGrandTheftDiamondPlugin#registerStorableManager(StorableManager, PluginFile)}
	 */
	public static <T extends Storable> void registerStorableManager(StorableManager<? extends Storable> manager, PluginFile file) {
		getPlugin().registerStorableManager(manager, file);
	}
	
	/**
	 * Equivalent to {@link BukkitGrandTheftDiamondPlugin#unregisterStorableManager(PluginFile)}
	 */
	public static void unregisterStorableManager(PluginFile file) {
		getPlugin().unregisterStorableManager(file);
	}
	
	/**
	 * Returns the plugin's arena manager
	 * @return The plugin's arena manager
	 * @see ArenaManager#getInstance()
	 */
	public ArenaManager getArenaManager() {
		return ArenaManager.getInstance();
	}
	
	/**
	 * Returns the plugin's chat manager
	 * @return The plugin's chat manager
	 * @see ChatManager#getInstance()
	 */
	public static ChatManager getChatManager() {
		return ChatManager.getInstance();
	}
	
	/**
	 * Returns the plugin's economy manager
	 * @return The plugin's economy manager
	 * @see EconomyManager#getInstance()
	 */
	public static EconomyManager getEconomyManager() {
		return EconomyManager.getInstance();
	}
	
	/**
	 * Returns the plugin's file manager
	 * @return The plugin's file manager
	 * @see FIleManager#getInstance()
	 */
	public static FileManager getFileManager() {
		return FileManager.getInstance();
	}
	
	/**
	 * Returns the plugin's game manager
	 * @return The plugin's game manager
	 * @see GameManager#getInstance()
	 */
	public static GameManager getGameManager() {
		return GameManager.getInstance();
	}
	
	/**
	 * Returns the plugin's gang manager
	 * @return The plugin's gang manager
	 * @see GangManager#getInstance()
	 */
	public static GangManager getGangManager() {
		return GangManager.getInstance();
	}
	
	/**
	 * Returns the plugin's house manager
	 * @return The plugin's house manager
	 * @see HouseManager#getInstance()
	 */
	public static HouseManager getHouseManager() {
		return HouseManager.getInstance();
	}
	
	/**
	 * Returns the plugin's jail manager
	 * @return The plugin's jail manager
	 * @see HouseManager#getInstance()
	 */
	public static JailManager getJailManager() {
		return JailManager.getInstance();
	}
	
	/**
	 * Returns the plugin's name tag manager
	 * @return The plugin's name tag manager
	 * @see NametagManager#getInstance()
	 */
	public static NametagManager getNametagManager() {
		return NametagManager.getInstance();
	}
	
	/**
	 * Returns the plugin's item manager
	 * @return The plugin's item manager
	 * @see PluginItemManager#getInstance()
	 */
	public static ItemManager getItemManager() {
		return ItemManager.getInstance();
	}
	
	/**
	 * Returns the plugin's rob manager
	 * @return The plugin's rob manager
	 * @see RobManager#getInstance()
	 */
	public static RobManager getRobManager() {
		return RobManager.getInstance();
	}
	
	/**
	 * Returns the plugin's sign manager
	 * @return The plugin's sign manager
	 * @see SignManager#getInstance()
	 */
	public static SignManager getSignManager() {
		return SignManager.getInstance();
	}
	
	/**
	 * Returns the plugin's messenger
	 * @return The plugin's messenger
	 * @see Messenger#getInstance()
	 */
	public static Messenger getMessenger() {
		return Messenger.getInstance();
	}
	
	/**
	 * Returns the plugin's language manager
	 * @return The plugin's language manager
	 * @see LanguageManager#getInstance()
	 */
	public static LanguageManager getLanguageManager() {
		return LanguageManager.getInstance();
	}

	/**
	 * Returns the plugin's data handle
	 * @return he plugin's data handle
	 * @see PluginData#getInstance()
	 */
	public static PluginData getPluginData() {
		return PluginData.getInstance();
	}
	
	/**
	 * Returns the plugin's data handle for temporary data 
	 * @return plugin's temporary data handle
	 * @see TemporaryPluginData#getInstance()
	 */
	public static TemporaryPluginData getTemporaryPluginData() {
		return TemporaryPluginData.getInstance();
	}
	
	/**
	 * Equivalent to {@link BukkitGrandTheftDiamondPlugin#getChangeLog()}
	 */
	public static ChangeLog getChangeLog() {
		return getPlugin().getChangeLog();
	}
	
	/**
	 * Equivalent to {@link BukkitGrandTheftDiamondPlugin#registerCommand(CommandExecutor, String, String...)}
	 */
	public static void registerCommand(CommandExecutor executor, String name, String... optionalAliases) throws IllegalArgumentException {
		getPlugin().registerCommand(executor, name, optionalAliases);
	}
	
	/**
	 * Equivalent to {@link BukkitGrandTheftDiamondPlugin#getCommandExecutor(String)}
	 */
	public static CommandExecutor getCommandExecutor(String name) throws IllegalArgumentException {
		return getPlugin().getCommandExecutor(name);
	}
	
	/**
	 * Equivalent to {@link BukkitGrandTheftDiamondPlugin#getRegisteredCommands()}
	 */
	public static Set<String> getRegisteredCommands() {
		return getPlugin().getRegisteredCommands();
	}
	
	/**
	 * Equivalent to {@link BukkitGrandTheftDiamondPlugin#isCommandRegistered(String)}
	 */
	public static boolean isCommendRegistered(String name) throws IllegalArgumentException {
		return getPlugin().isCommandRegistered(name);
	}
	
	/**
	 * Equivalent to {@link BukkitGrandTheftDiamondPlugin#checkPermission(CommandSender, String)}
	 */
	public static boolean checkPermission(CommandSender sender, String perm) {
		return getPlugin().checkPermission(sender, perm);
	}
	
	/**
	 * Equivalent to {@link BukkitGrandTheftDiamondPlugin#checkPermission(CommandSender, String, boolean, NoPermissionType)}
	 */
	public static boolean checkPermission(CommandSender sender, String perm, boolean sendNoPermissionMessage, NoPermissionType type) {
		return getPlugin().checkPermission(sender, perm, sendNoPermissionMessage, type);
	}
	
	/**
	 * Equivalent to {@link BukkitGrandTheftDiamondPlugin#registerListener(Listener)}
	 */
	public static void registerListener(Listener l) {
		getPlugin().registerListener(l);
	}

//SCHEDULER
	public static void runTask(Runnable task) {
		Bukkit.getScheduler().runTask(BukkitGrandTheftDiamondPlugin.getInstance(), task);
	}
	
	public static void runTaskAsynchronously(Runnable task) {
		Bukkit.getScheduler().runTaskAsynchronously(getPlugin(), task);
	}
	
	public static void cancelTask(int id) {
		Bukkit.getScheduler().cancelTask(id);
	}
	
//long
	public static void runTaskLater(Runnable task, long time, TimeUnit unit) {
		Bukkit.getScheduler().runTaskLater(getPlugin(), task, toTicks(time, unit));
	}
	
	public static int scheduleSyncDelayedTask(Runnable task, long time, TimeUnit unit) {
		return Bukkit.getScheduler().scheduleSyncDelayedTask(getPlugin(), task, toTicks(time, unit));
	}
	
	public static int scheduleRepeatingTask(Runnable task, long toStart, long periode, TimeUnit unit) {
		return Bukkit.getScheduler().scheduleSyncRepeatingTask(getPlugin(), task, toTicks(toStart, unit), toTicks(periode, unit));
	}
	
//double
	public static void runTaskLater(Runnable task, double time, TimeUnit unit) {
		Bukkit.getScheduler().runTaskLater(getPlugin(), task, toTicks(time, unit));
	}
	
	public static int scheduleSyncDelayedTask(Runnable task, double time, TimeUnit unit) {
		return Bukkit.getScheduler().scheduleSyncDelayedTask(getPlugin(), task, toTicks(time, unit));
	}
	
	public static int scheduleRepeatingTask(Runnable task, double toStart, double periode, TimeUnit unit) {
		return Bukkit.getScheduler().scheduleSyncRepeatingTask(getPlugin(), task, toTicks(toStart, unit), toTicks(periode, unit));
	}
	
	private static long toTicks(double time, TimeUnit unit) {
		return TimeUnit.SECONDS.convert((long) (time * 20), unit);
	}
	
	private static long toTicks(long time, TimeUnit unit) {
		return TimeUnit.SECONDS.convert(time, unit) * 20;
	}
	
}

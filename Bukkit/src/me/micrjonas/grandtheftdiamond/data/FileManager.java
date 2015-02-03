package me.micrjonas.grandtheftdiamond.data;

import java.io.File;
import java.io.IOException;
import java.nio.file.Paths;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.EnumMap;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.UUID;
import java.util.logging.Level;

import me.micrjonas.grandtheftdiamond.GrandTheftDiamond;
import me.micrjonas.grandtheftdiamond.GrandTheftDiamondPlugin;
import me.micrjonas.grandtheftdiamond.data.storage.Storable;
import me.micrjonas.grandtheftdiamond.updater.DataConverter;
import net.minecraft.util.com.google.common.io.Files;

import org.bukkit.OfflinePlayer;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;


/**
 * Manager of all files used by the plugin
 */
public class FileManager {
	
	private static FileManager instance = new FileManager();
	
	/**
	 * Returns the loaded instance
	 * @return The loaded instance
	 */
	public static FileManager getInstance() {
		return instance;
	}
	
	private static final String FILE_PREFIX = GrandTheftDiamond.getDataFolder() + File.separator;
	
	private final Map<UUID, FileConfiguration> playerData = new HashMap<>();
	private final Map<PluginFile, FileConfiguration> dataFiles = new EnumMap<>(PluginFile.class);
	
	private FileManager() {
		moveBeforeLoad();
		for (PluginFile type : PluginFile.values()) {
			reloadFile(type, true);
		}
		updateLocations();
		File listeners = new File(GrandTheftDiamond.getDataFolder() + File.separator + "listeners");
		if (!listeners.isDirectory()) { // Bug fix of last version
			listeners.delete();
		}
	}
	
	private void moveBeforeLoad() {
		moveFile(new File(FILE_PREFIX + "eventConfig.yml"), PluginFile.EVENT_CONFIG.getFile());
		moveFile(new File(FILE_PREFIX + "onlyGTDModeConfig.yml"), PluginFile.ONLY_GTD_MODE_CONFIG.getFile());
	}
	
	private void updateLocations() {
		moveData("houses", PluginFile.HOUSES, "", "", true);
		moveData("data", PluginFile.JAILS, "jails", "", false);
		moveData("data", PluginFile.GANGS, "gangs", "", false);
		moveData("data", PluginFile.SAFES, "safes", "", false);
		moveData("data", PluginFile.SIGNS, "signs", "", false);
		moveData("data", PluginFile.ARENA, "arena.spawns", "spawns", false);
		moveData("data", PluginFile.ARENA, "arena", "bounds", true);
	}
	
	private void moveData(String from, PluginFile to, String pathFrom, String pathTo, boolean deleteOld) {
		File fromFile = new File(GrandTheftDiamondPlugin.getInstance().getDataFolder() + File.separator + from + ".yml");
		if (!fromFile.exists()) {
			return;
		}
		FileConfiguration fromData = YamlConfiguration.loadConfiguration(fromFile);
		FileConfiguration toData = getFileConfiguration(to);
		if (fromData.isConfigurationSection(pathFrom)) {
			if (pathTo.length() > 0) {
					pathTo = pathTo + ".";
			}
			for (String path : fromData.getConfigurationSection(pathFrom).getKeys(false)) {
				toData.set(pathTo + path, fromData.get(pathFrom + "." + path));
			}
		}
		else if (pathFrom.length() > 0 && pathTo.length() > 0) {
			toData.set(pathTo, fromData.get(pathFrom));
		}
		else {
			return;
		}
		if (pathFrom.length() > 0) {
			fromData.set(pathFrom, null);
		}
		else if (fromData.isConfigurationSection("")) {
			for (String path : fromData.getConfigurationSection("").getKeys(false)) {
				fromData.set(path, null);
			}
		}
		saveConfiguration(toData, to.getFile());
		if (deleteOld) {
			fromFile.delete();
		}
		else {
			saveConfiguration(fromData, fromFile);
		}
	}
	
	/**
	 * Moves the data of File from to File to
	 * @param from The old File
	 * @param to The new File
	 * @return True if the moving was successfully, else false
	 */
	public boolean moveFile(File from, File to) {
		if (from.exists()) {
			try {
				Files.move(from, to);
				return true;
			} 
			catch (IOException e) {
				GrandTheftDiamondPlugin.getInstance().getLogger().warning("Could not move " + from.getName() + " to " + to.getName());
				e.printStackTrace();
			}
		}
		return false;
	}
	
	/**
	 * Creates file {@code f} if it does not exist
	 * @param f File to create
	 * @return True, if the creation was successfully, else false
	 */
	public boolean createFileIfNotExists(File f) {
		if (!f.exists()) {
			return createNewFile(f, true);
		}
		return true;
	}
	
	/**
	 * Creates a new file
	 * @param f File to create
	 * @param broadcast Set to true, if you want to broadcast the creation, else set to false
	 * @return True, if the creation was successfully, else false
	 */
	public boolean createNewFile(File f, boolean broadcast) {
		try {
			f.getParentFile().mkdirs();
			f.createNewFile();
			if (broadcast) {
				GrandTheftDiamondPlugin.getInstance().getLogger().info(f.getPath() + " created");
			}
			return true;
		}
		catch (IOException ex) {
			GrandTheftDiamondPlugin.getInstance().getLogger().warning("Could not create " + f.getPath());
			ex.printStackTrace();
			return false;
		}
	}
	
	/**
	 * Stores the FileConfiguration to File
	 * @param data The FileConfiguration which shall get stored
	 * @param f The data file
	 * @return True if the storing was successfully, else false
	 */
	public boolean saveConfiguration(FileConfiguration data, File f) {
		if (createFileIfNotExists(f)) {
			try {
				data.save(f);
				return true;
			} 
			catch (IOException e) {
				GrandTheftDiamondPlugin.getInstance().getLogger().warning("Could not save " + f.getName());
				e.printStackTrace();
			}
		}
		return false;
	}
	
	/**
	 * Stores the FileConfiguration of the file to the file
	 * @param type The file which shall get stored
	 * @return True if action was successfull, else {@code false}
	 */
	@SuppressWarnings("deprecation")
	public boolean saveFileConfiguration(PluginFile type) {
		GrandTheftDiamondPlugin.getInstance().saveData(type);
		return saveConfiguration(getFileConfiguration(type), type.getFile());
	}
	
	
	/**
	 * Reloads all plugin files
	 */
	public void reloadAllFiles() {
		
		for (PluginFile type : PluginFile.values())
			reloadFile(type);
		
	}
	
	
	/**
	 * Returns the FileCOnfiguration of the given file
	 * @param type The File
	 * @return The FileCOnfiguration of the given file
	 */
	public FileConfiguration getFileConfiguration(PluginFile type) {
		return dataFiles.get(type);
	}
	
	/**
	 * Loads the FileConfiguration of the File if the File exists
	 * @param file File to load
	 * @param jarFallback The file path inside the plugin's JAR file
	 * @param If the loading fails, the method loads the configuration from the fallback file in the plugin's jar file
	 * @return The loaded FileConfiguration of the given File
	 * @throws IllegalArgumentException Thrown if {@code file} or {@code jarFallback} is {@code null}
	 * @throws IllegalStateException Thrown if {@code file} does not exist
	 */
	public FileConfiguration getFileConfiguration(File file, String jarFallback) throws IllegalArgumentException, IllegalStateException {
		if (file == null) {
			throw new IllegalArgumentException("File is not allowed to be null");
		}
		if (!file.exists()) {
			throw new IllegalStateException("File does not exist");
		}
		FileConfiguration conf = new YamlConfiguration();
		try {
			byte[] bytes = java.nio.file.Files.readAllBytes(Paths.get(file.toString()));
			try {
				conf.loadFromString(new String(bytes));
			}
			catch (InvalidConfigurationException ex) {
				GrandTheftDiamond.getLogger().log(Level.SEVERE, "Failed to load configuration for '" + file + "': " + ex.getMessage());
				String fileDate = new SimpleDateFormat("MM_dd_yyyy HH_mm_ss").format(Calendar.getInstance().getTime());
				String newFileName = file + "-" + fileDate + ".broken";
				boolean renamed = file.renameTo(new File(newFileName));
				if (renamed) {
					GrandTheftDiamond.getLogger().log(Level.SEVERE, "The file was recreated. The broken file was moved to " + newFileName);
				}
				if (renamed && jarFallback != null) {
					GrandTheftDiamondPlugin.getInstance().copyFromJar(jarFallback, file, true);
					return getFileConfiguration(file, null);
				}
				else {
					new File(file.toString()).createNewFile();
				}
			}
			return conf;
		}
		catch (IOException ex) {
			GrandTheftDiamond.getLogger().log(Level.SEVERE, "Failed to load '" + file + "'", ex);
		}
		return conf;
	}
	
	/**
	 * Loads the FileConfiguration of the File if the File exists
	 * @param f File to load
	 * @return The loaded FileConfiguration of the given File
	 */
	public FileConfiguration getFileConfiguration(File f) {
		return getFileConfiguration(f, null);
	}
	
	@SuppressWarnings("deprecation")
	private void reloadFile(PluginFile type, boolean startup) {
		createFileIfNotExists(type.getFile());
		dataFiles.put(type, getFileConfiguration(type.getFile(), type.getJarPath()));
		if (!startup) {
			GrandTheftDiamondPlugin.getInstance().fileReloaded(type);
		}
	}
	
	
	/**
	 * Reloads the specific file and updates the file user
	 * @param type The type of the file
	 */
	public void reloadFile(PluginFile type) {
		
		reloadFile(type, false);
		
	}
	
	
	/**
	 * Saves all data FileConfigurations to its File
	 */
	public void saveAllDataFiles() {
		
		for (PluginFile file : PluginFile.values()) {
			
			if (file.isDataFile())
				saveFileConfiguration(file);
			
		}
		
	}
	
	
	/**
	 * Saves all FileConfigurations to its File
	 */
	public void saveAllFiles() {
		
		for (PluginFile file : PluginFile.values())
			saveFileConfiguration(file);
		
	}
	
	
	/**
	 * Returns a List of all names of the plugin files
	 * @return A List of all names of the plugin files
	 */
	public List<String> getAllFileNames() {
		
		List<String> files = new ArrayList<>();
		
		for (PluginFile file : PluginFile.values())
			files.add(file.getFileName().substring(0, file.getFileName().length() - 4));
		
		return files;
		
	}
	
	
	/**
	 * Loads the player data of the player with the the given UUID
	 * @param playerId UUID of the file
	 * @return The loaded player data represented as {@code FileConfiguration}
	 */
	public FileConfiguration loadPlayerData(UUID playerId) {
		File f = new File(GrandTheftDiamondPlugin.getInstance().getDataFolder() + "/userdata", playerId.toString() + ".yml");
		createFileIfNotExists(f);
		FileConfiguration config = YamlConfiguration.loadConfiguration(f);
		playerData.put(playerId, config);
		DataConverter.convertStats(playerData.get(playerId));
		return config;
	}
	
	/**
	 * Loads the player data of all online players from their user file
	 */
	public void loadOnlinePlayerData() {
		for (Player p : GrandTheftDiamond.getOnlinePlayers()) {
			loadPlayerData(p.getUniqueId());
		}
	}
	
	/**
	 * Loads the {@code Player}'s data from it's user file
	 * @param p The {@code Player} to load
	 */
	public void loadPlayerData(Player p) {
		loadPlayerData(p.getUniqueId());
	}
	
	/**
	 * Returns the FileConfiguration of the Player's data file
	 * @param player The player name to get the file
	 * @return The FileConfiguration of the Player's data file
	 */
	@SuppressWarnings("deprecation")
	public FileConfiguration getPlayerData(String player) {
		OfflinePlayer p = GrandTheftDiamondPlugin.getInstance().getServer().getOfflinePlayer(player);
		if (p.isOnline()) {
			return getPlayerData(p.getPlayer());
		}
		if (p.getUniqueId() != null) {
			File f = new File(GrandTheftDiamond.getDataFolder() + File.separator + "userdata" + File.separator + p.getUniqueId() + ".yml");
			if (f.exists()) {
				return YamlConfiguration.loadConfiguration(f);
			}
		}
		return null;
	}
	
	/**
	 * Returns the {@code FileConfiguration} of the {@code Player}'s data file. Loads it to the RAM, if not loaded
	 * @param playerId The {@code UUID} of the Player
	 * @return the {@code FileConfiguration} of the Player's data file
	 */
	public FileConfiguration getPlayerData(UUID playerId) {
		FileConfiguration data = playerData.get(playerId);
		if (data == null) {
			return loadPlayerData(playerId);
			//return YamlConfiguration.loadConfiguration(new File(GrandTheftDiamond.getDataFolder() + File.separator + "userdata" + File.separator + playerId + ".yml"));
		}
		return data;
		
	}
	
	/**
	 * Returns the FileConfiguration of the Player's data file
	 * @param p The player to get the file
	 * @return The FileConfiguration of the Player's data file
	 */
	public FileConfiguration getPlayerData(Player p) {
		return playerData.get(p.getUniqueId());
	}
	
	/**
	 * Unloads the {@code Player}'s data and saves it to it's user file
	 * @param p The player to store
	 * @see FileManager#unloadPlayerData(UUID)
	 */
	public void unloadPlayerData(Player p) {
		unloadPlayerData(p.getUniqueId());
	}
	
	/**
	 * Unloads the player data for the {@code Player} with the given {@code UUID}
	 * @param playerId The unique id of the {@code Player}
	 */
	public void unloadPlayerData(UUID playerId) {
		if (playerData.containsKey(playerId)) {
			File file = new File(GrandTheftDiamond.getDataFolder() + "/userdata", playerId.toString() + ".yml");
			if (!file.exists()) {
				try {
					file.createNewFile();
				}
				catch (IOException ex) { }
			}
			try {
				playerData.get(playerId).save(file);
			} 
			
			catch (IOException e) { }
			playerData.remove(playerId);
		}
	}
	
	
	/**
	 * Unloads all loaded player data and stores it to their user files
	 */
	public void unloadAllPlayerData() {
		for (UUID playerId : playerData.keySet()) {
			unloadPlayerData(playerId);
		}
	}
	
	/**
	 * Stores {@code toStore} in {@code file} at the given path
	 * @param file The file in what the {@link Storable} should be stored
	 * @param toStore The object to store
	 * @param path The path in the file
	 */
	public void store(FileConfiguration file, Storable toStore, String path) {
		for (Entry<String, Object> elem : toStore.getStoreData().entrySet()) {
			file.set(path + "." + elem.getKey(), elem.getValue());
		}
	}
	
	
	/**
	 * Stores {@code toStore} in {@code file} at the given path
	 * @param file The file in what the {@code Storable} should be stored
	 * @param toStore The object to store
	 * @param path The path in the file
	 * @see FileManager#store(FileConfiguration, Storable, String)
	 */
	public void store(PluginFile file, Storable toStore, String path) {
		store(getFileConfiguration(file), toStore, path);
	}
	
	/**
	 * Stores {@code toStore} in {@code file}. Uses the name {@code toStore.getName()} as super path
	 * @param file The file in what the {@code Storable} should be stored
	 * @param toStore The object to store
	 */
	public void store(FileConfiguration file, Storable toStore) {
		for (Entry<String, Object> elem : toStore.getStoreData().entrySet()) {
			file.set(toStore.getName() + "." + elem.getKey(), elem.getValue());
		}
	}
	
	/**
	 * Stores {@code toStore} in {@code file}. Uses the name as super path
	 * @param file The file in what the {@code Storable} should be stored
	 * @param toStore The object to store
	 */
	public void store(PluginFile file, Storable toStore) {
		store(getFileConfiguration(file), toStore);
	}

	/**
	 * Stores {@code toStore} in {@code file} at the given path
	 * @param file The file in what the {@code Storable} should be stored
	 * @param toStore The object to store
	 * @param path The storage path inside the file
	 */
	public void store(FileConfiguration file, Collection<? extends Storable> toStore, String path) {
		for (Storable elem : toStore) {
			store(file, elem, path + "." + elem.getName());
		}
	}
	
	/**
	 * Stores {@code toStore} in {@code file} at the given path
	 * @param file The file in what the {@code Storable} should be stored
	 * @param toStore The object to store
	 * @param path The storage path inside the file
	 */
	public void store(PluginFile file, Collection<? extends Storable> toStore, String path) {
		store(getFileConfiguration(file), toStore, path);
	}
	
	/**
	 * Clears the whole file
	 * @param file The file to clear
	 * @throws IllegalArgumentException Thrown if {@code file} is {@code null}
	 */
	public static void clearFile(FileConfiguration file) throws IllegalArgumentException {
		if (file == null) {
			throw new IllegalArgumentException("File to clear cannot be null");
		}
		for (String path : file.getConfigurationSection("").getKeys(false)) {
			file.set(path, null);
		}
	}

}

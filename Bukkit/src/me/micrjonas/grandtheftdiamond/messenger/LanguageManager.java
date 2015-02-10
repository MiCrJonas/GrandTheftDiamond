package me.micrjonas.grandtheftdiamond.messenger;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import me.micrjonas.grandtheftdiamond.GrandTheftDiamond;
import me.micrjonas.grandtheftdiamond.bukkit.BukkitGrandTheftDiamondPlugin;
import me.micrjonas.grandtheftdiamond.data.FileManager;
import me.micrjonas.grandtheftdiamond.data.PluginFile;

import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;

public class LanguageManager {
	
	private static LanguageManager instance = new LanguageManager();
	
	public static LanguageManager getInstance() {
		return instance;
	}
	
	private String defaultLanguage = null;
	private FileConfiguration defaultLanguageConfig = null;
	private final Map<String, FileConfiguration> languages = new HashMap<>();
	
	private LanguageManager() {
		reloadLanguages();
	}
	
	/**
	 * (Re-)loads the language files from the language folder
	 */
	public void reloadLanguages() {
		languages.clear();
		File path = new File(GrandTheftDiamond.getDataFolder(), "language");
		if (!path.exists()) {
			path.mkdirs();
		}
		File defaultMessageFile = new File(GrandTheftDiamond.getDataFolder() + File.separator + "language", "english.yml");
		FileManager.getInstance().createFileIfNotExists(defaultMessageFile);
		for (File file : path.listFiles()) {
			if (file.getName().endsWith(".yml")) {
				String fileName = file.getName().substring(0, file.getName().length() - 4).toLowerCase().replace(" ", "_");
				FileConfiguration yamlConfiguration = YamlConfiguration.loadConfiguration(file);
				languages.put(fileName, yamlConfiguration);	
				if (fileName.equals(FileManager.getInstance().getFileConfiguration(PluginFile.CONFIG).getString("language.defaultLanguage"))) {
					defaultLanguage = fileName;
					defaultLanguageConfig = yamlConfiguration;
				}
			}
		}
		if (defaultLanguage == null) {
			FileManager.getInstance().getFileConfiguration(PluginFile.CONFIG).set("language.defaultLanguage", "english");
			defaultLanguage = "english";
			defaultLanguageConfig = languages.get("english");
		}
	}
	
	/**
	 * Checks whether a language exists or not, ignores case sensitive
	 * @param lang The language to check; Allowed to be null
	 * @return True if the language exists, else false
	 * @throws IllegalArgumentException Thrown if {@code lang} is {@code null}
	 */
	public boolean isLanguage(String lang) throws IllegalArgumentException {
		if (lang == null) {
			throw new IllegalArgumentException("Language to check cannot be null");
		}
		return languages.containsKey(lang);
	}
	
	
	/**
	 * Sets the default language
	 * @param lang The new default language; ignores case sensitive
	 * @throws IllegalArgumentException If the language does not exist or {@code lang} is {@code null}
	 */
	public void setDefaultLanguage(String lang) throws IllegalArgumentException {
		if (lang == null) {
			throw new IllegalArgumentException("Language to check cannot be null");
		}
		lang = lang.toLowerCase();
		if (languages.containsKey(lang)) {
			defaultLanguage = lang;
			defaultLanguageConfig = languages.get(lang);
		}
		else {
			throw new IllegalArgumentException("language " + lang + " does not exist");
		}
	}
	
	/**
	 * Returns the language file of the command sender. Default language file for console sender
	 * @param sender The command sender to get the language file
	 * @return The sender's language file
	 * @throws IllegalArgumentException If sender is {@code null}
	 */
	public FileConfiguration getLanguageFile(CommandSender sender) throws IllegalArgumentException {
		if (sender == null) {
			throw new IllegalArgumentException("Sender is not allowed to be null");
		}
		if (sender instanceof Player) {
			FileConfiguration playerData = FileManager.getInstance().getPlayerData((Player) sender);
			String playerLanguage = playerData.getString("language");
			if (playerLanguage == null) {
				return defaultLanguageConfig;
			}
			FileConfiguration playerLanguageFile = languages.get(playerLanguage);
			return playerLanguageFile != null ? playerLanguageFile : defaultLanguageConfig;
		}
		return defaultLanguageConfig;
	}
	
	/**
	 * Returns the language's file
	 * @param lang The language to get the file; ignores case sensitive
	 * @return The language's file
	 * @throws IllegalArgumentException If the language does not exist
	 */
	public FileConfiguration getLanguageFile(String lang) throws IllegalArgumentException {
		FileConfiguration langFile = languages.get(lang.toLowerCase());
		if (langFile != null) {
			return langFile;
		}
		else {
			throw new IllegalArgumentException("Language '" + lang + "' does not exist");
		}
	}
	
	/**
	 * Returns the default language file
	 * @return The default language file
	 */
	public FileConfiguration getDefaultLanguageFile() {
		return defaultLanguageConfig;
	}
	
	/**
	 * Returns a message of the sender's message file
	 * @param sender Returns the message from it's message file
	 * @param message The path to the message
	 * @return A message of the sender's message file
	 * @throws IllegalArgumentException If sender and/or message is {@code null}
	 */
	public String getLanguageMessage(CommandSender sender, String message) throws IllegalArgumentException {
		if (sender instanceof Player) {
			String playerLanguage = FileManager.getInstance().getPlayerData((Player) sender).getString("language");
			if (playerLanguage != null && languages.containsKey(playerLanguage)) {
				String languageMessage = languages.get(playerLanguage).getString(message);
				if (languageMessage != null) {
					return languageMessage;
				}
			}
		}
		return defaultLanguageConfig.getString(message);
	}
	
	/**
	 * Saves a message file
	 * @param language The language of the message file
	 * @throws IllegalArgumentException If language is {@code null}
	 */
	public void saveLanguageFile(String language) throws IllegalArgumentException {
		if (language == null) {
			throw new IllegalArgumentException("language is not allowed to be null");
		}
		FileConfiguration languageFile = getLanguageFile(language);
		if (languageFile != null) {
			try {
				languageFile.save(new File(GrandTheftDiamond.getDataFolder() + File.separator + "language" + File.separator + "english.yml"));
			} 
			catch (IOException e) {
				BukkitGrandTheftDiamondPlugin.getInstance().getLogger().warning("Could not save " + language + ".yml");
				e.printStackTrace();
			}
		}
	}
	
	/**
	 * Returns a Collection of all message files
	 * @return A collection of all message files
	 */
	public Collection<String> getRegisteredLanguages() {
		List<String> languages = new ArrayList<>(this.languages.keySet());
		Collections.sort(languages);
		return languages;
	}

	/**
	 * Returns the default language
	 * @return The default language
	 */
	public String getDefaultLanguage() {
		return defaultLanguage;
	}

}

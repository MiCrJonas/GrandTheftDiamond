package me.micrjonas.grandtheftdiamond.data;

import java.io.File;

import me.micrjonas.grandtheftdiamond.GrandTheftDiamond;
import me.micrjonas.grandtheftdiamond.util.Nameable;

/**
 * Represents a file used by the plugin
 */
public enum PluginFile implements Nameable {
	
	/**
	 * Path: data/arena.yml
	 */
	ARENA(true),
	
	/**
	 * Path: config.yml
	 */
	CONFIG(false),
	
	/**
	 * Path: data/dealers.yml
	 */
	DEALERS(true),
	
	/**
	 * Path: event-config.yml
	 */
	EVENT_CONFIG(false),
	
	/**
	 * Path: data/gangs.yml
	 */
	GANGS(true),
	
	/**
	 * Path: data/houses.yml
	 */
	HOUSES(true),
	
	/**
	 * Path: data/jails.yml
	 */
	JAILS(true),
	
	/**
	 * Path: only-gtd-mode-config.yml
	 */
	ONLY_GTD_MODE_CONFIG(false),
	
	/**
	 * Path: ranks.yml
	 */
	RANKS(false),
	
	/**
	 * Path: data/safes.yml
	 */
	SAFES(true),
	
	/**
	 * Path: data/signs.yml
	 */
	SIGNS(true);
	
	private File file;
	private final boolean isDataFile;
	private final String jarSource;
	
	private PluginFile(boolean isDataFile) {
		this.isDataFile = isDataFile;
		init(name().toLowerCase().replace('_', '-') + ".yml", isDataFile);
		if (isConfiguration()) {
			jarSource = "resources/configuration/" + getFileName();
		}
		else {
			jarSource = null;
		}
	}
	
	private void init(String fileName, boolean isDataFile) {
		if (isDataFile) {
			file = new File(GrandTheftDiamond.getDataFolder() + File.separator + "data", fileName);
		}
		else {
			file = new File(GrandTheftDiamond.getDataFolder(), fileName);
		}
	}

	@Override
	public String getName() {
		return name().toLowerCase();
	}
	
	/**
	 * Returns the name of the file
	 * @return The name of the file
	 */
	public String getFileName() {
		return file.getName();
	}
	
	/**
	 * Returns the represented File
	 * @return The represented File
	 */
	public File getFile() {
		return file;
	}
	
	/**
	 * Returns whether the file is a configuration file
	 * @return True if the file is a configuration file, else {@code false}
	 * @see #isDataFile()
	 */
	public boolean isConfiguration() {
		return !isDataFile();
	}
	
	/**
	 * Returns whether the file is a data file
	 * @return True if the file is a data file, else {@code false}
	 * @see #isConfiguration()
	 */
	public boolean isDataFile() {
		return isDataFile;
	}
	
	/**
	 * Returns the path to the default configuration in the plugin's JAR file
	 * @return The file's path in the JAR file. {@code null} if {@link #isConfiguration()} returns {@code false}
	 */
	public String getJarPath() {
		return jarSource;
	}

}

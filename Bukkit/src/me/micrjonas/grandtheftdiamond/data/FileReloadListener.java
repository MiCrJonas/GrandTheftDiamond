package me.micrjonas.grandtheftdiamond.data;

import java.util.EventListener;

import me.micrjonas.grandtheftdiamond.GrandTheftDiamond;

import org.bukkit.configuration.file.FileConfiguration;


/**
 * Listens to a file reload of a file used by the plugin<br>
 * Register with {@link GrandTheftDiamond#registerFileReloadListener(FileReloadListener)}
 */
public interface FileReloadListener extends EventListener {
	
	/**
	 * Get called after a specific file was reloaded by the {@link FileManager}
	 * @param file The reloaded file
	 * @param fileConfiguration The {@link FileConfiguration} of the reloaded file
	 */
	public void configurationReloaded(PluginFile file, FileConfiguration fileConfiguration);

}

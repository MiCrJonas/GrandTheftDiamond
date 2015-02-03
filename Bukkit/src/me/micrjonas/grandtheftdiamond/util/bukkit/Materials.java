package me.micrjonas.grandtheftdiamond.util.bukkit;

import me.micrjonas.grandtheftdiamond.data.FileManager;
import me.micrjonas.grandtheftdiamond.data.PluginFile;

import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;

public class Materials {
	
	private Materials() { }
	
	
	@SuppressWarnings("deprecation")
	public static Material getMaterialOrDefault(String typeString, Material defaultValue) {
		
		if (typeString == null)
			return defaultValue;
		
		try {
			
			return Material.getMaterial(Integer.parseInt(typeString));
			
		}
		
		catch (NumberFormatException ex) {  }
		
		
		try {
			
			return Material.valueOf(typeString.toUpperCase());
			
		}
		
		catch (IllegalArgumentException ex2) {
			
			return defaultValue;
			
		}
		
	}
	
	
	public static Material getMaterialFromConfig(FileConfiguration config, String path) {
		
		return getMaterialOrDefault(config.getString(path), null);
		
	}
	
	
	public static Material getMaterialFromConfig(String path) {
		
		return getMaterialFromConfig(FileManager.getInstance().getFileConfiguration(PluginFile.CONFIG), path);
		
	}

}

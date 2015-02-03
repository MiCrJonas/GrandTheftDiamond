package me.micrjonas.grandtheftdiamond.manager;

import me.micrjonas.grandtheftdiamond.data.FileManager;
import me.micrjonas.grandtheftdiamond.data.PluginFile;

import org.bukkit.configuration.file.FileConfiguration;

public enum EconomySystemType {
	
	BOTH, GRAND_THEFT_DIAMOND, NONE, VAULT;
	
	private static EconomySystemType currentType = NONE;
	
	static {
		
		FileConfiguration config = FileManager.getInstance().getFileConfiguration(PluginFile.CONFIG);
		boolean gtd = config.getBoolean("economy.useGrandTheftDiamondEconomy") && config.getInt("economy.percentOfDefaultBalance.GTD") > 0;
		boolean vault = config.getBoolean("useVaultEconomy") && config.getInt("economy.percentOfDefaultBalance.Vault") > 0;
		
		if (!gtd && vault)
			currentType = VAULT;
		
		else if (gtd && !vault)
			currentType = GRAND_THEFT_DIAMOND;
		
		else if (gtd && vault)
			currentType = BOTH;
		
	}
	
	public static EconomySystemType getCurrentType() {
		
		return currentType;
		
	}

}

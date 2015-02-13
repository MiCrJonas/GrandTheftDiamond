package me.micrjonas.grandtheftdiamond.command;

import java.io.File;

import me.micrjonas.grandtheftdiamond.GrandTheftDiamond;
import me.micrjonas.grandtheftdiamond.data.FileReloadListener;
import me.micrjonas.grandtheftdiamond.data.PluginFile;
import me.micrjonas.util.ReleaseType;

import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

public class CommandInfo implements CommandExecutor, FileReloadListener {

	private boolean useVaultChat;
	private boolean useVaultEconomy;
	private boolean useNametagEdit;
	
	public CommandInfo() {
		
		GrandTheftDiamond.registerFileReloadListener(this);
		
	}
	
	@Override
	public void configurationReloaded(PluginFile file, FileConfiguration fileConfiguration) {
		
		if (file == PluginFile.CONFIG) {
			
			useVaultChat = fileConfiguration.getBoolean("useVaultChat");
			useVaultEconomy = fileConfiguration.getBoolean("useVaultEconomy");
			useNametagEdit = fileConfiguration.getBoolean("useNametagEdit");
			
		}
		
	}
	
	@Override
	public void onCommand(CommandSender sender, String alias, String[] args, String[] originalArgs) {
		
		sender.sendMessage("§aGrandTheftDiamond §7version §a" + GrandTheftDiamond.getVersion());
		sender.sendMessage("§7Author: §aMiCrJonas1997");
		
		if (Bukkit.getPluginManager().getPlugin("Vault") == null) {
			
			sender.sendMessage("§7VaultChat is §cdisabled§7! (Plugin not found)");
			sender.sendMessage("§7VaultEconomy is §cdisabled§7! (Plugin not found)");
			
		}
		
		else {
			
			sender.sendMessage("§7Vault Chat is §" + (useVaultChat ? "aenabled" : "cdisabled"));
			sender.sendMessage("§7Vault Economy is §" + (useVaultEconomy ? "aenabled" : "cdisabled"));
			
		}
		
		if (Bukkit.getPluginManager().getPlugin("NametagEdit") == null)
			sender.sendMessage("§7NametagEdit is §cdisabled§7 (Plugin not found)");
		
		else
			sender.sendMessage("§7NametagEdit is §" + (useNametagEdit ? "aenabled" : "cdisabled"));
		
		
		YamlConfiguration metricsConfig = YamlConfiguration.loadConfiguration(new File("plugins/PluginMetrics/config.yml"));
		
		if (!metricsConfig.getBoolean("opt-out"))
			sender.sendMessage("§7PluginMetrics is §" + (useNametagEdit ? "aenabled" : "cdisabled"));
		
		ReleaseType vType = GrandTheftDiamond.getVersion().getReleaseType();
		
		if (vType == ReleaseType.BETA)
			sender.sendMessage("§7This is a beta version! Please report bugs. Thanks a lot!");
		
		else if (vType == ReleaseType.PRE)
			sender.sendMessage("§7This is a pre release! Please report bugs. Thanks buddy!");
		
		else if (vType == ReleaseType.DEV)
			sender.sendMessage("§7This is a development build! Please report bugs. Thank you :)");
		
		sender.sendMessage(" §ahttp://dev.bukkit.org/bukkit-plugins/grand-theft-diamond/");
		
	}

}

package me.micrjonas.grandtheftdiamond.manager;

import java.util.logging.Level;

import me.micrjonas.grandtheftdiamond.GrandTheftDiamond;
import me.micrjonas.grandtheftdiamond.data.FileManager;
import me.micrjonas.grandtheftdiamond.data.FileReloadListener;
import me.micrjonas.grandtheftdiamond.data.PluginData;
import me.micrjonas.grandtheftdiamond.data.PluginFile;
import net.milkbowl.vault.economy.Economy;

import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.plugin.RegisteredServiceProvider;

public class EconomyManager implements FileReloadListener {
	
	private static EconomyManager instance = new EconomyManager();
	
	/**
	 * Returns the loaded instance
	 * @return The loaded instance
	 */
	public static EconomyManager getInstance() {
		return instance;
	}
	
	private Economy economy = null;
	private boolean useVault;
	
	private int percentOfDefaultGTD;
	private int percentOfDefaultVault;
	
	private boolean resetToDefault;
	private int startBalance;
	private int minBalance;
	private int maxBalance;
	
	private EconomyManager() {
		
		GrandTheftDiamond.registerFileReloadListener(this);
		setupEconomy();
		
	}
	
	
	private void setupEconomy() {

		if (FileManager.getInstance().getFileConfiguration(PluginFile.CONFIG).getBoolean("useVaultEconomy") && Bukkit.getPluginManager().getPlugin("Vault") != null) {
				
			if (Bukkit.getPluginManager().getPlugin("Vault") != null) {
			
			    RegisteredServiceProvider<Economy> economyProvider = Bukkit.getServer().getServicesManager().getRegistration(net.milkbowl.vault.economy.Economy.class);
			        
			    if (economyProvider != null) {
			        	
			        economy = economyProvider.getProvider();
			    }

			    useVault = economy != null;
			        
			}
				
			else {
				
				Bukkit.getServer().getLogger().log(Level.WARNING, "Vault could not be found! Cannot use Server-Economy-Part and Chatfeatures of GTD!");
				Bukkit.getServer().getLogger().log(Level.WARNING, "Disable the usage of Vault and this message in the config.yml \"useVaultEconomy: false\"!");
				Bukkit.getServer().getLogger().log(Level.WARNING, "GTD-Money-System is enabled nevertheless!");
					
			}
			
		}
		
	}
	
	

	@Override
	public void configurationReloaded(PluginFile file, FileConfiguration fileConfiguration) {
		
		if (file == PluginFile.CONFIG) {
			
			setupEconomy();
			
			percentOfDefaultGTD = fileConfiguration.getInt("economy.percentOfDefaultBalance.GTD");
			percentOfDefaultVault = fileConfiguration.getInt("economy.percentOfDefaultBalance.Vault");
			
			resetToDefault = fileConfiguration.getBoolean("economy.resetToStartBalance");
			startBalance = fileConfiguration.getInt("economy.startBalance");
			minBalance = fileConfiguration.getInt("economy.minBalance");
			maxBalance = fileConfiguration.getInt("economy.maxBalance");	
			
		}
		
	}
	
	
	boolean useVault() {
		
		return useVault;
		
	}
	
	
	public Economy getVaultEconomy() {
		
		return economy;
		
	}
	
	
	public int getMinBalance() {
		
		return minBalance;
		
	}
	
	
	public int getMaxBalance() {
		
		return maxBalance;
		
	}
	
	
	public int getStartBalance() {
		
		return startBalance;
		
	}
	
	
	public boolean hasBalanceExact(Player p, int balance) {
		
		if (createNewAccount(p)) {
			
			if (FileManager.getInstance().getPlayerData(p).getInt("money") >= balance)
				return true;
			
		}
		
		return false;
		
	}
	
	
	public boolean hasVaultBalanceExact(Player p, double balance) {
		
		if (!useVault)
			return true;
		
		return this.economy.has(p, balance);
		
	}
	
	
	public boolean hasBalance(Player p, int balance) {
		
		return hasBalanceExact(p, (int) (balance / 100D * percentOfDefaultGTD));
		
	}
	
	
	public boolean hasVaultBalance(Player p, int balance) {
		
		return hasVaultBalanceExact(p, (int) (balance / 100D * percentOfDefaultVault));
		
	}
	
	
	public int getBalance(String player) {
		
		FileConfiguration playerData = FileManager.getInstance().getPlayerData(player);
		
		if (playerData != null)
			return playerData.getInt("money");
		
		return 0;
		
	}
	
	
	public int getBalance(Player p) {
		
		if (createNewAccount(p))
			return FileManager.getInstance().getPlayerData(p).getInt("money");
		
		return 0;
		
	}
	
	
	public double getVaultBalance(Player p) {
		
		if(useVault() && this.economy.hasAccount(p))
			return this.economy.getBalance(p);
		
		return 0;
		
	}
	
	
	private boolean createNewAccount(Player p) {
		
		if (PluginData.getInstance().isPlayer(p)) {
			
			if (!FileManager.getInstance().getPlayerData(p).isInt("money"))
				FileManager.getInstance().getPlayerData(p).set("money", getStartBalance());
			
			return true;
			
		}
			
		return false;
		
	}
	
	
	public void setBalance(Player p, int balance) {
		FileManager.getInstance().getPlayerData(p).set("money", balance);
	}
	
	public void resetBalance(Player p) {
		int resetBalance = 0;
		if (resetToDefault) {
			resetBalance = getStartBalance();
		}
		FileManager.getInstance().getPlayerData(p).set("money", resetBalance);
	}
	
	public void deposit(Player p, int balance, boolean depositVault) {
		int balanceGTD = (int) Math.round(balance / 100D * percentOfDefaultGTD);
		int balanceVault = (int) Math.round(balance / 100D * percentOfDefaultVault);
		depositExact(p, balanceGTD, balanceVault);
	}
	
	
	public void depositExact(Player p, int balanceGTD, int balanceVault) {
		changeGTD(p, balanceGTD);
	}
	
	
	public void withdraw(Player p, int balance, boolean depositVault) {
		int balanceGTD = (int) Math.round(balance / 100D * percentOfDefaultGTD);
		int balanceVault = (int) Math.round(balance / 100D * percentOfDefaultVault);
		withdrawExact(p, balanceGTD, balanceVault);
	}
	
	
	public void withdrawExact(Player p, int balanceGTD, int balanceVault) {
		changeGTD(p, balanceGTD * -1);
	}
	
	private void changeVault(OfflinePlayer p, int balance) {
		if (!economy.hasAccount(p)) {
			economy.createPlayerAccount(p);
		}
		if (balance < 0) {
			economy.withdrawPlayer(p, balance * -1);
		}
		else if (balance > 0) {
			economy.depositPlayer(p, balance);
		}
	}
	
	private void changeGTD(Player p, int balance) {
		int newBalance = getBalance(p) + balance;
		if (newBalance >= getMinBalance()) {
			if (newBalance <= getMaxBalance()) {
				FileManager.getInstance().getPlayerData(p).set("money", newBalance);
			}
			else {
				FileManager.getInstance().getPlayerData(p).set("money", getMaxBalance());
			}
		}
		else {
			FileManager.getInstance().getPlayerData(p).set("money", getMinBalance());
	
		}
	}
}

package me.micrjonas.grandtheftdiamond.data.player;

import java.util.LinkedHashMap;
import java.util.Map;

import me.micrjonas.grandtheftdiamond.Team;
import me.micrjonas.grandtheftdiamond.data.FileManager;
import me.micrjonas.grandtheftdiamond.data.storage.Storable;
import me.micrjonas.grandtheftdiamond.util.bukkit.PotionEffects;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.inventory.ItemStack;

public class TeamSpecificPlayerData extends PlayerDataStorage implements Storable {

	private final PlayerData mainData;
	private final String storePath;
	
	private int deaths;
	private int killedCivilians;
	private int killedGangsters;
	private int killedCops;
	
	TeamSpecificPlayerData(PlayerData mainData, Team team) {
		super(mainData.getPlayer());
		this.mainData = mainData;
		storePath = "teamSpecific." + team.name() + ".";
		loadData();
	}
		
	@Override
	public String getName() {
		return mainData.getName();
	}

	@Override
	public Map<String, Object> getStoreData() {
		Map<String, Object> data = new LinkedHashMap<>();
		Map<Integer, ItemStack> inventoryContents = new LinkedHashMap<>();
		Map<Integer, ItemStack> armorContents = new LinkedHashMap<>();
	//Start of inventory
		ItemStack[] inventory = getInventory();
		ItemStack[] armor = getArmor();
		for (int i = 0; i < inventory.length; i++) {
			if (inventory[i] != null)
				inventoryContents.put(i, inventory[i]);
		}
		
		for (int i = 0; i < armor.length; i++) {
			if (armor[i] != null)
				inventoryContents.put(i, armor[i]);
		}
	//End of inventory
		data.put("inventory.contents", inventoryContents);
		data.put("inventory.armorContents", armorContents);
		data.put("health", getHealth());
		data.put("foodLevel", getFoodLevel());
		data.put("deaths", getDeaths());
		data.put("killedCivilians", getKilledCivilians());
		data.put("killedGangsters", getKilledGangsters());
		data.put("killedCops", getKilledCops());
		data.put("potionEffects", PotionEffects.toMapList(getPotionEffects()));
		return data;
	}
	
	private void loadData() {
		FileConfiguration dataFile = FileManager.getInstance().getPlayerData(getPlayer());
		setHealth(dataFile.getDouble(storePath + "health"));
		setFoodLevel(dataFile.getInt(storePath + "foodLevel"));
		setDeaths(dataFile.getInt(storePath + "health"));
		setKilledCivilians(dataFile.getInt(storePath + "killedCivilians"));
		setKilledGangsters(dataFile.getInt(storePath + "killedGangsters"));
		setKilledCops(dataFile.getInt(storePath + "killedCops"));
		setPotionEffects(PotionEffects.getEffectsFromConfig(dataFile, storePath + "potionEffects"), false);
	}
	
	
	/**
	 * Returns the {@code Player}'s deaths
	 * @return The {@code Player}'s death count
	 */
	public int getDeaths() {
		return deaths;
	}
	
	private void setDeaths(int deaths) {
		this.deaths = deaths;
	}

	/**
	 * Sets the {@code Player}'s death count one up
	 */
	public void deathsOneUp() {
		deaths++;
	}

	/**
	 * Returns the count of killed civilians
	 * @return The count of killed civilians
	 */
	public int getKilledCivilians() {
		return killedCivilians;
	}
	
	private void setKilledCivilians(int killed) {
		killedCivilians = killed;
	}

	/**
	 * Sets the count of killed civilians one up
	 */
	public void killedCiviliansOneUp() {
		killedCivilians++;
	}
	
	/**
	 * Returns the count of killed gangsters
	 * @return The count of killed gangsters
	 */
	public int getKilledGangsters() {
		return killedGangsters;
	}
	
	private void setKilledGangsters(int killed) {
		killedGangsters = killed;
	}

	/**
	 * Sets the count of killed gangsters one up
	 */
	public void killedGangstersOneUp() {
		killedGangsters++;
	}

	/**
	 * Returns the count of killed cops
	 * @return The count of killed cops
	 */
	public int getKilledCops() {
		return killedCops;
	}

	private void setKilledCops(int killed) {
		killedCops = killed;
	}
	
	/**
	 * Sets the count of killed cops one up
	 */
	public void killedCopsOneUp() {
		killedCops++;
	}
	
}

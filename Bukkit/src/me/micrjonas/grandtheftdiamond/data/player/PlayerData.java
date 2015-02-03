package me.micrjonas.grandtheftdiamond.data.player;

import java.util.EnumMap;
import java.util.LinkedHashMap;
import java.util.Map;

import me.micrjonas.grandtheftdiamond.Team;
import me.micrjonas.grandtheftdiamond.data.FileManager;
import me.micrjonas.grandtheftdiamond.data.storage.Storable;
import me.micrjonas.grandtheftdiamond.gang.Gang;
import me.micrjonas.grandtheftdiamond.gang.GangManager;
import me.micrjonas.grandtheftdiamond.jail.JailManager;
import me.micrjonas.grandtheftdiamond.messenger.LanguageManager;
import me.micrjonas.grandtheftdiamond.util.Enums;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;

public class PlayerData implements Storable {
	
	private final Player p;
	private final FileConfiguration dataFile;
	
	private final Map<Team, TeamSpecificPlayerData> teamSpecificData = new EnumMap<>(Team.class);
	private final BanData banData;
	private final PlayerSessionData sessionData;
	
	private String language;
	private Team team;
	private int exp;
	private int wantedLevel;
	
	public PlayerData(Player p, FileConfiguration dataFile) {
		if (p == null) {
			throw new IllegalArgumentException("p is not allowed to be null");
		}
		if (dataFile == null) {
			dataFile = FileManager.getInstance().getPlayerData(p);
		}
		this.p = p;
		this.dataFile = dataFile;
		banData = new BanData(p);
		sessionData = new PlayerSessionData(p);
		loadData();
	}
	
	public PlayerData(Player p) {
		this(p, null);
	}
	
	@Override
	public Map<String, Object> getStoreData() {
		Map<String, Object> data = new LinkedHashMap<>();
		data.put("teamSpecific.CIVILIAN", teamSpecificData.get(Team.CIVILIAN).getStoreData());
		data.put("teamSpecific.COP", teamSpecificData.get(Team.COP).getStoreData());
		data.put("team", team.name());
		data.put("experience", exp);
		data.put("wantedLevel", wantedLevel);
		return data;
	}
	
	@Override
	public String getName() {
		return p.getName();
	}
	
	private void loadData() {
		language = dataFile.getString("language");
		if (!(language == null || !LanguageManager.getInstance().isLanguage(language))) {
			language = null;
		}
		Team team = Enums.getEnumFromConfig(Team.class, dataFile, "team");
		this.team = team == null || team == Team.EACH_TEAM ? Team.NONE : team;
		exp = dataFile.getInt("experience");
		wantedLevel = dataFile.getInt("wantedLevel");
	}
	
	/**
	 * Returns the {@code Player} of the player data
	 * @return The data's {@code Player}
	 */
	public Player getPlayer() {
		return p;
	}
	
	/**
	 * Returns the {@code Player}'s gang
	 * @return The {@code Player}'s gang. Null if the player is not in a gang
	 * @see GangManager#getPlayerGang(org.bukkit.OfflinePlayer)
	 */
	public Gang getGang() {
		return GangManager.getInstance().getPlayerGang(getPlayer());
	}
	
	/**
	 * Returns the player's ban data
	 * @return the banData The player's ban data
	 */
	public BanData getBanData() {
		return banData;
	}
	
	/**
	 * Returns the team specific data of the {@code Player}
	 * @param team The related {@link Team}
	 * @return The team specific data of the {@code Player}
	 */
	public TeamSpecificPlayerData getTeamSpecificPlayerData(Team team) {
		Team.requiresRealTeam(team);
		return teamSpecificData.get(team);
	}
	
	/**
	 * Returns the {@code Player}'s session data (temporary data)
	 * @return The {@code Player}'s session data
	 */
	public PlayerSessionData getSessionData() {
		return sessionData;
	}
	
	/**
	 * Returns the Player's language
	 * @return The Player's language
	 */
	public String getLanguage() {
		if (language == null) {
			return LanguageManager.getInstance().getDefaultLanguage();
		}
		return language;
	}
	
	/**
	 * Sets the Player's language
	 * @param lang The new language; null for default language
	 * @throws IllegalArgumentException If the language does not exist
	 */
	public void setLanguage(String lang) throws IllegalArgumentException {
		if (lang == null)
			language = lang;
		
		else if (!LanguageManager.getInstance().isLanguage(lang))
			throw new IllegalArgumentException("language " + lang.toLowerCase() + " does not exist");
		
		language = lang;
	}

	/**
	 * Returns the player's {@link Team}
	 * @return the team the player's {@link Team}
	 */
	public Team getTeam() {
		return team;
	}

	/**
	 * Sets the player's {@link Team}
	 * @param team the team to set
	 */
	public void setTeam(Team team) {
		this.team = team;
	}
	
	/**
	 * Returns the player's experience
	 * @return the player's experience
	 */
	public int getExpPoints() {
		return exp;
	}

	/**
	 * Adds experience points to the {@code Player}
	 * @param exp The experience points to add
	 * @throws IllegalArgumentException Thrown if {@code exp} is < 0
	 */
	public void addExpPoints(int exp) {
		if (exp < 0) {
			throw new IllegalArgumentException("Exp value is not allowed to be < 0");
		}
		this.exp += exp;
	}

	/**
	 * Returns the player's wanted level
	 * @return the player's wanted level
	 */
	public int getWantedLevel() {
		if (team != Team.CIVILIAN)
			wantedLevel = 0;
		
		return wantedLevel;
	}

	/**
	 * Sets the player's wanted level
	 * @param wantedLevel the wantedLevel to set
	 * @throws IllegalStateException If the Player's team is not CIVILIAN
	 */
	public void setWantedLevel(int wantedLevel) throws IllegalStateException {
		if (team != Team.CIVILIAN)
			throw new IllegalStateException("cannot set the wanted level when team is != Team.CIVILIAN");
		
		this.wantedLevel = wantedLevel;
	}
	
	/**
	 * Checks whether the player is handcuffed
	 * @return True if the player is handcuffed, else false
	 * @see JailManager#isHandcuffed(Player)
	 */
	public boolean isHandcuffed() {
		return JailManager.getInstance().isHandcuffed(p);
	}
	
}

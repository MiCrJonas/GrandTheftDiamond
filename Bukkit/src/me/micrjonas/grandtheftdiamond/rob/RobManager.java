package me.micrjonas.grandtheftdiamond.rob;

import java.util.Calendar;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

import me.micrjonas.grandtheftdiamond.GrandTheftDiamond;
import me.micrjonas.grandtheftdiamond.Team;
import me.micrjonas.grandtheftdiamond.api.event.player.PlayerPreRobSafeEvent;
import me.micrjonas.grandtheftdiamond.api.event.player.PlayerRobEvent;
import me.micrjonas.grandtheftdiamond.data.FileReloadListener;
import me.micrjonas.grandtheftdiamond.data.PluginData;
import me.micrjonas.grandtheftdiamond.data.PluginFile;
import me.micrjonas.grandtheftdiamond.data.TemporaryPluginData;
import me.micrjonas.grandtheftdiamond.data.storage.StorableManager;
import me.micrjonas.grandtheftdiamond.manager.EconomyManager;
import me.micrjonas.grandtheftdiamond.messenger.Messenger;
import me.micrjonas.grandtheftdiamond.pluginsupport.dynmap.DynmapMarkerManager;
import me.micrjonas.grandtheftdiamond.updater.DataConverter;
import me.micrjonas.grandtheftdiamond.util.Color;
import me.micrjonas.grandtheftdiamond.util.bukkit.Locations;

import org.apache.commons.lang.NullArgumentException;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

/**
 * Manages all robbing related plugin components
 */
public final class RobManager implements FileReloadListener, StorableManager<Safe> {
	
	private static RobManager instance = new RobManager();
	
	/**
	 * Returns the loaded instance of the plugin's {@link RobManager}
	 * @return The loaded instance of the plugin's {@link RobManager}
	 */
	public static RobManager getInstance() {
		return instance;
	}
	
	private final Map<UUID, String> safeCreatingPlayers = new HashMap<>();
	private final Map<Block, Safe> safes = new HashMap<>();
	private final Map<UUID, Safe> robbingPlayers = new HashMap<>();
	private final Map<Block, Player> currentlyRobbedSafes = new HashMap<>();
	private final Set<Block> unrobbableSafes = new HashSet<>();
	private final Map<UUID, Integer> robScheduler = new HashMap<>();
	
	private boolean safeRobbingEnabled;
	private int robTime;
	
	private String safeMarkerLabel;
	
	private RobManager() {
		GrandTheftDiamond.registerFileReloadListener(this);
		GrandTheftDiamond.registerStorableManager(this, PluginFile.SAFES);
	}
	
	
	@Override
	public void configurationReloaded(PluginFile file, FileConfiguration fileConfiguration) {
		if (file == PluginFile.CONFIG) {
			if (fileConfiguration.getBoolean("dynmap.markers.safe.show")) {
				safeMarkerLabel = fileConfiguration.getString("dynmap.markers.safe.name");
			}
			else {
				safeMarkerLabel = null;
			}
		}
		else if (file == PluginFile.EVENT_CONFIG) {
			safeRobbingEnabled = fileConfiguration.getBoolean("robbing.safe.enabled");
			robTime = fileConfiguration.getInt("robbing.safe.robTime");
			if (robTime < 0) {
				robTime = 0;
			}
		}
	}
	
	@Override
	public Collection<Safe> getAllObjects() {
		return Collections.unmodifiableCollection(safes.values());
	}
	
	@Override
	public void loadObjects(FileConfiguration dataFile) {
		safes.clear();
		DataConverter.convertSafeData(dataFile);
		if (dataFile.isConfigurationSection("")) {
			for (String safe : dataFile.getConfigurationSection("").getKeys(false))  {
				Location loc = Locations.getLocationFromFile(dataFile, safe, true);
				if (!(loc == null || loc.getBlock() == null || loc.getBlock().getType() == Material.AIR || isSafeAt(loc)))
					createSafe(loc.getBlock(), safe);
					//safes.put(loc.getBlock(), new Safe(safe, loc.getBlock()));
				else
					dataFile.set(safe, null);
			}
			DataConverter.convertSafeData(dataFile);
		}
	}
	
	// Null if disabled
	String getSafeMarkerLabel() {
		return safeMarkerLabel;
	}
	
	private boolean isSafeAt(Location loc) {
		return safes.containsKey(loc.getBlock());
	}
	
	/**
	 * Sets whether a player is creating a safe
	 * @param p The player who creates a safe (or not)
	 * @param safeName The name of the safe, set to null if the player is not creating a safe
	 */
	public void setCreatingSafe(Player p, String safeName){
		if (safeName != null) {
			safeCreatingPlayers.put(p.getUniqueId(), safeName);
		}
		else {
			safeCreatingPlayers.remove(p.getUniqueId());
		}
	}
	
	/**
	 * Checks whether a player is creating a safe
	 * @param p The player to check
	 * @return True if the player is creating a safe, else false
	 */
	public boolean isCreatingSafe(Player p) {
		return safeCreatingPlayers.containsKey(p.getUniqueId());
	}
	
	/**
	 * Checks whether a block is a safe
	 * @param safe The block to check
	 * @return True if the block is registered as safe, else false
	 * @throws IllegalArgumentException Thrown if {@code safe} is {@code null}
	 */
	public boolean isSafe(Block safe) throws IllegalArgumentException {
		if (safe == null) {
			throw new IllegalArgumentException("Safe block is not allowed to be null");
		}
		return safes.containsKey(safe);
	}
	
	/**
	 * Returns the safe to a block
	 * @param safe The safe block
	 * @return The safe of a block, null if {@code safe} is not registered as safe
	 * @throws IllegalArgumentException Thrown if {@code safe} is {@code null}
	 */
	public Safe getSafe(Block safe) throws IllegalArgumentException {
		if (safe == null) {
			throw new IllegalArgumentException("Safe block is not allowed to be null");
		}
		return safes.get(safe);
	}
	
	/**
	 * Checks whether a player is robbing a safe
	 * @param p The player to check
	 * @return True if the player is robbing a safe, else false
	 * @throws IllegalArgumentException Thrown if {@code p} is {@code null}
	 */
	public boolean isRobbing(Player p) throws IllegalArgumentException {
		if (p == null) {
			throw new IllegalArgumentException("Player to check is not allowed to be null");
		}
		return robbingPlayers.containsKey(p.getUniqueId());
	}
	
	/**
	 * Cancels the robbing of a player
	 * @param p Player to cancel
	 * @throws IllegalArgumentException Thrown if {@code p} is {@code null}
	 * @throws NullArgumentException Thrown if the player is {@code null}
	 * @throws IllegalStateException Thrown if the player is not robbing a safe
	 */
	public void cancelRobbing(Player p) throws IllegalArgumentException, IllegalStateException {
		if (p == null) {
			throw new IllegalArgumentException("Player to cancel is not allowed to be null");
		}
		if (!isRobbing(p)) {
			throw new IllegalStateException("Player is not robbing");
		}
		GrandTheftDiamond.cancelTask(robScheduler.get(p.getUniqueId()));
		currentlyRobbedSafes.remove(robbingPlayers.get(p.getUniqueId()).getBlock());
		unrobbableSafes.remove(robbingPlayers.get(p.getUniqueId()));
		robbingPlayers.remove(p.getUniqueId());
		Messenger.getInstance().sendPluginMessage(p, "robStopped");
	}
	
	/**
	 * Called when a player clicked a safe
	 * @param e The bukkit's click event
	 * @throws IllegalArgumentException Thrown if event {@code e} is {@code null}
	 * @deprecated Do not call
	 */
	@Deprecated
	public void safeRightClicked(PlayerInteractEvent e) throws IllegalArgumentException {
		if (!safeRobbingEnabled) {
			return;
		}
		Player p = e.getPlayer();
		Block safe = e.getClickedBlock();
		if (isCreatingSafe(p)) {
			if (PluginData.getInstance().inArena(safe.getLocation())) {
				String name = safeCreatingPlayers.get(p.getUniqueId());
				if (!isSafe(safe)) {
					Messenger.getInstance().sendPluginMessage(p, "safeCreated", "%name%", name);
				}
				else {
					Messenger.getInstance().sendPluginMessage(p, "safeUpdated", "%name%", name);
				}
				createSafe(safe, name);
			}
			else {
				Messenger.getInstance().sendPluginMessage(p, "mustBeInArena");
			}
			setCreatingSafe(p, null);
		}
		else {
			if (TemporaryPluginData.getInstance().isIngame(p)) {
				if (PluginData.getInstance().getTeam(p) == Team.CIVILIAN) {
					if (isSafe(safe)) {
						if (!isRobbing(p)) {
							if (!unrobbableSafes.contains(safe)) {
								startRobbing(e);
							}
							else {
								Messenger.getInstance().sendPluginMessage(p, "safeAlreadyRobbed");
							}
						}
						else {
							Messenger.getInstance().sendPluginMessage(p, "alreadyRobbing");
						}
					}
					else {
						Messenger.getInstance().sendPluginMessage(p, "notASafe");
					}
				}
				else {
					Messenger.getInstance().sendPluginMessage(p, "cannotRobAsCop");
				}
			}
		}
	}
	
	/**
	 * Removes/unregisters a safe
	 * @param safe The safe to remove
	 * @throws IllegalArgumentException Thrown if {@code safe} is {@code null}
	 */
	public void removeSafe(Block safe) {
		Safe realSafe = safes.remove(safe);
		if (realSafe != null) {
			unrobbableSafes.remove(safe);
			Player p = currentlyRobbedSafes.get(safe);
			if (p != null) {
				cancelRobbing(p);
			}
			realSafe.setInvalid();
			DynmapMarkerManager.getInstance().updateMarker(realSafe);
		}
	}
	
	/**
	 * Creates a new safe. If the block is already a safe, the safe's name gets updated
	 * @param safe The block of the safe
	 * @param name The name of the safe
	 * @throws IllegalArgumentException Thrown if {@code safe} is {@code null}
	 */
	public void createSafe(Block safe, String name) throws IllegalArgumentException {
		if (safe == null) {
			throw new IllegalArgumentException("Safe block is not allowed to be null");
		}
		Safe realSafe;
		if (isSafe(safe)) {
			realSafe = getSafe(safe);
			realSafe.setName(name);
		}
		else {
			realSafe = new Safe(name, safe);
			safes.put(safe, realSafe);
		}
		DynmapMarkerManager.getInstance().updateMarker(realSafe);
	}
	
	private void startRobbing(final PlayerInteractEvent e) {
		final Player p = e.getPlayer();
		final Safe safe = getSafe(e.getClickedBlock());
		final PlayerPreRobSafeEvent e2 = new PlayerPreRobSafeEvent(p, safe, robTime);
		
		Bukkit.getPluginManager().callEvent(e2);
		if (e2.isCancelled()) {
			return;
		}
		currentlyRobbedSafes.put(e.getClickedBlock(), p);
		unrobbableSafes.add(e.getClickedBlock());
		robbingPlayers.put(p.getUniqueId(), getSafe(e.getClickedBlock()));
		robScheduler.put(p.getUniqueId(), GrandTheftDiamond.scheduleSyncDelayedTask(new Runnable() {
			@Override
			public void run() {
				currentlyRobbedSafes.remove(e.getClickedBlock());
				robbingPlayers.remove(p.getUniqueId());
				PlayerRobEvent e3 = new PlayerRobEvent(p, safe);
				EconomyManager.getInstance().deposit(p, e3.getBalance(), true);
				PluginData.getInstance().setWantedLevel(p, PluginData.getInstance().getWantedLevel(p) + e3.getWantedLevel());
				p.getInventory().addItem(e3.getReceivedItems().toArray(new ItemStack[e3.getReceivedItems().size()]));
				Messenger.getInstance().sendPluginMessage(p, "robFinishedSafe", new String[]{"%amount%"}, new String[]{String.valueOf(e3.getBalance())});
				ichMagBier(p);
				GrandTheftDiamond.scheduleSyncDelayedTask(new Runnable() {
					@Override
					public void run() {
						unrobbableSafes.remove(e.getClickedBlock());
					}
				}, e3.getTimeToNextRob(), TimeUnit.SECONDS);
			}
		}, e2.getRobTime(), TimeUnit.SECONDS));
		Messenger.getInstance().sendPluginMessage(p, "startRobSafe");
		Messenger.getInstance().sendPluginMessage(p, "doNotMove", new String[]{"%time%"}, new String[]{String.valueOf(e2.getRobTime())});
	}

	private void ichMagBier(Player p) {
		Calendar c = Calendar.getInstance();
		if (c.get(Calendar.MONTH) == 11 && c.get(Calendar.DAY_OF_MONTH) >= 24 && c.get(Calendar.DAY_OF_MONTH) <= 26 && Math.random() > 0.6) {
			Color color = Color.values()[(int) (Math.random() * Color.values().length)];
			ItemStack iAmDrunk = new ItemStack(Material.WOOL, 1, color.getItemDamageValue());
			ItemMeta meta = iAmDrunk.getItemMeta();
			meta.setDisplayName("§" + color.getChatValue() + "Christmas surprise");
			iAmDrunk.setItemMeta(meta);
			p.getInventory().addItem(iAmDrunk);
			Messenger.getInstance().sendMessage(p, "§cYou stole a christmas surprise! Santa Claus won't be happy!");
		}
	}

}

package me.micrjonas.grandtheftdiamond.item.pluginitem;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import me.micrjonas.grandtheftdiamond.GrandTheftDiamond;
import me.micrjonas.grandtheftdiamond.GrandTheftDiamondPlugin;
import me.micrjonas.grandtheftdiamond.data.FileReloadListener;
import me.micrjonas.grandtheftdiamond.data.PluginFile;
import me.micrjonas.grandtheftdiamond.listener.player.PlayerInteractListener;
import me.micrjonas.grandtheftdiamond.manager.Manager;
import me.micrjonas.grandtheftdiamond.messenger.Messenger;

import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;


/**
 * Manages the plugin's items
 */
public class ItemManager implements FileReloadListener, Manager<PluginItem> {

	private final static ItemManager instance = new ItemManager();
	
	private final static String PATH = "objects.";
	private final static String CARS_PATH = PATH + "cars";
	private final static String DRUGS_PATH = "drugs";
	private final static String FIREARMS_PATH = PATH + "firearms";
	private final static String FLAME_THROWER_PATH = PATH + "flamethrower";
	private final static String HANDCUFFS_PATH = PATH + "handcuffs";
	private final static String TASER_PATH = PATH + "taser";
	
	/**
	 * Returns the loaded instance
	 * @return The loaded instance
	 */
	public static ItemManager getInstance() {
		return instance;
	}
	
	/**
	 * Adds the item to the {@link Player}'s {@link Inventory} or spawns it for him. Also sends a message
	 * 	to the {@link Player}
	 * @param p The {@link Player}
	 * @param item The item to add
	 * @param amount The amount of items
	 * @throws IllegalArgumentException Thrown if {@code item} is {@code null}
	 * @see PluginItem#giveToPlayer(Player, int)
	 */
	public static void giveToPlayer(Player p, PluginItem item, int amount) throws IllegalArgumentException {
		item.giveToPlayer(p, amount);
		Messenger.getInstance().sendPluginMessage(p, "itemReceived", new String[]{"%item%"}, new String[]{item.getName()});
	}
	
	private static Map<String, ConfigurationSection> getEntries(FileConfiguration fileConfiguration, String path, String subPath) {
		Map<String, ConfigurationSection> entries = new HashMap<String, ConfigurationSection>();
		for (String absolutePath : fileConfiguration.getConfigurationSection(path).getKeys(false)) {
			ConfigurationSection configSection;
			if (subPath == null) {
				configSection = fileConfiguration.getConfigurationSection(path + "." + absolutePath);
			}
			else {
				configSection = fileConfiguration.getConfigurationSection(path + "." + absolutePath + "." + subPath);
			}
			if (configSection != null) {
				entries.put(absolutePath, configSection);
			}
		}
		return entries;
	}
// End of static
	
	private final Firearms firearmsManager = new Firearms();
	private final Map<String, PluginItem> items = new HashMap<>();
	private final Map<String, PluginItem> customItems = new HashMap<>();
	
	private ItemManager() {
		GrandTheftDiamond.registerFileReloadListener(this);
	}
	
	@Override
	public void configurationReloaded(PluginFile file, FileConfiguration fileConfiguration) {
		if (file == PluginFile.CONFIG) {
			items.clear();
			items.putAll(customItems);
			// Cars
			for (Entry<String, ConfigurationSection> entry : getEntries(fileConfiguration, CARS_PATH, null).entrySet()) {
				registerItem(new Car(entry.getKey(), entry.getValue()), entry.getKey());
			}
			// Drugs
			for (Entry<String, ConfigurationSection> entry : getEntries(fileConfiguration, DRUGS_PATH, "item").entrySet()) {
				Drug drug = new Drug(entry.getKey(), entry.getValue());
				registerInteractableItem(drug, entry.getKey(), drug.getItem(1));
			}
			for (Entry<String, ConfigurationSection> entry : getEntries(fileConfiguration, FIREARMS_PATH, null).entrySet()) {
				Firearm firearm = new Firearm(entry.getKey(), entry.getValue(), firearmsManager);
				registerInteractableItem(firearm, entry.getKey(), firearm.getItem(1));
			}
			// Flame thrower
			FlameThrower flameThrower = new FlameThrower(fileConfiguration.getConfigurationSection(FLAME_THROWER_PATH));
			registerInteractableItem(flameThrower, FlameThrower.NAME, flameThrower.getItem(1));
			// Handcuffs
			Handcuffs handcuffs = new Handcuffs(fileConfiguration.getConfigurationSection(HANDCUFFS_PATH));
			registerInteractableItem(handcuffs, Handcuffs.NAME, handcuffs.getItem(1));
			// Jetpack
			// Knife
			// Taser
			Taser taser = new Taser(fileConfiguration.getConfigurationSection(TASER_PATH));
			registerInteractableItem(taser, Taser.NAME, taser.getItem(1));
		}
	}
	
	@Override
	public Collection<PluginItem> getAllObjects() {
		return Collections.unmodifiableCollection(items.values());
	}
	
	/**
	 * Registers an item to get it with {@link #getItem(String)}
	 * @param item The item to register
	 * @param name The name of the item. Ignores case-sensitive
	 * @throws IllegalArgumentException Thrown if {@code item} or {@code name} is {@code null} or if an item with
	 *  the name is already registered
	 */
	public void registerItem(PluginItem item, String name) throws IllegalArgumentException {
		if (item == null) {
			throw new IllegalArgumentException("Item to register is not allowed to be null");
		}
		if (name == null) {
			throw new IllegalArgumentException("Name of item to register is not allowed to be null");
		}
		name = name.toLowerCase();
		if (items.containsKey(name)) {
			throw new IllegalArgumentException("Item with name '" + name + "' is already registered");
		}
		items.put(name, item);
	}
	
	/**
	 * Registers an {@link InteractablePluginItem}. If an other {@link ItemStack} will be passed, the old one will be
	 * 	overridden. The method calls {@link #registerItem(PluginItem, String)} first
	 * @param item The item to register
	 * @param name The name of the item. Ignores case-sensitive
	 * @param interactItem The {@code ItemStack}, the {@link PlayerInteractListener} should listen to on interact
	 * @throws IllegalArgumentException Thrown if {@code item}, {@code name} or @ {@code interactItem} is
	 * 	{@code null} or if an item with the name is already registered
	 */
	public void registerInteractableItem(InteractablePluginItem item, String name, ItemStack interactItem)
			throws IllegalArgumentException {
		registerItem(item, name);
		if (interactItem == null) {
			items.remove(name.toLowerCase());
			throw new IllegalArgumentException("Item to interact is not allowed to be null");
		}
		interactItem = interactItem.clone();
		GrandTheftDiamondPlugin.getInstance().getRegisteredListener(PlayerInteractListener.class)
				.regiserItem(item, interactItem);
	}
	
	/**
	 * Unregisters a {@code PluginItem} if registered. Unregisters also the {@link InteractablePluginItem} if it is one
	 * @param name The name of the registered {@link PluginItem}
	 * @throws IllegalArgumentException Thrown if {@code name} is {@code null}
	 */
	public void unregisterItem(String name) throws IllegalArgumentException {
		if (name == null) {
			throw new IllegalArgumentException("Name is not allowed to be null");
		}
		PluginItem item = items.remove(name.toLowerCase());
		if (item instanceof InteractablePluginItem) {
			GrandTheftDiamondPlugin.getInstance().getRegisteredListener(PlayerInteractListener.class)
					.unregisterItem((InteractablePluginItem) item);
		}
	}
	
	/**
	 * Returns a {@link PluginItem} by name. Ignores case-sensitive
	 * @param name The item's name
	 * @return A {@link PluginItem} by name. {@code null} if no {@link PluginItem} is registered with this name
	 * @throws IllegalArgumentException Thrown if {@code name} is {@code null}
	 */
	public PluginItem getItem(String name) throws IllegalArgumentException {
		if (name == null) {
			throw new IllegalArgumentException("Name is not allowed to be null");
		}
		return items.get(name.toLowerCase());
	}
	
	/**
	 * Checks whether an item is registered by a given name
	 * @param name The name to check
	 * @return True if the an item with the {@code name} is registered, else {@code false}
	 * @throws IllegalArgumentException Thrown if {@code name} is {@code null}
	 */
	public boolean isItem(String name) throws IllegalArgumentException {
		if (name == null) {
			throw new IllegalArgumentException("Name is not allowed to be null");
		}
		return items.containsKey(name.toLowerCase());
	}
	
	/**
	 * Returns a new sorted {@link List} with all item names
	 * @return A new sorted {@link List} as a copy of all registered items
	 */
	public List<String> getAllItemsSorted() {
		List<String> list = new ArrayList<String>(items.keySet());
		Collections.sort(list);
		return list;
	}
	
}

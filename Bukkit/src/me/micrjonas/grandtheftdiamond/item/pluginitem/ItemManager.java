package me.micrjonas.grandtheftdiamond.item.pluginitem;

import java.util.AbstractMap.SimpleEntry;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import me.micrjonas.grandtheftdiamond.GrandTheftDiamond;
import me.micrjonas.grandtheftdiamond.BukkitGrandTheftDiamondPlugin;
import me.micrjonas.grandtheftdiamond.Team;
import me.micrjonas.grandtheftdiamond.data.FileManager;
import me.micrjonas.grandtheftdiamond.data.FileReloadListener;
import me.micrjonas.grandtheftdiamond.data.PluginFile;
import me.micrjonas.grandtheftdiamond.item.Kit;
import me.micrjonas.grandtheftdiamond.listener.player.PlayerInteractListener;
import me.micrjonas.grandtheftdiamond.manager.Manager;
import me.micrjonas.grandtheftdiamond.messenger.Messenger;
import me.micrjonas.grandtheftdiamond.util.Enums;
import me.micrjonas.grandtheftdiamond.util.StringUtils;
import me.micrjonas.grandtheftdiamond.util.bukkit.LeveledEnchantment;
import me.micrjonas.grandtheftdiamond.util.bukkit.Materials;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.ShapedRecipe;
import org.bukkit.inventory.meta.ItemMeta;


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
	 * @throws IllegalArgumentException Thrown if {@code item} or {@code p} is {@code null} or if the amount is invalid
	 * @see PluginItem#giveToPlayer(Player, int)
	 */
	public static void giveToPlayer(Player p, PluginItem item, int amount) throws IllegalArgumentException {
		if (item == null) {
			throw new IllegalArgumentException("Item is not allowed to be null");
		}
		item.giveToPlayer(p, amount);
		Messenger.getInstance().sendPluginMessage(p, "itemReceived", new String[]{"%item%"}, new String[]{item.getName()});
	}
	
	/**
	 * Creates an {@link ItemStack} with optional item meta
	 * @param type The type of the {@link ItemStack}
	 * @param amount The amount of the {@link ItemStack}
	 * @param name The display name of the {@link ItemStack}. May be {@code null}
	 * @param lore The lore of the {@link ItemStack}. May be {@code null}
	 * @param enchantments The enchantments of the {@link ItemStack}. May be {@code null}
	 * @return The new {@link ItemStack}
	 * @throws IllegalArgumentException Thrown if {@code type} is {@code null} or {@code} is invalid
	 */
	public static ItemStack createItem(Material type, int amount, String name, List<String> lore,
			Collection<LeveledEnchantment> enchantments) throws IllegalArgumentException {
		if (type == null) {
			throw new IllegalArgumentException("Type of item is not allowed to be null");
		}
		if (lore != null) {
			for (int i = 0; i < lore.size(); i++) {
				lore.set(i, StringUtils.translateColors(lore.get(i)));
			}
		}
		ItemStack item = new ItemStack(type, amount);
	    ItemMeta meta = item.getItemMeta();
	    if (name != null) {
	    	meta.setDisplayName(StringUtils.translateColors(name));
	    }
	    meta.setLore(lore);
	    item.setItemMeta(meta);
	    if (enchantments != null) {
		    for (LeveledEnchantment enchantment : enchantments) {
		    	enchantment.addToItem(item, true);	
		    }
	    }
	    return item;
	}
	
	/**
	 * Creates an {@link ItemStack} with optional item meta
	 * @param type The type of the {@link ItemStack}
	 * @param amount The amount of the {@link ItemStack}
	 * @param name The display name of the {@link ItemStack}. May be {@code null}
	 * @param lore The lore of the {@link ItemStack}. May be {@code null}
	 * @return The new {@link ItemStack}
	 * @throws IllegalArgumentException Thrown if {@code type} is {@code null} or {@code} is invalid
	 * @see #createItem(Material, int, String, List, Collection)
	 */
	public static ItemStack createItem(Material type, int amount, String name, List<String> lore) {
		return createItem(type, amount, name, lore, null);
	}
	
	/**
	 * Creates a new {@link ItemStack} with data of a {@link Map}
	 * @param data The {@link ItemStack}'s data
	 * @param useAmountFromMap Whether the amount stored in the {@link Map} should be use or 1
	 * @return The new created {@link ItemStack}. May be {@code null} if some required data is missing in the {@link Map}
	 * @throws IllegalArgumentException Thrown if {@code data} is {@code null}
	 */
	@SuppressWarnings("unchecked")
	public static ItemStack getItemFromMap(Map<String, Object> data, boolean useAmountFromMap) throws IllegalArgumentException {
		if (data == null) {
			throw new IllegalArgumentException("Item data is not allowed to be null");
		}
		if (!data.containsKey("item")) {
			return null;
		}
		Material type = Materials.getMaterialOrDefault((String) data.get("item"), null);
		int amount = useAmountFromMap && data.containsKey("amount") && data.get("amount") instanceof Integer ? (int) data.get("amount") : 1;
		if (type == null) {
			return null;
		}
		String name = data.containsKey("name") ? (String) data.get("name") : null;
		List<String> lore = null;
		if (data.containsKey("lore")) {
			if (data.get("lore") instanceof List) {
				lore = (List<String>) data.get("lore");
			}
			else if (data.get("lore") instanceof String) {
				lore = Arrays.asList((String) data.get("lore"));
			}
		}
		Collection<LeveledEnchantment> enchantments = null;
		if (data.containsKey("enchantments")) {
			if (data.get("enchantments") instanceof List) {
				enchantments = getEnchantments((List<String>) data.get("enchantments"));
			}
			else if (data.get("enchantments") instanceof String) {
				enchantments = new HashSet<>();
				enchantments.add(getEnchantment((String) data.get("enchantments")));
			}
		}
		return createItem(type, amount, name, lore, enchantments);
	}
	
	@SuppressWarnings("deprecation")
	public static ItemStack getItemFromSection(ConfigurationSection section, boolean useAmountFromConfig) {
		//Item
		Set<LeveledEnchantment> enchantments = null; 
		if (section.isList("enchantments")) {
			enchantments = getEnchantments(section.getStringList("enchantments"));
		}
		Material type = Enums.valueOf(Material.class, section.getString("type"));
		if (type == null) {
			type = Enums.valueOf(Material.class, section.getString("item"));
		}
		if (type == null) {
			try {
				type = Material.getMaterial(section.getInt("item"));
			}
			catch (NumberFormatException ex) {
				return null;
			}
		}
		if (type == null) {
			return null;
		}
		ItemStack item = null;
		item = createItem(type,
				useAmountFromConfig && section.getInt("amount") > 0 ? section.getInt("amount") : 1, 
				section.getString("name"), 
				section.getStringList("lore"),
				enchantments);	
		
	//Recipe
		if (section.isList("craftRecipe")) {
			ShapedRecipe recipe = new ShapedRecipe(item);
			List<String> configList = section.getStringList("craftRecipe");
			String[] items = new String[configList.size()];
			List<Entry<Character, Material>> ingredients = new ArrayList<>();
			int longest = 0;
			char slot = '0';
			for (int i = 0; i < configList.size(); i++) {
				String recipeLine = "";
				String[] split = configList.get(i).split(" ");
				for (int j = 0; j < split.length && j < 3; j++, slot++) {
					Material required = Material.getMaterial(split[j].toUpperCase());
					recipeLine = recipeLine + slot;
					if (required != null && required != Material.AIR) {
						ingredients.add(new SimpleEntry<>(slot, required));
					}
				}
				if (longest < recipeLine.length()) {
					longest = recipeLine.length();
				}
				items[i] = recipeLine;
			}
			
			for (int i = 0; i < items.length; i++) {
				while (items[i].length() < longest) {
					items[i] = items[i] + "-";
				}
			}
			recipe.shape(items);
			for (Entry<Character, Material> entry : ingredients) {
				recipe.setIngredient(entry.getKey(), entry.getValue());
			}
			Bukkit.addRecipe(recipe);
		}
		return item;
	}
	
	/**
	 * Loads an {@link ItemStack} from a {@link PluginFile}
	 * @param file The file to load from
	 * @param path The path to load from
	 * @param useAmountFromConfig Whether the configured amount should be use or 1
	 * @return The new {@link ItemStack}. May be {@code null} if some required data is missing
	 * @throws IllegalArgumentException Thrown if {@code file} or {@code path} is {@code null} or if the path is not a
	 * 	{@link ConfigurationSection}
	 * @see #getItemFromSection(ConfigurationSection, boolean)
	 */
	public static ItemStack loadItemFromFile(PluginFile file, String path, boolean useAmountFromConfig) throws IllegalArgumentException {
		if (file == null) {
			throw new IllegalArgumentException("File is not allowed to be null");
		}
		if (path == null) {
			throw new IllegalArgumentException("Path is not allowed to be null");
		}
		ConfigurationSection section = FileManager.getInstance().getFileConfiguration(file).getConfigurationSection(path);
		if (section == null) {
			throw new IllegalArgumentException("Invalid path. Path does not represent a ConfigurationSection");
		}
		return getItemFromSection(section, useAmountFromConfig);
	}
	
	/**
	 * Saves an {@link ItemStack} to a {@link Map}}
	 * @param item The {@link ItemStack} to store
	 * @return A new {@link Map} with the {@code item}'s data
	 */
	public static Map<String, Object> itemToMap(ItemStack item) {
		if (item == null) {
			return null;
		}
		Map<String, Object> data = new LinkedHashMap<>();
		data.put("item", item.getType().name());
		data.put("amount", item.getAmount());
		if (item.hasItemMeta()) {
			if (item.getItemMeta().hasDisplayName()) {
				data.put("name", item.getItemMeta().getDisplayName());
			}
			if (item.getItemMeta().hasLore()) {
				data.put("lore", item.getItemMeta().getLore());
			}
		}
		Map<Enchantment, Integer> enchantments = item.getEnchantments();
		if (enchantments.size() > 0) {
			List<String> enchantmentData = new ArrayList<>();
			for (Entry<Enchantment, Integer> ench : enchantments.entrySet()) {
				enchantmentData.add(ench.getKey().getName() + " " + ench.getValue());
			}
			data.put("enchantments", enchantmentData);
		}
		return data;
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
	
	private static Set<LeveledEnchantment> getEnchantments(List<String> enchantmentConfigurations) {
		Set<LeveledEnchantment> enchantments = new HashSet<>();
		for (String enchantmentParts : enchantmentConfigurations) {
			LeveledEnchantment enchantment = getEnchantment(enchantmentParts);
			if (enchantment != null) {
				enchantments.add(enchantment);
			}
		}
		return enchantments;
	}
	
	@SuppressWarnings("deprecation")
	private static LeveledEnchantment getEnchantment(String enchantmentConfiguration) {
		String[] parts = enchantmentConfiguration.split(" ");
		Enchantment enchantment = null;
		try {
			enchantment = Enchantment.getByName(parts[0].toUpperCase());
		}
		catch (IllegalArgumentException ex) {
			try {
				enchantment = Enchantment.getById(Integer.parseInt(parts[0]));
			}
			catch (NumberFormatException ex2) { }
		}
		if (enchantment != null) {
			int level = 1;
			if (parts.length > 1) {
				try {
					level = Integer.parseInt(parts[1]);
				}
				catch (NumberFormatException ex) { }
			}
			if (level > enchantment.getMaxLevel()) {
				level = enchantment.getMaxLevel();
			}
			else if (level < enchantment.getStartLevel()) {
				level = enchantment.getStartLevel();
			}
			return new LeveledEnchantment(enchantment, level);
		}
		return null;
	}
// End of static
	
	private final Firearms firearmsManager = new Firearms();
	private final Map<String, PluginItem> items = new HashMap<>();
	private final Map<String, PluginItem> customItems = new HashMap<>();
	private final Map<String, Kit> kits = new HashMap<>();
	@SuppressWarnings("unchecked")
	private final Collection<Kit>[] startKits = new Collection[2];
	
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
			
			for (String kitName : fileConfiguration.getConfigurationSection("kits").getKeys(false)) {
				Kit kit = new Kit(kitName);
				if (fileConfiguration.isList("kits." + kitName + ".items")) {
					for (Object item : fileConfiguration.getList("kits." + kitName + ".items")) {
						if (item instanceof Map) {
							@SuppressWarnings("unchecked")
							ItemStack itemStack = getItemFromMap((Map<String, Object>) item, true);
							if (itemStack != null) {
								kit.addItem(itemStack);		
							}	
						}
					}
				}
				kits.put(kitName.toLowerCase(), kit);
			}
			
			for (Team team : Team.getRealTeams()) {
				List<Kit> startKits = new ArrayList<>();
				for (String kitName : fileConfiguration.getStringList("startKits." + team.name().toLowerCase())) {
					Kit kit = getKit(kitName);
					if (kit != null) {
						startKits.add(kit);
					}
				}
				this.startKits[team.ordinal()] = startKits;
			}
		}
	}
	
	@Override
	public Collection<PluginItem> getAllObjects() {
		return Collections.unmodifiableCollection(items.values());
	}
	
	/**
	 * Returns a {@link Kit} by name. Ignores case-sensitive
	 * @param name The name of the {@link Kit}
	 * @return The registered {@link Kit}. {@code null} if the {@link Kit} is not registered
	 * @throws IllegalArgumentException Thrown if {@code name} is {@code null}
	 */
	public Kit getKit(String name) throws IllegalArgumentException {
		if (name == null) {
			throw new IllegalArgumentException("Name is not allowed to be null");
		}
		return kits.get(name.toLowerCase());
	}
	
	/**
	 * Returns a {@link Collection} of all start {@link Kit}s of a specific {@link Team}. The {@link Collection} is not
	 *  modifiable. See: {@link Collections#unmodifiableCollection(Collection)}
	 * @param team The {@link Team}
	 * @return A {@link Collection} of all start {@link Kit}s of a specific {@link Team}. May be empty, but is never {@code null}
	 * @throws IllegalArgumentException Thrown if {@code team} is not a real {@link Team}
	 * @see Collections#unmodifiableCollection(Collection)
	 * @see Team#requiresRealTeam(Team)
	 */
	public Collection<Kit> getStartKits(Team team) throws IllegalArgumentException {
		Team.requiresRealTeam(team);
		return Collections.unmodifiableCollection(startKits[team.ordinal()]);
	}
	
	/**
	 * Returns a {@link Collection} of all start {@link Kit}s for a {@link Player} and does a permission check for
	 * 	each {@link Kit}. Permission is <i>grandtheftdiamond.startkit.{@link Kit#getName()}</i>
	 * @param team The {@link Player}'s {@link Team}
	 * @param p The {@link Player}
	 * @return A {@link Collection} of all start {@link Kit}s for the {@link Player}
	 * @throws IllegalArgumentException Thrown if {@code team} is not a real {@link Team} or {@code p} is {@code null}
	 * @see Team#requiresRealTeam(Team)
	 */
	public Collection<Kit> getPlayerStartKits(Team team, Player p) throws IllegalArgumentException {
		if (p == null) {
			throw new IllegalArgumentException("Player is not allowed to be null");
		}
		List<Kit> playerKits = new ArrayList<Kit>(getStartKits(team).size());
		for (Kit kit : getStartKits(team)) {
			if (GrandTheftDiamond.checkPermission(p, "startkit." + kit.getName())) {
				playerKits.add(kit);
			}
		}
		return playerKits;
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
		BukkitGrandTheftDiamondPlugin.getInstance().getRegisteredListener(PlayerInteractListener.class)
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
			BukkitGrandTheftDiamondPlugin.getInstance().getRegisteredListener(PlayerInteractListener.class)
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

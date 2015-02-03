package me.micrjonas.grandtheftdiamond.item;

import java.util.AbstractMap.SimpleEntry;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.EnumMap;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import me.micrjonas.grandtheftdiamond.GrandTheftDiamond;
import me.micrjonas.grandtheftdiamond.Team;
import me.micrjonas.grandtheftdiamond.data.FileManager;
import me.micrjonas.grandtheftdiamond.data.PluginFile;
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
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.ShapedRecipe;
import org.bukkit.inventory.meta.ItemMeta;

public class ItemManager {
	
	private static ItemManager instance = new ItemManager();
	
	public static ItemManager getInstance() {
		
		return instance;
		
	}
	
	private static Map<PluginFile, Map<String, ItemStack>> itemsFromConfig = new EnumMap<>(PluginFile.class);
	
	private final Map<String, Kit> kits = new HashMap<>();
	private final Map<Team, List<Kit>> startKits = new EnumMap<>(Team.class);
	
	private ItemManager() {
		
		init();
		
	}
	
	
	@SuppressWarnings("unchecked")
	private void init() {
		
		FileConfiguration config = FileManager.getInstance().getFileConfiguration(PluginFile.CONFIG);
		
		for (String kitName : config.getConfigurationSection("kits").getKeys(false)) {
			
			Kit kit = new Kit(kitName);
			
			if (config.isList("kits." + kitName + ".items")) {
				
				for (Object item : config.getList("kits." + kitName + ".items"))
					kit.addItem(getItemFromMap((Map<String, Object>) item, true));
				
			}
			
			kits.put(kitName.toLowerCase(), kit);
			
		}
		
		
		for (Team team : Team.getRealTeams()) {
			
			List<Kit> startKits = new ArrayList<>();
			
			for (String kitName : config.getStringList("startKits." + team.name().toLowerCase())) {
				
				Kit kit = getKit(kitName);
				
				if (kit != null)
					startKits.add(kit);
				
			}
			
			this.startKits.put(team, Collections.unmodifiableList(startKits));
			
		}
		
	}
	
	
	public List<Kit> getStartKits(Team team, Player p) throws IllegalArgumentException {
		
		if (team == null)
			throw new IllegalArgumentException("team is not allowed to be null");
		
		if (p == null)
			throw new IllegalArgumentException("p is not allowed to be null");
		
		List<Kit> startKits = new ArrayList<>();
		
		for (Kit kit : this.startKits.get(team)) {
			
			if (GrandTheftDiamond.checkPermission(p, "startkit." + kit.getName()))
				startKits.add(kit);
			
		}
		
		return Collections.unmodifiableList(startKits);

	}
	
	
	public Kit getKit(String name) throws IllegalArgumentException {
		
		if (name == null)
			throw new IllegalArgumentException("name is not allowed to be null");
		
		return kits.get(name.toLowerCase());
		
	}
	
	
	@SuppressWarnings("deprecation")
	public static void addItem(Player p, ItemStack item) {
		
		p.getInventory().addItem(item);
		p.updateInventory();
		
	}
	
	
	public static ItemStack getItemFromMap(Map<String ,Object> item) {
		
		return getItemFromMap(item, true);
		
	}
	
	
	@SuppressWarnings("unchecked")
	private static ItemStack getItemFromMap(Map<String ,Object> item, boolean useAmountFromConfig) {
		
		if (!item.containsKey("item"))
			return null;
		
		Material type = Materials.getMaterialOrDefault((String) item.get("item"), null);
		int amount = useAmountFromConfig && item.containsKey("amount") && item.get("amount") instanceof Integer ? (int) item.get("amount") : 1;
		
		if (type == null)
			return null;
		
		String name = item.containsKey("name") ? (String) item.get("name") : null;
		
		List<String> lore = null;
		if (item.containsKey("lore")) {
			
			if (item.get("lore") instanceof List)
				lore = (List<String>) item.get("lore");
			
			else if (item.get("lore") instanceof String)
				lore = Arrays.asList((String) item.get("lore"));
			
		}
		
		Set<LeveledEnchantment> enchantments = null;
		if (item.containsKey("enchantments")) {
			
			if (item.get("enchantments") instanceof List)
				enchantments = getEnchantments((List<String>) item.get("enchantments"));
			
			else if (item.get("enchantments") instanceof String) {
				
				enchantments = new HashSet<>();
				enchantments.add(getEnchantment((String) item.get("enchantments")));
				
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
						
						if (required != null && required != Material.AIR)
							ingredients.add(new SimpleEntry<>(slot, required));
						
					}
					
					if (longest < recipeLine.length())
						longest = recipeLine.length();
					
					items[i] = recipeLine;
					
				}
				
				for (int i = 0; i < items.length; i++) {
					
					while (items[i].length() < longest)
						items[i] = items[i] + "-";
					
				}
				
				recipe.shape(items);
				
				for (Entry<Character, Material> entry : ingredients)
					recipe.setIngredient(entry.getKey(), entry.getValue());
				
				Bukkit.addRecipe(recipe);
				
			}
			
			return item;
	}
	
	
	public static ItemStack loadItemFromFile(PluginFile file, String path, boolean useAmountFromConfig) {
		
		if (itemsFromConfig == null)
			itemsFromConfig = new EnumMap<>(PluginFile.class);
		
		Map<String, ItemStack> map = itemsFromConfig.get(file);
		
		if (map != null) {
			
			ItemStack item = map.get(path);
			
			if (item != null)
				return item;
			
		}
		
		else {
			
			map = new HashMap<>();
			itemsFromConfig.put(file, map);
			
		}
		
		ItemStack item = getItemFromSection(FileManager.getInstance().getFileConfiguration(file).getConfigurationSection(path), useAmountFromConfig);
		map.put(path, item);
		return item;
	}

	
	public static Map<String, Object> toMap(ItemStack item) {
		
		if (item == null)
			return null;
		
		Map<String, Object> data = new LinkedHashMap<>();
		
		data.put("item", item.getType().name());
		data.put("amount", item.getAmount());
		
		if (item.hasItemMeta()) {
			
			if (item.getItemMeta().hasDisplayName())
				data.put("name", item.getItemMeta().getDisplayName());
				
			if (item.getItemMeta().hasLore())
				data.put("lore", item.getItemMeta().getLore());
			
		}
		
		Map<Enchantment, Integer> enchantments = item.getEnchantments();
		
		if (enchantments.size() > 0) {
			
			List<String> enchantmentData = new ArrayList<>();
			
			for (Entry<Enchantment, Integer> ench : enchantments.entrySet())
				enchantmentData.add(ench.getKey().getName() + " " + ench.getValue());
			
			data.put("enchantments", enchantmentData);
			
		}
		
		return data;
		
	}
	
	
	public static ItemStack createItem(Material type, int amount, String name, List<String> lore, Set<LeveledEnchantment> enchantments) {
		
		if (type == null || type == Material.AIR)
			return new ItemStack(Material.AIR);
		
		if (lore != null) {
			
			for (int i = 0; i < lore.size(); i++)
				lore.set(i, StringUtils.translateColors(lore.get(i)));
			
		}
		
		ItemStack item = new ItemStack(type, amount);
	    ItemMeta im = item.getItemMeta();
	    
	    if (name != null)
	    	im.setDisplayName(StringUtils.translateColors(name));
	    
	    if (lore != null) {
	    	
	    	for (int i = 0; i < lore.size(); i++)
	    		lore.set(i, StringUtils.translateColors(lore.get(i)));
	    	
	    }
	    
	    im.setLore(lore);
	    item.setItemMeta(im);
	    
	    if (enchantments != null) {
	    	
		    for (LeveledEnchantment enchantment : enchantments)
		    	enchantment.addToItem(item, true);	
	    	
	    }
	    
	    return item;
		
	}
	
	
	
	public static ItemStack createItem(Material type, int amount, String name, List<String> lore) {
		
		return createItem(type, amount, name, lore, null);
	    
	}
	
	
	private static Set<LeveledEnchantment> getEnchantments(List<String> enchantmentConfigurations) {
		
		Set<LeveledEnchantment> enchantments = new HashSet<>();
		
		for (String enchantmentParts : enchantmentConfigurations) {
			
			LeveledEnchantment enchantment = getEnchantment(enchantmentParts);
			
			if (enchantment != null)
				enchantments.add(enchantment);
			
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
			
			if (level > enchantment.getMaxLevel())
				level = enchantment.getMaxLevel();
			
			else if (level < enchantment.getStartLevel())
				level = enchantment.getStartLevel();
			
			return new LeveledEnchantment(enchantment, level);
			
		}
		
		return null;
		
	}

}

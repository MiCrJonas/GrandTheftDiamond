package me.micrjonas.grandtheftdiamond.dealer;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import com.nisovin.shopkeepers.Shopkeeper;
import com.nisovin.shopkeepers.shopobjects.ShopObjectType;
import com.nisovin.shopkeepers.shoptypes.ShopkeeperType;

public class ShopkeeperDealer extends Shopkeeper {

	private Map<UUID, ItemStack[]> recipes = new LinkedHashMap<>();
	
	public ShopkeeperDealer(Location location, ShopObjectType objectType) {
		
		super(location, objectType);
		
	}

	
	@Override
	public List<ItemStack[]> getRecipes() {
		
		List<ItemStack[]> recipes = new ArrayList<>();
		
		for (ItemStack[] recipe : this.recipes.values())
			recipes.add(recipe);
		
		return recipes;
		
	}
	

	@Override
	public ShopkeeperType getType() {
		
		return ShopkeeperType.ADMIN;
		
	}

	@Override
	public boolean onEdit(Player p) {
		
		return true;
		
	}

	@Override
	public void onEditorClose(InventoryCloseEvent e) {
		
		// Empty, can't edit shopkeeper
		
	}

	@Override
	public void onPurchaseClick(InventoryClickEvent e) {
		
		e.setCancelled(true);
		
	}

	@Override
	protected void saveEditor(Inventory inv, Player p) {
		
		// Empty, can't edit shopkeeper
		
	}
	
	
	/**
	 * Returns the recipe by the given id<br>
	 * @param id The id of the recipe
	 * @return The recipe by the given id
	 */
	public ItemStack[] getRecipe(UUID id) {
		
		return recipes.get(id);
		
	}
	
	
	/**
	 * Adds a new recipe to the shopkeeper
	 * @param price0 The first price, required non-null
	 * @param price1 The second price, can be null
	 * @param result The result, required non-null
	 * @return The UUID of the recipe
	 */
	public UUID addRecipe(ItemStack price0, ItemStack price1, ItemStack result) {
		
		if (price0 == null)
			throw new IllegalArgumentException("price0 is not allowed to be null");
		
		if (price0.getType() == Material.AIR)
			throw new IllegalArgumentException("type of price0 cannot me Material.AIR");
		
		if (result == null)
			throw new IllegalArgumentException("result is not allowed to be null");
		
		if (result.getType() == Material.AIR)
			throw new IllegalArgumentException("type of result cannot me Material.AIR");
		
		if (price1 != null && price1.getType() == Material.AIR)
			price1 = null;
		
		ItemStack[] recipe = new ItemStack[]{price0, price1, result};
		
		UUID randomId = UUID.randomUUID();
		recipes.put(randomId, recipe);
		
		return randomId;
		
	}
	
	
	/**
	 * Removed the recipe by the given id
	 * @param id The id of the recipe
	 */
	public void removeRecipe(UUID id) {
		
		recipes.remove(id);
		
	}

}

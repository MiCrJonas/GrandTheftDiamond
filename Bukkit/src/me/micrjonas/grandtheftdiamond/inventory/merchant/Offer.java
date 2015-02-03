package me.micrjonas.grandtheftdiamond.inventory.merchant;

import java.util.UUID;

import me.micrjonas.grandtheftdiamond.inventory.merchant.ReflectionUtils.NMSMerchantRecipe;
import me.micrjonas.grandtheftdiamond.inventory.merchant.ReflectionUtils.OBCCraftItemStack;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

public class Offer {
	
	private UUID id = UUID.randomUUID();
	private ItemStack price0;
	private ItemStack price1;
	private ItemStack result;
	
	public Offer(ItemStack price0, ItemStack price1, ItemStack result) {
		
		setPrice0(price0);
		setPrice1(price1);
		
		this.price0 = price0;
		this.result = result;
		
	}
	
	
	public Offer(ItemStack price0, ItemStack result) {
		
		this(price0, null, result);
		
	}
	
	
	public Offer(NMSMerchantRecipe handle) {
		
		price0 = OBCCraftItemStack.asBukkitCopy(handle.getBuyItem1());
		price1 = handle.getBuyItem2() == null ? null : OBCCraftItemStack.asBukkitCopy(handle.getBuyItem2());
		result = OBCCraftItemStack.asBukkitCopy(handle.getBuyItem3());

	}
	

	@Override
	public String toString() {
		
		return "Offer [id=" + id + ", price0=" + price0 + ", price1=" + price1
				+ ", result=" + result + "]";
		
	}


	@Override
	public int hashCode() {
		
		final int prime = 31;
		int result = 1;
		
		result = prime * result + ((price0 == null) ? 0 : price0.hashCode());
		result = prime * result + ((price1 == null) ? 0 : price1.hashCode());
		result = prime * result + ((this.result == null) ? 0 : this.result.hashCode());
		
		return result;
	}


	@Override
	public boolean equals(Object obj) {
		
		if (this == obj)
			return true;
		
		if (obj == null)
			return false;
		
		if (!(obj instanceof Offer))
			return false;
		
		Offer other = (Offer) obj;
		
		if (price0 == null) {
			
			if (other.price0 != null)
				return false;
			
		} 
		
		else if (!price0.equals(other.price0))
			return false;
		
		if (price1 == null) {
			
			if (other.price1 != null)
				return false;
			
		} 
		
		else if (!price1.equals(other.price1))
			return false;
		
		if (result == null) {
			if (other.result != null)
				return false;
			
		} 
		
		else if (!result.equals(other.result))
			return false;
		
		return true;
		
	}
	
	
	public NMSMerchantRecipe getHandle() {
		
		if (price1 == null)
			return new NMSMerchantRecipe(OBCCraftItemStack.asNMSCopy(price1), OBCCraftItemStack.asNMSCopy(result));
		
		else
			return new NMSMerchantRecipe(OBCCraftItemStack.asNMSCopy(price0), OBCCraftItemStack.asNMSCopy(price1), OBCCraftItemStack.asNMSCopy(result));
	
	}

	
	/**
	 * Returns the id of the Offer
	 * @return The unique id of the offer
	 */
	public UUID getUniqueId() {
		
		return id;
		
	}
	

	public ItemStack getPrice0() {
		
		return price0.clone();
		
	}


	public void setPrice0(ItemStack price) {
		
		if (price == null)
			throw new IllegalArgumentException("price is not allowed to be null");
		
		if (price.getType() == Material.AIR)
			throw new IllegalArgumentException("type of price cannot me Material.AIR");
		
		this.price0 = price.clone();
		
	}


	public ItemStack getPrice1() {
		
		if (price1 == null)
			return null;
		
		return price1.clone();
		
	}


	public void setPrice1(ItemStack price) {
		
		if (price == null)
			this.price1 = null;
		
		else if (price.getType() == Material.AIR)
			this.price1 = null;
		
		else
			this.price1 = price.clone();
		
	}


	public ItemStack getResult() {
		
		return result.clone();
		
	}


	public void setResult(ItemStack result) {
		
		if (result == null)
			throw new IllegalArgumentException("result is not allowed to be null");
		
		if (result.getType() == Material.AIR)
			throw new IllegalArgumentException("type of result cannot me Material.AIR");
		
		this.result = result.clone();
		
	}

}

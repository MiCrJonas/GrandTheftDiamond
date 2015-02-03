package me.micrjonas.grandtheftdiamond.inventory.merchant;

import java.lang.reflect.Proxy;
import java.util.ArrayList;
import java.util.List;

import me.micrjonas.grandtheftdiamond.inventory.merchant.ReflectionUtils.NMSMerchantRecipe;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

public class Merchant {
	
	private NMSMerchant h;
	private String title;
	
	public Merchant(String title) {
		
		this.title = title;
		this.h = new NMSMerchant();
		this.h.proxy = Proxy.newProxyInstance(Bukkit.class.getClassLoader(), new Class[] { ReflectionUtils.getClassByName(ReflectionUtils.getNMSPackageName() + ".IMerchant") }, this.h);


	}
	
	
	public Merchant() {
		
		this(null);
		
	}
	
	
	public String getTitle() {
		
		return this.title;
		
	}
	
	
	public void setTitle(String title) {
		
		this.title = title;
		
	}
	
	
	public List<Offer> getOffers() {
		
		List<Offer> offerList = new ArrayList<Offer>();
		
		for (Object recipe : (List<?>) this.h.getOffers(null)) {
			
			if (recipe.getClass().isInstance(NMSMerchantRecipe.getNMSClass()))
				offerList.add(new Offer(new NMSMerchantRecipe(recipe)));
	
		}
		
		return offerList;
		
	}
	
	
	public Merchant addOffer(Offer offer) {
		
		this.h.a(offer.getHandle().getMerchantRecipe());
		
		return this;
		
	}
	
	
	public Merchant addOffers(Offer[] offers) {
		
		for (Offer offer : offers)
			addOffer(offer);
		
		return this;
	}
	
	
	public Merchant setOffers(List<Offer> offers) {
		
		this.h.clearRecipes();
		
		for (Offer offer : offers)
			this.addOffer(offer);
		
		return this;
	}
	
	
	public boolean hasCustomer() {
		
		return this.h.b() != null;
		
	}
	
	
	public Player getCustomer() {
		
		return this.h.b() == null ? null : this.h.getBukkitEntity();
		
	}
	
	
	public Merchant setCustomer(Player p) {
		
		this.h.a_(p == null ? null : ReflectionUtils.toEntityHuman(p));
		
		return this;
		
	}
	
	
	public void openOfferInventory(Player p) {
		
		this.h.openTrading(ReflectionUtils.toEntityHuman(p), this.title);
		
	}
	
	
	protected NMSMerchant getHandle() {
		
		return this.h;
		
	}
	
	
	@Override
	public Merchant clone() {
		
		return new Merchant(title).setOffers(getOffers()).setCustomer(getCustomer());
		
	}
	
}

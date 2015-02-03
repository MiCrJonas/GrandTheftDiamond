package me.micrjonas.grandtheftdiamond.dealer;

import java.util.Collection;
import java.util.UUID;

import me.micrjonas.grandtheftdiamond.inventory.merchant.Offer;
import net.citizensnpcs.api.CitizensAPI;
import net.citizensnpcs.api.npc.NPC;
import net.citizensnpcs.trait.LookClose;

import org.bukkit.Location;
import org.bukkit.entity.EntityType;
import org.bukkit.event.player.PlayerTeleportEvent.TeleportCause;

public class CitizensDealer extends Dealer {

	static {
		
		//new CitizensDealerListener();
		
	}
	
	
	private NPC npc;
	
	CitizensDealer(UUID id, Location loc, String name, Collection<Offer> offers) {
		
		super(id, loc, name, offers);
		
	}
	
	
	@Override
	protected void spawn(Location loc) {
		
		npc = CitizensAPI.getNPCRegistry().createNPC(EntityType.VILLAGER, getUniqueId(), -1, getName());
		npc.getTrait(LookClose.class).lookClose(true);
		npc.spawn(getLocation());
		
	}

	
	@Override
	protected void destroy() {
		
		npc.destroy();
		
	}
	
	
	@Override
	protected void updateLocation(Location loc) {
		
		npc.teleport(loc, TeleportCause.PLUGIN);
		
	}
	
	
	public NPC getNPC() {
		
		return npc;
		
	}

}

package me.micrjonas.grandtheftdiamond.listener;

import me.micrjonas.grandtheftdiamond.data.PluginData;

import org.bukkit.Material;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.inventory.EntityEquipment;
import org.bukkit.inventory.ItemStack;

public class MobDeathListener implements Listener{

	@EventHandler
	public void onEntityDeath(EntityDeathEvent e) {
		
		Entity ent = e.getEntity();
		EntityEquipment entEquip = ((LivingEntity) ent).getEquipment();
		
		if (PluginData.getInstance().inArena(ent.getLocation())) {

			e.setDroppedExp(0);
			e.getDrops().clear();
			
		}
		
		
		if (entEquip.getItemInHand().getType() == Material.DIAMOND) {
			
			e.getDrops().add(new ItemStack(Material.DIAMOND));
			
		}
		
	}
	
}

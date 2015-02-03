package me.micrjonas.grandtheftdiamond.item;

import me.micrjonas.grandtheftdiamond.GrandTheftDiamond;

import org.bukkit.entity.Player;


public class StartKitData {
	
	private Kit kit;
	private String permission;

	public StartKitData(Kit kit, String permission) {
		
		this.kit = kit;
		this.permission = permission;
		
	}
	
	
	public Kit getKit() {
		
		return kit;
		
	}
	
	
	public String getPermission() {
		
		return permission;
		
	}
	
	
	public boolean addToPlayer(Player p) {
		
		if (GrandTheftDiamond.checkPermission(p, "startkit." + kit.getName())) {
			
			kit.giveToPlayer(p);
			
			return true;
			
		}
		
		return false;
		
	}
	
}

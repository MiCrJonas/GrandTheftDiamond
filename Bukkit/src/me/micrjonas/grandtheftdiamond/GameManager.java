package me.micrjonas.grandtheftdiamond;

import me.micrjonas.grandtheftdiamond.api.event.cause.JoinReason;
import me.micrjonas.grandtheftdiamond.api.event.cause.LeaveReason;
import me.micrjonas.grandtheftdiamond.api.event.player.PlayerJoinGameEvent;
import me.micrjonas.grandtheftdiamond.api.event.player.PlayerLeaveGameEvent;
import me.micrjonas.grandtheftdiamond.data.FileManager;
import me.micrjonas.grandtheftdiamond.data.FileReloadListener;
import me.micrjonas.grandtheftdiamond.data.PluginData;
import me.micrjonas.grandtheftdiamond.data.PluginFile;
import me.micrjonas.grandtheftdiamond.data.TemporaryPluginData;
import me.micrjonas.grandtheftdiamond.item.ItemManager;
import me.micrjonas.grandtheftdiamond.item.Kit;
import me.micrjonas.grandtheftdiamond.manager.ChatManager;
import me.micrjonas.grandtheftdiamond.manager.NametagManager;
import me.micrjonas.grandtheftdiamond.messenger.Messenger;
import me.micrjonas.grandtheftdiamond.sign.SignType;
import me.micrjonas.grandtheftdiamond.sign.SignUpdater;
import me.micrjonas.grandtheftdiamond.util.bukkit.Locations;
import me.micrjonas.grandtheftdiamond.util.bukkit.PotionEffects;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Damageable;
import org.bukkit.entity.Horse;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;

/**
 * Methods to join or leave the game
 */
public class GameManager implements FileReloadListener {
	
	private static GameManager instance = new GameManager();
	
	/**
	 * Returns the loaded instance of the plugin's {@link GameManager}
	 * @return The loaded instance of the plugin's {@link GameManager}
	 */
	public static GameManager getInstance() {
		return instance;
	}
	
	@SuppressWarnings("unused")
	private boolean changeResourcepack;
	@SuppressWarnings("unused")
	private String resourcepackURL;
	
	private boolean teleportToLeaveLocation;
	
	private GameManager() { }
	
	@Override
	public void configurationReloaded(PluginFile file, FileConfiguration fileConfiguration) {
		if (file == PluginFile.CONFIG) {
			changeResourcepack = fileConfiguration.getBoolean("resourcepack.changeResourcepackOnJoin");
			resourcepackURL = fileConfiguration.getString("resourcepack.inGame");
			teleportToLeaveLocation = fileConfiguration.getBoolean("teleportToLeavePositionOnJoin");
		}
	}
	
	/**
	 * Lets a player join the game
	 * @param p The player who should join the game
	 * @param team The new team for the player
	 * @param reason The reason why the player should join the game. If null, it will be set to {@code JoinReason.CUSTOM}
	 * @throws IllegalArgumentException Thrown if {@code p} or {@code team} are null
	 */
	@SuppressWarnings("deprecation")
	public void joinGame(Player p, Team team, JoinReason reason) throws IllegalArgumentException {
		if (p == null) {
			throw new IllegalArgumentException("Player to join is not allowed to be null");
		}
		if (team == null) {
			throw new IllegalArgumentException("Player team is not allowed to be null");
		}
		if (reason == null) {
			reason = JoinReason.CUSTOM;
		}
		if(PluginData.getInstance().arenaSet(true)) {
			if (!TemporaryPluginData.getInstance().isIngame(p)) {
				Location joinLocation = PluginData.getInstance().getTeamSpawn(team);
				if (teleportToLeaveLocation) {
					Location lastLocation = Locations.getLocationFromFile(FileManager.getInstance().getPlayerData(p), "lastLocation", false);
					if (lastLocation != null) {
						if (PluginData.getInstance().inArena(lastLocation)) {
							joinLocation = lastLocation;	
						}
						else {
							FileManager.getInstance().getPlayerData(p).set("lastLocation", null);
						}
					}
				}
				
				PlayerJoinGameEvent e = new PlayerJoinGameEvent(p,
							PluginData.getInstance().isBanned(p.getName()) || PluginData.getInstance().isBanned(p.getName(), team, true), 
							PluginData.getInstance().isBanned(p.getName()) || PluginData.getInstance().isBanned(p.getName(), team, true), 
							joinLocation,
							Messenger.getInstance().getPluginMessage("otherJoinedGame"),
							Messenger.getInstance().getPluginMessage("joinedGame"), 
							team, 
							reason,
							null);
				
				Bukkit.getPluginManager().callEvent(e);
				if (!e.isCancelledBecauseBanned() || !PluginData.getInstance().isBanned(p.getName())) {
					if (!e.isCancelledBecauseBanned() || !PluginData.getInstance().isBanned(p.getName(), team, true)) {	
						if (!e.isCancelled()) {	
							TemporaryPluginData.getInstance().setOldInventory(p, p.getInventory().getContents());
							TemporaryPluginData.getInstance().setOldArmor(p, p.getInventory().getArmorContents());
							TemporaryPluginData.getInstance().setOldLocation(p, p.getLocation());
							TemporaryPluginData.getInstance().setOldLevel(p, p.getLevel());
							TemporaryPluginData.getInstance().setOldExp(p, p.getExp());
							TemporaryPluginData.getInstance().setOldHealth(p, ((Damageable) p).getHealth());
							TemporaryPluginData.getInstance().setOldFoodLevel(p, p.getFoodLevel());
							TemporaryPluginData.getInstance().setOldPotionEffects(p, p.getActivePotionEffects());
									
							p.getInventory().clear();
							p.getInventory().setHelmet(new ItemStack(Material.AIR));
							p.getInventory().setChestplate(new ItemStack(Material.AIR));
							p.getInventory().setLeggings(new ItemStack(Material.AIR));
							p.getInventory().setBoots(new ItemStack(Material.AIR));
							
						//New data
							
							double health = FileManager.getInstance().getPlayerData(p).getDouble("health");
							Damageable dmg = p;
									
							dmg.setHealth(health > 0 ? health : dmg.getMaxHealth());
							
							if (FileManager.getInstance().getPlayerData(p).isInt("food"))
								p.setFoodLevel(FileManager.getInstance().getPlayerData(p).getInt("food"));
							
							else
								p.setFoodLevel(20);
							
						    p.setLevel(PluginData.getInstance().getWantedLevel(p));
							p.setExp(0);
							
							PotionEffects.removeFromPlayer(p);
							
							if (FileManager.getInstance().getPlayerData(p).isList("potionEffects")) 
								PotionEffects.addToPlayer(p, PotionEffects.getEffectsFromConfig(FileManager.getInstance().getPlayerData(p), "potionEffects"));

						
							PluginData.getInstance().setTeam(p, e.getNewTeam());
							
							if (PluginData.getInstance().inArena(e.getJoinLocation())) {
								p.teleport(e.getJoinLocation());
							}
							else {
								p.teleport(PluginData.getInstance().getDefaultSpawn());
							}
							FileConfiguration playerData = FileManager.getInstance().getPlayerData(p);
							
							if (playerData.get("inventory") == null) {
								
								for (Kit kit : ItemManager.getInstance().getStartKits(team, p))
									kit.giveToPlayer(p);
								
							}
							
							else {
								
								PlayerInventory inv = p.getInventory();
								ItemStack[] armor = new ItemStack[4];
								
								for (int i = 0; i < inv.getSize(); i++) {
									
									if (playerData.get("inventory.contents." + i) != null)
										inv.setItem(i, playerData.getItemStack("inventory.contents." + i));
									
								}
								
								for (int i = 0; i < 4; i++)
									armor[i] = playerData.getItemStack("inventory.armorContents." + i);
								
								p.getInventory().setArmorContents(armor);
								
							}
							
							p.updateInventory();
							for (Player otherP : TemporaryPluginData.getInstance().getIngamePlayers())
								Messenger.getInstance().sendMessage(otherP, Messenger.getInstance().replacePlayers(e.getJoinMessageGlobal().replaceAll("%team%", team.name().toLowerCase()), new Player[]{p}));

							Messenger.getInstance().sendMessage(p, e.getJoinMessagePlayer().replaceAll("%team%", team.name().toLowerCase()));
							
							TemporaryPluginData.getInstance().setIngame(p, true);
					
							SignUpdater.getInstance().updateSigns(SignType.JOIN);
							NametagManager.getInstance().updateNametags();
							ChatManager.getInstance().updateChat(p);
							
							/*if (changeResourcepack)
								p.setResourcePack(resourcepackURL);*/
							
							//StatsBoardManager.getInstance().show(p);
						}		
					}
					else {
						int banTimeLeft = PluginData.getInstance().getBanTimeLeft(p.getName());	
						if (banTimeLeft > 0) {
							Messenger.getInstance().sendPluginMessage(p, "tempBannedTeam", new String[]{"%team%", "%time%"}, new String[]{Messenger.getInstance().getPluginWord(team.name().toLowerCase()), String.valueOf(banTimeLeft)});
						}
						else { 
							Messenger.getInstance().sendPluginMessage(p, "bannedTeam", new String[]{"%team%"}, new String[]{Messenger.getInstance().getPluginWord(team.name().toLowerCase())});
						}
					}
				}
				else {
					int banTimeLeft = PluginData.getInstance().getBanTimeLeft(p.getName());	
					if (banTimeLeft > 0) {
						Messenger.getInstance().sendPluginMessage(p, "tempBanned", new String[]{"%time%"}, new String[]{String.valueOf(banTimeLeft)});
					}
					else {
						Messenger.getInstance().sendPluginMessage(p, "banned");
					}	
				}
			}
			else {
				Messenger.getInstance().sendPluginMessage(p, "alreadyIngame");
			}
		}
		else {
			if (!(GrandTheftDiamond.checkPermission(p, "setup.create"))) {
				Messenger.getInstance().sendPluginMessage(p, "adminNeedSetupMap");
			}
			
			else {
				Messenger.getInstance().sendPluginMessage(p, "youNeedSetupMap");
			}
		}	
	}
	
	
	/**
	 * Lets a player leave the game
	 * @param p The player who should leave the game
	 * @param reason The reason why the player should leave the game. If null, it will be set to {@code LeaveReason.CUSTOM}
	 * @throws IllegalArgumentException Thrown if {@code p} is {@code null}
	 */
	@SuppressWarnings("deprecation")
	public void leaveGame(Player p, LeaveReason reason) throws IllegalArgumentException {
		if (p == null) {
			throw new IllegalArgumentException("Player to leave is not allowed to be null");
		}
		if (reason == null) {
			reason = LeaveReason.CUSTOM;
		}
		if (TemporaryPluginData.getInstance().isIngame(p)) {
			
			PlayerLeaveGameEvent e = new PlayerLeaveGameEvent(p, 
					FileManager.getInstance().getFileConfiguration(PluginFile.ONLY_GTD_MODE_CONFIG).getBoolean("use") && !FileManager.getInstance().getFileConfiguration(PluginFile.ONLY_GTD_MODE_CONFIG).getBoolean("playersCanLeaveGame"), 
					reason == LeaveReason.BAN || reason == LeaveReason.KICK,
					reason,
					Messenger.getInstance().getPluginMessage("otherLeftGame"),
					Messenger.getInstance().getPluginMessage("leftGame"));
		
			Bukkit.getPluginManager().callEvent(e);
			
			if (!e.isCancelledBecauseOnlyGTDMode()) {
			
				if (!e.isCancelled()) {
					
					FileConfiguration playerData = FileManager.getInstance().getPlayerData(p);
					
					if (FileManager.getInstance().getFileConfiguration(PluginFile.CONFIG).getBoolean("saveInventoryAfterLeavingGame")) {
						
						PlayerInventory inv = p.getInventory();
						
						playerData.set("inventory", null);
						
						for (int i = 0; i < p.getInventory().getSize(); i++) {
							
							if (inv.getItem(i) != null)
								playerData.set("inventory.contents." + i, inv.getItem(i));
							
						}
						
						for (int i = 0; i < 4; i++) {
							
							if (inv.getArmorContents()[i].getType() != Material.AIR)
								playerData.set("inventory.armorContents." + i, inv.getArmorContents()[i]);
							
						}
						
					}
					
					Locations.saveLocationToFile(p.getLocation(), playerData, "lastLocation", false);
					
					playerData.set("health", ((Damageable) p).getHealth());
					playerData.set("food", p.getFoodLevel());
					
					playerData.set("potionEffects", PotionEffects.toMapList(p.getActivePotionEffects()));
					
					p.getInventory().clear();
			        p.getInventory().setHelmet(new ItemStack(Material.AIR));
			        p.getInventory().setChestplate(new ItemStack(Material.AIR));
			        p.getInventory().setLeggings(new ItemStack(Material.AIR));
			        p.getInventory().setBoots(new ItemStack(Material.AIR));
					
					TemporaryPluginData.getInstance().setIngame(p, false);
					
					if (p.getVehicle() instanceof Horse)
						p.getVehicle().eject();
					
					p.teleport(TemporaryPluginData.getInstance().getOldLocation(p));
					
					ItemStack[] contents = TemporaryPluginData.getInstance().getOldInventory(p);
					
					for (int i = 0; i < contents.length; i++) {
						
						if (contents[i] != null)
						 p.getInventory().setItem(i, contents[i]);
						
					}
					
					p.getInventory().setArmorContents(TemporaryPluginData.getInstance().getOldArmor(p));
					
					p.updateInventory();
					
					p.setLevel(TemporaryPluginData.getInstance().getOldLevel(p));
					p.setExp(TemporaryPluginData.getInstance().getOldExp(p));
					p.setHealth(TemporaryPluginData.getInstance().getOldHealth(p));
					p.setFoodLevel(TemporaryPluginData.getInstance().getOldFoodLevel(p));
					
					PotionEffects.removeFromPlayer(p);
					PotionEffects.addToPlayer(p, TemporaryPluginData.getInstance().getOldPotionEffects(p));
					
					p.updateInventory();
					
					TemporaryPluginData.getInstance().clearOldPlayerData(p);
					
					Messenger.getInstance().sendMessage(p, e.getLeaveMessagePlayer());
					
					if (e.getLeaveMessageGlobal() != null) {
						
						String globalMessage = Messenger.getInstance().replacePlayers(e.getLeaveMessageGlobal(), new Player[]{p});
					
						for(Player otherP : TemporaryPluginData.getInstance().getIngamePlayers()) {
							
							if (TemporaryPluginData.getInstance().isIngame(otherP)) {
							
								Messenger.getInstance().sendMessage(otherP, globalMessage);
								
							}	
								
						}
					
					}
					
					ChatManager.getInstance().updateChat(p);
					NametagManager.getInstance().updateNametags();
					
					SignUpdater.getInstance().updateSigns(SignType.JOIN);
					
					/*if (changeResourcepack)
						p.setResourcePack(resourcepackURL);*/
					
					//StatsBoardManager.getInstance().unshow(p);
					
				}
				
			}
			
			else 
				Messenger.getInstance().sendPluginMessage(p, "cannotLeaveGame");
			
		}
			
		else 
			Messenger.getInstance().sendPluginMessage(p, "notIngame");
		
		NametagManager.getInstance().updateNametags();
	}

}

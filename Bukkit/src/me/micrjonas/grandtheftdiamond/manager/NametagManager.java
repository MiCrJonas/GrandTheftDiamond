package me.micrjonas.grandtheftdiamond.manager;

import me.micrjonas.grandtheftdiamond.GrandTheftDiamond;
import me.micrjonas.grandtheftdiamond.data.FileReloadListener;
import me.micrjonas.grandtheftdiamond.data.PluginData;
import me.micrjonas.grandtheftdiamond.data.PluginFile;
import me.micrjonas.grandtheftdiamond.data.TemporaryPluginData;
import me.micrjonas.grandtheftdiamond.util.StringUtils;

import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.Team;

import ca.wacos.nametagedit.NametagAPI;

public class NametagManager implements FileReloadListener {
	
	private static NametagManager instance = null;
	
	public static NametagManager getInstance() {
		if (instance == null) {
			instance = new NametagManager();
		}
		return instance;
	}
	
	private String prefixCivilian;
	private String prefixGangster;
	private String prefixCop;
	
	private String suffixCivilian;
	private String suffixGangster;
	private String suffixCop;
	
	private boolean useNametag;
	
	private boolean useNametagEdit;
	
	private boolean somethingWrongMsgSend;
	
	public NametagManager() {
		
		GrandTheftDiamond.registerFileReloadListener(this);

	}
	
	
	@Override
	public void configurationReloaded(PluginFile file, FileConfiguration fileConfiguration) {
		
		if (file == PluginFile.CONFIG) {
			
			useNametagEdit = Bukkit.getPluginManager().getPlugin("NametagEdit") != null && fileConfiguration.getBoolean("useNametagEdit");
			
			useNametag = fileConfiguration.getBoolean("scoreboard.nametag.use");
			prefixCivilian = StringUtils.translateColors(fileConfiguration.getString("scoreboard.nametag.prefix.civilian"));
			prefixGangster = StringUtils.translateColors(fileConfiguration.getString("scoreboard.nametag.prefix.gangster"));
			prefixCop = StringUtils.translateColors(fileConfiguration.getString("scoreboard.nametag.prefix.cop"));
			
			suffixCivilian = StringUtils.translateColors(fileConfiguration.getString("scoreboard.nametag.suffix.civilian"));
			suffixGangster = StringUtils.translateColors(fileConfiguration.getString("scoreboard.nametag.suffix.gangster"));
			suffixCop = StringUtils.translateColors(fileConfiguration.getString("scoreboard.nametag.suffix.cop"));
			
		}
		
	}
	
	
	public void removeGtdTeams() {
		
		for (Team team : Bukkit.getServer().getScoreboardManager().getMainScoreboard().getTeams()) {
			
			if (team.getName().contains("gtd")) {
				
				team.unregister();
				
			}
			
		}
		
		for (Player p : GrandTheftDiamond.getOnlinePlayers()) {
			
			for (Team team : p.getScoreboard().getTeams()) {
				
				if (team.getName().contains("gtd")) {
					
					team.unregister();
					
				}
				
			}
			
		}
		
	}
	
	
	public void updateNametags() {
		
		if (useNametag) {
			
			if (prefixCivilian.replaceAll("%wantedLevel%", "  ").length() <= 16 &&
					prefixGangster.replaceAll("%wantedLevel%", "  ").length() <= 16 &&
					prefixCop.replaceAll("%wantedLevel%", "  ").length() <= 16 &&
					suffixCivilian.replaceAll("%wantedLevel%", "  ").length() <= 16 &&
					suffixGangster.replaceAll("%wantedLevel%", "  ").length() <= 16 &&
					suffixCop.replaceAll("%wantedLevel%", "  ").length() <= 16) {
			
				for (Player p : GrandTheftDiamond.getOnlinePlayers()) {
					
					if (useNametagEdit) {
							
						if (TemporaryPluginData.getInstance().isIngame(p)) {
							
							int wantedLevel = PluginData.getInstance().getWantedLevel(p);
							String wantedLevelString = String.valueOf(wantedLevel);
							
							String prefix = "";
							String suffix = "";
							
							if (PluginData.getInstance().getTeam(p) == me.micrjonas.grandtheftdiamond.Team.COP) {
								
								prefix = prefixCop;
								suffix = suffixCop;
								
							}
							
							else if (PluginData.getInstance().getWantedLevel(p) > 0) {
								
								prefix = prefixGangster.replaceAll("%wantedLevel%", wantedLevelString);
								suffix = suffixGangster.replaceAll("%wantedLevel%", wantedLevelString);
								
							}
							
							else { 
							
								prefix = prefixCivilian.replaceAll("%wantedLevel%", wantedLevelString);
								suffix = suffixCivilian.replaceAll("%wantedLevel%", wantedLevelString);
								
							}
							
							NametagAPI.setNametagSoft(p.getName(), prefix, suffix);
							
						}
						
						else
							NametagAPI.resetNametag(p.getName());
						
					}
					
					else {
						
						Scoreboard sb = p.getScoreboard();
						
						for (Player otherP : GrandTheftDiamond.getOnlinePlayers()) {
							
							if (TemporaryPluginData.getInstance().isIngame(otherP)) {
								
								if (PluginData.getInstance().getTeam(p) == me.micrjonas.grandtheftdiamond.Team.COP) {
									
									Team copLocal = sb.getTeam("gtdCop");
									
									if (sb.getTeam("gtdCop") == null) {
										
										copLocal = sb.registerNewTeam("gtdCop");
										
									}
									
									copLocal.setPrefix(prefixCop);
									copLocal.setSuffix(suffixCop);
									copLocal.addPlayer(otherP);
									
								}
								
								else {
								
									int wantedLevel = PluginData.getInstance().getWantedLevel(otherP);
									String wantedLevelString = String.valueOf(wantedLevel);
									
									if (wantedLevel < 1) {

										if (sb.getTeam("gtdCivilian") == null) {
											
											Team civilianLocal = sb.registerNewTeam("gtdCivilian");
											civilianLocal.setPrefix(prefixCivilian.replaceAll("%wantedLevel%", "0"));
											civilianLocal.setSuffix(suffixCivilian.replaceAll("%wantedLevel%", "0"));
											civilianLocal.addPlayer(otherP);
											
										}
										
										else {
												
											Team civilianLocal = sb.getTeam("gtdCivilian");
											civilianLocal.addPlayer(otherP);
											
										}
										
									}
									
									else {
										
										if (sb.getTeam("gtdGangster" + wantedLevelString) == null) {
											
											Team gangsterLocal = sb.registerNewTeam("gtdGangster" + wantedLevelString);
											gangsterLocal.setPrefix(prefixGangster.replaceAll("%wantedLevel%", wantedLevelString));
											gangsterLocal.setSuffix(suffixGangster.replaceAll("%wantedLevel%", wantedLevelString));
										
										}
										
										else {
											
											Team gangsterLocal = sb.getTeam("gtdGangster" + wantedLevelString);
											gangsterLocal.addPlayer(otherP);
											
										}
										
									}
									
								}
								
							}
							
							else {
								
								if (sb.getPlayerTeam(otherP) != null) {
									
									if (sb.getPlayerTeam(otherP).getName().contains("gtd"))
										sb.getPlayerTeam(otherP).removePlayer(otherP);
									
								}
								
							}
							
						}
						
						p.setScoreboard(sb);
						
					}
					
				}
				
			}
			
			else {
				
				String prefixOrSuffix = "prefix";
				String team = "civilians";

				if (prefixCivilian.length() > 16) { }
				
				else if (prefixGangster.length() > 16)
					team = "gangsters";
					
				else if (prefixCop.length() > 16)
					team = "cops";
				
				else if (suffixCivilian.length() > 16)
					prefixOrSuffix = "suffix";
				
				else if (suffixGangster.length() > 16) {
					
					team = "gangsters";
					prefixOrSuffix = "suffix";
					
				}
				
				else if (suffixCop.length() > 16) {
					
					team = "cops";
					prefixOrSuffix = "suffix";
					
				}
					
				if (!somethingWrongMsgSend) {
				
					GrandTheftDiamond.getLogger().info("The scoreboard-" + prefixOrSuffix + " of the " + team + " is too long. Max is 16!");
					somethingWrongMsgSend = true;
					
				}
					
			}
			
		}
		
	}
	
}

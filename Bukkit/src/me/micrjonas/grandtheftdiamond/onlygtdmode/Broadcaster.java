package me.micrjonas.grandtheftdiamond.onlygtdmode;

import java.util.Arrays;
import java.util.List;
import java.util.concurrent.TimeUnit;

import me.micrjonas.grandtheftdiamond.GrandTheftDiamond;
import me.micrjonas.grandtheftdiamond.data.PluginData;
import me.micrjonas.grandtheftdiamond.data.TemporaryPluginData;

import org.bukkit.entity.Player;

public class Broadcaster {
	
	private OnlyGTDModeManager manager;
	
	public Broadcaster(OnlyGTDModeManager manager) {
		
		this.manager = manager;
		
		startBroadcast();
		
	}
	
	
	void startBroadcast() {
		
		if (manager.getConfig().getBoolean("broadcaster.use")) {
			
			final String broadcastPrefix = manager.getConfig().getString("broadcaster.prefix");

			if (manager.getConfig().getBoolean("broadcaster.useTeamBroadcast")) {
			
				for (final String team : manager.getConfig().getConfigurationSection("broadcaster.teams").getKeys(false)) {
					
					int delay = manager.getConfig().getInt("broadcaster.teams." + team + ".delayInSeconds");
					
					if (delay > 0) {
					
						manager.getSchedulers().add(GrandTheftDiamond.scheduleRepeatingTask(new Runnable() {
							
							@Override
							public void run() {
								
								List<String> messages = manager.getConfig().getStringList("broadcaster.teams." + team + ".messages");
								
								int currentMessage = 0;
								
								if (!manager.getConfig().getBoolean("broadcaster.teams." + team + ".random")) {
								
									if (messages.size() > 1) {
										
										currentMessage = manager.getConfig().getInt("broadcaster.currentMessages.team." + team);
										
										if (currentMessage >= messages.size() - 1)
											manager.getConfig().set("broadcaster.currentMessages.team." + team, 0);
										
										else
											manager.getConfig().set("broadcaster.currentMessages.team." + team, currentMessage + 1);
										
									}
									
								}
								
								else
									currentMessage = (int) (Math.random() * messages.size());
								
								String messageToSend = messages.get(currentMessage);
								messageToSend = broadcastPrefix + messageToSend;
								messageToSend = manager.replaceMessage(messageToSend);
								
								for (Player p : TemporaryPluginData.getInstance().getIngamePlayers())  {
									
									if (PluginData.getInstance().getTeam(p).name().toLowerCase().equals(team))
										p.sendMessage(manager.replacePlayerData(messageToSend, p));
									
								}
								
							}
							
						}, manager.getConfig().getInt("broadcaster.teams." + team + ".startAfterSeconds"), delay, TimeUnit.SECONDS));
						
					}
							
				}
				
			}
			
			
			if (manager.getConfig().getBoolean("broadcaster.useGroupBroadcast")) {
				
				for (final String groups : manager.getConfig().getConfigurationSection("broadcaster.groups").getKeys(false)) {
					
					final String group = groups.toLowerCase();
					
					int delay = manager.getConfig().getInt("broadcaster.groups." + group + ".delayInSeconds");
					
					if (delay > 0) {
					
						manager.getSchedulers().add(GrandTheftDiamond.scheduleRepeatingTask(new Runnable() {
							
							@Override
							public void run() {
								
								List<String> messages = manager.getConfig().getStringList("broadcaster.groups." + group + ".messages");
								
								int currentMessage = 0;
								
								if (!manager.getConfig().getBoolean("broadcaster.groups." + group + ".random")) {
								
									if (messages.size() > 1) {
										
										currentMessage = manager.getConfig().getInt("broadcaster.currentMessages.group." + group);
										
										if (currentMessage >= messages.size() - 1)
											manager.getConfig().set("broadcaster.currentMessages.group." + group, 0);
										
										else
											manager.getConfig().set("broadcaster.currentMessages.group." + group, currentMessage + 1);
										
									}
									
								}
								
								else
									currentMessage = (int) (Math.random() * messages.size());
								
								String messageToSend = messages.get(currentMessage);
								messageToSend = broadcastPrefix + messageToSend;
								messageToSend = manager.replaceMessage(messageToSend);
								
								for (Player p : TemporaryPluginData.getInstance().getIngamePlayers())  {
									
									if (group.equals("default") || GrandTheftDiamond.checkPermission(p, "group." + group)) {
										
										messageToSend = manager.replacePlayerData(messageToSend, p);
										
										for (String finalMessage : manager.getMessages(Arrays.asList(messageToSend)))
											p.sendMessage(finalMessage);
										
									}
									
								}
								
							}
							
						}, manager.getConfig().getInt("broadcaster.groups." + group + ".startAfterSeconds"), delay, TimeUnit.SECONDS));
						
					}
					
				}
				
			}
			
		}
		
	}

}

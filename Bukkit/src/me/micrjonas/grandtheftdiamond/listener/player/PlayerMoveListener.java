package me.micrjonas.grandtheftdiamond.listener.player;

import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.TimeUnit;

import me.micrjonas.grandtheftdiamond.GrandTheftDiamond;
import me.micrjonas.grandtheftdiamond.data.PluginData;
import me.micrjonas.grandtheftdiamond.data.TemporaryPluginData;
import me.micrjonas.grandtheftdiamond.messenger.Messenger;
import me.micrjonas.grandtheftdiamond.rob.RobManager;

import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;

public class PlayerMoveListener implements Listener{

	private final Set<Player> messageCooldownPlayers = new HashSet<>();
	private final TemporaryPluginData tmpData = TemporaryPluginData.getInstance();
	
	@EventHandler
	public void onPlayerMove(PlayerMoveEvent e) {
		final Player p = e.getPlayer();
		if (tmpData.isIngame(p)) {
			if (!PluginData.getInstance().inArena(e.getTo())) {
				if (p.getVehicle() != null) {
					Entity vehicle = p.getVehicle();
					vehicle.eject();
					p.teleport(e.getFrom());
					vehicle.teleport(e.getFrom());
					vehicle.setPassenger(p);
				}
				else {
					p.teleport(e.getFrom());
				}
				if (messageCooldownPlayers.add(p)) {
					Messenger.getInstance().sendPluginMessage(p, "arenaEnd");
					GrandTheftDiamond.scheduleSyncDelayedTask(new Runnable() {
						@Override
						public void run() {
							messageCooldownPlayers.remove(p);
						}
					}, 1, TimeUnit.SECONDS);
				}
			}
			else if (RobManager.getInstance().isRobbing(p) && importantMove(e.getFrom(), e.getTo())) {
					RobManager.getInstance().cancelRobbing(p);
			}
		}
		
		else if (tmpData.isJoining(p)) {
			if (importantMove(e.getFrom(), e.getTo())) {
				tmpData.setIsJoining(p, false);
				tmpData.setCanJoin(p, false);
				Messenger.getInstance().sendPluginMessage(p, "movedWhileJoin");
			}
		}
	}
	
	private boolean importantMove(Location from, Location to) {
		if (from.getBlockX() != to.getBlockX()) {
			return true;
		}
		if (to.getBlockZ() != to.getBlockZ()) {
			return true;
		}
		return false;
	}

}

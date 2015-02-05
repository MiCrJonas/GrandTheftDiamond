package me.micrjonas.grandtheftdiamond.sign.handler;

import me.micrjonas.grandtheftdiamond.GameManager;
import me.micrjonas.grandtheftdiamond.GrandTheftDiamond;
import me.micrjonas.grandtheftdiamond.Team;
import me.micrjonas.grandtheftdiamond.api.event.cause.JoinReason;
import me.micrjonas.grandtheftdiamond.data.TemporaryPluginData;
import me.micrjonas.grandtheftdiamond.messenger.Messenger;
import me.micrjonas.grandtheftdiamond.messenger.NoPermissionType;

import org.bukkit.entity.Player;

/**
 * Listens to the interaction of a {@link Player} with a join sign
 */
public class JoinSignHandler implements SignHandler {

	@Override
	public void signClicked(Player clicker, String[] lines, String[] parsedLines) {
		if (TemporaryPluginData.getInstance().isIngame(clicker)) {
			Messenger.getInstance().sendPluginMessage(clicker, "alreadyIngame");
			return;
		}
		if (lines[1].contains("cop")) {
			if (GrandTheftDiamond.checkPermission(clicker, "use.sign.join.cop", true, NoPermissionType.USE)) {
				GameManager.getInstance().joinGame(clicker, Team.COP, JoinReason.SIGN);
			}
		}
			
		else if (lines[1].contains("civilian")) {
			if (GrandTheftDiamond.checkPermission(clicker, "use.sign.join.civilian", true, NoPermissionType.USE)) {
				GameManager.getInstance().joinGame(clicker, Team.CIVILIAN, JoinReason.SIGN);
			}
		}
	}

	@Override
	public boolean isValid(String[] lines, String[] parsedLines) {
		// TODO Auto-generated method stub
		return false;
	}
	
}

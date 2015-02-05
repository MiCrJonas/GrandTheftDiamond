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
		if (!TemporaryPluginData.getInstance().isIngame(clicker)) {
			if (parsedLines[0].equals("cop")) {
				if (GrandTheftDiamond.checkPermission(clicker, "use.sign.join.cop", true, NoPermissionType.USE)) {
					GameManager.getInstance().joinGame(clicker, Team.COP, JoinReason.SIGN);
				}
			}
			else { // Contains civilian, checked in isValid()
				if (GrandTheftDiamond.checkPermission(clicker, "use.sign.join.civilian", true, NoPermissionType.USE)) {
					GameManager.getInstance().joinGame(clicker, Team.CIVILIAN, JoinReason.SIGN);
				}
			}
		}
		else {
			Messenger.getInstance().sendPluginMessage(clicker, "alreadyIngame");
		}
	}

	@Override
	public boolean isValid(String[] lines, String[] parsedLines) {
		return parsedLines[1].equals("cop") || parsedLines[0].equals("civilian");
	}
	
}

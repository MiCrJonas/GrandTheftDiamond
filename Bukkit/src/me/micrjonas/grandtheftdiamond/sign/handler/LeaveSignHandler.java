package me.micrjonas.grandtheftdiamond.sign.handler;

import me.micrjonas.grandtheftdiamond.GameManager;
import me.micrjonas.grandtheftdiamond.GrandTheftDiamond;
import me.micrjonas.grandtheftdiamond.api.event.cause.LeaveReason;
import me.micrjonas.grandtheftdiamond.data.TemporaryPluginData;
import me.micrjonas.grandtheftdiamond.messenger.Messenger;
import me.micrjonas.grandtheftdiamond.messenger.NoPermissionType;

import org.bukkit.entity.Player;

/**
 * Listens to the interaction of a {@link Player} with a leave sign
 */
public class LeaveSignHandler implements SignHandler {

	@Override
	public void signClicked(Player clicker, String[] lines, String[] parsedLines) {
		if (GrandTheftDiamond.checkPermission(clicker, "use.sign.leave", true, NoPermissionType.USE)) {
			if (TemporaryPluginData.getInstance().isIngame(clicker)) {
				GameManager.getInstance().leaveGame(clicker, LeaveReason.SIGN);
			}
			else {
				Messenger.getInstance().sendPluginMessage(clicker, "notIngame");
			}
		}
	}

	@Override
	public boolean isValid(String[] lines, String[] parsedLines) {
		return true;
	}
}

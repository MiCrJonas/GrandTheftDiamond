package me.micrjonas.grandtheftdiamond.data.player;

import me.micrjonas.grandtheftdiamond.GameManager;
import me.micrjonas.grandtheftdiamond.Team;
import me.micrjonas.grandtheftdiamond.api.event.cause.LeaveReason;

import org.bukkit.entity.Player;

public class BanData {
	
	private final Player p;
	private Team bannedAs = Team.NONE;
	private long bannedUntilCivilian = -1;
	private long bannedUntilCop = -1;
	
	BanData(Player p) {
		this.p = p;
	}
	
	/**
	 * Returns the related Player
	 * @return The related Player
	 */
	public Player getPlayer() {
		return p;
	}
	
	/**
	 * Checks whether the Player is banned as the passed {@link Team}
	 * @param team The {@link Team}
	 * @return True if the Player is banned from the specific {@link Team}
	 */
	public boolean bannedAs(Team team) {
		if (team == null) {
			throw new IllegalArgumentException("team is not allowed to be null");
		}
		return Team.isCompatible(bannedAs, team);
	}
	
	/**
	 * Bans the {@code Player} for a specific {@link Team}
	 * @param team The team to ban
	 * @param kick Whether the method should kick the {@code Player} after banning him
	 * @see BanData#banAs(Team, int, boolean)
	 * @throws IllegalArgumentException Thrown if {@code team} is {@code null} or Team.NONE
	 */
	public void banAs(Team team, boolean kick) throws IllegalArgumentException {
		banAs(team, -1, kick);
	}
	
	/**
	 * Bans the {@code Player} for a specific time from a {@link Team}
	 * @param team The team to ban
	 * @param timeSeconds The time in seconds, -1 to ban for ever
	 * @param kick Whether the method should kick the {@code Player} after banning him
	 * @throws IllegalArgumentException Thrown if {@code team} is {@code null} or Team.NONE or if {@code timeSeconds} is 0
	 */
	public void banAs(Team team, int timeSeconds, boolean kick) throws IllegalArgumentException {
		if (team == null) {
			throw new IllegalArgumentException("team is not allowed to be null");
		}
		if (timeSeconds == 0) {
			throw new IllegalArgumentException("Time must be != 0");
		}
		if (timeSeconds < 0) {
			timeSeconds = -1;
		}
		else {
			timeSeconds += System.currentTimeMillis() + timeSeconds * 1000;
		}
		switch (team) {
			case EACH_TEAM: {
				bannedAs = Team.EACH_TEAM;
				bannedUntilCivilian = timeSeconds;
				bannedUntilCop = timeSeconds;
			} break;
				
			case COP: {
				if (bannedAs == Team.CIVILIAN) {
					bannedAs = Team.EACH_TEAM;
				}
				else {
					bannedAs = Team.COP;
				}
				bannedUntilCop = timeSeconds;
			} break;
			
			case CIVILIAN: {
				if (bannedAs == Team.COP) {
					bannedAs = Team.EACH_TEAM;
				}
				else {
					bannedAs = Team.CIVILIAN;
				}
				bannedUntilCivilian = timeSeconds;
			} break;
			
			default:
				throw new IllegalArgumentException("Cannot ban player for Team.NONE");
		}
		if (kick) {
			GameManager.getInstance().leaveGame(getPlayer(), LeaveReason.BAN);
		}
	}
	
	/**
	 * Unbans the {@code Player} from a specific {@link Team}
	 * @param team The team to unban
	 * @throws IllegalArgumentException Thrown if {@code team} is {@code null} or Team.NONE
	 */
	public void unbanAs(Team team) throws IllegalArgumentException {
		if (team == null) {
			throw new IllegalArgumentException("Team is not allowed to be null");
		}
		if (bannedAs == Team.NONE) {
			return;
		}
		switch (team) {
			case EACH_TEAM: {
				bannedAs = Team.NONE;
			} break;
		
			case COP: {
				if (bannedAs == Team.EACH_TEAM || bannedAs == Team.CIVILIAN) {
					bannedAs = Team.CIVILIAN;
				}
				else if (bannedAs == Team.COP) {
					bannedAs = Team.NONE;
				}
				else {
					bannedAs = Team.CIVILIAN;
				}
			} break;
			
			case CIVILIAN: {
				if (bannedAs == Team.EACH_TEAM || bannedAs == Team.COP) {
					bannedAs = Team.COP;
				}
				else {
					bannedAs = Team.NONE;
				}
			} break;
			
			case NONE: {
				throw new IllegalArgumentException("Cannot unban as Team.NONE");
			}
		}
	}
	
	/**
	 * Checks when the {@code Player} is no longer banned. The time is the time in mills since January 1st 1970
	 * @param team The team to check
	 * @return The time when the player is no longer banned, -1 if the {@code Player} is banned for ever or if the {@code Player} is not banned
	 */
	public long bannedUntil(Team team) {
		long bannedUntil = 0;
		switch (team) {
			case EACH_TEAM: case CIVILIAN: {
				bannedUntil = bannedUntilCivilian;
			} break;
				
			case COP: {
				bannedUntil = bannedUntilCop;
			} break;
			
			default:
				throw new IllegalArgumentException("A player cannot be banned as Team.NONE");
		}
		if (bannedUntil < System.currentTimeMillis()) {
			return -1;
		}
		return bannedUntil;
	}
	
	/**
	 * Returns the time, the {@code Player} is banned for
	 * @param team The team to check
	 * @return The left ban time in seconds, -1 if the {@code Player} is not banned or if he is banned for eve
	 */
	public int banSecondsLeft(Team team) {
		long bannedUntil = bannedUntil(team);
		if (bannedUntil < System.currentTimeMillis()) {
			return -1;
		}
		return (int) ((bannedUntil - System.currentTimeMillis()) / 1000);
	}

}

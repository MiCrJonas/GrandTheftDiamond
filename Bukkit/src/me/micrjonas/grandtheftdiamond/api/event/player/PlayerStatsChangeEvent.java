package me.micrjonas.grandtheftdiamond.api.event.player;

import me.micrjonas.grandtheftdiamond.api.event.GrandTheftDiamondEvent;
import me.micrjonas.grandtheftdiamond.api.event.cause.CauseEvent;
import me.micrjonas.grandtheftdiamond.api.event.cause.StatsChangeCause;
import me.micrjonas.grandtheftdiamond.stats.StatsType;

import org.bukkit.entity.Player;
import org.bukkit.event.HandlerList;

public class PlayerStatsChangeEvent extends GrandTheftDiamondPlayerEvent implements CauseEvent<StatsChangeCause> {

	/**
	 * Returns the {@link Event}'s {@link HandlerList}
	 * @return The {@link Event}'s {@link HandlerList}
	 * @see GrandTheftDiamondEvent#getHandlers(Class)
	 * @see GrandTheftDiamondEvent#getHandlers()
	 */
	public static HandlerList getHandlerList() {
		return getHandlers(PlayerStatsChangeEvent.class);
	}
	
	private final StatsType type;
	private final StatsChangeCause cause;
	private final int oldValue;
	private int newValue;
	
	public PlayerStatsChangeEvent(Player who, StatsType type, StatsChangeCause cause, int oldValue, int newValue) {
		super(who);
		this.type = type;
		this.cause = cause == null ? StatsChangeCause.CUSTOM : cause;
		this.oldValue = oldValue;
		this.newValue = newValue;
	}

	@Override
	public StatsChangeCause getCause() {
		return cause;
	}
	
	public StatsType getChangedStats() {
		return type;
	}
	
	public int getOldValue() {
		return oldValue;
	}
	
	public int getNewValue() {
		return newValue;
	}
	
	public void setNewValue(int newVaule) throws IllegalArgumentException, UnsupportedOperationException {
		if (getChangedStats().validValue(newVaule)) {
			if (getChangedStats().manipulable()) {
				this.newValue = newVaule;
			}
			else {
				throw new UnsupportedOperationException("Cannot manipulate StatsType." + getChangedStats());
			}
		}
		else {
			throw new IllegalArgumentException(newValue + " is not valid for StatsType." + getChangedStats());
		}
	}

}

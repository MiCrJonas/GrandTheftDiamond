package me.micrjonas.grandtheftdiamond.api.event.player;

import java.util.ArrayList;
import java.util.List;

import me.micrjonas.grandtheftdiamond.api.event.AbstractEvent;
import me.micrjonas.grandtheftdiamond.api.event.RobEvent;
import me.micrjonas.grandtheftdiamond.data.FileManager;
import me.micrjonas.grandtheftdiamond.data.PluginFile;
import me.micrjonas.grandtheftdiamond.rob.Robable;

import org.bukkit.entity.Player;
import org.bukkit.event.HandlerList;
import org.bukkit.inventory.ItemStack;

/**
 * Gets fired after a player robbed a safe
 */
public class PlayerRobEvent extends AbstractPlayerEvent implements RobEvent {
	
	/**
	 * Returns the {@link Event}'s {@link HandlerList}
	 * @return The {@link Event}'s {@link HandlerList}
	 * @see AbstractEvent#getHandlers(Class)
	 * @see AbstractEvent#getHandlers()
	 */
	public static HandlerList getHandlerList() {
		return getHandlers(PlayerRobEvent.class);
	}
	
	private final Robable robbed;
	private int timeToNextRob;
	private int balance;
	private final List<ItemStack> receivedItems = new ArrayList<>();
	private int wantedLevel;
	
	/**
	 * Default constructor
	 * @param robber The {@code Player} who robbed the object
	 * @param robbed The robbed object
	 * @throws IllegalArgumentException Thrown if {@code robber} or {@code robbed} is {@code null}
	 */
	public PlayerRobEvent(Player robber, Robable robbed) throws IllegalArgumentException {
		super(robber);
		this.robbed = robbed;
		this.timeToNextRob = (int) (Math.random() * (FileManager.getInstance().getFileConfiguration(PluginFile.EVENT_CONFIG).getInt("robbing.safe.timeToNextRob.max") - FileManager.getInstance().getFileConfiguration(PluginFile.EVENT_CONFIG).getInt("robbing.safe.timeToNextRob.min"))) + FileManager.getInstance().getFileConfiguration(PluginFile.EVENT_CONFIG).getInt("robbing.safe.timeToNextRob.min");
		this.balance = (int) (Math.random()*(FileManager.getInstance().getFileConfiguration(PluginFile.EVENT_CONFIG).getInt("robbing.safe.money.max") - FileManager.getInstance().getFileConfiguration(PluginFile.EVENT_CONFIG).getInt("robbing.safe.money.min"))) + FileManager.getInstance().getFileConfiguration(PluginFile.EVENT_CONFIG).getInt("robbing.safe.money.min");
		this.wantedLevel = FileManager.getInstance().getFileConfiguration(PluginFile.EVENT_CONFIG).getInt("robbing.safe.wantedLevel");
	}
	
	
	@Override
	public Robable getRobbed() {
		return robbed;
	}
	
	
	/**
	 * Returns the time until the safe can be robbed again
	 * @return The time until the safe can be robbed again
	 */
	public int getTimeToNextRob() {
		return timeToNextRob;
	}
	
	
	/**
	 * Sets the time until the safe can be robbed again
	 * @param time The time until the safe can be robbed again
	 */
	public void setTimeToNextRob(int time) {
		timeToNextRob = time;
	}
	
	
	/**
	 * Returns the robbed money
	 * @return The robbed money
	 */
	public int getBalance() {
		return balance;
	}
	
	
	/**
	 * Sets the robbed money
	 * @param balance The new robbed money
	 */
	public void setBalance(int balance) {
		this.balance = balance;
	}
	
	
	/**
	 * Returns the items a player receives of the rob. List is editable to edit the received items
	 * @return The items a player receives
	 */
	public List<ItemStack> getReceivedItems() {
		return receivedItems;
	}
	
	
	/**
	 * Returns the wanted level a player gets for robbing the safe
	 * @return The wanted level a player gets for robbing the safe
	 */
	public int getWantedLevel() {
		return wantedLevel;
	}
	
	
	/**
	 * Sets the wanted level a player gets for robbing the safe
	 * @param wantedLevel The new wanted level for robbing the safe
	 */
	public void setWantedLevel(int wantedLevel) {
		this.wantedLevel = wantedLevel;
	}

}

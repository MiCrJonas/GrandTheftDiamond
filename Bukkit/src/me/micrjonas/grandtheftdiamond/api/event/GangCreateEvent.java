package me.micrjonas.grandtheftdiamond.api.event;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import me.micrjonas.grandtheftdiamond.gang.Gang;
import me.micrjonas.grandtheftdiamond.util.collection.Collections;

import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;
import org.bukkit.event.HandlerList;

/**
 * Fired when a {@link CommandSender} tries to create a new {@link Gang}
 */
public class GangCreateEvent extends AbstractCreateEvent {
	
	/**
	 * Returns the {@link Event}'s {@link HandlerList}
	 * @return The {@link Event}'s {@link HandlerList}
	 * @see AbstractEvent#getHandlers(Class)
	 * @see AbstractEvent#getHandlers()
	 */
	public static HandlerList getHandlerList() {
		return getHandlers(GangCreateEvent.class);
	}
	
	private String name;
	private OfflinePlayer leader;
	private final Set<OfflinePlayer> members = new HashSet<>();
	
	/**
	 * @param creator The creator of the {@link Gang} who used a command to create it
	 * @param name The new name of the {@link Gang}
	 * @param leader The leader of the {@link Gang}
	 * @param members The new members of the {@link Gang}. May be {@code null}
	 * @throws IllegalArgumentException Thrown if one or more of {@code creator}, {@code name} or {@code leader} are {@code null}
	 */
	public GangCreateEvent(CommandSender creator, String name, OfflinePlayer leader, Collection<? extends OfflinePlayer> members)
			throws IllegalArgumentException {
		super(creator);
		if (name == null) {
			throw new IllegalArgumentException("Name is not allowed to be null");
		}
		if (leader == null) {
			throw new IllegalArgumentException("Leader is not allowed to be null");
		}
		this.name = name;
		this.leader = leader;
		if (members != null) {
			setMembers(members);
		}
	}
	
	/**
	 * Returns the name of the gang
	 * @return The name of the gang
	 */
	public String getGangName() {
		return name;
	}
	
	/**
	 * Sets the gang name
	 * @param name New gang name
	 * @throws IllegalArgumentException Thrown if {@code name} is {@code null}
	 */
	public void setGangName(String name) throws IllegalArgumentException {
		if (name == null) {
			throw new IllegalArgumentException("New name is not allowed to be null");
		}
		this.name = name;
	}
	
	/**
	 * Returns the owner of the gang
	 * @return The owner of the gang. Never {@code null}
	 */
	public OfflinePlayer getOwner() {
		return leader;
	}
	
	/**
	 * Sets the leader of the gang
	 * @param leader The new leader of the gang
	 * @throws IllegalArgumentException Thrown if {@code leader} is {@code null}
	 */
	public void setLeader(OfflinePlayer leader) throws IllegalArgumentException {
		if (leader == null) {
			throw new IllegalArgumentException("New leader is not allowed to be null");
		}
		this.leader = leader;
	}
	
	/**
	 * Returns a collection of all members
	 * @return A collection of all members. The {@link Collection} is immutable and never {@code null}
	 */
	public Collection<? extends OfflinePlayer> getMembers() {
		return java.util.Collections.unmodifiableSet(members);
	}
	
	/**
	 * Sets the new members, clears the old member list
	 * @param members A Collection of all new members, {@code null} to remove all members
	 */
	public void setMembers(Collection<? extends OfflinePlayer> members) {
		this.members.clear();
		if (members != null) {
			Collections.addNonNulls(members, this.members);
		}
	}

}

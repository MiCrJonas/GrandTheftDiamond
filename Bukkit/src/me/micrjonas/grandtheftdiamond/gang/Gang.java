package me.micrjonas.grandtheftdiamond.gang;

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Map.Entry;

import me.micrjonas.grandtheftdiamond.GrandTheftDiamond;
import me.micrjonas.grandtheftdiamond.data.storage.Storable;
import me.micrjonas.util.Nameable;

import org.bukkit.OfflinePlayer;

@SuppressWarnings("deprecation")
public class Gang implements Nameable, Storable {
	
	private final String name;
	private OfflinePlayer leader;
	private boolean friendlyFire;
	private final  Map<String, OfflinePlayer> members = new HashMap<>();
	private final Map<GangOption<?>, Object> options = new HashMap<>();
	
	Gang(String name, OfflinePlayer leader, Collection<? extends OfflinePlayer> members, 
			Map<GangOption<?>, Object> options) {
		
		this.name = name;
		setLeader(leader);
		if (members != null) {
			for (OfflinePlayer member : members) {
				if (member != null) {
					this.members.put(member.getName(), member);	
				}
			}
		}
		if (options != null) {
			for (Entry<GangOption<?>, Object> optionValue : options.entrySet()) {
				if (optionValue.getValue() != null && optionValue.getKey().getValueClass() != optionValue.getValue().getClass()) {
					options.remove(optionValue.getKey());
				}
			}
			options.putAll(options);	
		}
	}
	
	@Override
	public String getName() {
		return name;
	}
	
	@Override
	public Map<String, Object> getStoreData() {
		Map<String, Object> data = new LinkedHashMap<>();
		Map<String, Object> options = new HashMap<>();
		if (leader != null) {
			data.put("leader", leader.getUniqueId().toString());
		}
		data.put("members", GrandTheftDiamond.getPlayerUniqueIds(members.values()));
		for (Entry<GangOption<?>, Object> optionValue : this.options.entrySet()) {
			options.put(optionValue.getKey().name().toLowerCase(), optionValue.getValue());
		}
		data.put("options", options);
		return data;
	}
	
	/**
	 * Returns the gang's leader
	 * @return The leader of the gang
	 */
	public OfflinePlayer getLeader() {
		return leader;
	}
	
	/**
	 * Sets the leader of the gang
	 * @param leader The new leader
	 * @throws IllegalArgumentException Thrown if {@code leader} is {@code null}
	 */
	public void setLeader(OfflinePlayer leader) throws IllegalArgumentException {
		if (leader == null) {
			throw new IllegalArgumentException("Leader cannot be null");
		}
		this.leader = leader;
		members.remove(leader.getName());
	}
	
	/**
	 * Returns a new {@code Collection} of all members
	 * @return A new {@code Collection} of all members which is immutable
	 */
	public Collection<OfflinePlayer> getMembers() {
		return Collections.unmodifiableCollection(members.values());
	}
	
	/**
	 * Checks whether an OfflinePlayer is a member of the gang
	 * @param player Involved player
	 * @return True, if the OfflinePlayer is a gang member, else false
	 */
	public boolean isMember(OfflinePlayer member) {
		return members.containsKey(member.getName());
	}
	
	/**
	 * Adds a member to the gang, returns whether the player is already in the gang
	 * @param player Member to add
	 * @return True, if the OfflinePlayer was not a member before, else false
	 * @throws IllegalArgumentException Thrown if {@code member} is {@code null}
	 * @throws IllegalStateException Thrown if the gang cannot have more members or {@code member} is already a member of an other gang
	 */
	public boolean addMember(OfflinePlayer member) throws IllegalArgumentException, IllegalStateException {
		if (member == null) {
			throw new IllegalArgumentException("Member is not allowed to be null");
		}
		if (!canHaveMoreMembers()) {
			throw new IllegalStateException("Gang cannot have more members. Maximum is " + getOptionValue(GangOption.MAX_MEMBERS));
		}
		Gang memberGang = GangManager.getInstance().getPlayerGang(member);
		if (!(memberGang == null || memberGang == this)) {
			throw new IllegalStateException("Player is already member of gang '" + memberGang.getName() + "'. Cannot join two gangs");
		}
		if (member != leader) {
			OfflinePlayer old = members.put(member.getName(), member);
			GangManager.getInstance().setGang(member, this);
			return old == null;
		}
		return false;
	}
	
	/**
	 * Removes a member form the gang, returns whether the player was a member or not
	 * @param player Member to remove
	 * @return True, if the OfflinePlayer was a gang member, else false
	 * @throws IllegalArgumentException Thrown if {@code member} is null
	 */
	public boolean removeMember(OfflinePlayer member) throws IllegalArgumentException {
		if (member == null) {
			throw new IllegalArgumentException("Player cannot be null");
		}
		GangManager.getInstance().setGang(member, null);
		return members.remove(member.getName()) != null;
	}
	
	/**
	 * Returns the actually count of members in the gang exclusive the leader
	 * @return The actually count of members in the gang
	 */
	public int getMemberCount() {
		return members.size();
	}
	
	/**
	 * Checks whether the gang can have more members
	 * @return True if the gang can have more members, else false
	 */
	public boolean canHaveMoreMembers() {
		return getOptionValue(GangOption.MAX_MEMBERS) == -1 || getOptionValue(GangOption.MAX_MEMBERS) > members.size();
	}
	
	/**
	 * Returns whether friendly fire is allowed for this gang or not
	 * @return True if a gang member can attack each other, else {@code false}
	 */
	public boolean isFriendlyFireAllowed() {
		return friendlyFire;
	}
	
	/**
	 * Sets whether friendly fire is allowed in this gang
	 * @param allow True if a gang member can attack each other, {@code false} if not
	 */
	public void setAllowFriendlyFire(boolean allow) {
		friendlyFire = allow;
	}
	
	/**
	 * Sets a gang option
	 * @param option The option type
	 * @param value The new option value
	 */
	public <T> void setOption(GangOption<T> option, T value) {
		if (option == null) {
			throw new IllegalArgumentException("option is not allowed to be null");
		}
		if (value == null) {
			throw new IllegalArgumentException("value is not allowed to be null");
		}
		options.put(option, value);
	}
	
	/**
	 * Returns the value of a gang option
	 * @param option The option type
	 * @return The value of the gang option. Default value, if not set
	 * @see Gang#getOptionValue(GangOption)
	 */
	@SuppressWarnings("unchecked")
	public <T> T getOptionValue(GangOption<T> option) {
		Class<?> valueClass = option.getValueClass();
		if (valueClass == Boolean.class) {
			return (T) getOptionValueOrDefault((GangOption<Boolean>) option, Boolean.FALSE);
		}
		if (valueClass == Integer.class) {
			return (T) getOptionValueOrDefault((GangOption<Integer>) option, Integer.valueOf(0));
		}
		return null;
	}
	
	/**
	 * Returns the value of a gang option or {@code dflt}, if not set
	 * @param option The option type
	 * @param dflt The default value. Gets returned if the specific {@code option} is not set for this {@code Gang}
	 * @return The gang value or {@code dflt}
	 */
	@SuppressWarnings("unchecked")
	public <T> T getOptionValueOrDefault(GangOption<T> option, T dflt) {
		T value = (T) options.get(option);
		if (value != null) {
			return value;
		}
		return dflt;
	}

}

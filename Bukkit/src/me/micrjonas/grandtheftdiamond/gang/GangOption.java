package me.micrjonas.grandtheftdiamond.gang;

import java.util.HashMap;
import java.util.Map;

@Deprecated
public class GangOption<T> {
	
	private static final Map<String, GangOption<?>> values = new HashMap<>();
	
	/**
	 * Whether members of the same gang can attack each other
	 */
	public static final GangOption<Boolean> FRIENDLY_FIRE = new GangOption<>("FRIENDLY_FIRE", Boolean.class);
	
	/**
	 * The maximal member count
	 * @deprecated May get removed. Maybe in the future a gang can have leader-exp specific members
	 */
	@Deprecated
	public static final GangOption<Integer> MAX_MEMBERS = new GangOption<>("MAX_MEMBERS", Integer.class);
	
	/**
	 * Does the leader need to invite a {@code Player} to the gang?
	 * @deprecated May get removed. Maybe in the future only the leader can invite. Makes more sense
	 */
	@Deprecated
	public static final GangOption<Boolean> LEADER_MUST_INVITE = new GangOption<>("OWNER_MUST_INVITE", Boolean.class);
	
	public static GangOption<?> valueOf(String name) {
		GangOption<?> option = values.get(name);
		if (option != null) {
			return option;
		}
		throw new IllegalArgumentException("Enum value '" + name + "' does not exist");
	}
	
	public static GangOption<?>[] values() {
		return new GangOption[]{FRIENDLY_FIRE, MAX_MEMBERS, LEADER_MUST_INVITE};
	}
	
	private static int currentOrdinal = 0;
	
	private final String name;
	private final Class<?> valueClass;
	private final int ordinal;
	
	private GangOption(String name, Class<?> valueClass) {
		this.name = name;
		this.valueClass = valueClass;
		ordinal = currentOrdinal++;
		values.put(name, this);
	}
	
	
	@Override
	public String toString() {
		return name();
	}
	
	public String name() {
		return name;
	}
	
	public int ordinal() {
		return ordinal;
	}
	
	public Class<?> getValueClass() {
		return valueClass;
	}
	
}

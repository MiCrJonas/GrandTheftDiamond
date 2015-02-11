package me.micrjonas.grandtheftdiamond;

/**
 * Team of GrandTheftDiamond
 */
public enum Team {
	
// Start of static
	/**
	 * Civilian team
	 */
	 CIVILIAN(true),
	 
	 /**
	  * Cop team
	  */
	 COP(true),
	 
	 /**
	  * Help value, no real team.
	  * <ul>
	  * 	<li>Example: Used to ban a player for each team</li>
	  * </ul>
	  */
	 EACH_TEAM(false),
	 
	 /**
	  * Help value, no real team.
	  * <ul>
	  * 	<li>No team, use this instated of null</li>
	  * </ul>
	  */
	 NONE(false);
	 
	 /**
	 * Checks whether the given String is a team's name,
	 * Ignores case sensitive
	 * @param teamName The team name to check
	 * @return True if the given String is a name of a team, else false
	 */
	public static boolean isTeamIgnoreCase(String teamName) {
		return isTeam(teamName.toUpperCase());
	}
	
	 /**
	 * Checks whether the given String is a team
	 * @param teamName The team name to check
	 * @return True if the given String is a name of a team, else false
	 */
	public static boolean isTeam(String teamName) {
		try {
			valueOf(teamName);
			return true;
		}
		catch (IllegalArgumentException ex) {
			return false;
		}
	}
		
	/**
	 * Returns the team of the given name,
	 * Ignores case sensitive 
	 * @param teamName
	 * @return The team with the given name; Team.NONE if the team does not exists
	 */
	public static Team getTeamIgnoreCase(String teamName) {
		return (valueOf(teamName.toUpperCase()));
	}
	
	/**
	 * Returns the team of the given name
	 * @param teamName
	 * @return The team with the given name
	 */
	public static Team getTeam(String teamName) {
		return valueOf(teamName);
	}
	
	/**
	 * Checks whether two teams are the same<br>
	 * 
	 * Example: {@link #EACH_TEAM} is compatible with {@link #CIVILIAN},<br> 
	 * NONE is not compatible with COP,<br> 
	 * and {@link Team#COP} is not compatible with {@link #CIVILIAN}
	 * 
	 * @param team1 The first team to check
	 * @param team2 The second team to check
	 * @return True if both teams are the same or compatible, else false
	 */
	public static boolean isCompatible(Team team1, Team team2) {
		return team1 == team2 || team1 == Team.EACH_TEAM || team2 == Team.EACH_TEAM;
	}
	
	/**
	 * Returns the real teams. Array with {@link #CIVILIAN} and {@link #COP} as values
	 * @return An Array with {@link #CIVILIAN} and {@link #COP} as values
	 */
	public static Team[] getRealTeams() {
		return new Team[]{CIVILIAN, COP};
	}
	
	/**
	 * Throws an IllegalArgumentException if {@code team} is not a real Team
	 * @param team The team to check
	 * @throws IllegalArgumentException Thrown if {@code team} is not a real Team
	 */
	public static void requiresRealTeam(Team team) throws IllegalArgumentException {
		if (team == null) {
			throw new IllegalArgumentException("team is not allowed to be null");
		}
		if (!team.isRealTeam()) {
			throw new IllegalArgumentException("Team " + team.name() + " is not a real Team. Real team is required to pass");
		}
	}
//End of static
	
	private boolean realTeam;
	
	private Team(boolean realTeam) {
		this.realTeam = realTeam;
	}
	
	/**
	 * Checks whether the team is a real team
	 * {@link #CIVILIAN} and {@link #COP} are real teams, {@link #EACH_TEAM} and {@link #NONE} not
	 * @return True if the team is {@link Team#CIVILIAN} or {@link #COP}, else {@code false}
	 */
	public boolean isRealTeam() {
		return realTeam;
	}
	
	/**
	 * Equivalent to {@link Team#isCompatible(this, team)}
	 * @param team The {@code Team} to check
	 * @return True if the {@code Team}s are compatible, else {@code false}
	 * @see Team#isCompatible(Team, Team)
	 * @throws IllegalArgumentException Thrown if {@code team} is {@code null}
	 */
	public boolean isCompatible(Team team) throws IllegalArgumentException {
		return isCompatible(this, team);
	}

}

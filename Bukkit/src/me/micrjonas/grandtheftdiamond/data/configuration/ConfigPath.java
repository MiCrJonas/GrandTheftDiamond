package me.micrjonas.grandtheftdiamond.data.configuration;

import java.util.Arrays;

import me.micrjonas.grandtheftdiamond.Team;
import me.micrjonas.grandtheftdiamond.util.DataType;
import me.micrjonas.grandtheftdiamond.util.Enums;

/**
 * Represents a path of the plugin's file 'config.yml'
 */
public enum ConfigPath implements ConfigurationPath {
	
//Main
	ALLOW_DEV_MESSAGE(DataType.BOOLEAN, "allowDevMessage", true),
	USE_UPDATER(DataType.BOOLEAN, "useUpdater", true),
	UPDATE_CHECK_INTERVAL(DataType.INTEGER, "updateCheckIntervalInMinutes", 120),
	USE_CHANGELOG_FUNCTION(DataType.BOOLEAN, "useChangeLogFunction", true, "Want to use the change log function of the plugin? Connects to dev.bukkit.org"),
	AUTO_SAVE_INTERVAL(DataType.INTEGER, "autoSaveIntervall", 600, "Interval duration of the data storage in seconds. Set to '-1' to disable"),
	CHAT_PREFIX(DataType.STRING, "chatPrefix", "&6[GT-Diamond] &r", "The chat prefix of the plugin's messages"),
	SAVE_INVENTORY_AFTER_GAME(DataType.BOOLEAN, "saveInventoryAfterLeavingGame", true, "Want to store the inventory of a player and restore it after joining the game again?"),
	WAND_ITEM(DataType.ENUM, "wandItem", "ARROW"),
	
	MAX_LINES_PER_PAGE(DataType.INTEGER, "maxLinesPerPage", 8) {
		@Override
		public boolean isValidValue(String value) {
			return inRange(value, 1, Integer.MAX_VALUE);
		}
	},
	
	IGNORE_CANCELLED_INTERACTS(DataType.BOOLEAN, "ignoreCancelledInteracts", false, "If true, you cannot use firearms or other items if an other plugin cancelled the interac with the item"),
	TELEPORT_TO_LEAVE_LOCATION(DataType.BOOLEAN, "teleportToLeavePositionOnJoin", true, "Set to true if you want a player gets teleported back to his old location after he joined the game"),
	
	DISABLE_AUTO_HEAL(DataType.BOOLEAN, "disableAutoheal", true),
	DISABLE_HUNGER(DataType.BOOLEAN, "disableHunger", true),
	DISABLE_MOBSPAWNING_IN_ARENA(DataType.BOOLEAN, "disableMobspawningInArena", true),
	
	JOIN_TELEPORT_DELAY(DataType.INTEGER, "joinTeleportDelay", 0) {
		@Override
		public boolean isValidValue(String value) {
			return inRange(value, -1, Integer.MAX_VALUE);
		}
	},
	
//Signs
	SIGN_TITLE(DataType.STRING, "signs.signTitle", "[&aGTD&r]") {
		@Override
		public boolean isValidValue(String value) {
			return validSignLine(value);
		}
	},
	
	UPDATE_COOLDOWN_SIGNS(DataType.BOOLEAN, "signs.updateCooldownSigns", true),
	SIGN_USE_VAULT(DataType.BOOLEAN, "signs.shop.useVaultEconomy", false),
	
	SIGN_COOLDOWN_ACTIVE(DataType.STRING, "signs.item.cooldown.active", "&cCooldown") {
		@Override
		public boolean isValidValue(String value) {
			return validSignLine(value);
		}
	},
	
	SIGN_COOLDOWN_INACTIVE(DataType.STRING, "signs.item.cooldown.inactive", "&aUsable") {
		@Override
		public boolean isValidValue(String value) {
			return validSignLine(value);
		}
	},
	
	TELEPORT_TO_HOUSE_BY_SIGN(DataType.BOOLEAN, "signs.house.teleportToHouseIfOwn", true),
	SIGN_HOUSE_FREE(DataType.STRING, "signs.house.free", "&aFor free") {
		@Override
		public boolean isValidValue(String value) {
			return validSignLine(value);
		}
	},
	
//Temporary data
	CLEAR_DATA_ON_DISCONNECT(DataType.BOOLEAN, "temporaryPlayerData.clearDataOnDisconnect", true, "Set to true if you wish to clear the temporary player (Wand enabled, arresting, ...) data after a player quits"),
	TIME_TO_CLEAR_DATA_ON_DISCONNECT(DataType.INTEGER, "temporaryPlayerData.timeToClearDataOnDisconnect", 600, "The time until the data gets cleared after disconnecing in seconds.") {
		@Override
		public boolean isValidValue(String value) {
			return inRange(value, 0, Integer.MAX_VALUE);
		}
	},
	
//Economy
	ECONOMY_START_BALANCE(DataType.INTEGER, "economy.startBalance", 1000),
	ECONOMY_MIN_BALANCE(DataType.INTEGER, "economy.minBalance", -100_000),
	ECONOMY_MAX_BALANCE(DataType.INTEGER, "economy.maxBalance", 1_000_000_000, "Cannot be greater than 2.147.483.647 (Buffer overflow)"),
	USE_GTD_ECONOMY(DataType.BOOLEAN, "economy.useGrandTheftDiamondEconomy", true, "Set to false if you want to disable the plugin's economy system. Vault is enabled nevertheless"),
	
	ECONOMY_PERCENT_OF_DEFAULT_GTD(DataType.BOOLEAN, "economy.percentOfDefaultBalance.GTD", 100,
			"You can set an economy balance for some events like robbing. The player will get the defined number percent of this balance for the plugin's economy account") {
		@Override
		public boolean isValidValue(String value) {
			return inRange(value, 0, Integer.MAX_VALUE);
		}
	},
	
	ECONOMY_PERCENT_OF_DEFAULT_VAULT(DataType.BOOLEAN, "economy.percentOfDefaultBalance.Vault", 20,
			"You can set an economy balance for some events like robbing. The player will get the defined number percent of this balance for his Vault economy account") {
		@Override
		public boolean isValidValue(String value) {
			return inRange(value, 0, Integer.MAX_VALUE);
		}
	},
	
	RESET_TO_START_BALANCE(DataType.BOOLEAN, "economy.resetToStartBalance", true, "Set to true if you want a player has the default balance after using the 'eco reset' command"),
	CURRENCY_SYMBOL(DataType.STRING, "economy.currency.symbol", "$"),
	
//Ban system
	SEND_DEFAULT_BAN_MESSAGE(DataType.BOOLEAN, "banSystem.sendDefaultBanMessage", true),
	USE_COP_AUTO_BAN(DataType.BOOLEAN, "banSystem.copAutoban.use", true),
	COP_AUTO_BAN_COUNT_COPS(DataType.BOOLEAN, "banSystem.copAutoban.countCopKills", true),
	COP_AUTO_BAN_COUNT_CIVILIANS(DataType.BOOLEAN, "banSystem.copAutoban.countCivilianKills", true),
	COP_AUTO_BAN_MAX_KILLS(DataType.BOOLEAN, "banSystem.copAutoban.maxKilledCopsAndCivilians", 10),
	
	COP_AUTO_BAN_BAN_AS(DataType.ENUM, "banSystem.copAutoban.banAs", "COP") {
		@Override
		public boolean isValidValue(String value) {
			return Enums.valueOf(Team.class, value) != null;
		}
	},
	
	COP_AUTO_BAN_TIME(DataType.INTEGER, "banSystem.copAutoban.time", -1, "Time in seconds") {
		@Override
		public boolean isValidValue(String value) {
			return inRange(value, -1, Integer.MAX_VALUE);
		}
	}, 
	
	COP_AUTO_BAN_REASON(DataType.STRING, "banSystem.copAutoban.reason", "You killed to much civilians and cops!"),
	
//Teleport
	DISABLE_TELEPORT_TO_OUTSIDE(DataType.STRING, "disableTeleport.fromInsideToOutsideArena", true, "Cancel teleport events of a player who is in game if the to-location is outside of the arena"),
	DISABLE_TELEPORT_TO_INSIDE(DataType.STRING, "disableTeleport.fromInsideToInsideArena", false, "Cancel teleport event of a player who is in game"),
	
//Commands
	BLOCK_COMMANDS_INGAME(DataType.BOOLEAN, "coammands.blockCommandsIngame", true),
	COMMAND_BLOCK_WHITELIST(DataType.STRING_LIST, "commands.ingameWhitelist", 
			Arrays.asList("/gtd", "/gta", "/grandtheftdiamond", "/grandtheftauto", "time", "who")),
	
//Optional Add Ons
	ENABLED_ADD_ONS(DataType.BOOLEAN, "addOns.useVaultChat", true),
	USE_VAULT_ECONOMY(DataType.BOOLEAN, "addOns.useVaultEconomy", false),
	USE_NAMETAG_EDIT(DataType.BOOLEAN, "addOns.useNametagEdit", true),
	
//Jobs
	MAX_JOBS_PER_PLAYER_DEFAULT(DataType.INTEGER, "jobs.maxJobsPerPlayer", 1) {
		@Override
		public boolean isValidValue(String value) {
			return inRange(value, 0, Integer.MAX_VALUE);
		}
	},
	
//Chat
	CHAT_MESSAGE_PREFIX_GLOBAL(DataType.STRING, "chat.global.messagePrefix", "!"),
	CHAT_MESSAGE_PREFIX_IN_LOCAL(DataType.STRING, "chat.local.messagePrefix", "*"),
	CHAT_MESSAGE_PREFIX_TEAM(DataType.STRING, "chat.team.messagePrefix", "-"),
	
	CHAT_PREFIX_GLOBAL(DataType.STRING, "chat.global.chatPrefix", "&7[Global]&r "),
	CHAT_PREFIX_LOCAL(DataType.STRING, "chat.local.chatPrefix", "&7[Local]&r "),
	CHAT_PREFIX_TEAM(DataType.STRING, "chat.team.chatPrefix", "&7[Team]&r "),
	
	CIVILIAN_PREFIX(DataType.STRING, "chat.server.prefix.civilian", "&7[&aCiv&7]&r "),
	CIVILIAN_SUFFIX(DataType.STRING, "chat.server.suffix.civilian", ""),
	COP_PREFIX(DataType.STRING, "chat.server.prefix.cop", "&7[&9Cop&7]&r "),
	COP_SUFFIX(DataType.STRING, "chat.server.suffix.cop", ""),
	GANGSTER_PREFIX(DataType.STRING, "chat.server.prefix.gangster", "&7[&cCiv&7]&r "),
	GANGSTER_SUFFIX(DataType.STRING, "chat.server.suffix.gangster", " &c[%wantedLevel%]&r"),
	
//Scoreboard
	USE_SCOREBOARD_NAMETAG(DataType.BOOLEAN, "scoreboard.nametag.use", true, "Change the nametag of ingame players?"),
	
//Gangs
	INGAME_TO_MANAGER_GANGS(DataType.BOOLEAN, "gangs.mustBeIngameToManageGangs", true),
	
	MAX_MEMBERS_PER_GANG(DataType.INTEGER, "gangs.maxMembersPerGang", 5) {
		@Override
		public boolean isValidValue(String value) {
			return inRange(value, 1, Integer.MAX_VALUE);
		}
	},
	
	MAX_OWNED_GANGS(DataType.INTEGER, "gangs.maxOwnedGangs", 1) {
		@Override
		public boolean isValidValue(String value) {
			return inRange(value, -1, Integer.MAX_VALUE);
		}
	},
	
	MAX_GANG_MEMBERSHIPS(DataType.INTEGER, "gangs.maxMemberships", 1) {
		@Override
		public boolean isValidValue(String value) {
			return inRange(value, -1, Integer.MAX_VALUE);
		}
	},
	
	GANG_DEFAULT_OPTION_FRIENDLYFIRE(DataType.BOOLEAN, "gangs.optionDefaults.friendlyFire", false),
	GANG_DEFAULT_OPTION_OWNER_MUST_INVITE(DataType.BOOLEAN, "gangs.optionDefaults.ownerMustInvite", true),
	
	;
//END OF PATHS
	
	private static boolean validSignLine(String value) {
		
		return value.length() <= 16;
		
	}
	
	
	private static boolean inRange(String value, int min, int max) {
		
		return DataType.INTEGER.isValidValue(value) 
				&& DataType.INTEGER.fromString(value) >= min && DataType.INTEGER.fromString(value) <= max;
		
	}
	
//END OF STATIC
	
	private DataType<?> type;
	private String yamlFormat;
	private Object defaultValue;
	private String description;

	private ConfigPath(DataType<?> type, String yamlFormat, Object defaultValue, String description) {
		
		this.type = type;
		this.yamlFormat = yamlFormat;
		this.defaultValue = defaultValue;
		this.description = description;
		
	}
	
	
	private ConfigPath(DataType<?> type, String yamlFormat, Object defaultValue) {
		
		this(type, yamlFormat, defaultValue, null);
		
	}
	
	
	@Override
	public final String toString() {
		
		return yamlFormat;
		
	}
	
	
	@Override
	public boolean isValidValue(String value) {
		
		return type.isValidValue(value);
		
	}
	
	
	@Override
	public DataType<?> getType() {
		
		return type;
		
	}
	
	
	@Override
	public String getPathYaml() {
		
		return yamlFormat;
		
	}
	
	
	@Override
	public Object getDefaultValue() {
		
		return defaultValue;
		
	}
	
	
	@Override
	public String getDescription() {
		
		return description;
		
	}
	
}

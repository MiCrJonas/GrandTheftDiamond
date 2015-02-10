package me.micrjonas.grandtheftdiamond;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import me.micrjonas.grandtheftdiamond.bukkit.BukkitGrandTheftDiamondPlugin;
import me.micrjonas.grandtheftdiamond.data.FileManager;
import me.micrjonas.grandtheftdiamond.data.PluginFile;
import me.micrjonas.grandtheftdiamond.messenger.LanguageManager;
import me.micrjonas.grandtheftdiamond.messenger.Messenger;
import me.micrjonas.grandtheftdiamond.stats.StatsType;
import me.micrjonas.grandtheftdiamond.util.bukkit.PotionEffects;
import me.micrjonas.grandtheftdiamond.util.bukkit.TimedPotionEffect;

import org.bukkit.Effect;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Horse;
import org.bukkit.entity.Horse.Style;
import org.bukkit.entity.Horse.Variant;
import org.bukkit.potion.PotionEffectType;

public class ConfigInizializer {

	public ConfigInizializer() {
		createReadmes();
		addDefaultMessages();
		addDefaultConfigPaths();
		addDefaultResultConfigPaths();
		addOnlyGTDModeConfigDefaults();
		addRankDefaults();
	}
	
	private void createReadmes() {
		BukkitGrandTheftDiamondPlugin.getInstance().copyFromJar("resources/readme_language.txt", new File(GrandTheftDiamond.getDataFolder() + "/language/readme.txt"), true);
		BukkitGrandTheftDiamondPlugin.getInstance().copyFromJar("resources/readme_listeners.txt", new File(GrandTheftDiamond.getDataFolder() + "/listeners/readme.txt"), true);
		BukkitGrandTheftDiamondPlugin.getInstance().copyFromJar("resources/configuration/config.yml", PluginFile.CONFIG.getFile(), false);
	}

	private void addDefaultMessages() {
		FileConfiguration messages = LanguageManager.getInstance().getLanguageFile("english");
		messages.options().header("You can use %amountGTD% and %amountVault% were you can use money %amount%! \n" + 
									"But: If you disabled Vault for the specific event, amount is not 0! \n" + 
									"Only if Vault is completely disabled, %amountVault% is always 0! \n" +
									"Use '\\n' to create a new line" );
		
		messages.addDefault("SingleWords.amount", "amount");
		messages.addDefault("SingleWords.balance", "balance");
		messages.addDefault("SingleWords.changelog", "change log");
		messages.addDefault("SingleWords.chatMode", "chat mode");
		messages.addDefault("SingleWords.chatModes", "chat modes");
		messages.addDefault("SingleWords.civilian", "civilian");
		messages.addDefault("SingleWords.civilians", "civilians");
		messages.addDefault("SingleWords.console", "console");
		messages.addDefault("SingleWords.cooldown", "cooldown");
		messages.addDefault("SingleWords.cop", "cop");
		messages.addDefault("SingleWords.cops", "cops");
		messages.addDefault("SingleWords.dealer", "dealer");
		messages.addDefault("SingleWords.editor", "editor");
		messages.addDefault("SingleWords.gangster", "gangster");
		messages.addDefault("SingleWords.gangsters", "gangsters");
		messages.addDefault("SingleWords.got", "got");
		messages.addDefault("SingleWords.help", "help");
		messages.addDefault("SingleWords.house", "house");
		messages.addDefault("SingleWords.houseIdentifier", "house identifier");
		messages.addDefault("SingleWords.id", "id");
		messages.addDefault("SingleWords.information", "information");
		messages.addDefault("SingleWords.jaildID", "jailID");
		messages.addDefault("SingleWords.job", "job");
		messages.addDefault("SingleWords.jobs", "jobs");
		messages.addDefault("SingleWords.kit", "kit");
		messages.addDefault("SingleWords.language", "manguage");
		messages.addDefault("SingleWords.level", "level");
		messages.addDefault("SingleWords.lost", "lost");
		messages.addDefault("SingleWords.minRadiusToNextCar", "min radius to next car");
		messages.addDefault("SingleWords.mission", "mission");
		messages.addDefault("SingleWords.name", "name");
		messages.addDefault("SingleWords.none", "none");
		messages.addDefault("SingleWords.object", "object");
		messages.addDefault("SingleWords.objects", "objects");
		messages.addDefault("SingleWords.option", "option");
		messages.addDefault("SingleWords.owner", "owner");
		messages.addDefault("SingleWords.page", "page");
		messages.addDefault("SingleWords.player", "player");
		messages.addDefault("SingleWords.price", "price");
		messages.addDefault("SingleWords.radius", "radius");
		messages.addDefault("SingleWords.reason", "reason");
		messages.addDefault("SingleWords.spawnDelay", "spawn delay");
		messages.addDefault("SingleWords.statsType", "stats type");
		messages.addDefault("SingleWords.team", "team");
		messages.addDefault("SingleWords.time", "time");
		messages.addDefault("SingleWords.updater", "updater");
		messages.addDefault("SingleWords.value", "value");
		
		messages.addDefault("SingleWords.stats.stats", "stats");
		
		for (StatsType type : StatsType.values())
			messages.addDefault("SingleWords.stats." + type.name().toLowerCase(), Messenger.getInstance().getWordStartUpperCase(type.name().toLowerCase().replace('_', ' ')));
		
		messages.addDefault("Messages.updateCheckFailed", "&cFailed to check for updates.");
		
		messages.addDefault("Messages.noCommand", "&cThis is not a valid command.");
		
		messages.addDefault("Messages.wrongUsage", "&cWrong usage.");
		
		messages.addDefault("Messages.notAsConsole", "&cYou cant't do this as console.");
		messages.addDefault("Messages.wrongUsageAsConsole", "&cWrong usage as console. Add some arguments.");
		
		messages.addDefault("Messages.noPermissions", "&cYou don't have permissions to do this.\nRequired permission: '%permission%'.");
		messages.addDefault("Messages.noPermissionsCommand", "&cYou don't have permissions to use this command%newLine%(with the specific arguments).\nRequired permission: '%permission%'.");
		messages.addDefault("Messages.noPermissionsUse", "&cYou don't have permissions to use this.\nRequired permission: '%permission%'.");
		messages.addDefault("Messages.noPermissionsUseObject", "&cYou don't have permissions to use this object.\nRequired permission: '%permission%'.");
		messages.addDefault("Messages.noPermissionsCreate", "&cYou don't have permissions to create this.\nRequired permission: '%permission%'.");
		messages.addDefault("Messages.noPermissionsBreak", "&cYou don't have permissions to break this.\nRequired permission: '%permission%'.");
		messages.addDefault("Messages.noPermissionsOpen", "&cYou don't have permissions to open this.\nRequired permission: '%permission%'.");
		messages.addDefault("Messages.noPermissionsEdit", "&cYou don't have permissions to edit this.\nRequired permission: '%permission%'.");
		
		messages.addDefault("Messages.cannotLeaveGame", "&cThis server is running in only GTD Mode. %newLine%Means: You can't leave the game. %newLine%&eYour admin can change this in the onlyGTDModeConfig.yml.");
		
		messages.addDefault("Messages.playerNotOnline", "&cPlayer is not online.");
		
		messages.addDefault("Messages.noOption", "&c%argument% is not a valid option.");
		messages.addDefault("Messages.noValue", "&c%argument% is not a valid value.");
		
		messages.addDefault("Messages.playerNotFound", "&cPlayer not found.");
		
		messages.addDefault("Messages.askHasEverPlayed", "&cHas he ever played GTD?");
		
		messages.addDefault("Messages.playerNotIngame", "&cPlayer is not ingame.");
		messages.addDefault("Messages.playerNotJailed", "&cPlayer %player% is not jailed.");
		messages.addDefault("Messages.playerAlreadyJailed", "&cPlayer %player% is already jailed.");
		messages.addDefault("Messages.noJailsAvailable", "&cNo jails created.");
		
		messages.addDefault("Messages.pageNotFound", "&cPage %page% does not exist.");
		
		messages.addDefault("Messages.youNeedSetupMap", "&cYou need to set up the map.");
		messages.addDefault("Messages.youNeedSetupJail", "&cYou need to setup a jail.");
		messages.addDefault("Messages.adminNeedSetupMap", "&cYour server admin needs to set up the map.");
		
		messages.addDefault("Messages.nothingToAcceptCorrupt", "&cThere is nothing to accept. Or is the player no longer jailed?");
		
		messages.addDefault("Messages.objectNotExist", "&cThis object (or ammo) doesn't exist.");
		messages.addDefault("Messages.carNotExist", "&cThis car doesn't exist.");
		
		messages.addDefault("Messages.noSignForCooldown", "&cThis is not a sign to set a cooldown.");
		
		messages.addDefault("Messages.noFuel", "&cYou don't have enough fuel to use this.");
		messages.addDefault("Messages.notEnoughAmmo", "&cYou don't have enough ammo.");
		
		messages.addDefault("Messages.cannotPayToYourself", "&cYou can not pay money to yourself.");
		
		messages.addDefault("Messages.notEnoughMoney", "&cYou do not have enough money.");
		messages.addDefault("Messages.notEnoughMoneyOther", "&c%player% do not have enough money.");
		
		messages.addDefault("Messages.notAJob", "&c'%argument%' is not a valid job.");
		messages.addDefault("Messages.notAFirearm", "&c'%argument%' is not a valid firearm.");
		
		messages.addDefault("Messages.maxFlightHeightReached", "&cYou reached the maximal flight height.");
		
		messages.addDefault("Messages.wantedLevelToLowOther", "&cThe wanted level of %player% is to low. Minimum is %amount%.");
		
		messages.addDefault("Messages.cannotRobAsCop", "&cYou cannot rob something as a cop.");
		
		messages.addDefault("Messages.notASafe", "&cI'm sorry, but this is not a safe.");
		
		messages.addDefault("Messages.robStopped", "&cThe robbing of the actually safe was stopped.");
		
		messages.addDefault("Messages.safeAlreadyRobbed", "&cThis safe is already robbed. Come back later.");
		
		messages.addDefault("Messages.cannotTaseCops", "&cYou can not tase cops.");
		
		messages.addDefault("Messages.cannotHandcuffCops", "&cYou cannot handcuff cops.");
		messages.addDefault("Messages.cannotHandcuffAsCivilian", "&cYou cannot handcuff someone as Civilian.");
		
		messages.addDefault("Messages.alreadyRobbing", "&cYou are already robbing something. Please wait.");
		
		messages.addDefault("Messages.itemReceived", "&aYou got (some/a[n]) %item%.");
		messages.addDefault("Messages.itemReceivedOther", "&a%player% got a(n) %item%.");
		
		messages.addDefault("Messages.alreadyIngame", "&cYou are already in the game.");
		
		messages.addDefault("Messages.notIngame", "&cYou are not in the game.");
		
		messages.addDefault("Messages.alreadyInMission", "&cYou are already in a mission.");
		
		messages.addDefault("Messages.notInMission", "&cYou are not in a mission.");
		
		messages.addDefault("Messages.otherNotInMission", "&c%player% is not in a mission.");
		
		messages.addDefault("Messages.nothingToConfirm", "&cThere is nothing to confirm.");
		
		messages.addDefault("Messages.createArenaFirst", "&cCreate the arena first.");
		
		messages.addDefault("Messages.kickPlayersFirst", "&cKick all players out of GTD before doing this.");
		
		messages.addDefault("Messages.setBothPoints", "&cPlease set both points. You can do this with an arrow. Right + Left-Click. Enable your wand with '/gtd wand'.");
		
		messages.addDefault("Messages.canNotUseCommands", "&cThis command is blocked in the game.");
		
		messages.addDefault("Messages.arenaEnd", "&cThe map ends here.");
		
		messages.addDefault("Messages.signTitleTooLong", "&cThe signtitle is too long. (Maximum is 15)");
		
		messages.addDefault("Messages.changeInConfig", "&cPlease change it in the config.yml.");
		
		messages.addDefault("Messages.signLineMustBeANumber", "&cLine %line% must be a number. Please try again.");
		messages.addDefault("Messages.signLineWrong", "&cLine %line% is wrong. Please try again.");
		messages.addDefault("Messages.signLineEmpty", "&cLine %line% is empty. Please try again.");
		
		messages.addDefault("Messages.mustBeInArena", "&cYou must be in the arena to do this.");
		
		messages.addDefault("Messages.mustBeCop", "&cYou must be a cop to do this.");
		
		messages.addDefault("Messages.otherMustBeHandcuffed", "&c%player% must be a civilian who is handcuffed.");
		
		messages.addDefault("Messages.notAJail", "&c%argument% is not a valid jail.");
		
		messages.addDefault("Messages.mustBeANumber", "&cArgument %argument% must be a number.");
		
		messages.addDefault("Messages.mustBeACop", "&cYou must be a cop to do this.");
		messages.addDefault("Messages.mustBeACopOther", "&c%player% must be a cop.");
		
		messages.addDefault("Messages.haveCooldown", "&cThis sign has a cooldown. (%time%)");
		
		messages.addDefault("Messages.movedWhileJoin", "&cYou moved. Use /gtd join again.");
		
		messages.addDefault("Messages.mustBeIngame", "&cYou must be ingame to do this.");
		messages.addDefault("Messages.mustBeIngameOther", "&c%player% must be ingame.");
		
		messages.addDefault("Messages.notSelfAsPlayer", "&cYou can't use yourself as player.");
		
		messages.addDefault("Messages.jailNotSet", "&cJail %jail% was not set. Create the spawn and at least one cell");
		messages.addDefault("Messages.jailSpawnNotSet", "&cSet the spawn for jail '%jail%' first.");
		
		messages.addDefault("Messages.toFarAway", "&c%player% is to far away.");
		
		messages.addDefault("Messages.mustBeHigherThan", "&cArgument %argument% must be higher than %amount%.");
		
		messages.addDefault("Messages.toMuchCorruptAcceptsPerPlayer", "&c%player% accepted to much corrupts of you.");
		
		messages.addDefault("Messages.mustBeJailed", "&cYou must be jailed to do this.");
		
		messages.addDefault("Messages.useGun", "&cPlease use a gun.");
		
		messages.addDefault("Messages.died", "&eYou died and %lostOrGotMoney% %amountMoney%$ and %lostOrGotExp% %amountExp% exp.");

		messages.addDefault("Messages.getKilled", "&eYou were killed by %player% and %lostOrGotMoney% %amountMoney%$ and %lostOrGotExp% %amountExp% exp.");

		messages.addDefault("Messages.killedOther", "&eYou killed %player% and %lostOrGotMoney% %amountMoney%$ and %lostOrGotExp% %amountExp% exp.");
		
		messages.addDefault("Messages.alreadyHandcuffed", "&c%player% is already handcuffed.");
		
		messages.addDefault("Messages.getHandcuffed", "&eYou get handcuffed by %player%.");
		messages.addDefault("Messages.noLongerHandcuffed", "&eYou are no longer handcuffed.");
		
		messages.addDefault("Messages.getTased", "&eYou were tased by %player%.");
		messages.addDefault("Messages.tasedOther", "&eYou tased %player%.");
		
		messages.addDefault("Messages.releasedFromJail", "&aYou were released from jail.");
		
		messages.addDefault("Messages.handcuffedOther", "&eYou handcuffed %player%.");
		
		messages.addDefault("Messages.jailedByPlayer", "&cYou were jailed by %player% and lost %amount%$.");
		messages.addDefault("Messages.jailed", "&cYou were jailed for %time% and lost %amount%$.");
		messages.addDefault("Messages.jailedOnJoin", "&cYou are still jailed and were teleported back to jail. Time left: %time%");
		
		messages.addDefault("Messages.jailedOther", "&eYou jailed %player% and got %amount%$");
		messages.addDefault("Messages.jailedCommand", "&eYou jailed %player% for %time% seconds in jail %jail%.");
		
		messages.addDefault("Messages.arrested", "&eYou were arrested by %player% for %time% and lost %amount%$");
		messages.addDefault("Messages.arrestedOther", "&eYou arrested %player% for %time% and got %amount%$");
		
		messages.addDefault("Messages.detained", "&eYou You were detained by %player%.");
		messages.addDefault("Messages.detainedOther", "&eYou detained %player%. Take him to a jail(-sign).");
		
		messages.addDefault("Messages.economyIsDisabled", "&cThe plugin's economy system is disabled. Use Vault instead (if used).");
		messages.addDefault("Messages.notEnoughVaultMoney", "&cThere is not enough money on your server account.");
		messages.addDefault("Messages.notEnoughGtdMoney", "&cYou don't have enough money on your GTD-Account.");
		
		messages.addDefault("Messages.cannotTazeCops", "&cYou can not taze cops");
	
		messages.addDefault("Messages.cannotUseTaserAsCivlian", "&cYou can not use a taser as civilian");
		
		messages.addDefault("Messages.cannotHitCopsAsCop", "&cYou can't hit cops as cop.");
		messages.addDefault("Messages.cannotHitNonGangsterAsCop", "&cYou can't hit civilians without wantedlevel as cop.");
		
		messages.addDefault("Messages.getTazed", "&eYou get tazed by %player%.");
		
		messages.addDefault("Messages.kicked", "&cYou were kicked from the game.");
		
		messages.addDefault("Messages.targetPlayerSet", "&aYour compass shows you now the position of %player%.");
		
		messages.addDefault("Messages.posCreated", "&aPosition %position% created.");
		
		messages.addDefault("Messages.arenaCreated", "&aArena created.");
		
		messages.addDefault("Messages.clickSign", "&aClick a sign.");
		messages.addDefault("Messages.clickPlayer", "&aClick a player.");
		
		messages.addDefault("Messages.clickToSetSafe", "&aClick a(n) %block% to create safe '%name%'.");
		
		messages.addDefault("Messages.cooldownSet", "&aCooldown set.");
		
		messages.addDefault("Messages.needConfirmDisabled", "&aNo longer need to confirm something. Enable again with 'gtd confirm enable'");
		
		messages.addDefault("Messages.needConfirmEnabled", "&aNeed to confirm something is now enabled.");
		
		messages.addDefault("Messages.joinedGame", "&aYou joined the game in team %team% with jobs %jobs%.");
		messages.addDefault("Messages.leftGame", "&aYou left the game.");
		
		messages.addDefault("Messages.otherJoinedGame", "&a%player% &ajoined the game in team %team% with jobs %jobs%.");
		messages.addDefault("Messages.otherLeftGame", "&a%player% &aleft the game.");
		
		messages.addDefault("Messages.startRobSafe", "&aYou started robbing a safe. Please wait until it's opened.");
		
		messages.addDefault("Messages.robFinishedSafe", "&aYou robbed a safe and got %amount%$");
		
		messages.addDefault("Messages.doNotMove", "&eDon't move for the next %time%.");

		messages.addDefault("Messages.newTargetPlayer", "&aYour new targetplayer is %player%. Find him with the compass.");
		
		messages.addDefault("Messages.acceptedCorruptOther", "&a%player% accepted the corrupt.");
		messages.addDefault("Messages.acceptedCorrupt", "&aYou accepted the corrupt.");
		
		messages.addDefault("Messages.defaultSpawnSet", "&aDefault spawn set.");
		messages.addDefault("Messages.spawnSet", "&aA %spawnType% spawn with id %spawn% was set.");
		messages.addDefault("Messages.jailSpawnSet", "&aSpawn set of jail %jail%.");
		messages.addDefault("Messages.jailCellAdded", "&aJail cell added for jail %jail%.");

		messages.addDefault("Messages.howToDisableConfirmNeed", "&eYou can disable that you must always do so. '/gtd confirm disable'");
		
		messages.addDefault("Messages.allKicked", "&aAll players were kicked from the game.");
		messages.addDefault("Messages.kickedOther", "&aYou kicked %player% from the game.");
		messages.addDefault("Messages.pluginReloaded", "&aPlugin reloaded.");
		
		messages.addDefault("Messages.safeCreated", "&aSafe '%name%' created.");
		messages.addDefault("Messages.safeUpdated", "&aSafe '%name%' updated.");
		messages.addDefault("Messages.safeRemoved", "&aSafe removed.");
		
		messages.addDefault("Messages.signCreated", "&aSign created succesfully.");
		messages.addDefault("Messages.signRemoved", "&aSign removed.");
		
		messages.addDefault("Messages.ingameList", "&eThe following players are playing GTD (%ingameCount%):");
		
		messages.addDefault("Messages.updaterNotEnabled", "&cThe updater is disabled in config.yml.");
		messages.addDefault("Messages.updated", "&aNew version &e%version% &awas downloaded... Reload your server to use it.");
		messages.addDefault("Messages.downloadingFile", "&aDownloading file...");
		messages.addDefault("Messages.noUpdate", "&cNo update found.");
		
		messages.addDefault("Messages.banReasonFormat", " &cReason: &e%reason%");
		messages.addDefault("Messages.alreadyBanned", "&c%player% is already banned from GTD.");
		messages.addDefault("Messages.notBanned", "&c%player% is not banned from GTD.");
		messages.addDefault("Messages.notBannedTeam", "&c%player% is not banned from GTD (%team%).");
		messages.addDefault("Messages.getBanned", "&cYou were banned from GTD by %player% (%team%).");
		messages.addDefault("Messages.bannedOther", "&aYou banned %player% from GTD (%team%).");
		messages.addDefault("Messages.getTimeBanned", "&cYou were banned from GTD by %player% (%team%) for %time%.");
		messages.addDefault("Messages.timeBannedOther", "&aYou banned %player% from GTD (%team%) for %time% minutes.");
		messages.addDefault("Messages.getUnbanned", "&aYou are no longer banned from GTD.");
		messages.addDefault("Messages.unbannedOther", "&aYou unbanned %player% from GTD.");
		messages.addDefault("Messages.getUnbannedTeam", "&aYou are no longer banned from GTD (%team%).");
		messages.addDefault("Messages.unbannedTeamOther", "&aYou unbanned %player% from GTD (%team%).");
		messages.addDefault("Messages.banned", "&cYou are banned from GTD.");
		messages.addDefault("Messages.tempBanned", "&cYou are banned from GTD. Ban time left: %time%");
		messages.addDefault("Messages.bannedTeam", "&cYou are banned from GTD (%team%).");
		messages.addDefault("Messages.tempBannedTeam", "&cYou are banned from GTD (%team%). Ban time left: %time%");
		
		messages.addDefault("Messages.showBalanceSelf", "&eYour balance: &c$%amount%");
		messages.addDefault("Messages.showBalanceOther", "&e%player%'s balance: &c$%amount%");
		
		messages.addDefault("Messages.gaveMoneyToPlayer", "&eYou gave &c$%amount% &eto %player%.");
		messages.addDefault("Messages.gotMoneyFromPlayer", "&eYou got &c$%a &efrom %player%.");
		
		messages.addDefault("Messages.ecoSet", "&eYour GTD-Balance was set to &a$%amount%&e.");
		messages.addDefault("Messages.ecoSetOther", "&eGTD-Balance of %player% was set to &a$%amount%&e.");
		
		messages.addDefault("Messages.ecoGive", "&eYou got &a$%amount%&e.");
		messages.addDefault("Messages.ecoGiveOther", "&e%player% got &a$%amount%&e.");
		
		messages.addDefault("Messages.ecoTake", "&a$%a&e were taken from your GTD-Account.");
		messages.addDefault("Messages.ecoTakeOther", "&a$%amount%&e &ewere taken from %player%'s GTD-Account.");
		
		messages.addDefault("Messages.ecoReset", "&aYour GTD-balance have been resetted");
		messages.addDefault("Messages.ecoResetOther", "&a%player%'s GTD-Balance have been resetted");
		
		messages.addDefault("Messages.dataSaved", "&aData saved.");
		
		messages.addDefault("Messages.sign.invalidSignClicked", "&cYou cliecked an invalid sign.");
		
		messages.addDefault("Messages.gang.alreadyExist", "&cGang '%gang%' does already exist.");
		messages.addDefault("Messages.gang.alreadyInvited", "&c%player% is already invited.");
		messages.addDefault("Messages.gang.cannotAcceptInvite", "&cYou cannot accept a gang invite for gang '%gang%'.");
		messages.addDefault("Messages.gang.notExist", "&cGang '%gang%' does not exist.");
		messages.addDefault("Messages.gang.noLongerExist", "&cGang '%gang%' does no longer exist.");
		messages.addDefault("Messages.gang.noPvP", "&c%player% is in one of your gangs which have disabled PvP.\nYou can't hit him/her.");
		messages.addDefault("Messages.gang.toMuchMembers", "&cGang '%gang%' has to much members. Maximum is %amount%.");
		messages.addDefault("Messages.gang.alreadyInGang", "&cYou are already in a gang (%gang%).");
		messages.addDefault("Messages.gang.alreadyInGangOther", "&c%player% is already in a gang (%gang%).");
		messages.addDefault("Messages.gang.notOwner", "&cYou are not the owner of the gang. So you can't do this.");
		messages.addDefault("Messages.gang.optionSet", "&aOption &e%option% &aset to &e%value% &afor gang &e%gang%&a.");
		messages.addDefault("Messages.gang.created", "&aGang '%gang%' created.");
		messages.addDefault("Messages.gang.createdOwner", "&aGang '%gang%' created with owner %player%.");
		messages.addDefault("Messages.gang.deleted", "&aGang '%gang%' deleted.");
		messages.addDefault("Messages.gang.otherDeleted", "&a%player% deleted the gang '%gang%'. %newLine%You were a member or owner of this gang.");
		messages.addDefault("Messages.gang.askForMembership", "&e%player% want to invite you to his gang '%gang%'. %newLine%Use &a/gtd gang accept %gang% &eto accept.");
		messages.addDefault("Messages.gang.askedForMembershipOther", "&eYou asked %player% for a membership in the gang '%gang%'.");
		messages.addDefault("Messages.gang.joined", "&aYou joined the gang '%gang%'.");
		messages.addDefault("Messages.gang.otherJoined", "&a%player% joined the gang '%gang%'.");
		
		messages.addDefault("Messages.kit.notExist", "&cKit %kit% does not exist.");
		messages.addDefault("Messages.kit.added", "&aKit %kit% was added to your inventory.");
		messages.addDefault("Messages.kit.addedOther", "&aKit %kit% was added to %player%'s inventory.");
		messages.addDefault("Messages.kit.hasCooldown", "&cKit %kit% has a cooldown. Time left: %time%.");
		messages.addDefault("Messages.kit.hasCooldownOther", "&cKit %kit% has a cooldown for %player%. Time left: %time%.");
		
		messages.addDefault("Messages.mission.notExist", "&cMission %mission% does not exist.");
		messages.addDefault("Messages.mission.alreadyInMission", "&cYou are already in a mission.");
		messages.addDefault("Messages.mission.alreadyInMissionOther", "&c%player% is already in a mission.");
		messages.addDefault("Messages.mission.joined", "&aYou joined mission %mission%.");
		messages.addDefault("Messages.mission.joinedOther", "&a%player% joined mission %mission%.");
		
		messages.addDefault("Messages.car.notOwner", "&cYou are not the owner of this car/horse. %newLine%(Owner is %owner%)");
		messages.addDefault("Messages.car.canNotChangeInventory", "&cYou are not allowed to change the inventory of that car/horse.");
		messages.addDefault("Messages.car.canNotDamageOwn", "&cYou are not allowed to damage your own car/horse.");
		messages.addDefault("Messages.car.canNotDamage", "&cYou are not allowed to damage the car/horse. %newLine%(Owner is %owner%)");
		messages.addDefault("Messages.car.canNotDamageNoOwner", "&cYou are not allowed to damage the car/horse.");
		
		
		messages.addDefault("Messages.house.clickDoorToSet", "&aClick a door to set the door of house %house%.");
		messages.addDefault("Messages.house.doorCreated", "&aYou created the door for house %house%.");
		messages.addDefault("Messages.house.bought", "&aYou bought the house %house%.");
		messages.addDefault("Messages.house.notPurchasable", "&cYou cannot buy this house.");
		messages.addDefault("Messages.house.notOwnHouse", "&cHouse %house% is not yours.");
		messages.addDefault("Messages.house.alreadyDoor", "&cThis door is already a door of an house.");
		messages.addDefault("Messages.house.noLongerHouse", "&cHouse %house% does no longer exist.");
		messages.addDefault("Messages.house.teleported", "&aTeleported to house %house%.");
		messages.addDefault("Messages.house.notExist", "&cHouse %house% does not exist.");
		messages.addDefault("Messages.house.alreadyExist", "&cHouse %house% does already exist.");
		messages.addDefault("Messages.house.doorSet", "&aYou set the door house %house%.");
		messages.addDefault("Messages.house.priceSet", "&aYou set the price of house %house%.");
		messages.addDefault("Messages.house.spawnSet", "&aYou set the spawn of house %house%.");
		messages.addDefault("Messages.house.created", "&aYou created house %house%. Your location is now the spawn location of the house. Use '/gtd house setspawn %house%' to change the spawn.");
		
		messages.addDefault("Messages.dealer.created", "&aDealer '%dealer%' created at your current location.\nSneak and right click the dealer to set the trades.");
		
		messages.addDefault("Messages.drug.tripedOut", "&aYou triped out (%drug%).");
		
		messages.addDefault("Messages.job.joined", "&aYou joined job %job%.");
		messages.addDefault("Messages.job.joinedOther", "%player% joined job %job%.");
		messages.addDefault("Messages.job.left", "&aYou left job %job%.");
		messages.addDefault("Messages.job.leftOther", "%player% left job %job%.");
		messages.addDefault("Messages.job.notExist", "&cJob %job% does not exist.");
		messages.addDefault("Messages.job.canNotJoinMore", "&cYou can't join more jobs.");
		messages.addDefault("Messages.job.canNotJoinMoreOther", "&c%player% can't join more jobs.");
		messages.addDefault("Messages.job.alreadyJoined", "&cYou already joined the job %job%.");
		messages.addDefault("Messages.job.alreadyJoinedOther", "&c%player% already joined the job %job%.");
		messages.addDefault("Messages.job.notJoined", "&cYou didn't join job %job%.");
		messages.addDefault("Messages.job.notJoinedOther", "&c%player% didn't join job %job%.");
		messages.addDefault("Messages.job.wrongTeam", "&cThe job %job% is not available for team %wrongTeam%%newLine%(Required team is %requiredTeam%)");
		messages.addDefault("Messages.job.info", "&eJob information for job &6%job%&e: %newLine%%information%");
		
		messages.addDefault("Messages.changelog.notEnabled", "&cConnecting with dev.bukkit.org to get the changelog is not enabled.");
		messages.addDefault("Messages.changelog.notLoaded", "&cChange log not loaded. Please wait some seconds and try again.");
		messages.addDefault("Messages.changelog.noVersion", "&c%argument% is not a version. Format: <type>_<version>\nExample: 'beta_1.2.3'");
		
		messages.addDefault("Messages.wand.enabled", "&eWand &aenabled&e.");
		messages.addDefault("Messages.wand.disabled", "&eWand &cdisabled&e.");
		
		messages.addDefault("Messages.stats.notLoaded", "&cStats not loaded. Try again later.");
		
		messages.addDefault("Messages.language.noLanguage", "&c%language% is not a valid language.");
		messages.addDefault("Messages.language.ownLanguageSet", "&aYour language was set to %language%.");
		messages.addDefault("Messages.language.defaultLanguageSet", "&aDefault language was set to %language%.");
		
		messages.addDefault("Messages.reloaded.all", "&aAll files reloaded.");
		messages.addDefault("Messages.reloaded.file", "&a%file% reloaded.");
		messages.addDefault("Messages.reloaded.plugin", "&aPlugin reloaded.");
		
		messages.addDefault("Messages.chat.modeSet", "&aYour chat mode was set to '%chat-mode%'.");
		messages.addDefault("Messages.chat.modeSetOther", "&aChat mode of %player% was set to '%chat-mode%'.");
		messages.addDefault("Messages.chat.modeNotExist", "&cChat mode '%chat-mode%' does not exist.\n&eUse '/gtd chat info' to see all chat modes.");
		
		messages.addDefault("Help.noCommandDescriptionAvailable", "&cNo command description available.");
		
		messages.addDefault("Help.arrest.description", "Arrest a player after handcuffing");
		messages.addDefault("Help.changelog.description", "Shows the change log of the given version");
		messages.addDefault("Help.ban.description", "Manage the ban system");
		messages.addDefault("Help.corrupt.description", "Use this in the jail");
		messages.addDefault("Help.create.description", "Create the arena");
		messages.addDefault("Help.detain.description", "Detain a player after handcuffing");
		messages.addDefault("Help.eco.description", "Manage the economy system");
		messages.addDefault("Help.find.description", "Set your compass target to a player");
		messages.addDefault("Help.gang.description", "Manage the gangs");
		messages.addDefault("Help.gang.accept", "Accept to join a gang");
		messages.addDefault("Help.gang.add", "Add a member to a gang");
		messages.addDefault("Help.gang.create", "Create a new gang");
		messages.addDefault("Help.gang.delete", "Delete a gang");
		messages.addDefault("Help.gang.invite", "Invite a player to your gang");
		messages.addDefault("Help.gang.list", "List all gangs");
		messages.addDefault("Help.gang.option", "Set some gang options");
		messages.addDefault("Help.gang.options", "A list of all gang options");
		messages.addDefault("Help.gang.removemember", "Remove a member from a gang");
		messages.addDefault("Help.give.description", "Gives you or an other player a GTD object");
		messages.addDefault("Help.help.description", "Shows this page. Use '/gtd help help' for more information");
		messages.addDefault("Help.house.description", "Manage the houses of the plugin");
		messages.addDefault("Help.info.description", "Shows some information about the plugin");
		messages.addDefault("Help.job.description", "Manage the jobs of the plugin");
		messages.addDefault("Help.join.description", "Join the game...");
		messages.addDefault("Help.language.description", "Select your language");
		messages.addDefault("Help.leave.description", "Leave the game...");
		messages.addDefault("Help.kick.description", "Kicks a player or all players from GTD");
		messages.addDefault("Help.kit.description", "You can give a kit to yourself or an other player");
		messages.addDefault("Help.list.description", "List all players in GTD");
		messages.addDefault("Help.mission.description", "Join or leave a misson");
		messages.addDefault("Help.money.description", "Shows your current GTD money");
		messages.addDefault("Help.objects.description", "A list of all GTD objects...");
		messages.addDefault("Help.pay.description", "Pay some GTD money to an other player");
		messages.addDefault("Help.reload.description", "Reload the confirurations/plugin");
		messages.addDefault("Help.savedata.description", "Store the data into the file data.yml");
		messages.addDefault("Help.setjail.description", "Create a jail with this command");
		messages.addDefault("Help.setsafe.description", "Create a safe to rob");
		messages.addDefault("Help.setspawn.description", "Set some arena spawns");
		messages.addDefault("Help.sign.description", "Set some options to a sign");
		messages.addDefault("Help.unban.description", "Unban a player from GTD");
		messages.addDefault("Help.update.description", "Updates the plugin");
		messages.addDefault("Help.wand.description", "Enable/disable the wand to set the arena");
		
		messages.addDefault("Formats.rightUsage", "&eUse: '%rightUsage%'");
		messages.addDefault("Formats.time", "%days%d%hours%h%minutes%m%seconds%s");
		messages.addDefault("Formats.list", "&6%object%: &e%list%");
		messages.addDefault("Formats.listObject", "%object%, ");
		messages.addDefault("Formats.listObjectAdditionalInformation", "%newLine%&e%object% - %additionalInformation%");
		messages.addDefault("Formats.players", "%player%, ");
		messages.addDefault("Formats.ingameList", " &6%team%s (%teamCount%): &e%players%");
		messages.addDefault("Formats.banReason", "&eReason: &c%reason%");
		messages.addDefault("Formats.defaultBanReason", "&eSick to the rules.");
		messages.addDefault("Formats.gangList", "&eGangs: &6%gangs%");
		messages.addDefault("Formats.gangs", "%gang%, ");
		messages.addDefault("Formats.help", "&e/%command% &f| &e%description%");
		
		messages.addDefault("Formats.header", "&e[] --- &6GrandTheftDiamond &e- &6%title% &e --- []");
		messages.addDefault("Formats.headerWithPages", "&e[] --- &6GrandTheftDiamond &e- &6%title% &e- &6%currentPage%/%pageCount% &e--- []");
		
		messages.addDefault("Formats.chat.modes", "&e- %chat-mode% | Message prefix: '%message-prefix%'");
		
		messages.addDefault("Formats.stats.self", "&e%statsType%: %value%");
		
		messages.options().copyDefaults(true);
		
		LanguageManager.getInstance().saveLanguageFile("english");
		
	}
	
	private void addDefaultConfigPaths() {
		
		FileConfiguration config = FileManager.getInstance().getFileConfiguration(PluginFile.CONFIG);
		
		config.options().header("Configuration file - GrandTheftDiamond - Plugin version: " + BukkitGrandTheftDiamondPlugin.getInstance().getDescription().getVersion() + '\n' + 
				"Gangsters are Civilians with WantedLevel > 0\n." +
				"Configuration for jail, rob etc. can be found in the file 'event-config.yml.'\n\n" + 
				"More information: http://micrjonas1997.bplaced.net/grand-theft-diamond/page.php?page=configuration");
	

		config.addDefault("allowDevMessage", true);
		
		config.addDefault("useUpdater", true);
		config.addDefault("updateCheckIntervalInMinutes", 120);
		
		config.addDefault("useChangeLogFunction", true);
		
		config.addDefault("autoSaveIntervall", 600);
		
		if (LanguageManager.getInstance().getLanguageFile("english").isString("ChatPrefix"))
			config.addDefault("chatPrefix", LanguageManager.getInstance().getLanguageFile("english").getString("ChatPrefix"));
		
		else
			config.addDefault("chatPrefix", "&6[GT-Diamond] &r");
		
		config.addDefault("compassUpdateRate", 10);
		
		config.addDefault("saveInventoryAfterLeavingGame", true);
		
		config.addDefault("wandItem", Material.ARROW.name());

		config.addDefault("maxLinesPerPage", 8);
		
		config.addDefault("ignoreCancelledInteracts", false);
		
		config.addDefault("signs.signTitle", "[&aGTD&r]");
		
		config.addDefault("temporaryPlayerData.clearDataOnDisconnect", true);
		config.addDefault("temporaryPlayerData.timeToClearDataOnDisconnect", 600);
		
		/*config.addDefault("resourcepack.changeResourcepackOnJoin", true);
		config.addDefault("resourcepack.inGame", "");
		config.addDefault("resourcepack.default", "");*/
		
		config.addDefault("teleportToLeavePositionOnJoin", true);
		
		config.addDefault("dynmap.enabled", true);
		config.addDefault("dynmap.markers.hospital.show", true);
		config.addDefault("dynmap.markers.hospital.name", "Hospital");
		config.addDefault("dynmap.markers.house.show", true);
		config.addDefault("dynmap.markers.house.name.owned", "<font color=\"#FF6C47\">House (%name%) of %owner%</font>");
		config.addDefault("dynmap.markers.house.name.buyable", "<font color=\"#A4FF77\">House (%name%/$%price%)</font>");
		config.addDefault("dynmap.markers.jail.show", true);
		config.addDefault("dynmap.markers.jail.name", "Jail (%name%)");
		config.addDefault("dynmap.markers.safe.show", true);
		config.addDefault("dynmap.markers.safe.name", "Safe (%name%)");
		config.addDefault("dynmap.markers.arena.show", true);
		config.addDefault("dynmap.markers.arena.name", "Arena (%name%)");
		config.addDefault("dynmap.markers.arena.border.color", "0 0 255");
		config.addDefault("dynmap.markers.arena.border.opacy", 0.8);
		config.addDefault("dynmap.markers.arena.border.weight", 5);
		config.addDefault("dynmap.markers.arena.fill.color", "0 0 255");
		config.addDefault("dynmap.markers.arena.fill.opacy", 0.2);
		
		config.addDefault("useVaultChat", true);
		config.addDefault("useNametagEdit", true);
		config.addDefault("useVaultEconomy", false);
		
		config.addDefault("economy.startBalance", 1000);
		config.addDefault("economy.minBalance", -5000);
		config.addDefault("economy.maxBalance", 1000000);
		
		config.addDefault("economy.useGrandTheftDiamondEconomy", true);
		
		if (config.getBoolean("economy.useGrandTheftDiamondEconomy"))
			config.addDefault("economy.percentOfDefaultBalance.GTD", 100);
		
		else {
			
			config.set("economy.percentOfDefaultBalance.GTD", 0);
			
			if (config.getBoolean("useVaultEconomy"))
				config.set("economy.percentOfDefaultBalance.Vault", 100);
			
		}
		
		config.addDefault("economy.percentOfDefaultBalance.Vault", 20);
		
		config.addDefault("economy.resetToStartBalance", true);
		
		config.addDefault("economy.currency.symbol", "$");
		
		config.addDefault("signs.signTitle", "[&aGTD&r]");
		
		config.addDefault("signs.updateCooldownSigns", true);
		
		config.addDefault("signs.shop.useGTDEconomy", null);
		config.addDefault("signs.shop.useVaultEconomy", false);
		
		config.addDefault("signs.item.cooldown.active", "&cCooldown");
		config.addDefault("signs.item.cooldown.inactive", "&aUsable");
		
		config.addDefault("signs.house.teleportToHouseIfOwn", true);
		config.addDefault("signs.house.free", "&aFor free");
		
		config.addDefault("banSystem.sendDefaultBanMessage", true);
		config.addDefault("banSystem.copAutoban.use", true);
		config.addDefault("banSystem.copAutoban.countCivilianKills", true);
		config.addDefault("banSystem.copAutoban.countCopKills", true);
		config.addDefault("banSystem.copAutoban.maxKilledCopsAndCivilians", 10);
		config.addDefault("banSystem.copAutoban.banAs", "cop");
		config.addDefault("banSystem.copAutoban.time", -1);
		config.addDefault("banSystem.copAutoban.reason", "You killed to much civilians and cops!");
		
		config.addDefault("disableTeleport.fromInsideToOutsideArena", true);
		config.addDefault("disableTeleport.fromInsideToInsideArena", false);
		
		config.addDefault("blockIngameCommands", true);
		
		List<String> allowedCommands = Arrays.asList("/gtd", "/gta", "/grandtheftdiamond", "/grandtheftauto", "/time", "/who");
		config.addDefault("commandWhitelist", allowedCommands);
		
		config.addDefault("disableAutoheal", true);
		config.addDefault("disableHunger", true);
		
		config.addDefault("disableMobspawningInArena", false);
		
		config.addDefault("joinTeleportDelay", 0);
		
		if (!config.contains("drugs")) {
			config.addDefault("drugs.beer.item.item", Material.POTION.name());
			config.addDefault("drugs.beer.item.name", "Beer");
			config.addDefault("drugs.beer.item.lore", Arrays.asList("Good german beer"));
			config.addDefault("drugs.beer.effects", Arrays.asList(PotionEffects.toMap(new TimedPotionEffect(PotionEffectType.CONFUSION, 40 * 20, 0, 4, 8))));
			
			config.addDefault("drugs.chrystal_meth.item.item", Material.NETHER_STAR.name());
			config.addDefault("drugs.chrystal_meth.item.name", "Chrystal Meth");
			config.addDefault("drugs.chrystal_meth.effects", Arrays.asList(PotionEffects.toMap(new TimedPotionEffect(PotionEffectType.CONFUSION, 50 * 20, 0, 10, 20)), PotionEffects.toMap(new TimedPotionEffect(PotionEffectType.SPEED, 40 * 20, 0, 5, 10))));
			
			config.addDefault("drugs.weed.item.item", Material.CLAY_BRICK.name());
			config.addDefault("drugs.weed.item.name", "Weed");
			config.addDefault("drugs.weed.effects", Arrays.asList(PotionEffects.toMap(new TimedPotionEffect(PotionEffectType.JUMP, 50 * 20, 0, 10, 20))));
		}
			
		config.addDefault("objects.flamethrower.name", "Flamethrower");
		config.addDefault("objects.flamethrower.item", Material.STONE_AXE.name());
		config.addDefault("objects.flamethrower.burnTime", 4.0);
		config.addDefault("objects.flamethrower.useFuel", true);
		config.addDefault("objects.flamethrower.effects", Arrays.asList(Effect.MOBSPAWNER_FLAMES.name(), Effect.MOBSPAWNER_FLAMES.name()));
		config.addDefault("objects.flamethrower.sounds", Arrays.asList(Sound.ZOMBIE_HURT.name()));
		config.addDefault("objects.flamethrower.disable", false);
		
		config.addDefault("objects.grenade.name", "Grenade");
		config.addDefault("objects.grenade.explosionRadius", 2.0);
		config.addDefault("objects.grenade.breakBlocks", false);
		config.addDefault("objects.grenade.setFire", false);
		config.addDefault("objects.grenade.disable", false);
		
		config.addDefault("objects.handcuffs.name", "Handcuffs");
		config.addDefault("objects.handcuffs.item", Material.STRING.name());
		config.addDefault("objects.handcuffs.requiredClickDuration", 0);
		config.addDefault("objects.handcuffs.handcuffedTime", 20);
		config.addDefault("objects.handcuffs.disableAllInteractsWhileHandcuffed", true);
		config.addDefault("objects.handcuffs.effects.1.type", "SLOW");
		config.addDefault("objects.handcuffs.effects.1.duration", 20);
		config.addDefault("objects.handcuffs.effects.1.amplifier", 2);
		config.addDefault("objects.handcuffs.disable", false);
		
		config.addDefault("objects.jetpack.name", "Jetpack");
		config.addDefault("objects.jetpack.useFuel", true);
		config.addDefault("objects.jetpack.power", -0.5);
		config.addDefault("objects.jetpack.maxFlightHeight", 120);
		config.addDefault("objects.jetpack.disableFallDamage", true);
		config.addDefault("objects.jetpack.jetpackControl.name", "Jetpack Control");
		config.addDefault("objects.jetpack.jetpackControl.item", Material.BLAZE_ROD.name());
		config.addDefault("objects.jetpack.disable", false);
		
		config.addDefault("objects.knife.name", "Knife");
		config.addDefault("objects.knife.item", Material.IRON_SWORD.name());
		config.addDefault("objects.knife.damage", 4);
		config.addDefault("objects.knife.useCooldown", true);
		config.addDefault("objects.knife.cooldownInTicks", 10);
		config.addDefault("objects.knife.disable", false);
		
		config.addDefault("objects.taser.name", "Taser");
		config.addDefault("objects.taser.item", Material.SHEARS.name());
		config.addDefault("objects.taser.civilianCanUseTaser", false);
		config.addDefault("objects.taser.copsCanGetTazed", false);
		config.addDefault("objects.taser.damage", 2);
		config.addDefault("objects.taser.effects.1.type", "SLOW");
		config.addDefault("objects.taser.effects.1.duration", 5);
		config.addDefault("objects.taser.effects.1.amplifier", 5);
		config.addDefault("objects.taser.effects.2.type", "CONFUSION");
		config.addDefault("objects.taser.effects.2.duration", 5);
		config.addDefault("objects.taser.effects.2.amplifier", 1);
		config.addDefault("objects.taser.effects.3.type", "BLINDNESS");
		config.addDefault("objects.taser.effects.3.duration", 2);
		config.addDefault("objects.taser.effects.3.amplifier", 1);
		config.addDefault("objects.taser.effects.4.type", "WEAKNESS");
		config.addDefault("objects.taser.effects.4.duration", 5);
		config.addDefault("objects.taser.effects.4.amplifier", 3);
		config.addDefault("objects.taser.disable", false);
		
		boolean addFirearms = !config.contains("objects.firearms");
		config.addDefault("objects.firearms.allowUseGunMessage", true);
		if (addFirearms) {
			config.addDefault("objects.firearms.gun.name", "Gun");
			config.addDefault("objects.firearms.gun.item", Material.STONE_HOE.name());
			config.addDefault("objects.firearms.gun.shotsPerSecond", 0.8);
			config.addDefault("objects.firearms.gun.projectilesPerShot", 1);
			config.addDefault("objects.firearms.gun.damage", 3);
			config.addDefault("objects.firearms.gun.power", "80%");
			config.addDefault("objects.firearms.gun.accuracy", "80%");
			config.addDefault("objects.firearms.gun.projectile", "SNOWBALL");
			config.addDefault("objects.firearms.gun.sounds.onShot", Arrays.asList("EXPLODE", "CLICK delayInTicks:5", "CLICK delayInTicks:7"));
			config.addDefault("objects.firearms.gun.sounds.onHit", Arrays.asList("ZOMBIE_HURT"));
			config.addDefault("objects.firearms.gun.zoom.use", false);
			config.addDefault("objects.firearms.gun.zoom.effects", Arrays.asList("SLOW 3"));
			config.addDefault("objects.firearms.gun.knockback", 0.1);
			config.addDefault("objects.firearms.gun.ammo.use", true);
			config.addDefault("objects.firearms.gun.ammo.item", 332);
			config.addDefault("objects.firearms.gun.ammo.name", "Ammo (Gun)");
			config.addDefault("objects.firearms.gun.disable", false);
			
			config.addDefault("objects.firearms.machinegun.name", "Machine gun");
			config.addDefault("objects.firearms.machinegun.item", Material.IRON_HOE.name());
			config.addDefault("objects.firearms.machinegun.shotsPerSecond", 5.0);
			config.addDefault("objects.firearms.machinegun.projectilesPerShot", 1);
			config.addDefault("objects.firearms.machinegun.damage", 4);
			config.addDefault("objects.firearms.machinegun.power", "80%");
			config.addDefault("objects.firearms.machinegun.accuracy", "80%");
			config.addDefault("objects.firearms.machinegun.projectile", "SNOWBALL");
			config.addDefault("objects.firearms.machinegun.sounds.onShot", Arrays.asList("EXPLODE"));
			config.addDefault("objects.firearms.machinegun.sounds.onHit", Arrays.asList("ZOMBIE_HURT"));
			config.addDefault("objects.firearms.machinegun.zoom.use", true);
			config.addDefault("objects.firearms.machinegun.zoom.effects", Arrays.asList("SLOW 3"));
			config.addDefault("objects.firearms.machinegun.knockback", 0.2);
			config.addDefault("objects.firearms.machinegun.ammo.use", true);
			config.addDefault("objects.firearms.machinegun.ammo.item", 332);
			config.addDefault("objects.firearms.machinegun.ammo.name", "Ammo (Machine gun)");
			config.addDefault("objects.firearms.machinegun.disable", false);
			
			config.addDefault("objects.firearms.uzi.name", "Uzi");
			config.addDefault("objects.firearms.uzi.item", Material.GOLD_HOE.name());
			config.addDefault("objects.firearms.uzi.shotsPerSecond", 2.0);
			config.addDefault("objects.firearms.uzi.projectilesPerShot", 2);
			config.addDefault("objects.firearms.uzi.damage", 4);
			config.addDefault("objects.firearms.uzi.power", "80%");
			config.addDefault("objects.firearms.uzi.accuracy", "60%");
			config.addDefault("objects.firearms.uzi.projectile", "SNOWBALL");
			config.addDefault("objects.firearms.uzi.sounds.onShot", Arrays.asList("EXPLODE"));
			config.addDefault("objects.firearms.uzi.sounds.onHit", Arrays.asList("ZOMBIE_HURT"));
			config.addDefault("objects.firearms.uzi.zoom.use", false);
			config.addDefault("objects.firearms.uzi.zoom.effects", Arrays.asList("SLOW 3"));
			config.addDefault("objects.firearms.uzi.knockback", 0.5);
			config.addDefault("objects.firearms.uzi.ammo.use", true);
			config.addDefault("objects.firearms.uzi.ammo.item", 332);
			config.addDefault("objects.firearms.uzi.ammo.name", "Ammo (Uzi)");
			config.addDefault("objects.firearms.uzi.disable", false);
			
			config.addDefault("objects.firearms.sniper.name", "Sniper");
			config.addDefault("objects.firearms.sniper.item", Material.DIAMOND_HOE.name());
			config.addDefault("objects.firearms.sniper.shotsPerSecond", 0.5);
			config.addDefault("objects.firearms.sniper.projectilesPerShot", 1);
			config.addDefault("objects.firearms.sniper.damage", 6);
			config.addDefault("objects.firearms.sniper.power", "100%");
			config.addDefault("objects.firearms.sniper.accuracy", "100%");
			config.addDefault("objects.firearms.sniper.projectile", "SNOWBALL");
			config.addDefault("objects.firearms.sniper.sounds.onShot", Arrays.asList("EXPLODE"));
			config.addDefault("objects.firearms.sniper.sounds.onHit", Arrays.asList("ZOMBIE_HURT"));
			config.addDefault("objects.firearms.sniper.zoom.use", true);
			config.addDefault("objects.firearms.sniper.zoom.effects", Arrays.asList("SLOW 20", "NIGHT_VISION 1"));
			config.addDefault("objects.firearms.sniper.knockback", 0.2);
			config.addDefault("objects.firearms.sniper.ammo.use", true);
			config.addDefault("objects.firearms.sniper.ammo.item", 332);
			config.addDefault("objects.firearms.sniper.ammo.name", "Ammo (Sniper)");
			config.addDefault("objects.firearms.sniper.disable", false);
		}
		
		boolean addCars = !config.contains("objects.cars");
		config.addDefault("objects.cars.disableInventoryChange", true);
		config.addDefault("objects.cars.playersCanDamageNotOwnedCar", true);
		config.addDefault("objects.cars.playersCanDamageOwnedCar", false);
		config.addDefault("objects.cars.playersCanDamageOwnCar", true);
		config.addDefault("objects.cars.disableAllCarDamage", false);
		
		config.addDefault("objects.cars.autoCarSpawnDelayUntilNextSpawning", 120);
		config.addDefault("objects.cars.minRadiusToNextFreeHorseForSpawn", 120);
		config.addDefault("objects.cars.maxRadiusToNextPlayerForSpawn", 80);
		
		if (addCars) {
			config.addDefault("objects.cars.policecar.name", "Police car");
			config.addDefault("objects.cars.policecar.team", "COP");
			config.addDefault("objects.cars.policecar.horseColor", Horse.Color.WHITE.name());
			config.addDefault("objects.cars.policecar.horseStyle", Style.BLACK_DOTS.name());
			config.addDefault("objects.cars.policecar.variant", Variant.HORSE.name());
			config.addDefault("objects.cars.policecar.maxHealth", 20.0);
			config.addDefault("objects.cars.policecar.jumpStrength", 0.2);
			config.addDefault("objects.cars.policecar.speed", -1);
			
			config.addDefault("objects.cars.civilcar.name", "Civil Car");
			config.addDefault("objects.cars.civilcar.team", "CIVILIAN");
			config.addDefault("objects.cars.civilcar.horseColor", Horse.Color.BROWN.name());
			config.addDefault("objects.cars.civilcar.horseStyle", Style.WHITEFIELD.name());
			config.addDefault("objects.cars.civilcar.variant", Variant.HORSE.name());
			config.addDefault("objects.cars.civilcar.maxHealth", 20.0);
			config.addDefault("objects.cars.civilcar.jumpStrength", 0.2);
			config.addDefault("objects.cars.civilcar.speed", -1);
			
			config.addDefault("objects.cars.sportscar.name", "Sports Car");
			config.addDefault("objects.cars.sportscar.team", Team.CIVILIAN.name());
			config.addDefault("objects.cars.sportscar.horseColor", Horse.Color.WHITE.name());
			config.addDefault("objects.cars.sportscar.horseStyle", Style.WHITEFIELD.name());
			config.addDefault("objects.cars.sportscar.variant", Variant.HORSE.name());
			config.addDefault("objects.cars.sportscar.maxHealth", 40.0);
			config.addDefault("objects.cars.sportscar.jumpStrength", 0.6);
			config.addDefault("objects.cars.sportscar.speed", 1);
		}
		
		config.addDefault("chat.global.messagePrefix", "!");
		config.addDefault("chat.global.chatPrefix", "&7[Global Chat]&r ");
		
		config.addDefault("chat.local.messagePrefix", "*");
		config.addDefault("chat.local.chatPrefix", "&7[Local Chat]&r ");
		config.addDefault("chat.local.defaultForIngamePlayers", false);
		config.addDefault("chat.local.ifDefault.sendLocalChatPrefix", true);
		config.addDefault("chat.local.ifDefault.sendGlobalChatPrefix", false);
		
		config.addDefault("chat.team.messagePrefix", "-");
		config.addDefault("chat.team.messageFormat", "&7[GroupChat] [&6%group%&7] %player%: &f%message%");
		
		config.addDefault("chat.serverChat.prefix.civilian", "&7[&aCiv&7]&r ");
		config.addDefault("chat.serverChat.prefix.gangster", "&7[&cCiv&7]&r ");
		config.addDefault("chat.serverChat.prefix.cop", "&7[&9Cop&7]&r ");
		
		config.addDefault("chat.serverChat.suffix.civilian", "");
		config.addDefault("chat.serverChat.suffix.gangster", " &c[%wantedLevel%]&r");
		config.addDefault("chat.serverChat.suffix.cop", "");	
		
		config.addDefault("scoreboard.nametag.use", true);

		config.addDefault("scoreboard.stats.use", true);
		config.addDefault("scoreboard.stats.title", "Stats");
		
		for (StatsType type : StatsType.values()) {
			config.addDefault("scoreboard.stats.shownStats." + type.name(), 
					Messenger.getInstance().getWordStartUpperCase(type.name().replace('_', ' ')));	
		}
		
		config.addDefault("scoreboard.nametag.prefix.civilian", "&7[Civ]&r ");
		config.addDefault("scoreboard.nametag.prefix.gangster", "&7[Civ]&r ");
		config.addDefault("scoreboard.nametag.prefix.cop", "&7[Cop]&r ");
		
		config.addDefault("scoreboard.nametag.suffix.civilian", "");
		config.addDefault("scoreboard.nametag.suffix.gangster", " &c[%wantedLevel%]");
		config.addDefault("scoreboard.nametag.suffix.cop", "");
		
		config.addDefault("gangs.memberCount.requiredExpPerMember", 1000);
		config.addDefault("gangs.mustBeIngameToManageGangs", true);
		config.addDefault("gangs.maxMembersPerGang", 5);
		config.addDefault("gangs.maxOwnedGangs", 1);
		config.addDefault("gangs.maxMemberships", 2);
		config.addDefault("gangs.defaultFriendlyFire", false);
		config.addDefault("gangs.defaultOwnerMustInvite", true);
		
		
		addKits(config);
		
		List<String> copKits;
		List<String> civilianKits;
		
		if (config.isConfigurationSection("startkits")) {
			
			copKits = new ArrayList<>();
			civilianKits = new ArrayList<>();
			
			for (String kit : config.getStringList("startkits.cop"))
				copKits.add(kit.split(" ")[0]);
			
			for (String kit : config.getStringList("startkits.civilian"))
				civilianKits.add(kit.split(" ")[0]);
			
			config.set("startkits", null);
			
		}
		
		else {
			
			copKits = Arrays.asList("cop-default");
			civilianKits = Arrays.asList("civilian-default");
			
		}
		
		config.addDefault("startKits.cop", copKits);
		config.addDefault("startKits.civilian", civilianKits);
		
		config.options().copyDefaults(true);
		FileManager.getInstance().saveFileConfiguration(PluginFile.CONFIG);
		
	}
	
	
	private void addKits(FileConfiguration config) {
		
		if (config.isConfigurationSection("kits")) {
			
			for (String kit : config.getConfigurationSection("kits").getKeys(false)) {
				
				String listPath = "kits." + kit + ".items";
				
				if (config.isList(listPath)) {
					
					if (config.getList(listPath).size() > 0 && config.getList(listPath).get(0) instanceof Map)
						continue;
					
					if (config.getList("kits." + kit + ".items").size() > 0 && config.getList("kits." + kit + ".items").get(1) instanceof Map)
						return;
					
					List<Map<String, Object>> items = new ArrayList<>();
					
					for (String item : config.getStringList("kits." + kit + ".items")) {
						
						String[] split = item.split(" ");
						int amount = 0;
						
						if (split.length > 1) {
								
							try {
								amount = Integer.parseInt(split[1]);
							}
								
							catch (NumberFormatException ex) { }
								
						}
						
						Map<String, Object> itemMap = new LinkedHashMap<>();
						
						itemMap.put("item", "grandtheftdiamond:" + split[0]);
						
						if (amount > 1)
							itemMap.put("amount", amount);
						
						items.add(itemMap);
						
					}
					
					config.set("kits." + kit + ".items", items);
					
				}
				
			}
			
		}
		
		else {
		
		//CIVILIAN
			
			Map<String, Object> item = new LinkedHashMap<>();
			Map<String, Object> item2 = new LinkedHashMap<>();
			Map<String, Object> item3 = new LinkedHashMap<>();
			
			item.put("item", "SUGAR");
			item.put("amount", 2);
			item.put("name", "Useless Sugar");
			item.put("lore", Arrays.asList("&7I'm Just Here To Show How", "&7To Configure Custom Kits/Items"));
			item.put("enchantments", Arrays.asList("CUSTOM_ENCHANTMENT 1"));
			
			item2.put("item", "grandtheftdiamond:gun");
			
			item3.put("item", "grandtheftdiamond:ammo:gun");
			item3.put("amount", 64);
			
			config.set("kits.civilian-default.items", Arrays.asList(item, item2, item3));
			
		//COP
			
			Map<String, Object> item4 = new LinkedHashMap<>();
			Map<String, Object> item5 = new LinkedHashMap<>();
			Map<String, Object> item6 = new LinkedHashMap<>();
			Map<String, Object> item7 = new LinkedHashMap<>();
			
			item4.put("item", "grandtheftdiamond:handcuffs");
			item5.put("item", "grandtheftdiamond:taser");
			item6.put("item", "grandtheftdiamond:machinegun");
			
			item7.put("item", "grandtheftdiamond:ammo:machinegun");
			item7.put("amount", 64);
			
			config.set("kits.cop-default.items", Arrays.asList(item4, item5, item6, item7));
			
		}
		
	}
		
		
	private void addDefaultResultConfigPaths() {
		
		FileConfiguration eventConfig = FileManager.getInstance().getFileConfiguration(PluginFile.EVENT_CONFIG);
		
		if (eventConfig.isConfigurationSection("Config")) {
		
			for (String path : eventConfig.getConfigurationSection("Config").getKeys(false)) {
				
				eventConfig.addDefault(path, eventConfig.get("Config." + path));
				
			}
			
			eventConfig.set("Config", null);
			
		}
		
		eventConfig.addDefault("kill.civilianKilled.civilian.wantedLevel", 1);
		eventConfig.addDefault("kill.civilianKilled.civilian.experience", 0);
		eventConfig.addDefault("kill.civilianKilled.civilian.money", 0);
		eventConfig.addDefault("kill.civilianKilled.gangster.wantedLevel", 1);
		eventConfig.addDefault("kill.civilianKilled.gangster.experience", 20);
		eventConfig.addDefault("kill.civilianKilled.gangster.money", 50);
		eventConfig.addDefault("kill.civilianKilled.cop.wantedLevel", 2);
		eventConfig.addDefault("kill.civilianKilled.cop.experience", 0);
		eventConfig.addDefault("kill.civilianKilled.cop.money", -50);
		
		eventConfig.addDefault("kill.copKilled.civilian.wantedLevel", 1);
		eventConfig.addDefault("kill.copKilled.civilian.experience", 0);
		eventConfig.addDefault("kill.copKilled.civilian.money", -200);
		eventConfig.addDefault("kill.copKilled.gangster.wantedLevel", 1);
		eventConfig.addDefault("kill.copKilled.gangster.experience", 20);
		eventConfig.addDefault("kill.copKilled.gangster.money", 50);
		eventConfig.addDefault("kill.copKilled.cop.wantedLevel", 2);
		eventConfig.addDefault("kill.copKilled.cop.experience", 0);
		eventConfig.addDefault("kill.copKilled.cop.money", -50);
		
		eventConfig.addDefault("kill.copKilled.jailCopAfterKillingCop", true);
		eventConfig.addDefault("kill.copKilled.jailCopAfterKillingNonGangster", true);
		eventConfig.addDefault("kill.copKilled.jailTime", 30);
		eventConfig.addDefault("kill.copKilled.money", -200);
		
		if (eventConfig.isConfigurationSection("rob")) {
			
			eventConfig.set("robbing", eventConfig.get("rob"));
			eventConfig.set("rob", null);
			
		}
		
		eventConfig.addDefault("robbing.safe.enabled", true);
		eventConfig.addDefault("robbing.safe.block", Material.IRON_BLOCK.name());
		eventConfig.addDefault("robbing.safe.robTime", 10);
		eventConfig.addDefault("robbing.safe.timeToNextRob.min", 180);
		eventConfig.addDefault("robbing.safe.timeToNextRob.max", 240);
		eventConfig.addDefault("robbing.safe.wantedLevel", 2);
		eventConfig.addDefault("robbing.safe.experience", 20);
		eventConfig.addDefault("robbing.safe.money.min", 200);
		eventConfig.addDefault("robbing.safe.money.max", 300);
		
		eventConfig.addDefault("jail.jailTime", "%wantedLevel% * 20 + 10");
		eventConfig.addDefault("jail.moneyBalance", "%wantedLevel% * 50");
		
		eventConfig.addDefault("jail.arrest.maxDistanceToGangster", 5);
		
		eventConfig.addDefault("jail.arrest.experienceForCop", 30);
		
		eventConfig.addDefault("jail.arrest.moneyForCop", "%wantedLevel% * 10");
		
		eventConfig.addDefault("jail.detain.maxDistanceToGangster", 5);
		
		eventConfig.addDefault("jail.detain.experienceForCop", 50);
		
		eventConfig.addDefault("jail.detain.moneyForCop", "%wantedLevel% * 15");
		
		eventConfig.addDefault("damage.disableCopDamageByCop", true);
		eventConfig.addDefault("damage.disableNotGangsterDamageByCop", false);
		
		eventConfig.addDefault("death.killed.money", -50);
		eventConfig.addDefault("death.killed.experience", 0);
		eventConfig.addDefault("death.otherReason.money", -100);
		eventConfig.addDefault("death.otherReason.experience", -20);
		
		eventConfig.addDefault("corrupt.useMinAmountPerWantedLevel", true);
		eventConfig.addDefault("corrupt.minAmountPerWantedLevel", 0);
		eventConfig.addDefault("corrupt.minAcceptsPerPlayerBeforeDisable", 5);
		eventConfig.addDefault("corrupt.maxSamePlayerPerCentPlayers", 30);
		
		eventConfig.options().copyDefaults(true);
		
		FileManager.getInstance().saveFileConfiguration(PluginFile.EVENT_CONFIG);
		
	}
	
	
	private void addOnlyGTDModeConfigDefaults() {
		
		FileConfiguration onlyGTDModeConfig = FileManager.getInstance().getFileConfiguration(PluginFile.ONLY_GTD_MODE_CONFIG);
		
		onlyGTDModeConfig.options().header("You can use the following in ALL (Without %player%, %balance% and %team% in motd.multiplayerMenu) messages of this file to replace some information:" + "\n" +
											"%player% -> The player which gets the message" + "\n" +
											"%ingameList% -> A list of all players which are ingame" + "\n" + 
											"%<team>List% -> A list of all players in this team" + "\n" +
											"%ingameCount% -> The count of all players which are ingame" + "\n" + 
											"%<team>Count% -> The count of all players in this team" + "\n" +
											"%team% -> The team of a player" + "\n" +
											"%date% -> The current date in the server's time zone" + "\n" +
											"%balance% -> Balance of the player" + "\n"
											);
		
		onlyGTDModeConfig.addDefault("use", false);

		onlyGTDModeConfig.addDefault("playersCanLeaveGame", false);
		
		onlyGTDModeConfig.addDefault("joinMessage.disableMessage", false);
		onlyGTDModeConfig.addDefault("joinMessage.change", true);
		onlyGTDModeConfig.addDefault("joinMessage.message", "&e%player% joined the server and is in team %team%.");
		onlyGTDModeConfig.addDefault("joinMessage.sendMessageToJoinedPlayer", false);
		
		onlyGTDModeConfig.addDefault("quitMessage.disableMessage", false);
		onlyGTDModeConfig.addDefault("quitMessage.change", false);
		onlyGTDModeConfig.addDefault("quitMessage.message", "&e%player% left the server.");
		
		onlyGTDModeConfig.addDefault("commands.enableCommandsWithoutPrefix", true);
		onlyGTDModeConfig.addDefault("commands.commandsWithoutPrefixBlacklist", Arrays.asList("reload", "help", "?"));
		
		onlyGTDModeConfig.addDefault("motd.multiplayerMenu.change", true);
		onlyGTDModeConfig.addDefault("motd.multiplayerMenu.text", Arrays.asList("&6&lGrandTheftDiamond &7- &6Plugin by MiCrJonas1997! %newLine% &aCivilians: %civilianCount%| Gangsters: %gangsterCount%| Cops: %copCount%", 
																				"&6Your admin didn't change the MOTD in %newLine% .../GrandTheftDiamond/onlyGTDModeConfig.yml!"));
		
		onlyGTDModeConfig.addDefault("motd.chat.use", true);
		onlyGTDModeConfig.addDefault("motd.chat.delayInTicks", 5);
		onlyGTDModeConfig.addDefault("motd.chat.text", Arrays.asList("", 
																	"&aWelcome to GrandTheftDiamond by MiCrJonas1997!", 
																	"&aYou are in team %team%!", 
																	"&aPlayers ingame: %ingameCount% (Civilians: %civilianCount%, Gangsters: %gangsterCount%, Cops: %copCount%)", 
																	"&aIt's %date%",
																	""));
		
		onlyGTDModeConfig.addDefault("broadcaster.use", true);
		onlyGTDModeConfig.addDefault("broadcaster.prefix", "&6[GTD-Broadcaster] &e");
		onlyGTDModeConfig.addDefault("broadcaster.useTeamBroadcast", true);
		onlyGTDModeConfig.addDefault("broadcaster.useGroupBroadcast", true);
		
		onlyGTDModeConfig.addDefault("broadcaster.teams.civilian.startAfterSeconds", 100);
		onlyGTDModeConfig.addDefault("broadcaster.teams.civilian.delayInSeconds", 600);
		onlyGTDModeConfig.addDefault("broadcaster.teams.civilian.random", false);
		onlyGTDModeConfig.addDefault("broadcaster.teams.civilian.messages", Arrays.asList("You are a civilian! So you can rob safes to get money!"));
		
		onlyGTDModeConfig.addDefault("broadcaster.teams.cop.startAfterSeconds", 200);
		onlyGTDModeConfig.addDefault("broadcaster.teams.cop.delayInSeconds", 600);
		onlyGTDModeConfig.addDefault("broadcaster.teams.cop.random", false);
		onlyGTDModeConfig.addDefault("broadcaster.teams.cop.messages", Arrays.asList("The following players are gangsters: %gangsterList%! Jail them to get money!"));
		
		onlyGTDModeConfig.addDefault("broadcaster.groups.default.startAfterSeconds", 300);
		onlyGTDModeConfig.addDefault("broadcaster.groups.default.delayInSeconds", 600);
		onlyGTDModeConfig.addDefault("broadcaster.groups.default.random", false);
		onlyGTDModeConfig.addDefault("broadcaster.groups.default.messages", Arrays.asList("We hope you have fun with GrandTheftDiamond", 
																						"Please report bugs to: &n&9http://dev.bukkit.org/bukkit-plugins/grand-theft-diamond/", 
																						"I'm a message!",
																						"We love you, %player%!",
																						"&aPlayers ingame: %ingameCount% (Civilians: %civilianCount%, Gangsters: %gangsterCount%, Cops: %copCount%)"));
		
		onlyGTDModeConfig.addDefault("broadcaster.groups.vip.startAfterSeconds", 400);
		onlyGTDModeConfig.addDefault("broadcaster.groups.vip.delayInSeconds", 600);
		onlyGTDModeConfig.addDefault("broadcaster.groups.vip.random", false);
		onlyGTDModeConfig.addDefault("broadcaster.groups.vip.messages", Arrays.asList("Uhh, you have VIP permissions!"));
		
		onlyGTDModeConfig.options().copyDefaults(true);
		
		FileManager.getInstance().saveFileConfiguration(PluginFile.ONLY_GTD_MODE_CONFIG);
		
	}
	
	
	private void addRankDefaults() {
		
		FileConfiguration ranks = FileManager.getInstance().getFileConfiguration(PluginFile.RANKS);
		
		ranks.options().header("NOT USEABLE YET!"/*"You can add custom ranks like SWAT or Robber (no custom teams!)"*/);
		
		ranks.addDefault("permissionsFromLowerRanks", true);
		
		if (ranks.isConfigurationSection("teams"))
			return;
		
	//COP
		ranks.addDefault("ranks.cop.Officer.exp", 0);
		ranks.addDefault("ranks.cop.Officer.color", "8");
		ranks.addDefault("ranks.cop.Officer.permissions", new ArrayList<>());
		
		ranks.addDefault("ranks.cop.SWAT.exp", 1000);
		ranks.addDefault("ranks.cop.SWAT.color", "c");
		ranks.addDefault("ranks.cop.SWAT.permissions", Arrays.asList("gta.use.item.sniper"));
		
	//CIVILIAN
		ranks.addDefault("ranks.civilian.Gangster.exp", 0);
		ranks.addDefault("ranks.civilian.Gangster.color", "8");
		ranks.addDefault("ranks.civilian.Gangster.permissions", new ArrayList<>());
		
		ranks.addDefault("ranks.civilian.Robber.exp", 1000);
		ranks.addDefault("ranks.civilian.Robber.color", "c");
		ranks.addDefault("ranks.civilian.Robber.permissions", Arrays.asList("gta.use.item.flamethrower"));
		
		ranks.options().copyDefaults(true);
		
		FileManager.getInstance().saveFileConfiguration(PluginFile.RANKS);
		
	}
	
}

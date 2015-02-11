package me.micrjonas.grandtheftdiamond.command;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import me.micrjonas.grandtheftdiamond.GrandTheftDiamond;
import me.micrjonas.grandtheftdiamond.data.FileManager;
import me.micrjonas.grandtheftdiamond.data.PluginFile;
import me.micrjonas.grandtheftdiamond.data.TemporaryPluginData;
import me.micrjonas.grandtheftdiamond.gang.Gang;
import me.micrjonas.grandtheftdiamond.gang.GangManager;
import me.micrjonas.grandtheftdiamond.gang.GangOption;
import me.micrjonas.grandtheftdiamond.messenger.Messenger;
import me.micrjonas.grandtheftdiamond.messenger.NoPermissionType;
import me.micrjonas.util.Nameables;

import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

/**
 * Manages command '/gtd gang *' and its {@link TabCompleter}
 */
@SuppressWarnings("deprecation")
public class CommandGang implements CommandExecutor, TabCompleter {

	private final static List<String> VALID_ARGUMENTS;
	private final static Set<String> trues = new HashSet<>();
	private final static Set<String> falses = new HashSet<>();
	
	static {
		VALID_ARGUMENTS = Arrays.asList(
				"accept",
				"addmember",
				"create",
				"delete",
				"help",
				"info",
				"invite",
				"list",
				"option",
				"options",
				"removemember");
		
		trues.add("allow");
		trues.add("true");
		trues.add("yes");
		
		falses.add("deny");
		falses.add("disallow");
		falses.add("false");
		falses.add("no");
	}
	
	@SuppressWarnings("unused")
	private boolean trueOrFalse(String value) throws IllegalArgumentException {
		if (trues.contains(value)) {
			return true;
		}
		if (falses.contains(value)) {
			return false;
		}
		throw new IllegalArgumentException();
	}
	
	// Returns null if the player cannot join the gang or it does not exist. If toJoin is null, sender will be toJoin
	@SuppressWarnings("unused")
	private Gang joinGang(CommandSender sender, Player toJoin, String gangName) {
		Gang gang = checkGang(sender, gangName);
		if (gang != null) {
			if (toJoin == null) {
				toJoin = (Player) sender;
			}
			if (GangManager.getInstance().getPlayerGang(toJoin) == null) {
				if (gang.canHaveMoreMembers()) {
					gang.addMember(toJoin);
					Messenger.getInstance().sendPluginMessage(toJoin, "gang.joined", "%gang%", gang.getName());
					if (sender != toJoin) {
						Messenger.getInstance().sendPluginMessage(toJoin, "gang.otherJoined", new Player[]{toJoin}, "%gang%", gang.getName());
					}
				}
				else {
					Messenger.getInstance().sendPluginMessage(sender, "gang.toMuchMembers", new String[]{"%gang",  "%amount%"}, new String[]{gang.getName(), String.valueOf(FileManager.getInstance().getFileConfiguration(PluginFile.CONFIG).getInt("gangs.maxMembersPerGang"))});	
				}
			}
			else {
				if (sender == toJoin) {
					Messenger.getInstance().sendPluginMessage(toJoin, "gang.alreadyInGang", "%gang%", GangManager.getInstance().getPlayerGang(toJoin).getName());
				}
				else {
					Messenger.getInstance().sendPluginMessage(toJoin, "gang.alreadyInGangOther", new Player[]{toJoin}, "%gang%", GangManager.getInstance().getPlayerGang(toJoin).getName());
				}
			}
		}
		return gang;
	}
	
	private Gang checkGang(CommandSender sender, String gangName) {
		Gang gang = GangManager.getInstance().getGang(gangName);
		if (gang == null) {
			Messenger.getInstance().sendPluginMessage(sender, "gang.notExist", "%gang%", gangName);
		}
		return gang;
	}
	
	@Override
	public void onCommand(CommandSender sender, String alias, String[] args, String[] originalArgs) {
		if (args.length >= 2) {
			switch (args[1]) {
				case "accept": {
					if (sender instanceof Player) {
						Player p = (Player) sender;
						if (GrandTheftDiamond.checkPermission(sender, "gang.accept", true, NoPermissionType.COMMAND)) {
							if (args.length >= 3) {
								if (GangManager.getInstance().isGang(args[2]) || (TemporaryPluginData.getInstance().canAcceptGangInvites(p) && TemporaryPluginData.getInstance().getGangInvitesToAccept(p).contains(args[2].toLowerCase()))) {
									if (TemporaryPluginData.getInstance().canAcceptGangInvites(p) && TemporaryPluginData.getInstance().getGangInvitesToAccept(p).contains(args[2].toLowerCase())) {
										if (GangManager.getInstance().isGang(args[2])) {
											if (GangManager.getInstance().getGang(args[2]).canHaveMoreMembers()) {
												if (GangManager.getInstance().getPlayerGang(p) == null) {
													TemporaryPluginData.getInstance().removeGangInviteToAccept(p, args[2]);
													GangManager.getInstance().getGang(args[2]).addMember(p);
													Messenger.getInstance().sendPluginMessage(p, "gang.joined", new String[]{"%gang%"}, new String[]{GangManager.getInstance().getGang(args[2]).getName()});
													for (OfflinePlayer otherPOffline : GangManager.getInstance().getGang(args[2]).getMembers()) {
														if (otherPOffline.isOnline() && otherPOffline != p && otherPOffline != p) {
															Player otherP = (Player) otherPOffline;
															Messenger.getInstance().sendPluginMessage(otherP, "gang.otherJoined", new Player[]{p}, new String[]{"%gang%"}, new String[]{GangManager.getInstance().getGang(args[2]).getName()});
														}
													}
												}
												else {
													Messenger.getInstance().sendPluginMessage(p, "gang.alreadyInGang", "%gang%", GangManager.getInstance().getPlayerGang(p).getName());
												}
											}
											else {
												Messenger.getInstance().sendPluginMessage(p, "gang.toMuchMembers", new String[]{"%gang",  "%amount%"}, new String[]{GangManager.getInstance().getGang(args[2]).getName(), String.valueOf(FileManager.getInstance().getFileConfiguration(PluginFile.CONFIG).getInt("gangs.maxMembersPerGang"))});	
											}
										}
										else {
											TemporaryPluginData.getInstance().removeGangInviteToAccept(p, args[2]);
											Messenger.getInstance().sendPluginMessage(p, "gang.noLongerExist", new String[]{"%gang%"}, new String[]{args[2]});
										}
									}
									else {
										Messenger.getInstance().sendPluginMessage(p, "gang.cannotAcceptInvite", new String[]{"%gang%"}, new String[]{args[2]});
									}
								}
								else {
									Messenger.getInstance().sendPluginMessage(sender, "gang.notExist", new String[]{"%gang%"}, new String[]{args[2]});
								}
							}
							else {
								Messenger.getInstance().sendPluginMessage(sender, "wrongUsage");
								Messenger.getInstance().sendRightUsage(sender, alias, "gang accept <" + Messenger.getInstance().getPluginWord("gang") + ">");
							}
						}
					}
					else {
						Messenger.getInstance().sendPluginMessage(sender, "notAsConsole");
					}
				} break;
				
				case "addmember": {
					Messenger.getInstance().sendPluginMessage(sender, "§cComing soon");
				} break;
				
				case "create": {
					if (sender instanceof Player || args.length >= 4) {
						if (args.length >= 3) {
							if ((GrandTheftDiamond.checkPermission(sender, "gang.create.own") && args.length == 3) ||
									(GrandTheftDiamond.checkPermission(sender, "gang.create.other")) && args.length >= 4) {
								if (!GangManager.getInstance().isGang(args[2])) {
									String newName = originalArgs[2];
									Player newOwner = null;
									if (args.length == 3 && GrandTheftDiamond.checkPermission(sender, "gang.create.own")) {
										newOwner = (Player) sender;
									}
									else if (args.length >= 4 && GrandTheftDiamond.checkPermission(sender, "gang.create.other")) {
										newOwner = Bukkit.getServer().getPlayer(args[3]);
										if (newOwner == null) {
											Messenger.getInstance().sendPluginMessage(sender, "playerNotOnline");
											return;
										}
									}
									else {
										Messenger.getInstance().sendPluginMessage(sender, "noPermissionsCommand", "%permission%", "gang.create." + (args.length == 3 ? "own" : "other"));
										return;
									}
									GangManager.getInstance().createGang(sender, newName, newOwner);
									if (sender == newOwner) {
										Messenger.getInstance().sendPluginMessage(sender, "gang.created", new String[]{"%gang%"}, new String[]{originalArgs[2]});
									}
									else {
										Messenger.getInstance().sendPluginMessage(sender, "gang.createdOwner", new Player[]{newOwner}, new String[]{"%gang%"}, new String[]{originalArgs[2]});
									}
								}
								else {
									Messenger.getInstance().sendPluginMessage(sender, "gang.alreadyExist", new String[]{"%gang%"}, new String[]{args[2]});
								}
							}
							else {
								Messenger.getInstance().sendPluginMessage(sender, "noPermissionsCommand", "%permission%", "gang.create." + (args.length == 3 ? "own" : "other"));
						
							}
						}
						else {
							Messenger.getInstance().sendPluginMessage(sender, "wrongUsage");
							Messenger.getInstance().sendRightUsage(sender, alias, "create <" + Messenger.getInstance().getPluginWord("name") + "> [" + Messenger.getInstance().getPluginWord("owner") + "]");
						}
					}
					else {
						Messenger.getInstance().sendPluginMessage(sender, "wrongUsageAsConsole");
						Messenger.getInstance().sendRightUsage(sender, alias, "gang create <" + Messenger.getInstance().getPluginWord("name") + "> <" + Messenger.getInstance().getPluginWord("owner") + ">");
					}
				} break;
	
				case "delete":
				case "remove": {
					if (args.length >= 3) {
						if (args.length == 3) {
							if (GrandTheftDiamond.checkPermission(sender, "gang.delete.own", true, NoPermissionType.COMMAND)) {
								if (GangManager.getInstance().isGang(args[2])) {
									for (OfflinePlayer p : GangManager.getInstance().getGang(args[2]).getMembers()) {
										if (p.isOnline() && p != sender) {
											Messenger.getInstance().sendPluginMessage(p.getPlayer(), "gang.otherDeleted", new CommandSender[]{sender}, new String[]{"%gang%"}, new String[]{GangManager.getInstance().getGang(args[2]).getName()});
										}
									}
									GangManager.getInstance().removeGang(args[2]);
									Messenger.getInstance().sendPluginMessage(sender, "gang.deleted", new String[]{"%gang%"}, new String[]{args[2]});
								}
								else {
									Messenger.getInstance().sendPluginMessage(sender, "gang.notExist", new String[]{"%gang%"}, new String[]{args[2]});
								}
							}
						}
						else if (GrandTheftDiamond.checkPermission(sender, "gang.delete.other")) {
							if (GangManager.getInstance().isGang(args[2])) {
								GangManager.getInstance().removeGang(args[2]);
								Messenger.getInstance().sendPluginMessage(sender, "gang.deleted", new String[]{"%gang%"}, new String[]{args[2]});
							}
							else {
								Messenger.getInstance().sendPluginMessage(sender, "gang.notExist", new String[]{"%gang%"}, new String[]{args[2]});
							}
						}
						else {
							Messenger.getInstance().sendPluginMessage(sender, "gang.notOwner");
						}
					}
					else {
						Messenger.getInstance().sendPluginMessage(sender, "wrongUsage");
						Messenger.getInstance().sendRightUsage(sender, alias, "gang delete <" + Messenger.getInstance().getPluginWord("name") + ">");
					} 
				} break;
				
				case "removemember": {
					Messenger.getInstance().sendPluginMessage(sender, "§cComing soon");
				} break;
				
				case "info": {
					Messenger.getInstance().sendPluginMessage(sender, "§cComing soon");
				} break;
				
				case "invite": {
					if (sender instanceof Player) {
						Player p = (Player) sender;
						if (args.length >= 4) {
							if (GangManager.getInstance().isGang(args[2])) {
								if (GangManager.getInstance().getGang(args[2]).getLeader() == p || !GangManager.getInstance().getGang(args[2]).getOptionValue(GangOption.LEADER_MUST_INVITE)) {
									if (GangManager.getInstance().getGang(args[2]).canHaveMoreMembers()) {
										Player otherP = Bukkit.getServer().getPlayer(args[3]);
										if (otherP != null) {
											if (GangManager.getInstance().getPlayerGang(p) == null) {
												if (!GangManager.getInstance().getGang(args[2]).isMember(otherP) && (!TemporaryPluginData.getInstance().canAcceptGangInvites(otherP) || !TemporaryPluginData.getInstance().getGangInvitesToAccept(otherP).contains(args[2].toLowerCase()))) {
													if (otherP != p) {
														TemporaryPluginData.getInstance().addGangInviteToAccept(otherP, args[2]);
														Messenger.getInstance().sendPluginMessage(otherP, "gang.askForMembership", new Player[]{p}, new String[]{"%gang%"}, new String[]{GangManager.getInstance().getGang(args[2]).getName()});
														Messenger.getInstance().sendPluginMessage(p, "gang.askedForMembershipOther", new Player[]{otherP}, new String[]{"%gang%"}, new String[]{GangManager.getInstance().getGang(args[2]).getName()});
													}
													else {
														Messenger.getInstance().sendPluginMessage(otherP, "notSelfAsPlayer");
													}
												}
												else {
													Messenger.getInstance().sendPluginMessage(sender, "gang.alreadyInvited", new Player[]{otherP});
												}
											}
											else {
												Messenger.getInstance().sendPluginMessage(p, "gang.alreadyInGangOther", new Player[]{otherP}, new String[]{"%gang%"}, new String[]{GangManager.getInstance().getPlayerGang(p).getName()});
											}
										}
										else {
											Messenger.getInstance().sendPluginMessage(sender, "playerNotOnline");
										}
									}
									else {
										Messenger.getInstance().sendPluginMessage(p, "gang.toMuchMembers", new String[]{"%gang",  "%amount%"}, new String[]{GangManager.getInstance().getGang(args[2]).getName(), String.valueOf(FileManager.getInstance().getFileConfiguration(PluginFile.CONFIG).getInt("gangs.maxMembersPerGang"))});
									}
								}
								else {
									Messenger.getInstance().sendPluginMessage(sender, "gang.notOwner");
								}
							}
							else {
								Messenger.getInstance().sendPluginMessage(sender, "gang.notExist", new String[]{"%gang%"}, new String[]{args[2]});
							}
						}
						else {
							Messenger.getInstance().sendPluginMessage(sender, "wrongUsage");
							Messenger.getInstance().sendRightUsage(sender, alias, "gang invite <" + Messenger.getInstance().getPluginWord("gang") + "> <" + Messenger.getInstance().getPluginWord("player" + ">"));
						}
					}
					else {
						Messenger.getInstance().sendPluginMessage(sender, "notAsConsole");
					}
				} break;
				
				case "list": {
					if (GrandTheftDiamond.checkPermission(sender, "gang.list")) {
						String gangs = "";
						if (GangManager.getInstance().getAllObjects() != null) {
							for (Gang gang : GangManager.getInstance().getAllObjects()) {
								gangs = gangs + Messenger.getInstance().getFormat("gangs").replaceAll("%gang%", gang.getName());
							}
						}
						else {
							gangs = Messenger.getInstance().getFormat("gangs").replaceAll("%gang%", Messenger.getInstance().getPluginWord("none"));
						}
						sender.sendMessage(Messenger.getInstance().getFormat("gangList").replaceAll("%gangs%", gangs));
					}
					else {
						Messenger.getInstance().sendPluginMessage(sender, "noPermissionsCommand");
					}
				} break;
				
				case "help":
				case"?": {
					Messenger.getInstance().sendMessageSection(sender, "Help.gang");
				} break;
				
				default: {
					Messenger.getInstance().sendPluginMessage(sender, "wrongUsage");
					Messenger.getInstance().sendRightUsage(sender, alias, "gang ?");
				}
			}
		}
		else {
			Messenger.getInstance().sendPluginMessage(sender, "wrongUsage");
			Messenger.getInstance().sendRightUsage(sender, alias, "gang ?");
		}
	}

	
	@Override
	public Collection<String> onTabComplete(CommandSender sender, String[] args) {
		if (args.length == 2) {
			return VALID_ARGUMENTS;
		}
		switch (args[1]) {
			case "accept": {
				if (sender instanceof Player) {
					return TemporaryPluginData.getInstance().getGangInvitesToAccept((Player) sender);
				}
			} break;
			
			case "addmember": {
				if (args.length == 3) {
					return Nameables.getNameList(GangManager.getInstance().getAllObjects());
				}
				if (args.length == 4) {
					return new ArrayList<>(GrandTheftDiamond.getPlayerNames(TemporaryPluginData.getInstance().getIngamePlayers()));
				}
			} break;
			
			case "delete": {
				return Nameables.getNameList(GangManager.getInstance().getAllObjects());
			}
			
			case "invite": {
				if (args.length == 3) {
					return Nameables.getNameList(GangManager.getInstance().getAllObjects());
				}
				if (args.length == 4) {
					return new ArrayList<>(GrandTheftDiamond.getPlayerNames(TemporaryPluginData.getInstance().getIngamePlayers()));
				}
			} break;
			
			case "option": {
				if (args.length == 3) {
					return Nameables.getNameList(GangManager.getInstance().getAllObjects());
				}
				if (args.length == 4) {
					return Arrays.asList("pvp", "ownerMustInvite");
				}
				if (args.length == 5) {
					return Arrays.asList("true", "false");
				}
			} break;
			
			case "removemember": {
				if (args.length == 3) {
					return Nameables.getNameList(GangManager.getInstance().getAllObjects());
				}
				if (args.length == 4) {
					Gang gang = GangManager.getInstance().getGang(args[2]);
					if (gang != null) {
						return new ArrayList<>(GrandTheftDiamond.getPlayerNames(gang.getMembers()));
					}
				}
			} break;
		}
		return null;
	}

}

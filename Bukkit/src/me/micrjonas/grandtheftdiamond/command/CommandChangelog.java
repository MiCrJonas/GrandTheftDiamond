package me.micrjonas.grandtheftdiamond.command;

import java.util.Collection;

import me.micrjonas.grandtheftdiamond.GrandTheftDiamond;
import me.micrjonas.grandtheftdiamond.data.FileReloadListener;
import me.micrjonas.grandtheftdiamond.data.PluginFile;
import me.micrjonas.grandtheftdiamond.messenger.Messenger;
import me.micrjonas.grandtheftdiamond.util.Nameables;

import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;

/**
 * Manages command '/gtd changelog *' and its {@link TabCompleter}
 */
public class CommandChangelog implements CommandExecutor, TabCompleter, FileReloadListener {

	private boolean changeLogEnabled;
	private final String headerFormat = Messenger.getInstance().getFormat("header");
	
	/**
	 * Default constructor
	 */
	public CommandChangelog() {
		GrandTheftDiamond.registerFileReloadListener(this);
	}
	
	@Override
	public void configurationReloaded(PluginFile file, FileConfiguration fileConfiguration) {
		if (file == PluginFile.CONFIG) {
				changeLogEnabled = fileConfiguration.getBoolean("useChangeLogFunction");
		}
	}
	
	@Override
	public void onCommand(CommandSender sender, String alias, String[] args, String[] originalArgs) {
		if (changeLogEnabled) {
			if (GrandTheftDiamond.getChangeLog().isAvailable()) {
				if (args.length == 1) {
					Messenger.getInstance().sendMessage(sender, headerFormat.replaceAll("%title%", Messenger.getInstance().getPluginWordStartsUpperCase("changelog") + " (" + Messenger.getInstance().getWordStartUpperCase(GrandTheftDiamond.getChangeLog().getLatestVersion().toString()) + ")"), false);
					for (String line : GrandTheftDiamond.getChangeLog().getChangeLog().get(GrandTheftDiamond.getChangeLog().getLatestVersion())) {
						Messenger.getInstance().sendMessage(sender, line, false);
					}
				}
				else {
					if (GrandTheftDiamond.getChangeLog().getChangeLog().containsKey(args[1])) {
						Messenger.getInstance().sendMessage(sender, headerFormat.replaceAll("%title%", Messenger.getInstance().getPluginWordStartsUpperCase("changelog") + " (" + Messenger.getInstance().getWordStartUpperCase(args[1]) + ")"), false);
						for (String line : GrandTheftDiamond.getChangeLog().getChangeLog().get(args[1])) {
							Messenger.getInstance().sendMessage(sender, line, false);
						}
					}
					else {
						Messenger.getInstance().sendPluginMessage(sender, "changelog.noVersion", "%argument%", args[1]);
					}
				}
			}
			else {
				Messenger.getInstance().sendPluginMessage(sender, "changelog.notLoaded");
			}
		}
		else {
			Messenger.getInstance().sendPluginMessage(sender, "changelog.notEnabled");
		}
	}

	@Override
	public Collection<String> onTabComplete(CommandSender sender, String[] args) {
		if (GrandTheftDiamond.getChangeLog() == null || GrandTheftDiamond.getChangeLog().getChangeLog() == null) {
			return null;
		}
		return Nameables.getNameList(GrandTheftDiamond.getChangeLog().getVersions());
	}

}

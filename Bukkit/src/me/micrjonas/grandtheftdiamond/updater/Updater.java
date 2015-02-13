package me.micrjonas.grandtheftdiamond.updater;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.Enumeration;
import java.util.logging.Level;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

import me.micrjonas.grandtheftdiamond.GrandTheftDiamond;
import me.micrjonas.grandtheftdiamond.bukkit.BukkitGrandTheftDiamondPlugin;
import me.micrjonas.util.ReleaseType;
import me.micrjonas.util.Version;

import org.bukkit.Bukkit;
import org.bukkit.configuration.file.YamlConfiguration;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.JSONValue;

/**
 * Check dev.bukkit.org to find updates for a given GrandTheftDiamondPlugin.getInstance(), and download the updates if required.
 * <p/>
 * <b>VERY, VERY IMPORTANT</b>: Because there are no standards for adding auto-update toggles in your plugin's config, this system provides NO CHECK WITH YOUR CONFIG to make sure the user has allowed auto-updating.
 * <br>
 * It is a <b>BUKKIT POLICY</b> that you include a boolean value in your config that prevents the auto-updater from running <b>AT ALL</b>.
 * <br>
 * If you fail to include this option in your config, your plugin will be <b>REJECTED</b> when you attempt to submit it to dev.bukkit.org.
 * <p/>
 * An example of a good configuration option would be something similar to 'auto-update: true' - if this value is set to false you may NOT run the auto-updater.
 * <br>
 * If you are unsure about these rules, please read the plugin submission guidelines: http://goo.gl/8iU5l
 *
 * @author Gravity
 * @version 2.0
 */
	

public class Updater {
	
	private final BukkitGrandTheftDiamondPlugin plugin = BukkitGrandTheftDiamondPlugin.getInstance();
	
	private static Updater updater = null;
	
	private static boolean update = false;
	private static String updateVersionName = "";
	private static ReleaseType releaseType = null;
	private static String updateVersion = "";
	private static String updateLink = "";

	
	public static boolean updateAvailable() {
		return update;
	}
	
	public static String getUpdateVersionName() {
		return updateVersionName;
	}
	
	public static Version getUpdateVersion1() {
		return new Version(getUpdateVersionName().substring("GrandTheftDiamond v".length(), getUpdateVersionName().length()));
	}
	
	public static ReleaseType getType() {
		return releaseType;
	}
	
	public static String getUpdateVersion() {
		return updateVersion;
	}
	
	public static String getUpdateLink() {
		return updateLink;
	}
	
	public static void updateUpdater() {
		GrandTheftDiamond.getLogger().info("Checking for updates...");
		updater = new Updater(UpdateType.NO_DOWNLOAD);
		update = updater.getResult() == UpdateResult.UPDATE_AVAILABLE;
		updateVersionName = updater.getLatestName();
		updateVersion = updater.getLatestGameVersion();
		releaseType = updater.getLatestType();
		updateLink = updater.getLatestFileLink();
	}
	
	public static void download() {
		new Updater(UpdateType.DOWNLOAD_NO_VERSION_CHECK);
		update = false;
	}
	
	private UpdateType type;
	private String versionName;
	private String versionLink;
	private String versionType;
	private String versionGameVersion;

	private URL url; // Connecting to RSS
	private final File file = plugin.getFile(); // The plugin's file
	private Thread thread; // Updater thread

	private final int id = 68987; // Project's Curse ID
	private String apiKey = null; // BukkitDev ServerMods API key
	private static final String TITLE_VALUE = "name"; // Gets remote file's title
	private static final String LINK_VALUE = "downloadUrl"; // Gets remote file's download link
	private static final String TYPE_VALUE = "releaseType"; // Gets remote file's release type
	private static final String VERSION_VALUE = "gameVersion"; // Gets remote file's build version
	private static final String QUERY = "/servermods/files?projectIds="; // Path to GET
	private static final String HOST = "https://api.curseforge.com"; // Slugs will be appended to this to get to the project's RSS feed

	private static final String USER_AGENT = "Updater (by Gravity)";
	private static final String delimiter = "^v|[\\s_-]v"; // Used for locating version numbers in file names
	private static final String[] NO_UPDATE_TAG = { "DEV", "PRE", "SNAPSHOT" }; // If the version number contains one of these, don't update.
	private static final int BYTE_SIZE = 1024; // Used for downloading files
	private final YamlConfiguration config = new YamlConfiguration(); // Config file
	private String updateFolder;// The folder that downloads will be placed in
	private UpdateResult result = UpdateResult.SUCCESS; // Used for determining the outcome of the update process


	/**
	 * Initialize the updater.
	 *
	 * @param type	 Specify the type of update this will be. See {@link UpdateType}
	 */
	private Updater(UpdateType type) {
		this.type = type;
		this.updateFolder = Bukkit.getServer().getUpdateFolder();

		final File pluginFile = plugin.getDataFolder().getParentFile();
		final File updaterFile = new File(pluginFile, "Updater");
		final File updaterConfigFile = new File(updaterFile, "config.yml");

		this.config.options().header("This configuration file affects all plugins using the Updater system (version 2+ - http://forums.bukkit.org/threads/96681/ )" + '\n'
				+ "If you wish to use your API key, read http://wiki.bukkit.org/ServerMods_API and place it below." + '\n'
				+ "Some updating systems will not adhere to the disabled value, but these may be turned off in their plugin's configuration.");
		this.config.addDefault("api-key", "PUT_API_KEY_HERE");
		this.config.addDefault("disable", false);

		if (!updaterFile.exists()) {
			updaterFile.mkdir();
		}

		boolean createFile = !updaterConfigFile.exists();
		try {
			if (createFile) {
				updaterConfigFile.createNewFile();
				this.config.options().copyDefaults(true);
				this.config.save(updaterConfigFile);
			} else {
				this.config.load(updaterConfigFile);
			}
		} catch (final Exception e) {
			if (createFile) {
				plugin.getLogger().severe("The updater could not create configuration at " + updaterFile.getAbsolutePath());
			} else {
				plugin.getLogger().severe("The updater could not load configuration at " + updaterFile.getAbsolutePath());
			}
			plugin.getLogger().log(Level.SEVERE, null, e);
		}

		if (this.config.getBoolean("disable")) {
			this.result = UpdateResult.DISABLED;
			return;
		}

		String key = this.config.getString("api-key");
		if (key.equalsIgnoreCase("PUT_API_KEY_HERE") || key.equals("")) {
			key = null;
		}

		this.apiKey = key;

		try {
			this.url = new URL(Updater.HOST + Updater.QUERY + id);
		} catch (final MalformedURLException e) {
			plugin.getLogger().log(Level.SEVERE, "The project ID provided for updating, " + id + " is invalid.", e);
			this.result = UpdateResult.FAIL_BADID;
		}

		this.thread = new Thread(new UpdateRunnable());
		this.thread.start();
	}

	/**
	 * Get the result of the update process.
	 *
	 * @return result of the update process.
	 * @see UpdateResult
	 */
	public UpdateResult getResult() {
		this.waitForThread();
		return this.result;
	}

	/**
	 * Get the latest version's release type.
	 *
	 * @return latest version's release type.
	 * @see ReleaseType
	 */
	public ReleaseType getLatestType() {
		this.waitForThread();
		if (this.versionType != null) {
			for (ReleaseType type : ReleaseType.values()) {
				if (this.versionType.equals(type.name().toLowerCase())) {
					return type;
				}
			}
		}
		return null;
	}

	/**
	 * Get the latest version's game version (such as "CB 1.2.5-R1.0").
	 *
	 * @return latest version's game version.
	 */
	public String getLatestGameVersion() {
		this.waitForThread();
		return this.versionGameVersion;
	}

	/**
	 * Get the latest version's name (such as "Project v1.0").
	 *
	 * @return latest version's name.
	 */
	public String getLatestName() {
		this.waitForThread();
		return this.versionName;
	}

	/**
	 * Get the latest version's direct file link.
	 *
	 * @return latest version's file link.
	 */
	public String getLatestFileLink() {
		this.waitForThread();
		return this.versionLink;
	}

	/**
	 * As the result of Updater output depends on the thread's completion, it is necessary to wait for the thread to finish
	 * before allowing anyone to check the result.
	 */
	private void waitForThread() {
		if ((this.thread != null) && this.thread.isAlive()) {
			try {
				this.thread.join();
			} catch (final InterruptedException e) {
				plugin.getLogger().log(Level.SEVERE, null, e);
			}
		}
	}

	/**
	 * Save an update from dev.bukkit.org into the server's update folder.
	 *
	 * @param folder the updates folder location.
	 * @param file the name of the file to save it as.
	 * @param link the url of the file.
	 */
	private void saveFile(File folder, String file, String link) {
		if (!folder.exists()) {
			folder.mkdir();
		}
		BufferedInputStream in = null;
		FileOutputStream fout = null;
		try {
			// Download the file
			final URL url = new URL(link);
			in = new BufferedInputStream(url.openStream());
			fout = new FileOutputStream(folder.getAbsolutePath() + File.separator + file);

			final byte[] data = new byte[Updater.BYTE_SIZE];
			int count;
			while ((count = in.read(data, 0, Updater.BYTE_SIZE)) != -1) {
				fout.write(data, 0, count);
			}
			//Just a quick check to make sure we didn't leave any files from last time...
			for (final File xFile : new File(plugin.getDataFolder().getParent(), this.updateFolder).listFiles()) {
				if (xFile.getName().endsWith(".zip")) {
					xFile.delete();
				}
			}
			// Check to see if it's a zip file, if it is, unzip it.
			final File dFile = new File(folder.getAbsolutePath() + File.separator + file);
			if (dFile.getName().endsWith(".zip")) {
				// Unzip
				this.unzip(dFile.getCanonicalPath());
			}
		} catch (final Exception ex) {
			plugin.getLogger().warning("The auto-updater tried to download a new update, but was unsuccessful.");
			this.result = UpdateResult.FAIL_DOWNLOAD;
		} finally {
			try {
				if (in != null) {
					in.close();
				}
				if (fout != null) {
					fout.close();
				}
			} catch (final Exception ex) {
			}
		}
	}

	/**
	 * Part of Zip-File-Extractor, modified by Gravity for use with Updater.
	 *
	 * @param file the location of the file to extract.
	 */
	private void unzip(String file) {
		try {
			final File fSourceZip = new File(file);
			final String zipPath = file.substring(0, file.length() - 4);
			ZipFile zipFile = new ZipFile(fSourceZip);
			Enumeration<? extends ZipEntry> e = zipFile.entries();
			while (e.hasMoreElements()) {
				ZipEntry entry = e.nextElement();
				File destinationFilePath = new File(zipPath, entry.getName());
				destinationFilePath.getParentFile().mkdirs();
				if (entry.isDirectory()) {
					continue;
				} else {
					final BufferedInputStream bis = new BufferedInputStream(zipFile.getInputStream(entry));
					int b;
					final byte buffer[] = new byte[Updater.BYTE_SIZE];
					final FileOutputStream fos = new FileOutputStream(destinationFilePath);
					final BufferedOutputStream bos = new BufferedOutputStream(fos, Updater.BYTE_SIZE);
					while ((b = bis.read(buffer, 0, Updater.BYTE_SIZE)) != -1) {
						bos.write(buffer, 0, b);
					}
					bos.flush();
					bos.close();
					bis.close();
					final String name = destinationFilePath.getName();
					if (name.endsWith(".jar") && pluginFile(name)) {
						destinationFilePath.renameTo(new File(plugin.getDataFolder().getParent(), this.updateFolder + File.separator + name));
					}
				}
				entry = null;
				destinationFilePath = null;
			}
			e = null;
			zipFile.close();
			zipFile = null;

			// Move any plugin data folders that were included to the right place, Bukkit won't do this for us.
			for (final File dFile : new File(zipPath).listFiles()) {
				if (dFile.isDirectory()) {
					if (pluginFile(dFile.getName())) {
						final File oFile = new File(plugin.getDataFolder().getParent(), dFile.getName()); // Get current dir
						final File[] contents = oFile.listFiles(); // List of existing files in the current dir
						for (final File cFile : dFile.listFiles()) // Loop through all the files in the new dir
						{
							boolean found = false;
							for (final File xFile : contents) // Loop through contents to see if it exists
							{
								if (xFile.getName().equals(cFile.getName())) {
									found = true;
									break;
								}
							}
							if (!found) {
								// Move the new file into the current dir
								cFile.renameTo(new File(oFile.getCanonicalFile() + File.separator + cFile.getName()));
							} else {
								// This file already exists, so we don't need it anymore.
								cFile.delete();
							}
						}
					}
				}
				dFile.delete();
			}
			new File(zipPath).delete();
			fSourceZip.delete();
		} catch (final IOException e) {
			plugin.getLogger().log(Level.SEVERE, "The auto-updater tried to unzip a new update file, but was unsuccessful.", e);
			this.result = UpdateResult.FAIL_DOWNLOAD;
		}
		new File(file).delete();
	}

	/**
	 * Check if the name of a jar is one of the plugins currently installed, used for extracting the correct files out of a zip.
	 *
	 * @param name a name to check for inside the plugins folder.
	 * @return true if a file inside the plugins folder is named this.
	 */
	private boolean pluginFile(String name) {
		for (final File file : new File("plugins").listFiles()) {
			if (file.getName().equals(name)) {
				return true;
			}
		}
		return false;
	}

	/**
	 * Check to see if the program should continue by evaluating whether the plugin is already updated, or shouldn't be updated.
	 *
	 * @param title the plugin's title.
	 * @return true if the version was located and is not the same as the remote's newest.
	 */
	private boolean versionCheck(String title) {
		if (this.type != UpdateType.DOWNLOAD_NO_VERSION_CHECK) {
			final String localVersion = BukkitGrandTheftDiamondPlugin.getInstance().getDescription().getVersion();
			String[] titleSplit = title.split(delimiter);
			if (titleSplit.length == 2) {
				
				if (!shouldUpdate(localVersion, titleSplit[1])) {
					
                    // We already have the latest version, or this build is tagged for no-update
                    this.result = UpdateResult.NO_UPDATE;
                    return false;
					
				}

			} else {
				// The file's name did not contain the string 'vVersion'
				final String authorInfo = BukkitGrandTheftDiamondPlugin.getInstance().getDescription().getAuthors().size() == 0 ? "" : " (" + BukkitGrandTheftDiamondPlugin.getInstance().getDescription().getAuthors().get(0) + ")";
				plugin.getLogger().warning("The author of this plugin " + authorInfo + " has misconfigured their Auto Update system");
				plugin.getLogger().warning("File versions should follow the format 'PluginName vVERSION'");
				plugin.getLogger().warning("Please notify the author of this error.");
				this.result = UpdateResult.FAIL_NOVERSION;
				return false;
			}
		}
		return true;
	}

	/**
	 * <b>If you wish to run mathematical versioning checks, edit this method.</b>
	 * <p>
	 * With default behavior, Updater will NOT verify that a remote version available on BukkitDev
	 * which is not this version is indeed an "update".
	 * If a version is present on BukkitDev that is not the version that is currently running,
	 * Updater will assume that it is a newer version.
	 * This is because there is no standard versioning scheme, and creating a calculation that can
	 * determine whether a new update is actually an update is sometimes extremely complicated.
	 * </p>
	 * <p>
	 * Updater will call this method from {@link #versionCheck(String)} before deciding whether
	 * the remote version is actually an update.
	 * If you have a specific versioning scheme with which a mathematical determination can
	 * be reliably made to decide whether one version is higher than another, you may
	 * revise this method, using the local and remote version parameters, to execute the
	 * appropriate check.
	 * </p>
	 * <p>
	 * Returning a value of <b>false</b> will tell the update process that this is NOT a new version.
	 * Without revision, this method will always consider a remote version at all different from
	 * that of the local version a new update.
	 * </p>
	 * @param localVersion the current version
	 * @param remoteVersion the remote version
	 * @return true if Updater should consider the remote version an update, false if not.
	 */
	public boolean shouldUpdate(String localVersion, String remoteVersion) {
		if (hasTag(remoteVersion)) {
			return false;
		}
		return GrandTheftDiamond.getVersion().before(new Version(remoteVersion));
	}

	/**
	 * Evaluate whether the version number is marked showing that it should not be updated by this program.
	 *
	 * @param version a version number to check for tags in.
	 * @return true if updating should be disabled.
	 */
	private boolean hasTag(String version) {
		for (final String string : Updater.NO_UPDATE_TAG) {
			if (version.toUpperCase().contains(string)) {
				return true;
			}
		}
		return false;
	}

	/**
	 * Make a connection to the BukkitDev API and request the newest file's details.
	 *
	 * @return true if successful.
	 */
	private boolean read() {
		try {
			final URLConnection conn = this.url.openConnection();
			conn.setConnectTimeout(5000);

			if (this.apiKey != null) {
				conn.addRequestProperty("X-API-Key", this.apiKey);
			}
			conn.addRequestProperty("User-Agent", Updater.USER_AGENT);

			conn.setDoOutput(true);

			final BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream()));
			final String response = reader.readLine();

			final JSONArray array = (JSONArray) JSONValue.parse(response);

			if (array.size() == 0) {
				plugin.getLogger().warning("The updater could not find any files for the project id " + this.id);
				this.result = UpdateResult.FAIL_BADID;
				return false;
			}

			this.versionName = (String) ((JSONObject) array.get(array.size() - 1)).get(Updater.TITLE_VALUE);
			this.versionLink = (String) ((JSONObject) array.get(array.size() - 1)).get(Updater.LINK_VALUE);
			this.versionType = (String) ((JSONObject) array.get(array.size() - 1)).get(Updater.TYPE_VALUE);
			this.versionGameVersion = (String) ((JSONObject) array.get(array.size() - 1)).get(Updater.VERSION_VALUE);

			return true;
		} catch (final IOException e) {
			if (e.getMessage().contains("HTTP response code: 403")) {
				plugin.getLogger().severe("dev.bukkit.org rejected the API key provided in plugins/Updater/config.yml");
				plugin.getLogger().severe("Please double-check your configuration to ensure it is correct.");
				this.result = UpdateResult.FAIL_APIKEY;
			} else {
				plugin.getLogger().severe("The updater could not contact dev.bukkit.org for updating.");
				plugin.getLogger().severe("If you have not recently modified your configuration and this is the first time you are seeing this message, the site may be experiencing temporary downtime.");
				this.result = UpdateResult.FAIL_DBO;
			}
			//plugin.getLogger().log(Level.SEVERE, null, e);
			return false;
		}
	}

	private class UpdateRunnable implements Runnable {

		@Override
		public void run() {
			if (Updater.this.url != null) {
				// Obtain the results of the project's file feed
				if (Updater.this.read()) {
					if (Updater.this.versionCheck(Updater.this.versionName)) {
						if ((Updater.this.versionLink != null) && (Updater.this.type != UpdateType.NO_DOWNLOAD)) {
							String name = Updater.this.file.getName();
							// If it's a zip file, it shouldn't be downloaded as the plugin's name
							if (Updater.this.versionLink.endsWith(".zip")) {
								final String[] split = Updater.this.versionLink.split("/");
								name = split[split.length - 1];
							}
							Updater.this.saveFile(new File(GrandTheftDiamond.getDataFolder().getParent(), Updater.this.updateFolder), name, Updater.this.versionLink);
						} else {
							Updater.this.result = UpdateResult.UPDATE_AVAILABLE;
						}
					}
				}
			}
		}
	}
}
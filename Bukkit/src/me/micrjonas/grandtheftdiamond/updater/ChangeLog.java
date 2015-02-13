package me.micrjonas.grandtheftdiamond.updater;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.ConnectException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import me.micrjonas.grandtheftdiamond.GrandTheftDiamond;
import me.micrjonas.util.Version;

import org.apache.commons.lang.StringEscapeUtils;

/**
 * Represents the plugin's history change log
 */
public class ChangeLog {
	
	private final static URL CHANGE_LOG_URL;
	
	static {
		URL url;
		try {			
			url = new URL("http://dev.bukkit.org/bukkit-plugins/grand-theft-diamond/files.rss");			
		} 
		catch (MalformedURLException ex) { // Never reached
			ex.printStackTrace();
			url = null;
		}
		CHANGE_LOG_URL = url;
	}
	
	private Map<Version, List<String>> changeLog;
	private Version latestVersion;
	private List<Version> versions;
	
	/**
	 * Returns a {@code Map} containing all versions as key with the changes in a {@code List} as value
	 * @return A {@code Map} with the change log. The map is immutable
	 */
	public Map<Version, List<String>> getChangeLog() {
		return changeLog;
	}
	
	/**
	 * Returns the latest uploaded version
	 * @return The latest version found on bukkit.org
	 */
	public Version getLatestVersion() {
		return latestVersion;
	}
	
	/**
	 * Returns a {@code List} containing all versions.
	 * @return A {@code List} with all versions sorted by their release date
	 */
	public List<Version> getVersions() {
		return versions;
	}
	
	/**
	 * Returns whether the change log is loaded from bukkit.org
	 * @return True if the change log is loaded, else false
	 */
	public boolean isAvailable() {
		return getChangeLog() != null && getLatestVersion() != null;
	}
	
	/**
	 * Updates the change log, connecting from bukkit.org
	 * @return True if the change log was updated, false if the update failed
	 */
	public boolean updateChangeLog() {
		try {
			update();
			return true;
		} 
		catch (UnknownHostException | ConnectException ex) {
			GrandTheftDiamond.getLogger().warning("Failed to connect to dev.bukkit.org to download the change log. Is dev.bukkit.org offline?");
		}
		catch (IOException e) {
			e.printStackTrace();
		}
		return false;
	}
	
	private void update() throws IOException {
		Map<Version, List<String>> versionFeed = new HashMap<>();
		BufferedReader reader = new BufferedReader(new InputStreamReader(CHANGE_LOG_URL.openStream()));
		
		String line;
		List<String> lines = new ArrayList<>();
		while((line = reader.readLine()) != null) {
			lines.add(line);
		}
		latestVersion = new Version(lines.get(12).substring(49, lines.get(12).length() - 36).toLowerCase().replaceAll(" ", "_"));
		versionFeed.put(latestVersion, new ArrayList<String>());
		Version version = latestVersion;
		int liCount = 0;
		int ulCount = 0;
		for (int i = 13; i < lines.size() && !(line = lines.get(i)).equals("</description>"); i++) {
			if (line.length() > 0) {
				int firstIndex = line.indexOf("&lt;p&gt;&lt;strong&gt; &lt;u&gt;");
				if (firstIndex >= 0) {
					String versionName = line.substring(firstIndex + 33, line.length() - 36).toLowerCase().replaceAll(" ", "_");
					if (versionName.contains("/")) {
						String[] split = versionName.split("/");
						versionName = split[split.length - 1];	
					}
					try {
						version = new Version(versionName);
					}
					catch (IllegalArgumentException ex) {
						GrandTheftDiamond.getLogger().warning("Version name '" + versionName + "' of change log is invalid. Please report this");
						GrandTheftDiamond.getLogger().warning("Unparsed: " + line);
						continue;
					}
					versionFeed.put(version, new ArrayList<String>());
				}
				else if (versionFeed.containsKey(version)) {
					line = StringEscapeUtils.unescapeHtml(line);
					liCount += containsCount(line, "<li>");
					ulCount += containsCount(line, "<ul>");
					liCount -= containsCount(line, "</li>");
					ulCount -= containsCount(line, "</ul>");
					line = line.replaceAll("<p>", "§l")
							.replaceAll("<.*>", "")
							.replaceAll("</.*>", "")
							.replaceAll("&gt;", ">")
							.replaceAll("t;", "<");
					
					if (!(line.equals(" ") || line.equals(""))) {
						line = "- " + line;
						for (int j = liCount; j > 0; j--) {
							line = " " + line;
						}
						for (int j = ulCount; j > 0; j--) {
							line = " " + line;
						}
						versionFeed.get(version).add(line);
					}
				}
			}
		}
		for (Version tmpVersion : versionFeed.keySet()) {
			versionFeed.put(tmpVersion, Collections.unmodifiableList(versionFeed.get(tmpVersion)));
		}
		changeLog = Collections.unmodifiableMap(versionFeed);
		versions = new ArrayList<>(changeLog.keySet());
		Collections.sort(versions);
		Collections.reverse(versions);
		versions = Collections.unmodifiableList(versions);
	}
	
	private int containsCount(String arg0, String arg1) {
		return (int) (Math.ceil(arg0.length() - arg0.replaceAll(arg1, "").length()) / arg1.length());
	}

}

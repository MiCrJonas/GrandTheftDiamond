package me.micrjonas.grandtheftdiamond.data;

import me.micrjonas.grandtheftdiamond.GrandTheftDiamond;

import org.bukkit.configuration.file.FileConfiguration;


class YAMLConverter {
	
	public static void convert(final PluginFile file) {
		GrandTheftDiamond.runTaskAsynchronously(new Runnable() {
			@Override
			public void run() {
				FileConfiguration config = FileManager.getInstance().getFileConfiguration(PluginFile.CONFIG);
				for (String path : config.getConfigurationSection("").getKeys(true)) {
					if (!(config.isConfigurationSection(path) || path.contains("-"))) {
						String newPath = changeString(path);
						config.set(newPath, config.get(path));
						config.set(path, null);
					}
				}
				FileManager.getInstance().saveFileConfiguration(file);
			}
		});	
	}
	
	private static String changeString(String s) {
		if ((!s.contains(".") && Character.isUpperCase(s.charAt(0))) || Character.isUpperCase(s.charAt(s.lastIndexOf(".") + 1))) {
			return s;
		}
		for (int i = 0; i < s.length(); i++) {
			if (Character.isUpperCase(s.charAt(i))) {
				s = s.substring(0, i) + "-" + Character.toLowerCase(s.charAt(i)) + s.substring(i + 1, s.length());
				i++;
			}
		}
		return s;
	}

}

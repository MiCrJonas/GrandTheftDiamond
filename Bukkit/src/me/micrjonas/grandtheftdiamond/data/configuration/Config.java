package me.micrjonas.grandtheftdiamond.data.configuration;

import java.io.File;
import java.io.IOException;

import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.YamlConfiguration;
 
public class Config extends YamlConfiguration {
	private int comments;
 
	private final File file;
 
	public Config(String content, File configFile, int comments) {
		this.comments = comments;
		this.file = configFile;
	   reloadConfig();
	}
 
	@Override
	public void save(File file) throws IOException {
		ConfigManager.getInstance().saveConfig(saveToString(), file);
	}
	
	@Override
	public Object get(String path, Object def) {
		if (path.indexOf("_COMMENT_") > 0) {
			return def;
		}
		return super.get(path, def);
	}
	
	private String getCommentPath(String path) {
		return path.substring(0, path.lastIndexOf(".")) + "." + "_COMMENT_" + comments;
	}
	
	public void set(String path, Object value, String comment) {
		if(!super.contains(path)) {
			super.set(getCommentPath(path), " " + comment);
			comments++;
		}
		super.set(path, value);
	}
 
	public void set(String path, Object value, String[] comment) {
		for(String comm : comment) {
			if(!super.contains(path)) {
				super.set(getCommentPath(path), " " + comm);
				comments++;
			}
		}
		super.set(path, value);
	}
	
	public void addDefault(String path, Object value, String comment) {
		if (super.get(path) == null) {
			set(path, value, comment);
		}
	}
	
	public void addDefault(String path, Object value, String[] comment) {
		if (super.get(path) == null) {
			set(path, value, comment);
		}
	}
	
	public void setHeader(String[] header) {
		ConfigManager.getInstance().setHeader(this.file, header);
		comments = header.length + 2;
		reloadConfig();
	}

	public void reloadConfig() {	
		try {
			super.loadFromString(ConfigManager.getInstance().getConfigContent(file));
		}
		catch (InvalidConfigurationException e) {
			e.printStackTrace();
		}
	}
 
	public void saveConfig() {
		ConfigManager.getInstance().saveConfig(saveToString(), file);
	}
 
}
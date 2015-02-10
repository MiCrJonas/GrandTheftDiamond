package me.micrjonas.grandtheftdiamond.api;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.Enumeration;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

import me.micrjonas.grandtheftdiamond.GrandTheftDiamond;
import me.micrjonas.grandtheftdiamond.bukkit.BukkitGrandTheftDiamondPlugin;
import me.micrjonas.grandtheftdiamond.data.FileManager;

import org.bukkit.Bukkit;
import org.bukkit.event.Listener;

/**
 * Class to load the listeners from the listener folder. Loads *.class and *.jar files
 */
public class ListenerLoader extends URLClassLoader {
	
	private final File folder;
	
	/**
	 * @param loader The Bukkit's {@link ClassLoader}
	 * @param folder The {@code URL} to the listener folder
	 */
	public ListenerLoader(ClassLoader loader, URL folder) {
		super(new URL[]{folder}, loader);
		File tmpFolder = null;
		try {
			tmpFolder = new File(folder.toURI());
			tmpFolder.mkdir();
		} 
		catch (URISyntaxException ex) {
			ex.printStackTrace();
			this.folder = null;
			return;
		}
		tmpFolder.mkdirs();
		this.folder = tmpFolder;
	}
	
	/**
	 * Loads the listeners of the listener folder
	 */
	public void loadListeners() {
		if (folder == null) {
			GrandTheftDiamond.getLogger().warning("Cannot load find folder");
		}
		FileManager.getInstance().createFileIfNotExists(folder);
		for (File f : folder.listFiles()) {
			//Load class file
			if (f.getName().endsWith(".class")) {
				loadListener(f.getName());
			}
			//Load jar file
			else if (f.getName().endsWith(".jar")) {
				try {
					addURL(f.toURI().toURL());
				} 
				catch (MalformedURLException e) {
					GrandTheftDiamond.getLogger().warning("Failed to load listeners of '" + f.getName() + "':");
					e.printStackTrace();
					continue;
				}
				JarFile jar;
				try {
					jar = new JarFile(f);
				} 
				catch (IOException e) {
					GrandTheftDiamond.getLogger().warning("Failed to load listeners of '" + f.getName() + "':");
					e.printStackTrace();
					continue;
				}
				Enumeration<JarEntry> entries = jar.entries();
				while (entries.hasMoreElements()) {
					JarEntry entry = entries.nextElement();
					if (!entry.isDirectory() && entry.getName().endsWith(".class")) {
						loadListener(entry.getName());
					}
				}
				try {
					jar.close();
				} 
				catch (IOException e) {
					GrandTheftDiamond.getLogger().warning("Failed to close listener file '" + f.getName() + "':");
					e.printStackTrace();
				}
			}
		}
	}
	
	private void loadListener(String name) {
		Class<?> clazz;
		Object obj;
		try {
			clazz = loadClass(name.substring(0, name.length() - 6));
		} 
		catch (ClassNotFoundException e) {
			GrandTheftDiamond.getLogger().warning("Failed to load listener '" + name + "':");
			e.printStackTrace();
			return;
		}
		try {
			obj = clazz.newInstance();
		} 
		catch (InstantiationException | IllegalAccessException e) {
			GrandTheftDiamond.getLogger().warning("Failed to load listener '" + clazz.getName() + "':");
			e.printStackTrace();
			return;
		}
		if (obj instanceof Listener) {
			Listener l = (Listener) obj;
			try {
				Bukkit.getPluginManager().registerEvents(l, BukkitGrandTheftDiamondPlugin.getInstance());
				GrandTheftDiamond.getLogger().info("Listener '" + l.getClass().getName() + "' loaded");
			}
			catch (Exception e) {
				GrandTheftDiamond.getLogger().warning("Failed to load listener '" + l.getClass().getName() + "':");
				e.printStackTrace();
			}
		}
		else {
			GrandTheftDiamond.getLogger().warning("Listener '" + clazz.getName() + "' must be an instance of org.bukkit.event.Listener");
		}
	}

}

package me.micrjonas.grandtheftdiamond.util;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.bukkit.ChatColor;

public class StringUtils {
	
	private StringUtils() { }
	
	public static String removeColors(String s) {
		return s == null ? null : s.replaceAll("(?i)&([a-f0-9k-o])", "").replaceAll("(?i)§([a-f0-9k-o])", "");
	}
	
	
	public static String translateColors(String s) {
		return ChatColor.translateAlternateColorCodes('&', s);
	}
	
	
	public static Map<String, String> getArgumentValues(String[] args, boolean throwException) {
		
		Map<String, String> values = new LinkedHashMap<>();
		List<String> wrongArgs = new ArrayList<>();
		
		for (String arg : args) {
			
			String[] split = arg.split(":");
			
			if (split.length == 2 && split[0].length() > 1 && split[0].startsWith("-"))
				values.put(split[0].substring(1, split[0].length()).toLowerCase(), split[1]);
			
			else
				wrongArgs.add(arg);
			
		}
		
		if (wrongArgs.size() > 0)
			throw new IllegalArgumentFormatException(wrongArgs);
		
		return values;
		
	}
	
	
	public static String connect(String[] strings, int startIndex, int endIndex) {
		StringBuilder builder = new StringBuilder();
		for (int i = startIndex; i < endIndex; i++)
			builder.append(strings[i]);
		
		return builder.toString();
	}

}

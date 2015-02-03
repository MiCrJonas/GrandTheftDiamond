package me.micrjonas.grandtheftdiamond.data.storage;

import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Map.Entry;

import me.micrjonas.grandtheftdiamond.util.IllegalArgumentFormatException;
import me.micrjonas.grandtheftdiamond.util.Objects;

public class Storables {
	
	private final static char ASSIGNMENT_MARK = '=';
	//private final static char DELIMITER = ',';
	private final static String ASSIGNMENT_MARK_STRING = String.valueOf(ASSIGNMENT_MARK);
	//private final static String DELIMITER_STRING = String.valueOf(DELIMITER);
	
	private Storables() { /*private constructor*/ }
	
	public static String serialize(Storable s) throws IllegalArgumentException {
		if (s == null)
			throw new IllegalArgumentException("s is not allowed to be null");
		
		StringBuilder builder = new StringBuilder();
		Iterator<Entry<String, Object>> iter = s.getStoreData().entrySet().iterator();
		Entry<String, Object> entry;
		while (iter.hasNext()) {
			entry = iter.next();
			builder.append(entry.getKey()).append(ASSIGNMENT_MARK).append(entry.getValue());
			if (iter.hasNext())
				builder.append(",");
		}
		return builder.toString();
	}
	
	public static Map<String, Object> deserialize(String s) throws IllegalArgumentException, IllegalArgumentFormatException {
		if (s == null)
			throw new IllegalArgumentException("s is not allowed to be null");
		
		Map<String, Object> data = new LinkedHashMap<>();
		String[] entries = s.split(",");
		
		for (String entry : entries) {
			String[] parts = entry.split(ASSIGNMENT_MARK_STRING);
			if (parts.length == 2)
				data.put(parts[0], Objects.belongingObject(s));
			
			else
				throw new IllegalArgumentFormatException("Cannot deseralize input String: " + s);
		}
		return data;
	}
	
	/*public static Map<String, Object> deserialize2(String s) throws IllegalArgumentException, IllegalArgumentFormatException {
		if (s == null)
			throw new IllegalArgumentException("s is not allowed to be null");
		
		Map<String, Object> data = new LinkedHashMap<>();
		String[] pairs = s.split(",");
		for (String pair : pairs) {
			List<Entry<StringBuilder, StringBuilder>> parts = new ArrayList<>();
			int index = 0;
			boolean isConnectedString = false;
			for (int i = 0; i < s.length(); i++) {
				if (s.charAt(i) == '\"')
					isConnectedString = !isConnectedString;
				
				else if (isConnectedString)
					parts.get(i).append(s.charAt(i));
				
				else
					
			}
		}
	}*/

}

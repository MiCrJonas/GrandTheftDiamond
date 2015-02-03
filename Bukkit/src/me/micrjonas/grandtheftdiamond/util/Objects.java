package me.micrjonas.grandtheftdiamond.util;

import java.util.Arrays;


public class Objects {
	
	private Objects() { }
	
	public static boolean getBooleanOrDefault(Object obj, boolean defaultValue) {
		
		if (obj != null) {
			
			if (obj instanceof Boolean)
				return (boolean) obj;
			
			if (obj instanceof String) {
				
				try {
					return Boolean.parseBoolean((String) obj);
				}
				
				catch (NumberFormatException ex) { }
				
			}
			
		}
		
		return defaultValue;
		
	}
	
	
	public static boolean getBooleanValue(Object obj) {
		
		return getBooleanOrDefault(obj, false);
		
	}
	
	
	public static int getIntOrDefault(Object obj, int defaultValue) {
		
		if (obj != null) {
			
			if (obj instanceof Integer)
				return (int) obj;
			
			if (obj instanceof Long)
				return ((Long) obj).intValue();
			
			if (obj instanceof Double)
				return ((Double) obj).intValue();
			
			if (obj instanceof Float)
				return ((Float) obj).intValue();
			
			if (obj instanceof String) {
				
				try {
					return Integer.parseInt((String) obj);
				}
				
				catch (NumberFormatException ex) { }
				
			}
			
		}
		
		return defaultValue;
		
	}
	
	
	public static int getIntValue(Object obj) {
		
		return getIntOrDefault(obj, 0);
		
	}
	
	
	public static double getDoubleOrDefault(Object obj, double defaultValue) {
		
		if (obj != null) {
			
			if (obj instanceof Double)
				return (double) obj;
			
			if (obj instanceof Float)
				return ((Float) obj).doubleValue();
			
			if (obj instanceof Integer)
				return ((Integer) obj).doubleValue();
			
			if (obj instanceof String) {
				
				try {
					return Double.parseDouble((String) obj);
				}
				
				catch (NumberFormatException ex) { }
				
			}
			
		}
		
		return defaultValue;
		
	}
	
	
	public static double getDouble(Object obj) {
		
		return getDoubleOrDefault(obj, 0);
		
	}
	
	
	public static Object belongingObject(String s) {
		
		if (s.startsWith("[") && s.endsWith("]")) {
			
			if (s.length() == 2)
				return Arrays.asList();
			
			return Arrays.asList(s.substring(1, s.length() - 1).split(", "));
			
		}
		
		try {
			return Integer.valueOf(s);
		}
		catch (NumberFormatException e) {
			try {
				return Double.valueOf(s);
			}
			catch (NumberFormatException e1) {
				try {
					return Boolean.valueOf(s);
				}
				catch (NumberFormatException e2) {
					if (s.length() == 1)
						return s.charAt(0);
				}
			}
		}
		
		return s;
		
	}

}

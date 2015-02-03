package me.micrjonas.grandtheftdiamond.util;

import java.util.Arrays;
import java.util.List;

public abstract class DataType<T> {
	
	public static final DataType<Boolean> BOOLEAN;
	public static final DataType<Character> CHARACTER;
	public static final DataType<Double> DOUBLE;
	public static final DataType<Enum<?>> ENUM;
	public static final DataType<Integer> INTEGER;
	public static final DataType<String> STRING;
	public static final DataType<List<String>> STRING_LIST;
	
	static {
		BOOLEAN = new DataType<Boolean>() {
			@Override
			public boolean isValidValue(String s) {
				try {
					Boolean.valueOf(s);
					return true;
				}
				catch (NumberFormatException ex) {
					return false;
				}
			}
			
			@Override
			public Boolean fromString(String s) {
				return Boolean.valueOf(s);
			}
		};
		
		CHARACTER = new DataType<Character>() {
			@Override
			public boolean isValidValue(String s) {
				return s.length() == 1;			
			}
			
			@Override
			public Character fromString(String s) {
				if (isValidValue(s)) {
					return s.charAt(0);
				}
				throw new NumberFormatException("For input string: \"" + s + "\"");
			}
		};
		
		DOUBLE = new DataType<Double>() {
			@Override
			public boolean isValidValue(String s) {
				try {
					Double.valueOf(s);
					return true;
				}
				catch (NumberFormatException ex) {
					return false;
				}
			}
			
			@Override
			public Double fromString(String s) {
				return Double.valueOf(s);
			}
		};
		
		ENUM = new DataType<Enum<?>>() {
			@Override
			public boolean isValidValue(String s) {
				for (int i = 0; i < s.length(); i++) {
					if (Character.isLowerCase(s.charAt(i))) {
						return false;
					}
				}
				return true;
			}
			
			@Override
			public Enum<?> fromString(String s) {
				throw new UnsupportedOperationException("Cannot use DataType#fromString(String) for Enum");
			}
		};
		
		INTEGER = new DataType<Integer>() {
			@Override
			public boolean isValidValue(String s) {
				try {
					Integer.valueOf(s);
					return true;
				}
				catch (NumberFormatException ex) {
					return false;
				}
			}
			
			@Override
			public Integer fromString(String s) {
				return Integer.valueOf(s);
			}
		};
		
		STRING = new DataType<String>() {
			@Override
			public boolean isValidValue(String s) {
				return true;
			}
			@Override
			public String fromString(String s) {
				return s;
			}
		};
		
		STRING_LIST = new DataType<List<String>>() {
			@Override
			public boolean isValidValue(String s) {
				return s != null;
			}
			
			@Override
			public List<String> fromString(String s) {
				String[] split = s.split(",");
				return Arrays.asList(split);
			}
		};
	}
	
	private DataType() { /*private constructor*/ }

	public abstract boolean isValidValue(String s);
	public abstract T fromString(String s);

}

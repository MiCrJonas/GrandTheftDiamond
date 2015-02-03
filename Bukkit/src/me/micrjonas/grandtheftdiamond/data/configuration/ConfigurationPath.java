package me.micrjonas.grandtheftdiamond.data.configuration;

import me.micrjonas.grandtheftdiamond.util.DataType;

public interface ConfigurationPath extends FilePath {
	
	/**
	 * Checks whether the passed value is valid for this path
	 * @param value The value to check
	 * @return True if {@code value} is a valid value for the path, else false
	 */
	public boolean isValidValue(String value);
	
	
	/**
	 * Returns the {@link DataType} of the path
	 * @return The {@link DataType} of the path
	 */
	public DataType<?> getType();
	
	
	/**
	 * Returns the description of the configuration path
	 * @return The path's configuration description
	 */
	public String getDescription();

}

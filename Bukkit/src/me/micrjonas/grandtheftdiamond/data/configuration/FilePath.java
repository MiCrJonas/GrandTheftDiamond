package me.micrjonas.grandtheftdiamond.data.configuration;


public interface FilePath {
	
	/**
	 * Returns the path format of a YAML configuration
	 * @return The path in a YAML configuration
	 */
	public String getPathYaml();
	
	
	/**
	 * Returns the default value of the path
	 * @return The default value of the path
	 */
	public Object getDefaultValue();

}

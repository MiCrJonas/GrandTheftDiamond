package me.micrjonas.grandtheftdiamond.updater;

/**
 * A {@link Version}'s release type
 */
public enum ReleaseType implements Comparable<ReleaseType> {

	/**
	 * Alpha release
	 */
	ALPHA,
	
	/**
	 * Beta release
	 */
	BETA,
	
	/**
	 * Pre-Release
	 */
	PRE,
	
	/**
	 * Release
	 */
	RELEASE,
	
	/**
	 * Dev build
	 */
	DEV;
	
}

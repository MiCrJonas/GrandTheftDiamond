package me.micrjonas.grandtheftdiamond.messenger;

public enum NoPermissionType {
	
	DEFAULT(""), COMMAND("Command"), CREATE("Create"), BREAK("Break"), EDIT("Edit"), OPEN("Open"), USE("Use"), USE_OBJECT("UseObject");
	
	private String path;
	
	private NoPermissionType(String path) {
		
		this.path = "noPermissions" + path;
		
	}
	
	
	public String getPath() {
		
		return path;
		
	}

}

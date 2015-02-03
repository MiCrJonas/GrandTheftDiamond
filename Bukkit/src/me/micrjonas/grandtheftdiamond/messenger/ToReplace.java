package me.micrjonas.grandtheftdiamond.messenger;

public enum ToReplace {
	
	HOUSE("%house%"),
	PLAYER("%player%");
	
	private String toReplace;
	
	private ToReplace(String toReplace) {
		
		this.toReplace = toReplace;
		
	}
	
	
	@Override
	public String toString() {
		
		return toReplace;
		
	}

}

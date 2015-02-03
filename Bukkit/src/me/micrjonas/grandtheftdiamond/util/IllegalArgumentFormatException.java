package me.micrjonas.grandtheftdiamond.util;

import java.util.Collection;
import java.util.Collections;

public class IllegalArgumentFormatException extends IllegalArgumentException {

	private static final long serialVersionUID = -8347747728628538815L;

	private final Collection<String> wrongArguments;
	
	public IllegalArgumentFormatException() {
		this((String) null);
	}
	
	public IllegalArgumentFormatException(String message) {
		super(message);
		wrongArguments = null;
	}
	
	public IllegalArgumentFormatException(Collection<String> wrongArguments) {
		if (wrongArguments != null) {
			this.wrongArguments = Collections.unmodifiableCollection(wrongArguments);
		}
		else {
			this.wrongArguments = null;
		}
	}
	
	public Collection<String> getWrongArguments() {
		return wrongArguments;
	}

}

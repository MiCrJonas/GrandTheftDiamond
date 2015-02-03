package me.micrjonas.grandtheftdiamond.util;


/**
 * An interface that marks classes with immutable objects.<br>
 * 	No {@code public} obtainable variable
 * 	(whether {@code public} access or obtainable using a {@code public} getter) is allowed to change its value.<br>
 * 	The values of an object value may change. The value's object does not need to be immutable, but it's not
 * 	allowed to change the represented object of this value
 */
public interface Immutable { }

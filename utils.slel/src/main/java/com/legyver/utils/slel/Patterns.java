package com.legyver.utils.slel;

/**
 * Patterns supported by the super-lightweight expression language
 */
public interface Patterns {
	/**
	 * The regular expression matching a SLEL variable
	 */
	String SLEL_VARIABLE =  "\\$\\{(([a-z\\.-])*)\\}";
}

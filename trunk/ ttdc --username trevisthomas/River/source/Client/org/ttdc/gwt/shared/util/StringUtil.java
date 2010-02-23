package org.ttdc.gwt.shared.util;

public class StringUtil {
	public static boolean empty(String s){
		return !notEmpty(s);
		
	}
	public static boolean notEmpty(String s){
		return s != null && s.trim().length() > 0;
	}
}

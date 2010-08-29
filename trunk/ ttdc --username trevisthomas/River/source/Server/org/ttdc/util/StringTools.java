package org.ttdc.util;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public final class StringTools {
	public static String safeSQLString(String src){
		String dest = null;
		if(src != null){
			dest = src.replace("'", "''");
		}
		return dest;
	}
	
	
	//private static List<Character> escapeCharacters = new ArrayList<Character>(Arrays.asList(new Character[]));
	private static final char[] escapeCharacters  = {'[','\\','^','$',',','|','?','*','+','(',')'};
	static{
		Arrays.sort(escapeCharacters);
	}
	public static String escapeRexExSpecialCharacters(String s){
		StringBuilder sb = new StringBuilder();
		for(int i = 0;i<s.length();i++){
			char c = s.charAt(i);
			
			if(Arrays.binarySearch(escapeCharacters, c) >= 0){
				sb.append("\\");
			}
			sb.append(c);
		}
		return sb.toString();
	}
	
	public static String unescapeRexExSpecialCharacters(String s){
		StringBuilder sb = new StringBuilder();
		char c = ' ';
		Character cOld = null;
		for(int i = 0;i<s.length();i++){
			c = s.charAt(i);
			if(cOld == null){
				cOld = c;
				continue;
			}
			
			if(cOld == '\\' && Arrays.binarySearch(escapeCharacters, c) >= 0 ){
				//ignore
			}
			else{
				sb.append(cOld);
			}
			cOld = c;
		}
		sb.append(c);
		
		return sb.toString();
		
		
	}
	
	
	
	/**
	 * Basically i strip out a's and the's
	 * @return
	 */
	private static String [] ignoredWords = {"a ","the ", "an "};
	public static String formatTitleForSort(String title){
		for(int i = 0;i < ignoredWords.length;i++){
			if(title.toLowerCase().startsWith(ignoredWords[i])){
				int index = ignoredWords[i].length();
				return title.substring(index) + ", " + title.substring(0,index).trim();
			}
		}
		return title;
	}
	
	/**
	 * Takes a date and returns a fancy string describing how long ago the date occurred
	 * 
	 * 'an instant ago'
	 * 'one minute'
	 *  
	 * Note: if the date is more than 12 hours ago i show date and time, less than that i show my fancy
	 * message	
	 * 
	 * 
	 * @param date
	 * @return
	 */
	private final static int CINDERELLA_TIME = 1000 * 60 * 60 * 12; 
	
	/*
	 * I never finished this function because i realized that i didnt need it.
	public static String prettyDate(Date date){
		SimpleDateFormat sdf = new SimpleDateFormat("EEEE MMMM d, yyyy' at 'h:mm aa");
		String str;
		if(date == null){
			return "";
		}
		long now = System.currentTimeMillis();
		if(date.getTime() > now || date.getTime() < (now - CINDERELLA_TIME)){
			return sdf.format(date);
		}
		else{
			long diff = (now - date.getTime())/1000;	
			long day_diff = diff % 86400;

			
			if(diff < 60){
				str = "An instant ago";
			}
			else if(diff < 120){
				str = "1 minute ago";
			}
			else if(diff < 3600){
				str = Math.floor( diff / 60 ) + " minutes ago";
			}
			else if(diff < 7200){
				str = "1 hour ago";
			}	
			else if(diff < 86400){
				str = Math.floor( diff / 3600 ) + " hours ago";
			}
			else if (day_diff == 1){
				str = "Yesterday";
			}
			else if(day_diff < 7){
				str = day_diff + " days ago";
			}
			else{
				str = (int)Math.ceil( day_diff / 7 ) + " weeks ago";
			}
		}
		return str;
	}
	*/
}

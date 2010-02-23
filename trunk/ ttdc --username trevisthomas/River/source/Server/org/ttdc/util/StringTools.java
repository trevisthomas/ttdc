package org.ttdc.util;

public final class StringTools {
	public static String safeSQLString(String src){
		String dest = null;
		if(src != null){
			dest = src.replace("'", "''");
		}
		return dest;
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

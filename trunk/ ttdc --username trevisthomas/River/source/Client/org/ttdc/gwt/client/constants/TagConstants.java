package org.ttdc.gwt.client.constants;

public interface TagConstants {
	public final static String TYPE_TOPIC = "TOPIC";
	public final static String TYPE_DISPLAY = "DISPLAY";
	public final static String TYPE_RATING = "RATING";
	public final static String TYPE_AVERAGE_RATING = "AVERAGE_RATING";
	
	
	public final static String TYPE_TITLE = "TITLE"; //Title is on all root posts. So if a post has a title it's a thread.
	
	public final static String TYPE_CREATOR = "CREATOR"; 
	/* The DATE types are auto created tags used for awesome tag browsing functionality
	 * 
	 * Current thinking is that i will create the date tags as posts are added.  So when a post 
	 * is added i look for a tag type DATE_YEAR (if not found create) and then i tag the new post with it.
	 * I then do the same for MONTH and DAY.
	 * 
	 * Post has a Date field which is used for sorting, these tags are used for browsing
	 *
	 */
	
	public final static String TYPE_DATE_YEAR = "DATE_YEAR"; //Check and create when need since the set grows over time
	public final static String TYPE_DATE_MONTH = "DATE_MONTH"; //(Should probably pre-create all of these since the set is fixed)
	public final static String TYPE_DATE_DAY = "DATE_DAY"; //Precreate for same reason above
	
	public final static String TYPE_STATUS = "STATUS"; //Values: LOCKED 
	public final static String TYPE_VISIBILITY = "VISIBILITY"; //Values: TRUSTED, ADMIN
	
	//public final static String TYPE_TITLE = "TITLE"; //Title is on all root posts. So if a post has a title it's a thread.
	public final static String TYPE_SORT_TITLE = "SORT_TITLE"; 
	public final static String TYPE_REVIEW = "REVIEW";
	public final static String TYPE_MOVIE = "MOVIE";
	public final static String TYPE_RELEASE_YEAR = "RELEASE_YEAR"; //Initially for movies. Because the year it came out is often different from the year i added it
	public final static String TYPE_LEGACY_THREAD = "LEGACY_THREAD";
		
	public final static String TYPE_RATABLE = "RATABLE";//This tag
	public final static String TYPE_EARMARK = "EARMARK";//Authenticated users can ear mark a post so that they can find it later. Value of this tag type should be the creator's guid
	public final static String TYPE_LIKE = "LIKE";
	
	public final static String TYPE_URL = "URL"; //Initially for imdb links to movies but could be used for lots of things
	public final static String VALUE_RATING_5 = "5.0";
	public final static String VALUE_RATING_4_5 = "4.5";
	public final static String VALUE_RATING_4 = "4.0";
	public final static String VALUE_RATING_3_5 = "3.5";
	public final static String VALUE_RATING_3 = "3.0";
	public final static String VALUE_RATING_2_5 = "2.5";
	public final static String VALUE_RATING_2 = "2.0";
	public final static String VALUE_RATING_1_5 = "1.5";
	public final static String VALUE_RATING_1 = "1.0";
	public final static String VALUE_RATING_0_5 = "0.5";
	public final static String VALUE_RATING_0 = "0.0"; //Not sure about this one
	
	public final static String VALUE_NWS = "NWS";
	public final static String VALUE_INF = "INF";
	public final static String VALUE_PRIVATE = "PRIVATE";
	public final static String VALUE_LOCKED = "LOCKED";  //This is intended for root posts to lock a thread
	public final static String VALUE_DELETED = "DELETED"; //Once tagged as deleted the post wont show up for anyone (maybe admin will still see)
	
	
	public final static String VALUE_LINK = "LINK";//Tag for posts with links in them.
	public static final String TYPE_WEEK_OF_YEAR = "WEEK_OF_YEAR";
}

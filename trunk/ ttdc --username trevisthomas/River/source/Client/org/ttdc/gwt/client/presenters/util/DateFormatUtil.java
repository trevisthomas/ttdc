package org.ttdc.gwt.client.presenters.util;

import java.util.Date;

import com.google.gwt.i18n.client.DateTimeFormat;

public class DateFormatUtil {
	public static final DateTimeFormat longDateFormatter = DateTimeFormat.getFormat("EEE, MMM d, yyyyy 'at' HH:mm:ss a");
	public static final DateTimeFormat longDayFormatter = DateTimeFormat.getFormat("EEEE, MMM d, yyyy");
	public static final DateTimeFormat timeFormatter = DateTimeFormat.getFormat("hh:mm:ss a");
	public static final DateTimeFormat shortTimeFormatter = DateTimeFormat.getFormat("h:mm:ss");
	
	public static String formatLongDate(Date date){
		return longDateFormatter.format(date);
	}
	
	public static String formatLongDay(Date date){
		return longDayFormatter.format(date);
	}
	
	public static String formatTime(Date date){
		return timeFormatter.format(date);
	}
	
}	

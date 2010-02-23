package org.ttdc.gwt.client.presenters.util;

import java.util.Date;

import com.google.gwt.i18n.client.DateTimeFormat;

public class DateFormatUtil {
	private static DateTimeFormat longDateFormatter = DateTimeFormat.getFormat("EEE, MMM d, yyyyy 'at' HH:mm:ss a");
	private static DateTimeFormat longDayFormatter = DateTimeFormat.getFormat("EEEE, MMM d, yyyy");
	
	public static String formatLongDate(Date date){
		return longDateFormatter.format(date);
	}
	
	public static String formatLongDay(Date date){
		return longDayFormatter.format(date);
	}
}	

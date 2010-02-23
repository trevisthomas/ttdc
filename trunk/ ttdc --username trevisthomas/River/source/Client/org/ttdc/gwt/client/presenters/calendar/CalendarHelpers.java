package org.ttdc.gwt.client.presenters.calendar;

import org.ttdc.gwt.client.messaging.history.HistoryConstants;
import org.ttdc.gwt.client.messaging.history.HistoryToken;

public final class CalendarHelpers {
	public static final String[] DAY_NAMES = {"Sunday","Monday","Tuesday","Wednesday","Thursday","Friday","Saturday"};
	public static final String[] DAY_ABBREVIATIONS = {"S","M","T","W","T","F","S"};
	public static final String[] MonthNames = {"January","February","March","April","May","June","July","August","September","October","November","December"};
	public static final String[] MONTH_ABBREVIATIONS = {"Jan","Feb","Mar","Apr","May","Jun","Jul","Aug","Sep","Oct","Nov","Dec"};
	
	/**
	 * 
	 * @param dayOfWeek 1 ~ 7
	 * @return string name of day
	 */
	public static String getDayName(int dayOfWeek){
		return DAY_NAMES[dayOfWeek-1];
	}
	public static String getDayAbbreviation(int dayOfWeek){
		return DAY_ABBREVIATIONS[dayOfWeek-1];
	}
	public static String getMonthName(int monthOfYear){
		return MonthNames[monthOfYear-1];
	}
	
	public static HistoryToken buildDayHistoryToken(int yr, int mo, int dd){
		HistoryToken token = new HistoryToken();
		token.setParameter(HistoryConstants.VIEW, HistoryConstants.VIEW_CALENDAR);
		token.setParameter(HistoryConstants.CALENDAR_SCALE_KEY, HistoryConstants.CALENDAR_SCALE_VALUE_DAY);
		token.setParameter(HistoryConstants.CALENDAR_MONTH_OF_YEAR, mo);
		token.setParameter(HistoryConstants.CALENDAR_DAY_OF_MONTH, dd);
		token.setParameter(HistoryConstants.CALENDAR_YEAR, yr);
		return token;
	}
	
	public static HistoryToken buildWeekHistoryToken(int yr, int weekOfYear){
		HistoryToken token = new HistoryToken();
		token.setParameter(HistoryConstants.VIEW, HistoryConstants.VIEW_CALENDAR);
		token.setParameter(HistoryConstants.CALENDAR_SCALE_KEY, HistoryConstants.CALENDAR_SCALE_VALUE_WEEK);
		token.setParameter(HistoryConstants.CALENDAR_WEEK_YEAR, weekOfYear);
		token.setParameter(HistoryConstants.CALENDAR_YEAR, yr);
		return token;
	}
	
	public static HistoryToken buildMonthHistoryToken(int yr, int monthOfYear){
		HistoryToken token = new HistoryToken();
		token.setParameter(HistoryConstants.VIEW, HistoryConstants.VIEW_CALENDAR);
		token.setParameter(HistoryConstants.CALENDAR_SCALE_KEY, HistoryConstants.CALENDAR_SCALE_VALUE_MONTH);
		token.setParameter(HistoryConstants.CALENDAR_MONTH_OF_YEAR, monthOfYear);
		token.setParameter(HistoryConstants.CALENDAR_YEAR, yr);
		return token;
	}
	public static HistoryToken buildYearHistoryToken(int yr){
		HistoryToken token = new HistoryToken();
		token.setParameter(HistoryConstants.VIEW, HistoryConstants.VIEW_CALENDAR);
		token.setParameter(HistoryConstants.CALENDAR_SCALE_KEY, HistoryConstants.CALENDAR_SCALE_VALUE_YEAR);
		token.setParameter(HistoryConstants.CALENDAR_YEAR, yr);
		return token;
	}
}

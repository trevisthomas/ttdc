package org.ttdc.gwt.server.util;

import java.util.Calendar;
import java.util.GregorianCalendar;

import org.ttdc.gwt.shared.calender.Day;
import org.ttdc.gwt.shared.calender.DayProxy;
import org.ttdc.gwt.shared.calender.Month;
import org.ttdc.gwt.shared.calender.Week;
import org.ttdc.gwt.shared.calender.Year;

public class CalendarBuilder {
	private static String[] MonthNames = {"January","February","March",
        "April","May","June",
        "July","August","September",
        "October","November","December"};
	
	private static final int[] DaysPerMonth = {31,99,31,30,31,30,31,31,30,31,30,31};
	
	private final DayProxy dayProxy;
	private Calendar today = null;
	
	private int monthToday;
    private int yearToday;
    private int dayToday;
	
	public CalendarBuilder(DayProxy dayProxy){
		this.dayProxy = dayProxy;
	}
	
	public Year buildYear(int yearNumber){
		setup();
		Year year = new Year();
		year.setYearNumber(yearNumber);
		for(int mo = 1 ; mo <= 12 ; mo++){
			Month month = buildMonthObject(mo,yearNumber);
			year.setMonth(mo, month);
		}
		return year;
	}

	/**
	 * Method getMonthName.  Returns the name of the month.  1-12!!
	 * @param month
	 * @return String
	 */
	public static String getMonthName(int month){
    	return MonthNames[month-1];
    }
	
	public Month buildMonth(int yearNumber, int monthNumber){
		setup();
		Month month = buildMonthObject(monthNumber,yearNumber);
		return month;
	}
	
	
	public Week buildWeek(int yearNumber, int weekOfYear){
		setup();
    	Calendar cal = GregorianCalendar.getInstance();
    	cal.set(Calendar.YEAR, yearNumber);
    	cal.set(Calendar.WEEK_OF_YEAR, weekOfYear);
    	cal.set(Calendar.DAY_OF_WEEK, Calendar.SUNDAY);
    	
    	Week week = new Week();
    	week.setWeekOfYear(weekOfYear);
    	week.setYear(yearNumber);
    	
    	for(int dayOfWeek = 1; dayOfWeek<8; dayOfWeek++){
    		Day day = createDay(cal.get(Calendar.YEAR),cal.get(Calendar.MONTH)+1,cal.get(Calendar.DAY_OF_MONTH));
    		day.setVisable(true);
    		dayProxy.fill(day);
    		week.add(day);
    		cal.roll(Calendar.DAY_OF_WEEK, true);
    	}
    	return week;
    }
	
	public Day buildDay(int yearNumber, int monthOfYear, int dayOfMonth){
		setup();
		Day day = createDay(yearNumber, monthOfYear, dayOfMonth);
		day.setVisable(true);
		dayProxy.fill(day);
		return day;
	}
	
	
	private void setup() {
		today = Calendar.getInstance();
		monthToday = today.get(Calendar.MONTH) + 1;
	    yearToday = today.get(Calendar.YEAR);
	    dayToday = today.get(Calendar.DAY_OF_MONTH);
	}
	/**
	 * Method LeapYear.
	 * @param year
	 * @return boolean
	 */
    private boolean LeapYear(int year) {
        if ((year/4.0)   != Math.floor((double)year/4.0))   return false;
        if ((year/100.0) != Math.floor((double)year/100.0)) return true;
        if ((year/400.0) != Math.floor((double)year/400.0)) return false;
        return true;
    }
    
    private Month buildMonthObject(int mo, int yr){
	    
	    
    	 Month m = new Month();
         
         //fix the days per month for leap years
         if (LeapYear(yr))
             DaysPerMonth[1] = 29;
         else
             DaysPerMonth[1] = 28;
         
         //Calculate the day of the week for the first day of the month! 
         Calendar cal = new GregorianCalendar();
         cal.set(yr, mo-1, 1, 0, 0, 0);
         
         int dayOfFirst = cal.get(Calendar.DAY_OF_WEEK);
         int DayOfMonth = 1 - dayOfFirst;
         int DayOfWeekCounter = 0;       
         
         //Begining of month
         m.setName(MonthNames[mo-1]);
         m.setMonthNumber(mo);
         m.setYearNumber(yr);
         int weekOfYear = determineWeekOfYear(yr,mo,1);
         while (DayOfMonth <= DaysPerMonth[mo - 1] - 1) {
			// StartWeek
			Week w = new Week();
			w.setWeekOfYear(weekOfYear);
			weekOfYear++;
			do {
				Day day = null;
				DayOfMonth++;
				DayOfWeekCounter++;
				if (DayOfMonth > 0 & DayOfMonth <= DaysPerMonth[mo - 1]) {
					day = createDay(yr, mo, DayOfMonth);

					day.setVisable(true);
					day.setDay(DayOfMonth);
					
					Calendar thisDay = createCalendar(day);
					if (thisDay.after(today)) 
						day.setFuture(true);

					if (day.isVisable() && !day.isFuture())
						dayProxy.fill(day);
				} else {
					day = new Day();
					day.setVisable(false);
				}

				w.add(day);
			} while (DayOfWeekCounter % 7 != 0);
			// EndWeek
			m.add(w);
		}   
         // End of Month
         return m;
    }
    
    private Calendar createCalendar(Day day){
    	Calendar tempCal = Calendar.getInstance();
        tempCal.set(day.getYear(),day.getMonth()-1,day.getDay(),0,0,0);
        return tempCal;
    }
    
    private Day createDay(int yr, int mo, int dayOfMonth){
    	Day day = new Day();
    	day.setYear(yr);
    	day.setMonth(mo);
    	day.setDay(dayOfMonth);
    	
    	if (dayOfMonth == dayToday && mo == monthToday && yr == yearToday)
			day.setToday(true);
    	
    	return day;
    }
    
    private int determineWeekOfYear(int yr, int mo, int dayOfMonth){
    	Calendar cal = GregorianCalendar.getInstance();
    	cal.set(Calendar.YEAR, yr);
    	cal.set(Calendar.MONTH, mo-1);
    	cal.set(Calendar.DAY_OF_MONTH, dayOfMonth);
    	return cal.get(Calendar.WEEK_OF_YEAR);
    }

}

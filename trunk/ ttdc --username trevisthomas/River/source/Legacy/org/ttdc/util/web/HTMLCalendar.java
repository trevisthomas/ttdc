package org.ttdc.util.web;

import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.List;

/**
 * @author Trevis
 *
 * Created on September 19, 2002, 6:16 PM
 * HTMLCalendar.java
 */
public class HTMLCalendar{
    static public String[] MonthNames = {"January","February","March",
                                  "April","May","June",
                                  "July","August","September",
                                  "October","November","December"};

    static int[] DaysPerMonth = {31,99,31,30,31,30,31,31,30,31,30,31};
    
	/**
	 * Method getMonthName.  Returns the name of the month.  1-12!!
	 * @param month
	 * @return String
	 */
    static public String getMonthName(int month){
    	return MonthNames[month-1];
    }
    
    
    
    /**
	 * Method buildMonth.
	 * @param Month
	 * @param Year
	 * @param days
	 * @return String
	 */
    static public String buildMonth(int Month,int Year,List<Integer> days){
		return buildMonth(Month,Year,days,true);
    }
    
	/**
	 * Method buildMonth.
	 * @param Month
	 * @param Year
	 * @param days
	 * @param showtitle  - if true month title is displayed.
	 * @return String
	 */
    static public String buildMonth(int Month,int Year,List<Integer> days,boolean showtitle){
    	Calendar rightNow = Calendar.getInstance();
	    int monthToday = rightNow.get(Calendar.MONTH) + 1;
	    int yearToday = rightNow.get(Calendar.YEAR);
	    int dayToday = rightNow.get(Calendar.DAY_OF_MONTH);
    	
        StringBuffer buffer = new StringBuffer();
        
        //fix the days per month for leap years
        if (LeapYear(Year))
            DaysPerMonth[1] = 29;
        else
            DaysPerMonth[1] = 28;
        //Calculate the day of the week for the first day of the month! 
        Calendar cal = new GregorianCalendar();
        cal.set(Calendar.MONTH,Month-1);
        cal.set(Calendar.YEAR,Year);
        cal.set(Calendar.DAY_OF_MONTH,1);
        int dayOfFirst = cal.get(Calendar.DAY_OF_WEEK);
        int DayOfMonth = 1 - dayOfFirst;
        int DayOfWeekCounter = 0;       
        //Begining of month
        buffer.append("<table class=\"month\">");
        if(showtitle){
	        buffer.append("<tr><th colspan=\"7\">"+MonthNames[Month-1]+"</th>\n</tr>");
	    }
        
        while(DayOfMonth <= DaysPerMonth[Month-1]-1){
            //StartWeek
            buffer.append("\n<tr>");
            do{
                DayOfMonth++; 
                DayOfWeekCounter++;
                if(DayOfMonth > 0 & DayOfMonth <= DaysPerMonth[Month-1]){
                	if( DayOfMonth == dayToday && Month == monthToday && Year == yearToday)
                		buffer.append("<td class=\"todayOnCal\">");
                	else
                		buffer.append("<td>");
                		
                    if(days.indexOf(DayOfMonth) == -1){
                        buffer.append(DayOfMonth);
                    }
                    else
                    {//Link it!
                        buffer.append("<a href=\"History.do?forDate="+Month+"/"+DayOfMonth+"/"+Year+"\">"+DayOfMonth+"</a>");
                    }
                   	buffer.append("</td>");
                	
                }
                else{
                    buffer.append("<td class=\"empty\">&nbsp;</td>");
                }
            }while(DayOfWeekCounter%7 != 0);
            //EndWeek
            buffer.append("\n</tr>");
        }   
        //End of Month
        buffer.append("\n</table>");
        return buffer.toString();
    }	


	/**
	 * Method LeapYear.
	 * @param year
	 * @return boolean
	 */
    private static boolean LeapYear(int year) {
        if ((year/4.0)   != Math.floor((double)year/4.0))   return false;
        if ((year/100.0) != Math.floor((double)year/100.0)) return true;
        if ((year/400.0) != Math.floor((double)year/400.0)) return false;
        return true;
    }
    
    static public Month buildMonthObject(int Month,int Year, Calendar rightNow, List<Integer> days,boolean showtitle){
    	//Calendar rightNow = Calendar.getInstance();
	    int monthToday = rightNow.get(Calendar.MONTH) + 1;
	    int yearToday = rightNow.get(Calendar.YEAR);
	    int dayToday = rightNow.get(Calendar.DAY_OF_MONTH);
	    
    	 Month m = new Month();
         
         //fix the days per month for leap years
         if (LeapYear(Year))
             DaysPerMonth[1] = 29;
         else
             DaysPerMonth[1] = 28;
         //Calculate the day of the week for the first day of the month! 
         Calendar cal = new GregorianCalendar();
         cal.set(Calendar.MONTH,Month-1);
         cal.set(Calendar.YEAR,Year);
         cal.set(Calendar.DAY_OF_MONTH,1);
         int dayOfFirst = cal.get(Calendar.DAY_OF_WEEK);
         int DayOfMonth = 1 - dayOfFirst;
         int DayOfWeekCounter = 0;       
         
         //Begining of month
         m.setName(MonthNames[Month-1]);
         m.setMonth(Month);
         m.setYear(Year);
         
         while(DayOfMonth <= DaysPerMonth[Month-1]-1){
             //StartWeek
         	Month.Week w = new Month.Week();
         	Month.Week.Day day = null;
             do{
                 DayOfMonth++; 
                 DayOfWeekCounter++;
                 day = new Month.Week.Day();
                 if(DayOfMonth > 0 & DayOfMonth <= DaysPerMonth[Month-1]){
                 	day.setVisable(true);
                 	day.setDay(DayOfMonth);
                 	if( DayOfMonth == dayToday && Month == monthToday && Year == yearToday)
                 		day.setToday(true);
                 		
                     if(days.indexOf(DayOfMonth) != -1){
                         day.setContent(true);
                     }
                 }
                 else{
                 	day.setVisable(false);
                 }
                 Calendar tempCal = Calendar.getInstance();
                 tempCal.set(Year,Month-1,DayOfMonth);
                 if(tempCal.after(rightNow)){
                 	day.setFuture(true);
                 }
                 w.add(day);
             }while(DayOfWeekCounter%7 != 0);
             //EndWeek
             m.add(w);
         }   
         //End of Month
         return m;
    }
    
    /**
     * 
     * @param Month
     * @param Year
     * @param days
     * @param showtitle
     * @return
     */
    static public Month buildMonthObject(int Month,int Year,List<Integer> days,boolean showtitle){
    	Calendar rightNow = Calendar.getInstance();
    	return buildMonthObject(Month, Year, rightNow, days, showtitle);
       
    }	
    
    static public Month buildMonthObject(int month,int year, int day, List<Integer> days,boolean showtitle){
    	Calendar rightNow = Calendar.getInstance();
    	rightNow.set(year, month - 1, day);
    	return buildMonthObject(month, year, rightNow, days, showtitle);
       
    }
    
    
}
    


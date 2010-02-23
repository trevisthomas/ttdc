package org.ttdc.gwt.server.util;
import static junit.framework.Assert.*;

import java.util.Calendar;

import org.apache.log4j.Logger;
import org.junit.Before;
import org.junit.Test;
import org.ttdc.gwt.shared.calender.Day;
import org.ttdc.gwt.shared.calender.DayProxy;
import org.ttdc.gwt.shared.calender.Month;
import org.ttdc.gwt.shared.calender.Week;
import org.ttdc.gwt.shared.calender.Year;



public class CalendarBuilderTest {
	private final static Logger log = Logger.getLogger(CalendarBuilderTest.class);
	
	int d;
	int yr;
	int mo;
	
	@Before
	public void startup(){
		Calendar today = Calendar.getInstance();
		d = today.get(Calendar.DAY_OF_MONTH);
		yr = today.get(Calendar.YEAR);
		mo = today.get(Calendar.MONTH)+1;
	}
	
	@Test
	public void testYear(){
		log.debug("Building year calendar.");
		CalendarBuilder cb = new CalendarBuilder(new DayProxy(){
			@Override
			public void fill(Day day) {
				log.debug(day.toString());
				if(day.getMonth() != mo && day.getDay() != d){
					assertFalse("Today flag is not set correctly",day.isToday());
				}
			}
		});
		
		int y = 2009;
		
		Year year = cb.buildYear(y);
		
		assertTrue("Year not set", year.getYearNumber() == y);
		assertTrue("December missing", year.getMonth(12).getName() == "December");
		assertEquals("week of year is wrong.",49, year.getMonth(12).getWeeks().get(0).getWeekOfYear());
		
	}
	
	@Test
	public void testWeek(){
		log.debug("Building week calendar.");
		CalendarBuilder cb = new CalendarBuilder(new MyDayProx());
		
		Week week = cb.buildWeek(2009,49);
		
		assertEquals("Week of year not correct", 49, week.getWeekOfYear());
		assertEquals("Month should be November missing", 11, week.getDays().get(0).getMonth());
		assertEquals("Sunday is wrong", 29,week.getDays().get(0).getDay());
	}
	
	@Test
	public void buildMonth(){
		log.debug("Building month calendar.");
		CalendarBuilder cb = new CalendarBuilder(new MyDayProx());
		
		int y = 2009;
		int m = 11;
		
		Month month = cb.buildMonth(y,m);
		
		assertEquals(30,month.getWeeks().get(4).getDays().get(1).getDay());
		
	}
	
	@Test
	public void testBuildDay() {
		log.debug("Building day calendar.");
		
		int y = 2009;
		int m = 11;
		int d = 13;
		
		CalendarBuilder cb = new CalendarBuilder(new MyDayProx());
		
		Day day = cb.buildDay(y,m,d);
		
		assertEquals(d,day.getDay());
	}
	
	class MyDayProx implements DayProxy{
		@Override
		public void fill(Day day) {
			log.debug(day.toString());
			if(!(day.getMonth() == mo && day.getDay() == d)){
				assertFalse("Today flag is not set correctly",day.isToday());
			}
		}
	}
}

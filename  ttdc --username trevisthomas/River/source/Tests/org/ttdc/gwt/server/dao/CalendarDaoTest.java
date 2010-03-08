package org.ttdc.gwt.server.dao;


import org.junit.Test;
import org.ttdc.gwt.shared.calender.Day;
import org.ttdc.gwt.shared.calender.Month;
import org.ttdc.gwt.shared.calender.Week;
import org.ttdc.gwt.shared.calender.Year;

import static junit.framework.Assert.*;
import static org.ttdc.persistence.Persistence.*;
public class CalendarDaoTest {
	
	@Test
	public void testYear(){
		CalendarDao dao = new CalendarDao();
		int y = 2008;
		dao.setYearNumber(y);
		beginSession();
		Year year = dao.buildYear();
		commit();
		assertTrue("Year not set", year.getYearNumber() == y);
		assertTrue("December missing", year.getMonth(12).getName() == "December");
		assertEquals("week of year is wrong.",49, year.getMonth(12).getWeeks().get(0).getWeekOfYear());
		
		Day d = year.getMonth(4).getWeeks().get(0).getDays().get(2);
		assertEquals("Not the day i expectd", 1,d.getDay());
		assertEquals("Not the month i expected", 4,d.getMonth());
		assertFalse("No content on 4/1/2008",d.isContent());
		
		d = year.getMonth(5).getWeeks().get(0).getDays().get(4);
		assertEquals("Not the day i expectd",1,d.getDay());
		assertEquals("Not the month i expected",5,d.getMonth());
		assertTrue("Content content on 5/2/2008",d.isContent());
		
	}
	
	@Test
	public void testWeek(){
		CalendarDao dao = new CalendarDao();
		int y = 2009;
		dao.setYearNumber(y);
		dao.setWeekOfYear(26); 
		
		beginSession();
		
		Week week = dao.buildWeek();
		
		assertEquals("Test hour didnt have the expected number of posts",7,week.getDays().get(0).getHour(19).getPosts().size());
		commit();
	}
	
	@Test
	public void testWeekWithEmptyDay(){
		CalendarDao dao = new CalendarDao();
		int y = 2008;
		dao.setYearNumber(y);
		dao.setWeekOfYear(1); 
		
		beginSession();
		
		Week week = dao.buildWeek();
		
		assertEquals(0, week.getDays().get(3).getHours().size());
		commit();
		
	}
	
	/*
	 *  Remember! You had a bug in the time conversion because timezones werent being properly calculated
	 *  when you were playing with daylight savings.  The month calendar was broken because of it and 
	 *  this test didnt see the problem.   You just didnt have any data on the rendered calendar. 3/2006 
	 *  did show the problem. The date range class now has a better converter that should compensate for that.
	 * 
	 */
	@Test
	public void testMonth(){
		CalendarDao dao = new CalendarDao();
		int y = 2008;
		int mo = 1;
		dao.setYearNumber(y);
		dao.setMonthOfYear(mo); 
		
		beginSession();
		Month month = dao.buildMonth();
		commit();
		
		assertEquals(21,month.getWeeks().get(3).getDays().get(1).getDay());
		
		Day d = month.getWeeks().get(3).getDays().get(5);
		assertEquals("Wrong day at index",25,d.getDay());
		assertEquals("Jan 25 2008 doesnt have the expected number of active threads",18,d.getThreads().size());
		
		
		//assertEquals("Month should be November missing", 11, week.getDays().get(0).getMonth());
		//assertEquals("Sunday is wrong", 29,week.getDays().get(0).getDay());
	}
	
	
	
	@Test
	public void testSimpleMonth(){
		CalendarDao dao = new CalendarDao();
		int y = 2008;
		int mo = 1;
		dao.setYearNumber(y);
		dao.setMonthOfYear(mo); 
		
		beginSession();
		Month month = dao.buildSimpleMonth();
		commit();
		
		assertEquals(21,month.getWeeks().get(3).getDays().get(1).getDay());
		
		Day d = month.getWeeks().get(3).getDays().get(5);
		assertEquals("Wrong day at index",25,d.getDay());
		assertNull("Simple month's day should not hvae threads", d.getThreads());
	}
	
	@Test
	public void testDay(){
		CalendarDao dao = new CalendarDao();
		int y = 2008;
		int mo = 1;
		int d = 1;
		
		dao.setYearNumber(y);
		dao.setMonthOfYear(mo); 
		dao.setDayOfMonth(d);
		
		beginSession();
		Day day = dao.buildDay();
		commit();
		
		assertEquals(d,day.getDay());
		assertEquals(mo,day.getMonth());
		assertEquals(y,day.getYear());
		
		//2 posts at 1 am new years day 2008
		assertEquals(2,day.getHour(1).getPosts().size());
		
		
	}
}

package org.ttdc.gwt.server.command;

import static org.junit.Assert.*;

import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.List;

import org.junit.Test;
import org.ttdc.gwt.server.command.executors.CalendarCommandExecutor;
import org.ttdc.gwt.server.dao.Helpers;
import org.ttdc.gwt.shared.commands.results.CalendarCommandResult;

import org.ttdc.gwt.shared.calender.Day;
import org.ttdc.gwt.shared.calender.Month;
import org.ttdc.gwt.shared.calender.Week;
import org.ttdc.gwt.shared.calender.Year;
import org.ttdc.gwt.shared.commands.CalendarCommand;


public class CalendarCommandTest {
	@Test
	public void testYear(){
		int y = 2008;
		CalendarCommand cmd = new CalendarCommand();
		cmd.setYear(y);
		cmd.setScope(CalendarCommand.Scope.YEAR);
		
		CommandExecutor cmdexec = CommandExecutorFactory.createExecutor("50E7F601-71FD-40BD-9517-9699DDA611D6",cmd);
		assertTrue("Factory returned the wrong implementation", cmdexec instanceof CalendarCommandExecutor);
		CalendarCommandResult result = (CalendarCommandResult)cmdexec.execute();
		Year year = result.getYear();
		
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
		
//		List<String> tagIds = result.getCalendarTagIdList();
//		Helpers.assertEqualsOneOfExpected(tagIds, "C3A03265-D406-4701-84F2-9782E60B7CC1"); //2008
		
		Calendar cal = new GregorianCalendar();
		cal.set(y, 0, 1, 0, 0, 0);
		assertEquals("Year start date is wrong",cal.getTime().toString(),result.getStartDate().toString());
		cal = new GregorianCalendar();
		cal.set(y+1, 0, 1, 0, 0, 0);		
		assertEquals("Year end date is wrong",cal.getTime().toString(),result.getEndDate().toString());
		
		assertEquals(2009, result.getNextYear());
		assertEquals(2007, result.getPrevYear());
		
		assertEquals(2008, result.getRelevantYear());
		assertEquals(1, result.getRelevantDayOfMonth());
		assertEquals(1, result.getRelevantWeekOfYear());
		assertEquals(1, result.getRelevantMonthOfYear());
		
	}
	
	@Test
	public void testMonth(){
		int y = 2008;
		int mo = 1;
		
		CalendarCommand cmd = new CalendarCommand();
		cmd.setYear(y);
		cmd.setMonthOfYear(mo);
		cmd.setScope(CalendarCommand.Scope.MONTH);
		
		CommandExecutor cmdexec = CommandExecutorFactory.createExecutor("50E7F601-71FD-40BD-9517-9699DDA611D6",cmd);
		assertTrue("Factory returned the wrong implementation", cmdexec instanceof CalendarCommandExecutor);
		CalendarCommandResult result = (CalendarCommandResult)cmdexec.execute();
		
		Month month = result.getMonth();
		
		assertEquals(21,month.getWeeks().get(3).getDays().get(1).getDay());
		
		Day d = month.getWeeks().get(3).getDays().get(5);
		assertEquals("Wrong day at index",25,d.getDay());
		assertEquals("Jan 25 2008 doesnt have the expected number of active threads",18,d.getThreads().size());
		
//		List<String> tagIds = result.getCalendarTagIdList();
//		Helpers.assertEqualsOneOfExpected(tagIds, "C3A03265-D406-4701-84F2-9782E60B7CC1"); //2008
//		Helpers.assertEqualsOneOfExpected(tagIds, "FDE20875-75E1-4D12-AF2B-BB17A83DB319"); //Month Jan
		
		Calendar cal = new GregorianCalendar();
		cal.set(y, mo-1, 1, 0, 0, 0);
		assertEquals("Month start date is wrong",cal.getTime().toString(),result.getStartDate().toString());
		cal = new GregorianCalendar();
		cal.set(y, mo, 1, 0, 0, 0);
		assertEquals("Month end date is wrong",cal.getTime().toString(),result.getEndDate().toString());
		
		assertEquals(2008, result.getNextYear());
		assertEquals(2007, result.getPrevYear());
		assertEquals(2, result.getNextMonthOfYear());
		assertEquals(12, result.getPrevMonthOfYear());
		
		assertEquals(2008, result.getRelevantYear());
		assertEquals(1, result.getRelevantDayOfMonth());
		assertEquals(1, result.getRelevantWeekOfYear());
		assertEquals(1, result.getRelevantMonthOfYear());
	}
	
	@Test
	public void testWeek(){
		int y = 2009;
		CalendarCommand cmd = new CalendarCommand();
		cmd.setYear(y);
		cmd.setWeekOfYear(26);
		cmd.setScope(CalendarCommand.Scope.WEEK);
		
		CommandExecutor cmdexec = CommandExecutorFactory.createExecutor("50E7F601-71FD-40BD-9517-9699DDA611D6",cmd);
		assertTrue("Factory returned the wrong implementation", cmdexec instanceof CalendarCommandExecutor);
		CalendarCommandResult result = (CalendarCommandResult)cmdexec.execute();
		
		Week week = result.getWeek();
		
		assertEquals("Test hour didnt have the expected number of posts",7,week.getDays().get(0).getHour(19).getPosts().size());
		
		List<String> tagIds = result.getCalendarTagIdList();
//		Helpers.assertEqualsOneOfExpected(tagIds, "F4EA1844-EFBB-4903-8806-0AB1BF8B9E3D"); //2009
//		Helpers.assertEqualsOneOfExpected(tagIds, "A9D6F999-AB8E-4EFE-84B1-F74CD4A75A7D"); //Week 26
		
		Calendar cal = new GregorianCalendar();
		cal.set(y, 6-1, 21, 0, 0, 0);
		assertEquals("Week start date is wrong",cal.getTime().toString(),result.getStartDate().toString());
		cal = new GregorianCalendar();
		cal.set(y, 6-1, 28, 0, 0, 0);
		assertEquals("Week end date is wrong",cal.getTime().toString(),result.getEndDate().toString());
		
		assertEquals(2009, result.getNextYear());
		assertEquals(2009, result.getPrevYear());
		assertEquals(27, result.getNextWeekOfYear());
		assertEquals(25, result.getPrevWeekOfYear());
		
		assertEquals(2009, result.getRelevantYear());
		assertEquals(21, result.getRelevantDayOfMonth());
		assertEquals(26, result.getRelevantWeekOfYear());
		assertEquals(6, result.getRelevantMonthOfYear());
		
	}
	
	
	
	
	
	
	@Test
	public void testDay(){
		int y = 2008;
		int mo = 1;
		int d = 1;
		
		CalendarCommand cmd = new CalendarCommand();
		cmd.setYear(y);
		cmd.setMonthOfYear(mo);
		cmd.setDayOfMonth(d);
		cmd.setScope(CalendarCommand.Scope.DAY);
		
		CommandExecutor cmdexec = CommandExecutorFactory.createExecutor("50E7F601-71FD-40BD-9517-9699DDA611D6",cmd);
		assertTrue("Factory returned the wrong implementation", cmdexec instanceof CalendarCommandExecutor);
		CalendarCommandResult result = (CalendarCommandResult)cmdexec.execute();
		
		Day day = result.getDay();
		
		assertEquals(d,day.getDay());
		assertEquals(mo,day.getMonth());
		assertEquals(y,day.getYear());
		
		//2 posts at 1 am new years day 2008
		assertEquals(2,day.getHour(1).getPosts().size());
//		List<String> tagIds = result.getCalendarTagIdList();
//		Helpers.assertEqualsOneOfExpected(tagIds, "C3A03265-D406-4701-84F2-9782E60B7CC1"); //2008
//		Helpers.assertEqualsOneOfExpected(tagIds, "FDE20875-75E1-4D12-AF2B-BB17A83DB319"); //Month Jan
//		Helpers.assertEqualsOneOfExpected(tagIds, "0DC3E731-470D-4F13-AF8B-4C9768C24ED6"); //Day 1
		
		Calendar cal = new GregorianCalendar();
		cal.set(y, mo-1, d, 0, 0, 0);
		assertEquals("Day start date is wrong",cal.getTime().toString(),result.getStartDate().toString());
		cal = new GregorianCalendar();
		cal.set(y, mo-1, d+1, 0, 0, 0);
		assertEquals("Day end date is wrong",cal.getTime().toString(),result.getEndDate().toString());
		
		assertEquals(2008, result.getNextYear());
		assertEquals(2007, result.getPrevYear());
		assertEquals(1, result.getNextMonthOfYear());
		assertEquals(12, result.getPrevMonthOfYear());
		assertEquals(2, result.getNextDayOfMonth());
		assertEquals(31, result.getPrevDayOfMonth());
		
		assertEquals(2008, result.getRelevantYear());
		assertEquals(1, result.getRelevantDayOfMonth());
		assertEquals(1, result.getRelevantWeekOfYear());
		assertEquals(1, result.getRelevantMonthOfYear());
	}
	
	@Test
	public void testFutureDay(){
		int y = 2020;
		int mo = 12;
		int d = 1;
		
		CalendarCommand cmd = new CalendarCommand();
		cmd.setYear(y);
		cmd.setMonthOfYear(mo);
		cmd.setDayOfMonth(d);
		cmd.setScope(CalendarCommand.Scope.DAY);
		
		CommandExecutor cmdexec = CommandExecutorFactory.createExecutor("50E7F601-71FD-40BD-9517-9699DDA611D6",cmd);
		assertTrue("Factory returned the wrong implementation", cmdexec instanceof CalendarCommandExecutor);
		CalendarCommandResult result = (CalendarCommandResult)cmdexec.execute();
		
		Day day = result.getDay();
		
		assertEquals(d,day.getDay());
		assertEquals(mo,day.getMonth());
		assertEquals(y,day.getYear());
		
		List<String> tagIds = result.getCalendarTagIdList();
		assertTrue(tagIds.size() == 0);
		
	}
}

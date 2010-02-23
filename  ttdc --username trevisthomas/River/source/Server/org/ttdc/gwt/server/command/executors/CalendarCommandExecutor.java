package org.ttdc.gwt.server.command.executors;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.List;

import org.apache.log4j.Logger;
import org.ttdc.gwt.client.presenters.calendar.CalendarHelpers;
import org.ttdc.gwt.client.services.CommandResult;
import org.ttdc.gwt.server.command.CommandExecutor;
import org.ttdc.gwt.server.dao.CalendarDao;
import org.ttdc.gwt.server.dao.TagDao;
import org.ttdc.gwt.shared.calender.Day;
import org.ttdc.gwt.shared.calender.Month;
import org.ttdc.gwt.shared.calender.Week;
import org.ttdc.gwt.shared.calender.Year;
import org.ttdc.gwt.shared.commands.CalendarCommand;
import org.ttdc.gwt.shared.commands.results.CalendarCommandResult;
import org.ttdc.persistence.objects.Tag;

import static org.ttdc.persistence.Persistence.*;

public class CalendarCommandExecutor  extends CommandExecutor<CalendarCommandResult>{
	private final static Logger log = Logger.getLogger(CalendarCommandExecutor.class);
	@Override
	protected CommandResult execute() {
		CalendarCommandResult result = new CalendarCommandResult();
		CalendarCommand cmd = (CalendarCommand) getCommand();
		List<String> tagIdList = new ArrayList<String>();
		
		try{
			beginSession();
			CalendarDao dao = new CalendarDao();
			if (cmd.getScope().equals(CalendarCommand.Scope.YEAR)) {
				dao.setYearNumber(cmd.getYear());
				Year year = dao.buildYear();
				result.setYear(year);
				loadTagsForYear(cmd, tagIdList);
				
				Calendar cal = GregorianCalendar.getInstance();
				cal.set(Calendar.YEAR,cmd.getYear()-1);
				int prevYear = cal.get(Calendar.YEAR);
				result.setPrevYear(prevYear);
				
				cal.set(Calendar.YEAR,cmd.getYear()+1);
				int nextYear = cal.get(Calendar.YEAR);
				result.setNextYear(nextYear);
				
				result.setRelevantYear(year.getYearNumber());
				result.setRelevantMonthOfYear(1);
				result.setRelevantWeekOfYear(1);
				result.setRelevantDayOfMonth(1);
				result.setScope(cmd.getScope());
				
			} 	
			else if (cmd.getScope().equals(CalendarCommand.Scope.MONTH)) {
				dao.setMonthOfYear(cmd.getMonthOfYear());
				dao.setYearNumber(cmd.getYear());
				Month month = dao.buildMonth();
				result.setMonth(month);
				loadTagsForMonth(cmd, tagIdList);
				
				determinePrevNextMonth(result, cmd);
				
				Calendar cal = GregorianCalendar.getInstance();
				cal.set(Calendar.YEAR, cmd.getYear());
				cal.set(Calendar.MONTH, cmd.getMonthOfYear()-1);
				cal.set(Calendar.DAY_OF_MONTH, 1);
				setRelevantDateValues(result, cal, cmd.getScope());
				
			} 
			else if(cmd.getScope().equals(CalendarCommand.Scope.SIMPLE_MONTH)){
				//SIMPLE_MONTH is for the front page calendar
				dao.setMonthOfYear(cmd.getMonthOfYear());
				dao.setYearNumber(cmd.getYear());
				Month month = dao.buildSimpleMonth();
				result.setMonth(month);
				loadTagsForMonth(cmd, tagIdList);
				
				determinePrevNextMonth(result, cmd);
				
//				Calendar cal = GregorianCalendar.getInstance();
//				cal.set(Calendar.YEAR, cmd.getYear());
//				cal.set(Calendar.MONTH, cmd.getMonthOfYear()-1);
//				cal.set(Calendar.DAY_OF_MONTH, 1);
//				setRelevantDateValues(result, cal, cmd.getScope());
			}
			else if (cmd.getScope().equals(CalendarCommand.Scope.WEEK)) {
				dao.setWeekOfYear(cmd.getWeekOfYear());
				dao.setYearNumber(cmd.getYear());
				Week week = dao.buildWeek();
				result.setWeek(week);
				loadTagsForWeek(cmd, tagIdList);
				
				determinePrevNextWeek(result, cmd);
				
				Calendar cal = GregorianCalendar.getInstance();
				cal.set(Calendar.YEAR, cmd.getYear());
				cal.set(Calendar.WEEK_OF_YEAR,cmd.getWeekOfYear());
				cal.set(Calendar.DAY_OF_WEEK, Calendar.SUNDAY);
				setRelevantDateValues(result, cal, cmd.getScope());
			}
			else if (cmd.getScope().equals(CalendarCommand.Scope.DAY)) {
				dao.setDayOfMonth(cmd.getDayOfMonth());
				dao.setMonthOfYear(cmd.getMonthOfYear());
				dao.setYearNumber(cmd.getYear());
				Day day = dao.buildDay();
				result.setDay(day);
				loadTagsForDay(cmd, tagIdList);
				
				determinePrevNextDay(result, cmd);
				
				Calendar cal = GregorianCalendar.getInstance();
				cal.set(Calendar.YEAR, cmd.getYear());
				cal.set(Calendar.MONTH, cmd.getMonthOfYear()-1);
				cal.set(Calendar.DAY_OF_MONTH, cmd.getDayOfMonth());
				setRelevantDateValues(result, cal, cmd.getScope());
			}
			else {
				throw new RuntimeException("Unimplemented CalendarCommand Scope");
			}
			
			result.setCalendarTagIdList(tagIdList);
			
			commit();
		}
		catch(Exception e){
			rollback();
		}
		return result;
	}

	private void setRelevantDateValues(CalendarCommandResult result, Calendar cal, CalendarCommand.Scope scope) {
		result.setRelevantYear(cal.get(Calendar.YEAR));
		result.setRelevantMonthOfYear(cal.get(Calendar.MONTH)+1);
		result.setRelevantWeekOfYear(cal.get(Calendar.WEEK_OF_YEAR));
		result.setRelevantDayOfMonth(cal.get(Calendar.DAY_OF_MONTH));
		result.setScope(scope);
	}
	
	private void determinePrevNextDay(CalendarCommandResult result, CalendarCommand cmd) {
		Calendar cal = GregorianCalendar.getInstance();
		cal.set(Calendar.YEAR, cmd.getYear());
		cal.set(Calendar.MONTH, cmd.getMonthOfYear()-1);
		cal.set(Calendar.DAY_OF_MONTH, cmd.getDayOfMonth());
		cal.add(Calendar.DAY_OF_YEAR, -1);
		result.setPrevYear(cal.get(Calendar.YEAR));
		result.setPrevMonthOfYear(cal.get(Calendar.MONTH)+1);
		result.setPrevDayOfMonth(cal.get(Calendar.DAY_OF_MONTH));
		
		cal.set(Calendar.YEAR, cmd.getYear());
		cal.set(Calendar.MONTH,cmd.getMonthOfYear()-1);
		cal.set(Calendar.DAY_OF_MONTH, cmd.getDayOfMonth());
		cal.add(Calendar.DAY_OF_YEAR, 1);
		result.setNextYear(cal.get(Calendar.YEAR));
		result.setNextMonthOfYear(cal.get(Calendar.MONTH)+1);
		result.setNextDayOfMonth(cal.get(Calendar.DAY_OF_MONTH));
	}

	private void determinePrevNextMonth(CalendarCommandResult result, CalendarCommand cmd) {
		/*
		 * There was a WEIRD bug here... i had to set the day of the month for this to work
		 * for some months it wasnt working properly if i didnt set it.  I swear that it seemed
		 * that i needed to set day of month. before the month value! Wack.
		 */
		Calendar cal = GregorianCalendar.getInstance();
		cal.set(Calendar.YEAR, cmd.getYear());
		cal.set(Calendar.DAY_OF_MONTH, 1);
		cal.set(Calendar.MONTH, cmd.getMonthOfYear()-1);
		
		cal.add(Calendar.MONTH, -1);
		result.setPrevYear(cal.get(Calendar.YEAR));
		result.setPrevMonthOfYear(cal.get(Calendar.MONTH)+1);
		
		cal.set(Calendar.YEAR, cmd.getYear());
		cal.set(Calendar.MONTH,cmd.getMonthOfYear()-1);
		cal.set(Calendar.DAY_OF_MONTH, 1);
		cal.add(Calendar.MONTH, 1);
		result.setNextYear(cal.get(Calendar.YEAR));
		result.setNextMonthOfYear(cal.get(Calendar.MONTH)+1);
	}
	
	private void determinePrevNextWeek(CalendarCommandResult result, CalendarCommand cmd) {
		Calendar cal = GregorianCalendar.getInstance();
		cal.set(Calendar.YEAR, cmd.getYear());
		cal.set(Calendar.WEEK_OF_YEAR, cmd.getWeekOfYear());
		cal.add(Calendar.WEEK_OF_YEAR, -1);
		result.setPrevYear(cal.get(Calendar.YEAR));
		result.setPrevWeekOfYear(cal.get(Calendar.WEEK_OF_YEAR));
		
		cal.set(Calendar.YEAR, cmd.getYear());
		cal.set(Calendar.WEEK_OF_YEAR,cmd.getWeekOfYear());
		cal.add(Calendar.WEEK_OF_YEAR, 1);
		result.setNextYear(cal.get(Calendar.YEAR));
		result.setNextWeekOfYear(cal.get(Calendar.WEEK_OF_YEAR));
	}

	private void loadTagsForYear(CalendarCommand cmd, List<String> tagIdList) {
		try{
			Tag tag = loadTagId(Tag.TYPE_DATE_YEAR, ""+cmd.getYear());	
			tagIdList.add(tag.getTagId());
		}
		catch(RuntimeException e){
			log.error(e);
		}
	}

	private void loadTagsForMonth(CalendarCommand cmd, List<String> tagIdList) {
		try{
			Tag tag = loadTagId(Tag.TYPE_DATE_YEAR, ""+cmd.getYear());
			tagIdList.add(tag.getTagId());
			
			tag = loadTagId(Tag.TYPE_DATE_MONTH, CalendarHelpers.getMonthName(cmd.getMonthOfYear()));
			tagIdList.add(tag.getTagId());
		}
		catch(RuntimeException e){
			log.error(e);
		}
	}

	private void loadTagsForWeek(CalendarCommand cmd, List<String> tagIdList) {
		try{
			Tag tag = loadTagId(Tag.TYPE_DATE_YEAR, ""+cmd.getYear());
			tagIdList.add(tag.getTagId());
			
			tag = loadTagId(Tag.TYPE_WEEK_OF_YEAR, ""+cmd.getWeekOfYear());
			tagIdList.add(tag.getTagId());
		}
		catch(RuntimeException e){
			log.error(e);
		}
	}

	private void loadTagsForDay(CalendarCommand cmd, List<String> tagIdList) {
		try{
			Tag tag = loadTagId(Tag.TYPE_DATE_YEAR, ""+cmd.getYear());
			tagIdList.add(tag.getTagId());
			
			tag = loadTagId(Tag.TYPE_DATE_DAY, ""+cmd.getDayOfMonth());
			tagIdList.add(tag.getTagId());
			
			tag = loadTagId(Tag.TYPE_DATE_MONTH, CalendarHelpers.getMonthName(cmd.getMonthOfYear()));
			tagIdList.add(tag.getTagId());
		}
		catch(RuntimeException e){
			log.error(e);
		}
	}
	
	private Tag loadTagId(String type, String value) {
		TagDao tagdao = new TagDao();
		tagdao.setType(type);
		tagdao.setValue(value);
		Tag tag = tagdao.load();
		if(tag == null)
			throw new RuntimeException("Failed to load "+tagdao.getType()+" tag for "+tagdao.getValue());
		
		return tag;
	}
	
}

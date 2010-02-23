package org.ttdc.gwt.shared.commands.results;

import java.util.List;

import org.ttdc.gwt.client.services.CommandResult;
import org.ttdc.gwt.shared.calender.Day;
import org.ttdc.gwt.shared.calender.Month;
import org.ttdc.gwt.shared.calender.Week;
import org.ttdc.gwt.shared.calender.Year;
import org.ttdc.gwt.shared.commands.CalendarCommand;

public class CalendarCommandResult implements CommandResult{
	private Year year;
	private Month month;
	private Week week;
	private Day day;
	private List<String> calendarTagIdList;
	
	CalendarCommand.Scope scope;
	
	private int nextYear;
	private int prevYear;
	private int nextDayOfMonth;
	private int prevDayOfMonth;
	private int nextMonthOfYear;
	private int prevMonthOfYear;
	private int nextWeekOfYear;
	private int prevWeekOfYear;
	
	private int relevantYear;
	private int relevantMonthOfYear;
	private int relevantWeekOfYear;
	private int relevantDayOfMonth;
	
	public Year getYear() {
		return year;
	}
	public void setYear(Year year) {
		this.year = year;
	}
	public Month getMonth() {
		return month;
	}
	public void setMonth(Month month) {
		this.month = month;
	}
	public Week getWeek() {
		return week;
	}
	public void setWeek(Week week) {
		this.week = week;
	}
	public Day getDay() {
		return day;
	}
	public void setDay(Day day) {
		this.day = day;
	}
	public List<String> getCalendarTagIdList() {
		return calendarTagIdList;
	}
	public void setCalendarTagIdList(List<String> calendarTagIdList) {
		this.calendarTagIdList = calendarTagIdList;
	}
	public int getNextYear() {
		return nextYear;
	}
	public void setNextYear(int nextYear) {
		this.nextYear = nextYear;
	}
	public int getPrevYear() {
		return prevYear;
	}
	public void setPrevYear(int prevYear) {
		this.prevYear = prevYear;
	}
	public int getNextDayOfMonth() {
		return nextDayOfMonth;
	}
	public void setNextDayOfMonth(int nextDayOfMonth) {
		this.nextDayOfMonth = nextDayOfMonth;
	}
	public int getPrevDayOfMonth() {
		return prevDayOfMonth;
	}
	public void setPrevDayOfMonth(int prevDayOfMonth) {
		this.prevDayOfMonth = prevDayOfMonth;
	}
	public int getNextMonthOfYear() {
		return nextMonthOfYear;
	}
	public void setNextMonthOfYear(int nextMonthOfYear) {
		this.nextMonthOfYear = nextMonthOfYear;
	}
	public int getPrevMonthOfYear() {
		return prevMonthOfYear;
	}
	public void setPrevMonthOfYear(int prevMonthOfYear) {
		this.prevMonthOfYear = prevMonthOfYear;
	}
	public int getNextWeekOfYear() {
		return nextWeekOfYear;
	}
	public void setNextWeekOfYear(int nextWeekOfYear) {
		this.nextWeekOfYear = nextWeekOfYear;
	}
	public int getPrevWeekOfYear() {
		return prevWeekOfYear;
	}
	public void setPrevWeekOfYear(int prevWeekOfYear) {
		this.prevWeekOfYear = prevWeekOfYear;
	}
	public int getRelevantYear() {
		return relevantYear;
	}
	public void setRelevantYear(int relevantYear) {
		this.relevantYear = relevantYear;
	}
	public int getRelevantMonthOfYear() {
		return relevantMonthOfYear;
	}
	public void setRelevantMonthOfYear(int relevantMonthOfYear) {
		this.relevantMonthOfYear = relevantMonthOfYear;
	}
	public int getRelevantWeekOfYear() {
		return relevantWeekOfYear;
	}
	public void setRelevantWeekOfYear(int relevantWeekOfYear) {
		this.relevantWeekOfYear = relevantWeekOfYear;
	}
	public int getRelevantDayOfMonth() {
		return relevantDayOfMonth;
	}
	public void setRelevantDayOfMonth(int relevantDayOfMonth) {
		this.relevantDayOfMonth = relevantDayOfMonth;
	}
	public CalendarCommand.Scope getScope() {
		return scope;
	}
	public void setScope(CalendarCommand.Scope scope) {
		this.scope = scope;
	}
}

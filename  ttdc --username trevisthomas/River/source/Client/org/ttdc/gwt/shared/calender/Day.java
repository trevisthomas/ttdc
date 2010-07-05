package org.ttdc.gwt.shared.calender;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.ttdc.gwt.shared.util.EqualsUtil;
import org.ttdc.gwt.shared.util.HashCodeUtil;

import com.google.gwt.user.client.rpc.IsSerializable;

public class Day implements IsSerializable{
	private int day;
	private int month;
	private int year;
	private List<Hour> hours;
	private List<CalendarThreadSummary> threads;
	private Date date;
	
	public Date getDate() {
		if(date == null)
			date = new Date(year-1900, month+1, day);
		return date;
	}

	private boolean visable = false; 
	private boolean content = false;
	private boolean today = false;
	private boolean future = false;
	
	public String toString(){
		return ""+day+(today?"*":"");
	}
	
	@Override
	public boolean equals(Object obj) {
		if( this == obj) return true;
		if( !(obj instanceof Day) ) return false;
		Day that = (Day) obj;
		return 
			EqualsUtil.areEqual(this.day, that.day)	&&
			EqualsUtil.areEqual(this.month, that.month)	&&
			EqualsUtil.areEqual(this.year, that.year);	
	}
	
	@Override
	public int hashCode() {
		int result = HashCodeUtil.SEED;
		result = HashCodeUtil.hash(result, day);
	    result = HashCodeUtil.hash(result, month);
	    result = HashCodeUtil.hash(result, year);
	    return result;
	}
	
	public Day() {
		// TODO Auto-generated constructor stub
	}
	
	public Day(Date d){
		if(d == null)
			date = new Date();
		else
			date = d;
		
		int year = 1900+date.getYear();
		int month = 1+date.getMonth();
		int dayOfMonth = date.getDate(); //day of month
		
		setYear(year);
		setMonth(month);
		setDay(dayOfMonth);
	}
	public Date toDate(){
		Date d = new Date(year-1900,month-1,day);
		return d;
	}
	
	//This method will initialize the day instance to contain 24 hour objects for showing 
	//days and weeks on the calendar.
	public void initHourly(){
		hours = new ArrayList<Hour>();
		for(int i = 0 ; i < 24 ; i++)
			hours.add(new Hour(i));
	}
	
	public void initSummary(){
		threads = new ArrayList<CalendarThreadSummary>();
	}
	
	public boolean isHourly(){
		return hours != null;
	}
	
	public boolean isSummary(){
		return threads!=null;
	}
	
	public boolean isVisable() {
		return visable;
	}
	public void setVisable(boolean visable) {
		this.visable = visable;
	}
	public int getDay() {
		return day;
	}
	public void setDay(int day) {
		this.day = day;
	}
	public boolean isContent() {
		return content;
	}
	public void setContent(boolean content) {
		this.content = content;
	}
	public boolean isToday() {
		return today;
	}
	public void setToday(boolean today) {
		this.today = today;
	}
	public boolean isFuture() {
		return future;
	}
	public void setFuture(boolean future) {
		this.future = future;
	}
	public int getMonth() {
		return month;
	}
	public void setMonth(int month) {
		this.month = month;
	}

	public int getYear() {
		return year;
	}

	public void setYear(int year) {
		this.year = year;
	}

	public List<Hour> getHours() {
		return hours;
	}

	public void setHours(List<Hour> hours) {
		this.hours = hours;
	}
	
	public Hour getHour(int hr){
		return hours.get(hr);
	}

	public List<CalendarThreadSummary> getThreads() {
		return threads;
	}

	public void setThreads(List<CalendarThreadSummary> threads) {
		this.threads = threads;
	}
	
	public void addThread(CalendarThreadSummary threadSummary){
		threads.add(threadSummary);
	}
}

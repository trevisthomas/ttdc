package org.ttdc.gwt.shared.calender;

import java.util.ArrayList;
import java.util.List;

import com.google.gwt.user.client.rpc.IsSerializable;

public class Week implements IsSerializable{
	private int weekOfYear;
	private int year;
	private List<Day> days = new ArrayList<Day>();
	
	@Override
	public String toString() {
		return days.toString();
	}
	
	public void add(Day day){
		days.add(day);
	}
	public List<Day> getDays() {
		return days;
	}
	public void setDays(List<Day> days) {
		this.days = days;
	}
	public int getWeekOfYear() {
		return weekOfYear;
	}
	public void setWeekOfYear(int weekOfYear) {
		this.weekOfYear = weekOfYear;
	}
	public int getYear() {
		return year;
	}
	public void setYear(int year) {
		this.year = year;
	}
}
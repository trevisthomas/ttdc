package org.ttdc.gwt.shared.calender;

import com.google.gwt.user.client.rpc.IsSerializable;

public class CalendarThreadSummary implements IsSerializable {
	private int year;
	private int month;
	private int day;
	private int count;
	private String title;
	private String rootId;
	
	@Override
	public String toString() {
		return title +" ("+count+") " + month+ "/"+ day +"/"+year ;
	}
	
	public int getYear() {
		return year;
	}
	public void setYear(int year) {
		this.year = year;
	}
	public int getMonth() {
		return month;
	}
	public void setMonth(int month) {
		this.month = month;
	}
	public int getDay() {
		return day;
	}
	public void setDay(int day) {
		this.day = day;
	}
	public int getCount() {
		return count;
	}
	public void setCount(int count) {
		this.count = count;
	}
	public String getTitle() {
		return title;
	}
	public void setTitle(String title) {
		this.title = title;
	}
	public String getRootId() {
		return rootId;
	}
	public void setRootId(String rootId) {
		this.rootId = rootId;
	}
}

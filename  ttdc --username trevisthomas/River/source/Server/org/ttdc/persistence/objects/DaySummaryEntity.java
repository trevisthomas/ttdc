package org.ttdc.persistence.objects;

import javax.persistence.Id;

@javax.persistence.Entity
public class DaySummaryEntity {
	private int year;
	private int month;
	private int day;
	private int count;
	
	/*
	 * 
	 * DANGER WARNING DANGER!
	 * 
	 * This class will only be unique for a months worth of data.  If the 
	 * @Id param repeats hibernate will not properly map the persistent data
	 * to the classes.
	 * 
	 */
	String uniqueId = null;
	@Id
	public String getUniqueId(){
		return uniqueId;
	}
	
	public void setUniqueId(String uniqueId){
		this.uniqueId = uniqueId;
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
	
}

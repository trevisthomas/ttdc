package org.ttdc.persistence.objects;

import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Transient;

import org.hibernate.annotations.GenericGenerator;

@javax.persistence.Entity
public class DaySummaryEntity {
	private int year;
	private int month;
	private int day;
	private int count;
	private Date date;
	
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
	@GeneratedValue( generator="system-uuid" )
	@GenericGenerator(name = "system-uuid", strategy = "guid")
	public String getUniqueId(){
		return uniqueId;
	}
	
	public void setUniqueId(String uniqueId){
		this.uniqueId = uniqueId;
	}
	@Transient
	public int getYear() {
		return year;
	}
	public void setYear(int year) {
		this.year = year;
	}
	@Transient
	public int getMonth() {
		return month;
	}
	public void setMonth(int month) {
		this.month = month;
	}
	@Transient
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

	public Date getDate() {
		return date;
	}

	public void setDate(Date date) {
		this.date = date;
		Calendar cal = GregorianCalendar.getInstance();
		cal.setTime(date);
		setYear(cal.get(Calendar.YEAR));
		setMonth(cal.get(Calendar.MONTH)+1);
		setDay(cal.get(Calendar.DAY_OF_MONTH));
	}
	
}

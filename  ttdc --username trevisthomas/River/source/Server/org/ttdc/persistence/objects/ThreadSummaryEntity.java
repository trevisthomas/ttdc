package org.ttdc.persistence.objects;

import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Transient;

import org.hibernate.annotations.GenericGenerator;

@Entity
public class ThreadSummaryEntity {
	private int year;
	private int month;
	private int day;
	private int count;
	private String title;
	private String rootId;
	private Date date;
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
	
}

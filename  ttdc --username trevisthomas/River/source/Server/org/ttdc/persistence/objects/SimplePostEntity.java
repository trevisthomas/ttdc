package org.ttdc.persistence.objects;

import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Transient;

@Entity
public class SimplePostEntity {
	private int year;
	private int month;
	private int day;
	private int hour;

	private Date date;
	private String creatorLogin;
	private String creatorId;
	private String postId;
	private String summary;
	private String title;
	private String rootId;
	
	@Override
	public String toString() {
		String str = title + " : " + creatorLogin + " : " + (summary != null ? summary : "");
		return str;
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
	@Transient
	public int getHour() {
		return hour;
	}
	public void setHour(int hour) {
		this.hour = hour;
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
		setHour(cal.get(Calendar.HOUR_OF_DAY));
	}
	public String getCreatorLogin() {
		return creatorLogin;
	}
	public void setCreatorLogin(String creatorLogin) {
		this.creatorLogin = creatorLogin;
	}
	public String getCreatorId() {
		return creatorId;
	}
	public void setCreatorId(String creatorId) {
		this.creatorId = creatorId;
	}
	
	@Id
	public String getPostId() {
		return postId;
	}
	public void setPostId(String postId) {
		this.postId = postId;
	}
	public String getSummary() {
		return summary;
	}
	public void setSummary(String summary) {
		this.summary = summary;
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

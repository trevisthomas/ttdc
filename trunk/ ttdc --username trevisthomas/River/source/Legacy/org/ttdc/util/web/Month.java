package org.ttdc.util.web;

import java.util.ArrayList;
import java.util.List;

@Deprecated
public class Month {
	
	public static class Week{
		public static class Day{
			private boolean visable = false; //
			private int day;
			private boolean content = false;
			private boolean today = false;
			private boolean future = false;
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
		}
		
		private List<Day> days = new ArrayList<Day>();
		public void add(Day day){
			days.add(day);
		}
		public List<Day> getDays() {
			return days;
		}
		public void setDays(List<Day> days) {
			this.days = days;
		}
	}
	
	private String name;
	private int year;
	private int month;
	private List<Week> weeks = new ArrayList<Week>();
	
	public void add(Week week) {
		weeks.add(week);
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	
	public int getYear() {
		return year;
	}

	public void setYear(int year) {
		this.year = year;
	}

	public List<Week> getWeeks() {
		return weeks;
	}

	public void setWeeks(List<Week> weeks) {
		this.weeks = weeks;
	}

	public int getMonth() {
		return month;
	}

	public void setMonth(int month) {
		this.month = month;
	}

	
}

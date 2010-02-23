package org.ttdc.gwt.shared.calender;

import java.util.ArrayList;
import java.util.List;

import com.google.gwt.user.client.rpc.IsSerializable;

public final class Month implements IsSerializable{
	private String name;
	private int yearNumber;
	private int monthNumber;
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

	public List<Week> getWeeks() {
		return weeks;
	}

	public void setWeeks(List<Week> weeks) {
		this.weeks = weeks;
	}

	public int getYearNumber() {
		return yearNumber;
	}

	public void setYearNumber(int yearNumber) {
		this.yearNumber = yearNumber;
	}

	public int getMonthNumber() {
		return monthNumber;
	}

	public void setMonthNumber(int monthNumber) {
		this.monthNumber = monthNumber;
	}

	
}

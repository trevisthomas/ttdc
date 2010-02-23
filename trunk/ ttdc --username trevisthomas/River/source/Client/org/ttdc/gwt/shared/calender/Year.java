package org.ttdc.gwt.shared.calender;

import com.google.gwt.user.client.rpc.IsSerializable;

public final class Year implements IsSerializable{
	private Month[] months = new Month[13];
	private int yearNumber;
	
	public Year(){	
		months[0] = null;
	}
	
	public void setMonth(int index, Month month){
		if(index > 12 || index < 1) throw new RuntimeException("Bad month mo-jo, mo fo.");
		months[index] = month;
	}
	
	public Month getMonth(int index){
		return months[index];
	}

	public int getYearNumber() {
		return yearNumber;
	}

	public void setYearNumber(int yearNumber) {
		this.yearNumber = yearNumber;
	}
	
	
}

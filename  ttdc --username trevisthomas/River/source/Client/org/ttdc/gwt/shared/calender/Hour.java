package org.ttdc.gwt.shared.calender;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import com.google.gwt.user.client.rpc.IsSerializable;

public class Hour implements IsSerializable{
	private int hourOfDay; // 0 - 23
	private List<CalendarPost> posts;
	
	public Hour(){};
	
	@Override
	public String toString() {
		return posts.toString();
	}
	
	public Hour(int hourOfDay) {
		this.hourOfDay = hourOfDay;
		posts = new ArrayList<CalendarPost>();
	}
	
	public void add(CalendarPost post){
		posts.add(post);
	}
	public List<CalendarPost> getPosts(){
		return posts;
	}
	public int getHourOfDay() {
		return hourOfDay;
	}
}

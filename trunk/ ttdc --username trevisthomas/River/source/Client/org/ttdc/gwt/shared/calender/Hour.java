package org.ttdc.gwt.shared.calender;

import java.util.ArrayList;
import java.util.List;

import org.ttdc.gwt.client.beans.GPost;

import com.google.gwt.user.client.rpc.IsSerializable;

public class Hour implements IsSerializable{
	private int hourOfDay; // 0 - 23
	private List<CalendarPost> calendarPosts;
	private List<GPost> posts = new ArrayList<GPost>();  //Added when i decided to show full, expanded posts on day
	
	public Hour(){};
	
	@Override
	public String toString() {
		return calendarPosts.toString();
	}
	
	public Hour(int hourOfDay) {
		this.hourOfDay = hourOfDay;
		calendarPosts = new ArrayList<CalendarPost>();
	}
	
	public void addCalendarPost(CalendarPost cp){
		calendarPosts.add(cp);
	}
	
	public void addPost(GPost post){
		posts.add(post);
	}
	
	public List<CalendarPost> getCalendarPosts(){
		return calendarPosts;
	}
	public int getHourOfDay() {
		return hourOfDay;
	}
	
	public List<GPost> getPosts() {
		return posts;
	}
	
	public void setPosts(List<GPost> posts) {
		this.posts = posts;
	}
}

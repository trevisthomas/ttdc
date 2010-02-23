package org.ttdc.gwt.client.messaging.calendar;

import java.util.Date;

import org.ttdc.gwt.client.messaging.Event;

public class CalendarEvent extends Event<CalendarEventType, Date>{
	private CalendarEventType type;
	private Date source;
	
	public CalendarEvent(){}
	
	public CalendarEvent(CalendarEventType type, Date source){
		this.type = type;
		this.source = source;
	}
	
	public CalendarEventType getType() {
		return type;
	}
	public Date getSource() {
		return source;
	}
	
}

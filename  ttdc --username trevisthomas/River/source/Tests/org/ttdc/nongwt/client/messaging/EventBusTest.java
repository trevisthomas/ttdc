package org.ttdc.nongwt.client.messaging;
import static org.junit.Assert.*;

import java.util.Date;

import org.junit.Test;
import org.ttdc.gwt.client.beans.GAssociationPostTag;
import org.ttdc.gwt.client.beans.GPerson;
import org.ttdc.gwt.client.beans.GPost;
import org.ttdc.gwt.client.messaging.EventBus;
import org.ttdc.gwt.client.messaging.calendar.CalendarEvent;
import org.ttdc.gwt.client.messaging.calendar.CalendarEventListener;
import org.ttdc.gwt.client.messaging.calendar.CalendarEventType;
import org.ttdc.gwt.client.messaging.error.MessageEvent;
import org.ttdc.gwt.client.messaging.error.MessageEventListener;
import org.ttdc.gwt.client.messaging.error.MessageEventType;
import org.ttdc.gwt.client.messaging.history.HistoryEvent;
import org.ttdc.gwt.client.messaging.history.HistoryEventListener;
import org.ttdc.gwt.client.messaging.history.HistoryEventType;
import org.ttdc.gwt.client.messaging.person.PersonEvent;
import org.ttdc.gwt.client.messaging.person.PersonEventListener;
import org.ttdc.gwt.client.messaging.person.PersonEventType;
import org.ttdc.gwt.client.messaging.post.PostEvent;
import org.ttdc.gwt.client.messaging.post.PostEventListener;
import org.ttdc.gwt.client.messaging.post.PostEventType;
import org.ttdc.gwt.client.messaging.tag.TagEvent;
import org.ttdc.gwt.client.messaging.tag.TagEventListener;
import org.ttdc.gwt.client.messaging.tag.TagEventType;

public class EventBusTest {
	
	
	@Test
	public void fireMultipleEvents(){
		EventBus bus = EventBus.getInstance();
		bus.addListener(
			new HistoryEventListener(){
				public void onHistoryEvent(HistoryEvent event) {
					assertEquals(HistoryEventType.VIEW_CHANGE,event.getType());
				}
			}	
		);
		
		//This one is ignored
		PersonEvent personEvent = new PersonEvent(PersonEventType.USER_CHANGED,new GPerson());
		bus.fireEvent(personEvent);
		
		//This one cauese success
		HistoryEvent event = new HistoryEvent("");
		bus.fireEvent(event);
		
	}
	
	@Test
	public void testHistoryEvents(){
		EventBus bus = EventBus.getInstance();
		bus.addListener(
			new HistoryEventListener(){
				public void onHistoryEvent(HistoryEvent event) {
					assertEquals(HistoryEventType.VIEW_CHANGE,event.getType());
				}
			}	
		);
		
		HistoryEvent event = new HistoryEvent("");
		
		bus.fireEvent(event);
	}
	
	@Test
	public void testPersonEvents(){
		
		PersonEvent personEvent = new PersonEvent(PersonEventType.USER_CHANGED, new GPerson());
		PersonEventListener personEventListener = new PersonEventListener(){
			public void onPersonEvent(PersonEvent event) {
				assertEquals(PersonEventType.USER_CHANGED, event.getType());
			}
		};
		
		EventBus bus = EventBus.getInstance();
		bus.addListener(personEventListener);
		bus.fireEvent(personEvent);
		
	}
	@Test
	public void testPostEvents(){
		PostEvent postEvent = new PostEvent(PostEventType.EDIT, new GPost());
		PostEventListener postEventListener = new PostEventListener(){
			public void onPostEvent(PostEvent event) {
				assertEquals(PostEventType.EDIT, event.getType());
			}
		};
		
		EventBus bus = EventBus.getInstance();
		bus.addListener(postEventListener);
		bus.fireEvent(postEvent);
	}
	@Test
	public void testCalendarEvents(){
		CalendarEvent calendarEvent = new CalendarEvent(CalendarEventType.DATE_CHANGED, new Date());
		CalendarEventListener calendarEventListener = new CalendarEventListener(){
			public void onCalendarEvent(CalendarEvent event) {
				assertEquals(CalendarEventType.DATE_CHANGED, event.getType());
			}
		};
		
		EventBus bus = EventBus.getInstance();
		bus.addListener(calendarEventListener);
		bus.fireEvent(calendarEvent);
	}
	@Test
	public void testTagEvents(){
		TagEvent tagEvent = new TagEvent(TagEventType.NEW_TAG, new GAssociationPostTag());
		TagEventListener tagEventListener = new TagEventListener(){
			public void onTagEvent(TagEvent event) {
				assertEquals(TagEventType.NEW_TAG, event.getType());
			}
		};
		
		EventBus bus = EventBus.getInstance();
		bus.addListener(tagEventListener);
		bus.fireEvent(tagEvent);
	}
	
	@Test
	public void testExceptionEvents(){
		MessageEvent tagEvent = new MessageEvent(MessageEventType.SYSTEM_ERROR, "An error");
		MessageEventListener messageEventListener = new MessageEventListener(){
			public void onMessageEvent(MessageEvent event) {
				assertEquals(MessageEventType.SYSTEM_ERROR, event.getType());
			}
		};
		
		EventBus bus = EventBus.getInstance();
		bus.addListener(messageEventListener);
		bus.fireEvent(tagEvent);
	}
}

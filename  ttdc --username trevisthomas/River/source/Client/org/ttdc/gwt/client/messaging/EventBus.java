package org.ttdc.gwt.client.messaging;

import java.util.ArrayList;
import java.util.List;

import org.ttdc.gwt.client.messaging.calendar.CalendarEvent;
import org.ttdc.gwt.client.messaging.calendar.CalendarEventListener;
import org.ttdc.gwt.client.messaging.error.MessageEvent;
import org.ttdc.gwt.client.messaging.error.MessageEventListener;
import org.ttdc.gwt.client.messaging.error.MessageEventType;
import org.ttdc.gwt.client.messaging.history.HistoryConstants;
import org.ttdc.gwt.client.messaging.history.HistoryEvent;
import org.ttdc.gwt.client.messaging.history.HistoryEventListener;
import org.ttdc.gwt.client.messaging.history.HistoryNotification;
import org.ttdc.gwt.client.messaging.history.HistoryNotificationListener;
import org.ttdc.gwt.client.messaging.history.HistoryToken;
import org.ttdc.gwt.client.messaging.person.PersonEvent;
import org.ttdc.gwt.client.messaging.person.PersonEventListener;
import org.ttdc.gwt.client.messaging.post.PostEvent;
import org.ttdc.gwt.client.messaging.post.PostEventListener;
import org.ttdc.gwt.client.messaging.tag.TagEvent;
import org.ttdc.gwt.client.messaging.tag.TagEventListener;

import com.google.gwt.user.client.History;

public class EventBus {
	
	private EventBus(){}
	private final List<PostEventListener> postListeners = new ArrayList<PostEventListener>();
	private final List<TagEventListener> tagListeners = new ArrayList<TagEventListener>();
	private final List<CalendarEventListener> calListeners = new ArrayList<CalendarEventListener>();
	private final List<HistoryEventListener> historyListeners = new ArrayList<HistoryEventListener>();
	private final List<PersonEventListener> personListeners = new ArrayList<PersonEventListener>();
	private final List<MessageEventListener> errorListeners = new ArrayList<MessageEventListener>();
	private final List<HistoryNotificationListener> historyNotificationListener = new ArrayList<HistoryNotificationListener>();
	
	//NUKE!
	public static native void reload() /*-{
		$wnd.location.reload();
	}-*/;
	
	public void clearAll(){
		postListeners.clear();
		tagListeners.clear();
		calListeners.clear();
		historyListeners.clear();
		personListeners.clear();
		errorListeners.clear();
		historyNotificationListener.clear();
	}
	
	private static class SingletonHolder {
		private final static EventBus INSTANCE = new EventBus();
	}
	public static EventBus getInstance(){
		return SingletonHolder.INSTANCE;
	}
	
	public void addListener(MessageEventListener listener) {
		errorListeners.add(listener);
	}
	public boolean removeListener(MessageEventListener listener) {
		return errorListeners.remove(listener);
	}
	public boolean containsListener(MessageEventListener listener) {
		return errorListeners.contains(listener);
	}
	
	public void addListener(PostEventListener listener) {
		postListeners.add(listener);
	}
	public boolean removeListener(PostEventListener listener) {
		return postListeners.remove(listener);
	}
	public boolean containsListener(PostEventListener listener) {
		return postListeners.contains(listener);
	}
	
	public void addListener(TagEventListener listener) {
		tagListeners.add(listener);
	}
	public boolean removeListener(TagEventListener listener) {
		return tagListeners.remove(listener);
	}
	public boolean containsListener(TagEventListener listener) {
		return tagListeners.contains(listener);
	}
	
	public void addListener(CalendarEventListener listener) {
		calListeners.add(listener);
	}
	public boolean removeListener(CalendarEventListener listener) {
		return calListeners.remove(listener);
	}
	public boolean containsListener(CalendarEventListener listener) {
		return calListeners.contains(listener);
	}
	/**
	 * I'm making it so that there can be only ONE history event list
	 * @param listener
	 */
	public void addListener(HistoryEventListener listener) {
		historyListeners.add(listener);
	}
	public boolean removeListener(HistoryEventListener listener) {
		return historyListeners.remove(listener);
	}
	public boolean containsListener(HistoryEventListener listener) {
		return historyListeners.contains(listener);
	}
	
	
	public void addListener(PersonEventListener listener) {
		personListeners.add(listener);
	}
	public boolean removeListener(PersonEventListener listener) {
		return personListeners.remove(listener);
	}
	public boolean containsListener(PersonEventListener listener) {
		return personListeners.contains(listener);
	}
	
	public void addListener(HistoryNotificationListener listener) {
		historyNotificationListener.add(listener);
	}
	public boolean removeListener(HistoryNotificationListener listener) {
		return historyNotificationListener.remove(listener);
	}
	public boolean containsListener(HistoryNotificationListener listener) {
		return historyNotificationListener.contains(listener);
	}
	
	
	
	private void notifyListeners(PostEvent event) {
		List<PostEventListener> list = new ArrayList(postListeners);
		for(PostEventListener listener : list){
			listener.onPostEvent(event);
		}
	}
	private void notifyListeners(TagEvent event) {
		for(TagEventListener listener : tagListeners){
			listener.onTagEvent(event);
		}
	}
	private void notifyListeners(CalendarEvent event) {
		for(CalendarEventListener listener : calListeners){
			listener.onCalendarEvent(event);
		}
	}
	private void notifyListeners(HistoryEvent event) {
		for(HistoryEventListener listener : historyListeners){
			listener.onHistoryEvent(event);
		}
	}
	private void notifyListeners(PersonEvent event) {
		for(PersonEventListener listener : personListeners){
			listener.onPersonEvent(event);
		}
	}
	private void notifyListeners(MessageEvent event) {
		for(MessageEventListener listener : errorListeners){
			listener.onMessageEvent(event);
		}
	}
	
	private void notifyListeners(HistoryNotification event) {
		for(HistoryNotificationListener listener : historyNotificationListener){
			listener.onHistoryNotification(event.getSource());
		}
	}
	
	public void fireHistory(HistoryToken token){
		fireEvent(new HistoryNotification(token.toString()));
	}
	
	public void fireEvent(Throwable t){
		fireEvent(new MessageEvent(MessageEventType.SYSTEM_ERROR,t.getMessage()));
	}
	
	public static void fireEvent(Event<?,?> event) {
		if(event instanceof PersonEvent)
			EventBus.getInstance().notifyListeners((PersonEvent)event);
		else if(event instanceof PostEvent)
			EventBus.getInstance().notifyListeners((PostEvent)event);
		else if(event instanceof TagEvent)
			EventBus.getInstance().notifyListeners((TagEvent) event);
		else if(event instanceof CalendarEvent)
			EventBus.getInstance().notifyListeners((CalendarEvent)event);
		else if(event instanceof HistoryEvent)
			EventBus.getInstance().notifyListeners((HistoryEvent)event);
		else if(event instanceof MessageEvent)
			EventBus.getInstance().notifyListeners((MessageEvent)event);
		else if(event instanceof HistoryNotification){
			EventBus.getInstance().notifyListeners((HistoryNotification)event);
		}
		else
			throw new RuntimeException("An unknown event was passed to Event Bus, discarded.");
	}
	
	/*
	 * Helper method to make code wishing to fire a history event cleaner
	 */
	public static void fireHistoryToken(HistoryToken token){
		EventBus.fireEvent(new HistoryNotification(token.toString()));
	}

	public static void fireReturnHomeEvent() {
		HistoryToken token = new HistoryToken();
		token.addParameter(HistoryConstants.VIEW, HistoryConstants.VIEW_HOME);
		EventBus.fireHistoryToken(token);
	}
	
	public static void reloadHome() {
		HistoryToken token = new HistoryToken();
		token.addParameter(HistoryConstants.VIEW, HistoryConstants.VIEW_HOME);
		History.newItem(token.toString(),false);
		reload();
	}
	
	public static void fireRedirectToLogin(){
		HistoryToken token = new HistoryToken();
		token.addParameter(HistoryConstants.VIEW, HistoryConstants.VIEW_USER_TOOLS);
		EventBus.fireHistoryToken(token);
	}
	
	public static void fireErrorMessage(String message){
		EventBus.fireEvent(new MessageEvent(MessageEventType.ERROR,message));
	}
	
	public static void fireMessage(String message){
		EventBus.fireEvent(new MessageEvent(MessageEventType.INFO,message));
	}
}

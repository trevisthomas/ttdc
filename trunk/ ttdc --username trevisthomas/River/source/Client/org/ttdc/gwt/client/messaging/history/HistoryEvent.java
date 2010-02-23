package org.ttdc.gwt.client.messaging.history;

import org.ttdc.gwt.client.messaging.Event;

/*
 * HistoryEvent is the event type used when users start the site from a book mark, or 
 * when they navigate the site via links.   
 *
 */
public final class HistoryEvent extends Event<HistoryEventType, HistoryToken>{
	private HistoryToken source;
	private HistoryEventType type;
	
	public HistoryEvent() {}
	
	public static HistoryEvent createViewChange(String viewName){
		return new HistoryEvent(HistoryEventType.VIEW_CHANGE,HistoryConstants.VIEW+"="+viewName);
	}
	
	public HistoryEvent(HistoryEventType type, String queryString) {
		HistoryToken token = new HistoryToken(queryString);
		this.source = token;
		this.type = type;
	}
	
	public HistoryEvent(String queryString) {
		HistoryToken token = new HistoryToken(queryString);
		this.source = token;
		this.type = HistoryEventType.VIEW_CHANGE;
	}
	
	public HistoryEvent(HistoryToken token){
		this.type = HistoryEventType.VIEW_CHANGE;
		this.source = token;
	}
	
	public HistoryToken getSource() {
		return source;
	}
	
	public HistoryEventType getType() {
		return type;
	}
}

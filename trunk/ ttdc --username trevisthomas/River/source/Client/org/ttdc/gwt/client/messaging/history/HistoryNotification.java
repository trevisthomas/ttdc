package org.ttdc.gwt.client.messaging.history;

import org.ttdc.gwt.client.messaging.Event;
import org.ttdc.gwt.client.messaging.HistoryMonitor;

/**
 * History notification notifies the GWT history system that i would like to 
 * record something into history. HistoryMonitor serves two jobs.  One, 
 * it listens to the EventBus for these HistoryNotification messages
 * and when one comes in, it grabs the value and calls History.newItem
 * which in turn records the history for the browser which then calls onValueChange
 * which is also handled by HistoryMonitor. That method fires a HistoryEvent onto the 
 * event bus which allows my presenter code to respond.
 * 
 * see {@link HistoryMonitor}
 *  
 *
 */
public class HistoryNotification extends Event<String, String>{
	private String queryString = "";
	public HistoryNotification(String queryString){
		this.queryString = queryString;
	}
	@Override
	public String getSource() {
		return queryString;
	}

	@Override
	public String getType() {
		// TODO Auto-generated method stub
		return null;
	}
	
}

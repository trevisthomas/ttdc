package org.ttdc.gwt.client.messaging;

import org.ttdc.gwt.client.messaging.history.HistoryEvent;
import org.ttdc.gwt.client.messaging.history.HistoryNotificationListener;

import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.user.client.History;

/**
 * Singleton to listen for GWT browser history changes and fire HistoryEvent's
 * onto the EventBus.
 * 
 * This class also listens to EventBus for HistoryNotification events so that
 * it can translate those into GWT history events.  I do this so that use 
 * browser history.  Calling fireEvent with a history event from
 * a Presenter will get the page to change but it wont track properly
 * in browser history.
 * 
 * @author Trevis
 *
 */
public class HistoryMonitor implements ValueChangeHandler<String>, HistoryNotificationListener {
	private static HistoryMonitor INSTANCE = null;
	
	public static void initInstance(){
		if(INSTANCE == null)
			INSTANCE = new HistoryMonitor();
	}
	private HistoryMonitor(){
		History.addValueChangeHandler(this);
		EventBus.getInstance().addListener(this);
	}
	
	@Override
	public void onValueChange(ValueChangeEvent<String> event) {
		HistoryEvent historyEvent = new HistoryEvent(event.getValue());
		EventBus.getInstance().fireEvent(historyEvent);
	}
	
	@Override
	public void onHistoryNotification(String queryString) {
		History.newItem(queryString);
	}
	
}

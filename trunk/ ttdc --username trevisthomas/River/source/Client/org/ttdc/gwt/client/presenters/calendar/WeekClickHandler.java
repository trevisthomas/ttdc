package org.ttdc.gwt.client.presenters.calendar;

import org.ttdc.gwt.client.messaging.EventBus;
import org.ttdc.gwt.client.messaging.history.HistoryToken;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;

public class WeekClickHandler implements ClickHandler{
	private HistoryToken token;
	WeekClickHandler(HistoryToken token){
		this.token = token;
	}
	
	@Override
	public void onClick(ClickEvent event) {
		EventBus.getInstance().fireHistory(token);
	}
}

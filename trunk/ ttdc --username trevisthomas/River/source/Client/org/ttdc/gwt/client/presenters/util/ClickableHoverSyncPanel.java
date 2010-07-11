package org.ttdc.gwt.client.presenters.util;

import java.util.ArrayList;
import java.util.List;

import org.ttdc.gwt.client.messaging.EventBus;
import org.ttdc.gwt.client.messaging.history.HistoryToken;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.MouseOutEvent;
import com.google.gwt.event.dom.client.MouseOutHandler;
import com.google.gwt.event.dom.client.MouseOverEvent;
import com.google.gwt.event.dom.client.MouseOverHandler;
import com.google.gwt.user.client.ui.FocusPanel;
import com.google.gwt.user.client.ui.Widget;

public class ClickableHoverSyncPanel extends FocusPanel{
	private String hoverStyle;
	private String style;
	private final HistoryToken token;
	private List<Widget> synched = new ArrayList<Widget>();
	private boolean disableHoverStyleOnSelf = false;
	
	public ClickableHoverSyncPanel(HistoryToken token, String style, String hoverStyle) {
		this.token = token;
		this.hoverStyle = hoverStyle;
		this.style=style;
		init();
	}
	
	public ClickableHoverSyncPanel(String style, String hoverStyle) {
		this.token = null;
		this.hoverStyle = hoverStyle;
		this.style=style;
		init();
	}
	
	public void addSynchedHoverTarget(Widget widget){
		synched.add(widget);
	}
	
	private void init(){
		addMouseOverHandler(new MouseOverHandler() {
			@Override
			public void onMouseOver(MouseOverEvent event) {
				broadcastRemoveStyle(style);
				
				broadcastAddStyle(hoverStyle);
			}
		});
		
		addMouseOutHandler(new MouseOutHandler() {
			@Override
			public void onMouseOut(MouseOutEvent event) {
				
				broadcastRemoveStyle(hoverStyle);
				broadcastAddStyle(style);
			}
		});
		
		addClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				if(token != null)
					EventBus.getInstance().fireHistory(token);
			}
		});
	}
	
	private void broadcastAddStyle(String hoverStyle) {
		if(!isDisableHoverStyleOnSelf())
			addStyleName(hoverStyle);	
		for(Widget w : synched){
			w.addStyleName(hoverStyle);
		}
	}
	
	private void broadcastRemoveStyle(String hoverStyle) {
		if(!isDisableHoverStyleOnSelf())
			removeStyleName(hoverStyle);
		for(Widget w : synched){
			w.removeStyleName(hoverStyle);
		}
	}

	public void setDisableHoverStyleOnSelf(boolean b) {
		disableHoverStyleOnSelf = b;
	}
	
	public boolean isDisableHoverStyleOnSelf() {
		return disableHoverStyleOnSelf;
	}
}

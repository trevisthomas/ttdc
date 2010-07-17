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
	private String childHoverStyle;
	private String childStyle;
	private HistoryToken token;
	private List<Widget> synched = new ArrayList<Widget>();
	
	public ClickableHoverSyncPanel(String style, String hoverStyle) {
		this.hoverStyle = hoverStyle;
		this.style=style;
		this.childHoverStyle = hoverStyle;
		this.childStyle=style;
		init();
	}
	
	public HistoryToken getToken() {
		return token;
	}

	public void setToken(HistoryToken token) {
		this.token = token;
	}

	public ClickableHoverSyncPanel(String style, String hoverStyle, String childStyle, String childHoverStyle) {
		this.hoverStyle = hoverStyle;
		this.style=style;
		this.childHoverStyle = childHoverStyle;
		this.childStyle=childStyle;
		init();
	}
	
	public void addSynchedHoverTarget(Widget widget){
		synched.add(widget);
		performMouseOut();
	}
	
	@Override
	public void setStyleName(String style) {
		super.setStyleName(style);
		super.addStyleName("tt-cursor-pointer");
	}
	
	private void init(){
		performMouseOut();
		addStyleName("tt-cursor-pointer");
		addMouseOverHandler(new MouseOverHandler() {
			@Override
			public void onMouseOver(MouseOverEvent event) {
				performMouseOver();
			}
		});
		
		addMouseOutHandler(new MouseOutHandler() {
			@Override
			public void onMouseOut(MouseOutEvent event) {
				performMouseOut();
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
	
	private void performMouseOver() {
		broadcastRemoveStyle(style, childStyle);
		broadcastAddStyle(hoverStyle, childHoverStyle);
		for(Widget w : synched){
			if(w instanceof ClickableHoverSyncPanel){
				((ClickableHoverSyncPanel)w).performMouseOver();
			}
		}
	}
	
	private void performMouseOut() {
		broadcastRemoveStyle(hoverStyle, childHoverStyle);
		broadcastAddStyle(style, childStyle);
		for(Widget w : synched){
			if(w instanceof ClickableHoverSyncPanel){
				((ClickableHoverSyncPanel)w).performMouseOut();
			}
		}
	}
	
	private void broadcastAddStyle(String css, String css2) {
		addStyleName(css);	
		for(Widget w : synched){
			if(!(w instanceof ClickableHoverSyncPanel)){
				w.addStyleName(css2);
			}
		}
	}
	
	private void broadcastRemoveStyle(String css, String css2) {
		removeStyleName(css);
		for(Widget w : synched){
			if(!(w instanceof ClickableHoverSyncPanel)){
				w.removeStyleName(css2);
			}
		}
	}
	
	protected void updatePrimary(String newStyle, String newHoverStyle){
		removeStyleName(this.style);
		removeStyleName(this.hoverStyle);
		this.style = newStyle;
		this.hoverStyle = newHoverStyle;
		performMouseOut();
	}
	
	protected void updateChild(String newStyle, String newHoverStyle){
//		removeStyleName(this.style);
//		removeStyleName(this.hoverStyle);
//		this.style = newStyle;
//		this.hoverStyle = newHoverStyle;
//		performMouseOut();
		throw new RuntimeException("Not implemented");
	}
	
	protected String getHoverStyle() {
		return hoverStyle;
	}

	protected String getStyle() {
		return style;
	}

	protected String getChildHoverStyle() {
		return childHoverStyle;
	}

	protected String getChildStyle() {
		return childStyle;
	}
}

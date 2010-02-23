package org.ttdc.gwt.client.presenters.calendar;

import com.google.gwt.user.client.ui.DockPanel;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.HasText;
import com.google.gwt.user.client.ui.HasWidgets;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.RootPanel;
import com.google.gwt.user.client.ui.SimplePanel;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;

public class CalendarView implements CalendarPresenter.View{
	private final Label title = new Label();
	
	private final SimplePanel headerPanel = new SimplePanel();
	private final SimplePanel prevLink = new SimplePanel(); 
	private final SimplePanel nextLink = new SimplePanel();
	
	private final FlowPanel prevNextPanel = new FlowPanel();
	private final SimplePanel scalePanel = new SimplePanel();
	private final DockPanel controls = new DockPanel();
	
	private final VerticalPanel mainPanel = new VerticalPanel();
	private final SimplePanel calendarPanel = new SimplePanel();
	private final SimplePanel searchPanel = new SimplePanel();
	private final SimplePanel messagePanel = new SimplePanel();
	
	public CalendarView() {
		mainPanel.add(headerPanel);
		mainPanel.add(messagePanel);
		prevNextPanel.add(prevLink);
		prevNextPanel.add(nextLink);
		prevNextPanel.add(title);
		
		controls.setHorizontalAlignment(DockPanel.ALIGN_LEFT);
		controls.add(prevNextPanel, DockPanel.WEST);
		controls.setHorizontalAlignment(DockPanel.ALIGN_RIGHT);
		controls.add(scalePanel, DockPanel.EAST);
		
		controls.setStyleName("tt-fullwidth");
		mainPanel.add(searchPanel);
		
		mainPanel.add(controls);
		
		mainPanel.add(calendarPanel);
		calendarPanel.setStyleName("tt-calendar");
		
		mainPanel.setStyleName("tt-view-container");
	}
	
	public void show(){
		RootPanel.get("content").clear();
		RootPanel.get("content").add(mainPanel);
	}
	
	@Override
	public void clear(){
		calendar().clear();
		scaleTarget().clear();
		prevLink.clear();
		nextLink.clear();
		searchPanel.clear();
		headerPanel.clear();
		
	}
	
	@Override
	public Widget getWidget() {
		// TODO Auto-generated method stub
		throw new RuntimeException("I didnt think that this 'CalendarView.getWidget' method was used");
		//return null;
	}
	
	@Override
	public HasWidgets calendar() {
		return calendarPanel;
	}

	@Override
	public HasWidgets nextLink() {
		return prevLink;
	}

	@Override
	public HasWidgets previousLink() {
		return nextLink;
	}

	@Override
	public HasText title() {
		return title;
	}

	@Override
	public HasWidgets scaleTarget() {
		return scalePanel;
	};

	@Override
	public HasWidgets searchTarget() {
		return searchPanel;
	}

	@Override
	public HasWidgets headerPanel() {
		return headerPanel;
	}

	@Override
	public HasWidgets messagePanel() {
		return messagePanel;
	}
}

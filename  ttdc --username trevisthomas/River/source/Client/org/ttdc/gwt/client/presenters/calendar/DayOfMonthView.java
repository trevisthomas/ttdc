package org.ttdc.gwt.client.presenters.calendar;

import com.google.gwt.event.dom.client.HasClickHandlers;
import com.google.gwt.user.client.ui.Grid;
import com.google.gwt.user.client.ui.HasWidgets;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;

public class DayOfMonthView implements DayOfMonthPresenter.View{
	private final Grid mainPanel = new Grid(2,1);
	private final VerticalPanel threadSummaryTarget = new VerticalPanel();
	private final Label header = new Label();
	
	
	public DayOfMonthView() {
		mainPanel.setStyleName("tt-calendar-month-day");
		header.setStyleName("tt-calendar-month-day-header");
		
		mainPanel.setWidget(0, 0, header);
		mainPanel.setWidget(1, 0,threadSummaryTarget);
	}
	
	@Override
	public Widget getWidget() {
		return mainPanel;
	}

	@Override
	public void setDayOfMonth(int dayOfMonth) {
		//header = new HTMLPanel(""+dayOfMonth);
		
		header.setText(""+dayOfMonth);
		//mainPanel.setWidget(0, 0, header);
	}

	@Override
	public HasWidgets threadSummaryTarget() {
		return threadSummaryTarget;
	}

	@Override
	public HasClickHandlers dayClickTarget() {
		return header;
	}
}

package org.ttdc.gwt.client.presenters.calendar;

import org.ttdc.gwt.client.presenters.util.ClickableHoverSyncPanel;

import com.google.gwt.event.dom.client.HasClickHandlers;
import com.google.gwt.user.client.ui.Grid;
import com.google.gwt.user.client.ui.HasWidgets;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.SimplePanel;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;

public class DayOfMonthView implements DayOfMonthPresenter.View{
	private final SimplePanel mainPanel = new SimplePanel(); 
	private final Grid bodyGrid = new Grid(2,1);
	private final SimplePanel threadSummaryPanel = new SimplePanel();
	private final VerticalPanel threadSummaryVerticalTable = new VerticalPanel();
	private final Label headerLabel = new Label();
	private final ClickableHoverSyncPanel header = new ClickableHoverSyncPanel("tt-color-contrast3","tt-color-contrast3-hover","tt-color-contrast2","tt-color-contrast2-hover");
	
	
	public DayOfMonthView() {
		
	}
	
	@Override
	public Widget getWidget() {
		return mainPanel;
	}

	private void init(){
		mainPanel.add(bodyGrid);
		//header.setDisableHoverStyleOnSelf(true);
		
		mainPanel.setStyleName("tt-calendar-month-day tt-color-contrast2");
		//header.setStyleName("tt-color-contrast3");
		header.addStyleName("tt-text-large-bold");
		header.add(headerLabel);
		bodyGrid.addStyleName("tt-fill-both");
		bodyGrid.setWidget(0, 0, header);
		bodyGrid.getRowFormatter().addStyleName(0, "tt-calendar-month-day-header");
		
		threadSummaryPanel.setStyleName("tt-fill-both tt-border tt-calendar-month-day-body");
		threadSummaryPanel.add(threadSummaryVerticalTable);
		threadSummaryVerticalTable.setStyleName("tt-text-small");
		bodyGrid.setWidget(1, 0,threadSummaryPanel);
		
		header.addSynchedHoverTarget(threadSummaryPanel);
	}
	@Override
	public void setDayOfMonth(int dayOfMonth) {
		//headerLabel = new HTMLPanel(""+dayOfMonth);
		
		headerLabel.setText(""+dayOfMonth);
		//mainPanel.setWidget(0, 0, headerLabel);
		init();
	}

	@Override
	public HasWidgets threadSummaryTarget() {
		return threadSummaryVerticalTable;
	}

	@Override
	public HasClickHandlers dayClickTarget() {
		return header;
	}
	
	@Override
	public ClickableHoverSyncPanel getSynchTarget() {
		return header;
	}
}

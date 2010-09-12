package org.ttdc.gwt.client.presenters.calendar;

import java.util.HashMap;
import java.util.Map;

import org.ttdc.gwt.client.presenters.util.ClickableHoverSyncPanel;

import com.google.gwt.event.dom.client.HasClickHandlers;
import com.google.gwt.user.client.ui.Grid;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HasHTML;
import com.google.gwt.user.client.ui.HasText;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.Widget;

public class MonthDetailView implements MonthDetailPresenter.View{
	private final Grid mainPanel = new Grid(7,8);
	
	
	@Override
	public Widget getWidget() {
		return mainPanel;
	}
	
	public MonthDetailView() {
		mainPanel.setStyleName("tt-center");
		setupHeader();
	}
	
	private final void setupHeader(){
		for(int dayOfWeek = 1 ; dayOfWeek < 8 ; dayOfWeek++)
			mainPanel.setWidget(0, dayOfWeek, new Label(CalendarHelpers.getDayName(dayOfWeek)));
	}

	@Override
	public void insertDay(int weekOfMonth, int dayOfWeek, int dayOfMonth, Widget widget, ClickableHoverSyncPanel synchPanel) {
		getWeekSyncPanel(weekOfMonth).addSynchedHoverTarget(synchPanel);
		mainPanel.setWidget(weekOfMonth, dayOfWeek, widget);
	}

	@Override
	public HasClickHandlers weekTargetClickHandlers(int weekOfMonth) {
		return getWeekSyncPanel(weekOfMonth);
	}

	@Override
	public HasHTML weekTargetText(int weekOfMonth) {
		return (HTML)getWeekSyncPanel(weekOfMonth).getWidget();
	}
	
	
	private ClickableHoverSyncPanel getWeekSyncPanel(int weekOfMonth){
		return getWeekLabelFromGrid(weekOfMonth);
	}
	
	private final Map<Integer, ClickableHoverSyncPanel> map = new HashMap<Integer, ClickableHoverSyncPanel>();
	
	private ClickableHoverSyncPanel getWeekLabelFromGrid(int weekOfMonth){
		if(map.containsKey(weekOfMonth)){
			return map.get(weekOfMonth);
		}
		else{
			ClickableHoverSyncPanel clickablePanel = new ClickableHoverSyncPanel("tt-color-month-week-header","tt-color-month-week-header-hover");
			Grid table = new Grid(2,1);
			table.setStyleName("tt-fill-both");
			table.getRowFormatter().addStyleName(0, "tt-calendar-month-day-header");
			table.setWidget(1, 0, clickablePanel);
			
			//Label label = new Label();
			
			HTML label = new HTML();
			
			clickablePanel.add(label);
			mainPanel.setWidget(weekOfMonth, 0, table);
			mainPanel.getCellFormatter().addStyleName(weekOfMonth, 0, "tt-fill-height");
			clickablePanel.addStyleName("tt-fill-both");
			
			map.put(weekOfMonth, clickablePanel);
			
			return clickablePanel;
		}
	}

}

package org.ttdc.gwt.client.presenters.calendar;

import org.ttdc.gwt.client.presenters.util.ClickableHoverSyncPanel;

import com.google.gwt.event.dom.client.HasClickHandlers;
import com.google.gwt.user.client.ui.Grid;
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
	public void insertDay(int weekOfMonth, int dayOfWeek, int dayOfMonth, Widget widget) {
		getWeekSyncPanel(weekOfMonth).addSynchedHoverTarget(widget);
		mainPanel.setWidget(weekOfMonth, dayOfWeek, widget);
	}

	@Override
	public HasClickHandlers weekTargetClickHandlers(int weekOfMonth) {
		return getWeekSyncPanel(weekOfMonth);
	}

	@Override
	public HasText weekTargetText(int weekOfMonth) {
		return (Label)getWeekSyncPanel(weekOfMonth).getWidget();
	}
	
	@Override
	public ClickableHoverSyncPanel getWeekSyncPanel(int weekOfMonth){
		return getWeekLabelFromGrid(weekOfMonth);
	}
	
	private ClickableHoverSyncPanel getWeekLabelFromGrid(int weekOfMonth){
		Widget w = mainPanel.getWidget(weekOfMonth, 0);
		if(w != null){
			return (ClickableHoverSyncPanel)w;
		}
		else{
			ClickableHoverSyncPanel clickablePanel = new ClickableHoverSyncPanel("tt-color-contrast2","tt-color-contrast2-hover");
			Label label = new Label();
			clickablePanel.add(label);
			mainPanel.setWidget(weekOfMonth, 0, clickablePanel);
			mainPanel.getCellFormatter().addStyleName(weekOfMonth, 0, "tt-fill-height");
			clickablePanel.addStyleName("tt-fill-both");
			return clickablePanel;
		}
	}

}

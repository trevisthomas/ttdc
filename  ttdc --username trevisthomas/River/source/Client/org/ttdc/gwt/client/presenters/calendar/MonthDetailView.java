package org.ttdc.gwt.client.presenters.calendar;

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
		setupHeader();
	}
	
	private final void setupHeader(){
		for(int dayOfWeek = 1 ; dayOfWeek < 8 ; dayOfWeek++)
			mainPanel.setWidget(0, dayOfWeek, new Label(CalendarHelpers.getDayName(dayOfWeek)));
	}

	@Override
	public void insertDay(int weekOfMonth, int dayOfWeek, int dayOfMonth, Widget widget) {
		mainPanel.setWidget(weekOfMonth, dayOfWeek, widget);
	}

	@Override
	public HasClickHandlers weekTargetClickHandlers(int weekOfMonth) {
		return getWeekLabelFromGrid(weekOfMonth);
	}

	@Override
	public HasText weekTargetText(int weekOfMonth) {
		return getWeekLabelFromGrid(weekOfMonth);
	}
	
	private Label getWeekLabelFromGrid(int weekOfMonth){
		Widget w = mainPanel.getWidget(weekOfMonth, 0);
		if(w != null){
			return (Label)w;
		}
		else{
			Label label = new Label();
			mainPanel.setWidget(weekOfMonth, 0, label);
			return label;
		}
	}

}

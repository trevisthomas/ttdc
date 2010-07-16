package org.ttdc.gwt.client.presenters.calendar;

import com.google.gwt.user.client.ui.Grid;
import com.google.gwt.user.client.ui.Widget;

public class YearView implements YearPresenter.View{
	private final Grid mainPanel = new Grid(3,4);
	
	
	public YearView() {
		mainPanel.setStyleName("tt-center");
	}
	
	@Override
	public Widget getWidget() {
		return mainPanel;
	}

	@Override
	public void insertMonth(int monthOfYear, Widget widget) {
		int row = 0;
		int col = monthOfYear;
		if(monthOfYear > 8){
			row = 2;
			col = monthOfYear - 8;
		}
		else if(monthOfYear > 4){
			row = 1;
			col = monthOfYear - 4;
		}
		int i = monthOfYear-1;
		mainPanel.setWidget(row, col-1 , widget);
		//mainPanel.addStyleName("")
		//mainPanel.getCellFormatter().setWidth(row, col-1, "230");
		mainPanel.getCellFormatter().setStyleName(row, col-1, "tt-calendar-year-month-cell");
		widget.addStyleName("tt-center");
	}
	
}

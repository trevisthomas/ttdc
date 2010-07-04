package org.ttdc.gwt.client.presenters.calendar;

import com.google.gwt.user.client.ui.Grid;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.SimplePanel;
import com.google.gwt.user.client.ui.Widget;

public class WeekView implements WeekPresenter.View{
	private final Grid hours = new Grid(25,8);
	private final SimplePanel mainPanel = new SimplePanel();
	
	public WeekView() {
		generateSidebar();
		mainPanel.setStyleName("tt-calendar-week");
		mainPanel.add(hours);
	}
	
	//TODO somehow share this with DayView
	private void generateSidebar(){
		String html;
		for(int h = 0; h < 24 ; h++){
			if(h < 12){
				html = (h!=0?h:12)+"a";
			}
			else if(h == 12){
				html = "Noon";
			}
			else {
				html = h-12+"p";
			}
			HTMLPanel htmlPanel = new HTMLPanel(html);
			htmlPanel.setStyleName("tt-calendar-hour-label");
			hours.setWidget(h+1, 0, htmlPanel);
			
			if(h%2 != 0 ){
				for(int i = 0 ; i < 8 ; i++){
					hours.getCellFormatter().setStyleName(h+1, i, "tt-graybar");
				}
			}
			for(int i = 1 ; i < 8 ; i++){ //Skip the first col
				hours.getCellFormatter().addStyleName(h+1, i, "tt-calendar-day-of-week");
			}
			hours.getCellFormatter().addStyleName(h+1, 0, "tt-calendar-hour-label-cell");
			
		}
	}
	
	@Override
	public Widget getWidget() {
		return mainPanel.getWidget();
	}

	@Override
	public void insertHourWidget(int dayOfWeek, int hourOfDay, Widget w) {
		/* WOW. It took me a good hour and a half to find this bug.  The +1 was missing which caused
		 * all of the data on a week to be shifted.  It was shifting by one hour per day, so that by the 
		 * of the week it was off by 7 hours.  Very strange looking.
		 */
		hours.setWidget(hourOfDay+1,dayOfWeek,w);
	}
	
	@Override
	public void insertDayHeader(int dayOfWeek, Widget w) {
		w.setStyleName("tt-calendar-day-header");
		hours.setWidget(0, dayOfWeek, w);
	}
	
}

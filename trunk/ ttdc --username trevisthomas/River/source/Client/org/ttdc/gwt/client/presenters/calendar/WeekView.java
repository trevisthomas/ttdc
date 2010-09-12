package org.ttdc.gwt.client.presenters.calendar;

import org.ttdc.gwt.client.presenters.util.ClickableHoverSyncPanel;

import com.google.gwt.user.client.ui.Grid;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.SimplePanel;
import com.google.gwt.user.client.ui.Widget;

public class WeekView implements WeekPresenter.View{
	private final Grid hours = new Grid(25,8);
	private final SimplePanel mainPanel = new SimplePanel();
	
	public WeekView() {
		generateSidebar();
		mainPanel.setStyleName("tt-calendar-week ");
		mainPanel.add(hours);
		
		HTML blank = new HTML("&nbsp;");
		blank.setStyleName("tt-calendar-hour-label ");
		hours.setStyleName("tt-center");
		hours.getCellFormatter().addStyleName(0, 0, "tt-fill-height");
		hours.setWidget(0, 0, blank);
	}
	
	//TODO somehow share this with DayView
	private void generateSidebar(){
		String html;
		for(int h = 0; h < 24 ; h++){
			if(h < 12){
				html = (h!=0?h:12)+"a";
			}
			else if(h == 12){
				html = "N";
			}
			else {
				html = h-12+"p";
			}
			HTMLPanel htmlPanel = new HTMLPanel(html);
			htmlPanel.setStyleName("tt-calendar-hour-label tt-text-huge tt-color-calendar-hour-label");
			hours.setWidget(h+1, 0, htmlPanel);
			
			for(int i = 1 ; i < 8 ; i++){ //Skip the first col
				//hours.getCellFormatter().addStyleName(h+1, i, "tt-calendar-week-day tt-color-contrast2 tt-border-bottom-right");
				hours.getCellFormatter().addStyleName(h+1, i, "tt-calendar-week-day");
			}
			hours.getCellFormatter().addStyleName(h+1, 0, "tt-fill-height tt-special-border-bottom");
		}
		
		for(int i = 1 ; i < 8 ; i++){ //Skip the first col
			hours.getCellFormatter().addStyleName(0, i, "tt-calendar-week-day-header");
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
		w.addStyleName("tt-calendar-week-hour");
	}
	
	@Override
	public void insertDayHeader(int dayOfWeek, ClickableHoverSyncPanel panel) {
		hours.setWidget(0, dayOfWeek, panel);
		
		for(int h = 1; h < 25 ; h++){
			Widget w = hours.getWidget(h, dayOfWeek);	
			panel.addSynchedHoverTarget(w);
		}
	}
	
}

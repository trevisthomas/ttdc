package org.ttdc.gwt.client.presenters.calendar;

import com.google.gwt.user.client.ui.Grid;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.SimplePanel;
import com.google.gwt.user.client.ui.Widget;

public class DayView implements DayPresenter.View{
	//private final VerticalPanel hours = new VerticalPanel();
	private final Grid hours = new Grid(24,2);
	private final SimplePanel mainPanel = new SimplePanel();
	public DayView() {
		generateSidebar();
		mainPanel.addStyleName("tt-calendar-day");
		mainPanel.add(hours);
	}
	
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
			hours.setWidget(h, 0, htmlPanel);
			hours.getCellFormatter().setStyleName(h, 0, "tt-calendar-hour-label-cell");
			if(h%2 == 0 ){
				hours.getCellFormatter().setStyleName(h, 0, "tt-graybar");
				hours.getCellFormatter().setStyleName(h, 1, "tt-graybar");
			}
			
			//hours.getRowFormatter().setStyleName(h, "tt-border");
		}
	}
	
	@Override
	public void insertHourWidget(int hour, Widget widget) {
		hours.setWidget(hour,1,widget);
	}
	
	@Override
	public Widget getWidget() {
		return mainPanel;
	}
}

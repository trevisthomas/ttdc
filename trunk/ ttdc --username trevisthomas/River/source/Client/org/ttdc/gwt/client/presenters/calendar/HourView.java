package org.ttdc.gwt.client.presenters.calendar;

import com.google.gwt.user.client.ui.HasWidgets;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;

public class HourView implements HourPresenter.View{
	private final VerticalPanel mainPanel = new VerticalPanel();
	public HourView() {
		mainPanel.addStyleName("tt-calendar-hour");
	}
	@Override
	public HasWidgets calendarPostTarget() {
		return mainPanel;
	};
	
	@Override
	public Widget getWidget() {
		return mainPanel;
	}
}

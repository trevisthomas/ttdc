package org.ttdc.gwt.client.uibinder.calendar;


import java.util.Date;

import org.ttdc.gwt.client.Injector;
import org.ttdc.gwt.client.messaging.history.HistoryToken;
import org.ttdc.gwt.client.presenters.calendar.CalendarHelpers;
import org.ttdc.gwt.client.presenters.util.ClickableHoverSyncPanel;

import com.google.gwt.core.client.GWT;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.Widget;
import com.google.inject.Inject;

public class CalendarBreadCrumbPanel extends Composite{
	interface MyUiBinder extends UiBinder<Widget, CalendarBreadCrumbPanel> {}
    private static final MyUiBinder binder = GWT.create(MyUiBinder.class);
    
    private Injector injector;
    @UiField(provided = true) ClickableHoverSyncPanel prevElement;  
    @UiField(provided = true) ClickableHoverSyncPanel nextElement;
    @UiField(provided = true) ClickableHoverSyncPanel yearElement;
    @UiField(provided = true) ClickableHoverSyncPanel monthElement;
    @UiField(provided = true) ClickableHoverSyncPanel weekElement;
    //@UiField(provided = true) Widget dayElement = new Widget();
    
    @Inject
    public CalendarBreadCrumbPanel(Injector injector) { 
    	this.injector = injector;
    	
    	prevElement = buildMeAClickable("<<");
    	nextElement = buildMeAClickable(">>");
    	yearElement = buildMeAClickable("Year");
    	monthElement = buildMeAClickable("Month");
    	weekElement = buildMeAClickable("Week");
    	
    	initWidget(binder.createAndBindUi(this));
	}

    public void setYear(final int year){
    	HistoryToken token = CalendarHelpers.buildYearHistoryToken(year);
    	yearElement.setToken(token);
    }

	private ClickableHoverSyncPanel buildMeAClickable(final String lableText) {
		ClickableHoverSyncPanel button = new ClickableHoverSyncPanel("tt-color-calendar-nav","tt-color-calendar-nav-hover");
    	button.addStyleName("tt-calendar-scale-button tt-border");
    	button.add(new Label(lableText));
		return button;
	}
    
    public void setMonth(final int year, final int month){
    	HistoryToken token = CalendarHelpers.buildMonthHistoryToken(year, month);
    	monthElement.setToken(token);
    }

	public void setWeek(final int weekOfYear, final int year, final Date startDate, final Date endDate){
		HistoryToken token = CalendarHelpers.buildWeekHistoryToken(year, weekOfYear);
		weekElement.setToken(token);
	}
	
    public void setPrevNext(HistoryToken prevToken, HistoryToken nextToken){
    	prevElement.setToken(prevToken);
    	nextElement.setToken(nextToken);
    }
    
    @Override
    public Widget getWidget() {
    	return this;
    }

}

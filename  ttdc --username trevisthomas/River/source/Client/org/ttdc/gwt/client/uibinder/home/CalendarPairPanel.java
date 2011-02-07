package org.ttdc.gwt.client.uibinder.home;

import java.util.Date;

import org.ttdc.gwt.client.Injector;
import org.ttdc.gwt.client.uibinder.calendar.SmallMonthPanel;
import org.ttdc.gwt.shared.calender.Day;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.SimplePanel;
import com.google.gwt.user.client.ui.Widget;
import com.google.inject.Inject;

public class CalendarPairPanel extends Composite{
	interface MyUiBinder extends UiBinder<Widget, CalendarPairPanel> {}
    private static final MyUiBinder binder = GWT.create(MyUiBinder.class);
    
    private Injector injector;
    
    @UiField SimplePanel leftCalendarElement;
    @UiField SimplePanel rightCalendarElement;
    
    private Date dateToday;
        
    @Inject
    public CalendarPairPanel(Injector injector) { 
    	this.injector = injector;
    	
    	initWidget(binder.createAndBindUi(this));
	}
    
    
    public void init(){
		dateToday = new Date();
		Date date = dateToday;
		initCalendars(date);
	}


    SmallMonthPanel monthPrevious;
    SmallMonthPanel monthCurrent;
	private void initCalendars(Date date) {
		monthPrevious = injector.createSmallMonthPanel();
    	monthCurrent = injector.createSmallMonthPanel();

		Day day = new Day(date);
		monthCurrent.setSelectedDay(day);
    	monthCurrent.initForDualAsRight(day.getYear(),day.getMonth(), new ClickHandler() {
			
			@Override
			public void onClick(ClickEvent event) {
				monthCurrent.switchToNext();
				monthPrevious.switchToNext();
			}
		});
    	dateToday = new Date();;
		int prevMonth = day.getMonth() - 1;
		int prevMonthYear = day.getYear();
		
		if(prevMonth < 1){
			prevMonth = 12;
			prevMonthYear = day.getYear() - 1;
		}
		
		monthPrevious.initForDualAsLeft(prevMonthYear, prevMonth, new ClickHandler() {
			
			@Override
			public void onClick(ClickEvent event) {
				monthCurrent.switchToPrev();
				monthPrevious.switchToPrev();
			}
		});
		
		leftCalendarElement.clear();
		leftCalendarElement.add(monthPrevious);
		rightCalendarElement.clear();
		rightCalendarElement.add(monthCurrent);
		
	}
}

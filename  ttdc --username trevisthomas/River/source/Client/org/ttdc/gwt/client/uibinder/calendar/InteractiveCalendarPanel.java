package org.ttdc.gwt.client.uibinder.calendar;

import java.util.Date;

import org.ttdc.gwt.client.Injector;
import org.ttdc.gwt.client.presenters.home.CalendarSelectorModulePresenter;
import org.ttdc.gwt.shared.calender.Day;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FocusPanel;
import com.google.gwt.user.client.ui.SimplePanel;
import com.google.gwt.user.client.ui.Widget;
import com.google.inject.Inject;

public class InteractiveCalendarPanel extends Composite{
	interface MyUiBinder extends UiBinder<Widget, InteractiveCalendarPanel> {}
    private static final MyUiBinder binder = GWT.create(MyUiBinder.class);
    
    private Injector injector;
    
    @UiField(provided = true) FocusPanel changeViewElement;
    @UiField SimplePanel dateChoiceElement;
    
    private CalendarSelectorModulePresenter selectorPresenter;
	private SmallMonthPanel monthPresenter;
	private Mode mode;
	private Date dateToday;
	private Object activeUiComponent;
    
    @Inject
    public InteractiveCalendarPanel(Injector injector) { 
    	this.injector = injector;
    	
    	changeViewElement = new FocusPanel();
    	
    	initWidget(binder.createAndBindUi(this));
	}
    
    @Override
    public Widget getWidget() {
    	return this;
    }
    
    public Date getDateToday() {
		return dateToday;
	}

	public enum Mode {CALENDER_INTERFACE_MODE,DATE_PICKER_MODE};
	
	public void init(Mode mode) {
		Date date = new Date();
		init(mode, date);
	}
	
	public void init(Mode mode, Date date) {
		this.mode = mode;
		boolean select = true;
		if(date == null){
			select = false;
			date = new Date();
		}
		dateToday = date;
		Day day = new Day(date);
		showCalendar(day.getYear(),day.getMonth());
		
		if(select){
			monthPresenter.setSelectedDay(day);
		}
	}
	
	@UiHandler("changeViewElement")
	public void onChangeViewElement(ClickEvent e){
		if(activeUiComponent == monthPresenter){
			int year = monthPresenter.getYear();
			int month = monthPresenter.getMonth();
			showSelector(year,month);
		}	
		else if(activeUiComponent == selectorPresenter){
			int year = selectorPresenter.getSelectedYear();
			int month = selectorPresenter.getSelectedMonth();
			
			if(!(monthPresenter.getYear() == year && monthPresenter.getMonth() == month)){
				monthPresenter = null;
			}
			
			showCalendar(year,month);
		}
	}
	
	private void showSelector(int year, int monthOfYear){
		changeViewElement.setStyleName("tt-center ui-icon ui-icon-carat-1-n tt-cursor-pointer tt-center;");
		selectorPresenter = injector.getCalendarSelectorModulePresenter();
		selectorPresenter.init(year, monthOfYear);
		dateChoiceElement.clear();
		dateChoiceElement.add(selectorPresenter.getWidget());
		activeUiComponent = selectorPresenter;
	}
	
	private void showCalendar(int year, int monthOfYear) {
		changeViewElement.setStyleName("tt-center ui-icon ui-icon-carat-1-s tt-cursor-pointer");
		if(monthPresenter == null){
			monthPresenter = injector.createSmallMonthPanel();
			monthPresenter.initInteractive(year, monthOfYear);
			if(mode.equals(Mode.DATE_PICKER_MODE))
				monthPresenter.setSelectableDayMode(true);
			else
				monthPresenter.setSelectableDayMode(false);
		}
		dateChoiceElement.clear();
		dateChoiceElement.add(monthPresenter.getWidget());
		activeUiComponent = monthPresenter;
	}
	
	public Day getSelectedDay(){
		return monthPresenter.getSelectedDay();
	}

	
}
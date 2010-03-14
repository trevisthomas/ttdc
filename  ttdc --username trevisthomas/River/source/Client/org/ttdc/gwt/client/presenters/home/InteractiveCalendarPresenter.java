package org.ttdc.gwt.client.presenters.home;

import java.util.Date;

import org.ttdc.gwt.client.Injector;
import org.ttdc.gwt.client.presenters.calendar.MonthPresenter;
import org.ttdc.gwt.client.presenters.shared.BasePresenter;
import org.ttdc.gwt.client.presenters.shared.BaseView;
import org.ttdc.gwt.client.presenters.util.PresenterHelpers;
import org.ttdc.gwt.shared.calender.Day;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.HasClickHandlers;
import com.google.gwt.user.client.ui.HasWidgets;
import com.google.inject.Inject;

public class InteractiveCalendarPresenter extends BasePresenter<InteractiveCalendarPresenter.View>{
	public interface View extends BaseView{
		HasWidgets calendarPanel();
		HasWidgets selectorPanel();
		public void showSelector();
		public void showCalendar();
		
		HasClickHandlers showSelectorClickHandler();
		HasClickHandlers hideSelectorClickHandler();
	}
	private CalendarSelectorModulePresenter selectorPresenter;
	private MonthPresenter monthPresenter;
	private Mode mode;

	@Inject
	protected InteractiveCalendarPresenter(Injector injector) {
		super(injector, injector.getInteractiveCalendarView());
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
		Day day = new Day(date);
		showCalendar(day.getYear(),day.getMonth());
		
		if(select){
			monthPresenter.setSelectedDay(day);
		}
		
		view.showSelectorClickHandler().addClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				int year = monthPresenter.getYear();
				int month = monthPresenter.getMonth();
				showSelector(year,month);
			}
		});
		view.hideSelectorClickHandler().addClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				int year = selectorPresenter.getSelectedYear();
				int month = selectorPresenter.getSelectedMonth();
				
				if(!(monthPresenter.getYear() == year && monthPresenter.getMonth() == month)){
					view.calendarPanel().clear();
				}
				
				showCalendar(year,month);
			}
		});
	}
	
	private void showSelector(int year, int monthOfYear){
		selectorPresenter = injector.getCalendarSelectorModulePresenter();
		selectorPresenter.init(year, monthOfYear);
		view.selectorPanel().clear();//Maybe reuse?
		view.selectorPanel().add(selectorPresenter.getWidget());
		view.showSelector();
	}
	
	private void showCalendar(int year, int monthOfYear) {
		if(PresenterHelpers.isWidgetEmpty(view.calendarPanel())){
			monthPresenter = injector.getMonthPresenter();
			monthPresenter.init(year, monthOfYear);
			if(mode.equals(Mode.DATE_PICKER_MODE))
				monthPresenter.setSelectableDayMode(true);
			else
				monthPresenter.setSelectableDayMode(false);
			view.calendarPanel().clear();
			view.calendarPanel().add(monthPresenter.getWidget());
		}
		view.showCalendar();
	}
	
	public Day getSelectedDay(){
		return monthPresenter.getSelectedDay();
	}
}

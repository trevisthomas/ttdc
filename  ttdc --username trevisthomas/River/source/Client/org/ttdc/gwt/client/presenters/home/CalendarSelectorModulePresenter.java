package org.ttdc.gwt.client.presenters.home;

import org.ttdc.gwt.client.Injector;
import org.ttdc.gwt.client.presenters.shared.BasePresenter;
import org.ttdc.gwt.client.presenters.shared.BaseView;
import com.google.gwt.event.dom.client.HasClickHandlers;
import com.google.inject.Inject;

public class CalendarSelectorModulePresenter extends BasePresenter<CalendarSelectorModulePresenter.View>{
	public interface View extends BaseView{
		void startFromYear(int year);
		
		void setSelectedYear(int year);
		void setSelectedMonth(int month);
		int getSelectedYear();
		int getSelectedMonth();
		
		HasClickHandlers nextYearPageClickHandler();
		HasClickHandlers prevYearPageClickHandler();
	}
	private int startYear;
	
	@Inject
	protected CalendarSelectorModulePresenter(Injector injector) {
		super(injector, injector.getCalendarSelectorModuleView());
	}
	
	public int getSelectedYear(){
		return view.getSelectedYear();
	}
	public int getSelectedMonth(){
		return view.getSelectedMonth();
	}
	
	public void init(int year, int monthOfYear){
		startYear = determineStartYear(year);
		view.startFromYear(startYear);
		view.setSelectedYear(year);
		view.setSelectedMonth(monthOfYear);
	}
	
	private int determineStartYear(int year) {
		return year - 8;
	}
}

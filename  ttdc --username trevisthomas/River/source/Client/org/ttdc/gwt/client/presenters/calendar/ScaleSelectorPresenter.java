package org.ttdc.gwt.client.presenters.calendar;

import org.ttdc.gwt.client.Injector;
import org.ttdc.gwt.client.messaging.history.HistoryConstants;
import org.ttdc.gwt.client.messaging.history.HistoryToken;
import org.ttdc.gwt.client.presenters.shared.BasePresenter;
import org.ttdc.gwt.client.presenters.shared.BaseView;
import org.ttdc.gwt.client.presenters.shared.HyperlinkPresenter;

import com.google.gwt.user.client.ui.HasWidgets;
import com.google.inject.Inject;

public class ScaleSelectorPresenter extends BasePresenter<ScaleSelectorPresenter.View>{
	@Inject
	public ScaleSelectorPresenter(Injector injector) {
		super(injector, injector.getScaleSelectorView());
	}
	
	public interface View extends BaseView{
		HasWidgets yearTarget();
		HasWidgets monthTarget();
		HasWidgets weekTarget();
		HasWidgets dayTarget();
		
		void setSelectionYear();
		void setSelectionMonth();
		void setSelectionWeek();
		void setSelectionDay();
	}
	
	private int year;
	private int monthOfYear;
	private int weekOfYear;
	private int dayOfMonth;
		
	private void setupClickHandlers() {
		HyperlinkPresenter link;
		HistoryToken token;
		
		link = injector.getHyperlinkPresenter();
		token = CalendarHelpers.buildDayHistoryToken(year, monthOfYear, dayOfMonth);
		link.setToken(token, "Day");
		view.dayTarget().add(link.getWidget());
		
		link = injector.getHyperlinkPresenter();
		token = CalendarHelpers.buildWeekHistoryToken(year, weekOfYear);
		link.setToken(token, "Week");
		view.weekTarget().add(link.getWidget());
		
		link = injector.getHyperlinkPresenter();
		token = CalendarHelpers.buildMonthHistoryToken(year, monthOfYear);
		link.setToken(token, "Month");
		view.monthTarget().add(link.getWidget());
		
		link = injector.getHyperlinkPresenter();
		token = CalendarHelpers.buildYearHistoryToken(year);
		link.setToken(token, "Year");
		view.yearTarget().add(link.getWidget());
		
	}

	public void setToday(int year, int monthOfYear, int weekOfYear, int dayOfMonth, String scale){
		this.year = year;
		this.monthOfYear = monthOfYear;
		this.weekOfYear = weekOfYear;
		this.dayOfMonth = dayOfMonth;
		setScale(scale);
		setupClickHandlers();
	} 
	
	private void setScale(String scale){
		if(HistoryConstants.CALENDAR_SCALE_VALUE_DAY.equals(scale)){
			view.setSelectionDay();
		}
		else if(HistoryConstants.CALENDAR_SCALE_VALUE_WEEK.equals(scale)){
			view.setSelectionWeek();
		}
		else if(HistoryConstants.CALENDAR_SCALE_VALUE_MONTH.equals(scale)){
			view.setSelectionMonth();
		}
		else if(HistoryConstants.CALENDAR_SCALE_VALUE_YEAR.equals(scale)){
			view.setSelectionYear();
		}
		else{
			throw new RuntimeException("Invalid calendar scale value");
		}
	}

}

package org.ttdc.gwt.client.presenters.calendar;

import org.ttdc.gwt.client.Injector;
import org.ttdc.gwt.client.messaging.history.HistoryConstants;
import org.ttdc.gwt.client.messaging.history.HistoryToken;
import org.ttdc.gwt.client.presenters.shared.BasePresenter;
import org.ttdc.gwt.client.presenters.shared.BaseView;
import org.ttdc.gwt.client.presenters.shared.HyperlinkPresenter;
import org.ttdc.gwt.shared.calender.Day;
import org.ttdc.gwt.shared.calender.Hour;
import org.ttdc.gwt.shared.calender.Week;

import com.google.gwt.user.client.ui.Widget;
import com.google.inject.Inject;


public class WeekPresenter extends BasePresenter<WeekPresenter.View>{
	
	
	@Inject
	public WeekPresenter(Injector injector) {
		super(injector, injector.getWeekView());
	}

	public interface View extends BaseView{
		void insertHourWidget(int dayOfWeek, int hourOfDay, Widget w);
		void insertDayHeader(int dayOfWeek, Widget w); //maybe this should just be a presenter
	}
	
	public void setWeek(Week week){
		int dayOfWeek = 1;
		for(Day day : week.getDays()){
			if(day.isContent()){
				for(Hour h : day.getHours()){
					HourPresenter hourPresenter = injector.getHourPresenter();
					hourPresenter.setHour(h);
					view.insertHourWidget(dayOfWeek, h.getHourOfDay(), hourPresenter.getWidget());
				}
			}
			HyperlinkPresenter linkPresenter = injector.getHyperlinkPresenter();
			HistoryToken token = CalendarHelpers.buildDayHistoryToken(day.getYear(),day.getMonth(),day.getDay());
			String linkText = day.getMonth()+"/"+day.getDay() +" "+ CalendarHelpers.getDayName(dayOfWeek);
			
			linkPresenter.setToken(token, linkText);
			view.insertDayHeader(dayOfWeek, linkPresenter.getWidget());
//			view.insertDayHeader(day.getMonth(), dayOfWeek,day.getDay(), dayNames[dayOfWeek-1]);
			dayOfWeek++;
		}
	}
		
}

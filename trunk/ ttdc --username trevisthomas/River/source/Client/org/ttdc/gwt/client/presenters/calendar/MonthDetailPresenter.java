package org.ttdc.gwt.client.presenters.calendar;

import java.util.List;

import org.ttdc.gwt.client.Injector;
import org.ttdc.gwt.client.messaging.history.HistoryToken;
import org.ttdc.gwt.client.presenters.shared.BasePresenter;
import org.ttdc.gwt.client.presenters.shared.BaseView;
import org.ttdc.gwt.client.presenters.util.ClickableHoverSyncPanel;
import org.ttdc.gwt.shared.calender.Day;
import org.ttdc.gwt.shared.calender.Month;
import org.ttdc.gwt.shared.calender.Week;

import com.google.gwt.event.dom.client.HasClickHandlers;
import com.google.gwt.user.client.ui.HasHTML;
import com.google.gwt.user.client.ui.HasText;
import com.google.gwt.user.client.ui.Widget;
import com.google.inject.Inject;

public class MonthDetailPresenter extends BasePresenter<MonthDetailPresenter.View>{
	@Inject
	public MonthDetailPresenter(Injector injector) {
		super(injector, injector.getMonthDetailView());
	}
	
	public interface View extends BaseView{
		void insertDay(int weekOfMonth, int dayOfWeek, int dayOfMonth, Widget widget, ClickableHoverSyncPanel synchPanel);
		HasClickHandlers weekTargetClickHandlers(int weekOfMonth);
		HasHTML weekTargetText(int weekOfMonth);
		//ClickableHoverSyncPanel getWeekSyncPanel(int weekOfMonth); 
	}
	
	public void setMonth(Month month){
		List<Week> weeks = month.getWeeks();
		int weekOfMonth = 1;
		for(Week week : weeks){
			//load week header
			//view.weekTargetText(weekOfMonth).setText("#"+week.getWeekOfYear());
			view.weekTargetText(weekOfMonth).setHTML("&nbsp;&raquo;&nbsp;");
			HistoryToken token = CalendarHelpers.buildWeekHistoryToken(month.getYearNumber(), week.getWeekOfYear());
			view.weekTargetClickHandlers(weekOfMonth).addClickHandler(new WeekClickHandler(token));
			int dayOfWeek = 1;
			for(Day day : week.getDays()){
				DayOfMonthPresenter domPresenter = injector.getDayOfMonthPresenter();
				domPresenter.setCalendarThreadSummaryList(day);
				view.insertDay(weekOfMonth, dayOfWeek, day.getDay(),domPresenter.getWidget(), domPresenter.getSynchTarget());
				dayOfWeek++;
			}
			weekOfMonth++;
		}
	}
}

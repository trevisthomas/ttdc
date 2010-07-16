package org.ttdc.gwt.client.presenters.calendar;

import java.util.Date;
import java.util.List;

import org.ttdc.gwt.client.Injector;
import org.ttdc.gwt.client.messaging.EventBus;
import org.ttdc.gwt.client.messaging.history.HistoryToken;
import org.ttdc.gwt.client.presenters.calendar.MonthView.ClickableDay;
import org.ttdc.gwt.client.presenters.calendar.MonthView.DayClickHandler;
import org.ttdc.gwt.client.presenters.shared.BasePresenter;
import org.ttdc.gwt.client.presenters.shared.BaseView;
import org.ttdc.gwt.client.presenters.shared.HyperlinkPresenter;
import org.ttdc.gwt.shared.calender.Day;
import org.ttdc.gwt.shared.calender.Month;
import org.ttdc.gwt.shared.calender.Week;
import org.ttdc.gwt.shared.commands.CalendarCommand;
import org.ttdc.gwt.shared.commands.CommandResultCallback;
import org.ttdc.gwt.shared.commands.results.CalendarCommandResult;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.HasClickHandlers;
import com.google.gwt.user.client.ui.HasWidgets;
import com.google.gwt.user.client.ui.Widget;
import com.google.inject.Inject;

public class MonthPresenter extends BasePresenter<MonthPresenter.View> implements DayClickHandler{
	public interface View extends BaseView{
		//void insertDay(int weekOfMonth, int dayOfWeek, int dayOfMonth, Day day, Widget widget);
		void insertDay(int weekOfMonth, int dayOfWeek, int dayOfMonth, Day day);
		HasClickHandlers weekTargetClickHandlers(int weekOfMonth);
		HasWidgets monthHeaderTarget();
		HasWidgets yearHeaderTarget();
		
		HasClickHandlers prevMonthClickHandlers();
		HasClickHandlers nextMonthClickHandlers();
		
		void enablePrevNext(boolean enable);
		
		void clear();
		void setDayClickHandler(DayClickHandler handler);
		void setSelectedDay(Day day);
		void deselectDay();
		Day getSelectedDay();
		void enableSelectableDayMode(boolean enable);
		
		HasClickHandlers clearClickHandlers();
		HasClickHandlers nowClickHandlers();
	}
	
	
	//Note, this is the prev and next year of the prev and next month. (for jan/december crossovers)
	private int prevYear;
	private int prevMonth;
	private int nextYear;
	private int nextMonth;
	private int year;
	private int month;
	//private boolean interactive;
	private Day now;
	
	private Day selectedDay = null;
	
	

	private boolean selectableDayMode = false; 
	
	private DayClickHandler dayClickHandler = null;

	@Inject
	public MonthPresenter(Injector injector) {
		super(injector, injector.getMonthView());
		
		now = new Day(new Date());
		
		view.prevMonthClickHandlers().addClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				init(prevYear,prevMonth,MonthPresenter.this.dayClickHandler);
			}
		});
		
		view.nextMonthClickHandlers().addClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				init(nextYear,nextMonth,MonthPresenter.this.dayClickHandler);
			}
		});
		
		view.clearClickHandlers().addClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				view.deselectDay();
			}
		});
		
		view.nowClickHandlers().addClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				view.setSelectedDay(now);				
			}
		});
		
		view.enableSelectableDayMode(selectableDayMode);
	}
	
	
	public void init(Month month){
		renderMonth(month,false);
		view.setDayClickHandler(this);
		//view.enableSelectableDayMode(false);
	}
	
	public void initInteractive(int year,int monthOfYear){
		if(dayClickHandler == null)
			init(year, monthOfYear, this);
		else
			init(year, monthOfYear, dayClickHandler);
	}
	
	
//	public void init(Day day) {
//		init(day.getYear(), day.getMonth());
//		selectedDay = day; //Cant set it in the view before the data is back.
//	}
	/*
	 * This version of the constructor is because of an idea i had to make this thing more self 
	 * sufficent when operating as a stand alone calendar.  Basically It gets it's own data since it 
	 * needs to have the know how to have a prev next... 
	 */
	public void init(int year,int monthOfYear, DayClickHandler handler){
		if(handler != null){
			this.dayClickHandler = handler;
			view.setDayClickHandler(handler);
		}
		else
			view.setDayClickHandler(this);
		

		
		this.year = year;
		this.month = monthOfYear;
		
		CalendarCommand cmd = new CalendarCommand();
		cmd.setScope(CalendarCommand.Scope.SIMPLE_MONTH);
		cmd.setYear(year);
		cmd.setMonthOfYear(monthOfYear);
		injector.getService().execute(cmd, buildCalendarMonthCallback());
	}
	
	
	
	private CommandResultCallback<CalendarCommandResult> buildCalendarMonthCallback() {
		CommandResultCallback<CalendarCommandResult> callback = new CommandResultCallback<CalendarCommandResult>(){
			@Override
			public void onSuccess(CalendarCommandResult result) {
				view.clear();
				renderMonth(result.getMonth(),true);
				
				view.setSelectedDay(selectedDay);
				
				prevYear = result.getPrevYear();
				prevMonth = result.getPrevMonthOfYear();
				nextYear = result.getNextYear();
				nextMonth = result.getNextMonthOfYear();
			}
		};
		return callback;
	}
	
	private void renderMonth(Month month, boolean standalone){
		HistoryToken token = CalendarHelpers.buildMonthHistoryToken(month.getYearNumber(), month.getMonthNumber());
		HyperlinkPresenter monthLink = injector.getHyperlinkPresenter();
		monthLink.setToken(token, month.getName());
		view.monthHeaderTarget().add(monthLink.getWidget());
		
		if(standalone){
			view.enablePrevNext(true);
			HistoryToken yrToken = CalendarHelpers.buildYearHistoryToken(month.getYearNumber());
			HyperlinkPresenter yearLink = injector.getHyperlinkPresenter();
			yearLink.setToken(yrToken, month.getYearNumber() + "");
			view.yearHeaderTarget().add(yearLink.getWidget());
		}
		
		List<Week> weeks = month.getWeeks();
		int weekOfMonth = 1;
		
		for(Week week : weeks){
			token = CalendarHelpers.buildWeekHistoryToken(month.getYearNumber(), week.getWeekOfYear());
			view.weekTargetClickHandlers(weekOfMonth).addClickHandler(new WeekClickHandler(token));
			int dayOfWeek = 1;
			for(Day day : week.getDays()){
				if(day.isVisable()){
//					HyperlinkPresenter dayLink = injector.getHyperlinkPresenter();
//					if(day.isContent()){
//						token = CalendarHelpers.buildDayHistoryToken(day.getYear(),day.getMonth(),day.getDay());
//						dayLink.setToken(token, ""+day.getDay());
//						view.insertDay(weekOfMonth, dayOfWeek, day.getDay(), day, dayLink.getWidget());
//					}
//					else{
//						view.insertDay(weekOfMonth, dayOfWeek, day.getDay(), day);
//					}
					view.insertDay(weekOfMonth, dayOfWeek, day.getDay(), day);
				}
				dayOfWeek++;
			}
			
			weekOfMonth++;
		}
	}

	public int getYear() {
		return year;
	}

	public int getMonth() {
		return month;
	}

	/*
	 * Called when a user clicks a day in the calendar 
	 */
	@Override
	public void onDayClick(ClickableDay day) {
		if(isSelectableDayMode()){
			//processDayClickAsSelection(day);
			view.setSelectedDay(day.getDay());
			day.highlight();
		}
		else{
			processDayClickAsLink(day.getDay());
		}
	}
	
	
	public boolean isSelectableDayMode() {
		return selectableDayMode;
	}

	public void setSelectableDayMode(boolean selectableDayMode) {
		this.selectableDayMode = selectableDayMode;
		view.enableSelectableDayMode(selectableDayMode);
	}
	
	public void setSelectedDay(Day selectedDay) {
		this.selectedDay = selectedDay;
		view.setSelectedDay(selectedDay);
	}
	
	public Day getSelectedDay(){
		return view.getSelectedDay();
	}

//	public Day getStartDay() {
//		return startDay;
//	}
//	
//	public Day getEndDay() {
//		return endDay;
//	}
	
//	public void setStartDay(Day startDay) {
//		this.startDay = startDay;
//		selectDay(startDay);
//	}
//
//	public void setEndDay(Day endDay) {
//		this.endDay = endDay;
//		selectDay(endDay);
//	}
	
	public void processDayClickAsLink(Day day) {
		if(day.isContent()){
			HistoryToken token = CalendarHelpers.buildDayHistoryToken(day.getYear(),day.getMonth(),day.getDay());
			EventBus.fireHistoryToken(token);
		}
	}

	
	
//	public void processDayClickAsSelection(Day day) {
//		if(startDay == null){
//			initStartDay(day);
//		}
//		else if(endDay == null){
//			initEndDay(day);
//		}
//		else{
//			Date start = startDay.toDate();
//			Date end = endDay.toDate();
//			Date nu = day.toDate();
//			if(start.getTime() < end.getTime()){
//				if(nu.getTime() < start.getTime()){
//					initStartDay(day);
//				}
//				else{
//					initEndDay(day);
//				}
//			}
//			else{//start > end
//				if(nu.getTime() > end.getTime()){
//					initEndDay(day);
//				}
//				else{
//					initStartDay(day);
//				}
//			}
//		}
//	}
//
//	private void initEndDay(Day day) {
//		selectDay(day);
//		if(endDay != null){
//			deselectDay(endDay);
//		}
//		endDay = day;
//		EventBus.fireEvent(new CalendarEvent(CalendarEventType.CALENDAR_RANGE_CHANGED,null));
//	}
//
//	private void initStartDay(Day day) {
//		selectDay(day);
//		if(startDay != null){
//			deselectDay(startDay);
//		}
//		startDay = day;
//		EventBus.fireEvent(new CalendarEvent(CalendarEventType.CALENDAR_RANGE_CHANGED,null));
//	}
}

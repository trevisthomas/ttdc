package org.ttdc.gwt.client.uibinder.calendar;

import static org.ttdc.gwt.client.messaging.history.HistoryConstants.CALENDAR_DAY_OF_MONTH;
import static org.ttdc.gwt.client.messaging.history.HistoryConstants.CALENDAR_MONTH_OF_YEAR;
import static org.ttdc.gwt.client.messaging.history.HistoryConstants.CALENDAR_SCALE_KEY;
import static org.ttdc.gwt.client.messaging.history.HistoryConstants.CALENDAR_SCALE_VALUE_DAY;
import static org.ttdc.gwt.client.messaging.history.HistoryConstants.CALENDAR_SCALE_VALUE_MONTH;
import static org.ttdc.gwt.client.messaging.history.HistoryConstants.CALENDAR_SCALE_VALUE_WEEK;
import static org.ttdc.gwt.client.messaging.history.HistoryConstants.CALENDAR_SCALE_VALUE_YEAR;
import static org.ttdc.gwt.client.messaging.history.HistoryConstants.CALENDAR_WEEK_YEAR;
import static org.ttdc.gwt.client.messaging.history.HistoryConstants.CALENDAR_YEAR;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.ttdc.gwt.client.Injector;
import org.ttdc.gwt.client.messaging.history.HistoryConstants;
import org.ttdc.gwt.client.messaging.history.HistoryToken;
import org.ttdc.gwt.client.presenters.calendar.CalendarHelpers;
import org.ttdc.gwt.client.presenters.calendar.DayPresenter;
import org.ttdc.gwt.client.presenters.calendar.MonthDetailPresenter;
import org.ttdc.gwt.client.presenters.calendar.ScaleSelectorPresenter;
import org.ttdc.gwt.client.presenters.calendar.WeekPresenter;
import org.ttdc.gwt.client.presenters.calendar.YearPresenter;
import org.ttdc.gwt.client.presenters.shared.HyperlinkPresenter;
import org.ttdc.gwt.client.presenters.util.DateFormatUtil;
import org.ttdc.gwt.client.services.RpcServiceAsync;
import org.ttdc.gwt.client.uibinder.common.BasePageComposite;
import org.ttdc.gwt.client.uibinder.shared.StandardPageHeaderPanel;
import org.ttdc.gwt.shared.calender.Day;
import org.ttdc.gwt.shared.commands.CalendarCommand;
import org.ttdc.gwt.shared.commands.CommandResultCallback;
import org.ttdc.gwt.shared.commands.results.CalendarCommandResult;

import com.google.gwt.core.client.GWT;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.SimplePanel;
import com.google.gwt.user.client.ui.Widget;
import com.google.gwt.user.datepicker.client.CalendarUtil;
import com.google.inject.Inject;

public class CalendarPanel extends BasePageComposite{
	interface MyUiBinder extends UiBinder<Widget, CalendarPanel> {}
    private static final MyUiBinder binder = GWT.create(MyUiBinder.class);
    
    private Injector injector;
    private HistoryToken lastToken;
	private final Map<HistoryToken,CalendarCommandResult> cacheMap = new HashMap<HistoryToken,CalendarCommandResult>(); 
	private final StandardPageHeaderPanel pageHeaderPanel;
    private final CalendarBreadCrumbPanel calendarBreadCrumbPanel;
    
    @UiField(provided = true) Widget pageHeaderElement;
    
    @UiField Label calendarTitleElement;
    @UiField(provided = true) SimplePanel calendarBodyElement = new SimplePanel();
    @UiField SimplePanel calendarBreadCrumbElement;    
    
    @Inject
    public CalendarPanel(Injector injector) { 
    	this.injector = injector;
    	pageHeaderPanel = injector.createStandardPageHeaderPanel(); 
    	pageHeaderElement = pageHeaderPanel.getWidget();
    	
    	initWidget(binder.createAndBindUi(this));
    	
    	calendarTitleElement.setText("loading...");
    	calendarBreadCrumbPanel = injector.createBreadCrumbPanel();
    	calendarBreadCrumbElement.add(calendarBreadCrumbPanel);
	}
    
    
    @Override
    public Widget getWidget() {
    	return this;
    }
	
    
    
    @Override  
    protected void onShow(HistoryToken args) {
		if(lastToken != null && args.equals(lastToken)){
//			view.show();
			return;
		}
			
		String scale = args.getParameter(CALENDAR_SCALE_KEY);
		if(CALENDAR_SCALE_VALUE_YEAR.equals(scale)){
			pageHeaderPanel.init("Calendar Year","A Year in the Life");
			showYearCalendar(args);
		}
		else if(CALENDAR_SCALE_VALUE_MONTH.equals(scale)){
			pageHeaderPanel.init("Calendar Month","One Month...");
			showMonthCalendar(args);
		}
		else if(CALENDAR_SCALE_VALUE_WEEK.equals(scale)){
			pageHeaderPanel.init("Calendar Week","Une semain");
			showWeekCalendar(args);
		}
		else if(CALENDAR_SCALE_VALUE_DAY.equals(scale)){
			pageHeaderPanel.init("Calendar Day","One day...");
			showDayCalendar(args);
		}
		else{
			//Defaulting to Month
			//throw new RuntimeException("Unrecongizied scale value \""+scale+"\"");
			Date date = new Date();
			HistoryToken today = CalendarHelpers.buildMonthHistoryToken(1900+date.getYear(), 1+date.getMonth());
			pageHeaderPanel.init("Calendar Month","One Month...");
			showMonthCalendar(today);
		}
		
		lastToken = args;
	
	}
    
    

	private void showYearCalendar(final HistoryToken args) {
		if(cacheMap.containsKey(args)){
			displayCalendarResultsYear(cacheMap.get(args));
		}
		else{
			CalendarCommand cmd = new CalendarCommand();
			cmd.setScope(CalendarCommand.Scope.YEAR);
			cmd.setYear(args.getParameterAsInt(CALENDAR_YEAR));
			
			CommandResultCallback<CalendarCommandResult> callback = new CommandResultCallback<CalendarCommandResult>(){
				@Override
				public void onSuccess(CalendarCommandResult result) {
					cacheMap.put(args,result);
					displayCalendarResultsYear(result);
				}
			};
			RpcServiceAsync service = injector.getService();
			service.execute(cmd, callback);
		}
	}
	
	

	private void showMonthCalendar(final HistoryToken args) {
		if(cacheMap.containsKey(args)){
			displayCalendarResultsMonth(cacheMap.get(args));
		}
		else{
			CalendarCommand cmd = new CalendarCommand();
			cmd.setScope(CalendarCommand.Scope.MONTH);
			cmd.setYear(args.getParameterAsInt(CALENDAR_YEAR));
			cmd.setMonthOfYear(args.getParameterAsInt(CALENDAR_MONTH_OF_YEAR));
			
			CommandResultCallback<CalendarCommandResult> callback = new CommandResultCallback<CalendarCommandResult>(){
				@Override
				public void onSuccess(CalendarCommandResult result) {
					cacheMap.put(args,result);
					displayCalendarResultsMonth(result);
				}
			};
			
			RpcServiceAsync service = injector.getService();
			service.execute(cmd, callback);
		}
	}

	private void showWeekCalendar(final HistoryToken args) {
		if(cacheMap.containsKey(args)){
			displayCalendarResultsWeek(cacheMap.get(args));
		}
		else{
			CalendarCommand cmd = new CalendarCommand();
			cmd.setScope(CalendarCommand.Scope.WEEK);
			cmd.setYear(args.getParameterAsInt(CALENDAR_YEAR));
			cmd.setWeekOfYear(args.getParameterAsInt(CALENDAR_WEEK_YEAR));
			
			CommandResultCallback<CalendarCommandResult> callback = new CommandResultCallback<CalendarCommandResult>(){
				@Override
				public void onSuccess(CalendarCommandResult result) {
					cacheMap.put(args,result);
					displayCalendarResultsWeek(result);
				}
			};
			RpcServiceAsync service = injector.getService();
			service.execute(cmd, callback);
		}
	}
	
	

	private void showDayCalendar(final HistoryToken args) {
		if(cacheMap.containsKey(args)){
			displayCalendarResultsDay(cacheMap.get(args));
		}
		else{
			CalendarCommand cmd = new CalendarCommand();
			cmd.setScope(CalendarCommand.Scope.DAY);
			cmd.setYear(args.getParameterAsInt(CALENDAR_YEAR));
			cmd.setDayOfMonth(args.getParameterAsInt(CALENDAR_DAY_OF_MONTH));
			cmd.setMonthOfYear(args.getParameterAsInt(CALENDAR_MONTH_OF_YEAR));
			
			CommandResultCallback<CalendarCommandResult> callback = new CommandResultCallback<CalendarCommandResult>(){
				@Override
				public void onSuccess(CalendarCommandResult result) {
					cacheMap.put(args,result);
					displayCalendarResultsDay(result);
				}
			};
			
			RpcServiceAsync service = injector.getService();
			service.execute(cmd, callback);
		}
	}
	
	private void displayCalendarResultsDay(CalendarCommandResult result) {
//		DayPresenter dayPresenter = injector.getDayPresenter();
//		Day day = result.getDay();
//		dayPresenter.setDay(day);
//		calendarBodyElement.clear();
//		calendarBodyElement.add(dayPresenter.getWidget());
		
		DayPanel dayPanel = injector.createDayPanel();
		Day day = result.getDay();
		dayPanel.setDay(day);
		calendarBodyElement.clear();
		calendarBodyElement.add(dayPanel);
		
		calendarTitleElement.setText(DateFormatUtil.formatLongDay(day.getDate()));
				
		showSearchWithResults(result);
		
		calendarBreadCrumbPanel.setYear(day.getYear());
		calendarBreadCrumbPanel.setMonth(day.getYear(), day.getMonth());
		calendarBreadCrumbPanel.setWeek(result.getRelevantWeekOfYear(), day.getYear(), result.getWeekStartDate(), result.getWeekEndDate());
		//calendarBreadCrumbPanel.setDay(day.getDate(), day.getYear(),day.getMonth(),day.getDay());
		
		calendarBreadCrumbPanel.setPrevNext(CalendarHelpers.buildDayHistoryToken(result.getPrevYear(), result.getPrevMonthOfYear(), result.getPrevDayOfMonth()),
				CalendarHelpers.buildDayHistoryToken(result.getNextYear(), result.getNextMonthOfYear(), result.getNextDayOfMonth()));
	}
	
	private void displayCalendarResultsWeek(CalendarCommandResult result) {
		calendarBodyElement.clear();
		WeekPresenter weekPresenter = injector.getWeekPresenter();
		weekPresenter.setWeek(result.getWeek());
		calendarBodyElement.add(weekPresenter.getWidget());
		calendarTitleElement.setText("");
				
		showSearchWithResults(result);
		
		Day firstDayOfWeek = result.getWeek().getDays().get(0);
		Day lastDayOfWeek = result.getWeek().getDays().get(6);

		calendarBreadCrumbPanel.setYear(lastDayOfWeek.getYear());
		calendarBreadCrumbPanel.setMonth(lastDayOfWeek.getYear(), lastDayOfWeek.getMonth());
//		calendarBreadCrumbPanel.setWeek(result.getWeek().getWeekOfYear(), result.getWeek().getYear(), firstDayOfWeek.getDate(), lastDayOfWeek.getDate());
		
		calendarBreadCrumbPanel.setPrevNext(CalendarHelpers.buildWeekHistoryToken(result.getPrevYear(), result.getPrevWeekOfYear()),
				CalendarHelpers.buildWeekHistoryToken(result.getNextYear(), result.getNextWeekOfYear()));
	}
	
	
	
	private void displayCalendarResultsMonth(CalendarCommandResult result) {
		
		MonthDetailPresenter monthDetailPresenter = injector.getMonthDetailPresenter();
		monthDetailPresenter.setMonth(result.getMonth());
		calendarBodyElement.clear();
		calendarBodyElement.add(monthDetailPresenter.getWidget());
		calendarTitleElement.setText( CalendarHelpers.getMonthName(result.getMonth().getMonthNumber()) + " " + result.getMonth().getYearNumber());
				
		showSearchWithResults(result);
		
		calendarBreadCrumbPanel.setYear(result.getMonth().getYearNumber());
		//calendarBreadCrumbPanel.setMonth(result.getMonth().getYearNumber(),result.getMonth().getMonthNumber());
		
		calendarBreadCrumbPanel.setPrevNext(CalendarHelpers.buildMonthHistoryToken(result.getPrevYear(), result.getPrevMonthOfYear()),
				CalendarHelpers.buildMonthHistoryToken(result.getNextYear(), result.getNextMonthOfYear()));
	}
	
	private void displayCalendarResultsYear(CalendarCommandResult result) {
		YearPresenter yearPresenter = injector.getYearPresenter();
		yearPresenter.setYear(result.getYear());
		calendarBodyElement.clear();
		calendarBodyElement.add(yearPresenter.getWidget());
		calendarTitleElement.setText(""+result.getYear().getYearNumber());
				
		showSearchWithResults(result);
		
		//calendarBreadCrumbPanel.setYear(result.getYear().getYearNumber());
		
		calendarBreadCrumbPanel.setPrevNext(CalendarHelpers.buildYearHistoryToken(result.getPrevYear()),
				CalendarHelpers.buildYearHistoryToken(result.getNextYear()));
	}

	
//	private void setupCalendarScale(CalendarCommandResult result) {
//		String scale = "";
//		if(CalendarCommand.Scope.DAY.equals(result.getScope()))
//			scale = HistoryConstants.CALENDAR_SCALE_VALUE_DAY;
//		if(CalendarCommand.Scope.WEEK.equals(result.getScope()))
//			scale = HistoryConstants.CALENDAR_SCALE_VALUE_WEEK;
//		if(CalendarCommand.Scope.MONTH.equals(result.getScope()))
//			scale = HistoryConstants.CALENDAR_SCALE_VALUE_MONTH;
//		if(CalendarCommand.Scope.YEAR.equals(result.getScope()))
//			scale = HistoryConstants.CALENDAR_SCALE_VALUE_YEAR;
//		
//		ScaleSelectorPresenter scaleSelectorPresenter = injector.getScaleSelectorPresenter();
//		scaleSelectorPresenter.setToday(result.getRelevantYear(), result.getRelevantMonthOfYear(), result.getRelevantWeekOfYear(), result.getRelevantDayOfMonth(), scale);
//		scaleElement.clear();
//		scaleElement.add(scaleSelectorPresenter.getWidget());
//	}
//	
//	private void configurePrevLink(HistoryToken token) {
//		HyperlinkPresenter linkPresenter = injector.getHyperlinkPresenter();
//		linkPresenter.setToken(token, "<<Prev");
//		prevElement.clear();
//		prevElement.add(linkPresenter.getWidget());
//	}
//
//	private void configureNextLink(HistoryToken token) {
//		HyperlinkPresenter linkPresenter;
//		linkPresenter = injector.getHyperlinkPresenter();
//		linkPresenter.setToken(token, "Next>>");
//		nextElement.clear();
//		nextElement.add(linkPresenter.getWidget());
//	}
	
	
//	private String generateCalendarTitle(CalendarCommandResult result){
//		
//	}
	
	private void showSearchWithResults(CalendarCommandResult result) {
		pageHeaderPanel.getSearchBoxPresenter().init(result.getStartDate(), result.getEndDate());
	}

}
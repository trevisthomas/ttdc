package org.ttdc.gwt.client.presenters.calendar;

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
import org.ttdc.gwt.client.presenters.search.SearchBoxPresenter;
import org.ttdc.gwt.client.presenters.shared.BasePagePresenter;
import org.ttdc.gwt.client.presenters.shared.BasePageView;
import org.ttdc.gwt.client.presenters.shared.HyperlinkPresenter;
import org.ttdc.gwt.client.services.RpcServiceAsync;
import org.ttdc.gwt.client.uibinder.calendar.CalendarPanel;
import org.ttdc.gwt.client.uibinder.shared.StandardPageHeaderPanel;
import org.ttdc.gwt.shared.calender.Day;
import org.ttdc.gwt.shared.commands.CalendarCommand;
import org.ttdc.gwt.shared.commands.CommandResultCallback;
import org.ttdc.gwt.shared.commands.results.CalendarCommandResult;

import com.google.gwt.user.client.ui.HasText;
import com.google.gwt.user.client.ui.HasWidgets;
import com.google.gwt.user.client.ui.Widget;
import com.google.inject.Inject;

/**
 * See {@link CalendarPanel}
 * 
 * @deprecated
 */
public class CalendarPresenter extends BasePagePresenter<CalendarPresenter.View> {
	//private String 
	private HistoryToken lastToken;
	private final Map<HistoryToken,CalendarCommandResult> cacheMap = new HashMap<HistoryToken,CalendarCommandResult>(); 
	private final StandardPageHeaderPanel pageHeaderPanel;
	
	@Inject
	public CalendarPresenter(Injector injector) {
		super(injector,injector.getCalendarView());
		pageHeaderPanel = injector.createStandardPageHeaderPanel();
		view.title().setText("loading...");
		view.headerPanel().add(injector.getUserIdentityPresenter().getWidget());
	}
	
	/**
	 * 
	 * The View interface
	 *
	 */
	public static interface View extends BasePageView{
		HasWidgets previousLink();
		HasWidgets nextLink();
		HasText title();
		HasWidgets scaleTarget();
		HasWidgets calendar();
		HasWidgets searchTarget();
		HasWidgets headerPanel();
		void insertPageHeader(Widget w);
		void clear();
	}
	
	
	
	@Override
	public void show(HistoryToken args) {
		
		view.insertPageHeader(pageHeaderPanel);
		
		if(lastToken != null && args.equals(lastToken)){
			view.show();
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
	
	
	private void displayCalendarResultsWeek(CalendarCommandResult result) {
		view.clear();
		WeekPresenter weekPresenter = injector.getWeekPresenter();
		weekPresenter.setWeek(result.getWeek());
		getView().calendar().add(weekPresenter.getWidget());
		getView().title().setText("Todo load do better for: "+result.getWeek().getWeekOfYear());
		getView().show();
		
		HistoryToken token = CalendarHelpers.buildWeekHistoryToken(result.getPrevYear(), result.getPrevWeekOfYear());
		configurePrevLink(token);
		
		token = CalendarHelpers.buildWeekHistoryToken(result.getNextYear(), result.getNextWeekOfYear());
		configureNextLink(token);
		
		setupCalendarScale(result);
		
		showSearchWithResults(result);
	}
	
	private void displayCalendarResultsDay(CalendarCommandResult result) {
		view.clear();
		DayPresenter dayPresenter = injector.getDayPresenter();
		Day day = result.getDay();
		dayPresenter.setDay(day);
		getView().calendar().add(dayPresenter.getWidget());
		getView().title().setText(day.getMonth()+" "+day.getDay()+" "+day.getYear());
		getView().show();
		
		HistoryToken token = CalendarHelpers.buildDayHistoryToken(result.getPrevYear(), result.getPrevMonthOfYear(), result.getPrevDayOfMonth());
		configurePrevLink(token);
		
		token = CalendarHelpers.buildDayHistoryToken(result.getNextYear(), result.getNextMonthOfYear(), result.getNextDayOfMonth());
		configureNextLink(token);
		
		setupCalendarScale(result);
		
		showSearchWithResults(result);
	}
	
	private void displayCalendarResultsMonth(CalendarCommandResult result) {
		view.clear();
		MonthDetailPresenter monthDetailPresenter = injector.getMonthDetailPresenter();
		monthDetailPresenter.setMonth(result.getMonth());
		getView().calendar().add(monthDetailPresenter.getWidget());
		getView().title().setText("TODO: Month "+result.getMonth().getMonthNumber()+" needs a name");
		getView().show();
		
		HistoryToken token = CalendarHelpers.buildMonthHistoryToken(result.getPrevYear(), result.getPrevMonthOfYear());
		configurePrevLink(token);
		
		token = CalendarHelpers.buildMonthHistoryToken(result.getNextYear(), result.getNextMonthOfYear());
		configureNextLink(token);
		
		setupCalendarScale(result);
		
		showSearchWithResults(result);
	}
	
	private void displayCalendarResultsYear(CalendarCommandResult result) {
		view.clear();
		YearPresenter yearPresenter = injector.getYearPresenter();
		yearPresenter.setYear(result.getYear());
		getView().calendar().add(yearPresenter.getWidget());
		getView().title().setText(""+result.getYear().getYearNumber());
		getView().show();
		
		HistoryToken token = CalendarHelpers.buildYearHistoryToken(result.getPrevYear());
		configurePrevLink(token);
		
		token = CalendarHelpers.buildYearHistoryToken(result.getNextYear());
		configureNextLink(token);
		
		setupCalendarScale(result);
		
		showSearchWithResults(result);
	}
	

	private void setupCalendarScale(CalendarCommandResult result) {
		String scale = "";
		if(CalendarCommand.Scope.DAY.equals(result.getScope()))
			scale = HistoryConstants.CALENDAR_SCALE_VALUE_DAY;
		if(CalendarCommand.Scope.WEEK.equals(result.getScope()))
			scale = HistoryConstants.CALENDAR_SCALE_VALUE_WEEK;
		if(CalendarCommand.Scope.MONTH.equals(result.getScope()))
			scale = HistoryConstants.CALENDAR_SCALE_VALUE_MONTH;
		if(CalendarCommand.Scope.YEAR.equals(result.getScope()))
			scale = HistoryConstants.CALENDAR_SCALE_VALUE_YEAR;
		
		ScaleSelectorPresenter scaleSelectorPresenter = injector.getScaleSelectorPresenter();
		scaleSelectorPresenter.setToday(result.getRelevantYear(), result.getRelevantMonthOfYear(), result.getRelevantWeekOfYear(), result.getRelevantDayOfMonth(), scale);
		view.scaleTarget().add(scaleSelectorPresenter.getWidget());
	}
	
	private void configurePrevLink(HistoryToken token) {
		HyperlinkPresenter linkPresenter = injector.getHyperlinkPresenter();
		linkPresenter.setToken(token, "<<Prev");
		view.previousLink().add(linkPresenter.getWidget());
	}

	private void configureNextLink(HistoryToken token) {
		HyperlinkPresenter linkPresenter;
		linkPresenter = injector.getHyperlinkPresenter();
		linkPresenter.setToken(token, "Next>>");
		view.nextLink().add(linkPresenter.getWidget());
	}
	
	private void showSearchWithResults(CalendarCommandResult result) {
		pageHeaderPanel.getSearchBoxPresenter().init(result.getStartDate(), result.getEndDate());
	}
}

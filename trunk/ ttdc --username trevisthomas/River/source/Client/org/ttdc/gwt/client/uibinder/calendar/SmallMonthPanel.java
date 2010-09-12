package org.ttdc.gwt.client.uibinder.calendar;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.ttdc.gwt.client.Injector;
import org.ttdc.gwt.client.messaging.EventBus;
import org.ttdc.gwt.client.messaging.history.HistoryToken;
import org.ttdc.gwt.client.presenters.calendar.CalendarHelpers;
import org.ttdc.gwt.client.presenters.calendar.WeekClickHandler;
import org.ttdc.gwt.client.presenters.shared.HyperlinkPresenter;
import org.ttdc.gwt.client.presenters.util.ClickableHoverSyncPanel;
import org.ttdc.gwt.client.presenters.util.ClickableIconPanel;
import org.ttdc.gwt.shared.calender.Day;
import org.ttdc.gwt.shared.calender.Month;
import org.ttdc.gwt.shared.calender.Week;
import org.ttdc.gwt.shared.commands.CalendarCommand;
import org.ttdc.gwt.shared.commands.CommandResultCallback;
import org.ttdc.gwt.shared.commands.results.CalendarCommandResult;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FocusPanel;
import com.google.gwt.user.client.ui.Grid;
import com.google.gwt.user.client.ui.Hyperlink;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.Widget;
import com.google.inject.Inject;

public class SmallMonthPanel extends Composite implements DayClickHandler{
	interface MyUiBinder extends UiBinder<Widget, SmallMonthPanel> {}
    private static final MyUiBinder binder = GWT.create(MyUiBinder.class);
    
    private Injector injector;
    
    @UiField(provided = true) FocusPanel prevMonthElement = new ClickableIconPanel("ui-icon ui-icon-circle-arrow-w");
    @UiField(provided = true) Hyperlink monthTitleElement;
    @UiField(provided = true) Hyperlink yearTitleElement;
    @UiField(provided = true) FocusPanel nextMonthElement = new ClickableIconPanel("ui-icon ui-icon-circle-arrow-e");
    @UiField(provided = true) Grid daysGridElement = new Grid(7,8);
    @UiField Button clearButtonElement;
    @UiField Button nowButtonElement;
    private int prevYear;
	private int prevMonth;
	private int nextYear;
	private int nextMonth;
	private int year;
	private int month;
	private Day now;
	private Day selectedDay = null;
	private boolean selectableDayMode = false; 
	private DayClickHandler dayClickHandler = null;
	private Map<Day, ClickableDay> dayMap = new HashMap<Day, ClickableDay>();

	private HyperlinkPresenter monthLink;
	private HyperlinkPresenter yearLink;
	
    @Override
    public Widget getWidget() {
    	return this;
    }
    
	@Inject
	public SmallMonthPanel(Injector injector) {
		this.injector = injector;
	
		monthLink = injector.getHyperlinkPresenter();
		monthTitleElement = monthLink.getHyperlink();
	
		yearLink = injector.getHyperlinkPresenter();
		yearTitleElement = yearLink.getHyperlink();
		
		now = new Day(new Date());
		initWidget(binder.createAndBindUi(this));
		
		prevMonthElement.addClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				init(prevYear,prevMonth,SmallMonthPanel.this.dayClickHandler);
			}
		});
		
		nextMonthElement.addClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				init(nextYear,nextMonth,SmallMonthPanel.this.dayClickHandler);
			}
		});
		
		clearButtonElement.addClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				deselectDay();
			}
		});
		
		nowButtonElement.addClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				setSelectedDay(now);				
			}
		});
		
		clearButtonElement.setText("Clear");
		nowButtonElement.setText("Now");
		
		enableSelectableDayMode(selectableDayMode);
		setupWeekNameHeader();
	}
	
	public void enableSelectableDayMode(boolean enable) {
		if(enable){
			clearButtonElement.setVisible(true);
			nowButtonElement.setVisible(true);
		}
		else{	
			clearButtonElement.setVisible(false);
			nowButtonElement.setVisible(false);
		}
	}
	
	public void init(Month month){
		renderMonth(month,false);
		dayClickHandler = this;
		//view.enableSelectableDayMode(false);
	}
	
	public void initInteractive(int year,int monthOfYear){
		if(dayClickHandler == null)
			init(year, monthOfYear, this);
		else
			init(year, monthOfYear, dayClickHandler);
	}
	
	/*
	 * This version of the constructor is because of an idea i had to make this thing more self 
	 * sufficient when operating as a stand alone calendar.  Basically It gets it's own data since it 
	 * needs to have the know how to have a prev next... 
	 */
	private void init(int year,int monthOfYear, DayClickHandler handler){
		if(handler != null){
			dayClickHandler = handler;
		}
		else
			dayClickHandler = this;
		
		this.year = year;
		this.month = monthOfYear;
		
		CalendarCommand cmd = new CalendarCommand();
		cmd.setScope(CalendarCommand.Scope.SIMPLE_MONTH);
		cmd.setYear(year);
		cmd.setMonthOfYear(monthOfYear);
		injector.getService().execute(cmd, buildCalendarMonthCallback());
	}
	
	private void setupWeekNameHeader(){
		for(int dayOfWeek = 1 ; dayOfWeek < 8 ; dayOfWeek++){
			daysGridElement.setWidget(0, dayOfWeek, createFilledCenteredLabel(CalendarHelpers.getDayAbbreviation(dayOfWeek)));
		}
	}
	
	private CommandResultCallback<CalendarCommandResult> buildCalendarMonthCallback() {
		CommandResultCallback<CalendarCommandResult> callback = new CommandResultCallback<CalendarCommandResult>(){
			@Override
			public void onSuccess(CalendarCommandResult result) {
				renderMonth(result.getMonth(),true);
				
				setSelectedDay(selectedDay);
				
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
		monthLink.setToken(token, month.getName());
		
		if(standalone){
			prevMonthElement.setVisible(true);
			nextMonthElement.setVisible(true);
			HistoryToken yrToken = CalendarHelpers.buildYearHistoryToken(month.getYearNumber());
			yearLink.setToken(yrToken, month.getYearNumber() + "");
		}
		else{
			prevMonthElement.setVisible(false);
			nextMonthElement.setVisible(false);
		}
		
		List<Week> weeks = month.getWeeks();
		int weekOfMonth = 1;
		
		for(Week week : weeks){
			token = CalendarHelpers.buildWeekHistoryToken(month.getYearNumber(), week.getWeekOfYear());
			getWeekElementFromGrid(weekOfMonth).addClickHandler(new WeekClickHandler(token));
			int dayOfWeek = 1;
			for(Day day : week.getDays()){
				if(day.isVisable()){
					insertDay(weekOfMonth, dayOfWeek, day.getDay(), day);
				}
				else{
					clearDay(weekOfMonth, dayOfWeek, day.getDay());
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
	//@Override
	public void onDayClick(ClickableDay day) {
		if(isSelectableDayMode()){
			//processDayClickAsSelection(day);
			setSelectedDay(day.getDay());
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
		enableSelectableDayMode(selectableDayMode);
	}
	
	public Day getSelectedDay(){
		return this.selectedDay;
	}

	public void processDayClickAsLink(Day day) {
		if(day.isContent()){
			HistoryToken token = CalendarHelpers.buildDayHistoryToken(day.getYear(),day.getMonth(),day.getDay());
			EventBus.fireHistoryToken(token);
		}
	}

	public void setSelectedDay(Day day){
		if (day == null) 
			return;
		deselectDay();
		
		this.selectedDay = day;
		ClickableDay clickableDay = dayMap.get(day);
		if(clickableDay != null){
			this.selectedDay = day;
			clickableDay.highlight();
		}
	}

	public void deselectDay(){
		if(selectedDay != null){
			ClickableDay clickableDay = dayMap.get(selectedDay);
			selectedDay = null;
			clickableDay.removeHighlight();
		}
	}
	
	public void insertDay(int weekOfMonth, int dayOfWeek, int dayOfMonth, Day day) {
		Label dayLabel = createFilledCenteredLabel("");

		dayLabel.setText(""+day.getDay());
		if(day.isToday())
			dayLabel.addStyleName("tt-calendar-is-today");
		else if(day.isFuture())
			dayLabel.addStyleName("tt-calendar-is-future");

		ClickableDay clickableDay = new ClickableDay(day);
		
		clickableDay.add(dayLabel);
		daysGridElement.setWidget(weekOfMonth, dayOfWeek, clickableDay);
		ClickableHoverSyncPanel weekClicker = getWeekElementFromGrid(weekOfMonth);
		weekClicker.addSynchedHoverTarget(clickableDay);
	}
	
	public void clearDay(int weekOfMonth, int dayOfWeek, int dayOfMonth) {
		Label dayLabel = createFilledCenteredLabel("");

//		dayLabel.setText(""+day.getDay());
//		if(day.isToday())
//			dayLabel.addStyleName("tt-calendar-is-today");
//		else if(day.isFuture())
//			dayLabel.addStyleName("tt-calendar-is-future");
//
//		ClickableDay clickableDay = new ClickableDay(day);
		
//		clickableDay.add(dayLabel);
		daysGridElement.setWidget(weekOfMonth, dayOfWeek, new Label(""));
//		ClickableHoverSyncPanel weekClicker = getWeekElementFromGrid(weekOfMonth);
//		weekClicker.addSynchedHoverTarget(clickableDay);
	}
	
	private Label createFilledCenteredLabel(String labelText){
		Label label = new Label(labelText);
		label.setStyleName("tt-fill-both tt-text-center");
		return label;
	}
	
	private ClickableHoverSyncPanel getWeekElementFromGrid(int weekOfMonth){
		Widget w = daysGridElement.getWidget(weekOfMonth, 0);
		if(w != null){
			return (ClickableHoverSyncPanel)w;
		}
		else{
			ClickableHoverSyncPanel weekClicker = new ClickableHoverSyncPanel("tt-color-calendar-small-month-week","tt-color-calendar-small-month-week-hover");
			weekClicker.setStyleName("tt-calendar-year-week-target tt-float-right");
			Label l = new Label("");
			l.getElement().setInnerHTML("&raquo;");
			weekClicker.add(l);
			daysGridElement.setWidget(weekOfMonth, 0, weekClicker);
			return weekClicker;
		}
	}
	
	class ClickableDay extends ClickableHoverSyncPanel{
		private final Day day;
		
		public Day getDay() {
			return day;
		}
		public void highlight(){
			updatePrimary("tt-color-calendar-small-month-day-selected","tt-color-calendar-small-month-day-selected-hover");
		}
		public void removeHighlight(){
			if(day.isContent()){
				updatePrimary("tt-color-calendar-small-month-day-content","tt-color-calendar-small-month-day-content-hover");
			}
			else{
				updatePrimary("tt-color-calendar-small-month-day","tt-color-calendar-small-month-day-hover");
			}
		}
		
		public ClickableDay(Day day) {
			super("tt-color-calendar-small-month-day","tt-color-calendar-small-month-day-hover","tt-no-clue-what-this-does","tt-no-clue-what-this-does-hover");
			dayMap.put(day, this);
			this.day = day;
			
			setStyleName("tt-calendar-small-month-day");
			if(day.isContent()){
				updatePrimary("tt-color-calendar-small-month-day-content","tt-color-calendar-small-month-day-content-hover");
			}
			
			addClickHandler(new ClickHandler() {
				@Override
				public void onClick(ClickEvent event) {
					if(dayClickHandler != null)
						dayClickHandler.onDayClick(ClickableDay.this);
					
				}
			});
		}
	}

}

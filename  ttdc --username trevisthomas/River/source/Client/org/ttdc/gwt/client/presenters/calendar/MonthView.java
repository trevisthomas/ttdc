package org.ttdc.gwt.client.presenters.calendar;

import java.util.HashMap;
import java.util.Map;

import org.ttdc.gwt.client.presenters.util.ClickableHoverSyncPanel;
import org.ttdc.gwt.client.presenters.util.ClickableIconPanel;
import org.ttdc.gwt.client.uibinder.calendar.SmallMonthPanel;
import org.ttdc.gwt.shared.calender.Day;

import com.google.gwt.dom.client.NativeEvent;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.HasClickHandlers;
import com.google.gwt.event.dom.client.MouseOutEvent;
import com.google.gwt.event.dom.client.MouseOutHandler;
import com.google.gwt.event.dom.client.MouseOverEvent;
import com.google.gwt.event.dom.client.MouseOverHandler;
import com.google.gwt.event.dom.client.MouseUpEvent;
import com.google.gwt.event.dom.client.MouseUpHandler;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.FocusPanel;
import com.google.gwt.user.client.ui.Grid;
import com.google.gwt.user.client.ui.HasWidgets;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.SimplePanel;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;

/**
 * 
 * see {@link SmallMonthPanel}
 *
 */
@Deprecated
public class MonthView implements MonthPresenter.View{
	private final VerticalPanel mainPanel = new VerticalPanel();
	private final SimplePanel monthNameTarget = new SimplePanel();
	private final SimplePanel yearNameTarget = new SimplePanel(); 
	private final FlowPanel headerPanel = new FlowPanel();
	
	private final Grid headerPanelGrid = new Grid(1,3); 
	private final Grid grid = new Grid(7,8);
	private final ClickableIconPanel prevClickTarget = new ClickableIconPanel("tt-clickable-icon-prev");
	private final ClickableIconPanel nextClickTarget = new ClickableIconPanel("tt-clickable-icon-next");
	private DayClickHandler dayClickHandler = null;
	private Map<Day, ClickableDay> dayMap = new HashMap<Day, ClickableDay>();
	private Day selectedDay = null;
	private final FlowPanel controls = new FlowPanel(); 
	
	private final Button clearButton = new Button("Clear"); 
	private final Button nowButton = new Button("Now");
	
	@Override
	public Widget getWidget() {
		return mainPanel;
	}
	
	public MonthView() {
		setup();
		//Init that only happens once
		headerPanelGrid.setWidget(0, 1, headerPanel);
		headerPanelGrid.setStyleName("tt-fill");
		
		headerPanel.setStyleName("tt-center");
		
		grid.addStyleName("tt-calendar-small-month");
		
	}
	
	@Override
	public void clear() {
		// TODO Auto-generated method stub
		grid.clear();
		headerPanel.clear();
		monthNameTarget.clear();
		yearNameTarget.clear();
		mainPanel.clear();
		
		
		setup();
	}
	
	@Override
	public void enableSelectableDayMode(boolean enable) {
		if(enable){
			controls.setVisible(true);
		}
		else{	
			controls.setVisible(false);
		}
	}

	private void setup() {
		mainPanel.add(headerPanelGrid);
		
		headerPanel.add(monthNameTarget);
		headerPanel.add(yearNameTarget);
		
		mainPanel.add(grid);
		mainPanel.add(controls);
		controls.add(clearButton);
		controls.add(nowButton);
		setupHeader();
	}
	
		
	
	private final void setupHeader(){
		for(int dayOfWeek = 1 ; dayOfWeek < 8 ; dayOfWeek++){
			grid.setWidget(0, dayOfWeek, createFilledCenteredLabel(CalendarHelpers.getDayAbbreviation(dayOfWeek)));
		}
	}
	
	@Override
	public void setDayClickHandler(DayClickHandler handler){
		dayClickHandler = handler;
	}

//	@Override
//	public void insertDay(int weekOfMonth, int dayOfWeek, int dayOfMonth, Day day, Widget widget) {
////		widget.setStyleName("tt-calendar-year-day");
//		ClickableDay clickableDay = new ClickableDay(day);
//		clickableDay.add(widget);
//		grid.setWidget(weekOfMonth, dayOfWeek, widget);
//		//grid.getColumnFormatter().setStyleName("", arg1);
//	}
	
	
	private Label createFilledCenteredLabel(String labelText){
		Label label = new Label(labelText);
		label.setStyleName("tt-fill-both tt-text-center");
		return label;
	}
	@Override
	public void insertDay(int weekOfMonth, int dayOfWeek, int dayOfMonth, Day day) {
		//SimplePanel simpleDay = new SimplePanel();
		Label dayLabel = createFilledCenteredLabel("");
//		dayLabel.setStyleName("tt-calendar-year-day");
		
		dayLabel.setText(""+day.getDay());
		if(day.isToday())
			dayLabel.addStyleName("tt-calendar-is-today");
		else if(day.isFuture())
			dayLabel.addStyleName("tt-calendar-is-future");

		ClickableDay clickableDay = new ClickableDay(day);
		
		clickableDay.add(dayLabel);
		grid.setWidget(weekOfMonth, dayOfWeek, clickableDay);
		
		ClickableHoverSyncPanel weekClicker = getWeekLabelFromGrid(weekOfMonth);
		
		weekClicker.addSynchedHoverTarget(clickableDay);
	}
	
	

	@Override
	public HasClickHandlers weekTargetClickHandlers(int weekOfMonth) {
		return getWeekLabelFromGrid(weekOfMonth);
	}

	private ClickableHoverSyncPanel getWeekLabelFromGrid(int weekOfMonth){
		Widget w = grid.getWidget(weekOfMonth, 0);
		if(w != null){
			return (ClickableHoverSyncPanel)w;
		}
		else{
			ClickableHoverSyncPanel weekClicker = new ClickableHoverSyncPanel("tt-color-contrast1","tt-color-contrast1-hover");
			weekClicker.setStyleName("tt-calendar-year-week-target tt-float-right");
			weekClicker.add(new Label("+"));
			grid.setWidget(weekOfMonth, 0, weekClicker);
//			FocusPanel focusPanel = new FocusPanel(new Label("*"));
//			focusPanel.addStyleName("tt-calendar-year-week-target tt-float-right");
//			grid.setWidget(weekOfMonth, 0, focusPanel);
//			return focusPanel;
			return weekClicker;
		}
	}
	
	@Override
	public void enablePrevNext(boolean enable) {
		if(enable){
			headerPanelGrid.setWidget(0, 0, prevClickTarget);
			headerPanelGrid.setWidget(0, 2, nextClickTarget);
		}
		else{
			headerPanelGrid.remove(prevClickTarget);
			headerPanelGrid.remove(nextClickTarget);
		}
	}

	@Override
	public HasWidgets monthHeaderTarget() {
		return monthNameTarget;
	}

	@Override
	public HasWidgets yearHeaderTarget() {
		return yearNameTarget;
	}

	@Override
	public HasClickHandlers nextMonthClickHandlers() {
		return nextClickTarget;
	}

	@Override
	public HasClickHandlers prevMonthClickHandlers() {
		return prevClickTarget;
	}

	@Override
	public Day getSelectedDay() {
		return selectedDay;
	}
	
	@Override
	public HasClickHandlers clearClickHandlers() {
		return clearButton;
	}

	@Override
	public HasClickHandlers nowClickHandlers() {
		return nowButton;
	} 
	
	/**
	 * 
	 * This interface is for listers to respond to days being clicked in a calendar
	 *
	 */
	public interface DayClickHandler{
		void onDayClick(ClickableDay day);
	} 
	
	
	
	@Override
	public void setSelectedDay(Day day){
		if (day == null) return;
		deselectDay();
		
		ClickableDay clickableDay = dayMap.get(day);
		if(clickableDay != null){
			selectedDay = day;
			clickableDay.highlight();
		}
	}
	@Override
	public void deselectDay(){
		if(selectedDay != null){
			ClickableDay clickableDay = dayMap.get(selectedDay);
			selectedDay = null;
			clickableDay.removeHighlight();
		}
	}
	
	
//	class ClickableDay extends FocusPanel{
//		private String hoverStyle = "tt-color-contrast2-hover";
//		private final Day day;
//		
//		public Day getDay() {
//			return day;
//		}
//		public void highlight(){
//			addStyleName("tt-color-selected");
//		}
//		public void removeHighlight(){
//			removeStyleName("tt-color-selected");
//		}
//		
//		public ClickableDay(Day day) {
//			dayMap.put(day, this);
//			//this.handler = handler;
//			this.day = day;
//			
//			setStyleName("tt-cursor-pointer tt-border tt-fill-both");
//			//addStyleName("tt-calendar-small-month-day");
//			if(day.isContent()){
//				addStyleName("tt-calendar-small-month-dayHasContent");	
//			}
//			
//			addMouseOverHandler(new MouseOverHandler() {
//				@Override
//				public void onMouseOver(MouseOverEvent event) {
//					addStyleName(hoverStyle);
//				}
//			});
//			
//			addMouseOutHandler(new MouseOutHandler() {
//				@Override
//				public void onMouseOut(MouseOutEvent event) {
//					removeStyleName(hoverStyle);
//				}
//			});
//			
//			addMouseUpHandler(new MouseUpHandler() {
//				@Override
//				public void onMouseUp(MouseUpEvent event) {
//					if(event.getNativeButton() == NativeEvent.BUTTON_LEFT){
//						if(dayClickHandler != null)
//							dayClickHandler.onDayClick(ClickableDay.this);
//					
//					}
//				}
//			});
//			
//			
//		}
//	}
	
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
			super("tt-color-calendar-small-month-day","tt-color-calendar-small-month-day-hover","tt-color-contrast2","tt-color-contrast2-hover");
			dayMap.put(day, this);
			//this.handler = handler;
			this.day = day;
			
			setStyleName("tt-border tt-calendar-small-month-day");
			//addStyleName("tt-calendar-small-month-day");
			if(day.isContent()){
				updatePrimary("tt-color-calendar-small-month-day-content","tt-color-calendar-small-month-day-selected-hover");
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

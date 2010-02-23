package org.ttdc.gwt.client.presenters.calendar;

import java.util.HashMap;
import java.util.Map;

import org.ttdc.gwt.client.presenters.util.ClickableIconPanel;
import org.ttdc.gwt.shared.calender.Day;

import com.google.gwt.event.dom.client.HasClickHandlers;
import com.google.gwt.event.dom.client.MouseOutEvent;
import com.google.gwt.event.dom.client.MouseOutHandler;
import com.google.gwt.event.dom.client.MouseOverEvent;
import com.google.gwt.event.dom.client.MouseOverHandler;
import com.google.gwt.event.dom.client.MouseUpEvent;
import com.google.gwt.event.dom.client.MouseUpHandler;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.FocusPanel;
import com.google.gwt.user.client.ui.Grid;
import com.google.gwt.user.client.ui.HasWidgets;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.SimplePanel;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;

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
		for(int dayOfWeek = 1 ; dayOfWeek < 8 ; dayOfWeek++)
			grid.setWidget(0, dayOfWeek, new Label(CalendarHelpers.getDayAbbreviation(dayOfWeek)));
	}
	
	@Override
	public void setDayClickHandler(DayClickHandler handler){
		dayClickHandler = handler;
	}

	@Override
	public void insertDay(int weekOfMonth, int dayOfWeek, int dayOfMonth, Day day, Widget widget) {
		widget.setStyleName("tt-calendar-year-day");
		ClickableDay clickableDay = new ClickableDay(day);
		clickableDay.add(widget);
		grid.setWidget(weekOfMonth, dayOfWeek, widget);
	}
	
	
	
	@Override
	public void insertDay(int weekOfMonth, int dayOfWeek, int dayOfMonth, Day day) {
		//SimplePanel simpleDay = new SimplePanel();
		Label dayLabel = new Label();
		dayLabel.setStyleName("tt-calendar-year-day");
		
		dayLabel.setText(""+day.getDay());
		if(day.isToday())
			dayLabel.addStyleName("tt-calendar-is-today");
		else if(day.isFuture())
			dayLabel.addStyleName("tt-calendar-is-future");

		ClickableDay clickableDay = new ClickableDay(day);
		
		clickableDay.add(dayLabel);
		grid.setWidget(weekOfMonth, dayOfWeek, clickableDay);
	}
	
	

	@Override
	public HasClickHandlers weekTargetClickHandlers(int weekOfMonth) {
		return getWeekLabelFromGrid(weekOfMonth);
	}

	private HasClickHandlers getWeekLabelFromGrid(int weekOfMonth){
		Widget w = grid.getWidget(weekOfMonth, 0);
		if(w != null){
			return (Label)w;
		}
		else{
			FocusPanel focusPanel = new FocusPanel(new Label("*"));
			focusPanel.addStyleName("tt-calendar-year-week-target");
			grid.setWidget(weekOfMonth, 0, focusPanel);
			return focusPanel;
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
		void onDayClick(Day day);
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
	
	
	class ClickableDay extends FocusPanel{
		private String hoverStyle = "tt-highlight";
		private final Day day;
		
		public void highlight(){
			addStyleName("tt-calendar-month-daySelected");
		}
		public void removeHighlight(){
			removeStyleName("tt-calendar-month-daySelected");
		}
		
		public ClickableDay(Day day) {
			dayMap.put(day, this);
			//this.handler = handler;
			this.day = day;
			setStyleName("tt-cursor-pointer");
			addStyleName("tt-calendar-small-month-day");
			if(day.isContent()){
				addStyleName("tt-calendar-small-month-dayHasContent");	
			}
			
			addMouseOverHandler(new MouseOverHandler() {
				@Override
				public void onMouseOver(MouseOverEvent event) {
					addStyleName(hoverStyle);
				}
			});
			
			addMouseOutHandler(new MouseOutHandler() {
				@Override
				public void onMouseOut(MouseOutEvent event) {
					removeStyleName(hoverStyle);
				}
			});
			
			addMouseUpHandler(new MouseUpHandler() {
				@Override
				public void onMouseUp(MouseUpEvent event) {
					if(dayClickHandler != null)
						dayClickHandler.onDayClick(ClickableDay.this.day);
					
					highlight();
				}
			});
		}
	}

}

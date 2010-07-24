package org.ttdc.gwt.client.presenters.home;

import java.util.HashMap;
import java.util.Map;

import org.ttdc.gwt.client.presenters.calendar.CalendarHelpers;
import org.ttdc.gwt.client.presenters.util.ClickableIconPanel;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.HasClickHandlers;
import com.google.gwt.user.client.ui.FocusPanel;
import com.google.gwt.user.client.ui.Grid;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.Widget;

public class CalendarSelectorModuleView implements CalendarSelectorModulePresenter.View{
	private final HorizontalPanel main = new HorizontalPanel();
	private final Grid monthGrid = new Grid(6,2);
	private final Grid yearGrid = new Grid(6,2);
	private final ClickableIconPanel prevPage = new ClickableIconPanel("tt-clickable-icon-prev");
	private final ClickableIconPanel nextPage = new ClickableIconPanel("tt-clickable-icon-prev");
	
	private int selectedYear;
	private int selectedMonth;
	private int startYear;
	
	private Map<Integer, Month> monthMap = new HashMap<Integer, Month>();
	private Map<Integer, Year> yearMap = new HashMap<Integer, Year>();
	
	public CalendarSelectorModuleView() {
		main.setStyleName("tt-calendar-month-year-selector-module");
		main.add(monthGrid);
		main.add(yearGrid);
		yearGrid.setWidget(0, 0, prevPage);
		yearGrid.setWidget(0, 1, nextPage);
		
		
		prevPage.addClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				startYear = startYear - 10;
				buildYearGrid(startYear);
			}
		});
		
		nextPage.addClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				startYear = startYear + 10;
				buildYearGrid(startYear);
			}
		});
	}
	
	@Override
	public Widget getWidget() {
		return main;
	}
	
	@Override
	public HasClickHandlers nextYearPageClickHandler() {
		return nextPage;
	}

	@Override
	public HasClickHandlers prevYearPageClickHandler() {
		return prevPage;
	}

	@Override
	public int getSelectedMonth() {
		return selectedMonth;
	}

	@Override
	public int getSelectedYear() {
		return selectedYear;
	}
	
	@Override
	public void setSelectedMonth(int month) {
		selectedMonth = month;
		if(monthMap.containsKey(selectedMonth)){
			monthMap.get(selectedMonth).setSelected();
		}
	}

	@Override
	public void setSelectedYear(int year) {
		selectedYear = year;
		if(yearMap.containsKey(selectedYear)){
			yearMap.get(selectedYear).setSelected();
		}
	}

	@Override
	public void startFromYear(int year) {
		startYear = year;
		buildMonthGrid();
		buildYearGrid(year);
	}
	
	private void buildYearGrid(int year) {
		yearMap.clear();
		for(int c = 0; c<2 ;c++){
			for(int r = 1;r < 6;r++){
				yearGrid.setWidget(r, c, new Year(year));
				year++;
			}
		}
		setSelectedYear(getSelectedYear());
	}

	private void buildMonthGrid() {
		monthMap.clear();
		int mo = 0;
		for(int c = 0; c<2 ;c++){
			for(int r = 0;r < 6;r++){
				monthGrid.setWidget(r, c, new Month(mo));
				mo++;
			}
		}
	}

	class Month extends FocusPanel{
		private int month;
		
		void setSelected(){
			addStyleName("tt-selected");
		}
		
		public Month(int month) {
			this.month = month + 1;
			addStyleName("tt-cursor-pointer");
			monthMap.put(this.month,this);
			add(new Label(CalendarHelpers.MONTH_ABBREVIATIONS[month]));
			addClickHandler(new ClickHandler() {
				@Override
				public void onClick(ClickEvent event) {
					for(Month m : monthMap.values()){
						m.removeStyleName("tt-selected");
					}
					Month.this.setSelected();
					selectedMonth = Month.this.month; //WOAH!!
				}
			});
		}
	}
	
	class Year extends FocusPanel{
		private int year;
		
		void setSelected(){
			addStyleName("tt-selected");
		}
		
		public Year(int year) {
			this.year = year;
			add(new Label(year+""));
			yearMap.put(year,this);
			addStyleName("tt-cursor-pointer");
			addClickHandler(new ClickHandler() {
				@Override
				public void onClick(ClickEvent event) {
					for(Year y : yearMap.values()){
						y.removeStyleName("tt-selected");
					}
					Year.this.setSelected();
					selectedYear = Year.this.year; //WOAH!!
				}
			});
		}
	}

	
}

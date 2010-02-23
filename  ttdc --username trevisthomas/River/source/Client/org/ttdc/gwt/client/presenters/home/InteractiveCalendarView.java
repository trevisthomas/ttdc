package org.ttdc.gwt.client.presenters.home;

import org.ttdc.gwt.client.presenters.util.ClickableIconPanel;

import com.google.gwt.event.dom.client.HasClickHandlers;
import com.google.gwt.user.client.ui.AbsolutePanel;
import com.google.gwt.user.client.ui.HasWidgets;
import com.google.gwt.user.client.ui.SimplePanel;
import com.google.gwt.user.client.ui.Widget;

public class InteractiveCalendarView implements InteractiveCalendarPresenter.View{
	private final AbsolutePanel main = new AbsolutePanel();
	private final SimplePanel selectorPanel = new SimplePanel();
	private final SimplePanel calendarPanel = new SimplePanel();
	private final SimplePanel headerPanel = new SimplePanel();
	private final ClickableIconPanel showSelectorButton = new ClickableIconPanel("tt-clickable-icon-down");
	private final ClickableIconPanel showCalenderButton = new ClickableIconPanel("tt-clickable-icon-up");  
	
	@Override
	public Widget getWidget() {
		return main;
	}
	
	public InteractiveCalendarView() {
		headerPanel.add(showSelectorButton);
		headerPanel.setHeight("20px");
		headerPanel.setWidth("100%");
		main.add(headerPanel,0,0);
		main.add(calendarPanel, 0, 20); //DEBUG
		main.setHeight("220px");
		main.setWidth("200px");
	}
	
	@Override
	public HasWidgets calendarPanel() {
		return calendarPanel;
	}

	@Override
	public HasWidgets selectorPanel() {
		return selectorPanel;
	}

	@Override
	public HasClickHandlers hideSelectorClickHandler() {
		return showCalenderButton;
	}
	
	@Override
	public HasClickHandlers showSelectorClickHandler() {
		return showSelectorButton;
	}

	@Override
	public void showCalendar() {
		headerPanel.clear();
		headerPanel.add(showSelectorButton);
		main.remove(selectorPanel);
		main.add(calendarPanel, 0, 20); //DEBUG
	}

	@Override
	public void showSelector() {
		headerPanel.clear();
		headerPanel.add(showCalenderButton);
		main.remove(calendarPanel);
		main.add(selectorPanel, 0, 20); //DEBUG
	}
	
}

package org.ttdc.gwt.client.presenters.calendar;

import org.ttdc.gwt.client.Injector;
import org.ttdc.gwt.client.messaging.EventBus;
import org.ttdc.gwt.client.messaging.history.HistoryConstants;
import org.ttdc.gwt.client.messaging.history.HistoryToken;
import org.ttdc.gwt.client.presenters.shared.BasePresenter;
import org.ttdc.gwt.client.presenters.shared.BaseView;
import org.ttdc.gwt.client.presenters.shared.HyperlinkPresenter;
import org.ttdc.gwt.shared.calender.Day;
import org.ttdc.gwt.shared.calender.Hour;
import org.ttdc.gwt.shared.calender.Week;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.FocusPanel;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.SimplePanel;
import com.google.gwt.user.client.ui.VerticalPanel;
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
					hourPresenter.setHourWithoutSummary(h);
					view.insertHourWidget(dayOfWeek, h.getHourOfDay(), hourPresenter.getWidget());
				}
			}
//			HyperlinkPresenter linkPresenter = injector.getHyperlinkPresenter();
//			HistoryToken token = CalendarHelpers.buildDayHistoryToken(day.getYear(),day.getMonth(),day.getDay());
//			String linkText = day.getMonth()+"/"+day.getDay() +" "+ CalendarHelpers.getDayName(dayOfWeek);
//			
//			linkPresenter.setToken(token, linkText);
//			view.insertDayHeader(dayOfWeek, linkPresenter.getWidget());
////			view.insertDayHeader(day.getMonth(), dayOfWeek,day.getDay(), dayNames[dayOfWeek-1]);
			
			HistoryToken token = CalendarHelpers.buildDayHistoryToken(day.getYear(),day.getMonth(),day.getDay());
			Widget w = buildDayHeaderCellWidget(day.getMonth(),day.getDay(),CalendarHelpers.getDayName(dayOfWeek),token);
			view.insertDayHeader(dayOfWeek, w);
			dayOfWeek++;
			
			
		}
	}
	
	private Widget buildDayHeaderCellWidget(final Integer month, final Integer day,final String name,final HistoryToken token){
		
		FocusPanel widget = new FocusPanel();
		VerticalPanel panel = new VerticalPanel();
		HTML dateWidget = new HTML(month+"/"+day);
		dateWidget.setStyleName("tt-text-huge");
		dateWidget.addStyleName("tt-center");
		
		HTML nameWidget = new HTML(name);
		nameWidget.setStyleName("tt-text-normal-bold");
		nameWidget.addStyleName("tt-center");
		
		panel.add(dateWidget);
		panel.add(nameWidget);
		widget.add(panel);
		widget.setStyleName("tt-text-center");
		//widget.addStyleName("tt-color-contrast1");
		widget.addStyleName("tt-cursor-pointer");
		panel.setStyleName("tt-center");
		
		widget.addClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				EventBus.getInstance().fireHistory(token);
			}
		});
		return widget;
	}
		
}

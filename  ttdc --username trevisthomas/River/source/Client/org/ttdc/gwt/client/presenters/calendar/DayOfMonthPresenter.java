package org.ttdc.gwt.client.presenters.calendar;

import java.util.List;

import org.ttdc.gwt.client.Injector;
import org.ttdc.gwt.client.messaging.EventBus;
import org.ttdc.gwt.client.messaging.history.HistoryConstants;
import org.ttdc.gwt.client.messaging.history.HistoryToken;
import org.ttdc.gwt.client.presenters.shared.BasePresenter;
import org.ttdc.gwt.client.presenters.shared.BaseView;
import org.ttdc.gwt.client.presenters.shared.HyperlinkPresenter;
import org.ttdc.gwt.shared.calender.CalendarThreadSummary;
import org.ttdc.gwt.shared.calender.Day;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.HasClickHandlers;
import com.google.gwt.user.client.ui.HasWidgets;
import com.google.inject.Inject;

public class DayOfMonthPresenter extends BasePresenter<DayOfMonthPresenter.View>{
	
	public interface View extends BaseView{
		void setDayOfMonth(int dayOfMonth);
		HasWidgets threadSummaryTarget();
		HasClickHandlers dayClickTarget();
	}
	
	
	@Inject
	public DayOfMonthPresenter(Injector injector) {
		super(injector, injector.getDayOfMonthView());
	}
	
	public void bindClickHandlers(final Day day){
		getView().dayClickTarget().addClickHandler(new ClickHandler(){
			@Override
			public void onClick(ClickEvent event) {
				HistoryToken token = CalendarHelpers.buildDayHistoryToken(day.getYear(),day.getMonth(),day.getDay());
				EventBus.getInstance().fireHistory(token);
			}
		});
	}
	
	private HyperlinkPresenter buildThreadSummaryLinkPresenter(CalendarThreadSummary summary){
		HyperlinkPresenter link = injector.getHyperlinkPresenter();
		HistoryToken token = new HistoryToken();
		token.setParameter(HistoryConstants.VIEW, HistoryConstants.VIEW_TOPIC);
		token.setParameter(HistoryConstants.POST_ID_KEY,summary.getRootId());
		String linkText = summary.getTitle() +" ("+summary.getCount()+") ";
		link.setToken(token, linkText);
		return link;
	}

	public void setCalendarThreadSummaryList(Day day){
		if(day.isVisable()){
			List<CalendarThreadSummary> threadSummaryList = day.getThreads();
			bindClickHandlers(day);
			view.setDayOfMonth(day.getDay());
			//Set the day of month
			if(threadSummaryList != null) {
				int threadCount = 0;
				for(CalendarThreadSummary summary : threadSummaryList){
					HyperlinkPresenter linkPresenter = buildThreadSummaryLinkPresenter(summary);
					view.threadSummaryTarget().add(linkPresenter.getWidget());
					if(threadCount++ > 5) return;
					//view.dayTarget(weekOfMonth, dayOfWeek, day.getDay()).add();
				}
			}
			else{
				//view.threadSummaryTarget().add();
			}
		}
		else{
			// Hm, maybe do something special?
		}
	}	
}

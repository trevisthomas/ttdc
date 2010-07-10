package org.ttdc.gwt.client.uibinder.calendar;


import java.util.Date;

import org.ttdc.gwt.client.Injector;
import org.ttdc.gwt.client.messaging.history.HistoryToken;
import org.ttdc.gwt.client.presenters.calendar.CalendarHelpers;
import org.ttdc.gwt.client.presenters.shared.HyperlinkPresenter;
import org.ttdc.gwt.client.presenters.util.DateFormatUtil;

import com.google.gwt.core.client.GWT;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.Grid;
import com.google.gwt.user.client.ui.Widget;
import com.google.inject.Inject;

public class CalendarBreadCrumbPanel extends Composite{
	interface MyUiBinder extends UiBinder<Widget, CalendarBreadCrumbPanel> {}
    private static final MyUiBinder binder = GWT.create(MyUiBinder.class);
    
    private Injector injector;
    
    @UiField(provided = true) Grid crumbsElement =  new Grid(1,6);
        
    @Inject
    public CalendarBreadCrumbPanel(Injector injector) { 
    	this.injector = injector;
    	
    	initWidget(binder.createAndBindUi(this));
    	
	}

    public void setYear(final int year){
    	HistoryToken token = CalendarHelpers.buildYearHistoryToken(year);
    	
    	HyperlinkPresenter presenter = injector.getHyperlinkPresenter();
		presenter.setToken(token, ""+year);
		
		crumbsElement.setWidget(0, 0, presenter.getWidget());
    }
    
    public void setMonth(final int year, final int month){
    	HistoryToken token = CalendarHelpers.buildMonthHistoryToken(year, month);
    	
    	HyperlinkPresenter presenter = injector.getHyperlinkPresenter();
		presenter.setToken(token, CalendarHelpers.getMonthName(month));
		
		crumbsElement.setWidget(0, 1, presenter.getWidget());
    }

	public void setWeek(final int weekOfYear, final int year, final Date startDate, final Date endDate){
		HistoryToken token = CalendarHelpers.buildWeekHistoryToken(year, weekOfYear);
		
		HyperlinkPresenter presenter = injector.getHyperlinkPresenter();
		presenter.setToken(token, DateFormatUtil.formatMediumDay(startDate) 
				+ " - " + DateFormatUtil.formatMediumDay(endDate));
		
		crumbsElement.setWidget(0, 2, presenter.getWidget());
	}
	
	public void setDay(final Date date, final int year, final int month, final int day){
		HistoryToken token = CalendarHelpers.buildDayHistoryToken(year, month, day);
	
		HyperlinkPresenter presenter = injector.getHyperlinkPresenter();
		presenter.setToken(token, DateFormatUtil.formatLongDayOfWeek(date));
		
		crumbsElement.setWidget(0, 3, presenter.getWidget());
    }
    
    public void setPrevNext(HistoryToken prevToken, HistoryToken nextToken){
    	HyperlinkPresenter prevLinkPresenter = injector.getHyperlinkPresenter();
    	prevLinkPresenter.setToken(prevToken, "<<Prev");
		crumbsElement.setWidget(0, 4, prevLinkPresenter.getWidget());
		
		HyperlinkPresenter nextLinkPresenter;
		nextLinkPresenter = injector.getHyperlinkPresenter();
		nextLinkPresenter.setToken(nextToken, "Next>>");
		crumbsElement.setWidget(0, 5, nextLinkPresenter.getWidget());
    }
    
    @Override
    public Widget getWidget() {
    	return this;
    }

}

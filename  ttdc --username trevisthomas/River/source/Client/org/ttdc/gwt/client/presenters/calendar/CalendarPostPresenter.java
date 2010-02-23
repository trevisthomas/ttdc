package org.ttdc.gwt.client.presenters.calendar;

import org.ttdc.gwt.client.Injector;
import org.ttdc.gwt.client.beans.GPost;
import org.ttdc.gwt.client.messaging.history.HistoryConstants;
import org.ttdc.gwt.client.messaging.history.HistoryToken;
import org.ttdc.gwt.client.presenters.shared.BasePresenter;
import org.ttdc.gwt.client.presenters.shared.BaseView;
import org.ttdc.gwt.client.presenters.shared.HyperlinkPresenter;
import org.ttdc.gwt.client.presenters.util.DateFormatUtil;
import org.ttdc.gwt.shared.calender.CalendarPost;

import com.google.gwt.user.client.ui.HasText;
import com.google.gwt.user.client.ui.HasWidgets;
import com.google.inject.Inject;

public class CalendarPostPresenter extends BasePresenter<CalendarPostPresenter.View>{
	@Inject
	public CalendarPostPresenter(Injector injector) {
		super(injector, injector.getCalendarPostSummaryView());
	}

	public interface View extends BaseView{
		HasWidgets threadTarget();
		HasWidgets personTarget();
		HasText getDateTarget();
		//void setDateTimeStamp(String datetime);
		HasText entrySummaryTarget();
		
	}
	
	
	
	public void setCalendarPost(CalendarPost cp){
		HistoryToken token = new HistoryToken();
		token.setParameter(HistoryConstants.VIEW, HistoryConstants.VIEW_USER_PROFILE);
		token.setParameter(HistoryConstants.PERSON_ID,cp.getCreatorId());
		HyperlinkPresenter personLink = injector.getHyperlinkPresenter();
		personLink.setToken(token, cp.getCreatorLogin());
		view.personTarget().add(personLink.getWidget());
		
		//perhaps just create the HistoryToken directly and pass it in?
		
		token = new HistoryToken();
		token.setParameter(HistoryConstants.VIEW, HistoryConstants.VIEW_TOPIC);
		token.setParameter(HistoryConstants.POST_ID_KEY, cp.getPostId());
		
		HyperlinkPresenter threadLink = injector.getHyperlinkPresenter();
		threadLink.setToken(token, cp.getTitle());
		view.threadTarget().add(threadLink.getWidget());
		//This is optional. Not sure how that will work just yet
		if(cp.getSummary() != null){
			view.entrySummaryTarget().setText(cp.getSummary());
			view.getDateTarget().setText(DateFormatUtil.formatLongDate(cp.getDate()));
		}
	}

}

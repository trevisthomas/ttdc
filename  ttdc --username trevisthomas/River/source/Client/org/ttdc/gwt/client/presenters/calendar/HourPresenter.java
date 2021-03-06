package org.ttdc.gwt.client.presenters.calendar;

import org.ttdc.gwt.client.Injector;
import org.ttdc.gwt.client.presenters.shared.BasePresenter;
import org.ttdc.gwt.client.presenters.shared.BaseView;
import org.ttdc.gwt.client.uibinder.post.PostSummaryPanel;
import org.ttdc.gwt.client.uibinder.post.SmallPostSummaryPanel;
import org.ttdc.gwt.shared.calender.CalendarPost;
import org.ttdc.gwt.shared.calender.Hour;

import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HasWidgets;
import com.google.inject.Inject;

public class HourPresenter extends BasePresenter<HourPresenter.View> {
	@Inject
	public HourPresenter(Injector injector) {
		super(injector, injector.getHourView());
	}

	public interface View extends BaseView {
		HasWidgets calendarPostTarget();
	}
	
	public void setHour(Hour hour){
		for(CalendarPost cp : hour.getCalendarPosts()){
			PostSummaryPanel postSummaryPanel = injector.createPostSummaryPanel();
			postSummaryPanel.init(cp);
			view.calendarPostTarget().add(postSummaryPanel);
		}
	}
	
	public void setHourWithoutSummary(Hour hour){
		for(CalendarPost cp : hour.getCalendarPosts()){
//			CalendarPostPresenter calendarPostPresenter = injector.getCalendarPostSummaryPresenter();
//			calendarPostPresenter.setCalendarPost(cp);
//			view.calendarPostTarget().add(calendarPostPresenter.getWidget());
			SmallPostSummaryPanel createSmallSummaryPanel = injector.createSmallSummaryPanel();
			createSmallSummaryPanel.init(cp);
			view.calendarPostTarget().add(createSmallSummaryPanel.getWidget());
		}
		if(hour.getCalendarPosts().size() == 0){
			view.calendarPostTarget().add(new HTML("&nbsp;"));
		}
	}
}

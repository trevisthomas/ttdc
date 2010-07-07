package org.ttdc.gwt.client.presenters.calendar;

import org.ttdc.gwt.client.Injector;
import org.ttdc.gwt.client.presenters.shared.BasePresenter;
import org.ttdc.gwt.client.presenters.shared.BaseView;
import org.ttdc.gwt.client.uibinder.calendar.DayPanel;
import org.ttdc.gwt.shared.calender.Day;
import org.ttdc.gwt.shared.calender.Hour;

import com.google.gwt.user.client.ui.Widget;
import com.google.inject.Inject;

/**
 * 
 * See {@link DayPanel}
 * @deprecated
 */
public class DayPresenter extends BasePresenter<DayPresenter.View>{
	@Inject
	public DayPresenter(Injector injector) {
		super(injector, injector.getDayView());
	}

	public interface View extends BaseView{
		//HasWidgets hoursTarget();
		void insertHourWidget(int hour, Widget widget);
		
	}
	
	public void setDay(Day day){
		if(day.isContent()){
			for(Hour h : day.getHours()){
				HourPresenter hourPresenter = injector.getHourPresenter();
				hourPresenter.setHour(h);
				//view.hoursTarget().add(hourPresenter.getWidget());
				view.insertHourWidget(h.getHourOfDay(),hourPresenter.getWidget());
			}
		}
	}
}

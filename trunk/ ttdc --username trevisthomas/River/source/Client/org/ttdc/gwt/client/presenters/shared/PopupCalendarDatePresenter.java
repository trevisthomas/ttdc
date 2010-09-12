package org.ttdc.gwt.client.presenters.shared;

import java.util.Date;

import org.ttdc.gwt.client.Injector;
import org.ttdc.gwt.client.presenters.util.DateFormatUtil;
import org.ttdc.gwt.client.uibinder.calendar.InteractiveCalendarPanel;

import com.google.gwt.user.client.ui.Widget;
import com.google.inject.Inject;

public class PopupCalendarDatePresenter extends BasePresenter<PopupCalendarDatePresenter.View> {
	private InteractiveCalendarPanel interactiveCalendar;
	
	public interface View extends BaseView{
		void setInteractiveCalendarWidget(Widget calendar);
		void hideLoginPopup();
		void setDateValue(String date);
	}
	
	@Inject
	protected PopupCalendarDatePresenter(Injector injector) {
		super(injector, injector.getPopupCalendarDateView());
		interactiveCalendar = injector.createInteractiveCalendarPanel();
		interactiveCalendar.init(InteractiveCalendarPanel.Mode.CALENDER_INTERFACE_MODE);
		view.setInteractiveCalendarWidget(interactiveCalendar);
		
		Date date = interactiveCalendar.getDateToday();
		view.setDateValue(DateFormatUtil.formatLongDay(date));
	}
}

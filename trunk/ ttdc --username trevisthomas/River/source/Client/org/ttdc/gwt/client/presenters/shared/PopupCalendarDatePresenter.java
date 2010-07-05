package org.ttdc.gwt.client.presenters.shared;

import java.util.Date;

import org.ttdc.gwt.client.Injector;
import org.ttdc.gwt.client.presenters.home.InteractiveCalendarPresenter;
import org.ttdc.gwt.client.presenters.home.InteractiveCalendarPresenter.Mode;
import org.ttdc.gwt.client.presenters.util.DateFormatUtil;

import com.google.gwt.user.client.ui.Widget;
import com.google.inject.Inject;

public class PopupCalendarDatePresenter extends BasePresenter<PopupCalendarDatePresenter.View> {
	private InteractiveCalendarPresenter interactiveCalendarPresenter;
	
	public interface View extends BaseView{
		void setInteractiveCalendarWidget(Widget calendar);
		void hideLoginPopup();
		void setDateValue(String date);
	}
	
	@Inject
	protected PopupCalendarDatePresenter(Injector injector) {
		super(injector, injector.getPopupCalendarDateView());
		interactiveCalendarPresenter = injector.getInteractiveCalendarPresenter();
		interactiveCalendarPresenter.init(Mode.CALENDER_INTERFACE_MODE);
		view.setInteractiveCalendarWidget(interactiveCalendarPresenter.getWidget());
		
		Date date = interactiveCalendarPresenter.getDateToday();
		view.setDateValue(DateFormatUtil.formatLongDay(date));
	}
}

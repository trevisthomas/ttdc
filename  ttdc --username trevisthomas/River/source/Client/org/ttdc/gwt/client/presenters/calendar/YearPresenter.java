package org.ttdc.gwt.client.presenters.calendar;

import org.ttdc.gwt.client.Injector;
import org.ttdc.gwt.client.presenters.shared.BasePresenter;
import org.ttdc.gwt.client.presenters.shared.BaseView;
import org.ttdc.gwt.shared.calender.Month;
import org.ttdc.gwt.shared.calender.Year;

import com.google.gwt.user.client.ui.Widget;
import com.google.inject.Inject;

public class YearPresenter extends BasePresenter<YearPresenter.View>{
	@Inject
	public YearPresenter(Injector injector) {
		super(injector, injector.getYearView());
	}
	
	public interface View extends BaseView{
		void insertMonth(int monthOfYear, Widget widget);
	}

	public void setYear(Year year){
		for(int mo = 1; mo < 13 ; mo++){
			Month month = year.getMonth(mo);
			MonthPresenter monthPresenter = injector.getMonthPresenter();
			monthPresenter.init(month);
			getView().insertMonth(mo,monthPresenter.getWidget());
		}
	}
}

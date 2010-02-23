package org.ttdc.gwt.client.presenters.search;

import org.ttdc.gwt.client.Injector;
import org.ttdc.gwt.client.presenters.shared.BasePresenter;
import org.ttdc.gwt.client.presenters.shared.BaseView;
import org.ttdc.gwt.client.presenters.util.DateFormatUtil;
import org.ttdc.gwt.shared.calender.Day;

import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.HasClickHandlers;
import com.google.inject.Inject;

public class SearchBoxDatePresenter extends BasePresenter<SearchBoxDatePresenter.View>{
	private Day day;
	
	public interface View extends BaseView{
		void setDateLabel(String dateLabel);
		HasClickHandlers removeClickHandler();
	}
	
	@Inject
	protected SearchBoxDatePresenter(Injector injector) {
		super(injector, injector.getSearchBoxDateView());
	}

	public void init(Day day, ClickHandler dayRemovedClickHandler){
		this.day = day;
		view.setDateLabel(DateFormatUtil.formatLongDay(day.toDate()));
		view.removeClickHandler().addClickHandler(dayRemovedClickHandler);
	}
	
	public Day getDay() {
		return day;
	}

	
}

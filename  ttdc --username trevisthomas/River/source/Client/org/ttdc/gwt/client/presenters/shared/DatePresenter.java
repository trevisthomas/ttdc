package org.ttdc.gwt.client.presenters.shared;

import java.util.Date;

import org.ttdc.gwt.client.Injector;
import org.ttdc.gwt.client.presenters.util.DateFormatUtil;

import com.google.gwt.i18n.client.DateTimeFormat;
import com.google.gwt.user.client.ui.HasText;
import com.google.inject.Inject;

/**
 * The idea is that this class can represent dates in the system and if you want 
 * that fancy dynamic date changing stuff you can implement it here.
 *
 */
public class DatePresenter extends BasePresenter<DatePresenter.View>{
	private final static DateTimeFormat dateFormat = DateTimeFormat.getFormat("MM/dd/yyyy");//TODO create a cool custom presenter for dates!
	public interface View extends BaseView{
		HasText dateText();
	}
	@Inject
	protected DatePresenter(Injector injector) {
		super(injector, injector.getDateView());
	}

	public void init(Date date){
		String value;
		if(date != null)
			//value = dateFormat.format(date);
			value = DateFormatUtil.formatLongDate(date);
		else
			value = "Never Happened";
		view.dateText().setText(value);
	}
}

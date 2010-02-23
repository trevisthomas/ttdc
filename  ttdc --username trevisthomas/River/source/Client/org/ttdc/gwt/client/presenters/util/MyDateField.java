package org.ttdc.gwt.client.presenters.util;

import java.util.Date;

import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.user.client.ui.HasValue;
import com.gwtext.client.widgets.form.DateField;

/**
 * 
 * Created so that the sexy gwtext DateField class can participate in MVP.
 *
 */
public class MyDateField extends DateField implements HasValue<Date>{

	@Override
	public void setValue(Date value, boolean fireEvents) {
		throw new RuntimeException("I'm not sure what this should do.  setValue(Date value, boolean fireEvents) needs an implementation");
	}
	
	@Override
	public void setValue(Date date) {
		if(date != null)
			super.setValue(date);
	}
	
	@Override
	public Date getValue() {
		return super.getValue();
	}
	
	

	@Override
	public HandlerRegistration addValueChangeHandler(ValueChangeHandler<Date> handler) {
		throw new RuntimeException("HandlerRegistration addValueChangeHandler(ValueChangeHandler<Date> handler) needs an implementation");
		
	}
	
}

package org.ttdc.gwt.client.presenters;

import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.event.shared.GwtEvent;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.user.client.ui.HasValue;

public class MockHasValue<T> implements HasValue<T> {
	T lastValue;
	public T getValue() {
	return lastValue;
	}
	public void setValue(T value) {
	this.lastValue = value;
	}
	public void setValue(T value, boolean fireEvents) {
		setValue(value);
	}
	public HandlerRegistration addValueChangeHandler(ValueChangeHandler<T> handler) {
		// TODO Auto-generated method stub
		return null;
	}
	public void fireEvent(GwtEvent<?> event) {
		// TODO Auto-generated method stub
		
	}
}
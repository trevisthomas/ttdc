package org.ttdc.gwt.client.presenters;

import static org.mockito.Mockito.mock;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.HasClickHandlers;
import com.google.gwt.event.shared.GwtEvent;
import com.google.gwt.event.shared.HandlerRegistration;
import static junit.framework.Assert.fail;

public class MockHasClickHandlers implements HasClickHandlers {
	public ClickHandler lastClickHandler;

	public HandlerRegistration addClickHandler(ClickHandler handler) {
		lastClickHandler = handler;
		return new HandlerRegistration() {
			public void removeHandler() {
			}
		};
	}

	public void fireEvent(GwtEvent<?> event) {
	}
	
	/**
	 * Utility method for clicking these mocked buttons
	 * 
	 */
	public static void clickMockButton(HasClickHandlers mockHasClickHandlers){
		if(((MockHasClickHandlers)mockHasClickHandlers).lastClickHandler == null)
			fail("Trevis, You forgot to register a handler before simulating a click");
		((MockHasClickHandlers)mockHasClickHandlers).lastClickHandler.onClick(mock(ClickEvent.class));
	}
}
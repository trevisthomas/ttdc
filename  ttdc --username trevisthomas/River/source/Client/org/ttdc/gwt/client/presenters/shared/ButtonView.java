package org.ttdc.gwt.client.presenters.shared;

import com.google.gwt.event.dom.client.HasClickHandlers;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.HasText;
import com.google.gwt.user.client.ui.Widget;

public class ButtonView implements ButtonPresenter.View{
	private final Button button = new Button();
	
	@Override
	public HasClickHandlers clickHandler() {
		return button;
	}

	@Override
	public Widget getWidget() {
		return button;
	}

	@Override
	public HasText text() {
		return button;
	}
	
}

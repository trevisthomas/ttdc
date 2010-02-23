package org.ttdc.gwt.client.presenters.shared;

import com.google.gwt.user.client.ui.HasText;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.Widget;

public class DateView implements DatePresenter.View{
	private final Label dateText = new Label();
	@Override
	public HasText dateText() {
		return dateText;
	}

	@Override
	public Widget getWidget() {
		return dateText;
	}
	
}

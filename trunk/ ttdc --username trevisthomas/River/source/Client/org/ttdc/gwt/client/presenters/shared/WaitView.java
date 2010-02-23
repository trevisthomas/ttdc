package org.ttdc.gwt.client.presenters.shared;

import org.ttdc.gwt.client.presenters.util.HtmlLabel;

import com.google.gwt.user.client.ui.HasText;
import com.google.gwt.user.client.ui.Widget;

public class WaitView implements WaitPresenter.View{
	private final HtmlLabel text = new HtmlLabel();
	
	@Override
	public HasText text() {
		return text;
	}
	
	@Override
	public Widget getWidget() {
		return text;
	}
	
}

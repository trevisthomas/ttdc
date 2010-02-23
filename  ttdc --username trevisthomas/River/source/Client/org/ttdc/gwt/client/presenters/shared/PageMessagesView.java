package org.ttdc.gwt.client.presenters.shared;

import org.ttdc.gwt.client.presenters.util.DisapearingWidget;

import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;

public class PageMessagesView implements PageMessagesPresenter.View{
	private final VerticalPanel main = new VerticalPanel();
	private final VerticalPanel messages = new VerticalPanel();
	private final VerticalPanel errors = new VerticalPanel();
	private final int TTL_MS = 6000;
	
	public PageMessagesView() {
		main.add(errors);
		main.add(messages);
	}
	@Override
	public Widget getWidget() {
		return main;
	}

	@Override
	public void clear() {
		errors.clear();
		messages.clear();
	}

	@Override
	public void error(String err) {
		errors.insert(DisapearingWidget.expire(new Label(err), TTL_MS),0);
	}

	@Override
	public void message(String msg) {
		messages.insert(DisapearingWidget.expire(new Label(msg), TTL_MS),0);
	}
	
}


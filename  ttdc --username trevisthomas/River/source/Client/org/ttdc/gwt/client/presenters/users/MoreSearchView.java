package org.ttdc.gwt.client.presenters.users;

import com.google.gwt.event.dom.client.HasClickHandlers;
import com.google.gwt.user.client.ui.Anchor;
import com.google.gwt.user.client.ui.Widget;

public class MoreSearchView implements MoreSearchPresenter.View{
	private final Anchor main = new Anchor("More");

	@Override
	public Widget getWidget() {
		return main;
	}

	@Override
	public HasClickHandlers moreButton() {
		return main;
	}

	@Override
	public void setVisible(boolean visible) {
		main.setVisible(visible);
	}
}

package org.ttdc.gwt.client.presenters.home;

import com.google.gwt.event.dom.client.HasClickHandlers;
import com.google.gwt.user.client.ui.Anchor;
import com.google.gwt.user.client.ui.Widget;

public class MoreLatestView implements MoreLatestPresenter.View{
	private final Anchor main = new Anchor("older");

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

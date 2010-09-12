package org.ttdc.gwt.client.presenters.users;

import com.google.gwt.event.dom.client.HasClickHandlers;
import com.google.gwt.user.client.ui.Anchor;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;

public class MoreSearchView implements MoreSearchPresenter.View{
	
	private final VerticalPanel main = new VerticalPanel();
	private final Anchor more = new Anchor();
	private final Label message = new Label();

	public MoreSearchView() {
		main.setStyleName("tt-more-comments");
		main.add(message);
		main.add(more);
		more.setHTML("&raquo; load more...");
	}
	@Override
	public Widget getWidget() {
		return main;
	}

	@Override
	public HasClickHandlers moreButton() {
		return more;
	}

	@Override
	public void setVisible(boolean visible) {
		main.setVisible(visible);
	}
	
	@Override
	public void setMessage(String text){
		message.setText(text);
	}
}

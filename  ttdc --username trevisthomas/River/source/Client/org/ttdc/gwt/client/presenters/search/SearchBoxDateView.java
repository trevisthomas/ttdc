package org.ttdc.gwt.client.presenters.search;

import com.google.gwt.event.dom.client.HasClickHandlers;
import com.google.gwt.user.client.ui.Anchor;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.Widget;

public class SearchBoxDateView implements SearchBoxDatePresenter.View{
	private final HorizontalPanel main = new HorizontalPanel();
	private final Anchor removeLink = new Anchor();
	private Label dateLabel = new Label();
	
	public SearchBoxDateView() {
		removeLink.setText("remove");
		
		main.add(removeLink);
		main.add(dateLabel);
		
	}

	@Override
	public HasClickHandlers removeClickHandler() {
		return removeLink;
	}

	@Override
	public void setDateLabel(String text) {
		this.dateLabel.setText(text);
	}

	@Override
	public Widget getWidget() {
		return main;
	}
}

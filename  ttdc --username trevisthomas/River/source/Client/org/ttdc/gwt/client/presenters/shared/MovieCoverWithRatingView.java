package org.ttdc.gwt.client.presenters.shared;

import com.google.gwt.user.client.ui.HasWidgets;
import com.google.gwt.user.client.ui.SimplePanel;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;

public class MovieCoverWithRatingView implements MovieCoverWithRatingPresenter.View{
	private final VerticalPanel main = new VerticalPanel();
	private final SimplePanel poster = new SimplePanel();
	private final SimplePanel rating = new SimplePanel();

	public MovieCoverWithRatingView() {
		main.add(rating);
		main.add(poster);
	}
	
	@Override
	public HasWidgets poster() {
		return poster;
	}

	@Override
	public HasWidgets rating() {
		return rating;
	}

	@Override
	public Widget getWidget() {
		return main;
	}
}

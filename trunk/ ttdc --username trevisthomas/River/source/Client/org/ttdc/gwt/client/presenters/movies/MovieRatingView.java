package org.ttdc.gwt.client.presenters.movies;

import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.SimplePanel;
import com.google.gwt.user.client.ui.Widget;

public class MovieRatingView implements MovieRatingPresenter.View{
	private final SimplePanel main = new SimplePanel();
	private final Label temp = new Label();
	
	public MovieRatingView() {
		main.add(temp);
	}
	
	@Override
	public void setRating(String rating) {
		temp.setText(rating);
	}

	@Override
	public Widget getWidget() {
		return main;
	}

}

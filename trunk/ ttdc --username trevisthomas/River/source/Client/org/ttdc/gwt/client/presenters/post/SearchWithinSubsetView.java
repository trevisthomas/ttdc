package org.ttdc.gwt.client.presenters.post;

import org.ttdc.gwt.client.presenters.util.ViewHelpers;

import com.google.gwt.event.dom.client.HasClickHandlers;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.Grid;
import com.google.gwt.user.client.ui.HasValue;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.Widget;

public class SearchWithinSubsetView implements SearchWithinSubsetPresenter.View{
	private final TextBox phraseField = new TextBox();
	private final Button searchButton = new Button("Search Within Results");
	private final Grid mainWidget = new Grid(1,2);
	
	public SearchWithinSubsetView() {
		mainWidget.setWidget(0, 0, phraseField);
		mainWidget.setWidget(0, 1, searchButton);
		
		ViewHelpers.configureSearchTextBox(phraseField,searchButton);
	}

	@Override
	public HasValue<String> getPhraseField() {
		return phraseField;
	}
	@Override
	public HasClickHandlers getSearchButton() {
		return searchButton;
	}
	@Override
	public Widget getWidget() {
		return mainWidget;
	}
}

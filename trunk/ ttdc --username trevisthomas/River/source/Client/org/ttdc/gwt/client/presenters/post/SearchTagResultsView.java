package org.ttdc.gwt.client.presenters.post;

import com.google.gwt.user.client.ui.HasText;
import com.google.gwt.user.client.ui.HasWidgets;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.RootPanel;
import com.google.gwt.user.client.ui.SimplePanel;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;

public class SearchTagResultsView implements SearchTagResultsPresenter.View{
	private final VerticalPanel mainPanel = new VerticalPanel();
	private final SimplePanel tagListPanel = new SimplePanel();
	private final SimplePanel postListPanel = new SimplePanel();
	private final SimplePanel searchWithinPanel = new SimplePanel(); 
	private final Label statusText = new Label();
	private final SimplePanel paginationTarget = new SimplePanel();
		
	@Override
	public HasWidgets getPostResultsTarget() {
		return postListPanel;
	}

	@Override
	public HasWidgets getTagResultsTarget() {
		return tagListPanel;
	}

	@Override
	public void show() {
		RootPanel.get("content").clear();
		mainPanel.add(searchWithinPanel);
		mainPanel.add(tagListPanel);
		mainPanel.add(postListPanel);
		mainPanel.add(statusText);
		mainPanel.add(paginationTarget);
		RootPanel.get("content").add(mainPanel);
	}

	@Override
	public Widget getWidget() {
		return null;//may not be called. If so return mainPanel;
	}

	@Override
	public HasText getStatusText() {
		return statusText;
	}

	@Override
	public HasWidgets getSearchWithinResultsTarget() {
		return searchWithinPanel;
	}

	@Override
	public HasWidgets paginationTarget() {
		return paginationTarget;
	}

	
}

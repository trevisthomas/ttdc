package org.ttdc.gwt.client.presenters.post;

import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HasText;
import com.google.gwt.user.client.ui.HasWidgets;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.RootPanel;
import com.google.gwt.user.client.ui.SimplePanel;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;

@Deprecated
public class SearchView extends Composite implements SearchPresenter.View{
	private final Label summaryDetail = new Label();
	private final VerticalPanel postListPanel = new VerticalPanel(); 
	private final SimplePanel controlsPanel = new SimplePanel();
	private final VerticalPanel rootPanel = new VerticalPanel();
	private final SimplePanel tagCloudWidgetPanel = new SimplePanel();
	private final SimplePanel tagResultsTarget = new SimplePanel();
	private final SimplePanel toggleResultsTarget = new SimplePanel(); 
	private final SimplePanel messagesPanel = new SimplePanel();
	private final SimplePanel navigationPanel = new SimplePanel();

	public HasText getSummaryDetail() {
		return summaryDetail;
	}
	
	@Override
	public void show() {
		rootPanel.add(navigationPanel);
		rootPanel.add(messagesPanel);
		rootPanel.add(controlsPanel);
		
		rootPanel.add(toggleResultsTarget);
		rootPanel.add(tagResultsTarget);
		rootPanel.add(postListPanel);
		rootPanel.add(summaryDetail);
		rootPanel.add(tagCloudWidgetPanel);
		
		initWidget(rootPanel);
		
		
		RootPanel.get("content").clear();
		RootPanel.get("content").add(this);
	}
	
	@Override
	public HasWidgets navigationPanel() {
		return navigationPanel;
	}
	
	@Override
	public Widget getWidget() {
		return super.getWidget();
	}

	public void refreshResults(Widget widget) {
		postListPanel.clear();
		postListPanel.add(widget);
	}

	@Override
	public void setTagCloudWidget(Widget widget) {
		tagCloudWidgetPanel.add(widget);
	}

	@Override
	public HasWidgets getResultsTarget() {
		return postListPanel;
	}

	@Override
	public HasWidgets toggleResultsTarget() {
		return toggleResultsTarget;
	}

	@Override
	public HasWidgets getTagResultsTarget() {
		return tagResultsTarget;
	}

	@Override
	public HasWidgets getSiteSearchTarget() {
		return controlsPanel;
	}

	@Override
	public HasWidgets messagePanel() {
		return messagesPanel;
	}
}
